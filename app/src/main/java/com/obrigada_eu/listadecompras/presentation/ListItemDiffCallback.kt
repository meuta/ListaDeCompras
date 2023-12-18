package com.obrigada_eu.listadecompras.presentation

import androidx.recyclerview.widget.DiffUtil
import com.obrigada_eu.listadecompras.domain.ShopListEntity

class ListItemDiffCallback : DiffUtil.ItemCallback<ShopListEntity>() {
    override fun areItemsTheSame(oldItem: ShopListEntity, newItem: ShopListEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShopListEntity, newItem: ShopListEntity): Boolean {
        return oldItem == newItem
    }

}