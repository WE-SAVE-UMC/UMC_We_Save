package com.example.we_save

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.we_save.databinding.ItemMainRvBinding
import com.example.we_save.ui.main.MainRecyclerAdapter

class MainRecentRecylcerAdapter (private val items: List<PostDTO>) : RecyclerView.Adapter<MainRecentRecylcerAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemMainRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostDTO) {
            // 이미지 로드
            if (!item.image_url.isNullOrEmpty()) {
                Glide.with(binding.imageView.context)
                    .load(item.image_url)
                    .placeholder(R.drawable.message_alarm_iv)
                    .error(R.drawable.earthquake_iv)
                    .into(binding.imageView)
            } else {

                val placeholderImageResId = when (item.categoryName ?: "") {
                    "화재" -> R.drawable.imgea_null_fire_background
                    "지진" -> R.drawable.image_null_earthquake_background
                    "홍수" -> R.drawable.image_null_rain_background
                    "차량사고" -> R.drawable.image_null_caracciendt_background
                    "폭설" -> R.drawable.image_null_snow_background
                    else -> R.drawable.image_null_etc_background
                }
                binding.imageView.setImageResource(placeholderImageResId)
            }

            // 카테고리 이름 설정
            binding.textView.text = item.categoryName ?: "없음"
            // 거리 설정
            val distanceInMeters = item.distance
            val formattedDistance = if (distanceInMeters >= 1000) {
                String.format("%.1fkm", distanceInMeters / 1000)
            } else {
                String.format("%dm", distanceInMeters.toInt())
            }

            binding.distanceTv.text = formattedDistance
            // 지역 이름 설정
            binding.regionNameTv.text = item.regionName ?: "지역 정보 없음"
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