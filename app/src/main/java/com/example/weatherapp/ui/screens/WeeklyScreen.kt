package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.data.dao.Condition
import com.example.weatherapp.data.dao.DayPart
import com.example.weatherapp.data.dao.HourForecast
import java.time.format.DateTimeFormatter


private fun List<HourForecast>.dayParts(): List<DayPart> =
    listOf(
        Triple("Night", 0..4, 2),
        Triple("Morning", 5..9, 8),
        Triple("Midday", 10..14, 12),
        Triple("Afternoon", 15..19, 17),
        Triple("Evening", 20..23, 21)
    ).mapNotNull { (label, range, iconHour) ->
        val slice = slice(range.filter { it < size })
        if (slice.isEmpty()) return@mapNotNull null
        DayPart(
            label = label,
            temp = slice.map { it.temp }.average(),
            rainChance = slice.maxOf { it.rainChance },
            precip = slice.sumOf { it.precipitation },
            condition = (getOrNull(iconHour) ?: slice.first()).condition
        )
    }

// ======================= SCREEN =======================

@Composable
fun WeeklyScreen(viewModel: MainViewModel) {
    val weather = viewModel.weather

    // Spinner until the API data has loaded
    if (weather == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val today = remember(weather) { weather.localNow().toLocalDate() }
    val dateFormat = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(weather.forecast.forecastDay.take(3).withIndex().toList()) { (i, day) ->
            val title = when (i) {
                0 -> "Today"
                1 -> "Tomorrow"
                else -> today.plusDays(i.toLong()).format(dateFormat)
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Text("${day.day.maxTemp.deg()} / ${day.day.minTemp.deg()}")
                    }
                    Text(
                        "${day.day.condition.text} • ${day.day.rainChance}% rain • ${day.day.precipitation} mm",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        day.hour.dayParts().forEach { p ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(p.label, style = MaterialTheme.typography.labelLarge)
                                AsyncImage(
                                    model = p.condition.iconUrl(),
                                    contentDescription = p.condition.text,
                                    modifier = Modifier.size(52.dp)
                                )
                                Text(p.temp.deg(), fontWeight = FontWeight.SemiBold)
                                Text("Rain ${p.rainChance}%", style = MaterialTheme.typography.labelSmall)
                                Text("${"%.1f".format(p.precip)} mm", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
