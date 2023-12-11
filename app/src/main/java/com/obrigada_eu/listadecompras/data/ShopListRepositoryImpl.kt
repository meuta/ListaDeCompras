package com.obrigada_eu.listadecompras.data

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
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper
) : ShopListRepository {

    private lateinit var shopListDbModel: MutableList<ShopItemDbModel>

    override fun getShopList(): Flow<List<ShopItem>> {

        return shopListDao.getShopList().map {
            shopListDbModel = it.toMutableList()
            mapper.mapListDbModelToEntity(it)
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        dbModel.position = (shopListDao.getLargestOrder() ?: -1) + 1
        shopListDao.addShopItem(dbModel)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        val item = shopListDao.getShopItem(shopItem.id)
        dbModel.position = item?.position ?: ((shopListDao.getLargestOrder() ?: -1) + 1)
        shopListDao.addShopItem(dbModel)
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        val itemIndex = shopListDbModel.indexOfFirst { it.id == shopItem.id }
        shopListDbModel.removeAt(itemIndex)
        shopListDao.deleteShopItem(shopItem.id)
        shopListDbModel.drop(itemIndex).forEach{
            it.position--
            shopListDao.updateItemOrder(ItemOrder(it.id, it.position) )
        }
    }

    override suspend fun getShopItem(itemId: Int): ShopItem? {
        val dbModel = shopListDao.getShopItem(itemId)
        return if (dbModel != null) {
            mapper.mapDbModelToEntity(dbModel)
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
        shopListDbModel
            .slice(min(from, to) .. max(from, to))
            .sortedBy { it.position }

        shopListDao.updateList(shopListDbModel)
    }
}