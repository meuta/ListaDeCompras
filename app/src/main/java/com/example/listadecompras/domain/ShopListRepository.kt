package com.example.listadecompras.domain

interface ShopListRepository {

    fun getShopList(): List<ShopItem>

    fun addItemToShopList(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)

    fun deleteShopItem(shopItem: ShopItem)

    fun getShopItemById(itemId: Int): ShopItem

}