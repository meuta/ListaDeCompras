package com.obrigada_eu.listadecompras.domain.shop_item

import javax.inject.Inject

class UndoDeleteUseCase @Inject constructor(private val shopItemRepository: ShopItemRepository) {

    suspend operator fun invoke() = shopItemRepository.undoDelete()
}