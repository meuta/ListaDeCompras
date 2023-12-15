package com.obrigada_eu.listadecompras.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityShopListBinding
import com.obrigada_eu.listadecompras.domain.ShopList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopListActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    private var listId = ShopList.UNDEFINED_ID

    private val shopListViewModel: ShopListViewModel by viewModels()

    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var binding: ActivityShopListBinding

    private lateinit var layoutManager: LinearLayoutManager

    private var fromGlobal: Int? = null
    private var toGlobal: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        parseIntent()

        setupRecyclerView()

        layoutManager = binding.rvShopList.layoutManager as LinearLayoutManager

        setupButtons()

        observeViewModel()

    }

    private fun observeViewModel() {
        shopListViewModel.getShopListName(listId)
        shopListViewModel.shopListName.observe(this) {
            Log.d("ShopListActivity", "shopListName.observe = $it")
            setupActionBar(it)
        }

        shopListViewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
            Log.d("ShopListActivity", "shopList.observe = ${it.map { it.name to it.shopListId }}")
        }
    }

    private fun parseIntent() {
        if (intent.hasExtra(LIST_ID)) {
            listId = intent.getIntExtra(LIST_ID, ShopList.UNDEFINED_ID)
            shopListViewModel.getShopList(listId)
            Log.d("ShopListActivity", "parseIntent() = $listId")
        }
    }

    private fun setupActionBar(name: String) {
        setSupportActionBar(binding.toolbarShopListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
            actionBar.title = name
        }
        binding.toolbarShopListActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupButtons() {
        with(binding) {
            buttonAddShopItem.setOnClickListener {
                if (isOnePaneMode()) {
                    val intent = ShopItemActivity.newIntentAddItem(this@ShopListActivity, listId)
                    startActivity(intent)
                } else {
                    val fragment = ShopItemFragment.newInstanceAddItem(listId)
                    launchFragment(fragment)
                }
            }
        }
    }

    override fun onEditingFinished() {
        supportFragmentManager.popBackStack()
    }

    private fun isOnePaneMode(): Boolean {
        return binding.shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment) {
        with(supportFragmentManager) {
            popBackStack()
            beginTransaction()
                .replace(R.id.shop_item_container, fragment)
                .addToBackStack(null).commit()
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
                this@ShopListActivity,
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
        val callback = object : SimpleCallback(
            UP or DOWN, LEFT or RIGHT
        ) {
            var context = applicationContext

            private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)
            private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_shopping_bag)
            private val intrinsicWidthDelete = deleteIcon!!.intrinsicWidth
            private val intrinsicHeightDelete = deleteIcon!!.intrinsicHeight
            private val intrinsicWidthEdit = editIcon!!.intrinsicWidth
            private val intrinsicHeightEdit = editIcon!!.intrinsicHeight

            private val clearPaint =
                Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

            override fun getSwipeThreshold(viewHolder: ViewHolder): Float {
                return 0.7f
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
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

            override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
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
                recyclerView: RecyclerView, viewHolder: ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }


            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.bindingAdapterPosition]
                if (direction == RIGHT) {
                    shopListViewModel.deleteShopItem(item)

                } else {
                    shopListViewModel.changeEnableState(item)
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder,
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
            if (isOnePaneMode()) {
                val intent = ShopItemActivity.newIntentEditItem(this, it.id)
                startActivity(intent)
            } else {
                val fragment = ShopItemFragment.newInstanceEditItem(it.id)
                launchFragment(fragment)
            }
        }
    }


    companion object {
        private const val LIST_ID = "list_id"

        fun newIntent(context: Context, listId: Int): Intent {
            val intent = Intent(context, ShopListActivity::class.java)
            intent.putExtra(LIST_ID, listId)
            return intent
        }

    }
}