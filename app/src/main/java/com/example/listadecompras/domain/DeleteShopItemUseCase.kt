package com.example.listadecompras.domain

import javax.inject.Inject

class DeleteShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(shopItem: ShopItem) = shopListRepository.deleteShopItem(shopItem)


}