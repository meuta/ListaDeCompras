package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.obrigada_eu.listadecompras.presentation.shop_list.ShopListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
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

    override lateinit var onFabClickListener: OnFabClickListener
    override lateinit var onListItemClickListener: OnListItemClickListener

    override val fragmentListViewModel: ListSetViewModel by activityViewModels()

    override lateinit var fragmentListAdapter: SwipeSwapAdapter<ShopList>

    override lateinit var listLayoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFabClickListener = this
        onListItemClickListener = this
        addTextChangedListeners()
    }

    override fun observeViewModel() {
        fragmentListViewModel.allListsWithoutItems.observe(viewLifecycleOwner) {
            fragmentListAdapter.submitList(it)
//            Log.d(TAG, "listSet.observe = ${it.map { it.name}}")
        }
        fragmentListViewModel.shopListIdLD.observe(viewLifecycleOwner) {
//            Log.d(TAG, "shopListIdLD.observe = $it")
            if (it != ShopList.UNDEFINED_ID) {
                startShopListActivity()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                fragmentListViewModel.cardNewListVisibilityStateFlow.collect {isVisible ->
//                    Log.d(TAG, "observeViewModel: cardNewListVisibilityStateFlow.collect = $isVisible")
                    with(binding){
                        if (!isVisible) {

                            etListName.setText("")
                            cardNewList.visibility = View.GONE
                            val inputMethodManager =
                                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(cardNewList.windowToken, 0)

                        } else {

                            cardNewList.visibility = View.VISIBLE
                            etListName.setText(requireContext().resources.getString(R.string.new_list))
                            etListName.requestFocus()
                            etListName.setSelection(0, etListName.text.length)

                            val inputMethodManager =
                                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.showSoftInput(etListName, 0)
                        }
                    }
                }
            }
        }


        fragmentListViewModel.oldFileName.observe(viewLifecycleOwner){ fileName ->
//            Log.d(TAG, "oldFileName.observe = $fileName")
            fileName?.let {
                with(binding){
                    etListName.tag = TAG_ERROR_INPUT_NAME
                    etListName.setText(it)
                    etListName.setSelection(etListName.text.length)
                    etListName.tag = null
                }
            }
        }
    }

    override fun setupButtons() {
        with(binding) {
            buttonAddItem.setOnClickListener {
                onFabClickListener.onFabClick()
            }

            buttonCreateList.setOnClickListener {
                fragmentListViewModel.addShopList(
                    etListName.text?.toString(),
                    fragmentListViewModel.fromTxtFile.value
                )
            }

            buttonCanselCreateList.setOnClickListener {
                fragmentListViewModel.updateUiState(false, false, null, null, null)
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
//                        Log.d(TAG, "onTextChanged: etListName.tag = ${etListName.tag}")
                        if( etListName.tag == null ) {
                            // Value changed by user
                            fragmentListViewModel.resetErrorInputName()
//                            Log.d(TAG, "onTextChanged: resetErrorInputName()")
                        }
                        else{
                            // Value changed by program
                            fragmentListViewModel.showErrorInputName()
//                            Log.d(TAG, "onTextChanged: show errorInputName")
                        }
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


    private fun startShopListActivity() {
//        Log.d(
//            TAG,
//            "startShopListActivity listId = ${fragmentListViewModel.shopListIdLD.value}"
//        )
        val intent = ShopListActivity.newIntent(this.requireContext())
        startActivity(intent)
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
                    val isVisible = fragmentListViewModel.cardNewListVisibilityStateFlow.first()

                    if (isVisible) {
                        fragmentListViewModel.updateUiState(false, false, null, null, null)
                    } else {
                        isEnabled = false
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

    override fun onListItemClick(itemId: Int) {
        lifecycleScope.launch {
            val isVisible = fragmentListViewModel.cardNewListVisibilityStateFlow.first()

            if (!isVisible) {
                fragmentListViewModel.setCurrentListId(itemId)
                fragmentListViewModel.updateUiState(false, false, null, null, null)
            }
        }
    }

    override fun onFabClick(listId: Int?) {
        lifecycleScope.launch {
            val isVisible = fragmentListViewModel.cardNewListVisibilityStateFlow.first()

            if (!isVisible) {
                fragmentListViewModel.updateUiState(true, false, null, null, null)
            }
        }
    }

    companion object {

        private const val TAG = "ListSetFragment"
        private const val TAG_ERROR_INPUT_NAME = 101

        fun newInstance() = ListSetFragment()
    }

}