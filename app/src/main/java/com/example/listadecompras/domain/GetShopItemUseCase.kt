package com.example.listadecompras.domain

import javax.inject.Inject

class GetShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(itemId: Int): ShopItem? = shopListRepository.getShopItem(itemId)


}