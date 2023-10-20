package com.obrigada_eu.listadecompras.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItemBbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val count: Double,
    val enabled: Boolean,
    @ColumnInfo(name = "shop_item_order")
    var mOrder: Int = 0
)