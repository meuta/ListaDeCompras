package com.obrigada_eu.listadecompras.domain.shop_list

import android.net.Uri
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import javax.inject.Inject

class GetListFromTxtFileUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(fileName: String, myFilePath: String? = null, uri: Uri? = null): Pair<ShopList?, List<ShopItem>> =
        shopListRepository.getListFromTxtFile(fileName, myFilePath, uri)
}