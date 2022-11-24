package com.example.listadecompras.domain

import javax.inject.Inject

//class GetShopItemUseCase(private val shopListRepository: ShopListRepository) {
class GetShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend fun getShopItemById(itemId: Int): ShopItem{
        return shopListRepository.getShopItem(itemId)
    }

}