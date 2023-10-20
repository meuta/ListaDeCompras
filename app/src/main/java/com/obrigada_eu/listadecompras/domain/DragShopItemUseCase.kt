package com.obrigada_eu.listadecompras.domain

import javax.inject.Inject

class DragShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(from: Int, to: Int) = shopListRepository.dragShopItem(from, to)
}