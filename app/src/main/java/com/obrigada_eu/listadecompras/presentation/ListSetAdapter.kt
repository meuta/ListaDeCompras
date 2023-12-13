package com.obrigada_eu.listadecompras.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ListItemBinding
import com.obrigada_eu.listadecompras.domain.ShopList

class ListSetAdapter : ListAdapter<ShopList, ListItemViewHolder>(ListItemDiffCallback()) {

    var onListItemClickListener: ((ShopList) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val layout = R.layout.list_item

        val binding =
            DataBindingUtil.inflate<ListItemBinding>(
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

        binding.listItem = shopList
    }
}





