package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class LoadTxtListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(listName: String) = shopListRepository.loadTxtList(listName)
}