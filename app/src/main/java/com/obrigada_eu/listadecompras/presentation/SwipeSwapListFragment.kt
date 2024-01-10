package com.obrigada_eu.listadecompras.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import com.obrigada_eu.listadecompras.databinding.FragmentShopListBinding
import com.obrigada_eu.listadecompras.presentation.list_set.ListSetViewModel
import com.obrigada_eu.listadecompras.presentation.shop_list.ShopListViewModel

abstract class SwipeSwapListFragment<
        T,
        VB : ViewDataBinding,
        VM : SwipeSwapViewModel,
        >(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    protected abstract val fragmentListViewModel: VM

    protected abstract var fragmentListAdapter: SwipeSwapAdapter<T>

    protected abstract fun createAdapter(context: Context?): SwipeSwapAdapter<T>

    private var _binding: VB? = null
    val binding: VB
        get() = _binding ?: throw RuntimeException("ViewBinding == null")

//    abstract fun T.initialize()

    protected abstract var listLayoutManager: LinearLayoutManager

    private var fromGlobal: Int? = null
    private var toGlobal: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnBackPressedCallback()
    }

    abstract fun setOnBackPressedCallback()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateMethod.invoke(inflater, container, false)

//        // Calling the extension function
//        binding.initialize()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(binding) {
            is FragmentShopListBinding -> (binding as FragmentShopListBinding).viewModel = fragmentListViewModel as ShopListViewModel
            is FragmentListSetBinding -> (binding as FragmentListSetBinding).viewModel = fragmentListViewModel as ListSetViewModel
            else -> throw RuntimeException("Unknown binding: $binding")
        }
        binding.lifecycleOwner = viewLifecycleOwner
        fragmentListAdapter = createAdapter(requireContext())
        setupRecyclerView()
        setupButtons()
        observeViewModel()
    }

    abstract fun setupButtons()

    abstract fun observeViewModel()

    private fun setupRecyclerView() {
        val recyclerView = when (binding) {
            is FragmentShopListBinding -> (binding as FragmentShopListBinding).rvShopList
            is FragmentListSetBinding -> (binding as FragmentListSetBinding).rvListSet
            else -> throw RuntimeException("Unknown binding: $binding")
        }

        with(recyclerView) {
            adapter = fragmentListAdapter

            recycledViewPool.setMaxRecycledViews(
                SwipeSwapAdapter.VIEW_TYPE_ENABLED, SwipeSwapAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                SwipeSwapAdapter.VIEW_TYPE_DISABLED, SwipeSwapAdapter.MAX_POOL_SIZE
            )

            layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
            listLayoutManager = layoutManager as LinearLayoutManager

            setupScrollController()
            setupSwipeAndDragListener(this)
            setupClickListener()
        }
    }

    abstract fun setupClickListener()


    private fun setupScrollController() {
        fragmentListAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

                val firstPos = listLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstPos >= 0) {
                    val firstView = listLayoutManager.findViewByPosition(firstPos)
                    firstView?.let {
                        val offsetTop = listLayoutManager.getDecoratedTop(it) -
                                listLayoutManager.getTopDecorationHeight(it) -
                                it.marginTop
                        listLayoutManager.scrollToPositionWithOffset(firstPos, offsetTop)
                    }
                }
            }
        })
    }

    private fun setupSwipeAndDragListener(rvShopList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            var context = requireContext()

            private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)
            private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_shopping_bag)
            private val intrinsicWidthDelete = deleteIcon!!.intrinsicWidth
            private val intrinsicHeightDelete = deleteIcon!!.intrinsicHeight
            private val intrinsicWidthEdit = editIcon!!.intrinsicWidth
            private val intrinsicHeightEdit = editIcon!!.intrinsicHeight

            private val clearPaint =
                Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                //the position from where item has been moved
                val from = viewHolder.bindingAdapterPosition

                //the position where the item is moved
                val to = target.bindingAdapterPosition
                toGlobal = to

                val list = fragmentListAdapter.currentList.toMutableList()
                val item = list[from]

                list.removeAt(from)
                list.add(to, item)

                fragmentListAdapter.submitList(list)

                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                when (actionState) {
                    ItemTouchHelper.ACTION_STATE_DRAG -> {
                        viewHolder?.itemView?.alpha = 0.7f
                        fromGlobal = viewHolder?.bindingAdapterPosition
                    }

                    ItemTouchHelper.ACTION_STATE_IDLE -> {
                        fromGlobal?.let { from ->
                            toGlobal?.let { to ->
                                if (fromGlobal != toGlobal) {
                                    dragListItem(from, to)
                                    fromGlobal = null
                                    toGlobal = null
                                }
                            }
                        }
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }


            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 0.7f
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = fragmentListAdapter.currentList[viewHolder.bindingAdapterPosition]
                if (direction == ItemTouchHelper.RIGHT) {
                    deleteListItem(item)
                    showUndoSnackbar()

                } else {
                    changeEnableState(item)
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(
                        c,
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }

                // Draw the edit background
                if (dX < 0) {

                    val editIconTop = itemView.top + (itemHeight - intrinsicHeightEdit) / 2
                    val editIconMargin = (itemHeight - intrinsicHeightEdit)
                    val editIconLeft = itemView.right - editIconMargin
                    val editIconRight = itemView.right - editIconMargin + intrinsicWidthEdit
                    val editIconBottom = editIconTop + intrinsicHeightEdit

                    // Draw the edit icon
                    editIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
                    editIcon.draw(c)

                } else if (dX > 0) {

                    val deleteIconTop = itemView.top + (itemHeight - intrinsicHeightDelete) / 2
                    val deleteIconMargin = (itemHeight - intrinsicHeightDelete)
                    val deleteIconLeft = itemView.left + deleteIconMargin - intrinsicWidthDelete
                    val deleteIconRight = itemView.left + deleteIconMargin
                    val deleteIconBottom = deleteIconTop + intrinsicHeightDelete

                    // Draw the delete icon
                    deleteIcon!!.setBounds(
                        deleteIconLeft,
                        deleteIconTop,
                        deleteIconRight,
                        deleteIconBottom
                    )
                    deleteIcon.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            private fun clearCanvas(
                c: Canvas?,
                left: Float,
                top: Float,
                right: Float,
                bottom: Float
            ) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun showUndoSnackbar() {
        var stringResId: Int?
        val view = when (binding) {
            is FragmentShopListBinding -> (binding as FragmentShopListBinding).rvShopList.also {
                stringResId = R.string.snack_bar_undo_delete_item_text
            }

            is FragmentListSetBinding -> (binding as FragmentListSetBinding).rvListSet.also {
                stringResId = R.string.snack_bar_undo_delete_list_text
            }

            else -> throw RuntimeException("Unknown binding: $binding")
        }
        stringResId?.let {
            val snackbar: Snackbar = Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            snackbar
                .setActionTextColor(requireActivity().getColor(R.color.white))
                .setAction(R.string.undo) { undoDelete() }
            snackbar.show()
        }
    }

    abstract fun undoDelete()

    abstract fun changeEnableState(item: T)

    abstract fun deleteListItem(item: T)

    abstract fun dragListItem(from: Int, to: Int)


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}