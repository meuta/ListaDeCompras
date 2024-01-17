package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class LoadFromTxtFileUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(fileName: String) = shopListRepository.loadFromTxtFile(fileName)
}