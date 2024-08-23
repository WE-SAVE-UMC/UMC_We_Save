package com.example.we_save.ui.main.pages

import org.w3c.dom.Element
import okhttp3.Request
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.we_save.R
import com.example.we_save.common.extensions.customToast
import com.example.we_save.databinding.FragmentFacilitiesBinding
import com.example.we_save.ui.main.MainFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Response
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import javax.xml.parsers.DocumentBuilderFactory



class FacilitiesFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentFacilitiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var selectedButton: ImageView? = null
    private val markers = mutableListOf<Marker>()

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                setupMap()
            } else {
                requireContext().applicationContext.customToast("권한이 거부되었습니다")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacilitiesBinding.inflate(inflater, container, false)
        val view = binding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.near_facility_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.near_facility_map_view, it).commit()
            }
        mapFragment.getMapAsync(this)
        val mainFragment = parentFragment as? MainFragment
        binding.leftArrowIv.setOnClickListener {
            mainFragment?.setViewPagerPage(0)
        }

        binding.pharmacyLocation.setOnClickListener {
            onButtonClicked(it as ImageView,
                R.drawable.click_pharmacy_icon,
                R.drawable.pharmacy_icon)
            fetchPharmacyDataWithinRadius()
        }
        binding.hospitalLocation.setOnClickListener {
            onButtonClicked(it as ImageView,
                R.drawable.click_hospital_icon,
                R.drawable.hospital_icon)
            fetchHospitalDataWithinRadius()
        }

        binding.emergencyLocation.setOnClickListener {
            onButtonClicked(it as ImageView,
                R.drawable.click_emergency_icon,
                R.drawable.emergency_icon)
            fetchEmergencyRoomDataWithinRadius()
        }

        binding.shelterLocation.setOnClickListener {
            onButtonClicked(it as ImageView,
                R.drawable.click_shelter_icon,
                R.drawable.shelter_icon)
            fetchEmergencyAssemblyAreaData()
        }

        binding.myLocation.setOnClickListener {
            locationSource.lastLocation?.let { location ->
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                naverMap.moveCamera(cameraUpdate)
            } ?: requireContext().applicationContext.customToast("현재 위치를 가져올 수 없습니다.")
        }

        return view
    }

    private fun onButtonClicked(button: ImageView, selectedImage: Int, defaultImage: Int) {
        selectedButton?.let {
            when (it.id) {
                R.id.pharmacy_location -> setButtonImage(it, R.drawable.pharmacy_icon)
                R.id.emergency_location -> setButtonImage(it, R.drawable.emergency_icon)
                R.id.shelter_location -> setButtonImage(it, R.drawable.shelter_icon)
                R.id.hospital_location -> setButtonImage(it, R.drawable.hospital_icon)
            }
        }

        // 현재 클릭된 버튼의 이미지를 선택된 이미지로 변경
        setButtonImage(button, selectedImage)
        selectedButton = button // 현재 선택된 버튼으로 업데이트
    }

    private fun setButtonImage(button: ImageView, imageRes: Int) {
        button.setImageResource(imageRes)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.uiSettings.isZoomControlEnabled = false
        if (hasAllPermissions()) {
            setupMap()
            //fetchEmergencyAssemblyAreaData() // 지도 준비 후 대피소 데이터를 가져옴
        } else {
            requestPermissions.launch(permissions)
        }
    }

    private fun hasAllPermissions(): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun setupMap() {
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        locationSource.lastLocation?.let {
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude))
            naverMap.moveCamera(cameraUpdate)
        }
    }

    // 새로운 대피소 데이터를 가져오는 함수
    private fun fetchEmergencyAssemblyAreaData() {
        lifecycleScope.launch {
            try {
                // URL 업데이트
                val url = "https://apis.data.go.kr/1741000/EmergencyAssemblyArea_Earthquake5/getArea4List2?ServiceKey=ZDE04T%2F8%2BdNZgK9LHd1i9FSVAESIpl7S%2F1NtsdCayF1ZGt9EiUq6G1K2iEhCAb%2Fto2jbI4UJxFz2vhVXHI%2FrBA%3D%3D&pageNo=1&numOfRows=100&type=xml"
                val response = fetchData(url)
                response?.let {
                    parseEmergencyAssemblyAreaData(it)
                }
            } catch (e: Exception) {
                Log.e("FacilitiesFragment", "잘못된 주소 데이터", e)
            }
        }
    }

    // XML 데이터를 파싱하여 대피소 마커 추가
    private fun parseEmergencyAssemblyAreaData(response: String) {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val xmlDoc = builder.parse(InputSource(StringReader(response)))
            val items: NodeList = xmlDoc.getElementsByTagName("row")

            for (i in 0 until items.length) {
                val item = items.item(i) as Element
                val lat = item.getElementsByTagName("ycord").item(0).textContent.toDouble()
                val lng = item.getElementsByTagName("xcord").item(0).textContent.toDouble()
                val name = item.getElementsByTagName("vt_acmdfclty_nm").item(0).textContent
                val address = item.getElementsByTagName("rn_adres").item(0).textContent

                addShelterMarker(lat, lng, name, address)
            }
        } catch (e: Exception) {
            Log.e("FacilitiesFragment", "Error parsing emergency assembly area data", e)
        }
    }
    private fun addShelterMarker(lat: Double, lng: Double, name: String, address: String) {
        addMarker(lat, lng, name, address, null, null, "shelter")
    }

    // 약국 데이터를 가져오는 함수
    private fun fetchPharmacyDataWithinRadius() {
        val center = naverMap.cameraPosition.target
        val radius = 1000

        lifecycleScope.launch {
            val totalPageCount = getTotalPageCount("pharmacy")
            for (pageNo in 1..totalPageCount) {
                val url = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire?serviceKey=ZDE04T%2F8%2BdNZgK9LHd1i9FSVAESIpl7S%2F1NtsdCayF1ZGt9EiUq6G1K2iEhCAb%2Fto2jbI4UJxFz2vhVXHI%2FrBA%3D%3D&numOfRows=100&pageNo=$pageNo&QT=1&ORD=NAME"
                val response = fetchData(url)
                response?.let { parsePharmacyDataWithinRadius(it, center, radius) }
            }
        }
    }

    // 반경 내 응급실 데이터를 가져오는 함수
    private fun fetchEmergencyRoomDataWithinRadius() {
        val center = naverMap.cameraPosition.target
        val radius = 5000 // 반경 5km

        lifecycleScope.launch {
            val totalPageCount = getTotalPageCount("emergency")
            for (pageNo in 1..totalPageCount) {
                val url = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList?ServiceKey=ZDE04T%2F8%2BdNZgK9LHd1i9FSVAESIpl7S%2F1NtsdCayF1ZGt9EiUq6G1K2iEhCAb%2Fto2jbI4UJxFz2vhVXHI%2FrBA%3D%3D&pageNo=$pageNo&numOfRows=100"
                val response = fetchData(url)
                response?.let { parseEmergencyRoomDataWithinRadius(it, center, radius) }
            }
        }
    }

    // 반경 내의 응급실 데이터만 파싱
    private fun parseEmergencyRoomDataWithinRadius(response: String, center: LatLng, radius: Int) {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val xmlDoc = builder.parse(InputSource(StringReader(response)))
            val items: NodeList = xmlDoc.getElementsByTagName("item")

            for (i in 0 until items.length) {
                val item = items.item(i) as Element
                val latNode = item.getElementsByTagName("YPos").item(0)
                val lngNode = item.getElementsByTagName("XPos").item(0)
                val nameNode = item.getElementsByTagName("yadmNm").item(0)
                val addressNode = item.getElementsByTagName("addr").item(0)

                if (latNode != null && lngNode != null && nameNode != null && addressNode != null) {
                    val lat = latNode.textContent.toDouble()
                    val lng = lngNode.textContent.toDouble()
                    val name = nameNode.textContent
                    val address = addressNode.textContent

                    // 반경 내에 있는지 확인
                    val distance = FloatArray(1)
                    Location.distanceBetween(center.latitude, center.longitude, lat, lng, distance)
                    if (distance[0] <= radius) {
                        Log.d("FacilitiesFragment", "Adding Emergency Room Marker: $name at ($lat, $lng)")
                        addMarker(lat, lng, name, address, null, null, "emergency")
                    }
                } else {
                    if (latNode == null) Log.e("FacilitiesFragment", "잘못된 위도 값: $i")
                    if (lngNode == null) Log.e("FacilitiesFragment", "잘못된 경도 값 $i")
                    if (nameNode == null) Log.e("FacilitiesFragment", "잘못된 응급실 값 $i")
                    if (addressNode == null) Log.e("FacilitiesFragment", "잘못된 주소 값 $i")
                }
            }
        } catch (e: Exception) {
            Log.e("FacilitiesFragment", "Error parsing emergency room data within radius", e)
        }
    }

    // 병원의 데이터를 가져온다.
    private fun fetchHospitalDataWithinRadius() {
        val center = naverMap.cameraPosition.target
        val radius = 5000 // 반경 5km

        lifecycleScope.launch {
            val totalPageCount = getTotalPageCount("hospital")
            for (pageNo in 1..totalPageCount) {
                val url = "https://safemap.go.kr/openApiService/data/getGenralHospitalData.do?serviceKey=U1OZHA2A-U1OZ-U1OZ-U1OZ-U1OZHA2ABC&numOfRows=100&pageNo=$pageNo&dataType=XML&DutyDiv=C"
                val response = fetchData(url)
                response?.let { parseHospitalDataWithinRadius(it, center, radius) }
            }
        }
    }
    private fun parseHospitalDataWithinRadius(response: String, center: LatLng, radius: Int) {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val xmlDoc = builder.parse(InputSource(StringReader(response)))
            val items: NodeList = xmlDoc.getElementsByTagName("item")

            for (i in 0 until items.length) {
                val item = items.item(i) as Element
                val latNode = item.getElementsByTagName("LAT").item(0)
                val lngNode = item.getElementsByTagName("LON").item(0)
                val nameNode = item.getElementsByTagName("DUTYNAME").item(0)
                val addressNode = item.getElementsByTagName("DUTYADDR").item(0)

                if (latNode != null && lngNode != null && nameNode != null && addressNode != null) {
                    val lat = latNode.textContent.toDouble()
                    val lng = lngNode.textContent.toDouble()
                    val name = nameNode.textContent
                    val address = addressNode.textContent

                    // 반경 내에 있는지 확인
                    val distance = FloatArray(1)
                    Location.distanceBetween(center.latitude, center.longitude, lat, lng, distance)
                    if (distance[0] <= radius) {
                        Log.d("FacilitiesFragment", "Adding Hospital Marker: $name at ($lat, $lng)")
                        addMarker(lat, lng, name, address, null, null, "hospital")
                    }
                } else {
                    if (latNode == null) Log.e("FacilitiesFragment", "Missing latitude for hospital item at index $i")
                    if (lngNode == null) Log.e("FacilitiesFragment", "Missing longitude for hospital item at index $i")
                    if (nameNode == null) Log.e("FacilitiesFragment", "Missing name for hospital item at index $i")
                    if (addressNode == null) Log.e("FacilitiesFragment", "Missing address for hospital item at index $i")
                }
            }
        } catch (e: Exception) {
            Log.e("FacilitiesFragment", "Error parsing hospital data within radius", e)
        }
    }

    private suspend fun getTotalPageCount(type: String): Int {
        val url = if (type == "pharmacy") {
            "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire?serviceKey=ZDE04T%2F8%2BdNZgK9LHd1i9FSVAESIpl7S%2F1NtsdCayF1ZGt9EiUq6G1K2iEhCAb%2Fto2jbI4UJxFz2vhVXHI%2FrBA%3D%3D&numOfRows=1&pageNo=1&QT=1&ORD=NAME"
        } else {
            "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList?ServiceKey=ZDE04T%2F8%2BdNZgK9LHd1i9FSVAESIpl7S%2F1NtsdCayF1ZGt9EiUq6G1K2iEhCAb%2Fto2jbI4UJxFz2vhVXHI%2FrBA%3D%3D&pageNo=1&numOfRows=1"
        }
        val response = fetchData(url)
        Log.d("FacilitiesFragment", "Total Page Count Response ($type): $response")
        return response?.let {
            parseTotalCount(it)
        } ?: 0
    }

    private fun parseTotalCount(response: String): Int {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val xmlDoc = builder.parse(InputSource(StringReader(response)))
            val totalCountNode = xmlDoc.getElementsByTagName("totalCount").item(0)
            totalCountNode.textContent.toInt()
        } catch (e: Exception) {
            Log.e("FacilitiesFragment", "Error parsing total count", e)
            0
        }
    }

    private suspend fun fetchData(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.d("FacilitiesFragment", "API Response: $responseBody") // 응답 내용 로그에 출력
                responseBody
            } catch (e: Exception) {
                Log.e("FacilitiesFragment", "Error fetching data from server", e)
                null
            }
        }
    }

    private fun parsePharmacyDataWithinRadius(response: String, center: LatLng, radius: Int) {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val xmlDoc = builder.parse(InputSource(StringReader(response)))
            val items: NodeList = xmlDoc.getElementsByTagName("item")

            for (i in 0 until items.length) {
                val item = items.item(i) as Element
                val lat = item.getElementsByTagName("wgs84Lat").item(0).textContent.toDouble()
                val lng = item.getElementsByTagName("wgs84Lon").item(0).textContent.toDouble()
                val name = item.getElementsByTagName("dutyName").item(0).textContent
                val address = item.getElementsByTagName("dutyAddr").item(0).textContent
                val dutyTimeStart = item.getElementsByTagName("dutyTime1s").item(0)?.textContent
                val dutyTimeEnd = item.getElementsByTagName("dutyTime1c").item(0)?.textContent

                val distance = FloatArray(1)
                Location.distanceBetween(center.latitude, center.longitude, lat, lng, distance)
                if (distance[0] <= radius) {
                    Log.d("FacilitiesFragment", "Adding Pharmacy Marker: $name at ($lat, $lng)")
                    addMarker(lat, lng, name, address, dutyTimeStart, dutyTimeEnd, "pharmacy")
                }
            }
        } catch (e: Exception) {
            Log.e("FacilitiesFragment", "Error parsing pharmacy data within radius", e)
        }
    }

    // 마커를 추가하는 함수
    private fun addMarker(
        lat: Double,
        lng: Double,
        title: String,
        address: String,
        startTime: String?,
        endTime: String?,
        type: String
    ) {
        val marker = Marker()
        marker.position = LatLng(lat, lng)
        marker.map = naverMap
        marker.captionText = title

        val drawable = when (type) {
            "pharmacy" -> ContextCompat.getDrawable(requireContext(), R.drawable.pharmacy_marker)
            "emergency" -> ContextCompat.getDrawable(requireContext(), R.drawable.emergency_marker)
            "shelter" -> ContextCompat.getDrawable(requireContext(), R.drawable.shleter_marker)
            "hospital"-> ContextCompat.getDrawable(requireContext(), R.drawable.hospital_marker)
            else -> null
        }

        drawable?.let {
            val bitmap = drawableToBitmap(it)
            val overlayImage = OverlayImage.fromBitmap(bitmap)
            marker.icon = overlayImage
        }

        Log.d("FacilitiesFragment", "Marker added at lat: $lat, lng: $lng with name: $title")

        marker.setOnClickListener {
            showBottomSheetDialog(title, address, startTime, endTime, type, lat, lng)
            true
        }

        markers.add(marker)
    }

    private fun showBottomSheetDialog(title: String, address: String, startTime: String?, endTime: String?, type: String, lat: Double, lng: Double) {
        val bottomSheetView = layoutInflater.inflate(R.layout.facility_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())

            bottomSheetDialog.setContentView(bottomSheetView)
        val nameTextView = bottomSheetView.findViewById<TextView>(R.id.pharmacy_name)
        val startTimeTextView = bottomSheetView.findViewById<TextView>(R.id.start_time_tv)
        val endTimeTextView = bottomSheetView.findViewById<TextView>(R.id.end_time_tv)
        val openStatusTextView = bottomSheetView.findViewById<TextView>(R.id.opne_time_tv)
        val openStatusImageView = bottomSheetView.findViewById<ImageView>(R.id.status_image_view)
        val addressTextView = bottomSheetView.findViewById<TextView>(R.id.pharmacy_address)
        val distanceTextView = bottomSheetView.findViewById<TextView>(R.id.pharmacy_distance)
        val directionsButton = bottomSheetView.findViewById<ImageView>(R.id.directions_button)
        val midIv = bottomSheetView.findViewById<TextView>(R.id.mid_time_iv)

        nameTextView.text = title
        addressTextView.text = formatAddress(address)

        // type에 따라 시간 관련 UI를 표시하거나 숨김
        if (type == "pharmacy" ) {
            startTimeTextView.visibility = View.VISIBLE
            endTimeTextView.visibility = View.VISIBLE
            midIv.visibility = TextView.VISIBLE

            startTimeTextView.text = formatTime(startTime ?: "")
            endTimeTextView.text = formatTime(endTime ?: "")

            val isOpen = checkIfOpen(startTime ?: "", endTime ?: "", LocalTime.now())
            openStatusTextView.text = if (isOpen) "영업중" else "영업 종료"
            openStatusImageView.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (isOpen) R.color.blue_40 else R.color.red_40)
        } else {
            startTimeTextView.visibility = View.GONE
            endTimeTextView.visibility = View.GONE
            midIv.visibility = TextView.GONE
            openStatusTextView.text = "진료중"
        }

        val currentLocation = locationSource.lastLocation
        if (currentLocation != null) {
            val distance = FloatArray(1)
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, lat, lng, distance)
            distanceTextView.text = "거리: ${distance[0].toInt()}m"
        } else {
            distanceTextView.text = "거리 정보를 가져올 수 없습니다."
        }

        directionsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng($title)"))
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun checkIfOpen(startTime: String, endTime: String, currentTime: LocalTime): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        val start = LocalTime.parse(startTime, formatter)
        val end = LocalTime.parse(endTime, formatter)

        return if (end.isAfter(start)) {
            currentTime.isAfter(start) && currentTime.isBefore(end)
        } else {
            currentTime.isAfter(start) || currentTime.isBefore(end)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    private fun formatTime(time: String): String {
        return try {
            val parsedTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"))
            parsedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: DateTimeParseException) {
            time
        }
    }
    private fun formatAddress(address: String): String {
        val parts = address.split(" ")
        return if (parts.size >= 3) {
            "${parts[1]} ${parts[2]}"
        } else {
            address
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}


