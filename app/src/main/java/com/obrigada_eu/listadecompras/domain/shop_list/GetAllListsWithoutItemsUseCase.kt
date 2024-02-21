package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class GetAllListsWithoutItemsUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(): List<ShopList> = shopListRepository.getAllListsWithoutItems()
}