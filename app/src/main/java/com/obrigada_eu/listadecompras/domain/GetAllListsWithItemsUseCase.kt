package com.obrigada_eu.listadecompras.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllListsWithItemsUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(): Flow<List<ShopList>> = shopListRepository.getAllListsWithItems()
}