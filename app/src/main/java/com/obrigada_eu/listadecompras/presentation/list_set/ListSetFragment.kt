package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.OnFocusChangeListener
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.AreYouShureDialogLayoutBinding
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.SwipeSwapAdapter
import com.obrigada_eu.listadecompras.presentation.SwipeSwapListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
        addEditTextFocusChangedListener()
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                fragmentListViewModel.allListsWithoutItemsStateFlow.collect {
                    fragmentListAdapter.submitList(it)
//            Log.d(TAG, "listSet.observe = ${it.map { it.name}}")
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                fragmentListViewModel.cardNewListVisibilityStateFlow.collect {isVisible ->
//                    Log.d(TAG, "observeViewModel: cardNewListVisibilityStateFlow.collect = $isVisible")
                    with(binding){
                        if (!isVisible) {

                            etListNameFromTitle.setText("")
                            cardNewList.visibility = GONE
                            coverView.visibility = GONE
                            val inputMethodManager =
                                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(cardNewList.windowToken, 0)


                        } else {
                            coverView.visibility = VISIBLE
                            cardNewList.visibility = VISIBLE
                            delay(25)
                            val inputMethodManager =
                                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.showSoftInput(etListNameFromTitle, 0)
                        }
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
                lifecycleScope.launch {
                    val alterName = if (fragmentListViewModel.listNameFromFileContent.first() != null) {
                        etListNameFromContent.trimmedText()
                    } else null

//                    Log.d(TAG, "setupButtons: alterName = $alterName")
                    fragmentListViewModel.addShopList(
                        etListNameFromTitle.trimmedText(),
                        fragmentListViewModel.fromTxtFile.first(),
                        alterName = alterName
                    )
                }
            }

            buttonCanselCreateList.setOnClickListener {
                showAlertDialog()
            }

            radioGroupListName.setOnCheckedChangeListener { _, checkedId ->
//                Log.d(TAG, "radioGroupListName: checkedId = $checkedId")
                fragmentListViewModel.setIsNameFromTitle(isFromTitle = when (checkedId) {
                    R.id.radio_tilte -> true
                    R.id.radio_content -> false
                    NO_RADIO_BUTTON_CHECKED_ID -> null
                    else -> throw RuntimeException("unknown radio button ID")
                })
            }
        }
    }

    private fun EditText.trimmedText() = this.text.toString().let { content ->
        content.trim().let { trimmedContent ->
            if (trimmedContent != content) {
                this.setText(trimmedContent)
                this.setSelection(trimmedContent.length)
                trimmedContent
            } else {
                content
            }
        }
    }

    private fun addEditTextFocusChangedListener(){
        with(binding){
            val editTextFocusChangedListener = OnFocusChangeListener { view, hasFocus ->
                if (view == etListNameFromTitle && hasFocus) {
                    radioTilte.isChecked = true
                    fragmentListViewModel.setIsNameFromTitle(true)
                }
                if (view == etListNameFromContent && hasFocus) {
                    radioContent.isChecked = true
                    fragmentListViewModel.setIsNameFromTitle(false)
                }
            }
            etListNameFromTitle.onFocusChangeListener = editTextFocusChangedListener
            etListNameFromContent.onFocusChangeListener = editTextFocusChangedListener
        }
    }


    private fun addTextChangedListeners() {
        val inputErrorListener = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(binding) {
                    if (etListNameFromTitle.text.hashCode() == s.hashCode()) {
//                        Log.d(TAG, "onTextChanged: etListNameFromTitle.tag = ${etListNameFromTitle.tag}")
                        if( etListNameFromTitle.tag == null ) {
                            // Value changed by user
                            fragmentListViewModel.resetErrorInputNameTitle()
//                            Log.d(TAG, "onTextChanged: resetErrorInputNameTitle")
                        }
                    }
                    if (etListNameFromContent.text.hashCode() == s.hashCode()) {
//                        Log.d(TAG, "onTextChanged: etListNameFromContent.tag = ${etListNameFromContent.tag}")
                        if( etListNameFromContent.tag == null ) {
                            // Value changed by user
                            fragmentListViewModel.resetErrorInputNameContent()
//                            Log.d(TAG, "onTextChanged: resetErrorInputName()")
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        with(binding) {
            etListNameFromTitle.addTextChangedListener(inputErrorListener)
            etListNameFromContent.addTextChangedListener(inputErrorListener)
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
                    val isVisible = fragmentListViewModel.cardNewListVisibilityStateFlow.first()

                    if (isVisible) {


                        showAlertDialog()

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

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireActivity()).create()
        val dialogBinding = AreYouShureDialogLayoutBinding.inflate(layoutInflater)
        with(dialogBinding) {
            noButton.setOnClickListener {
                alertDialog.dismiss()
            }
            yesButton.setOnClickListener {
                fragmentListViewModel.resetListNameFromContent()
                fragmentListViewModel.resetUserCheckedAlterName()
                fragmentListViewModel.updateUiState(
                    cardNewListVisibility = false,
                    showCreateListForFile = false,
                    oldFileName = null,
                    uri = null
                )
                alertDialog.dismiss()
            }
            alertDialog.setView(root)
        }
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    override fun onListItemClick(itemId: Int) {
        fragmentListViewModel.setCurrentListId(itemId)
        fragmentListViewModel.updateUiState(
            cardNewListVisibility = false,
            showCreateListForFile = false,
            oldFileName = null,
            uri = null
        )
    }

    override fun onFabClick() {
        lifecycleScope.launch {

            fragmentListViewModel.updateUiState(
                cardNewListVisibility = true,
                showCreateListForFile = false,
                oldFileName = null,
                uri = null
            )

                binding.cardNewList.visibility = VISIBLE

            with(binding.etListNameFromTitle) {
                tag = TAG_ERROR_INPUT_NAME
                setText(requireContext().resources.getString(R.string.new_list))
                tag = null
                requestFocus()
                delay(10)
                val inputMethodManager =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(this, 0)
                selectAll()
            }
        }
    }

    companion object {

        private const val TAG = "ListSetFragment"
        private const val TAG_ERROR_INPUT_NAME = 101
        private const val NO_RADIO_BUTTON_CHECKED_ID = -1

        fun newInstance() = ListSetFragment()
    }

}