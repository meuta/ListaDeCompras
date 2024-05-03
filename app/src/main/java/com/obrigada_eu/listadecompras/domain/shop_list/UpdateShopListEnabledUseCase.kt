package com.obrigada_eu.listadecompras.domain.shop_list


import javax.inject.Inject

class UpdateShopListEnabledUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend operator fun invoke(shopList: ShopList) = shopListRepository.updateListEnabled(shopList)
}