package com.obrigada_eu.listadecompras.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopItemDao {

//    @Query("SELECT * FROM shop_items ORDER BY shop_item_order ASC")
//    fun getShopList(): Flow<List<ShopItemDbModel>>
    @Query("SELECT * FROM shop_items WHERE shop_list_id=:shopListId ORDER BY shop_item_order ASC")
    fun getShopList(shopListId: Int): Flow<List<ShopItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)        //If we add an item with existed ID, it will be replace, so we can use it also in the edit case
    suspend fun addShopItem(shopItemBbModel: ShopItemDbModel)

//    @Query("DELETE FROM shop_items WHERE id=:shopItemId")
    @Query("DELETE FROM shop_items WHERE shop_item_id=:shopItemId")
    suspend fun deleteShopItem(shopItemId: Int)

//    @Query("SELECT * FROM shop_items WHERE id=:shopItemId LIMIT 1")
    @Query("SELECT * FROM shop_items WHERE shop_item_id=:shopItemId LIMIT 1")
    suspend fun getShopItem(shopItemId: Int): ShopItemDbModel?
    @Query("SELECT * FROM shop_items WHERE shop_item_order=:order LIMIT 1")
    suspend fun getShopItemByOrder(order: Int): ShopItemDbModel?

    @Query("SELECT MAX(shop_item_order) FROM shop_items")
    suspend fun getLargestOrder(): Int?

//    @Update(onConflict = OnConflictStrategy.REPLACE)
    @Update(entity = ShopItemDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateList(shopListBbModel: List<ShopItemDbModel>)

//    @Update(entity = ShopItemDbModel::class)
//    suspend fun updateItemOrder(order: ItemOrder)

    @Query("SELECT MAX(shop_item_order) FROM shop_items WHERE shop_list_id=:listId")
    suspend fun getLargestOrder(listId: Int): Int?

}