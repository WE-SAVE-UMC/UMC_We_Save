package com.example.we_save.ui.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.we_save.R
import com.example.we_save.data.apiservice.BlockResult
import com.example.we_save.databinding.ItemBlockBinding
import com.example.we_save.databinding.ItemPostWritingBinding

class MySettingBlockRVAdapter (private val blockList: ArrayList<BlockResult>): RecyclerView.Adapter<MySettingBlockRVAdapter.ViewHolder>() {


    interface MyItemClickListener{
        fun onItemClick(block: BlockResult)
        fun onBlockReleaseBtnClick(userId: Int)

        fun onBlockBtnClick(userId: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(block: BlockResult){
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

        // 차단 해제 버튼 클릭 시 userId를 전달
        holder.binding.itemReleaseBlockBtn.setOnClickListener {
            val userId = blockList[position].userId
            mItemClickListener.onBlockReleaseBtnClick(userId)
            // 차단 해제 버튼 없애기, 차단 하기 버튼 나타내기
            holder.binding.itemReleaseBlockBtn.visibility = View.GONE
            holder.binding.itemBlockBtn.visibility = View.VISIBLE

        }

        // 차단하기 버튼 클릭 시 userId를 전달
        holder.binding.itemBlockBtn.setOnClickListener {
            val userId = blockList[position].userId
            mItemClickListener.onBlockBtnClick(userId)
            // 차단 해제 버튼 없애기, 차단 하기 버튼 나타내기
            holder.binding.itemReleaseBlockBtn.visibility = View.VISIBLE
            holder.binding.itemBlockBtn.visibility = View.GONE

        }
    }

    override fun getItemCount(): Int = blockList.size


    inner class ViewHolder(val binding: ItemBlockBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(block: BlockResult){
            binding.itemBlockNameTv.text = block.nickname

            val img = "http://114.108.153.82:80${block.profileImage.filePath}"
            Glide.with(itemView.context)
                .load(img)
                .apply(RequestOptions().placeholder(R.drawable.ic_profile)) // 로딩 중에 보여줄 이미지
                .error(R.drawable.ic_profile) // 오류 발생 시 보여줄 이미지
                .circleCrop()
                .into(binding.itemBlockProfileIv)

        }
    }
}