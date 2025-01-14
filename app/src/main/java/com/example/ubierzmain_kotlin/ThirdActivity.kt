package com.example.ubierzmain_kotlin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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

// weather API data classes
data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val name: String
)

data class Weather(val description: String)
data class Main(val temp: Double)

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String
    ): WeatherResponse
}

class ThirdActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var textLocation: TextView
    private lateinit var textWeather: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        textLocation = findViewById(R.id.text_location)
        textWeather = findViewById(R.id.text_weather)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Инициализация сенсора
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            Toast.makeText(this, "Czujnik zbliżeniowy niedostępny", Toast.LENGTH_SHORT).show()
        }

        val buttonGetLocation: Button = findViewById(R.id.button_get_location)
        buttonGetLocation.setOnClickListener {
            getLocationAndWeather()
        }

        val buttonShowClothing: Button = findViewById(R.id.button_show_clothing)
        buttonShowClothing.setOnClickListener {
            getLocationAndWeather(showClothing = true)
        }

        val buttonShowMap: Button = findViewById(R.id.button_show_map)
        buttonShowMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] < proximitySensor!!.maximumRange) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun getLocationAndWeather(showClothing: Boolean = false) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_LOCATION)
        } else {
            retrieveLocation(showClothing)
        }
    }

    private fun retrieveLocation(showClothing: Boolean) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                getWeather(lat, lon, showClothing)
            } else {
                Toast.makeText(this, "Nie znaleziono lokalizacji", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeather(lat: Double, lon: Double, showClothing: Boolean) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)
        val apiKey = "cde18b99ec8602f96ab6edf2bbd1195d"

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = weatherApi.getWeather(lat, lon, apiKey, "pl")
                val cityName = response.name
                val temperature = response.main.temp - 273.15
                val description = response.weather[0].description.lowercase()

                textLocation.text = "Lokalizacja: $cityName"
                textWeather.text = "Pogoda w $cityName: ${"%.2f".format(temperature)}°C, ${description.capitalize()}"

                if (showClothing) {
                    val clothingRecommendation = getClothingRecommendation(temperature, description)
                    val weatherImageCode = getWeatherImageCode(description, temperature)

                    val intent = Intent(this@ThirdActivity, ClothingActivity::class.java)
                    intent.putExtra("clothing_recommendation", clothingRecommendation)
                    intent.putExtra("weather_image_code", weatherImageCode)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ThirdActivity, "Nie udało się uzyskać pogody", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeatherImageCode(description: String, temperature: Double): String {
        return when {
            description.contains("snow", ignoreCase = true) || description.contains("śnieg", ignoreCase = true) -> "snow"  // Snow
            description.contains("rain", ignoreCase = true) || description.contains("deszcz", ignoreCase = true) -> "rain"  // Rain
            temperature <= 0 -> "snowflake"  // Below zero, but no snow or rain
            else -> "sun"  // Temperature above 0 and no rain or snow
        }
    }

    private fun getClothingRecommendation(temp: Double, weather: String): String {
        return when {
            temp < 0 -> "Dzisiejsza pogoda jest mroźna. Ubierz ciepłą kurtkę, czapkę i rękawiczki."
            temp in 0.0..10.0 -> "Na dworze jest chłodno. Ubierz kurtkę, sweter i szalik."
            temp in 10.0..20.0 -> "Pogoda jest umiarkowana. Lżejsza kurtka lub bluza wystarczy."
            temp > 20 -> "Na dworze jest ciepło. Wystarczy lekka odzież, koszulka i spodnie lub szorty."
            weather.contains("deszcz", ignoreCase = true) -> "Oczekiwany jest deszcz. Zabierz parasol i załóż wodoodporną kurtkę."
            else -> "Pogoda neutralna. Wybierz wygodną odzież na co dzień."
        }
    }
}
