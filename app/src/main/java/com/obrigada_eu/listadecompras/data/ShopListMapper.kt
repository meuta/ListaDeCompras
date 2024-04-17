package com.obrigada_eu.listadecompras.data


import com.obrigada_eu.listadecompras.data.database.ShopItemDbModel
import com.obrigada_eu.listadecompras.data.database.ShopListDbModel
import com.obrigada_eu.listadecompras.data.database.ShopListWithShopItemsDbModel
import com.obrigada_eu.listadecompras.domain.ShopItem
import com.obrigada_eu.listadecompras.domain.ShopList
import com.obrigada_eu.listadecompras.domain.ShopListWithItems
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopListMapper @Inject constructor() {

    fun mapShopItemEntityToDbModel(shopItem: ShopItem) = ShopItemDbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        enabled = shopItem.enabled,
        shopListId = shopItem.shopListId
    )

    fun mapShopItemDbModelToEntity(shopItemDbModel: ShopItemDbModel) = ShopItem(
        id = shopItemDbModel.id,
        name = shopItemDbModel.name,
        count = shopItemDbModel.count,
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
        shopListWithShopItemsDbModel.shopListDbModel.enabled,
        shopListWithShopItemsDbModel.shopList.map { mapShopItemDbModelToEntity(it) },
        shopListWithShopItemsDbModel.shopListDbModel.id
    )

}