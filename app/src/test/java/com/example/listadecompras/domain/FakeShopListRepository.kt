package com.example.listadecompras.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class FakeShopListRepository : ShopListRepository {

    private val shopList = mutableListOf<ShopItem>()

//    private val _shopListLD = MutableLiveData<List<ShopItem>>()
//    val shopListLD: LiveData<List<ShopItem>>
//        get() = _shopListLD

//    override fun getShopList(): LiveData<List<ShopItem>> {
    override fun getShopList(): Flow<List<ShopItem>> {
//        _shopListLD.value = shopList
        return flow { emit(shopList) }
    }

    override suspend fun getShopItem(itemId: Int): ShopItem {
        return shopList.find { it.id == itemId } ?: ShopItem("", 0.0, false)
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        shopList.add(shopItem)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        val item = shopList.find { it.id == shopItem.id }
        item?.let {
            val index = shopList.indexOf(it)
            shopList[index] = shopItem
        }
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
    }



    override suspend fun dragShopItem(from: Int, to: Int) {
//        val item = shopList[from]

        if (from < to) {
            for (i in from until to) {
                Collections.swap(shopList, i, i+1)
//                shopList[i] = shopList[i+1]
            }

        } else if (from > to) {
            for (i in from downTo to+1) {
                Collections.swap(shopList, i, i-1)
//                shopList[i] = shopList[i-1]
            }
        }
//        shopList[to] = item

    }
}