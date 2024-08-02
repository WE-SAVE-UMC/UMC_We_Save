package com.example.we_save.ui

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val locationRequest by lazy {
        LocationRequest.Builder(10000)
            .setMinUpdateDistanceMeters(100f)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
    }

    private val geocoder by lazy { Geocoder(application) }

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    val address = location.mapLatest { location ->
        if (location == null) return@mapLatest null

        withContext(Dispatchers.IO) {
            try {
                return@withContext geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )?.lastOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val client = LocationServices.getSettingsClient(application)
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            try {
                client.checkLocationSettings(builder.build()).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _location.value = fusedLocationClient.lastLocation.await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        updateLocation()
    }

    @SuppressLint("MissingPermission")
    fun updateLocation(callback: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _location.value =
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .await()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                launch(Dispatchers.Main) {
                    callback?.invoke()
                }
            }
        }
    }
}