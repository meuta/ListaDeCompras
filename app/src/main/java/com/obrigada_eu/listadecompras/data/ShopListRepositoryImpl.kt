package com.obrigada_eu.listadecompras.data

import android.util.Log
import com.obrigada_eu.listadecompras.domain.ShopItem
import com.obrigada_eu.listadecompras.domain.ShopList
import com.obrigada_eu.listadecompras.domain.ShopListEntity
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
        return shopListDao.getShopList(listId).map {
            Log.d("getShopList", "shopList = $it")
// here was a problem, solved by the adding parameters to the coroutine collecting the Flow in a viewModel: .asLiveData(scope.coroutineContext, 0)
            shopListDbModel = it.toMutableList()
            mapper.mapListDbModelToEntity(it)
        }
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        dbModel.position = (shopListDao.getLargestOrder(shopItem.shopListId) ?: -1) + 1
        shopListDao.addShopItem(dbModel)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        val dbModel = mapper.mapEntityToDbModel(shopItem)
        val item = shopListDao.getShopItem(shopItem.id)
        dbModel.position = item?.position ?: ((shopListDao.getLargestOrder(shopItem.shopListId) ?: -1) + 1)
        shopListDao.addShopItem(dbModel)
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        val itemIndex = shopListDbModel.indexOfFirst { it.id == shopItem.id }
        shopListDbModel.removeAt(itemIndex)
        shopListDao.deleteShopItem(shopItem.id)
        shopListDbModel.drop(itemIndex).forEach {
            it.position--
            shopListDao.updateItemOrder(ItemOrder(it.id, it.position))
        }
        Log.d("deleteShopItem", " list = ${shopListDbModel.map { it.id }}")
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
        shopListDbModel.sortBy { it.position }
        shopListDao.updateList(shopListDbModel)
    }

    override suspend fun addShopList(shopListName: String) {
        val dbModel = ShopListDbModel(name = shopListName, id = ShopListEntity.UNDEFINED_ID)
        shopListDao.insertShopList(dbModel)
    }


    override fun getAllListsWithoutItems(): Flow<List<ShopListEntity>> {
        return shopListDao.getShopListsWithoutShopItems().map { list ->
            list.map { mapper.mapShopListDbModelToEntity(it) }
        }
    }

    override suspend fun getShopListName(listId: Int): String {
        return shopListDao.getShopListName(listId) ?: ""
    }

    override suspend fun deleteShopList(id: Int) {
        shopListDao.deleteShopList(id)
    }

    override suspend fun updateListName(id: Int, name: String) {
        shopListDao.updateListName(ListName(id, name))
    }


    override fun getShopListWithItems(listId: Int): Flow<ShopList> {
        return shopListDao.getShopListWithItems(listId).map { list ->
            Log.d("getShopListWithItems", "shopList = $list")
            val newList = list.shopList.sortedBy { it.position }.toMutableList()
            mapper.mapShopListWithItemsDbModelToShopList(list.copy(shopList = newList))
        }
    }


}