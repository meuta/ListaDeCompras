package com.obrigada_eu.listadecompras.data.datastore

import com.obrigada_eu.listadecompras.domain.shop_list.ShopList

data class ShopListPreferences(
    var shopListId: Int? = ShopList.UNDEFINED_ID

)
