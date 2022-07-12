package com.example.listadecompras.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository

object ShopListRepositoryImpl: ShopListRepository {

//    private val shopList = mutableListOf<ShopItem>()
    private val shopList = sortedSetOf<ShopItem>({ o1, o2 -> o1.id.compareTo(o2.id) })    // Creating a sorted list

    private var autoIncrementId = 0

    private val shopListLD = MutableLiveData<List<ShopItem>>()

    init {
        for (i in 0 until 10){
            val item = ShopItem("Name $i", 0.0, true)
            addItemToShopList(item)
        }
    }


    override fun getShopList(): LiveData<List<ShopItem>> {
//        return shopList.toList()        // For crating a copy of collection
        return shopListLD
    }

    override fun addItemToShopList(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) {
            shopItem.id = autoIncrementId++
        }
        shopList.add(shopItem)
        updateList()
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItemById(shopItem.id)
        shopList.remove(oldElement)
        addItemToShopList(shopItem)
        }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
        updateList()
    }

    override fun getShopItemById(itemId: Int): ShopItem {
        return shopList.find {
            it.id == itemId
        } ?: throw RuntimeException("Element with ID $itemId not found")
    }

    private fun updateList() {
        shopListLD.value = shopList.toList()            // .toList() means copy of list
    }
}