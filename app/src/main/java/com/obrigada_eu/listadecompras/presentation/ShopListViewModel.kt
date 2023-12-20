package com.obrigada_eu.listadecompras.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel @Inject constructor(
    getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsUseCase,
    private val getShopListUseCase: GetShopListUseCase,
    private val deleteShopItemUseCase: DeleteShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
    private val dragShopItemUseCase: DragShopItemUseCase,
    private val getShopListNameUseCase: GetShopListNameUseCase,
    private val updateShopListNameUseCase: UpdateShopListNameUseCase,
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _shopListName = MutableLiveData<String>()
    val shopListName: LiveData<String>
        get() = _shopListName


    private var _shopList = getShopListUseCase(0).asLiveData(scope.coroutineContext, 500)
    val shopList: LiveData<List<ShopItem>>
        get() = _shopList

    private var _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    val allListsWithoutItems = getAllListsWithoutItemsUseCase().asLiveData(scope.coroutineContext, 500)


    private var shopListId: Int = ShopListEntity.UNDEFINED_ID
    fun getShopList(listId: Int) {
        shopListId = listId
        scope.launch {
            _shopList = getShopListUseCase(listId).asLiveData(scope.coroutineContext, 500)
        }
    }

    fun getShopListName(listId: Int) {
        viewModelScope.launch {
            val name = getShopListNameUseCase(listId)
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

    fun updateShopListName(id: Int, inputName: String){
        val name = parseName(inputName)
        val fieldsValid = validateInput(name)
        if (fieldsValid) {
            scope.launch {
                updateShopListNameUseCase(id, name)
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

//        allListsWithoutItems.value?.let {list ->
//            if (list.map { it.name }.contains(name)) {
//                val id = list.find { it.name == name }?.id
//                if (id != _shopList.value?.id) {
        val names = allListsWithoutItems.value?.map { it.name }
        Log.d("validateInput", "names = $names")
        names?.let {
            if (names.contains(name)) {
                val id = allListsWithoutItems.value?.find { it.name == name }?.id
                if (id != shopListId) {
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
}