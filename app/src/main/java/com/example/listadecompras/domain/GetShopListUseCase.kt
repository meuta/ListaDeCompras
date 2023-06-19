package com.example.listadecompras.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(): Flow<List<ShopItem>> = shopListRepository.getShopList()

}