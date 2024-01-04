package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.shop_list.ShopListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSetFragment : Fragment() {


    private val listSetViewModel: ListSetViewModel by viewModels()

    private lateinit var listSetAdapter: ListSetAdapter
    private var _binding: FragmentListSetBinding? = null
    private val binding: FragmentListSetBinding
        get() = _binding ?: throw RuntimeException("FragmentListSetBinding == null")

    private lateinit var layoutManager: LinearLayoutManager

    private var fromGlobal: Int? = null
    private var toGlobal: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnBackPressedCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                with(binding) {
                    if (cardNewList.visibility == View.VISIBLE) {
                        etListName.setText("")
                        cardNewList.visibility = View.GONE
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = listSetViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()

        layoutManager = binding.rvListSet.layoutManager as LinearLayoutManager


        addTextChangedListeners()
        setupButtons()
        observeViewModel()
    }

    private fun observeViewModel() {
        listSetViewModel.allListsWithoutItems.observe(viewLifecycleOwner) {
            listSetAdapter.submitList(it)
            Log.d("ListSetFragment", "listSet.observe = ${it.map { it.name}}")
        }
        listSetViewModel.shopListIdLD.observe(viewLifecycleOwner) {
            Log.d("ListSetFragment", "shopListIdLD.observe = $it")
            if (it != ShopList.UNDEFINED_ID) {
                startShopListActivity()
            }
        }
    }

    private fun setupButtons() {
        with(binding) {
            buttonAddShopList.setOnClickListener {
                if (cardNewList.visibility == View.GONE) {
                    cardNewList.visibility = View.VISIBLE
                    etListName.setText(requireContext().resources.getString(R.string.new_list))
                    etListName.requestFocus()
                    etListName.setSelection(0, etListName.text.length)

                    val inputMethodManager =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(etListName, 0)
                }
            }
            buttonCreateList.setOnClickListener { view ->
                if (etListName.text.isNotEmpty()) {
                    listSetViewModel.addShopList(etListName.text?.toString())
                    val isError = listSetViewModel.errorInputName.value ?: true
                    if (!isError) {
                        etListName.setText("")
                        cardNewList.visibility = View.GONE
                        val inputMethodManager =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
    }

    private fun addTextChangedListeners() {
        val inputErrorListener = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(binding) {
                    if (etListName.text.hashCode() == s.hashCode()) {
                        listSetViewModel.resetErrorInputName()
                    }
                }
            }


            override fun afterTextChanged(s: Editable?) {
            }
        }
        with(binding) {
            etListName.addTextChangedListener(inputErrorListener)
        }
    }

    private fun setupRecyclerView() {
        listSetAdapter = ListSetAdapter()

        with(binding.rvListSet) {
            adapter = listSetAdapter

            recycledViewPool.setMaxRecycledViews(
                ListSetAdapter.VIEW_TYPE_ENABLED, ListSetAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ListSetAdapter.VIEW_TYPE_DISABLED, ListSetAdapter.MAX_POOL_SIZE
            )

            layoutManager = LinearLayoutManager(
                this@ListSetFragment.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            setupScrollController()
            setupSwipeAndDragListener(this)
            setupClickListener()
        }
    }

    private fun setupScrollController() {
        listSetAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
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

    private fun setupClickListener() {
        listSetAdapter.onListItemClickListener = {
            listSetViewModel.openShopList(it.id)
        }
    }

    private fun startShopListActivity() {
        Log.d(
            "ListSetFragment",
            "startShopListActivity listId = ${listSetViewModel.shopListIdLD.value}"
        )
        val intent = ShopListActivity.newIntent(this.requireContext())
        startActivity(intent)
    }

    private fun setupSwipeAndDragListener(rvLists: RecyclerView) {
        val callback = object : SimpleCallback(
            UP or DOWN, LEFT or RIGHT
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

                val list = listSetAdapter.currentList.toMutableList()
                val item = list[from]

                list.removeAt(from)
                list.add(to, item)

                listSetAdapter.submitList(list)

                return true
            }


            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                when (actionState) {
                    ACTION_STATE_DRAG -> {
                        viewHolder?.itemView?.alpha = 0.7f
                        fromGlobal = viewHolder?.bindingAdapterPosition
                    }

                    ACTION_STATE_IDLE -> {
                        fromGlobal?.let { from ->
                            toGlobal?.let { to ->
                                if (fromGlobal != toGlobal) {
                                    listSetViewModel.dragShopList(from, to)
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
                val list = listSetAdapter.currentList[viewHolder.bindingAdapterPosition]
                if (direction == RIGHT) {
                    listSetViewModel.deleteShopList(list.id)
                    showUndoSnackbar()

                    Log.d("onSwiped", "delete name = ${list.name}")
                } else {
                    listSetViewModel.changeEnableState(list)
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
        itemTouchHelper.attachToRecyclerView(rvLists)
    }



    private fun showUndoSnackbar() {
        val view: View = binding.rvListSet
        val snackbar: Snackbar = Snackbar.make(
            view,
            R.string.snack_bar_undo_delete_list_text,
            Snackbar.LENGTH_LONG
        )
        snackbar
            .setActionTextColor(requireActivity().getColor(R.color.white))
            .setAction(R.string.undo) { undoDelete() }
        snackbar.show()
    }

    private fun undoDelete() {
        listSetViewModel.undoDelete()
    }

    companion object {
        fun newInstance() = ListSetFragment()
    }

}