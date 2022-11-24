package com.example.listadecompras.domain

import androidx.lifecycle.LiveData
import javax.inject.Inject

//class GetShopListUseCase(private val shopListRepository: ShopListRepository) {
class GetShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    fun getShopList(): LiveData<List<ShopItem>>{
        return shopListRepository.getShopList()
    }
}