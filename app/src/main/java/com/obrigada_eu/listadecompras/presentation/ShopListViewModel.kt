package com.obrigada_eu.listadecompras.presentation

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
    private val getShopListUseCase: GetShopListUseCase,
    private val deleteShopItemUseCase: DeleteShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
    private val dragShopItemUseCase: DragShopItemUseCase,
    private val getShopListNameUseCase: GetShopListNameUseCase
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _shopListName = MutableLiveData<String>()
    val shopListName: LiveData<String>
        get() = _shopListName

    private var _shopList = getShopListUseCase(0).asLiveData()
    val shopList: LiveData<List<ShopItem>>
        get() = _shopList

    fun getShopList(listId: Int) {
        scope.launch {
            _shopList = getShopListUseCase(listId).asLiveData()
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
}