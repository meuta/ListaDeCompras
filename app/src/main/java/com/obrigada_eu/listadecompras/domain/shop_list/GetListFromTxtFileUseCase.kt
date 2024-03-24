package com.obrigada_eu.listadecompras.domain.shop_list

import android.net.Uri
import javax.inject.Inject

class GetListFromTxtFileUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(fileName: String, myFilePath: String? = null, uri: Uri? = null): ShopListWithItems? =
        shopListRepository.getListFromTxtFile(fileName, myFilePath, uri)
}