package com.example.we_save.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            binding.underArrowIv.setOnClickListener {
                if (isdescriptionVisible) {
                    binding.descriptionContainer.visibility = View.GONE
                    binding.underArrowIv.setImageResource(R.drawable.under_arrow_ic)
                } else {
                    binding.descriptionContainer.visibility = View.VISIBLE
                    binding.underArrowIv.setImageResource(R.drawable.up_arrow)
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