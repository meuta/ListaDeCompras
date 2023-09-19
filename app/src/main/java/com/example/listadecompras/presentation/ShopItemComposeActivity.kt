package com.example.listadecompras.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.shop_item_screen.ShopItemScreen
import com.example.listadecompras.ui.theme.ListaDeComprasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopItemComposeActivity : AppCompatActivity() {

    private var screenMode = MODE_UNKNOWN
    private var itemId = ShopItem.UNDEFINED_ID
    private var fragment: ShopItemComposeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListaDeComprasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    ShopItemScreen(
                        fragment = fragment,
                        fragmentManager = supportFragmentManager
                    )
                }
            }
        }
        parseIntent()

//        if (savedInstanceState == null) {            //Means that the Activity was not recreated
        launchRightMode()
//        }
    }

    private fun launchRightMode() {

        fragment = when (screenMode) {
            MODE_ADD -> ShopItemComposeFragment.newInstanceAddItem()
            MODE_EDIT -> ShopItemComposeFragment.newInstanceEditItem(itemId)
            else -> throw RuntimeException("Unknown second_screen_mode: $screenMode")

        }
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
            val intent = Intent(context, ShopItemComposeActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, itemId: Int): Intent {
            val intent = Intent(context, ShopItemComposeActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(SHOP_ITEM_ID, itemId)
            return intent
        }
    }

}
