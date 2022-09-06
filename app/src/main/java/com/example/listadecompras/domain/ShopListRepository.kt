package com.example.listadecompras.domain

import androidx.lifecycle.LiveData

interface ShopListRepository {

    fun getShopList(): LiveData<List<ShopItem>>

    fun addShopItem(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)

    fun deleteShopItem(shopItem: ShopItem)

    fun getShopItem(itemId: Int): ShopItem

}