package com.example.we_save.ui.alarm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.we_save.R
import com.example.we_save.databinding.ActivityAdvertiseMentBinding
import com.example.we_save.ui.MainActivity
import com.example.we_save.ui.main.MainFragment

class AdvertiseMentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdvertiseMentBinding
    lateinit var leftview : ConstraintLayout
    lateinit var rightview : ConstraintLayout
    lateinit var  leftbackground : ImageView
    lateinit var rightbackground : ImageView
    lateinit var leftoneIv : ImageView
    lateinit var righttwoIv : ImageView
    lateinit var leftanswerTv : TextView
    lateinit var rightanswerTv : TextView
    // 설명글
    private lateinit var descriptionBackground: ImageView
    private lateinit var rightCheckIv: ImageView
    private lateinit var rightTv: TextView
    private lateinit var descriptionTv: TextView
    // 광고 부분
    private lateinit var advertisementbackground : ImageView
    private lateinit var advertisementuppertext : TextView
    private lateinit var advertisementtext : TextView
    private lateinit var advertisementurl : TextView
    private lateinit var advertisementrightarrowiv : ImageView
    private lateinit var advertisementimageiv : ImageView

    private var correctAnswer: String = "견과류" // 서버로 부터 받아야 한다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvertiseMentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        leftview = binding.leftView // 퀴즈 왼쪽 뷰 배경 전체
        rightview = binding.rightView // 퀴즈 오른쪽 뷰 배경 전체
         leftbackground  = binding.leftBackground //퀴즈 왼쪽 뷰 배경 이미지
         rightbackground = binding.rightBackground //퀴즈 오른쪽 뷰 배경 이미지
         leftoneIv  = binding.leftOneIv // 퀴즈 왼쪽 1 이미지
         righttwoIv  = binding.rightTwoIv // 퀴즈 오른쪽 2 이미지
         leftanswerTv  = binding.leftAnswerTv // 퀴즈 왼쪽 답
         rightanswerTv  = binding.rightAnswerTv // 퀴즈 오른쪽 답

        descriptionBackground = binding.descriptionBackground //설명글 배경
        rightCheckIv = binding.rightCheckIv
        rightTv = binding.rightTv  // 맞습니다 or 아닙니다
        descriptionTv = binding.descriptionTv // 설명글

        advertisementbackground = binding.advertisementBackgroundIv // 광고배경이미지
        advertisementuppertext = binding.advertisementUpperTextTv // 광고위쪽 텍스트
        advertisementtext = binding.advertisementTextTv // 광고 텍스트
        advertisementurl = binding.advertisementUrlTv // 광고 url
        advertisementrightarrowiv = binding.advertisementRightArrowIv // 광고로 넘어가는 화살표이미지
        advertisementimageiv = binding.advertisementImageIv // 광고 이미지

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.leftArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity((intent))
        }

        leftview.setOnClickListener {
            handleClick(leftanswerTv.text.toString(), true)
        }

        rightview.setOnClickListener {
            handleClick(rightanswerTv.text.toString(), false)
        }
        val alarmImageView = findViewById<ImageView>(R.id.white_alarm_iv)
        alarmImageView.setOnClickListener {
            // 다른 액티비티로 이동
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }
        binding.leftArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleClick(selectedAnswer: String, isLeft: Boolean) {
        // 클릭 후 다른 쪽이 터치되지 않도록 설정
        leftview.isClickable = false
        rightview.isClickable = false

        if (isLeft) {
            leftbackground.backgroundTintList = ContextCompat.getColorStateList(this,
                R.color.red_20
            )
            leftoneIv.setImageResource(R.drawable.red_one_answer_iv)
        } else {
            rightbackground.backgroundTintList = ContextCompat.getColorStateList(this,
                R.color.red_20
            )
            righttwoIv.setImageResource(R.drawable.red_two_answer_iv)
        }

        // 1초 후에 정답 여부에 따라 이미지 변경
        Handler(Looper.getMainLooper()).postDelayed({
            descriptionBackground.visibility = View.VISIBLE
            rightCheckIv.visibility = View.VISIBLE
            rightTv.visibility = View.VISIBLE
            descriptionTv.visibility = View.VISIBLE
            advertisementbackground.visibility = View.VISIBLE
            advertisementuppertext.visibility = View.VISIBLE
            advertisementtext.visibility = View.VISIBLE
            advertisementurl.visibility = View.VISIBLE
            advertisementrightarrowiv.visibility = View.VISIBLE
            advertisementimageiv.visibility = View.VISIBLE
            if (selectedAnswer == correctAnswer) {
                if (isLeft) {
                    leftoneIv.setImageResource(R.drawable.answer_right_check_iv) // 정답일 경우 체크 이미지로 변경
                } else {
                    righttwoIv.setImageResource(R.drawable.answer_right_check_iv) // 정답일 경우 체크 이미지로 변경
                }
                rightTv.text = "정답이에요!"
            } else {
                if (isLeft) {
                    leftbackground.backgroundTintList = ContextCompat.getColorStateList(this,
                        R.color.gray_05
                    )
                    leftoneIv.setImageResource(R.drawable.left_one_wrong_iv) // 오답일 경우 원래 이미지로 변경
                    rightbackground.backgroundTintList = ContextCompat.getColorStateList(this,
                        R.color.red_20
                    )
                    righttwoIv.setImageResource(R.drawable.answer_right_check_iv)

                } else {
                    rightbackground.backgroundTintList = ContextCompat.getColorStateList(this,
                        R.color.gray_05
                    )
                    righttwoIv.setImageResource(R.drawable.left_one_wrong_iv) // 오답일 경우 원래 이미지로 변경
                    leftbackground.backgroundTintList = ContextCompat.getColorStateList(this,
                        R.color.red_20
                    )
                    leftoneIv.setImageResource(R.drawable.answer_right_check_iv)
                }
                rightTv.text = "오답이에요!"
                rightCheckIv.setImageResource(R.drawable.wrong_description_check_iv)
            }
        }, 1000)
    }
}
