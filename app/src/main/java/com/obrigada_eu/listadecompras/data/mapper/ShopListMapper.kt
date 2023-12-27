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

    fun mapEntityToDbModel(shopItem: ShopItem) = ShopItemDbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        enabled = shopItem.enabled,
        shopListId = shopItem.shopListId
    )

    fun mapDbModelToEntity(shopItemDbModel: ShopItemDbModel) = ShopItem(
        id = shopItemDbModel.id,
        name = shopItemDbModel.name,
        count = shopItemDbModel.count,
        enabled = shopItemDbModel.enabled,
        shopListId = shopItemDbModel.shopListId
    )

    fun mapListDbModelToEntity(list: List<ShopItemDbModel>) = list.map { mapDbModelToEntity(it) }


    fun mapShopListDbModelToEntity(
        shopListDbModel: ShopListDbModel
    ) : ShopList = ShopList(
        shopListDbModel.name,
        shopListDbModel.id
    )


    fun mapShopListWithItemsDbModelToShopList(
        shopListWithShopItemsDbModel: ShopListWithShopItemsDbModel
    ) : ShopListWithItems = ShopListWithItems(
        shopListWithShopItemsDbModel.shopListDbModel.name,
        shopListWithShopItemsDbModel.shopList.map { mapDbModelToEntity(it) },
        shopListWithShopItemsDbModel.shopListDbModel.id
    )

}