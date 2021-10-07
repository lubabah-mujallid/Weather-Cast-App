package com.example.weathercastapp

import retrofit2.Call
import retrofit2.http.GET

interface APIInterface {
    @GET("weather?zip=10001,us&appid=a6a7d46b67487feaa22f2fecc3cc2531")
    fun getWeatheCast(): Call<WeatherCast>
}