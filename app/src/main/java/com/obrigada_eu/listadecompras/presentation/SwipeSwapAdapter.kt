package com.obrigada_eu.listadecompras.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.obrigada_eu.listadecompras.databinding.ItemListDisabledBinding
import com.obrigada_eu.listadecompras.databinding.ItemListEnabledBinding
import com.obrigada_eu.listadecompras.databinding.ItemShopDisabledBinding
import com.obrigada_eu.listadecompras.databinding.ItemShopEnabledBinding
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.list_set.ListItemViewHolder

abstract class SwipeSwapAdapter<T>(diff: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, ListItemViewHolder>(diff) {

    var onItemClickListener: ((T) -> Unit)? = null

    abstract fun getLayoutEnabled(): Int
    abstract fun getLayoutDisabled(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> getLayoutEnabled()
            VIEW_TYPE_DISABLED -> getLayoutDisabled()
            else -> throw RuntimeException("Unknown viewType: $viewType")
        }

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return ListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {

        val item = getItem(position)
        val binding = holder.binding

        binding.root.setOnClickListener {
            onItemClickListener?.invoke(item)
        }

        when (binding) {
            is ItemListDisabledBinding -> binding.listItem = item as ShopList
            is ItemListEnabledBinding -> binding.listItem = item as ShopList
            is ItemShopDisabledBinding -> binding.shopItem = item as ShopItem
            is ItemShopEnabledBinding -> binding.shopItem = item as ShopItem
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is ShopItem -> if (item.enabled) VIEW_TYPE_ENABLED else VIEW_TYPE_DISABLED
            is ShopList -> if (item.enabled) VIEW_TYPE_ENABLED else VIEW_TYPE_DISABLED
            else -> throw RuntimeException("Unknown item type")
        }
    }


    companion object {
        const val VIEW_TYPE_ENABLED = 1
        const val VIEW_TYPE_DISABLED = 0

        const val MAX_POOL_SIZE = 15
    }
}