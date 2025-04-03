package com.obrigada_eu.listadecompras.presentation.list_set

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.AreYouShureDialogLayoutBinding
import com.obrigada_eu.listadecompras.databinding.FragmentNewListCreationBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListWithItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NewListCreationFragment : Fragment() {

    private var fromTxtFile: Boolean = false
    private var nameFromTitle: String = ""
    private var shopList: ShopListWithItems? = null

    private val fragmentViewModel: NewListCreationViewModel by viewModels()
    private val activityViewModel: ListSetViewModel by activityViewModels()


    private var _binding: FragmentNewListCreationBinding? = null
    private val binding: FragmentNewListCreationBinding
        get() = _binding ?: throw RuntimeException("FragmentNewListCreationBinding == null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnBackPressedCallback()
        parseParams()
    }

    private fun parseParams() {

        val args = requireArguments()

        if (!args.containsKey(FROM_TXT_FILE)) {
            throw RuntimeException("Param fromTxtFile is absent")
        }
        fromTxtFile = args.getBoolean(FROM_TXT_FILE, false)

        nameFromTitle = args.getString(NAME_FROM_TITLE_FIELD, "") ?: ""

        shopList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable(SHOP_ITEMS, ShopListWithItems::class.java)
        } else {
            @Suppress("DEPRECATION") args.getParcelable(SHOP_ITEMS)
        }

        fragmentViewModel.updateUiState(fromTxtFile, nameFromTitle, shopList?.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewListCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = fragmentViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (savedInstanceState == null) {
            with(binding.etListNameFromTitle) {
                requestFocus()
                tag = TAG_ERROR_INPUT_NAME
                if (fromTxtFile) {
                    setText(nameFromTitle)
                    setSelection(nameFromTitle.length)
                } else {
                    setText(resources.getString(R.string.new_list))
                    post { selectAll() }
                }
                tag = null
                post {
                    WindowCompat
                        .getInsetsController(requireActivity().window, binding.root)
                        .show(WindowInsetsCompat.Type.ime())
                }

                shopList?.name?.let { binding.etListNameFromContent.setText(it) }
            }
        }

        addTextChangedListeners()
        addEditTextFocusChangedListener()
        setupButtons()

        observeViewModel()
    }


    private fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                fragmentViewModel.closeFragment.collect {
//                    Log.d(TAG, "observeViewModel: resetListFragmentUI = $it")
                    it?.let { closeMyself() }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentViewModel.listSaved.collect {
//                    Log.d(TAG, "observeViewModel: listSaved = $it")
                    it?.let { activityViewModel.setListSaved(it) }
                }
            }
        }
    }

    private fun resetErrorInputName(editText: EditText, text: CharSequence?) {
        with(binding) {
            if (editText.text == text) {
                if (editText.tag == null) {
                    // Value changed by user
                    fragmentViewModel.resetErrorInputName(
                        when(editText) {
                            etListNameFromTitle -> NAME_FROM_TITLE_FIELD
                            etListNameFromContent -> NAME_FROM_CONTENT_FIELD
                            else -> ""
                        }
                    )
                }
            }
        }
    }

    private fun addTextChangedListeners() {
        with(binding) { listOf(etListNameFromTitle, etListNameFromContent).forEach {
            it.doOnTextChanged { text, _, _, _ -> resetErrorInputName(it, text) }
        } }
    }

    private fun addEditTextFocusChangedListener(){
        with(binding){
            val editTextFocusChangedListener = View.OnFocusChangeListener { view, hasFocus ->
                if (view == etListNameFromTitle && hasFocus) {
                    radioTilte.isChecked = true
                    fragmentViewModel.setIsNameFromTitle(true)
                }
                if (view == etListNameFromContent && hasFocus) {
                    radioContent.isChecked = true
                    fragmentViewModel.setIsNameFromTitle(false)
                }
            }

            listOf(etListNameFromTitle, etListNameFromContent).forEach {
                it.onFocusChangeListener  = editTextFocusChangedListener
            }
        }
    }


    private fun setupButtons() {
        with(binding) {

            buttonCreateList.setOnClickListener {
                lifecycleScope.launch {
                    val alterName = shopList?.let { etListNameFromContent.trimmedText() }
                    fragmentViewModel.addShopList(
                        etListNameFromTitle.trimmedText(),
                        shopList,
                        alterName
                    )
                }
            }

            buttonCanselCreateList.setOnClickListener {
                showAlertDialog()
            }

            radioGroupListName.setOnCheckedChangeListener { _, checkedId ->
                fragmentViewModel.setIsNameFromTitle(isFromTitle = when (checkedId) {
                    R.id.radio_tilte -> true
                    R.id.radio_content -> false
                    else -> throw RuntimeException("unknown radio button ID")
                })
            }
        }
    }


    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireActivity()).create()
        val dialogBinding = AreYouShureDialogLayoutBinding.inflate(layoutInflater)
        with(dialogBinding) {
            noButton.setOnClickListener {
                alertDialog.dismiss()
            }
            yesButton.setOnClickListener {
                closeMyself()
                alertDialog.dismiss()
            }
            alertDialog.setView(root)
        }
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun closeMyself() {
        parentFragmentManager.popBackStack()
        activityViewModel.resetCreateListFragmentState()
    }


    private fun EditText.trimmedText() = this.text.toString().let { content ->
        content.trim().let { trimmedContent ->
            if (trimmedContent != content) {
                this.setText(trimmedContent)
                this.setSelection(trimmedContent.length)
            }
            trimmedContent
        }
    }


    private fun setOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true ) {
            override fun handleOnBackPressed() = showAlertDialog()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        WindowCompat
            .getInsetsController(requireActivity().window, binding.root)
            .hide(WindowInsetsCompat.Type.ime())
        _binding = null
    }


    companion object {

        private const val TAG = "CreateNewListFragment"

        private const val TAG_ERROR_INPUT_NAME = 101

        private const val FROM_TXT_FILE = "from_txt_file"
        const val NAME_FROM_TITLE_FIELD = "title"
        const val NAME_FROM_CONTENT_FIELD = "content"
        private const val SHOP_ITEMS = "shopItems"

        @JvmStatic
        fun newInstance(state: NewListCreationFragmentState) =
            NewListCreationFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(FROM_TXT_FILE, state.fromTxtFile)
                    putString(NAME_FROM_TITLE_FIELD, state.nameFromTitle)
                    putParcelable(SHOP_ITEMS, state.shopList)
                }
            }
    }
}


data class NewListCreationFragmentState(
    val fromTxtFile: Boolean = false,
    val nameFromTitle: String? = null,
    val shopList: ShopListWithItems? = null
)