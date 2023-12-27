package com.obrigada_eu.listadecompras.data.repositories

import android.util.Log
import com.obrigada_eu.listadecompras.data.model.ItemOrder
import com.obrigada_eu.listadecompras.data.model.ShopItemDbModel
import com.obrigada_eu.listadecompras.data.mapper.ShopListMapper
import com.obrigada_eu.listadecompras.data.database.ShopItemDao
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


    override fun getShopList(listId: Int): Flow<List<ShopItem>> {
        return shopItemDao.getShopList(listId).map {
            Log.d("getShopList", "shopList = $it")
            shopListDbModel = it.toMutableList()
            mapper.mapListDbModelToEntity(it)
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
        val itemIndex = shopListDbModel.indexOfFirst { it.id == shopItem.id }
        shopListDbModel.removeAt(itemIndex)
        shopItemDao.deleteShopItem(shopItem.id)
        shopListDbModel.drop(itemIndex).forEach {
            it.position--
            shopItemDao.updateItemOrder(ItemOrder(it.id, it.position))
        }
        Log.d("deleteShopItem", " list = ${shopListDbModel.map { it.id }}")
    }

    override suspend fun getShopItem(itemId: Int): ShopItem? {
        val dbModel = shopItemDao.getShopItem(itemId)
        return if (dbModel != null) {
            mapper.mapShopItemDbModelToEntity(dbModel)
        } else null
    }

    override suspend fun dragShopItem(from: Int, to: Int) {
        shopListDbModel[from].position = to
        if (from < to) {
            shopListDbModel
                .slice(to downTo from + 1)
                .forEach { it.position-- }
        } else if (from > to) {
            shopListDbModel
                .slice(to until from)
                .forEach { it.position++ }
        }
        shopListDbModel.sortBy { it.position }
        shopItemDao.updateList(shopListDbModel)
    }

}