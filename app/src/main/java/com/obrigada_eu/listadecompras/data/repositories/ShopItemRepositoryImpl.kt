package com.obrigada_eu.listadecompras.data.repositories

import com.obrigada_eu.listadecompras.data.database.ShopItemDao
import com.obrigada_eu.listadecompras.data.mapper.ShopListMapper
import com.obrigada_eu.listadecompras.data.model.ShopItemDbModel
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton

class ShopItemRepositoryImpl @Inject constructor(
    private val shopItemDao: ShopItemDao,
    private val mapper: ShopListMapper
) : ShopItemRepository {


    private lateinit var shopListDbModel: MutableList<ShopItemDbModel>
    private var recentlyDeletedShopItemDbModel: ShopItemDbModel? = null

    override fun getShopList(listId: Int): Flow<List<ShopItem>> {
        return shopItemDao.getShopList(listId).map { list ->
//            Log.d("getShopList", "shopList = ${list.map{it.name to it.position}}")
            shopListDbModel = list.toMutableList()
            mapper.mapListDbModelToEntity(list)
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapShopItemEntityToDbModel(shopItem)
        dbModel.position = (shopItemDao.getLargestOrder(shopItem.shopListId) ?: -1) + 1
        shopItemDao.addShopItem(dbModel)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapShopItemEntityToDbModel(shopItem)
        val item = shopItemDao.getShopItem(shopItem.id)
        dbModel.position = item?.position ?: ((shopItemDao.getLargestOrder(shopItem.shopListId) ?: -1) + 1)
        shopItemDao.addShopItem(dbModel)
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        recentlyDeletedShopItemDbModel = shopItemDao.getShopItem(shopItem.id)
        shopItemDao.deleteShopItem(shopItem.id)
//        Log.d("deleteShopItem", " list = ${shopListDbModel.map { it.id }}")
    }

    override suspend fun undoDelete() {
        recentlyDeletedShopItemDbModel?.let {
            shopItemDao.addShopItem(it)
        }
    }

    override suspend fun getShopItem(itemId: Int): ShopItem? {
        val dbModel = shopItemDao.getShopItem(itemId)
        return if (dbModel != null) {
            mapper.mapShopItemDbModelToEntity(dbModel)
        } else null
    }

    override suspend fun dragShopItem(from: Int, to: Int) {

        val lastPosition = shopListDbModel[to].position
        if (from < to) {
            for (i in to downTo from + 1) {
                shopListDbModel[i].position = shopListDbModel[i - 1].position
            }
        } else if (from > to) {
            for (i in to until from) {
                shopListDbModel[i].position = shopListDbModel[i + 1].position
            }
        }
        shopListDbModel[from].position = lastPosition
        shopListDbModel.sortBy { it.position }
        shopItemDao.updateList(shopListDbModel)
    }
}