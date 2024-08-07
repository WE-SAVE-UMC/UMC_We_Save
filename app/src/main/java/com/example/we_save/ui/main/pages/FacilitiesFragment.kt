package com.example.we_save.ui.main.pages

import org.w3c.dom.Element
import okhttp3.Request
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.NaverDirectionService
import com.example.we_save.NaverSearchService
import com.example.we_save.Place
import com.example.we_save.R
import com.example.we_save.SearchResultsAdapter
import com.example.we_save.databinding.FragmentFacilitiesBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import org.json.XML
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.StringReader
import java.net.URLEncoder
import java.util.Locale
import java.util.concurrent.TimeUnit
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

    private val markers = mutableListOf<Marker>()

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                setupMap()
            } else {
                Toast.makeText(context, "권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
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

        binding.pharmacyLocation.setOnClickListener {
            fetchPharmacyDataWithinRadius()
        }

        binding.emergencyLocation.setOnClickListener {
            fetchEmergencyRoomData()
        }

        binding.myLocation.setOnClickListener {
            locationSource.lastLocation?.let { location ->
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                naverMap.moveCamera(cameraUpdate)
            } ?: Toast.makeText(context, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        if (hasAllPermissions()) {
            setupMap()
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

    private fun fetchPharmacyDataWithinRadius() {
        val center = naverMap.cameraPosition.target
        val radius = 5000 // 반경 5km 예시

        lifecycleScope.launch {
            val totalPageCount = getTotalPageCount("pharmacy")
            for (pageNo in 1..totalPageCount) {
                val url = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire?serviceKey=ZDE04T%2F8%2BdNZgK9LHd1i9FSVAESIpl7S%2F1NtsdCayF1ZGt9EiUq6G1K2iEhCAb%2Fto2jbI4UJxFz2vhVXHI%2FrBA%3D%3D&numOfRows=100&pageNo=$pageNo&QT=1&ORD=NAME"
                val response = fetchData(url)
                response?.let { parsePharmacyDataWithinRadius(it, center, radius) }
            }
        }
    }

    private fun fetchEmergencyRoomData() {
        lifecycleScope.launch {
            val totalPageCount = getTotalPageCount("emergency")
            for (pageNo in 1..totalPageCount) {
                val url = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList?ServiceKey=ZDE04T%2F8%2BdNZgK9LHd1i9FSVAESIpl7S%2F1NtsdCayF1ZGt9EiUq6G1K2iEhCAb%2Fto2jbI4UJxFz2vhVXHI%2FrBA%3D%3D&pageNo=$pageNo&numOfRows=100"
                val response = fetchData(url)
                Log.d("FacilitiesFragment", "Emergency Data Response: $response")
                response?.let { parseEmergencyRoomData(it) }
            }
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
                Log.d("FacilitiesFragment", "API Response: $responseBody")
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

                val distance = FloatArray(1)
                Location.distanceBetween(center.latitude, center.longitude, lat, lng, distance)
                if (distance[0] <= radius) {
                    Log.d("FacilitiesFragment", "Adding Pharmacy Marker: $name at ($lat, $lng)")
                    addMarker(lat, lng, name, address, "pharmacy")
                }
            }
        } catch (e: Exception) {
            Log.e("FacilitiesFragment", "Error parsing pharmacy data within radius", e)
        }
    }

    private fun parseEmergencyRoomData(response: String) {
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
                    Log.d("FacilitiesFragment", "Adding Emergency Room Marker: $name at ($lat, $lng)")
                    addMarker(lat, lng, name, address, "emergency")
                } else {
                    if (latNode == null) Log.e("FacilitiesFragment", "Missing latitude for emergency room item at index $i")
                    if (lngNode == null) Log.e("FacilitiesFragment", "Missing longitude for emergency room item at index $i")
                    if (nameNode == null) Log.e("FacilitiesFragment", "Missing name for emergency room item at index $i")
                    if (addressNode == null) Log.e("FacilitiesFragment", "Missing address for emergency room item at index $i")
                }
            }
        } catch (e: Exception) {
            Log.e("FacilitiesFragment", "Error parsing emergency room data", e)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun addMarker(lat: Double, lng: Double, title: String, address: String, type: String) {
        val marker = Marker()
        marker.position = LatLng(lat, lng)
        marker.map = naverMap
        marker.captionText = title

        val drawable = if (type == "pharmacy") {
            ContextCompat.getDrawable(requireContext(), R.drawable.pharmacy_marker)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.emergency_marker)
        }

        if (drawable != null) {
            val bitmap = drawableToBitmap(drawable)
            val overlayImage = OverlayImage.fromBitmap(bitmap)
            marker.icon = overlayImage
        }

        markers.add(marker)

        marker.setOnClickListener {
            showBottomSheetDialog(title, address, lat, lng)
            true
        }
    }

    private fun showBottomSheetDialog(title: String, address: String, lat: Double, lng: Double) {
        val bottomSheetView = layoutInflater.inflate(R.layout.facility_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val nameTextView = bottomSheetView.findViewById<TextView>(R.id.pharmacy_name)
        val distanceTextView = bottomSheetView.findViewById<TextView>(R.id.pharmacy_distance)
        val addressTextView = bottomSheetView.findViewById<TextView>(R.id.pharmacy_address)
        val directionsButton = bottomSheetView.findViewById<ImageView>(R.id.directions_button)

        nameTextView.text = title
        addressTextView.text = address

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}