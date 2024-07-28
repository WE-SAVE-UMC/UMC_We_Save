package com.example.we_save.ui.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.we_save.databinding.ItemPostWritingBinding

class PostWritingRVAdapter (private val writingList: ArrayList<Writing>): RecyclerView.Adapter<PostWritingRVAdapter.ViewHolder>() {

    interface MyItemClickListener{
        fun onItemClick(writing: Writing)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(writing: Writing){
        writingList.add(writing)
        notifyItemInserted(writingList.size - 1)
    }

//    fun removeItem(position: Int){
//        albumList.removeAt(position)
//        notifyDataSetChanged()
//    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPostWritingBinding = ItemPostWritingBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(writingList[position])
        holder.itemView.setOnClickListener{mItemClickListener.onItemClick(writingList[position]) }
    }

    override fun getItemCount(): Int = writingList.size


    inner class ViewHolder(val binding: ItemPostWritingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(writing: Writing){
            binding.itemPostWritingTitleTv.text = writing.title
            binding.itemPostWritingLocationTv.text = writing.location
            binding.itemPostWritingImgIv.setImageResource(writing.img!!)

        }
    }
}