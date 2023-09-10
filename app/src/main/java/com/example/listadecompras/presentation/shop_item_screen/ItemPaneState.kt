package com.example.listadecompras.presentation.shop_item_screen

data class ItemPaneState(
    val name: String = "",
    val showError: Boolean = false,
//    val screenMode: String = ""
){
    var isValidName: Boolean = name.isNotBlank()
}
