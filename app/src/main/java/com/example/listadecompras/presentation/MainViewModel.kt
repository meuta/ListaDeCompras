package com.example.listadecompras.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.listadecompras.data.ShopListRepositoryImpl
import com.example.listadecompras.domain.DeleteShopItemUseCase
import com.example.listadecompras.domain.EditShopItemUseCase
import com.example.listadecompras.domain.GetShopListUseCase
import com.example.listadecompras.domain.ShopItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application)  {

    private val repository = ShopListRepositoryImpl(application)     // !!! Not correct. Need to use Dependency Injection.

    private val scope = CoroutineScope(Dispatchers.IO)

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
//        deleteShopItemUseCase.deleteShopItem(shopItem)
        scope.launch {
            deleteShopItemUseCase.deleteShopItem(shopItem)
        }
    }

    fun changeEnableState(shopItem: ShopItem){
//        val newItem = shopItem.copy(enabled = !shopItem.enabled)           // Can use copy, because DataClass
//        editShopItemUseCase.editShopItem(newItem)
        scope.launch {
            val newItem = shopItem.copy(enabled = !shopItem.enabled)
            editShopItemUseCase.editShopItem(newItem)
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

}