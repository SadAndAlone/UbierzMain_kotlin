package com.example.ubierzmain_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(val main: Main, val name: String)
data class Main(val temp: Double)

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): WeatherResponse
}

class ThirdActivity : AppCompatActivity() {

    private lateinit var textLocation: TextView
    private lateinit var textWeather: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        textLocation = findViewById(R.id.text_location)
        textWeather = findViewById(R.id.text_weather)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val buttonGetLocation: Button = findViewById(R.id.button_get_location)
        buttonGetLocation.setOnClickListener {
            getLocationAndWeather()
        }

        val buttonShowClothing: Button = findViewById(R.id.button_show_clothing)
        buttonShowClothing.setOnClickListener {
            val intent = Intent(this, ClothingActivity::class.java)
            startActivity(intent)
        }

        val buttonShowMap: Button = findViewById(R.id.button_show_map)
        buttonShowMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getLocationAndWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запит дозволу
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_LOCATION)
        } else {
            // Дозвіл надано
            retrieveLocation()
        }
    }

    private fun retrieveLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                textLocation.text = "Location: Lat: $lat, Lon: $lon"
                Log.d("Location", "Lat: $lat, Lon: $lon")

                // Отримання погоди
                getWeather(lat, lon)
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                Log.e("LocationError", "Location not found")
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            Log.e("LocationError", "Failed to get location", e)
        }
    }

    private fun getWeather(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)
        val apiKey = "cde18b99ec8602f96ab6edf2bbd1195d"

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = weatherApi.getWeather(lat, lon, apiKey)
                Log.d("WeatherAPI", "Response: $response")
                val temperature = response.main.temp - 273.15 // Переведення з Кельвінів у Цельсії
                textWeather.text = "Weather in ${response.name}: ${"%.2f".format(temperature)}°C"
            } catch (e: Exception) {
                Log.e("WeatherAPI", "Failed to get weather", e)
                Toast.makeText(this@ThirdActivity, "Failed to get weather", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Дозвіл надано
            retrieveLocation()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}