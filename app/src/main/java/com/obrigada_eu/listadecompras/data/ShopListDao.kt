package com.obrigada_eu.listadecompras.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopListDao {

    @Query("SELECT * FROM shop_items ORDER BY shop_item_order ASC")
    fun getShopList(): Flow<List<ShopItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)        //If we add an item with existed ID, it will be replace, so we can use it also in the edit case
    suspend fun addShopItem(shopItemBbModel: ShopItemDbModel)

    @Query("DELETE FROM shop_items WHERE shop_item_id=:shopItemId")
    suspend fun deleteShopItem(shopItemId: Int)

    @Query("SELECT * FROM shop_items WHERE shop_item_id=:shopItemId LIMIT 1")
    suspend fun getShopItem(shopItemId: Int): ShopItemDbModel?
    @Query("SELECT * FROM shop_items WHERE shop_item_order=:order LIMIT 1")
    suspend fun getShopItemByOrder(order: Int): ShopItemDbModel?

    @Query("SELECT MAX(shop_item_order) FROM shop_items")
    suspend fun getLargestOrder(): Int?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateList(shopListBbModel: List<ShopItemDbModel>)

    @Update(entity = ShopItemDbModel::class)
    suspend fun updateItemOrder(order: ItemOrder)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShopList(shopListDbModel: ShopListDbModel)

    @Transaction
    @Query("SELECT * FROM shop_lists")
    fun getShopListsWithShopItems(): List<ShopListWithShopItemsDbModel>

}