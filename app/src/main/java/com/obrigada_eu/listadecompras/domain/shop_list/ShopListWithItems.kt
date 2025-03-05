package com.obrigada_eu.listadecompras.domain.shop_list

import android.os.Parcelable
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopListWithItems(
    val name: String,
    var enabled: Boolean,
    val itemList: List<ShopItem>,
    val id: Int = UNDEFINED_ID
) : Parcelable {
    companion object{
        const val UNDEFINED_ID = 0
    }
}
