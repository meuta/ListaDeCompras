package com.example.listadecompras.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

//    private var count = 0

    private lateinit var llShopList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        llShopList = findViewById(R.id.ll_shop_list)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.shopList.observe(this){
            Log.d("MainActivitySubscribeTest", it.toString())
/*
            if (count == 0) {
                count++
                val item = it[0]
                viewModel.changeEnableState(item)
            }
*/
        showList(it)
        }
    }

    private fun showList(list: List<ShopItem>) {
        llShopList.removeAllViews()
        for (shopItem in list){
            val layoutId = if (shopItem.enabled) {
                R.layout.item_shop_enabled
            } else {
                R.layout.item_shop_disabled
            }
            val view = LayoutInflater.from(this).inflate(layoutId, llShopList, false)
            val tvName = view.findViewById<TextView>(R.id.tv_name)
            val tvCount = view.findViewById<TextView>(R.id.tv_count)
            tvName.text = shopItem.name
            tvCount.text = shopItem.count.toString()
            view.setOnLongClickListener {
                viewModel.changeEnableState(shopItem)
                true
            }
            llShopList.addView(view)
        }
    }
}