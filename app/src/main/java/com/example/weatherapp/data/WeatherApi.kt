package com.example.weatherapp.data

import com.example.weatherapp.data.dao.Weather
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {
    @GET("/v1/forecast.json")
    suspend fun searchWeather(
        @Query("q") q: String = "", // location
        @Query("key") key: String = "a7cac05309364650b7b175312261910", // todo fix
        @Query("days") days: Int = 7,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"

    ): Weather
}