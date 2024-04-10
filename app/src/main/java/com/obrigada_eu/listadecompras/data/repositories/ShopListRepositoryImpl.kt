package com.obrigada_eu.listadecompras.data.repositories

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.obrigada_eu.listadecompras.BuildConfig
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.data.database.ShopItemDao
import com.obrigada_eu.listadecompras.data.database.ShopListDao
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.BufferedReader
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
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

    private lateinit var listSet: MutableList<ShopListDbModel>


    override suspend fun addShopList(shopListName: String, enabled: Boolean) {
        val dbModel =
            ShopListDbModel(name = shopListName, id = ShopList.UNDEFINED_ID, enabled = enabled)
        dbModel.position = (shopListDao.getLargestOrder() ?: -1) + 1
        shopListDao.insertShopList(dbModel)
    }


    override fun getAllListsWithoutItemsFlow(): Flow<List<ShopList>> {
        return shopListDao.getShopListsWithoutShopItemsFlow().map { set ->
//            Log.d("getAllListsWithoutItems", "ListSet = ${set.map { it.name to it.id }}")
            listSet = set.toMutableList()
            mapper.mapListSetToEntity(set)
        }
    }

    override suspend fun getAllListsWithoutItems(): List<ShopList> {
        return mapper.mapListSetToEntity(shopListDao.getShopListsWithoutShopItems())
    }


    override fun getShopListName(listId: Int): Flow<String> {
        return shopListDao.getShopListName(listId)
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

    override suspend fun dragShopList(from: Int, to: Int) {

        val lastPosition = listSet[to].position
        if (from < to) {
            for (i in to downTo from + 1) {
                listSet[i].position = listSet[i - 1].position
            }
        } else if (from > to) {
            for (i in to until from) {
                listSet[i].position = listSet[i + 1].position
            }
        }
        listSet[from].position = lastPosition
        listSet.sortBy { it.position }
        shopListDao.updateListSet(listSet)
    }

    override suspend fun updateListName(id: Int, name: String) {
        shopListDao.updateListName(ListName(id, name))
    }


    override fun getShopListWithItems(listId: Int): Flow<ShopListWithItems> {
        return shopListDao.getShopListWithItemsFlow(listId).map { list ->
//            Log.d(
//                "getShopListWithItems",
//                "shopList = ${list.shopList.map { it.name to it.position }}"
//            )
            val newList = list.shopList.sortedBy { it.position }.toMutableList()
            mapper.mapShopListWithItemsDbModelToShopList(list.copy(shopList = newList))
        }
    }


    override suspend fun exportListToTxt(listId: Int) {

        val shopListWithItems = shopListDao.getShopListWithItems(listId)
        val nameAndContent = getContent(shopListWithItems)
        saveFile(context, nameAndContent.first, nameAndContent.second)
    }


    private fun getContent(shopListWithItems: ShopListWithShopItemsDbModel): Pair<String, String> {
        val listName = shopListWithItems.shopListDbModel.name
        val listEnabled = if (shopListWithItems.shopListDbModel.enabled) "-" else "+"

        var content = context.getString(R.string.do_not_modify_this_file_top_warning)
        content += String.format(
            "%-4s\t%s\n\n",
            listEnabled,
            listName
        )
        val list = shopListWithItems.shopList
        list.sortedBy { it.position }.forEach {
            val row = String.format(
                "%-4s\t%-30s\t%-9s\t%-9s",
                (if (it.enabled) "-" else "+"),
                it.name,
                it.count ?: "",
                it.units ?: ""
            )
            content += "$row\n"
        }
        content = content.dropLast(1)
        return Pair(listName, content)
    }

    override fun shareTxtList(listId: Int): Flow<Intent> {
        return shopListDao.getShopListWithItemsFlow(listId).map { list ->
            val nameAndContent = getContent(list)

            val file = File(context.cacheDir, "${nameAndContent.first}.txt")
            PrintWriter(file).also {
                it.print("")
                it.close()
            }
            file.appendText(nameAndContent.second)

            val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            intent
        }
            .flowOn(Dispatchers.IO)
    }


    private fun saveFile(context: Context, fileName: String, text: String) {
        val dirDocuments = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val append = "$separator${context.resources.getString(R.string.app_name)}"
        val dirDocumentsApp = File("$dirDocuments$append")
        if (!dirDocuments.isDirectory) {
            dirDocuments.mkdir()
        }
        if (!dirDocumentsApp.isDirectory) {
            dirDocumentsApp.mkdir()
        }
        val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOCUMENTS}$append"
            )
            val extVolumeUri: Uri = MediaStore.Files.getContentUri("external")
            val fileUri: Uri? = context.contentResolver.insert(extVolumeUri, values)
            fileUri?.let { context.contentResolver.openOutputStream(it) }
        } else {
            val file = File(dirDocumentsApp.absolutePath, "$fileName.txt")
            FileOutputStream(file)
        }

        val bytes = text.toByteArray()
        outputStream?.write(bytes)
        outputStream?.close()
    }


    override suspend fun saveListToDb(shopListWithItems: ShopListWithItems): Boolean {
//        Log.d("loadTxtList", "listName = ${shopListWithItems.name}")

        try {
            addShopList(shopListWithItems.name, shopListWithItems.enabled)

            shopListDao.getShopListId(shopListWithItems.name)?.let { listId ->
                shopListWithItems.itemList.withIndex().forEach {
                    shopItemDao.addShopItem(
                        mapper
                            .mapShopItemEntityToDbModel(it.value)
                            .copy(shopListId = listId, position = it.index)
                    )
                }
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }


    override fun getListFromTxtFile(fileName: String, uri: Uri?): ShopListWithItems? {

//        Log.d("loadTxtList", "fileName = $fileName, uri = $uri")
        if (uri == null) {

            val dirDocuments = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val append = "$separator${context.resources.getString(R.string.app_name)}"

            val file = File("$dirDocuments$append$separator$fileName.txt")
//            Log.d(TAG, "getListFromTxtFile: file = $file")
            if (file.exists()) {
//                Log.d(TAG, "getListFromTxtFile: file.exists()")
                return try {
                    val bufferedReader: BufferedReader = file.bufferedReader()
                    val contentString = bufferedReader.use { it.readText() }
                    getListWithItemsFromString(contentString)
                } catch (e: Exception) {
//                    Log.d(TAG, "getListFromTxtFile: Exception = $e")
                    null
                }
            }

        } else {
            return try {
                val contentString: String = retrieveContentFromContentUri(uri)
                getListWithItemsFromString(contentString)
            } catch (e: Exception) {
//                Log.d(TAG, "getListFromTxtFile: Exception = $e")
                null
            }
        }

        return null
    }


    private fun getListWithItemsFromString(inputString: String): ShopListWithItems {
//        Log.d(TAG, "getListWithItemsFromString: inputString = $inputString")
        val list = mutableListOf<ShopItem>()
        val lines = inputString.split("\n")

        lines.drop(7).forEach { line ->
            val values = line.split("\t")

            val enabled = when (line.first()) {
                '-' -> true
                '+' -> false
                else -> {
                    throw IllegalArgumentException("Unknown symbol for the field 'enabled': ${line.first()}")
                }
            }
            val itemName = values[1].trim()
            val count = values[2].trim().ifEmpty { null }
            val units = values[3].trim().ifEmpty { null }

            val item = ShopItem(itemName, count?.toDouble(), units, enabled)
//            Log.d("loadTxtList", "item = $item")
            list.add(item)
        }
//        Log.d(TAG, "getListName: listEnabled = ${lines[5].first()}")

        val listEnabled = when (lines[5].first()) {
            '-' -> true
            '+' -> false
            else -> {
                throw IllegalArgumentException("Unknown symbol for the field 'enabled': ${lines[0].first()}")
            }
        }

        val listName = lines[5].split("\t")[1].trim()
//        Log.d("loadTxtList", "listName = $listName")

        return ShopListWithItems(listName, listEnabled, list)
    }


    private fun retrieveContentFromContentUri(fileUri: Uri): String {

        val iStream: InputStream? = context.contentResolver.openInputStream(fileUri)

        val inputStreamReader = InputStreamReader(iStream)
        //        Log.d(TAG, "createFileFromContentUri: inputString = $inputString")

        return inputStreamReader.use { it.readText() }

    }


    override suspend fun loadFilesList(): List<String>? {
        val dirDocuments =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val append = "$separator${context.resources.getString(R.string.app_name)}"
        val dirDocumentsApp = File("$dirDocuments$append")
//        if (!dirDocumentsApp.isDirectory) {
//            Log.d("loadFilesList", "!dirDocumentsApp.isDirectory")
//        }

        return dirDocumentsApp.list { _, filename -> filename.endsWith(".txt") }?.toList()
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
//        Log.d(TAG, "setCurrentListId: listId = $listId")
        shopListPreferences.edit { preferences ->
            preferences[KEY_LIST_ID] = listId
        }
    }


    override fun getCurrentListId(): StateFlow<Int> {
        return shopListIdFlow
    }



    private companion object {

        private const val TAG = "ShopListRepositoryImpl"

        private const val FILE_PROVIDER_AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"

        val KEY_LIST_ID = intPreferencesKey(name = "listId")

    }
}