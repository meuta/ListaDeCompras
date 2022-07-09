package com.example.listadecompras.domain

class GetShopItemByIdUseCase(private val shopListRepository: ShopListRepository) {

    fun getShopItemById(itemId: Int): ShopItem{
        return shopListRepository.getShopItemById(itemId)
    }

}