package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.SwipeSwapAdapter
import com.obrigada_eu.listadecompras.presentation.SwipeSwapListFragment
import com.obrigada_eu.listadecompras.presentation.shop_list.ShopListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSetFragment : SwipeSwapListFragment<
        ShopList,
        FragmentListSetBinding,
        ListSetViewModel
        >(FragmentListSetBinding::inflate) {

    override val fragmentListViewModel: ListSetViewModel by viewModels()

    override lateinit var fragmentListAdapter: SwipeSwapAdapter<ShopList>

    override lateinit var layoutManager: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnBackPressedCallback()
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

        binding.viewModel = fragmentListViewModel

        layoutManager = binding.rvListSet.layoutManager as LinearLayoutManager

        addTextChangedListeners()
        setupButtons()
        observeViewModel()
    }

    private fun observeViewModel() {
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
                    fragmentListViewModel.addShopList(etListName.text?.toString())
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

    override fun setupClickListener() {
        fragmentListAdapter.onItemClickListener = {
            fragmentListViewModel.openShopList(it.id)
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

    override fun dragListItem(from: Int, to: Int) {
        fragmentListViewModel.dragShopList(from, to)
    }

    override fun undoDelete() {
        fragmentListViewModel.undoDelete()
    }

    companion object {
        fun newInstance() = ListSetFragment()
    }

}