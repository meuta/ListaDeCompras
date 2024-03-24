package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class SaveListToDbUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(shopListWithItems: ShopListWithItems): Boolean =
        shopListRepository.saveListToDb(shopListWithItems)
}