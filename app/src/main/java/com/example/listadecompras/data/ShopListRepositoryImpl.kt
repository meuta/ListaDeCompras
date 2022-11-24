package com.example.listadecompras.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository
import javax.inject.Inject

//class ShopListRepositoryImpl(application: Application): ShopListRepository {

class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper
    ): ShopListRepository {

//    private val shopListDao = AppDatabase.getInstance(application).shopListDao()
//    private val mapper = ShopListMapper()

    override fun getShopList(): LiveData<List<ShopItem>> {

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