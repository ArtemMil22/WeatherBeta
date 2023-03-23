package com.example.weatherappcursey.model

data class WeatherModel(
    val city:String,
    val time:String,
    val condition:String,
    val currentTemp:String,
    val maxTemp:String,
    val minTemp:String,
    val imageUrl:String,
    val hours:String //будем хранить инфу по часам в этой в сыром виде
)
