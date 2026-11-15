package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.WeatherApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java
import com.example.weatherapp.data.dao.Weather
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.location.Location
import androidx.lifecycle.AndroidViewModel

class MainViewModel(app: Application) : AndroidViewModel(app) {
    //Properties
    var weather by mutableStateOf<Weather?>(null)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)

    var location by mutableStateOf<Location?>(null)
        private set

    private val api = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com") // API hostname
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java) // Interface

    init {
        //initializations
    }

    //Functions
    fun searchWeather (q: String) {
        // Launch co-routine
        viewModelScope.launch {
            weather = api.searchWeather(q)
        }
    }

    @SuppressLint("MissingPermission") // silence warning
    fun refreshLocation() {
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { result ->
                location = result
                val latlng = "lat: ${location?.latitude}, lng: ${location?.longitude}"
                Log.i("MainViewModel", "Phone is location at $latlng.")
            }
    }
}