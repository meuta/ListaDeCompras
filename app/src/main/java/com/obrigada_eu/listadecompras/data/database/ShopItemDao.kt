package com.obrigada_eu.listadecompras.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.obrigada_eu.listadecompras.data.model.ItemOrder
import com.obrigada_eu.listadecompras.data.model.ShopItemDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopItemDao {

    @Query("SELECT * FROM shop_items WHERE shop_list_id=:shopListId ORDER BY shop_item_order ASC")
    fun getShopList(shopListId: Int): Flow<List<ShopItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShopItem(shopItemBbModel: ShopItemDbModel)

    @Query("DELETE FROM shop_items WHERE shop_item_id=:shopItemId")
    suspend fun deleteShopItem(shopItemId: Int)

    @Query("SELECT * FROM shop_items WHERE shop_item_id=:shopItemId LIMIT 1")
    suspend fun getShopItem(shopItemId: Int): ShopItemDbModel?

    @Query("SELECT * FROM shop_items WHERE shop_item_order=:order LIMIT 1")
    suspend fun getShopItemByOrder(order: Int): ShopItemDbModel?

    @Query("SELECT MAX(shop_item_order) FROM shop_items WHERE shop_list_id=:listId")
    suspend fun getLargestOrder(listId: Int): Int?

    @Update(entity = ShopItemDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateList(shopListBbModel: List<ShopItemDbModel>)

    @Update(entity = ShopItemDbModel::class)
    suspend fun updateItemOrder(order: ItemOrder)

}