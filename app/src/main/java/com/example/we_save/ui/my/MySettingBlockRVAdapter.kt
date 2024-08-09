package com.example.we_save.ui.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.we_save.databinding.ItemBlockBinding
import com.example.we_save.databinding.ItemPostWritingBinding

class MySettingBlockRVAdapter (private val blockList: ArrayList<Block>): RecyclerView.Adapter<MySettingBlockRVAdapter.ViewHolder>() {


    interface MyItemClickListener{
        fun onItemClick(block: Block)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(block: Block){
        blockList.add(block)
        notifyItemInserted(blockList.size - 1)
    }

//    fun removeItem(position: Int){
//        albumList.removeAt(position)
//        notifyDataSetChanged()
//    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemBlockBinding = ItemBlockBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(blockList[position])
        holder.itemView.setOnClickListener{mItemClickListener.onItemClick(blockList[position]) }
    }

    override fun getItemCount(): Int = blockList.size


    inner class ViewHolder(val binding: ItemBlockBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(block: Block){
            binding.itemBlockProfileIv.setImageResource(block.img!!)
            binding.itemBlockNameTv.text = block.name

        }
    }
}