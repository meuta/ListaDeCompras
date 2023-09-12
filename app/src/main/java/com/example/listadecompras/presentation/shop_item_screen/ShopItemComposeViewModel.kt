package com.example.listadecompras.presentation.shop_item_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecompras.domain.AddShopItemUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopItemUseCase
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopItem.Companion.UNDEFINED_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopItemComposeViewModel @Inject constructor(
    private val getShopItemByIdUseCase: GetShopItemUseCase,
    private val addItemToShopListUseCase: AddShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase
) : ViewModel() {

//    private val _uiState = MutableStateFlow(ItemPaneState())
//    val uiState: StateFlow<ItemPaneState> = _uiState.asStateFlow()

//    var screenMode by mutableStateOf("")
//        private set

    var shopItemEditName by mutableStateOf("")
        private set
    var shopItemEditCount by mutableStateOf("")
        private set

    var itemId: Int? = null

    var showErrorName by mutableStateOf(false)
        private set
    var showErrorCount by mutableStateOf(false)
        private set

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem> = _shopItem

    var saveClick: () -> Unit = {}

    fun getShopItem(id: Int) {
        viewModelScope.launch {
            if (id != itemId) {
                val item = getShopItemByIdUseCase(id)
                _shopItem.value = item
                shopItemEditName = item.name
                shopItemEditCount = item.count.toString()

                itemId = id
//            _uiState.update { currentState ->
//                currentState.copy(name = shopItemEditName, count = shopItemEditCount)
//            }
            }
        }
    }

    fun getZeroItem() {

        if (itemId == null){
            itemId = UNDEFINED_ID
            shopItemEditName = ""
            shopItemEditCount = "1.0"
//        _uiState.update { currentState ->
//            currentState.copy(name = shopItemEditName, count = shopItemEditCount)
//        }
        }
    }

    fun onNameChanged(newName: String) {
        Log.d("ShopItemViewModel", "shopItemName.value = $newName")

        shopItemEditName = newName
//        showErrorName = false
        resetErrorInputName()
//        _uiState.update { currentState ->
//            currentState.copy(name = shopItemEditName, showErrorName = false)
//        }
    }

    fun onCountChanged(newCount: String) {
        Log.d("ShopItemViewModel", "shopItemCount.value = $newCount")

        shopItemEditCount = newCount
//        showErrorCount = false
        resetErrorInputCount()
//        _uiState.update { currentState ->
//            currentState.copy(count = newCount, showErrorCount = false)
//        }
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
                finishScreen()
            }
        }
    }

    fun editShopItem() {
        val name = parseName(shopItemEditName)
        val count = parseCount(shopItemEditCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _shopItem.value?.let {

                viewModelScope.launch {
                    val item = it.copy(name = name, count = count)
                    editShopItemUseCase(item)
                    finishScreen()
                }
            }
        }
    }

    private fun validateInput(name: String, count: Double): Boolean {
        var result = true
        if (name.isBlank()) {
            showErrorName = true
            result = false
        }
        if (count < 0) {
            showErrorCount = true
            result = false
        }
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

    private val _closeScreen = MutableLiveData<Unit>()
    val closeScreen: LiveData<Unit>
        get() = _closeScreen

    private fun finishScreen() {
        _closeScreen.value = Unit
    }

}