package com.example.listadecompras.data

import android.util.Log
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper
    ): ShopListRepository {

    private lateinit var shopListDbModel: List<ShopItemBbModel>

    override fun getShopList(): Flow<List<ShopItem>> {

        return shopListDao.getShopList().map {
            mapper.mapListDbModelToEntity(it).apply {
                shopListDbModel = it
                for (i in it ) {
                    Log.d("shopListDbModel", "${i.name} ${i.mOrder}")
                }
            }
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        dbModel.mOrder = (shopListDao.getLargestOrder() ?: 0) + 1
        shopListDao.addShopItem(dbModel)
        Log.d("addShopItem", "${dbModel.mOrder}")
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
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

    override suspend fun dragShopItem(from: Int, to: Int) {

        if (from < to) {
            Log.d("dragShopItem", "$from $to")
            val lastOrder = shopListDbModel[to].mOrder
            for (i in to downTo from + 1) {
                Log.d("dragShopItem", "name ${shopListDbModel[i].name}, order ${shopListDbModel[i].mOrder}")
                Log.d("dragShopItem", "name ${shopListDbModel[i-1].name}, order ${shopListDbModel[i-1].mOrder}")
                shopListDbModel[i].mOrder = shopListDbModel[i-1].mOrder

            }
            shopListDbModel[from].mOrder = lastOrder


        } else if (from > to) {
            Log.d("dragShopItem", "$from $to")
            val lastOrder = shopListDbModel[to].mOrder
            for (i in to until from) {
                Log.d("dragShopItem", "name ${shopListDbModel[i].name}, order ${shopListDbModel[i].mOrder}")
                Log.d("dragShopItem", "name ${shopListDbModel[i+1].name}, order ${shopListDbModel[i+1].mOrder}")
                shopListDbModel[i].mOrder = shopListDbModel[i+1].mOrder
            }
            shopListDbModel[from].mOrder = lastOrder
        }

        shopListDao.update(shopListDbModel)
        Log.d("dragShopItem", "shopListDao.update")

    }
}