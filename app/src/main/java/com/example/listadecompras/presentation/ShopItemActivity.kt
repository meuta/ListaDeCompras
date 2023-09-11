package com.example.listadecompras.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
//class ShopItemActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {
class ShopItemActivity : AppCompatActivity(), ShopItemComposeFragment.OnEditingFinishedListener {

    private var screenMode = MODE_UNKNOWN
    private var itemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_shop_item)

        parseIntent()

        if (savedInstanceState == null) {            //Means that the Activity was not recreated
            launchRightMode()
        }

    }

    override fun onEditingFinished() {
        finish()
    }

    private fun launchRightMode() {

        val fragment = when (screenMode) {
//            MODE_ADD -> ShopItemFragment.newInstanceAddItem()
//            MODE_EDIT -> ShopItemFragment.newInstanceEditItem(itemId)
            MODE_ADD -> ShopItemComposeFragment.newInstanceAddItem()
            MODE_EDIT -> ShopItemComposeFragment.newInstanceEditItem(itemId)
            else -> throw RuntimeException("Unknown second_screen_mode: $screenMode")

        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .commit()                                   // launch a transaction to execution
    }


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
