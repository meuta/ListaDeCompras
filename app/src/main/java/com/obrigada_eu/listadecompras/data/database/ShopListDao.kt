package com.obrigada_eu.listadecompras.data.database


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopListDao {

    @Query("SELECT * FROM shop_items WHERE shop_list_id=:shopListId ORDER BY shop_item_order ASC")
    fun getShopList(shopListId: Int): Flow<List<ShopItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)        //If we add an item with existed ID, it will be replace, so we can use it also in the edit case
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


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopList(shopListDbModel: ShopListDbModel)

    @Query("SELECT * FROM shop_lists")
    fun getShopListsWithoutShopItems(): Flow<List<ShopListDbModel>>

    @Query("SELECT shop_list_name FROM shop_lists WHERE id=:shopListId LIMIT 1")
    suspend fun getShopListName(shopListId: Int): String?

    @Transaction
    @Query("DELETE FROM shop_lists WHERE id=:shopListId")
    suspend fun deleteShopList(shopListId: Int)

    @Update(entity = ShopListDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateListName(name: ListName)



    @Query("SELECT id FROM shop_lists WHERE shop_list_name=:shopListName LIMIT 1")
    suspend fun getShopListId(shopListName: String): Int?

    @Update(entity = ShopListDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateListEnabled(enabled: ListEnabled)

    @Query("SELECT * FROM shop_lists WHERE id=:shopListId")
    fun getShopListWithItemsFlow(shopListId: Int): Flow<ShopListWithShopItemsDbModel>

    @Query("SELECT * FROM shop_lists WHERE id=:shopListId")
    fun getShopListWithItems(shopListId: Int): ShopListWithShopItemsDbModel
}
