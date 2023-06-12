package com.example.listadecompras.domain

import androidx.lifecycle.LiveData
import javax.inject.Inject

class GetShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    operator fun invoke(): LiveData<List<ShopItem>> = shopListRepository.getShopList()

}