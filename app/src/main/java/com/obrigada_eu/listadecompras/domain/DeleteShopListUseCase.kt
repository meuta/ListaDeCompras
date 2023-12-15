package com.obrigada_eu.listadecompras.domain

import javax.inject.Inject

class DeleteShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(id: Int) = shopListRepository.deleteShopList(id)
}