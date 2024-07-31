package com.example.we_save.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.we_save.databinding.ItemAlarmViewBinding

class AlarmRvAdapter (private val alarmdata: ArrayList<Alarm>) : RecyclerView.Adapter<AlarmRvAdapter.AlarmviewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmviewHolder {
        val binding: ItemAlarmViewBinding =
            ItemAlarmViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmviewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alarmdata.size
    }

    override fun onBindViewHolder(holder: AlarmviewHolder, position: Int) {
        holder.bind(alarmdata[position])
    }
    class AlarmviewHolder( val binding: ItemAlarmViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: Alarm) {
            binding.alarmBackground.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, alarm.alarmview)
            binding.alarmOvalBackground.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, alarm.alarmbackground)
            binding.alarmIv.setImageResource(alarm.ImageRes)
            binding.mainTv.text = alarm.maintext
            binding.subTv.text = alarm.subtext
        }
    }
    fun removeItem(position: Int) {
        alarmdata.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(alarm: Alarm) {
        alarmdata.add(alarm)
        notifyItemInserted(alarmdata.size - 1)
    }


}