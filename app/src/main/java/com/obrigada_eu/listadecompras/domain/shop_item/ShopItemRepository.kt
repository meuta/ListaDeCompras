package com.obrigada_eu.listadecompras.domain.shop_item

import kotlinx.coroutines.flow.Flow

interface ShopItemRepository {


    fun getShopList(listId: Int): Flow<List<ShopItem>>

    suspend fun addShopItem(shopItem: ShopItem)

    suspend fun editShopItem(shopItem: ShopItem)

    suspend fun deleteShopItem(shopItem: ShopItem)

    suspend fun getShopItem(itemId: Int): ShopItem?

    suspend fun dragShopItem(from: Int, to: Int)


}