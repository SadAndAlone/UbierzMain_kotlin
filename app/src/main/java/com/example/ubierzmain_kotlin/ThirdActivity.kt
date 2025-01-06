package com.example.ubierzmain_kotlin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ubierzmain_kotlin.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThirdActivity : AppCompatActivity() {

    private val apiKey = "2ce6579b292eb2903ce8152e1ffcc4a7"
    private val city = "London"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        fetchWeather()
    }

    private fun fetchWeather() {
        val weatherTextView = findViewById<TextView>(R.id.weather_text_view)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getCurrentWeather(city, apiKey)
                val weatherInfo = "Город: ${response.name}\n" +
                        "Температура: ${response.main.temp}°C\n" +
                        "Погода: ${response.weather[0].description}"

                runOnUiThread {
                    weatherTextView.text = weatherInfo
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@ThirdActivity, "Ошибка загрузки погоды", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
