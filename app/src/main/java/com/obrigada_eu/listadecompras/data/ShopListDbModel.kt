package com.obrigada_eu.listadecompras.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "shop_lists")
data class ShopListDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "shop_list_id")
    val id: Int,
    val name: String
)

data class ShopListWithShopItemsDbModel(
    @Embedded val shopListDbModel: ShopListDbModel,
    @Relation(
        parentColumn = "shop_list_id",
        entityColumn = "shop_list_id"
    )
    val shopList: List<ShopItemDbModel>
)