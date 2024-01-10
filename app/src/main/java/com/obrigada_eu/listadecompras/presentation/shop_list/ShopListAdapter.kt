package com.obrigada_eu.listadecompras.presentation.shop_list


import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.presentation.SwipeSwapAdapter

class ShopListAdapter : SwipeSwapAdapter<ShopItem>(ShopItemDiffCallback()) {

    override fun getLayoutEnabled(): Int = R.layout.item_shop_enabled
    override fun getLayoutDisabled(): Int = R.layout.item_shop_disabled

}