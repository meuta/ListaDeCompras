package com.obrigada_eu.listadecompras.data

import android.util.Log
import com.obrigada_eu.listadecompras.data.database.ShopItemDao
import com.obrigada_eu.listadecompras.data.database.ShopItemDbModel
import com.obrigada_eu.listadecompras.domain.ShopItem
import com.obrigada_eu.listadecompras.domain.ShopListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Integer.max
import java.lang.Integer.min
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListRepositoryImpl @Inject constructor(
    private val shopItemDao: ShopItemDao,
    private val mapper: ShopListMapper
) : ShopListRepository {

    private lateinit var shopListDbModel: MutableList<ShopItemDbModel>

    private var recentlyDeletedShopItem: ShopItem? = null
    private var recentlyDeletedShopItemPosition: Int? = null

    override fun getShopList(): Flow<List<ShopItem>> {

        return shopItemDao.getShopList(1).map {
            shopListDbModel = it.toMutableList()
            mapper.mapListDbModelToEntity(it)
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
//        val dbModel = mapper.mapEntityToDbModel(shopItem)
        val dbModel = mapper.mapShopItemEntityToDbModel(shopItem)
        dbModel.position = (shopItemDao.getLargestOrder() ?: -1) + 1
        shopItemDao.addShopItem(dbModel)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapShopItemEntityToDbModel(shopItem)
        val item = shopItemDao.getShopItem(shopItem.id)
        dbModel.position = item?.position ?: ((shopItemDao.getLargestOrder() ?: -1) + 1)
        shopItemDao.addShopItem(dbModel)
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        val itemIndex = shopListDbModel.indexOfFirst { it.id == shopItem.id }
//        shopListDbModel.removeAt(itemIndex)
//        shopItemDao.deleteShopItem(shopItem.id)
//        shopListDbModel.drop(itemIndex).forEach{
//            it.position--
//            shopItemDao.updateItemOrder(ItemOrder(it.id, it.position) )
//        }

        recentlyDeletedShopItem = shopItem
        recentlyDeletedShopItemPosition = itemIndex

        shopItemDao.deleteShopItem(shopItem.id)

        Log.d("deleteShopItem", " list = ${shopListDbModel.map { it.id }}")
    }


    override suspend fun undoDelete() {
        recentlyDeletedShopItemPosition?.let {
            recentlyDeletedShopItem?.let { it1 ->
                shopItemDao.addShopItem(mapper.mapShopItemEntityToDbModel(it1).copy(position = it))
            }
        }
    }


    override suspend fun getShopItem(itemId: Int): ShopItem? {
        val dbModel = shopItemDao.getShopItem(itemId)
        return if (dbModel != null) {
            mapper.mapShopItemDbModelToEntity(dbModel)
        } else null
    }

    override suspend fun dragShopItem(from: Int, to: Int) {
//        shopListDbModel[from].position = to
        shopListDbModel[from].position = shopListDbModel[to].position
        if (from < to) {
            shopListDbModel
                .slice(to downTo from + 1)
                .forEach { it.position-- }
        } else if (from > to) {
            shopListDbModel
                .slice(to until from)
                .forEach { it.position++ }
        }
        shopListDbModel
            .slice(min(from, to) .. max(from, to))
            .sortedBy { it.position }

        shopItemDao.updateList(shopListDbModel)
    }
}