package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.AreYouShureLayoutBinding
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
            Log.d(TAG, "listSet.observe = ${it.map { it.name}}")
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

                            etListNameTitle.setText("")
                            cardNewList.visibility = View.GONE
                            val inputMethodManager =
                                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(cardNewList.windowToken, 0)

                        } else {

                            cardNewList.visibility = View.VISIBLE
                            etListNameTitle.tag = TAG_ERROR_INPUT_NAME
                            etListNameTitle.setText(requireContext().resources.getString(R.string.new_list))
                            etListNameTitle.requestFocus()
                            etListNameTitle.setSelection(0, etListNameTitle.text.length)
                            etListNameTitle.tag = null

                            val inputMethodManager =
                                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.showSoftInput(etListNameTitle, 0)
                        }
                    }
                }
            }
        }


        fragmentListViewModel.oldFileName.observe(viewLifecycleOwner){ fileName ->
            Log.d(TAG, "oldFileName.observe = $fileName")
            fileName?.let {
                with(binding){
                    etListNameTitle.tag = TAG_ERROR_INPUT_NAME
                    etListNameTitle.setText(it)
                    etListNameTitle.setSelection(etListNameTitle.text.length)
                    etListNameTitle.tag = null
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                fragmentListViewModel.isTitle.collect(){
                    Log.d(TAG, "observeViewModel: isTitle.collect = $it")
                    if (it == null) {
                        binding.radioTilte.isChecked = true
                        binding.radioContent.isChecked = false
                        fragmentListViewModel.setIsTitle(true)
                    }
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
                val alterName = if (fragmentListViewModel.listNameFromFileContent.value != null) {
                    etListNameContent.text?.toString()
                } else null

                Log.d(TAG, "setupButtons: alterName = $alterName")
                fragmentListViewModel.addShopList(
                    etListNameTitle.text?.toString(),
                    fragmentListViewModel.fromTxtFile.value,
                    alterName = alterName
                )
            }

            buttonCanselCreateList.setOnClickListener {

                val alertDialog = AlertDialog.Builder(requireActivity())
                    .create()
                val dialogBinding = AreYouShureLayoutBinding.inflate(layoutInflater)
                with(dialogBinding){
                    noButton.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    yesButton.setOnClickListener {
                        fragmentListViewModel.resetListNameFromContent()
                        fragmentListViewModel.setIsTitle(null)
                        fragmentListViewModel.resetUserCheckedDifferNames()
                        fragmentListViewModel.updateUiState(false, false, null, null, null)
                        alertDialog.dismiss()
                    }
                    alertDialog.setView(root)
                }
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }

            radioTilte.setOnCheckedChangeListener { _, isChecked ->
                Log.d("RADIO", "Title is checked: $isChecked")
                fragmentListViewModel.setIsTitle(isTitle = isChecked)
            }

            radioContent.setOnCheckedChangeListener { _, isChecked ->
                Log.d("RADIO", "Content is checked: $isChecked")
                fragmentListViewModel.setIsTitle(isTitle = !isChecked)
            }

        }
    }

    private fun addTextChangedListeners() {
        val inputErrorListener = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(binding) {
                    if (etListNameTitle.text.hashCode() == s.hashCode()) {
//                        Log.d(TAG, "onTextChanged: etListNameTitle.tag = ${etListNameTitle.tag}")
                        if( etListNameTitle.tag == null ) {
                            // Value changed by user
                            fragmentListViewModel.resetErrorInputNameTitle()
                            Log.d(TAG, "onTextChanged: resetErrorInputNameTitle")
                        }
                    }
                    if (etListNameContent.text.hashCode() == s.hashCode()) {
//                        Log.d(TAG, "onTextChanged: etListNameContent.tag = ${etListNameContent.tag}")
                        if( etListNameContent.tag == null ) {
                            // Value changed by user
                            fragmentListViewModel.resetErrorInputNameContent()
                            Log.d(TAG, "onTextChanged: resetErrorInputName()")
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        with(binding) {
            etListNameTitle.addTextChangedListener(inputErrorListener)
            etListNameContent.addTextChangedListener(inputErrorListener)
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
                        fragmentListViewModel.setIsTitle(null)
                        fragmentListViewModel.resetUserCheckedDifferNames()
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

            Log.d(TAG, "onListItemClick: isVisible = $isVisible")
            Log.d(TAG, "onListItemClick: itemId = $itemId")

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