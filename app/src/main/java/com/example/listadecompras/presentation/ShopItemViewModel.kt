package com.example.listadecompras.presentation

import android.app.Application
import androidx.lifecycle.*
import com.example.listadecompras.data.ShopListRepositoryImpl
import com.example.listadecompras.domain.AddShopItemUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopItemUseCase
import com.example.listadecompras.domain.ShopItem
import kotlinx.coroutines.launch
import javax.inject.Inject

//class ShopItemViewModel(application: Application) : AndroidViewModel(application) {
class ShopItemViewModel @Inject constructor(
    private val getShopItemByIdUseCase: GetShopItemUseCase,
    private val addItemToShopListUseCase: AddShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase
) : ViewModel() {

//    private val repository = ShopListRepositoryImpl(application)     // Not correct. Need to use Dependency Injection.
//
//    private val getShopItemByIdUseCase = GetShopItemUseCase(repository)
//    private val addItemToShopListUseCase = AddShopItemUseCase(repository)
//    private val editShopItemUseCase = EditShopItemUseCase(repository)

    private var _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName


    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    fun getShopItem(itemId: Int) {
        viewModelScope.launch {
            val item = getShopItemByIdUseCase.getShopItemById(itemId)
            _shopItem.value = item
        }
    }

    private var _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _closeScreen = MutableLiveData<Unit>()
    val closeScreen: LiveData<Unit>
        get() = _closeScreen

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {

            viewModelScope.launch {
                val shopItem = ShopItem(name, count, true)
                addItemToShopListUseCase.addItemToShopList(shopItem)
                finishScreen()
            }

        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _shopItem.value?.let {

                viewModelScope.launch {
                    val item = it.copy(name = name, count = count)
                    editShopItemUseCase.editShopItem(item)
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
            _errorInputName.value = true
            result = false
        }
        if (count < 0) {
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

}