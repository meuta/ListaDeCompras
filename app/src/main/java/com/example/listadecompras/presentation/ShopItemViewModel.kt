package com.example.listadecompras.presentation

import androidx.lifecycle.ViewModel
import com.example.listadecompras.data.ShopListRepositoryImpl
import com.example.listadecompras.domain.AddItemToShopListUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopItemByIdUseCase
import com.example.listadecompras.domain.ShopItem

class ShopItemViewModel(itemId: Int) : ViewModel() {

    private val repository =
        ShopListRepositoryImpl     // Not correct. Need to use Dependency Injection.

    private val getShopItemByIdUseCase = GetShopItemByIdUseCase(repository)
    private val addItemToShopListUseCase = AddItemToShopListUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)


    fun getShopItem(itemId: Int) {
        val item = getShopItemByIdUseCase.getShopItemById(itemId)
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            addItemToShopListUseCase.addItemToShopList(shopItem)
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            editShopItemUseCase.editShopItem(shopItem)
        }
    }

    private fun parseName(inputName: String?): String {
//        return when (inputName) {
//            null -> ""
//            else -> inputName.trim { it <= ' ' }
//        }
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Double {
//        return when (inputName) {
//            null -> ""
//            else -> inputName.trim { it <= ' ' }
//        }
//        return (inputName?.trim() ?: "").toDouble()
        return try {
//             (inputCount?.trim() ?: "").toDouble()
            inputCount?.trim()?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun validateInput(name: String, count: Double): Boolean{
        var result = true
        if (name.isBlank()){
            // TODO Show error inputName
            result = false
        }
        if (count < 0){
            // TODO Show error inputCount
            result = false
        }
        return result
    }

}