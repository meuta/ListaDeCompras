package com.obrigada_eu.listadecompras.domain.shop_list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ShopListRepository {

    suspend fun addShopList(shopListName: String)

    fun getAllListsWithoutItems(): Flow<List<ShopList>>

    suspend fun getShopListName(listId: Int): String

    suspend fun deleteShopList(id: Int)

    suspend fun updateListEnabled(shopList: ShopList)

    suspend fun updateListName(id: Int, name: String)

    fun getShopListWithItems(listId: Int): Flow<ShopListWithItems>

    suspend fun setCurrentListId(listId: Int)

    fun getCurrentListId(): StateFlow<Int>

    suspend fun exportListToTxt(listId: Int)

    suspend fun loadTxtList(listName: String)

    suspend fun undoDelete()

    suspend fun dragShopList(from: Int, to: Int)
}