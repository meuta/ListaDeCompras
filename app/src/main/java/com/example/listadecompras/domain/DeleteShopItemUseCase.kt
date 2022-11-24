package com.example.listadecompras.domain

import javax.inject.Inject

//class DeleteShopItemUseCase(private val shopListRepository: ShopListRepository) {
class DeleteShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend fun deleteShopItem(shopItem: ShopItem){
        shopListRepository.deleteShopItem(shopItem)
    }

}