package com.obrigada_eu.listadecompras.domain

import kotlinx.coroutines.flow.Flow

interface ShopListRepository {

    fun getShopList(listId: Int): Flow<List<ShopItem>>

    suspend fun addShopItem(shopItem: ShopItem)

    suspend fun editShopItem(shopItem: ShopItem)

    suspend fun deleteShopItem(shopItem: ShopItem)

    suspend fun getShopItem(itemId: Int): ShopItem?

    suspend fun dragShopItem(from: Int, to: Int)


    suspend fun addShopList(shopListName: String)

    fun getAllListsWithItems(): Flow<List<ShopList>>

    suspend fun getShopListName(listId: Int): String
}