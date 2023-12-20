package com.obrigada_eu.listadecompras.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ShopListRepository {

    fun getShopList(listId: Int): Flow<List<ShopItem>>

    suspend fun addShopItem(shopItem: ShopItem)

    suspend fun editShopItem(shopItem: ShopItem)

    suspend fun deleteShopItem(shopItem: ShopItem)

    suspend fun getShopItem(itemId: Int): ShopItem?

    suspend fun dragShopItem(from: Int, to: Int)


    suspend fun addShopList(shopListName: String)

    fun getAllListsWithoutItems(): Flow<List<ShopListEntity>>

    suspend fun getShopListName(listId: Int): String

    suspend fun deleteShopList(id: Int)

    suspend fun updateListName(id: Int, name: String)

    fun getShopListWithItems(listId: Int): Flow<ShopList>

    suspend fun setCurrentListId(listId: Int)

    fun getCurrentListId(): StateFlow<Int>
}