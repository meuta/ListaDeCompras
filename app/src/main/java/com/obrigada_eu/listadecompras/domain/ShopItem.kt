package com.obrigada_eu.listadecompras.domain

data class ShopItem(
    val name: String,
    val count: Double,
    var enabled: Boolean,
    val id: Int = UNDEFINED_ID,
    val shopListId: Int = UNDEFINED_ID
)
{
    companion object{
        const val UNDEFINED_ID = 0
    }
}
