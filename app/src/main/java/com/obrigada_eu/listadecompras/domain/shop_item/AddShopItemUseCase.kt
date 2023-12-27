package com.obrigada_eu.listadecompras.domain.shop_item

import javax.inject.Inject

class AddShopItemUseCase @Inject constructor(private val shopItemRepository: ShopItemRepository) {

    suspend operator fun invoke(shopItem: ShopItem) = shopItemRepository.addShopItem(shopItem)
}