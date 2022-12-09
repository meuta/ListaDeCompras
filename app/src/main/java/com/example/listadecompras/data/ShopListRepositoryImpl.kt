package com.example.listadecompras.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository
import java.util.*
import javax.inject.Inject


class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper
    ): ShopListRepository {

    private lateinit var shopListDbModel: List<ShopItemBbModel>

    override fun getShopList(): LiveData<List<ShopItem>> {

        return Transformations.map(shopListDao.getShopList()) {
//            mapper.mapListDbModelToEntity(it)
            mapper.mapListDbModelToEntity(it).apply {
                shopListDbModel = it
            }
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
//        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        dbModel.mOrder = (shopListDao.getLargestOrder() ?: 0) + 1
        shopListDao.addShopItem(dbModel)
        Log.d("addShopItem", "${dbModel.mOrder}")
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
//        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        val item = shopListDao.getShopItem(shopItem.id)
        dbModel.mOrder = item.mOrder
        shopListDao.addShopItem(dbModel)

    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override suspend fun getShopItem(itemId: Int): ShopItem {
        val dbModel = shopListDao.getShopItem(itemId)
        Log.d("getShopItem", "name = ${dbModel.name}, mOrder = ${dbModel.mOrder}")
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun dragShopItem(shopItem: ShopItem, from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) {
                Log.d("dragShopItem", "$from $to")
                val order1: Int = shopListDbModel[i].mOrder
                val order2: Int = shopListDbModel[i + 1].mOrder
                Log.d("dragShopItem", "name ${shopListDbModel[i].name}, order $order1")
                Log.d("dragShopItem", "name ${shopListDbModel[i + 1].name}, order $order2")
                shopListDbModel[i].mOrder = order2
                shopListDbModel[i + 1].mOrder = order1
                Log.d("dragShopItem", "name ${shopListDbModel[i].name}, order ${shopListDbModel[i].mOrder}")
                Log.d("dragShopItem", "name ${shopListDbModel[i + 1].name}, order ${shopListDbModel[i + 1].mOrder}")

            }

        } else {
            for (i in from downTo to + 1) {
                Collections.swap(shopListDbModel, i, i - 1)
                val order1: Int = shopListDbModel[i].mOrder
                val order2: Int = shopListDbModel[i - 1].mOrder
                shopListDbModel[i].mOrder = order2
                shopListDbModel[i - 1].mOrder = order1
            }
        }

        shopListDao.update(shopListDbModel)

    }
}