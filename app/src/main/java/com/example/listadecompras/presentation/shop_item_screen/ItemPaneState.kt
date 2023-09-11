package com.example.listadecompras.presentation.shop_item_screen

data class ItemPaneState(
    val name: String = "",
    val count: String = "0.0",
    val id: Int = 0,
    val showErrorName: Boolean = false,
    val showErrorCount: Boolean = false,
){
}
