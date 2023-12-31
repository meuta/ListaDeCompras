package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class GetShopListNameUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(listId: Int): String = shopListRepository.getShopListName(listId)
}