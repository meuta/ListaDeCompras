package com.obrigada_eu.listadecompras.domain.shop_item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopItem(
    val name: String,
    val count: Double?,
    val units: String?,
    var enabled: Boolean,
    val id: Int = UNDEFINED_ID,
    val shopListId: Int = UNDEFINED_ID
) : Parcelable {
    companion object{
        const val UNDEFINED_ID = 0
    }
}
