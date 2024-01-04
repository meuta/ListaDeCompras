package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class DragShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(from: Int, to: Int) = shopListRepository.dragShopList(from, to)
}