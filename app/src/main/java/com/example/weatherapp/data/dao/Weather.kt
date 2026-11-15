package com.example.weatherapp.data.dao

import com.google.gson.annotations.SerializedName

data class Weather(
    val location: Location,
    val current: CurrentWeather,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    @SerializedName("localtime") val localTime: String
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Forecast(@SerializedName("forecastday")
    val forecastDay: List<ForecastDay>
)

data class ForecastDay(
    val day: DayWeather,
    val hour: List<HourForecast>
)

data class DayWeather(
    @SerializedName("maxtemp_c") val maxTemp: Double,
    @SerializedName("mintemp_c") val minTemp: Double,
    @SerializedName("totalprecip_mm") val precipitation: Double,
    @SerializedName("daily_chance_of_rain") val rainChance: Int,
    val condition: Condition
)

data class HourForecast(
    @SerializedName("temp_c") val temp: Double, val condition: Condition,
    @SerializedName("wind_kph") val windSpeed: Double,
    @SerializedName("wind_dir") val windDirection: String,
    @SerializedName("chance_of_rain") val rainChance: Int,
    @SerializedName("precip_mm") val precipitation: Double
)

data class CurrentWeather(
    @SerializedName("temp_c") val temp: Double,
    val condition: Condition,
    @SerializedName("wind_kph") val windSpeed: Double,
    @SerializedName("wind_dir") val windDirection: String,
    @SerializedName("feelslike_c") val feelsLike: Double,
    @SerializedName("precip_mm") val totalPrecipitation: Double
)

data class DayPart(
    val label: String,
    val temp: Double,
    val rainChance: Int,
    val precip: Double,
    val condition: Condition
)