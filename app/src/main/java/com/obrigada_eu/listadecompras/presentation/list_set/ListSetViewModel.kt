package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.domain.shop_list.DeleteShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.DragShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetAllListsWithoutItemsFlowUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetAllListsWithoutItemsUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetListFromTxtFileUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.LoadFilesListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.SaveListToDbUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.SetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.domain.shop_list.UndoDeleteListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.UpdateShopListEnabledUseCase
import com.obrigada_eu.listadecompras.presentation.SwipeSwapViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListSetViewModel @Inject constructor(
    getAllListsWithoutItemsFlowUseCase: GetAllListsWithoutItemsFlowUseCase,
    private val getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsUseCase,
    private val deleteShopListUseCase: DeleteShopListUseCase,
    private val changeEnabledUseCase: UpdateShopListEnabledUseCase,
    private val dragShopListUseCase: DragShopListUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase,
    private val getCurrentListIdUseCase: GetCurrentListIdUseCase,
    private val undoDeleteListUseCase: UndoDeleteListUseCase,
    private val loadFilesListUseCase: LoadFilesListUseCase,
    private val saveListToDbUseCase: SaveListToDbUseCase,
    private val getListFromTxtFileUseCase: GetListFromTxtFileUseCase,
    private val contentResolver: ContentResolver,
) : SwipeSwapViewModel() {


    private val scope = CoroutineScope(Dispatchers.IO)

    private val _shopListId = MutableStateFlow<Int>(ShopList.UNDEFINED_ID)
    val shopListId: StateFlow<Int> = _shopListId

    val allListsWithoutItemsStateFlow = getAllListsWithoutItemsFlowUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = emptyList()
    )



    private val _createListFragmentUI = MutableStateFlow<CreateNewListFragmentState?>(null)
    val createListFragmentUI: StateFlow<CreateNewListFragmentState?> = _createListFragmentUI

    fun setListFragmentUI(state: CreateNewListFragmentState? = null) {
        _createListFragmentUI.value = state ?: CreateNewListFragmentState()
    }

    fun resetListFragmentUI() {
        _createListFragmentUI.value = null
    }


    private val _fileReadingError = MutableStateFlow(false)
    val fileReadingError: StateFlow<Boolean> = _fileReadingError

    private val _listSaved: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val listSaved: StateFlow<Boolean?> = _listSaved


    init {
        viewModelScope.launch {
            getCurrentListIdUseCase().collect{
//                Log.d(TAG,"init id = $it")
                _shopListId.value = it
            }
        }
    }



    fun setCurrentListId(listId: Int){
        scope.launch {
            setCurrentListIdUseCase(listId)
        }
    }


    fun addShopList(
        inputName: String?,
        uri: Uri? = null,
    ) {

        val name = parseName(inputName)

        viewModelScope.launch {
            val fieldIsValid = validateInput(name)

            // getting list from text file:
            val listWithItemsToLoad = getListFromTxtFileUseCase(name, uri)
            _fileReadingError.value = listWithItemsToLoad == null

            listWithItemsToLoad?.let { listWithItems ->
                val listNameFromText = listWithItems.name

                if (listNameFromText == name && fieldIsValid) {
                    // names from title and content are equals, name is valid:

                    _listSaved.value = saveListToDbUseCase(listWithItems)
                } else {
                    setListFragmentUI(CreateNewListFragmentState(
                            fromTxtFile = true,
                            nameFromTitle = name,
                            shopList = listWithItemsToLoad
                    ))
                }
            }
        }
    }


    fun setListSaved(saved: Boolean) {
        _listSaved.value = saved
    }

    fun resetListSaved() {
        _listSaved.value = null
    }

    private suspend fun validateInput(name: String): Boolean {
//        Log.d("validateInput", "name = $name")
        return !(name.isBlank() || getAllListsWithoutItemsUseCase().map { it.name }.contains(name))
    }


    fun getFileName(uri: Uri): String? {

        var fileName: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null, null, null
            )?.use { cursor ->
                fileName = cursor.apply { moveToFirst() }.getString(0)
            }
        }
        if (fileName == null) {
            fileName = uri.path?.substringAfterLast('/')
        }
        return fileName?.substringBeforeLast('.')
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }


    fun deleteShopList(id: Int) {
        scope.launch {
            deleteShopListUseCase(id)
        }
    }

    fun undoDelete() {
        scope.launch {
            undoDeleteListUseCase()
        }
    }

    fun changeEnableState(shopList: ShopList) {
        viewModelScope.launch {
            val newItem = shopList.copy(enabled = !shopList.enabled)
            changeEnabledUseCase(newItem)
        }
    }


    fun dragShopList(from: Int, to: Int) {
        viewModelScope.launch {
            dragShopListUseCase(from, to)
        }
    }


    fun loadFilesList(): List<String>? {
        return loadFilesListUseCase()
    }



    companion object {

        private const val TAG = "ListSetViewModel"
    }
}