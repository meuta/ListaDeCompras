package com.example.listadecompras.domain

import javax.inject.Inject

class EditShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(shopItem: ShopItem) = shopListRepository.editShopItem(shopItem)


}