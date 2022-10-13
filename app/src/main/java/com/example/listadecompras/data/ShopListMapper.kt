package com.example.listadecompras.data

import com.example.listadecompras.domain.ShopItem

class ShopListMapper {

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

    fun mapListDbModelToEntity(list: List<ShopItemBbModel>) = list.map {mapDbModelToEntity(it)}

}