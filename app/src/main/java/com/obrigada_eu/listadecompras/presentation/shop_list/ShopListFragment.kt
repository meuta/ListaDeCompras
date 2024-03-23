package com.obrigada_eu.listadecompras.presentation.shop_list

import android.content.Context
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrigada_eu.listadecompras.databinding.FragmentShopListBinding
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.SwipeSwapAdapter
import com.obrigada_eu.listadecompras.presentation.SwipeSwapListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopListFragment : SwipeSwapListFragment<
        ShopItem,
        FragmentShopListBinding,
        ShopListViewModel
        > (FragmentShopListBinding::inflate) {

    override val fragmentListViewModel: ShopListViewModel by activityViewModels()

    override lateinit var onFabClickListener: OnFabClickListener
    override lateinit var onListItemClickListener: OnListItemClickListener

    override lateinit var fragmentListAdapter: SwipeSwapAdapter<ShopItem>

    override lateinit var listLayoutManager: LinearLayoutManager

    private var listId = ShopList.UNDEFINED_ID


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFabClickListener && context is OnListItemClickListener) {
            onFabClickListener = context
            onListItemClickListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun observeViewModel() {
        fragmentListViewModel.shopListIdLD.observe(viewLifecycleOwner){
//            Log.d("ShopListFragment", "shopListIdLD.observe = $it")
            listId = it
            if (it == ShopList.UNDEFINED_ID){
                requireActivity().finish()
            }
        }

        fragmentListViewModel.shopListLD.observe(viewLifecycleOwner) {
            fragmentListAdapter.submitList(it)
//            Log.d("ShopListFragment", "shopList.observe = ${it.map { listOf(it.name, it.count, it.units) }}")
        }
    }

    override fun setupButtons() {
        with(binding) {
            buttonAddItem.setOnClickListener {
                onFabClickListener.onFabClick(listId)
            }
        }
    }


    override fun createAdapter(context: Context?): SwipeSwapAdapter<ShopItem> {
        return ShopListAdapter()
    }

    override fun changeEnableState(item: ShopItem) {
        fragmentListViewModel.changeEnableState(item)
    }

    override fun deleteListItem(item: ShopItem) {
        fragmentListViewModel.deleteShopItem(item)
    }

    override fun undoDelete() {
        fragmentListViewModel.undoDelete()
    }

    override fun dragListItem(from: Int, to: Int) {
        fragmentListViewModel.dragShopItem(from, to)
    }

    private val callback = object : OnBackPressedCallback(
        true // default to enabled
    ) {
        override fun handleOnBackPressed() {
            val fragments = parentFragmentManager.fragments
//            Log.d("setOnBackPressedCallback", "list fragments = $fragments")
//            Log.d("setOnBackPressedCallback", "fragments this = ${this@ShopListFragment}")
            if (fragments.last() == this@ShopListFragment) {
                fragmentListViewModel.updateShopListIdState(ShopList.UNDEFINED_ID)
                requireActivity().finish()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun setOnBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {

        fun newInstance(): ShopListFragment {
            return ShopListFragment()
        }
    }
}