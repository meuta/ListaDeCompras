package com.obrigada_eu.listadecompras.data

import com.obrigada_eu.listadecompras.domain.ShopItem
import com.obrigada_eu.listadecompras.domain.ShopList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListMapper @Inject constructor() {

    fun mapEntityToDbModel(shopItem: ShopItem) = ShopItemDbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        enabled = shopItem.enabled,
        shopListId = -2
    )

    fun mapDbModelToEntity(shopItemDbModel: ShopItemDbModel) = ShopItem(
        id = shopItemDbModel.id,
        name = shopItemDbModel.name,
        count = shopItemDbModel.count,
        enabled = shopItemDbModel.enabled,
        shopListId = shopItemDbModel.shopListId
    )

    fun mapListDbModelToEntity(list: List<ShopItemDbModel>) = list.map { mapDbModelToEntity(it) }


    fun mapShopListWithItemsDbModelToEntityList(
        shopListWithShopItemsDbModel: ShopListWithShopItemsDbModel
    ) : ShopList = ShopList(
        shopListWithShopItemsDbModel.shopListDbModel.name,
        shopListWithShopItemsDbModel.shopListDbModel.id,
        shopListWithShopItemsDbModel.shopList.map { mapDbModelToEntity(it) }
    )
}