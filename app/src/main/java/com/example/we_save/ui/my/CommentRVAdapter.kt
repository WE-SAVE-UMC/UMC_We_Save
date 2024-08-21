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