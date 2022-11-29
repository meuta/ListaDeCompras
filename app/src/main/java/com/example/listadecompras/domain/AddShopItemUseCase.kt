package com.example.listadecompras.domain

import javax.inject.Inject

class AddShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend fun addItemToShopList(shopItem: ShopItem){
        shopListRepository.addShopItem(shopItem)
    }

}