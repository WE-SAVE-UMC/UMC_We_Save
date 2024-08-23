package com.example.we_save.ui.alarm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.example.we_save.R

import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.bumptech.glide.request.RequestOptions
import com.example.we_save.data.apiservice.AdvertisementService
import com.example.we_save.data.apiservice.GetQuizResponse
import com.example.we_save.data.apiservice.QuizResponse
import com.example.we_save.data.apiservice.QuizResponseRequest
import com.example.we_save.data.apiservice.QuizResult
import com.example.we_save.data.apiservice.QuizResult1
import com.example.we_save.data.apiservice.RetrofitClient
import com.example.we_save.databinding.ActivityAdvertiseMentBinding
import com.example.we_save.ui.MainActivity
import com.example.we_save.ui.main.MainFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder

class AdvertiseMentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdvertiseMentBinding
    private var adResult: QuizResult? = null
    private var correctAnswer: String = "" // 서버로부터 받아야 함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvertiseMentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상태바 색상 설정
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.leftArrow.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        // UI 초기화
        initializeViews()

        val quizId = intent.getIntExtra("quizId", -1)
        if (quizId != -1) {
            fetchQuizData(quizId)
        }

        // 클릭 리스너 설정
        binding.leftView.setOnClickListener {
            handleClick(binding.leftAnswerTv.text.toString(), true)
        }

        binding.rightView.setOnClickListener {
            handleClick(binding.rightAnswerTv.text.toString(), false)
        }
    }

    private fun initializeViews() {
        // UI 요소 초기화 (binding을 통해 UI와 연결)
        // 이곳에서 binding 객체를 통해 뷰들을 초기화할 수 있습니다.
    }

    private fun fetchQuizData(adId: Int) {
        val advertisementService = RetrofitClient.createService(AdvertisementService::class.java)

        advertisementService.getQuiz(adId).enqueue(object : Callback<GetQuizResponse> {
            override fun onResponse(call: Call<GetQuizResponse>, response: Response<GetQuizResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { quizResponse ->
                        if (quizResponse.isSuccess) {
                            adResult = quizResponse.result
                            updateUIWithQuizData(adResult!!)
                        } else {
                            Toast.makeText(this@AdvertiseMentActivity, "퀴즈 로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@AdvertiseMentActivity, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetQuizResponse>, t: Throwable) {
                Toast.makeText(this@AdvertiseMentActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUIWithQuizData(quizResult: QuizResult) {
        binding.quizQuestionTv.text = quizResult.question

        val options = quizResult.options
        if (options.isNotEmpty()) {
            binding.leftAnswerTv.text = options[0].text
            if (options.size > 1) {
                binding.rightAnswerTv.text = options[1].text
            }

            //binding.advertisementTextTv.text = options[0].responseText
            val quizId = intent.getIntExtra("quizId", -1)
            Log.d("acvertise" ,"$quizId")
            val (drawableResId, redirectUrl) = when (quizId) {
                1 -> Pair(
                    R.drawable.nut_ads_img,
                    "https://m.brand.naver.com/hbafstore/products/10384323591"
                )
                2 -> Pair(
                    R.drawable.car_ads_img,
                    "https://direct.samsungfire.com/ria/pc/product/car/?state=Front"
                )
                3 -> Pair(
                    R.drawable.toreta_image,
                    "https://brand.naver.com/cocacola/products/4611673852"
                )
                else -> Pair(
                    R.drawable.manghi_image,
                    "https://brand.naver.com/baseus/products/4661149558"
                )
            }


            Glide.with(this)
                .load(drawableResId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Glide 로드 오류 발생 시 로그 출력
                        Log.e("Glide", "Image load failed", e)
                        return false // false를 반환하여 Glide가 기본 오류 처리를 하도록 허용
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.advertisementBackgroundIv)
            binding.advertisementBackgroundIv.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                startActivity(intent)
            }

            correctAnswer = options.firstOrNull { it.isCorrect }?.text ?: ""
        } else {

        }
    }

    private fun handleClick(selectedAnswer: String, isLeft: Boolean) {
        val adResult = adResult
        if (adResult == null) {

            return
        }

        binding.leftView.isClickable = false
        binding.rightView.isClickable = false

        val selectedOption = if (isLeft) adResult.options[0] else adResult.options[1]
        val selectedOptionId = selectedOption.optionId

        if (isLeft) {
            binding.leftBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_20)
            binding.leftOneIv.setImageResource(R.drawable.red_one_answer_iv)
        } else {
            binding.rightBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_20)
            binding.rightTwoIv.setImageResource(R.drawable.red_two_answer_iv)
        }

        // 1초 후 정답 여부에 따른 UI 업데이트 및 서버에 응답 제출
        Handler(Looper.getMainLooper()).postDelayed({
            submitQuizResponse(adResult.adId, selectedOptionId)
        }, 1000)
    }

    private fun submitQuizResponse(adId: Int, selectedOptionId: Int) {
        val advertisementService = RetrofitClient.createService()
        val request = QuizResponseRequest(adId = adId, selectedOptionId = selectedOptionId)

        advertisementService.submitQuizResponse(request).enqueue(object : Callback<QuizResponse> {
            override fun onResponse(call: Call<QuizResponse>, response: Response<QuizResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        updateUIWithQuizResult(data.result)
                    }
                } else {
                    Toast.makeText(this@AdvertiseMentActivity, "서버에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<QuizResponse>, t: Throwable) {
                Toast.makeText(this@AdvertiseMentActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUIWithQuizResult(result: QuizResult1) {
        // 정답 여부에 따라 선택된 옵션과 정답 옵션의 배경을 변경
        if (result.correct) {
            // 사용자가 정답을 선택한 경우
            if (correctAnswer == binding.leftAnswerTv.text.toString()) {
                binding.leftBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_20)
                binding.leftOneIv.setImageResource(R.drawable.answer_right_check_iv)
                binding.rightBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_05)
                binding.rightTwoIv.setImageResource(R.drawable.left_one_wrong_iv)
            } else {
                binding.rightBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_20)
                binding.rightTwoIv.setImageResource(R.drawable.answer_right_check_iv)
                binding.leftBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_05)
                binding.leftOneIv.setImageResource(R.drawable.left_one_wrong_iv)
            }
            binding.rightTv.text = "정답이에요!"
        } else {
            // 사용자가 오답을 선택한 경우
            if (correctAnswer == binding.leftAnswerTv.text.toString()) {
                binding.leftBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_20)
                binding.leftOneIv.setImageResource(R.drawable.answer_right_check_iv)
                binding.rightBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_05)
                binding.rightTwoIv.setImageResource(R.drawable.left_one_wrong_iv)
            } else {
                binding.rightBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_20)
                binding.rightTwoIv.setImageResource(R.drawable.answer_right_check_iv)
                binding.leftBackground.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_05)
                binding.leftOneIv.setImageResource(R.drawable.left_one_wrong_iv)
            }
            binding.rightTv.text = "오답이에요!"
            binding.rightCheckIv.setImageResource(R.drawable.wrong_description_check_iv)
        }

        // 광고 이미지 및 텍스트 업데이트
        binding.descriptionBackground.visibility = View.VISIBLE
        binding.rightCheckIv.visibility = View.VISIBLE
        binding.rightTv.visibility = View.VISIBLE
        binding.descriptionTv.visibility = View.VISIBLE
        binding.advertisementBackgroundIv.visibility = View.VISIBLE

       // binding.advertisementTextTv.visibility = View.VISIBLE


        val imageUrl = "http://114.108.153.82/files/ads/" + result.imageUrl
        Log.d("AdvertiseMentActivity", "이미지 url: $imageUrl")



        if (result.correct) {
            binding.descriptionTv.text = result.correctMessage
        } else {
            binding.descriptionTv.text = result.incorrectMessage
        }
    }
}