package com.obrigada_eu.listadecompras.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(listId: Int): Flow<List<ShopItem>> = shopListRepository.getShopList(listId)
}