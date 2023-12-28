package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class ExportListToTxtUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(listId: Int) = shopListRepository.exportListToTxt(listId)
}