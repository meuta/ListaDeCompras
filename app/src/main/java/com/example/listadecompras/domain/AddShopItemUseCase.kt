package com.example.listadecompras.domain

class AddShopItemUseCase(private val shopListRepository: ShopListRepository) {

    fun addItemToShopList(shopItem: ShopItem){
        shopListRepository.addShopItem(shopItem)
    }

}