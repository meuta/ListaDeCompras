package com.example.listadecompras.presentation

sealed class Screen(val route: String) {
    object FirstScreen: Screen("first_screen")
    object ShopItemScreen: Screen("shop_item_screen")
}

