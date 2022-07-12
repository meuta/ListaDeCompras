package com.example.listadecompras.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem

class ShopListAdapter : RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {

    var shopList = listOf<ShopItem>()
        set(value) {
            field = value
            notifyDataSetChanged()          // For updating of list when the value is set
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_shop_disabled,
            parent,
            false
        )
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val shopItem = shopList[position]

        val status = if (shopItem.enabled){
            "active"
        } else {
            "not active"
        }

        holder.view.setOnClickListener {
            true
        }

        if (shopItem.enabled){
            holder.tvName.setTextColor(ContextCompat.getColor(holder.view.context, android.R.color.holo_red_light))

            holder.tvName.text = "${shopItem.name}. $status"
            holder.tvCount.text = shopItem.count.toString()
        } else {
            holder.tvName.setTextColor(ContextCompat.getColor(holder.view.context, android.R.color.white))
            holder.tvName.text = ""
            holder.tvCount.text = ""
        }
    }
/*
    override fun onViewRecycled(holder: ShopItemViewHolder) {
        super.onViewRecycled(holder)
        holder.tvName.setTextColor(ContextCompat.getColor(holder.view.context, android.R.color.white))
        holder.tvName.text = ""
        holder.tvCount.text = ""
    }
*/
    override fun getItemCount(): Int {
        return shopList.size
    }
/*
    override fun getItemViewType(position: Int): Int {
        return position
    }
*/
    class ShopItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvCount = view.findViewById<TextView>(R.id.tv_count)
    }
}