package com.example.we_save.ui.my

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.we_save.R
import com.example.we_save.data.apiservice.HeartInterface
import com.example.we_save.data.apiservice.HeartRequest
import com.example.we_save.data.apiservice.HeartResponse
import com.example.we_save.data.apiservice.MyPostInterface
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMySettingAreaBinding
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

class MySettingAreaFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMySettingAreaBinding? = null
    private val binding get() = _binding!!
    private var naverMap: NaverMap? = null
    private lateinit var locationSource: FusedLocationSource
    private var heartCount:Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMySettingAreaBinding.inflate(inflater, container, false)

        getHeart()

        // 지도 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.my_setting_area_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.my_setting_area_map_view, it).commit()
            }
        mapFragment.getMapAsync(this)


        // Start HeaderActivity with an intent
        val intent = Intent(context, HeaderActivity::class.java)


        var regionName = ""
        // 검색 버튼 -> 지역 이름 저장 ex) "부산 중구 남포동"
        binding.mySettingAreaSearchIv.setOnClickListener {
            regionName = binding.mySettingAreaSearchEt.text.toString()
            binding.mySettingAreaInformationNameTv.text = regionName

        }

        // 관심 지역 등록 버튼
        binding.mySettingAreaInformationRegist.setOnClickListener{
            if(heartCount == 0 || heartCount == 1){
                heartCount += 1
                postHeart(regionName)
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


        return binding.root
    }

    private fun getHeart(){
        val HeartService = getRetrofit().create(HeartInterface::class.java)

        HeartService.getHeart(getJwt()).enqueue(object : retrofit2.Callback<HeartResponse>{
            override fun onResponse(call: Call<HeartResponse>, response: Response<HeartResponse>) {
                Log.d("GETHeart/SUCCESS", response.toString())

                val resp = response.body()!!
                // 등록한 관심지역이 있는 경우
                if(resp.result.heartDTOs != null){
                    // "등록된 관심 지역이 없습니다" 문구 없애기
                    binding.mySettingAreaInterestNoTv.visibility = View.GONE

                    if(resp.result.heartDTOs[0] != null){
                        // 첫 번째 관심 지역 설정
                        binding.mySettingAreaInterestArea1.visibility = View.VISIBLE
                        binding.mySettingAreaInterestArea1Tv.text = resp.result.heartDTOs[0].regionName
                    }

                    if(resp.result.heartDTOs[1] != null){
                        // 두 번째 관심 지역 설정
                        binding.mySettingAreaInterestArea2.visibility = View.VISIBLE
                        binding.mySettingAreaInterestArea2Tv.text = resp.result.heartDTOs[1].regionName

                    }
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

        HeartService.postHeart(getJwt(), heartRequest).enqueue(object : retrofit2.Callback<HeartResponse>{
            override fun onResponse(call: Call<HeartResponse>, response: Response<HeartResponse>) {
                Log.d("POSTHeart/SUCCESS", response.toString())
            }

            override fun onFailure(call: Call<HeartResponse>, t: Throwable) {
                Log.d("POSTHeart/FAIL", t.message.toString())
            }

        })
    }

    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap


        // ui 세팅
        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
        uiSettings.isLocationButtonEnabled = true  // 현재 위치 버튼 활성화

        // 지도에서 현재 위치 추적 활성화
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 현재 위치 표시를 위한 LocationOverlay 설정
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true





//        // 공릉2동의 위도와 경도
//        val gongneung2dong = LatLng(37.6237, 127.0726)
//
//        // 마커 추가
//        val marker = Marker()
//        marker.position = gongneung2dong
//        marker.map = naverMap
//
//        // 지도 위치를 공릉2동으로 이동
//        val cameraUpdate = CameraUpdate.scrollTo(gongneung2dong)
//        naverMap.moveCamera(cameraUpdate)
    }


    // 지오코딩 요청
    fun getAddressFromLatLng(lat: Double, lng: Double, clientId: String, clientSecret: String) {
        val client = OkHttpClient()
        val url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=$lng,$lat&orders=addr_detail"
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Naver-Client-Id", clientId)
            .addHeader("X-Naver-Client-Secret", clientSecret)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val responseData = response.body?.string() ?: ""
                val json = JSONObject(responseData)
                val address = json.getJSONObject("results").getJSONObject("region").getString("area3")
                println("동 정보: $address")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private var _binding: FragmentMySettingAreaBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var searchEditText: EditText
//    private var naverMap: NaverMap? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentMySettingAreaBinding.inflate(inflater, container, false)
//
//        searchEditText = binding.mySettingAreaSearchEt
//
//        // Initialize the map
//        val mapFragment = childFragmentManager.findFragmentById(R.id.my_setting_area_map_view) as MapFragment?
//            ?: MapFragment.newInstance().also {
//                childFragmentManager.beginTransaction().add(R.id.my_setting_area_map_view, it).commit()
//            }
//        mapFragment.getMapAsync(this)
//
//        // Set up the search EditText listener
//        searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // Start searching when text changes
//                val query = s.toString()
//                if (query.isNotBlank()) {
//                    searchPlace(query)
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//        })
//
//        return binding.root
//    }
//
//    override fun onMapReady(naverMap: NaverMap) {
//        this.naverMap = naverMap
//        naverMap.uiSettings.isLocationButtonEnabled = true
//    }
//
//    private fun searchPlace(query: String) {
//        val search = SearchAPI()
//        search.searchPlace(query, object : SearchAPI.Callback {
//            override fun onResult(results: List<SearchResult>) {
//                if (results.isNotEmpty()) {
//                    val result = results[0]
//                    val location = LatLng(result.latitude, result.longitude)
//
//                    // Update map with new location
//                    val marker = Marker()
//                    marker.position = location
//                    marker.map = naverMap
//
//                    val cameraUpdate = CameraUpdate.scrollTo(location)
//                    naverMap?.moveCamera(cameraUpdate)
//                } else {
//                    Log.d("SEARCH", "No results found.")
//                }
//            }
//
//            override fun onError(error: String) {
//                Log.e("SEARCH", error)
//            }
//        })
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    // 네이버 장소 검색 API 호출
//    class SearchAPI {
//
//        private val client = OkHttpClient()
//        private val API_URL = "https://openapi.naver.com/v1/search/local.json"
//
//        fun searchPlace(query: String, callback: Callback) {
//            val url = "$API_URL?query=$query"
//            val request = Request.Builder()
//                .url(url)
//                .addHeader("X-Naver-Client-Id", "tgh2opnw8o")
//                .addHeader("X-Naver-Client-Secret", "YOUR_CLIENT_SECRET")
//                .build()
//
//
//            client.newCall(request).enqueue(object : okhttp3.Callback {
//                override fun onFailure(call: okhttp3.Call, e: IOException) {
//                    callback.onError(e.message ?: "Unknown error")
//                }
//
//                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                    if (response.isSuccessful) {
//                        val responseData = response.body?.string() ?: ""
//                        val json = JSONObject(responseData)
//                        val items = json.getJSONArray("items")
//                        val results = mutableListOf<SearchResult>()
//
//                        for (i in 0 until items.length()) {
//                            val item = items.getJSONObject(i)
//                            val lat = item.getString("lat")
//                            val lng = item.getString("lng")
//                            val result = SearchResult(lat.toDouble(), lng.toDouble())
//                            results.add(result)
//                        }
//                        callback.onResult(results)
//                    } else {
//                        callback.onError("API response error")
//                    }
//                }
//            })
//        }
//
//        interface Callback {
//            fun onResult(results: List<SearchResult>)
//            fun onError(error: String)
//        }
//    }
//
//    data class SearchResult(
//        val latitude: Double,
//        val longitude: Double
//    )
}