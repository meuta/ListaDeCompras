package com.example.listadecompras.presentation

sealed class Screen(val route: String) {
    object ShopListScreen: Screen("shop_list_screen")
    object ShopItemScreen: Screen("shop_item_screen")
}

