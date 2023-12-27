package com.obrigada_eu.listadecompras.domain.shop_item

import javax.inject.Inject

class GetShopItemUseCase @Inject constructor(private val shopItemRepository: ShopItemRepository) {

    suspend operator fun invoke(itemId: Int): ShopItem? = shopItemRepository.getShopItem(itemId)
}