package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.SwipeSwapAdapter
import com.obrigada_eu.listadecompras.presentation.SwipeSwapListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListSetFragment(
) : SwipeSwapListFragment<
        ShopList,
        FragmentListSetBinding,
        ListSetViewModel
        >(FragmentListSetBinding::inflate),
    SwipeSwapListFragment.OnFabClickListener,
    SwipeSwapListFragment.OnListItemClickListener
{

    override var onFabClickListener: OnFabClickListener? = null
    override var onListItemClickListener: OnListItemClickListener? = null

    override val fragmentListViewModel: ListSetViewModel by activityViewModels()

    override lateinit var fragmentListAdapter: SwipeSwapAdapter<ShopList>

    override lateinit var listLayoutManager: LinearLayoutManager

    private var createListFragment = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFabClickListener = this
        onListItemClickListener = this

        childFragmentManager.addOnBackStackChangedListener {
//            Log.d(TAG, "onViewCreated: backStackEntryCount = ${childFragmentManager.backStackEntryCount}")
            if (childFragmentManager.backStackEntryCount == 0) {
                fragmentListViewModel.showMenuGroup()
            } else {
                fragmentListViewModel.hideMenuGroup()
            }
        }
    }


    override fun observeViewModel(savedInstanceState: Bundle?) {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                fragmentListViewModel.allListsWithoutItemsStateFlow.collect {
                    fragmentListAdapter.submitList(it)
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentListViewModel.fragmentState.collect { state ->
                    if (savedInstanceState == null) {
                        createListFragment = true
                        showCreateListFragment(state)
                    } else {
                        if (!createListFragment) createListFragment = true else showCreateListFragment(state)
                    }
                }
            }
        }



        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                fragmentListViewModel.menuGroupVisibility.collect {
//                    Log.d(TAG, "observeViewModel: menuGroupVisibility.collect = $it")
                    with(binding.coverView){
                        post { visibility = if (it) GONE else VISIBLE }
                    }
                }
            }
        }
    }


    private fun showCreateListFragment(state: NewListCreationFragmentState?) {
//        Log.d(TAG, "showCreateListFragment: ListFragmentUI state = $state")
        if (childFragmentManager.backStackEntryCount > 0) childFragmentManager.popBackStack()

        if (state != null) {
            val orientation = requireActivity().resources.configuration.orientation
            binding.createNewListContainer.layoutParams.width =
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) 1200
                else ViewGroup.LayoutParams.MATCH_PARENT

            childFragmentManager.beginTransaction()
                .replace(
                    R.id.create_new_list_container,
                    NewListCreationFragment.newInstance(state),
                    NewListCreationFragment::class.simpleName
                )
                .addToBackStack(NewListCreationFragment::class.simpleName)
                .commit()
        }
    }


    override fun setupButtons() {
        with(binding) {
            buttonAddItem.setOnClickListener {
                onFabClickListener?.onFabClick()
            }
        }
    }


    override fun createAdapter(context: Context?): SwipeSwapAdapter<ShopList> {
        return ListSetAdapter()
    }


    override fun changeEnableState(item: ShopList) {
        fragmentListViewModel.changeEnableState(item)
    }

    override fun deleteListItem(item: ShopList) {
        fragmentListViewModel.deleteShopList(item.id)
    }

    override fun undoDelete() {
        fragmentListViewModel.undoDelete()
    }

    override fun dragListItem(from: Int, to: Int) {
        fragmentListViewModel.dragShopList(from, to)
    }

    override fun setOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                lifecycleScope.launch {

                    if (childFragmentManager.backStackEntryCount == 0) {
                        isEnabled = false
                        requireActivity().finish()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onListItemClick(itemId: Int) {
        fragmentListViewModel.setCurrentListId(itemId)
    }

    override fun onFabClick() {
        showCreateListFragment(NewListCreationFragmentState())
    }


    companion object {

        private const val TAG = "ListSetFragment"

        fun newInstance() = ListSetFragment()
    }

}