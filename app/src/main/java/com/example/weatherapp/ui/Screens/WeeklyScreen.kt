package com.example.weatherapp.ui.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R

@Composable
fun WeeklyScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 50.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sun),
                contentDescription = "Clear Day",
                Modifier.size(50.dp)
            )
        }
        Row() {
            Text(
                "October 1st",
                fontSize = 15.sp
            )
        }
        Row() {
            Text(
                "18°C",
                fontSize = 25.sp
            )
        }
        Row() {
            Text(
                "Clear skies, No chance of precipitation, Humidity of 35%",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(5.dp)
            )
        }
        HorizontalDivider(thickness = 2.dp)

        Row(
            modifier = Modifier
                .padding(vertical = 50.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cloudy),
                contentDescription = "Partly Cloudy",
                Modifier.size(50.dp)
            )
        }
        Row() {
            Text(
                "October 2nd",
                fontSize = 15.sp
            )
        }
        Row() {
            Text(
                "15°C",
                fontSize = 25.sp
            )
        }
        Row() {
            Text(
                "Party cloudy, No chance of precipitation, Humidity of 55%",
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(thickness = 2.dp)

        Row(
            modifier = Modifier
                .padding(vertical = 50.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.heavy_rain),
                contentDescription = "Heavy rain",
                Modifier.size(50.dp)
            )
        }
        Row() {
            Text(
                "October 3rd",
                fontSize = 15.sp
            )
        }
        Row() {
            Text(
                "12°C",
                fontSize = 25.sp
            )
        }
        Row() {
            Text(
                "Cloudy skies, 90% chance of precipitation, Humidity of 80%",
                textAlign = TextAlign.Center
            )
        }
    }
}
