package com.obrigada_eu.listadecompras.data

import com.obrigada_eu.listadecompras.domain.ShopItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListMapper @Inject constructor() {

    fun mapEntityToDbModel(shopItem: ShopItem) = ShopItemBbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        enabled = shopItem.enabled
    )

    fun mapDbModelToEntity(shopItemDbModel: ShopItemBbModel) = ShopItem(
        id = shopItemDbModel.id,
        name = shopItemDbModel.name,
        count = shopItemDbModel.count,
        enabled = shopItemDbModel.enabled
    )

    fun mapListDbModelToEntity(list: List<ShopItemBbModel>) = list.map { mapDbModelToEntity(it) }

}