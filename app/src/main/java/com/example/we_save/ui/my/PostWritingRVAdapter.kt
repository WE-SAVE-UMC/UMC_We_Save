package com.example.we_save.ui.my

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.we_save.R
import com.example.we_save.databinding.ItemPostWritingBinding

class PostWritingRVAdapter (private val writingList: ArrayList<Writing>): RecyclerView.Adapter<PostWritingRVAdapter.ViewHolder>() {

    interface MyItemClickListener{
        fun onItemClick(writing: Writing)
        fun onSelectClick(writing: Writing)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(writing: Writing){
        writingList.add(writing)
        notifyItemInserted(writingList.size - 1)
    }

    // 선택 위젯의 가시성 변화
    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectVisibility() {
        for (writing in writingList) {
            writing.selectedVisible =  !writing.selectedVisible
        }
        notifyDataSetChanged() // 모든 항목 업데이트를 위해 호출
    }

    // 선택된 모든 항목의 selected값을 false로 지정
    @SuppressLint("NotifyDataSetChanged")
    fun clearSelected() {
        for (writing in writingList) {
            writing.selected = false
        }
        notifyDataSetChanged() // 모든 항목 업데이트를 위해 호출
    }

    // 전체 선택
    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        for (writing in writingList) {
            writing.selected = true
        }
        notifyDataSetChanged() // 모든 항목 업데이트를 위해 호출
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
        holder.binding.itemPostWritingSelectIv.setOnClickListener {
            mItemClickListener.onSelectClick(writingList[position])

            // selected의 값을 바꿈
            writingList[position].selected = !writingList[position].selected
            notifyItemChanged(position)}
    }

    override fun getItemCount(): Int = writingList.size


    inner class ViewHolder(val binding: ItemPostWritingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(writing: Writing){
            binding.itemPostWritingTitleTv.text = writing.title
            binding.itemPostWritingLocationTv.text = writing.location
            binding.itemPostWritingImgIv.setImageResource(writing.img!!)

            // finished가 true인 경우 종료 텍스트 뷰를 띄움
            if (writing.finished) {
                binding.itemPostWritingFinishedTv.visibility = View.VISIBLE
            } else {
                binding.itemPostWritingFinishedTv.visibility = View.GONE
            }

            // select 위젯 가시성 업데이트
            if (writing.selectedVisible) {
                binding.itemPostWritingSelectIv.visibility = View.VISIBLE
            } else {
                binding.itemPostWritingSelectIv.visibility = View.GONE
            }

            // 선택 상태에 따라 이미지 변경
            if (writing.selected) {
                binding.itemPostWritingSelectIv.setImageResource(R.drawable.ic_checkbox_on) // 선택된 이미지
            } else {
                binding.itemPostWritingSelectIv.setImageResource(R.drawable.ic_checkbox_off) // 선택되지 않은 이미지
            }

        }
    }
}