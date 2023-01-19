package com.example.listadecompras.domain

import javax.inject.Inject

class DragShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend fun dragShopItem(from: Int, to: Int){
        shopListRepository.dragShopItem(from, to)
    }

}