package com.obrigada_eu.listadecompras.domain.shop_list

import android.net.Uri
import javax.inject.Inject

class LoadFromTxtFileUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(fileName: String, newFileName: String? = null, myFilePath: String? = null, uri: Uri? = null): Boolean =
        shopListRepository.loadFromTxtFile(fileName, newFileName, myFilePath, uri)
}