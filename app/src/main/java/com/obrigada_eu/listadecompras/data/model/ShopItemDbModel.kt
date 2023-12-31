package com.obrigada_eu.listadecompras.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "shop_items",
    foreignKeys = [
        ForeignKey(
            entity = ShopListDbModel::class,
            parentColumns = ["id"],
            childColumns = ["shop_list_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class ShopItemDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "shop_item_id")
    val id: Int,
    val name: String,
    val count: Double,
    val enabled: Boolean,
    @ColumnInfo(name = "shop_item_order")
    var position: Int = -1,
    @ColumnInfo(name = "shop_list_id")
    val shopListId: Int
)

data class ItemOrder (
    @ColumnInfo(name = "shop_item_id")
    val id: Int,
    @ColumnInfo(name = "shop_item_order")
    var position: Int
)