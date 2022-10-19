package com.example.listadecompras.domain

class GetShopItemUseCase(private val shopListRepository: ShopListRepository) {

    suspend fun getShopItemById(itemId: Int): ShopItem{
        return shopListRepository.getShopItem(itemId)
    }

}