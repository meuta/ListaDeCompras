package com.obrigada_eu.listadecompras.data

import com.obrigada_eu.listadecompras.domain.ShopItem
import com.obrigada_eu.listadecompras.domain.ShopListRepository
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
            }
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        dbModel.mOrder = (shopListDao.getLargestOrder() ?: 0) + 1
        shopListDao.addShopItem(dbModel)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        val item = shopListDao.getShopItem(shopItem.id)
        dbModel.mOrder = item?.mOrder ?: ((shopListDao.getLargestOrder() ?: 0) + 1)
        shopListDao.addShopItem(dbModel)
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override suspend fun getShopItem(itemId: Int): ShopItem? {
        val dbModel = shopListDao.getShopItem(itemId)
        return if (dbModel != null) {
            mapper.mapDbModelToEntity(dbModel)
        } else null
    }

    override suspend fun dragShopItem(from: Int, to: Int) {

        if (from < to) {
            val lastOrder = shopListDbModel[to].mOrder
            for (i in to downTo from + 1) {
                shopListDbModel[i].mOrder = shopListDbModel[i-1].mOrder
            }
            shopListDbModel[from].mOrder = lastOrder

        } else if (from > to) {
            val lastOrder = shopListDbModel[to].mOrder
            for (i in to until from) {
                shopListDbModel[i].mOrder = shopListDbModel[i+1].mOrder
            }
            shopListDbModel[from].mOrder = lastOrder
        }

        shopListDao.update(shopListDbModel)
    }
}