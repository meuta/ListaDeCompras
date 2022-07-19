package com.example.listadecompras.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.listadecompras.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ShopItemActivity : AppCompatActivity() {

//    private lateinit var viewModel: ShopItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)

        val mode = intent.getStringExtra(SECOND_SCREEN_MODE)
        Log.d("ShopItemActivity", mode.toString())
        if (mode.toString() == MODE_EDIT){

            val itemId = intent.getIntExtra(SHOP_ITEM_ID, -1)
            Log.d("ShopItemActivity", "id = ${itemId.toString()}")
        }
    }


    companion object{
        private const val SECOND_SCREEN_MODE = "second_screen_mode"
        private const val SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"

        fun newIntentAddItem(context: Context): Intent{
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, itemId: Int): Intent{
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(SHOP_ITEM_ID, itemId)
            return intent
        }

    }
}
