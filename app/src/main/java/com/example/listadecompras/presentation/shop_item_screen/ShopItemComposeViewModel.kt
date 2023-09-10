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

    private val _uiState = MutableStateFlow(ItemPaneState())
    val uiState: StateFlow<ItemPaneState> = _uiState.asStateFlow()

    var screenMode by mutableStateOf("")
        private set

    var shopItemEditName by mutableStateOf("")
        private set

    var showError by mutableStateOf(false)
        private set

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem> = _shopItem

    var saveClick : () -> Unit = {}

//    fun screenModeUpdate(mode: String) {
//        _uiState.update { currentState ->
//            currentState.copy(screenMode = mode)
//        }
//    }

    fun getShopItem(itemId: Int) {
        viewModelScope.launch {
            val item = getShopItemByIdUseCase(itemId)
            _shopItem.value = item
            shopItemEditName = item.name
        }
    }

    fun onNameChanged(newName: String) {
        Log.d("ShopItemViewModel", "shopItemName.value = $newName")

        shopItemEditName = newName
        showError = false
        _uiState.update { currentState ->
            currentState.copy(name = shopItemEditName, showError = showError)
        }
    }

    fun onSaveClick() {
        saveClick()
        showError = !_uiState.value.isValidName
        _uiState.update { currentState ->
            currentState.copy(showError = showError)
        }
    }

    fun addShopItem() {
        val name = parseName(shopItemEditName)
        val fieldsValid = validateInput(name)
        if (fieldsValid) {
            val item = ShopItem(name, 4.2, true)

            viewModelScope.launch {
                addItemToShopListUseCase(item)
                finishScreen()
            }
        }
    }

        fun editShopItem() {
        val name = parseName(shopItemEditName)
        val fieldsValid = validateInput(name)
        if (fieldsValid) {
            _shopItem.value?.let {

                viewModelScope.launch {
                    val item = it.copy(name = name)
                    editShopItemUseCase(item)
                    finishScreen()
                }
            }
        }
    }

    private fun validateInput(name: String): Boolean {
        var result = true
        if (name.isBlank()) {
            showError = true
            result = false
        }
        return result
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }


    private val _closeScreen = MutableLiveData<Unit>()
    val closeScreen: LiveData<Unit>
        get() = _closeScreen

    private fun finishScreen() {
        _closeScreen.value = Unit
    }

}