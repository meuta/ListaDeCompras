package com.example.listadecompras.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository
import kotlin.random.Random

object ShopListRepositoryImpl: ShopListRepository {

//    private val shopList = mutableListOf<ShopItem>()
    private val shopList = sortedSetOf<ShopItem>({ o1, o2 -> o1.id.compareTo(o2.id) })    // Creating a sorted list
    private val shopList1 = mutableListOf<ShopItem>()
    private var autoIncrementId = 0

    private val shopListLD = MutableLiveData<List<ShopItem>>()

    init {
        for (i in 0 until 100){
            val item = ShopItem("Name $i", 0.0, Random.nextBoolean())
            addShopItem(item)
        }
    }


    override fun getShopList(): LiveData<List<ShopItem>> {
//        return shopList.toList()        // For crating a copy of collection
        return shopListLD
    }

    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) {
            shopItem.id = autoIncrementId++         //++ executing after assigning to the shopItem.id
        }
        shopList.add(shopItem)
        updateList()
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItem(shopItem.id)
        shopList.remove(oldElement)
        addShopItem(shopItem)
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
        updateList()
    }

    override fun getShopItem(itemId: Int): ShopItem {
        return shopList.find {
            it.id == itemId
        } ?: throw RuntimeException("Element with ID $itemId not found")
    }

    private fun updateList() {
        shopListLD.value = shopList.toList()            // .toList() means copy of list
    }
}