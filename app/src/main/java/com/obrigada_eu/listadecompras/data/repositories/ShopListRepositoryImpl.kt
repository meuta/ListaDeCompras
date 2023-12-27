package com.obrigada_eu.listadecompras.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.obrigada_eu.listadecompras.data.model.ListName
import com.obrigada_eu.listadecompras.data.model.ShopListDbModel
import com.obrigada_eu.listadecompras.data.mapper.ShopListMapper
import com.obrigada_eu.listadecompras.data.datastore.ShopListPreferences
import com.obrigada_eu.listadecompras.data.database.ShopListDao
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListWithItems
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper,
    private val shopListPreferences: DataStore<Preferences>

) : ShopListRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun addShopList(shopListName: String) {
        val dbModel = ShopListDbModel(name = shopListName, id = ShopList.UNDEFINED_ID)
        shopListDao.insertShopList(dbModel)
    }


    override fun getAllListsWithoutItems(): Flow<List<ShopList>> {
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


    override fun getShopListWithItems(listId: Int): Flow<ShopListWithItems> {
        return shopListDao.getShopListWithItems(listId).map { list ->
            Log.d("getShopListWithItems", "shopList = $list")
            val newList = list.shopList.sortedBy { it.position }.toMutableList()
            mapper.mapShopListWithItemsDbModelToShopList(list.copy(shopList = newList))
        }
    }


    private val shopListIdFlow: StateFlow<Int> = shopListPreferences.data
        .catch { exception ->
            /*
                 * dataStore.data throws an IOException when an error
                 * is encountered when reading data
                 */
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            // Get our name value, defaulting to "" if not set
            preferences[KEY_LIST_ID] ?: ShopList.UNDEFINED_ID
        }.stateIn(scope, SharingStarted.Eagerly, ShopList.UNDEFINED_ID)



    override suspend fun setCurrentListId(listId: Int){
        shopListPreferences.edit { preferences ->
            preferences[KEY_LIST_ID] = listId
        }
    }


    override fun getCurrentListId(): StateFlow<Int> {
        return shopListIdFlow
    }


    suspend fun fetchInitialPreferences() =
        mapLoopPreferences(shopListPreferences.data.first().toPreferences())

    private fun mapLoopPreferences(preferences: Preferences): ShopListPreferences {
        val listId = preferences[KEY_LIST_ID] ?: ShopList.UNDEFINED_ID

        return ShopListPreferences(listId)
    }

    private companion object {

        val KEY_LIST_ID = intPreferencesKey(name = "listId")

    }
}