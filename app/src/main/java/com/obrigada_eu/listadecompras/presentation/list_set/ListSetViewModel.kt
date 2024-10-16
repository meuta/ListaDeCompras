package com.obrigada_eu.listadecompras.presentation.list_set

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.domain.shop_list.AddShopListUseCase
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
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListWithItems
import com.obrigada_eu.listadecompras.domain.shop_list.UndoDeleteListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.UpdateShopListEnabledUseCase
import com.obrigada_eu.listadecompras.presentation.SwipeSwapViewModel
import com.obrigada_eu.listadecompras.presentation.list_set.ListSetFragment.Companion.NAME_FROM_CONTENT_FIELD
import com.obrigada_eu.listadecompras.presentation.list_set.ListSetFragment.Companion.NAME_FROM_TITLE_FIELD
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ListSetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getAllListsWithoutItemsFlowUseCase: GetAllListsWithoutItemsFlowUseCase,
    private val getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsUseCase,
    private val addShopListUseCase: AddShopListUseCase,
    private val deleteShopListUseCase: DeleteShopListUseCase,
    private val changeEnabledUseCase: UpdateShopListEnabledUseCase,
    private val dragShopListUseCase: DragShopListUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase,
    private val getCurrentListIdUseCase: GetCurrentListIdUseCase,
    private val undoDeleteListUseCase: UndoDeleteListUseCase,
    private val loadFilesListUseCase: LoadFilesListUseCase,
    private val saveListToDbUseCase: SaveListToDbUseCase,
    private val getListFromTxtFileUseCase: GetListFromTxtFileUseCase,
) : SwipeSwapViewModel() {


    private val scope = CoroutineScope(Dispatchers.IO)



    private val _shopListId = MutableStateFlow<Int>(ShopList.UNDEFINED_ID)
    val shopListId: StateFlow<Int> = _shopListId

    val allListsWithoutItemsStateFlow = getAllListsWithoutItemsFlowUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = emptyList()
    )

    private val _cardNewListVisibilityStateFlow = MutableStateFlow(false)
    val cardNewListVisibilityStateFlow: StateFlow<Boolean> = _cardNewListVisibilityStateFlow

    private val _fromTxtFile = MutableStateFlow(false)
    val fromTxtFile: StateFlow<Boolean> = _fromTxtFile

    private val _listNameFromFileTitle = MutableStateFlow<String?>(null)
    val listNameFromFileTitle: StateFlow<String?> = _listNameFromFileTitle

    private val _listNameFromFileContent = MutableStateFlow<String?>(null)
    val listNameFromFileContent: StateFlow<String?> = _listNameFromFileContent

    private val _isNameFromTitle = MutableStateFlow<Boolean?>(null)
    val isNameFromTitle: StateFlow<Boolean?> = _isNameFromTitle

    private val _errorInputNameFromTitle: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorInputNameFromTitle: StateFlow<String?> = _errorInputNameFromTitle

    private val _errorInputNameFromContent: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorInputNameFromContent: StateFlow<String?> = _errorInputNameFromContent

    private val _fileReadingError = MutableStateFlow(false)
    val fileReadingError: StateFlow<Boolean> = _fileReadingError

    private val _listSaved: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val listSaved: StateFlow<Boolean?> = _listSaved

    private var fileUri: Uri? = null

    private var listWithItemsToLoad: ShopListWithItems? = null


    private val userCheckedAlterName = MutableStateFlow<Boolean>(false)


    init {
        viewModelScope.launch {
            getCurrentListIdUseCase().collect{
//                Log.d(TAG,"init id = $it")
                _shopListId.value = it
            }
        }
    }


    fun updateUiState(cardNewListVisibility: Boolean, showCreateListForFile: Boolean, oldFileName: String? = null, uri: Uri? = null) {
//        Log.d(TAG, "updateUiState: cardNewListVisibility = $cardNewListVisibility, showCreateListForFile = $showCreateListForFile, oldFileName = $oldFileName, uri= $uri")
        _cardNewListVisibilityStateFlow.update { cardNewListVisibility }
        _fromTxtFile.update { showCreateListForFile }

        if (_listNameFromFileTitle.value == null) _listNameFromFileTitle.value = oldFileName
//        Log.d(TAG,"oldFileName = $oldFileName")

        fileUri = uri

        if (!cardNewListVisibility) {
            if (_isNameFromTitle.value != null) _isNameFromTitle.value = null
            if (_listNameFromFileTitle.value != null) _listNameFromFileTitle.value = null
            if (_listNameFromFileContent.value != null) _listNameFromFileContent.value = null
            if (listWithItemsToLoad != null) listWithItemsToLoad = null
            if (userCheckedAlterName.value) userCheckedAlterName.value = false
            if (_errorInputNameFromTitle.value != null) _errorInputNameFromTitle.value = null
            if (_errorInputNameFromContent.value != null) _errorInputNameFromContent.value = null
            if (_fileReadingError.value) _fileReadingError.value = false
        }
    }



    fun setCurrentListId(listId: Int){
        scope.launch {
            setCurrentListIdUseCase(listId)
        }
    }


    fun addShopList(
        inputName: String?,
        fromTxtFile: Boolean = false,
        uri: Uri? = null,
        alterName: String? = null // editText content
    ) {

        val name = parseName(inputName)

        viewModelScope.launch {
            val fieldIsValid = validateInput(name, NAME_FROM_TITLE_FIELD)
//            Log.d(TAG, "addShopList: check: name = $name, fieldsValid = $fieldIsValid, alterName = $alterName")
            if (!fromTxtFile) {
                if (fieldIsValid) {
                    addShopListUseCase(name)
                    updateUiState(
                        cardNewListVisibility = false,
                        showCreateListForFile = false,
                        oldFileName = null,
                        uri = null
                    )
                }
            } else {
                // getting list from text file

                val oldName = _listNameFromFileTitle.value ?: name

                if (fileUri != uri) fileUri = uri

                listWithItemsToLoad = listWithItemsToLoad ?: getListFromTxtFileUseCase(oldName, fileUri)
                _fileReadingError.value = listWithItemsToLoad == null

                listWithItemsToLoad?.let { listWithItems ->
                    var listNameFromText = listWithItems.name
//                    Log.d("addShopList", "name1 = $oldName")
//                    Log.d("addShopList", "name2 = $listNameFromText")
//                    Log.d("addShopList", "alterName = $alterName")

                    if (listNameFromText != oldName) {
                        alterName?.let {
                            listNameFromText = parseName(alterName)
                        }
//                    Log.d(TAG, "addShopList: listNameFromText = $listNameFromText")

                        _listNameFromFileTitle.value = name
                        _listNameFromFileContent.value = listNameFromText
                    }

                    if (listNameFromText == oldName && fieldIsValid) {
                        // names from title and content are equals, name is valid:

                        _listSaved.value = saveListToDbUseCase(listWithItems.copy(name = name))
//                        Log.d(TAG, "addShopList: listSaved = $listSaved")

                        updateUiState(
                            cardNewListVisibility = false,
                            showCreateListForFile = false,
                            oldFileName = null,
                            uri = null
                        )

                    } else if (listNameFromText == oldName) {
                        // names from title and content are equals, name is not valid:

                        _isNameFromTitle.value = true

                        updateUiState(
                            cardNewListVisibility = true,
                            showCreateListForFile = true,
                            oldFileName = name,
                            uri = fileUri
                        )
                    } else {
                        // names from title and content are different:

                        val fieldIsValidContent = validateInput(listNameFromText, NAME_FROM_CONTENT_FIELD)

                        if (fieldIsValidContent && isNameFromTitle.value == false && userCheckedAlterName.value) {
                            // the name is taken from content
                            // and name_from_content is valid
                            // and user has checked name_from_content:

                            _listSaved.value = saveListToDbUseCase(listWithItems.copy(name = listNameFromText))
//                            Log.d(TAG, "addShopList: listSaved = $listSaved")

                            updateUiState(
                                cardNewListVisibility = false,
                                showCreateListForFile = false,
                                oldFileName = null,
                                uri = null
                            )

                        } else if (fieldIsValid && isNameFromTitle.value == true && userCheckedAlterName.value) {
                            // the name is taken from title
                            // and name_from_title is valid
                            // and user has checked name_from_content:

                            _listSaved.value = saveListToDbUseCase(listWithItems.copy(name = name))
//                            Log.d(TAG, "addShopList: listSaved = $listSaved")

                            updateUiState(
                                cardNewListVisibility = false,
                                showCreateListForFile = false,
                                oldFileName = null,
                                uri = null
                            )

                        } else {
                            // name_from_title is invalid and name_from_title is invalid
                            // or name_from_title is invalid when name is taken from title
                            // or name_from_content is invalid when name is taken from content
                            // or user has not checked names_from_content:

                            userCheckedAlterName.value = true
//                            Log.d(TAG, "addShopList: _userCheckedDifferNames.value = ${userCheckedAlterName.value}")
                            updateUiState(
                                cardNewListVisibility = true,
                                showCreateListForFile = true,
                                oldFileName = name,
                                uri = fileUri
                            )
                        }
                    }
                }
            }
        }
    }


    private suspend fun validateInput(name: String, field: String): Boolean {
//        Log.d("validateInput", "name = $name")

        if (name.isBlank()) {
            val errorText = context.getString(R.string.error_input_list_name_empty)
            when (field) {
                NAME_FROM_TITLE_FIELD -> _errorInputNameFromTitle.value = errorText
                NAME_FROM_CONTENT_FIELD -> _errorInputNameFromContent.value = errorText
            }
            return false
        }

        val myNamesList = getAllListsWithoutItemsUseCase().map { it.name }
//        Log.d("validateInput", "myNamesList = $myNamesList")
        if (myNamesList.contains(name)) {
            val errorText = context.getString(R.string.error_input_list_name_duplicated)
            when (field) {
                NAME_FROM_TITLE_FIELD -> _errorInputNameFromTitle.value = errorText
                NAME_FROM_CONTENT_FIELD -> _errorInputNameFromContent.value = errorText
            }
            return false
        }
        return true
    }


    fun getFileName(uri: Uri): String? {

        var fileName: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(
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


    fun resetErrorInputName(field: String) {
//        Log.d(TAG, "resetErrorInputName: ")
        when(field){
            NAME_FROM_TITLE_FIELD -> _errorInputNameFromTitle.value = null
            NAME_FROM_CONTENT_FIELD -> _errorInputNameFromContent.value = null
        }
    }

    fun resetListSaved() {
        _listSaved.value = null
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

    fun setIsNameFromTitle(isFromTitle: Boolean?) {
        _isNameFromTitle.value = isFromTitle
    }


    companion object {

        private const val TAG = "ListSetViewModel"

    }
}