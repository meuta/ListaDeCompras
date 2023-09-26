package com.example.listadecompras.presentation.shop_item_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecompras.domain.AddShopItemUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopItemUseCase
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.domain.ShopItem.Companion.UNDEFINED_ID
import com.example.listadecompras.presentation.shop_item_screen.components.ShopItemScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopItemViewModel @Inject constructor(
    private val getShopItemByIdUseCase: GetShopItemUseCase,
    private val addItemToShopListUseCase: AddShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

//    private val _uiState = MutableStateFlow(ItemPaneState())
//    val uiState: StateFlow<ItemPaneState> = _uiState.asStateFlow()

//    var screenMode by mutableStateOf("")
//        private set

    private val _state = mutableStateOf(ShopItemScreenState())
    val state: State<ShopItemScreenState> = _state

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

        savedStateHandle.get<Int>("itemId")?.let { itemId ->
            _state.value = state.value.copy(
                itemId = itemId
            )
            if (itemId != 0) {
//                viewModelScope.launch {
//                    itemUseCases.getNote(itemId)?.also { item ->
//                        currentNoteId = item.id
//                        _itemTitle.value = itemTitle.value.copy(
//                            text = item.title,
//                            isHintVisible = false
//                        )
//                        _itemContent.value = _itemContent.value.copy(
//                            text = item.content,
//                            isHintVisible = false
//                        )
//                    }
                getShopItem(itemId)
//                }
            } else {
                getZeroItem()
            }
        }
        savedStateHandle.get<String>("screenMode")?.let { mode ->
            _state.value = state.value.copy(
                screenMode = mode
            )

        }

    }

    fun getShopItem(id: Int) {
        viewModelScope.launch {
            if (id != currentItemId) {
                val item = getShopItemByIdUseCase(id)
//                _shopItem.value = item
                item?.let {
                    shopItem = it
                    shopItemEditName = it.name
                    shopItemEditCount = it.count.toString()

                    currentItemId = id
                }
//            _uiState.update { currentState ->
//                currentState.copy(name = shopItemEditName, count = shopItemEditCount)
//            }
            }
        }
    }

    fun getZeroItem() {

        if (currentItemId == null) {
            currentItemId = UNDEFINED_ID
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
        resetErrorInputName()
//        _uiState.update { currentState ->
//            currentState.copy(name = shopItemEditName, showErrorName = false)
//        }
    }

    fun onCountChanged(newCount: String) {
        Log.d("ShopItemViewModel", "shopItemCount.value = $newCount")

        shopItemEditCount = newCount
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