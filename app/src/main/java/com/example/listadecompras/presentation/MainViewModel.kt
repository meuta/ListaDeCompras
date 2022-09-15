package com.example.listadecompras.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.listadecompras.data.ShopListRepositoryImpl
import com.example.listadecompras.domain.DeleteShopItemUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopListUseCase
import com.example.listadecompras.domain.ShopItem

class MainViewModel: ViewModel()  {        // If need context => : AndroidViewModel()

    private val repository = ShopListRepositoryImpl     // !!! Not correct. Need to use Dependency Injection.

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun changeEnableState(shopItem: ShopItem){
        val newItem = shopItem.copy(enabled = !shopItem.enabled)           // Can use copy, because DataClass
        editShopItemUseCase.editShopItem(newItem)
    }

}