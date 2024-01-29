package com.obrigada_eu.listadecompras.data.mapper

import com.obrigada_eu.listadecompras.data.model.ShopItemDbModel
import com.obrigada_eu.listadecompras.data.model.ShopListDbModel
import com.obrigada_eu.listadecompras.data.model.ShopListWithShopItemsDbModel
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListWithItems
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListMapper @Inject constructor() {

    fun mapShopItemEntityToDbModel(shopItem: ShopItem) = ShopItemDbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        units = shopItem.units,
        enabled = shopItem.enabled,
        shopListId = shopItem.shopListId
    )

    fun mapShopItemDbModelToEntity(shopItemDbModel: ShopItemDbModel) = ShopItem(
        id = shopItemDbModel.id,
        name = shopItemDbModel.name,
        count = shopItemDbModel.count,
        units = shopItemDbModel.units,
        enabled = shopItemDbModel.enabled,
        shopListId = shopItemDbModel.shopListId
    )

    fun mapListDbModelToEntity(list: List<ShopItemDbModel>) = list.map { mapShopItemDbModelToEntity(it) }

    fun mapListSetToEntity(set: List<ShopListDbModel>) = set.map { mapShopListDbModelToEntity(it) }

    fun mapShopListEntityToDbModel(shopList: ShopList) = ShopListDbModel(
        id = shopList.id,
        name = shopList.name,
        enabled = shopList.enabled,
    )

    fun mapShopListDbModelToEntity(
        shopListDbModel: ShopListDbModel
    ) : ShopList = ShopList(
        shopListDbModel.name,
        shopListDbModel.enabled,
        shopListDbModel.id
    )


    fun mapShopListWithItemsDbModelToShopList(
        shopListWithShopItemsDbModel: ShopListWithShopItemsDbModel
    ) : ShopListWithItems = ShopListWithItems(
        shopListWithShopItemsDbModel.shopListDbModel.name,
        shopListWithShopItemsDbModel.shopList.map { mapShopItemDbModelToEntity(it) },
        shopListWithShopItemsDbModel.shopListDbModel.id
    )

}