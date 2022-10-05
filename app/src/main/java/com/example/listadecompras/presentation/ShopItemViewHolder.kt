package com.example.listadecompras.presentation


import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

//class ShopItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
class ShopItemViewHolder(
//    val binding: ItemShopDisabledBinding
    val binding: ViewDataBinding                //parent class for both
) : RecyclerView.ViewHolder(binding.root) //{
//    val tvName = view.findViewById<TextView>(R.id.tv_name)
//    val tvCount = view.findViewById<TextView>(R.id.tv_count)
//}
