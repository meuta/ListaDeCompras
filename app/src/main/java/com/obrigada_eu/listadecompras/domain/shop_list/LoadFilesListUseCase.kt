package com.obrigada_eu.listadecompras.domain.shop_list

import javax.inject.Inject

class LoadFilesListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(): List<String>? = shopListRepository.loadFilesList()
}