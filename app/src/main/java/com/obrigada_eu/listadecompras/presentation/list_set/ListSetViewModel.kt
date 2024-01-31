package com.obrigada_eu.listadecompras.presentation.list_set

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.domain.shop_list.AddShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.DeleteShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.DragShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetAllListsWithoutItemsUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.LoadFilesListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.LoadFromTxtFileUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.SetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.domain.shop_list.UndoDeleteListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.UpdateShopListEnabledUseCase
import com.obrigada_eu.listadecompras.presentation.SwipeSwapViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListSetViewModel @Inject constructor(
    getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsUseCase,
    private val addShopListUseCase: AddShopListUseCase,
    private val deleteShopListUseCase: DeleteShopListUseCase,
    private val changeEnabledUseCase: UpdateShopListEnabledUseCase,
    private val dragShopListUseCase: DragShopListUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase,
    private val getCurrentListIdUseCase: GetCurrentListIdUseCase,
    private val undoDeleteListUseCase: UndoDeleteListUseCase,
    private val loadFilesListUseCase: LoadFilesListUseCase,
    private val loadFromTxtFileUseCase: LoadFromTxtFileUseCase,
) : SwipeSwapViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _shopListIdLD = MutableLiveData<Int>()
    val shopListIdLD: LiveData<Int>
        get() = _shopListIdLD

    val allListsWithoutItems = getAllListsWithoutItemsUseCase().asLiveData(scope.coroutineContext)

    private var _showCreateListForFile = MutableStateFlow(false)
    val showCreateListForFile: LiveData<Boolean>
        get() = _showCreateListForFile.asLiveData()

    private var _oldFileName: MutableLiveData<String?> = MutableLiveData(null)
    val oldFileName: LiveData<String?>
        get() = _oldFileName

    private var _fileWithoutErrors = MutableStateFlow(true)
    val fileWithoutErrors: LiveData<Boolean>
        get() = _fileWithoutErrors.asLiveData()


    fun updateUiState(uiState: Boolean, oldFileName: String? = null) {
        _showCreateListForFile.update { uiState }

        if (this._oldFileName.value == null) {
            this._oldFileName.value = oldFileName
        } else {
            if (oldFileName == null) {
                this._oldFileName.value = null
            }
        }

        Log.d("ListSetViewModel","oldFileName = $oldFileName")
    }


    private var _filesList = MutableLiveData<List<String>?>()
    val filesList: LiveData<List<String>?>
        get() = _filesList


    init {
        viewModelScope.launch {
            getCurrentListIdUseCase().collect{
                Log.d("ListSetViewModel","init id = $it")
                _shopListIdLD.value = it
            }
        }
    }

    fun openShopList(listId: Int){
        scope.launch {
            setCurrentListIdUseCase(listId)
        }
    }


    fun addShopList(inputName: String?, fromTxtFile: Boolean = false) {
        val name = parseName(inputName)
        val fieldsValid = validateInput(name)
        Log.d("addShopList check", "fromTxtFile = $fromTxtFile, fieldsValid = $fieldsValid")
        if (!fromTxtFile && fieldsValid) {

            viewModelScope.launch {
                addShopListUseCase(name)
            }
        }
        if (fromTxtFile && fieldsValid) {

            val oldName = _oldFileName.value ?: name
            val newName = if (_oldFileName.value != null) name else null
            scope.launch {
                _fileWithoutErrors.value = loadFromTxtFileUseCase(oldName, newName)
            }
            updateUiState(false, null)
            if (!_fileWithoutErrors.value) _fileWithoutErrors.value = true
        }

        if (fromTxtFile && !fieldsValid) {
            updateUiState(true, name)
        }
    }

    private fun validateInput(name: String): Boolean {
        Log.d("validateInput", "name = $name")
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }

        val names = allListsWithoutItems.value?.map { it.name }

        Log.d("validateInput", "names = $names")
        names?.let {
            if (names.contains(name)) {
                _errorInputName.value = true
                Log.d("validateInput", "_errorInputName.value = ${_errorInputName.value}")
                result = false
            }
        }
        Log.d("validateInput", "result = $result")
        return result
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
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

}