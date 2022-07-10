package com.example.listadecompras.data

import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository

object ShopListRepositoryImpl: ShopListRepository {

    private val shopList = mutableListOf<ShopItem>()

    private var autoIncrementId = 0

    init {
        for (i in 0 until 10){
            val item = ShopItem("Name $i", 0.0, true)
            addItemToShopList(item)
        }
    }


    override fun getShopList(): List<ShopItem> {
        return shopList.toList()        // For crating a copy of collection
    }

    override fun addItemToShopList(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) {
            shopItem.id = autoIncrementId++
        }
        shopList.add(shopItem)
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItemById(shopItem.id)
        shopList.remove(oldElement)
        addItemToShopList(shopItem)
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
    }

    override fun getShopItemById(itemId: Int): ShopItem {
        return shopList.find {
            it.id == itemId
        } ?: throw RuntimeException("Element with ID $itemId not found")
    }
}