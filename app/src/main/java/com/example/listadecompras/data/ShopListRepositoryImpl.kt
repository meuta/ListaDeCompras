package com.example.listadecompras.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository
import kotlin.random.Random

class ShopListRepositoryImpl(application: Application): ShopListRepository {

    private val shopListDao = AppDatabase.getInstance(application).shopListDao()

    private val mapper = ShopListMapper()

    override fun getShopList(): LiveData<List<ShopItem>> {
//        return shopListLD
//        return shopListDao.getShopList()

//        return MediatorLiveData<List<ShopItem>>().apply {
//            addSource(shopListDao.getShopList()){
//                value = mapper.mapListDbModelToEntity(it)
//            }
//        }

        return Transformations.map(shopListDao.getShopList()) {
            mapper.mapListDbModelToEntity(it)
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override suspend fun getShopItem(itemId: Int): ShopItem {
        val dbModel = shopListDao.getShopItem(itemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

}