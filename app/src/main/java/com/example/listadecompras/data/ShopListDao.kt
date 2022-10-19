package com.example.listadecompras.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShopListDao {

    @Query("SELECT * FROM shop_items")
    fun getShopList(): LiveData<List<ShopItemBbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)        //If we add an item with existed ID, it will be replace, so we can use it also in the edit case
    fun addShopItem(shopItemBbModel: ShopItemBbModel)

    @Query("DELETE FROM shop_items WHERE id=:shopItemId")
    fun deleteShopItem(shopItemId: Int)

    @Query("SELECT * FROM shop_items WHERE id=:shopItemId LIMIT 1")
    suspend fun getShopItem(shopItemId: Int): ShopItemBbModel
}