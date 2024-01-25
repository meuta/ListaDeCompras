package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.SwipeSwapAdapter
import com.obrigada_eu.listadecompras.presentation.SwipeSwapListFragment
import com.obrigada_eu.listadecompras.presentation.shop_list.ShopListActivity
import dagger.hilt.android.AndroidEntryPoint

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
            Log.d("ListSetFragment", "listSet.observe = ${it.map { it.name}}")
        }
        fragmentListViewModel.shopListIdLD.observe(viewLifecycleOwner) {
            Log.d("ListSetFragment", "shopListIdLD.observe = $it")
            if (it != ShopList.UNDEFINED_ID) {
                startShopListActivity()
            }
        }

        fragmentListViewModel.showCreateListForFile.observe(viewLifecycleOwner){
            Log.d("ListSetFragment", "showCreateListForFile.observe = $it")
            if (it) {
                onFabClick()
            }
        }

        fragmentListViewModel.oldFileName.observe(viewLifecycleOwner){ fileName ->
            Log.d("ListSetFragment", "oldFileName.observe = $fileName")
            fileName?.let {
                with(binding){
                    etListName.setText(it)
                    etListName.setSelection(etListName.text.length)
                }
            }
        }
    }

    override fun setupButtons() {
        with(binding) {
            buttonAddItem.setOnClickListener {
                onFabClickListener.onFabClick()
            }

            buttonCreateList.setOnClickListener { view ->
                if (etListName.text.isNotEmpty()) {
                    var fromFile = false
                    if (fragmentListViewModel.showCreateListForFile.value == true){
                        fromFile = true
                    }
                    fragmentListViewModel.addShopList(etListName.text?.toString(), fromFile)
                    val isError = fragmentListViewModel.errorInputName.value ?: true
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
                        fragmentListViewModel.resetErrorInputName()
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
        Log.d(
            "ListSetFragment",
            "startShopListActivity listId = ${fragmentListViewModel.shopListIdLD.value}"
        )
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
                with(binding) {
                    if (cardNewList.visibility == View.VISIBLE) {
                        etListName.setText("")
                        cardNewList.visibility = View.GONE

                        fragmentListViewModel.updateUiState(false, null)

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
        fragmentListViewModel.openShopList(itemId)
        with(binding) {
            if (cardNewList.visibility == View.VISIBLE) {
                etListName.setText("")
                cardNewList.visibility = View.GONE
            }
        }
    }

    override fun onFabClick(listId: Int?) {
        with(binding) {
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
    }

    companion object {
        fun newInstance() = ListSetFragment()
    }

}