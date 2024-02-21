package com.obrigada_eu.listadecompras.domain.shop_list

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllListsWithoutItemsFlowUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(): Flow<List<ShopList>> = shopListRepository.getAllListsWithoutItemsFlow()
}