package com.example.we_save.ui

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.we_save.data.model.ReverseGeocoding
import com.example.we_save.domain.repositories.GeoCodingRepositoryImpl
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val locationRequest by lazy {
        LocationRequest.Builder(1000)
            .setMinUpdateDistanceMeters(100f)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
    }

    private val repository by lazy { GeoCodingRepositoryImpl.getInstance() }

    val address = MutableStateFlow<ReverseGeocoding?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val client = LocationServices.getSettingsClient(application)
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            try {
                client.checkLocationSettings(builder.build()).await()
                val location = fusedLocationClient.lastLocation.await()
                updateAddress(location)
            } catch (e: Exception) {
                e.printStackTrace()

                updateAddress()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateAddress(
        location: Location? = null,
        callback: ((ReverseGeocoding?, Exception?) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val location =
                    location ?: fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    )
                        .await()
                val response =
                    repository.reverseGeocoding(location.latitude, location.longitude).body()
                if (response?.results?.isEmpty() == true) {
                    delay(1000)
                    updateAddress(callback = callback)
                    return@launch
                }

                address.tryEmit(response)

                launch(Dispatchers.Main) {
                    callback?.invoke(response, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()

                launch(Dispatchers.Main) {
                    callback?.invoke(null, e)
                }
            }
        }
    }

    suspend fun getAddress(latitude: Double, longitude: Double) =
        repository.reverseGeocoding(latitude, longitude).body()
}