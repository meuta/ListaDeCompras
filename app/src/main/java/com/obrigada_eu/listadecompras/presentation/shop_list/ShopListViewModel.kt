package com.obrigada_eu.listadecompras.presentation.shop_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.domain.shop_item.DeleteShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.DragShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.EditShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.GetShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_item.UndoDeleteItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.ExportListToTxtUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetAllListsWithoutItemsUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetShopListNameUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.LoadTxtListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.SetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.UpdateShopListNameUseCase
import com.obrigada_eu.listadecompras.presentation.SwipeSwapViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel @Inject constructor(
    getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsUseCase,
    getShopListUseCase: GetShopListUseCase,
    private val deleteShopItemUseCase: DeleteShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
    private val dragShopItemUseCase: DragShopItemUseCase,
    private val getShopListNameUseCase: GetShopListNameUseCase,
    private val updateShopListNameUseCase: UpdateShopListNameUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase,
    getCurrentListIdUseCase: GetCurrentListIdUseCase,
    private val exportListToTxtUseCase: ExportListToTxtUseCase,
    private val loadTxtListUseCase: LoadTxtListUseCase,
    private val undoDeleteItemUseCase: UndoDeleteItemUseCase,
) : SwipeSwapViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val shopListIdFlow: StateFlow<Int> = getCurrentListIdUseCase()
    val shopListIdLD = shopListIdFlow.asLiveData(scope.coroutineContext)

    private val _shopListName = MutableLiveData<String>()
    val shopListName: LiveData<String>
        get() = _shopListName

    private val _shopList = getShopListUseCase(shopListIdFlow.value).asLiveData(scope.coroutineContext, 500)
    val shopList: LiveData<List<ShopItem>>
        get() = _shopList

    private var _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    val allListsWithoutItems = getAllListsWithoutItemsUseCase().asLiveData(scope.coroutineContext, 500)



    fun updateShopListIdState(listId: Int) {
        scope.launch {
            setCurrentListIdUseCase(listId)
        }
    }


    fun getShopListName() {
        viewModelScope.launch {
            val name = getShopListNameUseCase(shopListIdFlow.value)
            _shopListName.value = name
        }
    }

    fun deleteShopItem(shopItem: ShopItem) {
        viewModelScope.launch {
            deleteShopItemUseCase(shopItem)
        }
    }

    fun changeEnableState(shopItem: ShopItem) {
        viewModelScope.launch {
            val newItem = shopItem.copy(enabled = !shopItem.enabled)
            editShopItemUseCase(newItem)
        }
    }

    fun dragShopItem(from: Int, to: Int) {
        viewModelScope.launch {
            dragShopItemUseCase(from, to)
        }
    }

    fun updateShopListName(inputName: String){
        val name = parseName(inputName)
        val fieldsValid = validateInput(name)
        if (fieldsValid) {
            scope.launch {
                updateShopListNameUseCase(shopListIdFlow.value, name)
            }
        }
    }


    private fun validateInput(name: String): Boolean {
        _errorInputName.value = false
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }


        val names = allListsWithoutItems.value?.map { it.name }
        Log.d("validateInput", "names = $names")
        names?.let {
            if (names.contains(name)) {
                val id = allListsWithoutItems.value?.find { it.name == name }?.id
                if (id != shopListIdFlow.value) {
                    _errorInputName.value = true
                    Log.d("validateInput", "_errorInputName.value = ${_errorInputName.value}")
                    result = false
                }
            }
        }
        return result
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun exportListToTxt() {
        scope.launch {
            exportListToTxtUseCase(shopListIdFlow.value)
        }
    }

    fun loadTxtList(listName: String) {
        scope.launch {
            loadTxtListUseCase(listName)
        }
    }

    fun undoDelete() {
        scope.launch {
            undoDeleteItemUseCase()
        }
    }
}