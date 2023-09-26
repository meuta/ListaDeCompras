package com.example.listadecompras.presentation.first_screen

import com.example.listadecompras.domain.ShopItem

data class FirstScreenState (

    val shopList: List<ShopItem> = emptyList(),
//    val showItemScreen: Boolean? = null
)