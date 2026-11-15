package com.example.weatherapp.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.data.dao.Condition
import com.example.weatherapp.data.dao.HourForecast
import com.example.weatherapp.data.dao.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

val iconCache = LruCache<String, Bitmap>(60)

/** Downloads an image from [model] (a URL) and shows it. Cached in memory; blank while loading/failed. */
@Composable
fun WeatherIcon(model: String, contentDescription: String?, modifier: Modifier = Modifier) {
    val bitmap by produceState<Bitmap?>(initialValue = null, model) {
        value = iconCache.get(model) ?: withContext(Dispatchers.IO) {
            try {
                val conn = URL(model).openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.inputStream.use { BitmapFactory.decodeStream(it) }
                    ?.also { iconCache.put(model, it) }
            } catch (e: Exception) {
                null
            }
        }
    }
    val bmp = bitmap
    if (bmp != null) {
        Image(bitmap = bmp.asImageBitmap(), contentDescription = contentDescription, modifier = modifier)
    } else {
        Box(modifier)
    }
}

// ======================= HELPERS =======================

fun Condition.iconUrl(): String =
    if (icon.startsWith("//")) "https:$icon" else icon

fun Double.deg() = "${roundToInt()}°"

/** Parses Location.localTime, e.g. "2026-11-15 14:30" (also handles unpadded "2026-11-5 4:30"). */
fun Weather.localNow(): LocalDateTime =
    LocalDateTime.parse(location.localTime, DateTimeFormatter.ofPattern("yyyy-M-d H:mm"))

/** Next 24 hours starting at the current hour, paired with an hour-of-day label. */
fun Weather.upcomingHours(): List<Pair<String, HourForecast>> {
    val startHour = localNow().hour
    return forecast.forecastDay
        .flatMap { day -> day.hour.mapIndexed { h, hf -> "%02d:00".format(h) to hf } }
        .drop(startHour)
        .take(24)
        .mapIndexed { i, (label, hf) -> (if (i == 0) "Now" else label) to hf }
}

// ======================= SCREEN =======================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyScreen(viewModel: MainViewModel) {
    val weather = viewModel.weather

    // Show a spinner until the API data has loaded (replaces the unsafe weather!!)
    if (weather == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val current = weather.current
    val hours = remember(weather) { weather.upcomingHours() }
    val todayRainChance = weather.forecast.forecastDay.firstOrNull()?.day?.rainChance ?: 0

    Scaffold(topBar = { TopAppBar(title = { Text("Weather") }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(weather.location.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(
                "${weather.location.region}, ${weather.location.country}",
                style = MaterialTheme.typography.bodyMedium
            )
            WeatherIcon(
                model = current.condition.iconUrl(),
                contentDescription = current.condition.text,
                modifier = Modifier.size(120.dp)
            )
            Text("${current.temp.roundToInt()}°C", fontSize = 64.sp, fontWeight = FontWeight.Light)
            Text(current.condition.text, fontSize = 18.sp)
            Text("Feels like ${current.feelsLike.deg()}")

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Stat("Rain now", "${current.totalPrecipitation} mm")
                Stat("Chance today", "$todayRainChance%")
                Stat("Wind", "${current.windSpeed.roundToInt()} km/h ${current.windDirection}")
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Hourly forecast",
                Modifier.align(Alignment.Start),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(hours) { (label, hour) -> HourCard(label, hour) }
            }

//            Spacer(Modifier.height(24.dp))
//            Button(onClick = onOpenForecast, modifier = Modifier.fillMaxWidth()) {
//                Text("3-day forecast")
//            }
        }
    }
}

@Composable
fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun HourCard(label: String, h: HourForecast) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            WeatherIcon(
                model = h.condition.iconUrl(),
                contentDescription = h.condition.text,
                modifier = Modifier.size(44.dp)
            )
            Text(h.temp.deg(), fontWeight = FontWeight.SemiBold)
            Text("${h.rainChance}%", style = MaterialTheme.typography.labelSmall)
            Text("${h.precipitation}mm", style = MaterialTheme.typography.labelSmall)
        }
    }
}
