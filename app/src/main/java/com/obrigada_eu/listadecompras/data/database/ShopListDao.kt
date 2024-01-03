package com.obrigada_eu.listadecompras.data.database

import androidx.room.*
import com.obrigada_eu.listadecompras.data.model.ListEnabled
import com.obrigada_eu.listadecompras.data.model.ListName
import com.obrigada_eu.listadecompras.data.model.ShopListDbModel
import com.obrigada_eu.listadecompras.data.model.ShopListWithShopItemsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopList(shopListDbModel: ShopListDbModel)

    @Query("SELECT * FROM shop_lists")
    fun getShopListsWithoutShopItems(): Flow<List<ShopListDbModel>>

    @Query("SELECT shop_list_name FROM shop_lists WHERE id=:shopListId LIMIT 1")
    suspend fun getShopListName(shopListId: Int): String?

    @Query("SELECT id FROM shop_lists WHERE shop_list_name=:shopListName LIMIT 1")
    suspend fun getShopListId(shopListName: String): Int?

    @Transaction
    @Query("DELETE FROM shop_lists WHERE id=:shopListId")
    suspend fun deleteShopList(shopListId: Int)

    @Update(entity = ShopListDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateListName(name: ListName)

   @Update(entity = ShopListDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateListEnabled(enabled: ListEnabled)

    @Query("SELECT * FROM shop_lists WHERE id=:shopListId")
    fun getShopListWithItemsFlow(shopListId: Int): Flow<ShopListWithShopItemsDbModel>

    @Query("SELECT * FROM shop_lists WHERE id=:shopListId")
    fun getShopListWithItems(shopListId: Int): ShopListWithShopItemsDbModel

}