package com.obrigada_eu.listadecompras.presentation.list_set

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ListSetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllListsWithoutItemsFlowUseCase: GetAllListsWithoutItemsFlowUseCase,
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

    private val _errorInputName = MutableStateFlow(false)
    val errorInputName: StateFlow<Boolean> = _errorInputName

    private val _shopListIdLD = MutableLiveData<Int>()
    val shopListIdLD: LiveData<Int>
        get() = _shopListIdLD

    val allListsWithoutItems = getAllListsWithoutItemsFlowUseCase().asLiveData(scope.coroutineContext)


    private val _cardNewListVisibilityStateFlow = MutableStateFlow(false)
    val cardNewListVisibilityStateFlow: StateFlow<Boolean> = _cardNewListVisibilityStateFlow

    private var _fromTxtFile = MutableStateFlow(false)
    val fromTxtFile: StateFlow<Boolean> = _fromTxtFile

    private var _oldFileName: MutableLiveData<String?> = MutableLiveData(null)
    val oldFileName: LiveData<String?>
        get() = _oldFileName

    private var _fileWithoutErrors = MutableStateFlow(true)
    val fileWithoutErrors: StateFlow<Boolean> = _fileWithoutErrors

    private var namesList = listOf<String>()

    private var filePath: String? = null
    private var fileUri: Uri? = null

    init {
//        Log.d(TAG, "1. namesList = $namesList ")
        scope.launch {
            getAllListsWithoutItemsFlowUseCase().collect{list ->
                namesList = list.map { it.name }
//                Log.d(TAG, "2. namesList = $namesList ")

            }
        }
        viewModelScope.launch {
            getCurrentListIdUseCase().collect{
//                Log.d(TAG,"init id = $it")
                _shopListIdLD.value = it
            }
        }
    }

    fun updateUiState(cardNewListVisibility: Boolean, showCreateListForFile: Boolean, oldFileName: String? = null, filePath: String? = null, uri: Uri? = null) {
//        Log.d(TAG, "updateUiState: cardNewListVisibility = $cardNewListVisibility, showCreateListForFile = $showCreateListForFile, oldFileName = $oldFileName, filePath = $filePath, uri= $uri")
        _cardNewListVisibilityStateFlow.update { cardNewListVisibility }
        _fromTxtFile.update { showCreateListForFile }

        if (this._oldFileName.value == null) {
            this._oldFileName.value = oldFileName
        } else {
            if (oldFileName == null) {
                this._oldFileName.value = null
            }
        }

//        Log.d(TAG,"oldFileName = $oldFileName")

        this.filePath = filePath
        this.fileUri = uri
    }


    private var _filesList = MutableLiveData<List<String>?>()
    val filesList: LiveData<List<String>?>
        get() = _filesList



    fun setCurrentListId(listId: Int){
        scope.launch {
            setCurrentListIdUseCase(listId)
        }
    }


    fun addShopList(
        inputName: String?,
        fromTxtFile: Boolean = false,
        path: String? = null,
        uri: Uri? = null
    ) {
        val name = parseName(inputName)
//        Log.d(TAG, "addShopList: inputName = $inputName, fromTxtFile = $fromTxtFile, path = $path, uri = $uri")
//        Log.d("addShopList", "namesList = $namesList")

        var fieldsValid = !namesList.contains(name)

        if (!fieldsValid) {
            _errorInputName.value = true
        }

        viewModelScope.launch {
            fieldsValid = validateInput(name)
//            Log.d("addShopList check", "fromTxtFile = $fromTxtFile, fieldsValid = $fieldsValid")
            if (!fromTxtFile && fieldsValid) {

                addShopListUseCase(name)
                updateUiState(false, false, null, null, null)
            }

            if (fromTxtFile) {

                val oldName = _oldFileName.value ?: name

                val myFilePath = filePath ?: path
                val myFileUri = fileUri ?: uri

                val listWithItems = getListFromTxtFileUseCase(oldName, myFilePath, myFileUri)
                val listNameFromText = listWithItems.first?.name
                val listEnabled = listWithItems.first?.enabled
                val list = listWithItems.second

                _fileWithoutErrors.value = listNameFromText != null && listEnabled != null

                Log.d("addShopList", "name1 = $oldName")
                Log.d("addShopList", "name2 = $listNameFromText")
                if (_fileWithoutErrors.value) {
                    if (fieldsValid) {
                        val newName = if (_oldFileName.value != null) name else null


                            val listSaved = saveListToDbUseCase(oldName, newName, listEnabled ?: true, list)
                            Log.d(TAG, "addShopList: listSaved = $listSaved")

                        updateUiState(false, false, null, null, null)
                    } else {

                        updateUiState(true, true, name, myFilePath, myFileUri)
                    }
                }
            }
        }
    }

    private suspend fun validateInput(name: String): Boolean {
//        Log.d("validateInput", "name = $name")
        if (name.isBlank()) {
            _errorInputName.value = true
            return false
        }

        val myNamesList = getAllListsWithoutItemsUseCase().map { it.name }
//        Log.d("validateInput", "myNamesList = $myNamesList")

        if (myNamesList.contains(name)) {
            _errorInputName.value = true
//            Log.d("validateInput", "_errorInputName.value = ${_errorInputName.value}")
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

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetFileWithoutErrors() {
        _fileWithoutErrors.value = true
    }

    fun showErrorInputName() {
        _errorInputName.value = true
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


    fun loadFilesList() {
        viewModelScope.launch {
            _filesList.value = loadFilesListUseCase()
        }
    }

    companion object {

        private const val TAG = "ListSetViewModel"

    }
}