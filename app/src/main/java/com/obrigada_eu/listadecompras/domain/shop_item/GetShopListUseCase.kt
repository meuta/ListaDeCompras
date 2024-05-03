package com.obrigada_eu.listadecompras.domain.shop_item

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopListUseCase @Inject constructor(private val shopItemRepository: ShopItemRepository) {

    operator fun invoke(listId: Int): Flow<List<ShopItem>> = shopItemRepository.getShopList(listId)
}