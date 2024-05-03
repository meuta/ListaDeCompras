package com.obrigada_eu.listadecompras.domain.shop_item

import javax.inject.Inject

class DragShopItemUseCase @Inject constructor(private val shopItemRepository: ShopItemRepository) {

    suspend operator fun invoke(from: Int, to: Int) = shopItemRepository.dragShopItem(from, to)
}