package com.obrigada_eu.listadecompras.presentation.shop_list

import androidx.recyclerview.widget.DiffUtil
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem

class ShopItemDiffCallback: DiffUtil.ItemCallback<ShopItem>() {
    override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        return oldItem == newItem
    }

}