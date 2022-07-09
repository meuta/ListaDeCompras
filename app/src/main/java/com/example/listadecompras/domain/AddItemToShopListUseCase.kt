package com.example.listadecompras.domain

class AddItemToShopListUseCase(private val shopListRepository: ShopListRepository) {

    fun addItemToShopList(shopItem: ShopItem){
        shopListRepository.addItemToShopList(shopItem)
    }

}