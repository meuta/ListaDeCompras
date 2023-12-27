package com.obrigada_eu.listadecompras.presentation.list_set

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ItemListDisabledBinding
import com.obrigada_eu.listadecompras.databinding.ItemListEnabledBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList

class ListSetAdapter : ListAdapter<ShopList, ListItemViewHolder>(ListItemDiffCallback()) {

    var onListItemClickListener: ((ShopList) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_list_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_list_disabled
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

        val shopList = getItem(position)
        val binding = holder.binding

        binding.root.setOnClickListener {
            onListItemClickListener?.invoke(shopList)
        }

        when (binding) {
            is ItemListDisabledBinding -> {
                binding.listItem = shopList
            }

            is ItemListEnabledBinding -> {
                binding.listItem = shopList
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).enabled) VIEW_TYPE_ENABLED else VIEW_TYPE_DISABLED
    }


    companion object {
        const val VIEW_TYPE_ENABLED = 1
        const val VIEW_TYPE_DISABLED = 0

        const val MAX_POOL_SIZE = 15
    }
}





