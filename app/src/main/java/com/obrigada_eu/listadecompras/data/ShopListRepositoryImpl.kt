package com.obrigada_eu.listadecompras.data

import android.util.Log
import com.obrigada_eu.listadecompras.domain.ShopItem
import com.obrigada_eu.listadecompras.domain.ShopList
import com.obrigada_eu.listadecompras.domain.ShopListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper
) : ShopListRepository {

    private lateinit var shopListDbModel: MutableList<ShopItemDbModel>


    override fun getShopList(listId: Int): Flow<List<ShopItem>> {
        Log.d("RepositoryImpl", "getShopList() ")
        return shopListDao.getShopList(listId).map {
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
        Log.d("RepositoryImpl", "dragShopItem()     ${shopListDbModel.map{it.name to it.position}}")
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
        Log.d("RepositoryImpl", "dragShopItem() FIN ${shopListDbModel.map{it.name to it.position}}")
        shopListDao.updateList(shopListDbModel)
    }

    override suspend fun addShopList(shopListName: String) {
        val dbModel = ShopListDbModel(id = -2, name = shopListName)
        shopListDao.insertShopList(dbModel)
        Log.d("RepositoryImpl", "addShopList() List.name = $shopListName")
    }


    override fun getAllListsWithItems(): Flow<List<ShopList>> {
        Log.d("RepositoryImpl", "getAllLists() ")
        return shopListDao.getShopListsWithShopItems().map { list ->
            list.map { mapper.mapShopListWithItemsDbModelToEntityList(it) }
                .also { Log.d("RepositoryImpl", "getAllListsWithItems() List<ShopList> =\n $it") }
        }
    }
}