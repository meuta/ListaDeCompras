package com.obrigada_eu.listadecompras.presentation.shop_list

import androidx.recyclerview.widget.DiffUtil
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList

class ListItemDiffCallback : DiffUtil.ItemCallback<ShopList>() {
    override fun areItemsTheSame(oldItem: ShopList, newItem: ShopList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShopList, newItem: ShopList): Boolean {
        return oldItem == newItem
    }

}