package com.example.ubierzmain_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class ThirdActivity : AppCompatActivity() {

    private lateinit var textLocation: TextView
    private lateinit var textWeather: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        // Ініціалізація текстових полів
        textLocation = findViewById(R.id.text_location)
        textWeather = findViewById(R.id.text_weather)

        // Налаштування обробки системних панелей
        val mainView = findViewById<View>(R.id.main)
        mainView?.let { view ->
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Обробка натискання кнопки
        val buttonGetLocation: Button = findViewById(R.id.button_get_location)
        buttonGetLocation.setOnClickListener {
            getLocationAndWeather()
        }

        // Додайте обробник натискання для кнопки Show Clothing
        val buttonShowClothing: Button = findViewById(R.id.button_show_clothing)
        buttonShowClothing.setOnClickListener {
            val intent = Intent(this, ClothingActivity::class.java)
            startActivity(intent)
        }

        // Додайте обробник натискання для нової кнопки Map
        val buttonShowMap: Button = findViewById(R.id.button_show_map)
        buttonShowMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getLocationAndWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            // Симуляція отримання локації
            handler.postDelayed({
                val city = "Nowy Sącz"
                textLocation.text = "Location: $city"

                // Симуляція отримання погоди
                handler.postDelayed({
                    val weather = getRandomWeather()
                    textWeather.text = "Weather: $weather"
                }, 1000)
            }, 1000)
        }
    }

    private fun getRandomWeather(): String {
        val weatherConditions = listOf("Sunny", "Rainy", "Cloudy", "Snowy", "Windy")
        return weatherConditions.random()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndWeather()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}