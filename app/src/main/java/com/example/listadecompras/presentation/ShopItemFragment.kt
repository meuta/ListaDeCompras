package com.example.listadecompras.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.listadecompras.databinding.FragmentShopItemBinding
import com.example.listadecompras.domain.ShopItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopItemFragment : Fragment() {

    private val viewModel: ShopItemViewModel by viewModels()

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding      //We can use it just between onCreateView and onDestroyView not inclusive.
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == null")

    private var screenMode = MODE_UNKNOWN
    private var itemId = ShopItem.UNDEFINED_ID

    override fun onAttach(context: Context) {


        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw java.lang.RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ShopItemFragment", "onCreate")
        super.onCreate(savedInstanceState)
        parseParams()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        addTextChangedListeners()

        launchRightMode()

        observeViewModel()

    }


    private fun observeViewModel() {
        viewModel.closeScreen.observe(viewLifecycleOwner) {
            Log.d("closeScreenSubscribeTest", it.toString())
            activity?.onBackPressed()
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun addTextChangedListeners() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


    private fun parseParams() {

        val args = requireArguments()
        if (!args.containsKey(SECOND_SCREEN_MODE)) {
            throw RuntimeException("Param second_screen_mode is absent")
        }

        val mode = args.getString(SECOND_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown second_screen_mode: $mode")
        }
        screenMode = mode

        if (screenMode == MODE_EDIT && itemId == ShopItem.UNDEFINED_ID) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop_item_id is absent")
            }
            itemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
            Log.d("ShopItemActivity", "id = $itemId")
        }
    }

    private fun launchAddMode() {
        binding.btnSave.setOnClickListener {
            viewModel.addShopItem(binding.etName.text?.toString(), binding.etCount.text?.toString())
        }

    }

    private fun launchEditMode() {
        viewModel.getShopItem(itemId)
        binding.btnSave.setOnClickListener {
            viewModel.editShopItem(
                binding.etName.text?.toString(),
                binding.etCount.text?.toString()
            )
        }

    }


    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }


    companion object {
        private const val SECOND_SCREEN_MODE = "second_screen_mode"
        private const val SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""


        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SECOND_SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(itemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SECOND_SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, itemId)

                }
            }
        }
    }

}