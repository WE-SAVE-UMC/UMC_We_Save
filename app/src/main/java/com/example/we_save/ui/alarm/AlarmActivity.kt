package com.example.we_save.ui.alarm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.R
import com.example.we_save.data.apiservice.RetrofitClient
import com.example.we_save.databinding.ActivityAlarmBinding
import com.example.we_save.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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



    /*private fun loadAlarms() {
        // 서버에 요청을 보내 알람 데이터를 받아옴
        val notificationRequest = NotificationRequest(
            postId = 1,
            commentId = 1,
            commenterName = "익명1",
            content = "위험해요. 조심하세요.",
            isRead = false
        )

        val service = RetrofitClient.notificationService
        val call = service.sendCommentNotification(notificationRequest)

        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val alarm = Alarm(
                        alarmBackground = if (notificationRequest.isRead) R.color.gray_50 else R.color.red_00,
                        alarmOvalBackground = if (notificationRequest.isRead) R.color.red_00 else R.color.gray_50,
                        imageRes = R.drawable.message_alarm_iv,
                        mainText = notificationRequest.content,
                        subText = "알림 내용",
                        isRead = notificationRequest.isRead
                    )
                    alarms.add(alarm)
                    alarmAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AlarmActivity, "서버 오류: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Toast.makeText(this@AlarmActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        // 허위, 확인했어요!!
        val buttonNotificationRequest = ButtonNotificationRequest(
            postId = 3,
            buttonType = "허위예요", // 또는 "확인했어요"
            isRead = false
        )

        val buttonCall = service.sendButtonNotification(buttonNotificationRequest)

        buttonCall.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val imageRes = when (buttonNotificationRequest.buttonType) {
                        "허위예요" -> R.drawable.alarm_false_iv // 허위예요 이미지
                        "확인했어요" -> R.drawable.alarm_confirm_iv // 확인했어요 이미지
                        else -> R.drawable.image_null_etc // 기본 이미지 (예를 들어, 기본 아이콘)
                    }
                    val alarm = Alarm(
                        alarmBackground = if (buttonNotificationRequest.isRead) R.color.gray_50 else R.color.red_00,
                        alarmOvalBackground = if (buttonNotificationRequest.isRead) R.color.red_00 else R.color.gray_50,
                        imageRes = R.drawable.message_alarm_iv,
                        mainText = buttonNotificationRequest.buttonType, // "허위예요" 또는 "확인했어요"
                        subText = "버튼 알림",
                        isRead = buttonNotificationRequest.isRead
                    )
                    alarms.add(alarm)
                    alarmAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AlarmActivity, "서버 오류: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Toast.makeText(this@AlarmActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        // 신뢰도 높은 사건사고
        val nearbyPopularNotificationRequest = NearbyPopularNotificationRequest(
            postId = 1,
            title = "00 근처에 이상한 사람 돌아다닙니다.",
            postCreatedAt = "2024-08-10T14:48:00Z",
            isRead = false
        )

        val nearbyPopularCall = service.sendNearbyPopularNotification(nearbyPopularNotificationRequest)

        nearbyPopularCall.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val alarm = Alarm(
                        alarmBackground = if (nearbyPopularNotificationRequest.isRead) R.color.gray_50 else R.color.red_00,
                        alarmOvalBackground = if (nearbyPopularNotificationRequest.isRead) R.color.red_00 else R.color.gray_50,
                        imageRes = R.drawable.danger_alarm_iv, // 예를 들어 인기 글 알람의 이미지
                        mainText = "내 근처에 신뢰도 높은 사고가 있어요!",
                        subText = nearbyPopularNotificationRequest.title,
                        isRead = nearbyPopularNotificationRequest.isRead
                    )
                    alarms.add(alarm)
                    alarmAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AlarmActivity, "서버 오류: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Toast.makeText(this@AlarmActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        val statusNotificationRequest = StatusNotificationRequest(
            postId = 123,
            title = "성신여대 화재",
            expiryTime = "2024-08-14T15:00:00Z",
            isRead = false
        )
        // 상황 종료
        val statusCall = service.sendStatusNotification(statusNotificationRequest)

        statusCall.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val alarm = Alarm(
                        alarmBackground = if (statusNotificationRequest.isRead) R.color.gray_50 else R.color.red_00,
                        alarmOvalBackground = if (statusNotificationRequest.isRead) R.color.red_00 else R.color.gray_50,
                        imageRes = R.drawable.status_alarm_iv, // 상태 알림 이미지 리소스 설정
                        mainText = statusNotificationRequest.title,
                        subText = "게시물 상태 알림",
                        isRead = statusNotificationRequest.isRead
                    )
                    alarms.add(alarm)
                    alarmAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AlarmActivity, "서버 오류: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Toast.makeText(this@AlarmActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }*/
}
