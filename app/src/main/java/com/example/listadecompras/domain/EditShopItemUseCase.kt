package com.example.listadecompras.domain

import javax.inject.Inject

//class EditShopItemUseCase(private val shopListRepository: ShopListRepository) {
class EditShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend fun editShopItem(shopItem: ShopItem){
        shopListRepository.editShopItem(shopItem)
    }

}