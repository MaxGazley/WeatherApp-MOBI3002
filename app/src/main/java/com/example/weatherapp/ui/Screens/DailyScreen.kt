package com.example.weatherapp.ui.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun DailyScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()

    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 50.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.sun),
                contentDescription = "Clear Day")
        }
        Row() {
            Text(
                "October 1st",
                fontSize = 20.sp
            )
        }
        Row() {
            Text("18°C",
                fontSize = 25.sp)
        }
        Row() {
            Text("Clear skies, No chance of precipitation, Humidity of 35%",
                textAlign = TextAlign.Center)
        }
    }
}