package com.example.we_save

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.we_save.databinding.ItemSearchResultBinding
import com.example.we_save.Place as Place1

class SearchResultsAdapter(private val onClick: (Place1) -> Unit) : ListAdapter<Place1, SearchResultsAdapter.ViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }

    inner class ViewHolder(private val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place1) {
            binding.placeName.text = place.title
            binding.root.setOnClickListener {
                onClick(place)
            }
        }
    }

    private class PlaceDiffCallback : DiffUtil.ItemCallback<Place1>() {
        override fun areItemsTheSame(oldItem: Place1, newItem: Place1): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Place1, newItem: Place1): Boolean {
            return oldItem == newItem
        }
    }
}