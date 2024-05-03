package com.obrigada_eu.listadecompras.presentation.shop_item

import android.util.Log
import androidx.lifecycle.*
import com.obrigada_eu.listadecompras.domain.shop_item.AddShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.EditShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.GetShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopItemViewModel @Inject constructor(
    private val getShopItemByIdUseCase: GetShopItemUseCase,
    private val addItemToShopListUseCase: AddShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
) : ViewModel() {

    private val _errorInputName = MutableStateFlow<Boolean>(false)
    val errorInputName: StateFlow<Boolean> = _errorInputName


    private val _shopItem = MutableStateFlow<ShopItem?>(null)
    val shopItem: StateFlow<ShopItem?> = _shopItem


    private val _errorInputCount = MutableStateFlow<Boolean>(false)
    val errorInputCount: StateFlow<Boolean> = _errorInputCount


    private val _closeScreen = MutableStateFlow<Unit?>(null)
    val closeScreen: StateFlow<Unit?> = _closeScreen


    fun getShopItem(itemId: Int) {
        viewModelScope.launch {
            val item = getShopItemByIdUseCase(itemId)
            item?.let { _shopItem.value = it }
        }
    }

    fun addShopItem(inputName: String?, inputCount: String?, inputUnits: String?, listId: Int) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val units = parseUnits(inputUnits)
        val fieldsValid = validateInput(name, count, units)
        if (fieldsValid) {

            viewModelScope.launch {
                val shopItem = ShopItem(name, count, units, true, shopListId = listId)
                addItemToShopListUseCase(shopItem)
                finishScreen()
            }
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?, inputUnits: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val units = parseUnits(inputUnits)
        val fieldsValid = validateInput(name, count, units)
        if (fieldsValid) {
            _shopItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(name = name, count = count, units = units)
                    editShopItemUseCase(item)
                    finishScreen()
                }
            }
        }
    }

    private fun finishScreen() {
        _closeScreen.value = Unit
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Double? {
        return inputCount?.trim()?.ifEmpty { null }?.toDouble()
    }

    private fun parseUnits(inputUnits: String?): String? {
        return inputUnits?.trim()?.ifEmpty { null }
    }

    private fun validateInput(name: String, count: Double?, units: String?): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (units != null && count == null) {
            _errorInputCount.value = true
            result = false
        }
        if (count != null && count <= 0){
            _errorInputCount.value = true
            result = false
        }

        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }



    companion object{
        private const val TAG = "ShopItemViewModel"
    }
}