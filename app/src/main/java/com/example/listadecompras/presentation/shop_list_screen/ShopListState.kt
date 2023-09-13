package com.example.listadecompras.presentation.shop_list_screen

import com.example.listadecompras.domain.ShopItem

data class ShopListState(
    val shopList: List<ShopItem> = emptyList(),
)