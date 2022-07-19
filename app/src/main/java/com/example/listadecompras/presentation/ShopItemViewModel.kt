package com.example.listadecompras.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.listadecompras.data.ShopListRepositoryImpl
import com.example.listadecompras.domain.AddItemToShopListUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopItemByIdUseCase
import com.example.listadecompras.domain.ShopItem

class ShopItemViewModel() : ViewModel() {

    private val repository = ShopListRepositoryImpl     // Not correct. Need to use Dependency Injection.

    private val getShopItemByIdUseCase = GetShopItemByIdUseCase(repository)
    private val addItemToShopListUseCase = AddItemToShopListUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)


    private var _errorInputName =
        MutableLiveData<Boolean>()        //Can work with this only from ViewModel and can set a value into
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName


    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    fun getShopItem(itemId: Int) {
        val item = getShopItemByIdUseCase.getShopItemById(itemId)
        _shopItem.value = item
    }

    private var _errorInputCount =
        MutableLiveData<Boolean>()        //Can work with this only from ViewModel and can set a value into
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _closeScreen =
        MutableLiveData<Unit>()          //Unit because we only set it to true
    val closeScreen: LiveData<Unit>
        get() = _closeScreen

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            addItemToShopListUseCase.addItemToShopList(shopItem)
            finishScreen()
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _shopItem.value?.let {
                val item = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(item)
                finishScreen()
            }
        }
    }

    private fun finishScreen() {
        _closeScreen.value = Unit
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

    private fun validateInput(name: String, count: Double): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true     //Cannot assign to 'value': the setter is protected/*protected and package*/ for synthetic extension in '<library Gradle: androidx.lifecycle:lifecycle-livedata-core:2.3.1@aar>'
            result = false
        }
        if (count < 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false        //Cannot assign to 'value': the setter is protected/*protected and package*/ for synthetic extension in '<library Gradle: androidx.lifecycle:lifecycle-livedata-core:2.3.1@aar>'
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false        //Cannot assign to 'value': the setter is protected/*protected and package*/ for synthetic extension in '<library Gradle: androidx.lifecycle:lifecycle-livedata-core:2.3.1@aar>'
    }

}