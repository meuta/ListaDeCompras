package com.obrigada_eu.listadecompras.domain.shop_list

data class ShopList(
    val name: String,
    var enabled: Boolean,
    val id: Int = UNDEFINED_ID
)
{
    companion object{
        const val UNDEFINED_ID = 0
    }
}