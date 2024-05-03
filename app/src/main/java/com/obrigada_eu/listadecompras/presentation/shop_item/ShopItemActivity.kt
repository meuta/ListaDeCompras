package com.obrigada_eu.listadecompras.presentation.shop_item

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityShopItemBinding
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopItemActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    private var screenMode = MODE_UNKNOWN
    private var itemId = ShopItem.UNDEFINED_ID
    private var listId = ShopList.UNDEFINED_ID
    private var listName = SHOP_LIST_UNKNOWN

    private lateinit var binding: ActivityShopItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        parseIntent()
        setupActionBar(listName)

        if (savedInstanceState == null) {            //Means that the Activity was not recreated
            launchRightMode()
        }
    }

    private fun setupActionBar(listName: String) {
        setSupportActionBar(binding.toolbarShopItemActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        binding.toolbarShopItemActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvToolbarShopItemActivity.text = listName
    }

    override fun onEditingFinished() {
        finish()
    }

    private fun launchRightMode() {

        val fragment = when (screenMode) {
            MODE_ADD -> ShopItemFragment.newInstanceAddItem(listId)
            MODE_EDIT -> ShopItemFragment.newInstanceEditItem(itemId)
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
        }

        if (screenMode == MODE_ADD) {
            if (!intent.hasExtra(SHOP_LIST_ID)) {
                throw RuntimeException("Param shop_list_id is absent")
            }
            listId = intent.getIntExtra(SHOP_LIST_ID, ShopList.UNDEFINED_ID)
        }

        if (!intent.hasExtra(SHOP_LIST_NAME)) {
            throw RuntimeException("Param shop_list_name is absent")
        }
        intent.getStringExtra(SHOP_LIST_NAME)?.let { listName = it }
    }

    companion object {
        private const val SECOND_SCREEN_MODE = "second_screen_mode"
        private const val SHOP_ITEM_ID = "shop_item_id"
        private const val SHOP_LIST_ID = "shop_list_id"
        private const val SHOP_LIST_NAME = "shop_list_name"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""
        private const val SHOP_LIST_UNKNOWN = "Shop List"

        private const val TAG = "ShopItemActivity"

        fun newIntentAddItem(context: Context, listId: Int, listName: String): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_ADD)
            intent.putExtra(SHOP_LIST_ID, listId)
            intent.putExtra(SHOP_LIST_NAME, listName)
            return intent
        }

        fun newIntentEditItem(context: Context, itemId: Int, listName: String): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(SECOND_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(SHOP_ITEM_ID, itemId)
            intent.putExtra(SHOP_LIST_NAME, listName)
            return intent
        }
    }
}
