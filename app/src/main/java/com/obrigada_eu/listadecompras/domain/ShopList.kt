package com.obrigada_eu.listadecompras.domain

data class ShopList(
    val name: String,
    val id: Int = UNDEFINED_ID,
    val itemList: List<ShopItem>
)
{
    companion object{
        const val UNDEFINED_ID = 0
    }
}