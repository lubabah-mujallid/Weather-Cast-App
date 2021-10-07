package com.example.weathercastapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.Call
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {
    private lateinit var tvCity : TextView
    private lateinit var tvLastUpdate : TextView
    private lateinit var tvWeatherState : TextView
    private lateinit var tvTemperature : TextView
    private lateinit var tvtemplow : TextView
    private lateinit var tvTemphigh : TextView
    private lateinit var tvSunset : TextView
    private lateinit var tvSunrise : TextView
    private lateinit var tvWind : TextView
    private lateinit var tvHumidity : TextView
    private lateinit var pressure : TextView
    private lateinit var refresh : LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvCity = findViewById(R.id.tvCityCountry)
        tvLastUpdate = findViewById(R.id.tvUpdate)
        tvWeatherState = findViewById(R.id.tvState)
        tvTemperature = findViewById(R.id.tvTemp)
        tvtemplow = findViewById(R.id.tvLow)
        tvTemphigh = findViewById(R.id.tvHigh)
        tvSunset = findViewById(R.id.tvSunset)
        tvSunrise = findViewById(R.id.tvSunrise)
        tvWind = findViewById(R.id.tvWind)
        tvHumidity = findViewById(R.id.tvHumidity)
        pressure = findViewById(R.id.tvPressure)
        refresh = findViewById(R.id.llRefresh)

        requestAPI()

        refresh.setOnClickListener{
            requestAPI()
            //Toast.makeText(this@MainActivity, "Data Retrieved Successfully", Toast.LENGTH_LONG).show()
        }

    }

    private fun requestAPI(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MAIN", "fetch data")
            val WeatherInfo = async { fetchData() }.await()
            if(WeatherInfo.name?.isNotEmpty() == true){ updateTextView(WeatherInfo) }
            else{
                Log.d("MAIN", "Unable to get data")
                //Toast.makeText(this@MainActivity, "Couldn't Refresh Data, Please Try Again!", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun fetchData(): WeatherCast{
        Log.d("MAIN", "went inside fetch")
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        val call: Call<WeatherCast> = apiInterface!!.getWeatheCast()
        var base: String? = null
        var clouds: Clouds? = null
        var cod: Int? = null
        var coord: Coord? = null
        var dt: Int ? = null
        var id: Int? = null
        var main: Main? = null
        var name: String? = null
        var sys: Sys? = null
        var timezone: Int? = null
        var visibility: Int? = null
        var weather: List<Weather>? = null
        var wind: Wind? = null

        try {
            val response = call.execute()
            base = response.body()?.base
            name = response.body()?.name
            clouds = response.body()?.clouds
            cod = response.body()?.cod
            coord = response.body()?.coord
            dt = response.body()?.dt
            id = response.body()?.id
            main = response.body()?.main
            sys = response.body()?.sys
            timezone = response.body()?.timezone
            visibility = response.body()?.visibility
            weather = response.body()?.weather
            wind = response.body()?.wind
        }
        catch (e: Exception){ Log.d("MAIN", "ISSUE: $e")}
        Log.d("MAIN", weather.toString())
        return WeatherCast(base,clouds,cod,coord,dt,id,main,name,sys,timezone,visibility,weather,wind)
    }

    private suspend fun updateTextView(weather: WeatherCast) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())


        withContext(Dispatchers.Main){
            tvCity.text = weather.name.toString()
            Log.d("MAIN", currentDate)
            tvLastUpdate.text = "last update: $currentDate"
            tvWeatherState.text = weather.weather?.get(0)?.description.toString()
            tvTemperature.text = "${convertKelToC (weather.main?.temp.toString().toDouble())}℃"
            tvtemplow.text = "${convertKelToC (weather.main?.temp_min.toString().toDouble())}℃"
            tvTemphigh.text = "${convertKelToC (weather.main?.temp_max.toString().toDouble())}℃"


            "Sunrise \n ${weather.wind?.speed.toString()}}"
            tvSunset.text = "Sunset \n ${convertTime(weather.sys?.sunset.toString().toLong())}"
            tvSunrise.text = "Sunrise \n ${convertTime(weather.sys?.sunrise.toString().toLong())}"
            tvWind.text = "Wind \n ${weather.wind?.speed.toString()}"
            tvHumidity.text = "Humidity \n ${weather.main?.humidity.toString()}"
            pressure.text = "Pressure \n ${weather.main?.pressure.toString()}"

        }
    }

    private fun convertTime(x:Long) : String {
        return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(x*1000))
    }

    private fun convertKelToC (temp : Double): Double {
        var newTemp = temp - 272.15
        return newTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

}
/*

- layout file:
    - 2 tv at top for the city country, and time of last update
    - 4 tv in middle: weather, temp, high, low
    - 6 widgets at bottom with png pics
    - choose pretty color
    - add pictures

- main:
    - connect layout
        - connect refresh button 
    - manifest + girdle
    - retrofit
        - 2 classes and 1 interface
        - connect api
        - check key?
    - coroutines
        - run retrofit in background
    - Bonus: press on city ->change it by entering a new zip code

* */


/*
Create a weather application that displays the city country, and time of last update at the top
Users should be able to change the city by tapping it and entering a new zip code
The current temperature should be prominently displayed in the middle of the screen
At the bottom, create six widgets to display the following:todo

Use the following API: https://openweathermap.org/
Your app should look similar to this image:
Feel free to change the design, just make sure to include all the information.

Make sure to implement the following in your application:

Use coroutines to asynchronously fetch weather data
Parse JSON data to populate the app with updated information
Use try blocks as safeguards against crashes
Create a custom background
Add custom images to the app
Use Android Manifest to limit app to portrait mode
Allow users to retry fetching data if error occurs (the city should also be reset to a valid zip code)
//
*/