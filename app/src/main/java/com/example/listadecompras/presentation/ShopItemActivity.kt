package com.example.listadecompras.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception

class ShopItemActivity : AppCompatActivity() {

    //    private lateinit var viewModel: ShopItemViewModel
//
//    private lateinit var tilName: TextInputLayout
//    private lateinit var tilCount: TextInputLayout
//    private lateinit var etName: EditText
//    private lateinit var etCount: EditText
//    private lateinit var btnSave: Button
//
    private var screenMode = MODE_UNKNOWN
    private var itemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)

        parseIntent()
//        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]    //initialization
//
//        initViews()
//
//        addTextChangedListeners()
//
        launchRightMode()
//
//        observeViewModel()

    }

    //    private fun observeViewModel() {
//        viewModel.errorInputName.observe(this) {
//            Log.d("errorInputNameSubscribeTest", it.toString())
//            val message = if (it) {
//                getString((R.string.error_input_name))
//            } else {
//                null
//            }
//            tilName.error = message
//        }
//
//        viewModel.errorInputCount.observe(this) {
//            Log.d("errorInputCountSubscribeTest", it.toString())
//            val message = if (it) {
//                getString((R.string.error_input_count))
//            } else {
//                null
//            }
//            tilCount.error = message
//        }
//
//        viewModel.closeScreen.observe(this) {
//            Log.d("closeScreenSubscribeTest", it.toString())
//            finish()
//        }
//    }
//
    private fun launchRightMode() {
//        when (screenMode) {
//            MODE_ADD -> launchAddMode()
//            MODE_EDIT -> launchEditMode()
//        }

        val fragment = when (screenMode) {
            MODE_ADD -> ShopItemFragment.newInstanceAddItem()
            MODE_EDIT -> ShopItemFragment.newInstanceEditItem(itemId)
            else -> throw RuntimeException("Unknown second_screen_mode: $screenMode")

        }

        supportFragmentManager.beginTransaction()
            .add(R.id.shop_item_container, fragment)    //adding fragment to container
            .commit()                                   // launch a transaction to execution
    }

    //
//    private fun addTextChangedListeners() {
//        etName.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                viewModel.resetErrorInputName()
//            }
//            override fun afterTextChanged(s: Editable?) {
//            }
//        })
//
//        etCount.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                viewModel.resetErrorInputCount()
//            }
//            override fun afterTextChanged(s: Editable?) {
//            }
//        })
//    }
//
//    private fun initViews() {
//        tilName = findViewById(R.id.til_name)
//        tilCount = findViewById(R.id.til_count)
//        etName = findViewById(R.id.et_name)
//        etCount = findViewById(R.id.et_count)
//        btnSave = findViewById(R.id.btn_save)
//    }
//
    private fun parseIntent() {
        if (!intent.hasExtra(SECOND_SCREEN_MODE)) {
            throw RuntimeException("Param second_screen_mode is absent")
        }
        val mode = intent.getStringExtra(SECOND_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown second_screen_mode: $mode")
        }
        screenMode = mode

        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop_item_id is absent")
            }
            itemId = intent.getIntExtra(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
            Log.d("ShopItemActivity", "id = $itemId")
        }
    }
//
//    private fun launchAddMode(){
//        btnSave.setOnClickListener {
//                viewModel.addShopItem(etName.text?.toString(), etCount.text?.toString())
//        }
//    }
//
//    private fun launchEditMode(){
//        viewModel.getShopItem(itemId)
//        viewModel.shopItem.observe(this){
//            Log.d("shopItemSubscribeTest", it.toString())
//            etName.setText(it.name)
//            etCount.setText(it.count.toString())
//        }
//        btnSave.setOnClickListener {
//                viewModel.editShopItem(etName.text?.toString(), etCount.text?.toString())
//        }
//    }

    companion object {
        private const val SECOND_SCREEN_MODE = "second_screen_mode"
        private const val SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, itemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(SHOP_ITEM_ID, itemId)
            return intent
        }
    }
}
