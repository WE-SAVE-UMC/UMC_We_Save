package com.example.we_save.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.we_save.Item
import com.example.we_save.databinding.ItemMainRvBinding

class MainRecyclerAdapter (private val items: ArrayList<Item>) : RecyclerView.Adapter<MainRecyclerAdapter.ItemViewHolder>() {

    class ItemViewHolder( val binding: ItemMainRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.imageView.setImageResource(item.ImageRes)
            binding.textView.text = item.text
            binding.distanceTv.text = item.distamce
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemMainRvBinding =
            ItemMainRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}