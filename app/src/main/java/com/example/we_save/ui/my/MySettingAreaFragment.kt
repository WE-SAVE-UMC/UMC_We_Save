package com.example.we_save.ui.my

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.we_save.R
import com.example.we_save.data.apiservice.HeartInterface
import com.example.we_save.data.apiservice.HeartPostResponse
import com.example.we_save.data.apiservice.HeartRequest
import com.example.we_save.data.apiservice.HeartResponse
import com.example.we_save.data.apiservice.MyPostInterface
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMySettingAreaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.jar.Manifest
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MySettingAreaFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMySettingAreaBinding? = null
    private val binding get() = _binding!!
    private var naverMap: NaverMap? = null
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var heartCount:Int = 0

    private  var regionId_1 = 0
    private  var regionId_2 = 0

    private var NowLatitude = 0.0
    private var NowLongitude = 0.0

    private var searchedRegionName = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMySettingAreaBinding.inflate(inflater, container, false)

        Log.d("heartcount", heartCount.toString())

        // 초기 화면 구성
        binding.mySettingAreaInterestNoTv.visibility = View.GONE // 초기에는 숨김
        // FusedLocationSource 초기화
        locationSource = FusedLocationSource(this, 1000)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getHeart()

        // 지도 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.my_setting_area_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.my_setting_area_map_view, it).commit()
            }
        mapFragment.getMapAsync(this)




        // 검색 버튼 -> 지역 이름 저장 ex) "서울특별시 강남구 역삼동"
        binding.mySettingAreaSearchIv.setOnClickListener {
            searchedRegionName = binding.mySettingAreaSearchEt.text.toString()
            binding.mySettingAreaInformationNameTv.text = searchedRegionName
            binding.mySettingAreaInformationRegistStarIv.setImageResource(R.drawable.ic_star_off)

            // 관심 지역 등록 버튼
            binding.mySettingAreaInformationRegist.setOnClickListener{
                if(heartCount == 0 || heartCount == 1){
                    heartCount += 1
                    binding.mySettingAreaInformationRegistStarIv.setImageResource(R.drawable.ic_star_on)

                    postHeart(searchedRegionName)


                } else{     // 관심 지역이 2개인 경우
                    binding.mySettingAreaWarning.visibility = View.VISIBLE
                    binding.mySettingAreaInformation.visibility = View.GONE

                    // 5초 후에 경고 메시지 숨기기
                    lifecycleScope.launch {
                        delay(5000) // 5000ms = 5초
                        binding.mySettingAreaWarning.visibility = View.GONE
                        binding.mySettingAreaInformation.visibility = View.VISIBLE
                    }
                }
            }

        }



        // 첫 번째 등록 해제 버튼
        binding.mySettingAreaInterestArea1Cancel.setOnClickListener {
            deleteHeart(regionId_1)
        }

        // 첫 번째 등록 해제 버튼
        binding.mySettingAreaInterestArea2Cancel.setOnClickListener {
            deleteHeart(regionId_2)
        }


        return binding.root
    }


    // 관심 지역 조회
    private fun getHeart(){
        val HeartService = getRetrofit().create(HeartInterface::class.java)

        HeartService.getHeart(getJwt()).enqueue(object : retrofit2.Callback<HeartResponse> {
            override fun onResponse(call: Call<HeartResponse>, response: Response<HeartResponse>) {
                Log.d("GETHeart/SUCCESS", response.toString())

                val resp = response.body()!!

                // UI 초기화
                binding.mySettingAreaInterestArea1.visibility = View.GONE
                binding.mySettingAreaInterestArea2.visibility = View.GONE

                heartCount = resp.result.size  // heartCount 업데이트

                // 관심 지역이 등록된 경우
                if (resp.result.isNotEmpty()) {
                    // "등록된 관심 지역이 없습니다" 문구 없애기
                    binding.mySettingAreaInterestNoTv.visibility = View.GONE

                    // 첫 번째 관심 지역 설정
                    if (resp.result.size > 0) {
                        binding.mySettingAreaInterestArea1.visibility = View.VISIBLE
                        binding.mySettingAreaInterestArea1Tv.text = resp.result[0].regionName
                        regionId_1 = resp.result[0].regionId
                    }

                    // 두 번째 관심 지역 설정
                    if (resp.result.size > 1) {
                        binding.mySettingAreaInterestArea2.visibility = View.VISIBLE
                        binding.mySettingAreaInterestArea2Tv.text = resp.result[1].regionName
                        regionId_2 = resp.result[1].regionId
                    }
                } else {
                    // 등록된 관심 지역이 없을 때
                    heartCount = 0
                    binding.mySettingAreaInterestNoTv.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<HeartResponse>, t: Throwable) {
                Log.d("GETHeart/FAIL", t.message.toString())
            }
        })
    }

    private fun postHeart(regionName: String){
        val HeartService = getRetrofit().create(HeartInterface::class.java)
        val heartRequest = HeartRequest(regionName)

        HeartService.postHeart(getJwt(), heartRequest).enqueue(object : retrofit2.Callback<HeartPostResponse>{
            override fun onResponse(call: Call<HeartPostResponse>, response: Response<HeartPostResponse>
            ) {
                Log.d("POSTHeart/SUCCESS", response.toString())

                val resp = response.body()!!

                getHeart()
            }

            override fun onFailure(call: Call<HeartPostResponse>, t: Throwable) {
                Log.d("POSTHeart/FAIL", t.message.toString())
                Toast.makeText(requireContext(), t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun deleteHeart(regionId: Int){
        val HeartService = getRetrofit().create(HeartInterface::class.java)

        HeartService.deleteHeart(getJwt(), regionId).enqueue(object : retrofit2.Callback<HeartResponse>{
            override fun onResponse(call: Call<HeartResponse>, response: Response<HeartResponse>) {
                Log.d("DELETEHeart/SUCCESS", response.toString())

                heartCount -= 1
                getHeart()
            }

            override fun onFailure(call: Call<HeartResponse>, t: Throwable) {
                Log.d("DELETEHeart/FAIL", t.message.toString())
            }

        })
    }


    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }



    // 두 지점 사이의 거리를 계산하는 함수 (Haversine 공식 사용)
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // 지구 반경 (킬로미터 단위)

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    val regionCoordinates = mapOf(
        "서울특별시 강남구 역삼동" to LatLng(37.5008, 127.0364),
        "서울특별시 강남구 삼성동" to LatLng(37.5140, 127.0565),
        "서울특별시 강서구 등촌동" to LatLng(37.5508, 126.8644),
        "서울특별시 강서구 가양동" to LatLng(37.5615, 126.8545),
        "서울특별시 노원구 월계동" to LatLng(37.6235, 127.0652),
        "서울특별시 노원구 공릉동" to LatLng(37.6250, 127.0724),
        "서울특별시 성북구 성북동" to LatLng(37.5912, 127.0095),
        "서울특별시 성북구 성북동1가" to LatLng(37.5902, 127.0022),
        "서울특별시 영등포구 영등포동" to LatLng(37.5208, 126.9075),
        "서울특별시 영등포구 영등포동1가" to LatLng(37.5187, 126.9070),
        "서울특별시 강남구 논현동" to LatLng(37.5105, 127.0331)
    )

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 위치 소스 설정
        naverMap.locationSource = locationSource

        // UI 설정
        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
        uiSettings.isLocationButtonEnabled = true  // 현재 위치 버튼 활성화

        // 위치 추적 모드 활성화
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 위치 오버레이 설정
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        naverMap.addOnLocationChangeListener { currentLocation ->
            Log.d("NowLocation", "${currentLocation.latitude}  ${currentLocation.longitude}")
            NowLatitude = currentLocation.latitude
            NowLongitude = currentLocation.longitude

            // 지역번호에 해당하는 위도와 경도 가져오기
            val targetLocation = regionCoordinates[searchedRegionName]

            // targetLocation이 null이 아닌 경우에만 마커를 추가
            targetLocation?.let {
                // 지도에 마커 추가
                val marker = Marker()
                marker.position = it
                marker.map = naverMap

                // 지도 위치를 해당 지역으로 이동
                val cameraUpdate = CameraUpdate.scrollTo(it)
                naverMap.moveCamera(cameraUpdate)

                // 현재 위치와 targetLocation 사이의 거리 계산 (소수점 제거)
                val distance = calculateDistance(currentLocation.latitude, currentLocation.longitude, it.latitude, it.longitude).toInt()

                // TextView에 거리 표시 (정수형 거리 값을 %d로 포맷팅)
                binding.mySettingAreaInformationDistanceTv.text = String.format("%dkm", distance)
            } ?: run {
                Log.e("MapError", "해당 지역 이름에 대한 위치를 찾을 수 없습니다.")
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}