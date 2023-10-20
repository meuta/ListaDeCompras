package com.obrigada_eu.listadecompras.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopListDao {

    @Query("SELECT * FROM shop_items ORDER BY shop_item_order ASC")
    fun getShopList(): Flow<List<ShopItemBbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)        //If we add an item with existed ID, it will be replace, so we can use it also in the edit case
    suspend fun addShopItem(shopItemBbModel: ShopItemBbModel)

    @Query("DELETE FROM shop_items WHERE id=:shopItemId")
    suspend fun deleteShopItem(shopItemId: Int)

    @Query("SELECT * FROM shop_items WHERE id=:shopItemId LIMIT 1")
    suspend fun getShopItem(shopItemId: Int): ShopItemBbModel?

    @Query("SELECT MAX(shop_item_order) FROM shop_items")
    suspend fun getLargestOrder(): Int?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(shopListBbModel: List<ShopItemBbModel?>?)
}