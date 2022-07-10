package com.example.listadecompras.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.listadecompras.R

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // Subscribe to the object shopList:
        viewModel.shopList.observe(this){
            Log.d("MainActivitySubscribeTest", it.toString())
        }
        viewModel.getShopList()
    }
}