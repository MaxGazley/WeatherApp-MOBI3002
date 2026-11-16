package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.WeatherApi
import com.example.weatherapp.data.dao.Weather
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        const val DEFAULT_CITY = "Halifax" // used if location is denied or unavailable
    }

    // Properties
    var weather by mutableStateOf<Weather?>(null)

    /** Non-null when the last API call failed, so the UI can show a message. */
    private var error by mutableStateOf<String?>(null)

    private var location by mutableStateOf<Location?>(null)

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)

    private val api = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com") // API hostname
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java) // Interface

    // Functions

    /** [q] can be a city name or "lat,lon". */
    fun searchWeather(q: String) {
        viewModelScope.launch {
            try {
                error = null
                weather = api.searchWeather(q)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Weather request failed", e)
                error = e.message ?: "Could not load weather"
            }
        }
    }

    /** Gets the phone's location, then loads the weather for it. Call only after permission is granted. */
    @SuppressLint("MissingPermission") // silence warning
    fun refreshLocation() {
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { result ->
                location = result
                if (result != null) {
                    Log.i("MainViewModel", "Phone location: ${result.latitude},${result.longitude}")
                    searchWeather("${result.latitude},${result.longitude}")
                } else {
                    // Happens on emulators with no location set
                    Log.w("MainViewModel", "Location was null, using default city")
                    searchWeather(DEFAULT_CITY)
                }
            }
            .addOnFailureListener {
                Log.e("MainViewModel", "Location request failed", it)
                searchWeather(DEFAULT_CITY)
            }
    }
}
