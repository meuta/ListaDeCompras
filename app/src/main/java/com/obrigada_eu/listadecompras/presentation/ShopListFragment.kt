package com.obrigada_eu.listadecompras.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.FragmentShopListBinding
import com.obrigada_eu.listadecompras.domain.ShopListEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopListFragment: Fragment()  {
    private val shopListViewModel: ShopListViewModel by viewModels()

    private lateinit var onFabClickListener: OnFabClickListener
    private lateinit var onListItemClickListener: OnListItemClickListener

    private var _binding: FragmentShopListBinding? = null
    private val binding: FragmentShopListBinding
        get() = _binding ?: throw RuntimeException("FragmentShopListBinding == null")

    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var fromGlobal: Int? = null
    private var toGlobal: Int? = null

    private var listId = ShopListEntity.UNDEFINED_ID


    override fun onAttach(context: Context) {


        super.onAttach(context)
        if (context is OnFabClickListener && context is OnListItemClickListener) {
            onFabClickListener = context
            onListItemClickListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnBackPressedCallback()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = shopListViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        setupRecyclerView()

        layoutManager = binding.rvShopList.layoutManager as LinearLayoutManager

        setupButtons()

        observeViewModel()
    }

    private fun observeViewModel() {

        shopListViewModel.shopListIdLD.observe(viewLifecycleOwner){
            Log.d("ShopListFragment", "shopListIdLD.observe = $it")
            listId = it
        }

        shopListViewModel.shopList.observe(viewLifecycleOwner) {
            shopListAdapter.submitList(it)
            Log.d("ShopListFragment", "shopList.observe = ${it.map { it.name to it.shopListId }}")
        }

    }






    private fun setupButtons() {
        with(binding) {
            buttonAddShopItem.setOnClickListener {

                onFabClickListener.onFabClick(listId)
            }
        }
    }


    private fun setupRecyclerView() {
        shopListAdapter = ShopListAdapter()

        with(binding.rvShopList) {
            adapter = shopListAdapter

            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_ENABLED, ShopListAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_DISABLED, ShopListAdapter.MAX_POOL_SIZE
            )

            layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        setupScrollController()

        setupClickListener()

        setupSwipeAndDragListener(binding.rvShopList)
    }

    private fun setupScrollController() {
        shopListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

                val firstPos = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstPos >= 0) {
                    val firstView = layoutManager.findViewByPosition(firstPos)
                    firstView?.let {
                        val offsetTop = layoutManager.getDecoratedTop(it) -
                                layoutManager.getTopDecorationHeight(it) -
                                it.marginTop
                        layoutManager.scrollToPositionWithOffset(firstPos, offsetTop)
                    }
                }
            }
        })
    }


    private fun setupSwipeAndDragListener(rvShopList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 0.7f
            }

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

                val list = shopListAdapter.currentList.toMutableList()
                val item = list[from]

                list.removeAt(from)
                list.add(to, item)

                shopListAdapter.submitList(list)

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
                                    shopListViewModel.dragShopItem(from, to)
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


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.bindingAdapterPosition]
                if (direction == ItemTouchHelper.RIGHT) {
                    shopListViewModel.deleteShopItem(item)

                } else {
                    shopListViewModel.changeEnableState(item)
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

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = {
            onListItemClickListener.onListItemClick(it.id)
        }
    }


    interface OnListItemClickListener {
        fun onListItemClick(itemId: Int)
    }

    interface OnFabClickListener {
        fun onFabClick(listId: Int)
    }

    private val callback = object : OnBackPressedCallback(
        true // default to enabled
    ) {
        override fun handleOnBackPressed() {
            val fragments = parentFragmentManager.fragments
            Log.d("setOnBackPressedCallback", "list fragments = $fragments")
            Log.d("setOnBackPressedCallback", "fragments this = ${this@ShopListFragment}")
            if (fragments.last() == this@ShopListFragment) {
                shopListViewModel.updateShopListIdState(0)
                requireActivity().finish()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setOnBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {

        fun newInstance(): ShopListFragment {
            return ShopListFragment()
        }
    }
}