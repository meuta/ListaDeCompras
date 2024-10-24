package com.obrigada_eu.listadecompras.presentation.list_set

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
import com.obrigada_eu.listadecompras.domain.shop_list.UndoDeleteListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.UpdateShopListEnabledUseCase
import com.obrigada_eu.listadecompras.presentation.SwipeSwapViewModel
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


    private val _errorInputNameFromTitle: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorInputNameFromTitle: StateFlow<String?> = _errorInputNameFromTitle

    private val _errorInputNameFromContent: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorInputNameFromContent: StateFlow<String?> = _errorInputNameFromContent


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


    private val _fileReadingError = MutableStateFlow(false)
    val fileReadingError: StateFlow<Boolean> = _fileReadingError

    private val _listSaved: MutableStateFlow<Boolean?>  = MutableStateFlow(null)
    val listSaved: StateFlow<Boolean?> = _listSaved

    private var fileUri: Uri? = null


    private val _isNameFromTitle = MutableStateFlow<Boolean?>(null)
    val isNameFromTitle: StateFlow<Boolean?> = _isNameFromTitle


    private val _listNameFromFileContent = MutableStateFlow<String?>(null)
    val listNameFromFileContent: StateFlow<String?> = _listNameFromFileContent


    private val userCheckedAlterName = MutableStateFlow<Boolean>(false)

    private val _requestForFileLoading = MutableStateFlow<Boolean>(false)
    val requestForFileLoading: StateFlow<Boolean> = _requestForFileLoading

    init {
        viewModelScope.launch {
            getCurrentListIdUseCase().collect{
//                Log.d(TAG,"init id = $it")
                _shopListId.value = it
            }
        }
    }


    fun setRequestForFileLoading(isRequest: Boolean){
        _requestForFileLoading.value = isRequest
    }

    fun updateUiState(cardNewListVisibility: Boolean, showCreateListForFile: Boolean, oldFileName: String? = null, uri: Uri? = null) {
//        Log.d(TAG, "updateUiState: cardNewListVisibility = $cardNewListVisibility, showCreateListForFile = $showCreateListForFile, oldFileName = $oldFileName, uri= $uri")
        _cardNewListVisibilityStateFlow.update { cardNewListVisibility }
        _fromTxtFile.update { showCreateListForFile }

        if (this._listNameFromFileTitle.value == null) {
            this._listNameFromFileTitle.value = oldFileName
        } else {
            if (oldFileName == null) {
                this._listNameFromFileTitle.value = null
            }
        }
//        Log.d(TAG,"oldFileName = $oldFileName")
        this.fileUri = uri

        if (!cardNewListVisibility) {
            _isNameFromTitle.value = null
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
            val fieldIsValid = validateInput(name, "title")
//            Log.d("addShopList check", "fromTxtFile = $fromTxtFile, fieldsValid = $fieldsValid")
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
                val myFileUri = fileUri ?: uri

                val listWithItems = getListFromTxtFileUseCase(oldName, myFileUri)
                _fileReadingError.value = listWithItems == null

                listWithItems?.let {
                    var listNameFromText = it.name
//                    Log.d("addShopList", "name1 = $oldName")
//                    Log.d("addShopList", "name2 = $listNameFromText")
//                    Log.d("addShopList", "alterName = $alterName")

                    var fieldIsValidContent = true
                    if (listNameFromText != oldName) {
                        alterName?.let {
                            listNameFromText = parseName(alterName)
                        }
//                        Log.d(TAG, "addShopList: listNameFromText = $listNameFromText")
                        _listNameFromFileContent.value = listNameFromText

                        fieldIsValidContent = validateInput(listNameFromText, "content")
                    }

                    if (fieldIsValid && listNameFromText == oldName) {
                        // names from title and content are equals, name is valid:

                        _listSaved.value = saveListToDbUseCase(listWithItems.copy(name = name))
//                        Log.d(TAG, "addShopList: listSaved = $listSaved")

                        updateUiState(
                            cardNewListVisibility = false,
                            showCreateListForFile = false,
                            oldFileName = null,
                            uri = null
                        )

                        _listNameFromFileContent.value = null

                    } else {
                        if (listNameFromText != oldName){
                            // names from title and content are different:

//                            Log.d(TAG, "addShopList: isTitle.value = ${isNameFromTitle.value}")

                            if (fieldIsValidContent && isNameFromTitle.value == false && userCheckedAlterName.value) {
                                _listSaved.value = saveListToDbUseCase(listWithItems.copy(name = listNameFromText))
//                                Log.d(TAG, "addShopList: listSaved = $listSaved")

                                updateUiState(
                                    cardNewListVisibility = false,
                                    showCreateListForFile = false,
                                    oldFileName = null,
                                    uri = null
                                )

                                _listNameFromFileContent.value = null
                                userCheckedAlterName.value = false

                            } else if (fieldIsValid && isNameFromTitle.value == true && userCheckedAlterName.value) {
                                _listSaved.value = saveListToDbUseCase(listWithItems.copy(name = name))
//                                Log.d(TAG, "addShopList: listSaved = $listSaved")

                                updateUiState(
                                    cardNewListVisibility = false,
                                    showCreateListForFile = false,
                                    oldFileName = null,
                                    uri = null
                                )

                                _listNameFromFileContent.value = null
                                userCheckedAlterName.value = false
                            } else {

                                userCheckedAlterName.value = true
//                                Log.d(TAG, "addShopList: _userCheckedDifferNames.value = ${userCheckedAlterName.value}")
                                updateUiState(
                                    cardNewListVisibility = true,
                                    showCreateListForFile = true,
                                    oldFileName = name,
                                    uri = myFileUri
                                )
                            }

                        } else {
                            // names from title and content are equals, name is not valid:

                            updateUiState(
                                cardNewListVisibility = true,
                                showCreateListForFile = true,
                                oldFileName = name,
                                uri = myFileUri
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
            if (field  == "title"){
                _errorInputNameFromTitle.value = context.getString(R.string.error_input_list_name_empty)

            } else if(field  == "content"){
                _errorInputNameFromContent.value = context.getString(R.string.error_input_list_name_empty)
            }
            return false
        }
        val myNamesList = getAllListsWithoutItemsUseCase().map { it.name }
//        Log.d("validateInput", "myNamesList = $myNamesList")
        if (myNamesList.contains(name)) {
            if (field  == "title"){
                _errorInputNameFromTitle.value = context.getString(R.string.error_input_list_name_duplicated)
            } else if(field  == "content"){
                _errorInputNameFromContent.value = context.getString(R.string.error_input_list_name_duplicated)
            }
            return false
        }
        return true
    }


    fun getFileName(uri: Uri): String? {

        var fileName : String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri,null,null,null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                fileName = cursor.getString(nameIndex)
            }
        }
        if (fileName == null) {
            fileName = uri.path
            fileName?.let{
                val cut = it.lastIndexOf('/')
                if (cut != -1) {
                    fileName = it.substring(cut + 1)
                }
            }
        }
        return fileName?.dropLast(4)
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    fun resetErrorInputNameTitle() {
//        Log.d(TAG, "resetErrorInputNameTitle: ")
        _errorInputNameFromTitle.value = null
    }

    fun resetErrorInputNameContent() {
//        Log.d(TAG, "resetErrorInputNameContent: ")
        _errorInputNameFromContent.value = null
    }

    fun resetFileWithoutErrors() {
        _fileReadingError.value = false
    }

    fun resetListSaved() {
        _listSaved.value = null
    }

    fun resetListNameFromContent() {
        _listNameFromFileContent.value = null
    }

    fun resetUserCheckedAlterName() {
        userCheckedAlterName.value = false
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