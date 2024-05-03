package com.obrigada_eu.listadecompras.presentation.list_set

import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.SwipeSwapAdapter

class ListSetAdapter : SwipeSwapAdapter<ShopList>(ListItemDiffCallback()) {

    override fun getLayoutEnabled(): Int = R.layout.item_list_enabled
    override fun getLayoutDisabled(): Int = R.layout.item_list_disabled
}





