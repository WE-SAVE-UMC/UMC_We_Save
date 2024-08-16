package com.example.we_save.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.we_save.Item
import com.example.we_save.PostDTO
import com.example.we_save.databinding.ItemMainRvBinding

class MainRecyclerAdapter(private val items: List<PostDTO>) : RecyclerView.Adapter<MainRecyclerAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemMainRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostDTO) {
            // 이미지 로드
            Glide.with(binding.imageView.context).load(item.image_url).into(binding.imageView)
            // 카테고리 이름
            binding.textView.text = item.category_name
            // 거리
            binding.distanceTv.text = "${item.distance}km"
            // 지역 이름
            binding.regionNameTv.text = item.regionName
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