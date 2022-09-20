package com.example.listadecompras.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment(
//    private val screenMode: String = MODE_UNKNOWN,
//    private val itemId: Int = ShopItem.UNDEFINED_ID
) : Fragment() {

    private lateinit var viewModel: ShopItemViewModel
    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText
    private lateinit var btnSave: Button

    private var screenMode = MODE_UNKNOWN
    private var itemId = ShopItem.UNDEFINED_ID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        parseParams()
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]    //initialization

        initViews(view)

        addTextChangedListeners()

        launchRightMode()

        observeViewModel()

    }


    private fun observeViewModel() {
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            Log.d("errorInputNameSubscribeTest", it.toString())
            val message = if (it) {
                getString((R.string.error_input_name))
            } else {
                null
            }
            tilName.error = message
        }

        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            Log.d("errorInputCountSubscribeTest", it.toString())
            val message = if (it) {
                getString((R.string.error_input_count))
            } else {
                null
            }
            tilCount.error = message
        }

        viewModel.closeScreen.observe(viewLifecycleOwner) {
            Log.d("closeScreenSubscribeTest", it.toString())
            activity?.onBackPressed()
            requireActivity()
            context
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun addTextChangedListeners() {
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun initViews(view: View) {
        with(view) {
            tilName = findViewById(R.id.til_name)
            tilCount = findViewById(R.id.til_count)
            etName = findViewById(R.id.et_name)
            etCount = findViewById(R.id.et_count)
            btnSave = findViewById(R.id.btn_save)
        }
    }

    private fun parseParams() {

//        if (!intent.hasExtra(SECOND_SCREEN_MODE)){
//            throw RuntimeException("Param second_screen_mode is absent")
//        }

//        if (screenMode != MODE_ADD && screenMode != MODE_EDIT){
//            throw RuntimeException("Param second_screen_mode is absent: ${screenMode?:"null"}")
//        }
        val args = requireArguments()
        if (!args.containsKey(SECOND_SCREEN_MODE)) {
            throw RuntimeException("Param second_screen_mode is absent")
        }


//        val mode = intent.getStringExtra(SECOND_SCREEN_MODE)
//        if (mode != MODE_EDIT && mode != MODE_ADD){
//            throw RuntimeException("Unknown second_screen_mode: $mode")
//        }
//        screenMode = mode

//        if (screenMode == MODE_EDIT && itemId == ShopItem.UNDEFINED_ID) {
////            if (!intent.hasExtra(SHOP_ITEM_ID)){
//            throw RuntimeException("Param shop_item_id is absent")
////            }
////            itemId = intent.getIntExtra(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
////            Log.d("ShopItemActivity", "id = $itemId")
//        }

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
        btnSave.setOnClickListener {
            viewModel.addShopItem(etName.text?.toString(), etCount.text?.toString())
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(itemId)
        viewModel.shopItem.observe(viewLifecycleOwner) {
            Log.d("shopItemSubscribeTest", it.toString())
            etName.setText(it.name)
            etCount.setText(it.count.toString())
        }
        btnSave.setOnClickListener {
            viewModel.editShopItem(etName.text?.toString(), etCount.text?.toString())
        }
    }

    companion object {
        private const val SECOND_SCREEN_MODE = "second_screen_mode"
        private const val SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""

//        fun newIntentAddItem(context: Context): Intent {
//            val intent = Intent(context, ShopItemActivity::class.java)
//            intent.putExtra(SECOND_SCREEN_MODE, MODE_ADD)
//            return intent
//        }
//
//        fun newIntentEditItem(context: Context, itemId: Int): Intent {
//            val intent = Intent(context, ShopItemActivity::class.java)
//            intent.putExtra(SECOND_SCREEN_MODE, MODE_EDIT)
//            intent.putExtra(SHOP_ITEM_ID, itemId)
//            return intent
//        }

        fun newInstanceAddItem(): ShopItemFragment {
//            val args = Bundle()
//            args.putString(SECOND_SCREEN_MODE, MODE_ADD)
//            val fragment = ShopItemFragment()
//            fragment.arguments = args
//            return fragment

//            val args = Bundle().apply {
//                putString(SECOND_SCREEN_MODE, MODE_ADD)
//            }
//            return ShopItemFragment().apply {
//                arguments = args
//            }

            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SECOND_SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(itemId: Int): ShopItemFragment {
//            return ShopItemFragment(MODE_EDIT, itemId)
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SECOND_SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, itemId)

                }
            }
        }
    }

}