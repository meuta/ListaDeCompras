package com.example.listadecompras.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class ShopItemActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_shop_item)

        parseIntent()

        initViews()

        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]    //initialization

//        val mode = intent.getStringExtra(SECOND_SCREEN_MODE)
//        Log.d("ShopItemActivity", mode.toString())

//        if (mode.toString() == MODE_EDIT) {
//            val itemId = intent.getIntExtra(SHOP_ITEM_ID, -1)
//            Log.d("ShopItemActivity", "id = ${itemId.toString()}")
//        }

        when (screenMode){
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun initViews() {
        tilName = findViewById(R.id.tv_name)
        tilCount = findViewById(R.id.tv_count)
        etName = findViewById(R.id.et_name)
        etCount = findViewById(R.id.et_count)
        btnSave = findViewById(R.id.btn_save)
    }

    private fun parseIntent(){
        if (!intent.hasExtra(SECOND_SCREEN_MODE)){
            throw RuntimeException("Param second_screen_mode is absent")
        }
        val mode = intent.getStringExtra(SECOND_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD){
            throw RuntimeException("Unknown second_screen_mode: $mode")
        }
        screenMode = mode

        if (mode.toString() == MODE_EDIT) {
            if (!intent.hasExtra(SHOP_ITEM_ID)){
                throw RuntimeException("Param shop_item_id is absent")
            }
            itemId = intent.getIntExtra(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
            Log.d("ShopItemActivity", "id = ${itemId.toString()}")
        }
    }

    private fun launchAddMode(){

    }

    private fun launchEditMode(){
        viewModel.shopItem.observe(this){
            Log.d("ShopItemActivitySubscribeTest", it.toString())
        }
        viewModel.getShopItem(itemId)
    }

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
