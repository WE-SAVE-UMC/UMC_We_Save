package com.example.we_save.ui.search

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.we_save.R
import com.example.we_save.databinding.CounterMeasureItemBinding
import com.example.we_save.databinding.ItemSearchResultBinding
import com.example.we_save.data.apiservice.Place as Place1

class CountermeasuresAdapter(private val items: List<CountermeasureDto>) : RecyclerView.Adapter<CountermeasuresAdapter.ViewHolder>() {

    class ViewHolder(val binding: CounterMeasureItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isdescriptionVisible = false
        fun bind(item: CountermeasureDto) {
            binding.solution1Tv.text = item.title
            binding.boldDescription1Tv.text = item.mainContent
            binding.description1Tv.text = item.detailContent ?: "없음"

            binding.descriptionContainer.visibility = View.GONE
            binding.underArrowIv.setImageResource(R.drawable.under_arrow_ic)
            val color = Color.parseColor("#FEE5E1")
            val color1 = ContextCompat.getColor(binding.root.context, R.color.gray_05)
            binding.underArrowIv.setOnClickListener {
                if (isdescriptionVisible) {
                    binding.descriptionContainer.visibility = View.GONE
                    binding.underArrowIv.setImageResource(R.drawable.under_arrow_ic)
                    binding.firstBackground.backgroundTintList = ColorStateList.valueOf(color1)
                } else { // 보이는 상태
                    binding.descriptionContainer.visibility = View.VISIBLE
                    binding.underArrowIv.setImageResource(R.drawable.up_arrow)
                    binding.firstBackground.backgroundTintList = ColorStateList.valueOf(color)
                }
                isdescriptionVisible = !isdescriptionVisible
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CounterMeasureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}