package com.obrigada_eu.listadecompras.domain.shop_list

import android.content.Intent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShareTxtListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(listId: Int) : Flow<Intent> = shopListRepository.shareTxtList(listId)
}