package com.obrigada_eu.listadecompras.domain

data class ShopItem(
    val name: String,
    val count: Double,
    var enabled: Boolean,
    var id: Int = UNDEFINED_ID
)
{
    companion object{
        const val UNDEFINED_ID = 0
    }
}
