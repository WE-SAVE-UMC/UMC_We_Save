package com.example.we_save.ui.main.pages
import okhttp3.Request
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.NaverSearchService
import com.example.we_save.Place
import com.example.we_save.R
import com.example.we_save.SearchResultsAdapter
import com.example.we_save.databinding.FragmentFacilitiesBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class FacilitiesFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentFacilitiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var startSearchAdapter: SearchResultsAdapter
    private lateinit var endSearchAdapter: SearchResultsAdapter

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

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

        setupSearch()

        binding.startSearchResultsRecyclerView.layoutManager = LinearLayoutManager(context)
        startSearchAdapter = SearchResultsAdapter { place ->
            setEditText(binding.startLocationEditText, place.title)
            binding.startSearchResultsRecyclerView.visibility = View.GONE
        }
        binding.startSearchResultsRecyclerView.adapter = startSearchAdapter

        binding.endSearchResultsRecyclerView.layoutManager = LinearLayoutManager(context)
        endSearchAdapter = SearchResultsAdapter { place ->
            setEditText(binding.endLocationEditText, place.title)
            binding.endSearchResultsRecyclerView.visibility = View.GONE
        }
        binding.endSearchResultsRecyclerView.adapter = endSearchAdapter

        binding.findDirectionsButton.setOnClickListener {
            val startLocation = binding.startLocationEditText.text.toString()
            val endLocation = binding.endLocationEditText.text.toString()
            if (startLocation.isNotBlank() && endLocation.isNotBlank()) {
                searchPlacesAndFindDirections(startLocation, endLocation)
            } else {
                Toast.makeText(context, "출발지와 도착지를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private var startLocationTextWatcher: TextWatcher? = null
    private var endLocationTextWatcher: TextWatcher? = null

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

    private fun setupSearch() {
        startLocationTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    searchPlaces(s.toString(), isStartLocation = true)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        endLocationTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    searchPlaces(s.toString(), isStartLocation = false)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.startLocationEditText.addTextChangedListener(startLocationTextWatcher)
        binding.endLocationEditText.addTextChangedListener(endLocationTextWatcher)
    }

    private fun setEditText(editText: EditText, text: String) {
        editText.removeTextChangedListener(if (editText == binding.startLocationEditText) startLocationTextWatcher else endLocationTextWatcher)
        editText.setText(text)
        editText.setSelection(text.length)
        editText.addTextChangedListener(if (editText == binding.startLocationEditText) startLocationTextWatcher else endLocationTextWatcher)
    }

    private fun searchPlaces(query: String, isStartLocation: Boolean) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val naverPlaceSearchService = retrofit.create(NaverSearchService::class.java)

        lifecycleScope.launch {
            try {
                val clientId = "vdqKo5kQa1fN6o27fotj"
                val clientSecret = "OKGGoXgUCx"
                val response = naverPlaceSearchService.searchPlaces(query, 5, 1, "random", clientId, clientSecret)
                displayPlaces(response.items, isStartLocation)
            } catch (e: Exception) {
                Log.e("FacilitiesFragment", "Error searching places", e)
            }
        }
    }

    private fun displayPlaces(places: List<Place>, isStartLocation: Boolean) {
        val formattedPlaces = places.map {
            it.copy(title = Html.fromHtml(it.title, Html.FROM_HTML_MODE_LEGACY).toString()) // HTML 태그 제거
        }

        if (isStartLocation) {
            startSearchAdapter.submitList(formattedPlaces)
            binding.startSearchResultsRecyclerView.visibility = View.VISIBLE
        } else {
            endSearchAdapter.submitList(formattedPlaces)
            binding.endSearchResultsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun searchPlacesAndFindDirections(startQuery: String, endQuery: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val naverPlaceSearchService = retrofit.create(NaverSearchService::class.java)

        lifecycleScope.launch {
            try {
                val clientId = "vdqKo5kQa1fN6o27fotj"
                val clientSecret = "OKGGoXgUCx"
                val startResponse = naverPlaceSearchService.searchPlaces(startQuery, 5, 1, "random", clientId, clientSecret)
                val endResponse = naverPlaceSearchService.searchPlaces(endQuery, 5, 1, "random", clientId, clientSecret)

                if (startResponse.items.isNotEmpty() && endResponse.items.isNotEmpty()) {
                    val startPlace = startResponse.items[0]
                    val endPlace = endResponse.items[0]

                    val startLatLng = LatLng(startPlace.mapy.toDoubleOrNull() ?: 0.0, startPlace.mapx.toDoubleOrNull() ?: 0.0)
                    val endLatLng = LatLng(endPlace.mapy.toDoubleOrNull() ?: 0.0, endPlace.mapx.toDoubleOrNull() ?: 0.0)

                    findDirections(startLatLng, endLatLng)
                } else {
                    Toast.makeText(context, "출발지 또는 도착지를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FacilitiesFragment", "Error searching places for directions", e)
            }
        }
    }

    private fun findDirections(startLocation: LatLng, endLocation: LatLng) {
        val apiKey = "OKGGoXgUCx"
        val clientId = "vdqKo5kQa1fN6o27fotj"
        val startLocationString = "${startLocation.longitude},${startLocation.latitude}"
        val endLocationString = "${endLocation.longitude},${endLocation.latitude}"
        val url = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=$startLocationString&goal=$endLocationString"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("X-NCP-APIGW-API-KEY-ID", clientId)
            .addHeader("X-NCP-APIGW-API-KEY", apiKey)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { responseData ->
                    val jsonObject = JSONObject(responseData)
                    val route = jsonObject.getJSONObject("route").getJSONArray("traoptimal")
                    val path = route.getJSONObject(0).getJSONArray("path")

                    requireActivity().runOnUiThread {
                        val pathOverlay = com.naver.maps.map.overlay.PathOverlay()
                        val coords = mutableListOf<LatLng>()

                        for (i in 0 until path.length()) {
                            val point = path.getJSONArray(i)
                            val lat = point.getDouble(1)
                            val lng = point.getDouble(0)
                            coords.add(LatLng(lat, lng))
                        }

                        pathOverlay.coords = coords
                        pathOverlay.map = naverMap
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}