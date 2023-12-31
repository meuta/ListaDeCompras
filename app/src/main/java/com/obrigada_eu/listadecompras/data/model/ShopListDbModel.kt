package com.obrigada_eu.listadecompras.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "shop_lists")
data class ShopListDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "shop_list_name")
    val name: String,
    val enabled: Boolean
)

data class ListName(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "shop_list_name")
    var name: String
)

data class ShopListWithShopItemsDbModel(
    @Embedded val shopListDbModel: ShopListDbModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "shop_list_id"
    )
    val shopList: List<ShopItemDbModel>
)