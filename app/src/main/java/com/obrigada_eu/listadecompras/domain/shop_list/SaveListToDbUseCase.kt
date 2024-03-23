package com.obrigada_eu.listadecompras.domain.shop_list

import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import javax.inject.Inject

class SaveListToDbUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(fileName: String, newFileName: String? = null, listEnabled : Boolean, list: List<ShopItem>): Boolean =
        shopListRepository.saveListToDb(fileName, newFileName, listEnabled, list)
}