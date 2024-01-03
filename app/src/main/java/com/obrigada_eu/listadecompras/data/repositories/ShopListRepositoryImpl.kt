package com.obrigada_eu.listadecompras.data.repositories

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.obrigada_eu.listadecompras.data.database.ShopItemDao
import com.obrigada_eu.listadecompras.data.database.ShopListDao
import com.obrigada_eu.listadecompras.data.datastore.ShopListPreferences
import com.obrigada_eu.listadecompras.data.mapper.ShopListMapper
import com.obrigada_eu.listadecompras.data.model.ListEnabled
import com.obrigada_eu.listadecompras.data.model.ListName
import com.obrigada_eu.listadecompras.data.model.ShopListDbModel
import com.obrigada_eu.listadecompras.data.model.ShopListWithShopItemsDbModel
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListRepository
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListWithItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val shopItemDao: ShopItemDao,
    private val mapper: ShopListMapper,
    private val shopListPreferences: DataStore<Preferences>,
    private val context: Context
) : ShopListRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var recentlyDeletedShopListWithShopItemsDbModel: ShopListWithShopItemsDbModel? = null

    override suspend fun addShopList(shopListName: String) {
        val dbModel =
            ShopListDbModel(name = shopListName, id = ShopList.UNDEFINED_ID, enabled = true)
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
        recentlyDeletedShopListWithShopItemsDbModel = shopListDao.getShopListWithItems(id)
        shopListDao.deleteShopList(id)
    }

    override suspend fun undoDelete() {
        recentlyDeletedShopListWithShopItemsDbModel?.let { it1 ->
            shopListDao.insertShopList(it1.shopListDbModel)
            it1.shopList.forEach {
                shopItemDao.addShopItem(it)
            }
        }
    }

    override suspend fun updateListEnabled(shopList: ShopList) {
        shopListDao.updateListEnabled(ListEnabled(shopList.id, shopList.enabled))
    }

    override suspend fun updateListName(id: Int, name: String) {
        shopListDao.updateListName(ListName(id, name))
    }


    override fun getShopListWithItems(listId: Int): Flow<ShopListWithItems> {
        return shopListDao.getShopListWithItemsFlow(listId).map { list ->
            Log.d("getShopListWithItems", "shopList = $list")
            val newList = list.shopList.sortedBy { it.position }.toMutableList()
            mapper.mapShopListWithItemsDbModelToShopList(list.copy(shopList = newList))
        }
    }


    override suspend fun exportListToTxt(listId: Int) {

        val shopListWithItems = shopListDao.getShopListWithItems(listId)

        val listName = shopListWithItems.shopListDbModel.name
        val fileName = "$listName.txt"

        val list = shopListWithItems.shopList
        var content = "$listName\n\n"
        list.forEach {
            val row = String.format(
                "%-4s %-30s %s",
                (if (it.enabled) "-" else "+"),
                it.name,
                it.count
            )
            content += row + "\n"
        }
        content = content.dropLast(1)

        val extDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath

        try {
            val file = File(extDir, fileName)

            withContext(Dispatchers.IO) {
                val writer = FileWriter(file)
                writer.write(content)
                writer.close()
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }

        Log.d(
            "exportListToTxt", "fileName = $fileName\n" +
                    "content:\n$content\n" +
                    "extDir = $extDir"
        )
    }

    override suspend fun loadTxtList(listName: String) {
        val fileName = "$listName.txt"
        Log.d("loadTxtList", "fileName = $fileName")
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        val file = File(path, "$listName.txt")
        val exists = file.exists()
        Log.d("loadTxtList", "exists = $exists")
        if (exists) {
            val bufferedReader: BufferedReader = file.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            Log.d("loadTxtList", "content:\n$inputString")
            val lines = inputString.split("\n")
            Log.d("loadTxtList", "title = ${lines.first()}")
            val list = mutableListOf<ShopItem>()
            lines.takeLast(lines.size - 2).forEach { line ->
                val firstSymbol = line.first()
                val enabled = firstSymbol != '+'

                val count = line.takeLastWhile { it.isDigit() || it == '.' }

                val range = line.length - count.length
                val itemName = line.slice(1 until range).trim { it == ' ' }

                val item = ShopItem(itemName, count.toDouble(), enabled)
                Log.d("loadTxtList", "item = $item")
                list.add(item)
            }
            Log.d("loadTxtList", "currentTimestamp = ${System.currentTimeMillis()}")
            var newName =
                if (listName.contains("___")) listName.take(listName.length - 16) else listName
            Log.d("loadTxtList", "newName = $newName")
            newName = newName + "___" + System.currentTimeMillis()
            addShopList(newName)

            shopListDao.getShopListId(newName)?.let { listId ->
                list.withIndex().forEach {
                    shopItemDao.addShopItem(
                        mapper
                            .mapShopItemEntityToDbModel(it.value)
                            .copy(shopListId = listId, position = it.index)
                    )
                }
            }
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


    override suspend fun setCurrentListId(listId: Int) {
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