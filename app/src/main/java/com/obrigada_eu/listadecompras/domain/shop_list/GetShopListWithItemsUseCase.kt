package com.obrigada_eu.listadecompras.domain.shop_list

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopListWithItemsUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(listId: Int): Flow<ShopListWithItems> = shopListRepository.getShopListWithItems(listId)
}