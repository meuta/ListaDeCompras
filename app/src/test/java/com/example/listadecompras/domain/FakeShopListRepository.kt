package com.example.listadecompras.domain

import kotlinx.coroutines.flow.*
import java.util.*

class FakeShopListRepository : ShopListRepository {

    private val shopList = mutableListOf<ShopItem>()


    override fun getShopList(): Flow<List<ShopItem>> {
        return flow { emit(shopList) }
    }

//    override suspend fun getShopItem(itemId: Int): ShopItem {
    override suspend fun getShopItem(itemId: Int): ShopItem? {
//        return shopList.find { it.id == itemId } ?: ShopItem("", 0.0, false)
        return shopList.find { it.id == itemId }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        val id = shopList.size
        shopList.add(shopItem.copy(id = id))
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        val item = shopList.find { it.id == shopItem.id }
        item?.let {
            val index = shopList.indexOf(it)
            shopList[index] = shopItem
        }
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        val indexStart = shopList.indexOf(shopItem)
        shopList.remove(shopItem)
        for (index in indexStart until shopList.size){
            shopList[index].id--
        }
    }



    override suspend fun dragShopItem(from: Int, to: Int) {

        if (from < to) {
            for (i in from until to) {
                Collections.swap(shopList, i, i+1)
                shopList[i].id = shopList[i+1].id.also { shopList[i+1].id = shopList[i].id }
            }

        } else if (from > to) {
            for (i in from downTo to+1) {
                Collections.swap(shopList, i, i-1)
                shopList[i].id = shopList[i-1].id.also { shopList[i-1].id = shopList[i].id }
            }
        }
    }
}