package com.obrigada_eu.listadecompras.domain

import javax.inject.Inject

class AddShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(shopItem: ShopItem) = shopListRepository.addShopItem(shopItem)
}