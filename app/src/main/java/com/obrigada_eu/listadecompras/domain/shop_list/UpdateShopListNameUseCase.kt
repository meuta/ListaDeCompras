package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class UpdateShopListNameUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(id: Int, name: String) = shopListRepository.updateListName(id, name)
}