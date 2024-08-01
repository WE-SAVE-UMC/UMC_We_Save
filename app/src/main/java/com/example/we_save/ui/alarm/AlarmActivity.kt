package com.example.we_save.ui.alarm

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.R
import com.example.we_save.databinding.ActivityAlarmBinding
import com.example.we_save.ui.MainActivity

class AlarmActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmBinding
    lateinit var alarmAdapter: AlarmRvAdapter
    private val alarms = ArrayList<Alarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        binding.upperLeftArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity((intent))
        }
        alarms.add(
            Alarm(
                R.color.white, // 알람 배경 색
                R.color.red_00,  // 알람 원의 색
                R.drawable.danger_alarm_iv,
            "매인 알람1",
            "부가설명 입니다.")
        )
        alarms.add(
            Alarm(
                R.color.red_00, // 알람 배경 색
                R.color.white,  // 알람 원의 색
                R.drawable.message_alarm_iv,
            "매인 알람2",
            "부가설명 입니다.")
        )

        alarmAdapter = AlarmRvAdapter(alarms)
        binding.alarmRvView.apply {
            layoutManager = LinearLayoutManager(this@AlarmActivity, LinearLayoutManager.VERTICAL,false)
            adapter = alarmAdapter
        }
        // 슬라이드로 삭제
        val itemTouchHelperCallback = AlarmSlideCallback(alarmAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.alarmRvView)
    }
}