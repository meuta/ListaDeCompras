package com.example.listadecompras.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecompras.domain.AddShopItemUseCase
import com.example.listadecompras.domain.DeleteShopItemUseCase
import com.example.listadecompras.domain.DragShopItemUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopItemUseCase
import com.example.listadecompras.domain.GetShopListUseCase
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.shop_list_screen.ShopListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleViewModel @Inject constructor(
    private val getShopItemByIdUseCase: GetShopItemUseCase,
    private val addItemToShopListUseCase: AddShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
    private val getShopListUseCase: GetShopListUseCase,
    private val deleteShopItemUseCase: DeleteShopItemUseCase,
    private val dragShopItemUseCase: DragShopItemUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(ShopListState())
    val state: State<ShopListState> = _state

    private var getShopListJob: Job? = null
    var shopItemEditName by mutableStateOf("")
        private set
    var shopItemEditCount by mutableStateOf("")
        private set

    var showErrorName by mutableStateOf(false)
        private set
    var showErrorCount by mutableStateOf(false)
        private set

    private var shopItem: ShopItem? = null
    private var currentItemId: Int? = null

    var finish = false

    var saveClick: () -> Unit = {}

    init {
        getShopList()
    }

    private fun getShopList() {
        getShopListJob?.cancel()
        getShopListJob = getShopListUseCase()
            .onEach { shopList ->
                _state.value = state.value.copy(
                    shopList = shopList
                )
            }
            .launchIn(viewModelScope)
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

    init {
        savedStateHandle.get<Int>("itemId")?.let { itemId ->
            if (itemId != -1) {
                getShopItem(itemId)
            } else {
                getZeroItem()
            }
        }
    }

    private fun getShopItem(id: Int) {
        viewModelScope.launch {
            if (id != currentItemId) {
                val item = getShopItemByIdUseCase(id)
                item?.let{
                    shopItem = it
                    shopItemEditName = it.name
                    shopItemEditCount = it.count.toString()

                    currentItemId = id
                }
            }
        }
    }

    private fun getZeroItem() {

        if (currentItemId == null) {
            currentItemId = ShopItem.UNDEFINED_ID
            shopItemEditName = ""
            shopItemEditCount = "1.0"
        }
    }

    fun onNameChanged(newName: String) {
        Log.d("ShopItemViewModel", "shopItemName.value = $newName")

        shopItemEditName = newName
        resetErrorInputName()
    }

    fun onCountChanged(newCount: String) {
        Log.d("ShopItemViewModel", "shopItemCount.value = $newCount")

        shopItemEditCount = newCount
        resetErrorInputCount()
    }

    fun onSaveClick() {
        saveClick()
    }

    fun addShopItem() {
        val name = parseName(shopItemEditName)
        val count = parseCount(shopItemEditCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val item = ShopItem(name, count, true)
            viewModelScope.launch {
                addItemToShopListUseCase(item)
            }
        }
    }

    fun editShopItem() {
        val name = parseName(shopItemEditName)
        val count = parseCount(shopItemEditCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            shopItem?.let {
                val item = it.copy(name = name, count = count)
                viewModelScope.launch {
                    editShopItemUseCase(item)
                }
            }
        }
    }

    private fun validateInput(name: String, count: Double): Boolean {
        finish = false
        var result = true
        if (name.isBlank()) {
            showErrorName = true
            result = false
        }
        if (count < 0) {
            showErrorCount = true
            result = false
        }
        finish = result
        return result
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Double {
        return try {
            inputCount?.trim()?.toDouble() ?: -1.0
        } catch (e: Exception) {
            -1.0
        }
    }

    private fun resetErrorInputName() {
        showErrorName = false
    }

    private fun resetErrorInputCount() {
        showErrorCount = false
    }

}