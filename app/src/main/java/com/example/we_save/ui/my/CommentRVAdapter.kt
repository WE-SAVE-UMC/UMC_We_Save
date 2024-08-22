package com.example.we_save.ui.my

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.we_save.R
import com.example.we_save.data.apiservice.CommentResult
import com.example.we_save.data.apiservice.PostResult
import com.example.we_save.databinding.ItemPostWritingBinding

class CommentRVAdapter (private val postList: ArrayList<CommentResult>): RecyclerView.Adapter<CommentRVAdapter.ViewHolder>() {
/*
    interface MyItemClickListener {
        fun onItemClick(post: CommentResult)
        fun onSelectClick(post: CommentResult)
        fun onSelectCountChange(count: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    // 선택 위젯의 가시성 변화
    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectVisibility() {
        for (post in postList) {
            post.selectedVisible =  !post.selectedVisible
        }
        notifyDataSetChanged() // 모든 항목 업데이트를 위해 호출
        //mItemClickListener.onSelectCountChange(getSelectedCount()) // 선택된 항목 수 갱신
    }

    // 선택된 모든 항목의 selected 값을 false로 지정
    @SuppressLint("NotifyDataSetChanged")
    fun clearSelected() {
        for (post in postList) {
            post.selected = false
        }
        notifyDataSetChanged() // 모든 항목 업데이트를 위해 호출
        mItemClickListener.onSelectCountChange(getSelectedCount()) // 선택된 항목 수 갱신
    }

    // 전체 선택
    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        for (post in postList) {
            post.selected = true
        }
        notifyDataSetChanged() // 모든 항목 업데이트를 위해 호출
        mItemClickListener.onSelectCountChange(getSelectedCount()) // 선택된 항목 수 갱신
    }

    // 모든 항목 반환
    fun returnAll(): ArrayList<CommentResult> {
        return ArrayList(postList) // 복사본을 반환하여 원본 리스트를 보호
    }

    // selected가 true인 항목 개수를 반환
    fun getSelectedCount(): Int {
        return postList.count { it.selected }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPostWritingBinding = ItemPostWritingBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(postList[position])
        }

        // 선택 클릭 리스너 설정
        holder.binding.itemPostWritingSelectIv.setOnClickListener {
            mItemClickListener.onSelectClick(postList[position])
            postList[position].selected = !postList[position].selected
            notifyItemChanged(position)
            mItemClickListener.onSelectCountChange(getSelectedCount())
        }
    }

    override fun getItemCount(): Int = postList.size

    inner class ViewHolder(val binding: ItemPostWritingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: CommentResult) {
            binding.itemPostWritingTitleTv.text = post.title
            binding.itemPostWritingLocationTv.text = post.regionName

            if (post.imageUrl != null) {
                val img = "http://114.108.153.82:80${post.imageUrl}"
                Glide.with(itemView.context)
                    .load(img)
                    .apply(RequestOptions().placeholder(R.drawable.img_logo_horizontal)) // 로딩 중에 보여줄 이미지
                    .error(R.drawable.img_logo_horizontal) // 오류 발생 시 보여줄 이미지
                    .into(binding.itemPostWritingImgIv)
            } else {
                // 카테고리에 따라 이미지 변경
                binding.itemPostWritingImgIv.scaleType = ImageView.ScaleType.CENTER
                when (post.category) {
                    "FIRE" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_fire)
                    "EARTHQUAKE" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_earthquake)
                    "HEAVY_RAIN" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_rain)
                    "HEAVY_SNOW" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_snow)
                    "TRAFFIC_ACCIDENT" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_car)
                    "OTHER" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_etc)
                }
            }

            // 상태에 따른 UI 변경
            if (post.status == "PROCESSING") {
                binding.itemPostWritingFinishedTv.visibility = View.GONE
                binding.itemPostCompleteView.visibility = View.GONE
            } else if (post.status == "COMPLETED") {
                binding.itemPostWritingFinishedTv.visibility = View.VISIBLE
                binding.itemPostCompleteView.visibility = View.VISIBLE
            }

            // select 위젯 가시성 업데이트
            if (post.selectedVisible) {
                binding.itemPostWritingSelectIv.visibility = View.VISIBLE
            } else {
                binding.itemPostWritingSelectIv.visibility = View.GONE
            }

            // 선택 상태에 따라 이미지 변경
            if (post.selected) {
                binding.itemPostWritingSelectIv.setImageResource(R.drawable.ic_checkbox_on) // 선택된 이미지
            } else {
                binding.itemPostWritingSelectIv.setImageResource(R.drawable.ic_checkbox_off) // 선택되지 않은 이미지
            }
        }
    }*/
interface MyItemClickListener{
    fun onItemClick(post: CommentResult)
}

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPostWritingBinding = ItemPostWritingBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
        holder.itemView.setOnClickListener{mItemClickListener.onItemClick(postList[position]) }
    }

    override fun getItemCount(): Int = postList.size


    inner class ViewHolder(val binding: ItemPostWritingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(post: CommentResult){
            binding.itemPostWritingTitleTv.text = post.title
            binding.itemPostWritingLocationTv.text = post.regionName

            if(post.imageUrl != null){
                val img = "http://114.108.153.82:80${post.imageUrl}"
                Glide.with(itemView.context)
                    .load(img)
                    .apply(RequestOptions().placeholder(R.drawable.img_logo_horizontal)) // 로딩 중에 보여줄 이미지
                    .error(R.drawable.img_logo_horizontal) // 오류 발생 시 보여줄 이미지
                    .into(binding.itemPostWritingImgIv)
            } else {

                // 카테고리에 따라 이미지 변경
                binding.itemPostWritingImgIv.scaleType = ImageView.ScaleType.CENTER
                when(post.category){
                    "FIRE" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_fire)
                    "EARTHQUAKE" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_earthquake)
                    "HEAVY_RAIN" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_rain)
                    "HEAVY_SNOW" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_snow)
                    "TRAFFIC_ACCIDENT" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_car)
                    "OTHER" -> binding.itemPostWritingImgIv.setImageResource(R.drawable.card_etc)
                }

            }


            // status == COMPLETED 경우 종료 텍스트 뷰를 띄우고 어둡게
            if (post.status == "PROCESSING") {
                binding.itemPostWritingFinishedTv.visibility = View.GONE
                binding.itemPostCompleteView.visibility = View.GONE
            } else if(post.status == "COMPLETED") {
                binding.itemPostWritingFinishedTv.visibility = View.VISIBLE
                binding.itemPostCompleteView.visibility = View.VISIBLE
            }


        }
    }
}