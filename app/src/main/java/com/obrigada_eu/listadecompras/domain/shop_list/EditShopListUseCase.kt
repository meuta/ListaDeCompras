package com.obrigada_eu.listadecompras.domain.shop_list


import javax.inject.Inject

class EditShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(shopList: ShopList) = shopListRepository.editShopList(shopList)
}