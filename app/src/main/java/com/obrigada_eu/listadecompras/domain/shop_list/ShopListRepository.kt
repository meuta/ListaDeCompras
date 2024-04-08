package com.obrigada_eu.listadecompras.domain.shop_list

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ShopListRepository {

    suspend fun addShopList(shopListName: String, enabled: Boolean = true)

    fun getAllListsWithoutItemsFlow(): Flow<List<ShopList>>

    suspend fun getAllListsWithoutItems(): List<ShopList>

    fun getShopListName(listId: Int): Flow<String>

    suspend fun deleteShopList(id: Int)

    suspend fun updateListEnabled(shopList: ShopList)

    suspend fun updateListName(id: Int, name: String)

    fun getShopListWithItems(listId: Int): Flow<ShopListWithItems>

    suspend fun setCurrentListId(listId: Int)

    fun getCurrentListId(): StateFlow<Int>

    suspend fun exportListToTxt(listId: Int)

    fun shareTxtList(listId: Int): Flow<Intent>

    suspend fun loadFilesList(): List<String>?

    suspend fun saveListToDb(shopListWithItems: ShopListWithItems): Boolean

    fun getListFromTxtFile(fileName: String, uri: Uri? = null): ShopListWithItems?

    suspend fun undoDelete()

    suspend fun dragShopList(from: Int, to: Int)

}