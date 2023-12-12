package com.obrigada_eu.listadecompras.domain

import javax.inject.Inject

class AddShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(name: String) = shopListRepository.addShopList(name)
}