package com.obrigada_eu.listadecompras.domain.shop_list

import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem

data class ShopListWithItems(
    val name: String,
    var enabled: Boolean,
    val itemList: List<ShopItem>,
    val id: Int = UNDEFINED_ID
)
{
    companion object{
        const val UNDEFINED_ID = 0
    }
}
