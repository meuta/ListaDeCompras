package com.example.listadecompras.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository
import kotlin.random.Random

//object ShopListRepositoryImpl: ShopListRepository {
class ShopListRepositoryImpl(application: Application): ShopListRepository {

//    private val shopList = sortedSetOf<ShopItem>({ o1, o2 -> o1.id.compareTo(o2.id) })    // Creating a sorted list
//    private var autoIncrementId = 0
//
//    private val shopListLD = MutableLiveData<List<ShopItem>>()
//
//    init {
//        for (i in 0 until 10){
//            val item = ShopItem("Name $i", 0.0, Random.nextBoolean())
//            addShopItem(item)
//        }
//    }

    private val shopListDao = AppDatabase.getInstance(application).shopListDao()

    private val mapper = ShopListMapper()

    override fun getShopList(): LiveData<List<ShopItem>> {
//        return shopListLD
        return shopListDao.getShopList()

    }

    override fun addShopItem(shopItem: ShopItem) {
//        if (shopItem.id == ShopItem.UNDEFINED_ID) {
//            shopItem.id = autoIncrementId++         //++ executing after assigning to the shopItem.id
//            Log.d("autoIncrementId = ", autoIncrementId.toString())
//        }
//        shopList.add(shopItem)
//        updateList()
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override fun editShopItem(shopItem: ShopItem) {
//        val oldElement = getShopItem(shopItem.id)
//        shopList.remove(oldElement)
//        addShopItem(shopItem)
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override fun deleteShopItem(shopItem: ShopItem) {
//        shopList.remove(shopItem)
//        updateList()
        shopListDao.deleteShopItem(shopItem.id)
    }

    override fun getShopItem(itemId: Int): ShopItem {
//        return shopList.find {
//            it.id == itemId
//        } ?: throw RuntimeException("Element with ID $itemId not found")
        val dbModel = shopListDao.getShopItem(itemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

//    private fun updateList() {
//        shopListLD.value = shopList.toList()            // .toList() means copy of list
//    }
}