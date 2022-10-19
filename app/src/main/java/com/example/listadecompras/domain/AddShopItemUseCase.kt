package com.example.listadecompras.domain

class AddShopItemUseCase(private val shopListRepository: ShopListRepository) {

    suspend fun addItemToShopList(shopItem: ShopItem){
        shopListRepository.addShopItem(shopItem)
    }

}