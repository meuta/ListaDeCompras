package com.obrigada_eu.listadecompras.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopListWithItemsUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(listId: Int): Flow<ShopList> = shopListRepository.getShopListWithItems(listId)
}