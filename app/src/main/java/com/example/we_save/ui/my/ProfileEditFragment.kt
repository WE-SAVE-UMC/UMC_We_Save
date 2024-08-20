package com.example.we_save.ui.my

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation.Callback
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.we_save.R
import com.example.we_save.SplashActivity
import com.example.we_save.data.apiservice.LoginResponse
import com.example.we_save.data.apiservice.ProfileInterface
import com.example.we_save.data.apiservice.ProfileRequest
import com.example.we_save.data.apiservice.ProfileResponse
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMyPostBinding
import com.example.we_save.databinding.FragmentProfileEditBinding
import com.example.we_save.ui.main.pages.MyFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class ProfileEditFragment : Fragment() {

    lateinit var binding: FragmentProfileEditBinding
    private var selectedImageFile: File? = null
    private var newNickname: String? = null
    private var originalImageUrl: String? = null

    // ActivityResultLauncher를 사용해 갤러리에서 이미지 선택
    private val selectImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            binding.profileEditPhotoIv.setImageURI(selectedImageUri)
            selectedImageFile = selectedImageUri?.let { getFileFromUri(it) }  // 파일 객체 가져오기
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        // 프로필 정보가 화면에 뜸
        initProfile()
        Log.d("실패 ㄴㄴ", "${originalImageUrl}")



        // 카메라 버튼 -> 사진 선택
        binding.profileEditCircleIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }


        // 저장하기 버튼
        binding.profileEditSaveBtn.setOnClickListener {

            newNickname = binding.profileEditNameEt.text.toString()


            if(selectedImageFile != null && newNickname!!.isNotEmpty()) {           // 사진과 닉네임이 변경된 경우

                updateProfile(selectedImageFile, newNickname!!)
                Toast.makeText(requireContext(), "프로필이 저장되었습니다", Toast.LENGTH_SHORT).show()

                selectedImageFile = null

            } else if(selectedImageFile != null && newNickname!!.isEmpty()){        // 사진만 변경된 경우

                updateProfile(selectedImageFile, binding.profileEditNameEt.hint.toString())
                Toast.makeText(requireContext(), "이미지가 저장되었습니다", Toast.LENGTH_SHORT).show()

                selectedImageFile = null

            } else if(selectedImageFile == null && newNickname!!.isNotEmpty()){     // 닉네임만 변경된 경우

                Log.d("실패", "${originalImageUrl}")
                // originalImageUrl을 Uri로 변환
                val originalUri = Uri.parse(originalImageUrl)
                Log.d("실패 ㅋㅋㅋ", "${originalUri}")


                //val originalImageFile = originalUri?.let { getFileFromUri(it) }
                //val originalFile = getFileFromUri(originalUri)

                updateProfile(getImageFileFromUri(requireContext(), originalUri), newNickname!!)
                Toast.makeText(requireContext(), "닉네임이 저장되었습니다", Toast.LENGTH_SHORT).show()

            } else if(selectedImageFile == null && newNickname!!.isEmpty()) {       // 사진과 닉네임 모두 변경되지 않았을 경우

                Toast.makeText(requireContext(), "변경된 내용이 없습니다", Toast.LENGTH_SHORT).show()
            }

        }

        // 닉네임 EditText 뷰
        binding.profileEditNameEt.setOnClickListener {
            binding.profileEditNameEt.isCursorVisible = true
        }

        return binding.root
    }

    fun getImageFileFromUri(context: Context, uri: Uri): File? {
        return when (uri.scheme) {
            "content" -> {
                // Content URI 처리
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val mimeType = context.contentResolver.getType(uri)
                    val extension = when (mimeType) {
                        "image/jpeg" -> ".jpg"
                        "image/png" -> ".png"
                        else -> ".jpg"  // 기본 확장자 설정
                    }
                    val fileName = "image_file$extension"
                    val file = File(context.cacheDir, fileName)

                    inputStream?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    file
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            "file" -> {
                // File URI 처리
                File(uri.path ?: return null)
            }
            else -> {
                // 지원하지 않는 스킴
                null
            }
        }
    }


    // Uri -> File로 바꾸는 함수
    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val mimeType = context?.contentResolver?.getType(uri)
        // 확장자 추가
        val extension = when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> ""  // 확장자를 알 수 없는 경우
        }

        val fileName = "profile_image$extension"  // 확장자를 포함한 파일 이름 생성
        val file = File(context?.cacheDir, fileName)
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    // 프로필 수정
    private fun updateProfile(imageFile: File?, nickname: String) {
        val profileService = getRetrofit().create(ProfileInterface::class.java)

        // 닉네임을 RequestBody로 변환
        val nicknameRequestBody = nickname.toRequestBody("text/plain".toMediaTypeOrNull())

        // 이미지 파일을 MultipartBody.Part로 변환
        val profileImagePart: MultipartBody.Part? = imageFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("profileImage", it.name, requestFile)
        }

        profileService.setProfile(getJwt(), profileImagePart, nicknameRequestBody).enqueue(object : retrofit2.Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val resp: ProfileResponse = response.body()!!

                    // 로그에서 이미지 파일 링크 확인
                    val img = "http://114.108.153.82:80${resp.result.imageUrl}"
                    originalImageUrl = img
                    Log.d("UpdateProfile/SUCCESS", "Image URL: $img")

                    // 프로필 사진 설정
                    Glide.with(requireContext())
                        .load(img)
                        .apply(RequestOptions().placeholder(R.drawable.ic_profile)) // 로딩 중에 보여줄 이미지
                        .error(R.drawable.ic_profile) // 오류 발생 시 보여줄 이미지
                        .circleCrop()
                        .into(binding.profileEditPhotoIv)

                    // 닉네임 설정 및 EditText 내용 초기화
                    binding.profileEditNameEt.setText("")  // EditText 내용 지우기
                    binding.profileEditNameEt.setHint(resp.result.nickname)
                    binding.profileEditNameEt.isCursorVisible = false

                    // 프로필 초기화
                    initProfile()

                } else {
                    Log.d("UpdateProfile/ERROR", response.message())
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.d("UpdateProfile/FAIL", t.message.toString())
            }
        })
    }

    // 기본 프로필 정보
    private fun initProfile() {
        val profileService = getRetrofit().create(ProfileInterface::class.java)
        profileService.getProfile(getJwt()).enqueue(object: retrofit2.Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                Log.d("GETProfile/SUCCESS", response.toString())

                val resp: ProfileResponse = response.body()!!
                when (resp.code) {
                    // 로딩에 성공한 경우
                    "COMMON200" -> {
                        // 프로필 사진 설정
                        val img = "http://114.108.153.82:80${resp.result.imageUrl}"
                        originalImageUrl = img
                        Glide.with(requireContext())
                            .load(img)
                            .apply(RequestOptions().placeholder(R.drawable.ic_profile)) // 로딩 중에 보여줄 이미지
                            .error(R.drawable.ic_profile) // 오류 발생 시 보여줄 이미지
                            .circleCrop()
                            .into(binding.profileEditPhotoIv)

                        // 닉네임 설정
                        binding.profileEditNameEt.setHint(resp.result.nickname)

                        // 전화번호 설정
                        val phoneNum = resp.result.phoneNum
                        val formattedPhoneNum = phoneNum.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
                        binding.profileEditPhoneTv.text = formattedPhoneNum

                    }
                    // 로딩에 실패한 경우
                    "COMMON401" -> binding.profileEditNameEt.setHint("네트워크 오류")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.d("GETProfile/FAIL", t.message.toString())
            }
        })
    }

    // 로그인된 사용자의 토큰을 반환
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }
}
