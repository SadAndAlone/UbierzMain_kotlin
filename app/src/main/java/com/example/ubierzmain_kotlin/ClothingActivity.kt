package com.example.ubierzmain_kotlin

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ClothingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing)

        val clothingRecommendation = intent.getStringExtra("clothing_recommendation")
        val weatherImageCode = intent.getStringExtra("weather_image_code") // Получаем код изображения

        val textClothingRecommendation: TextView = findViewById(R.id.text_clothing_recommendation)
        val weatherImage: ImageView = findViewById(R.id.weather_image)

        textClothingRecommendation.text = clothingRecommendation ?: "Nie udało się uzyskać rekomendacji."

        // Устанавливаем изображение в зависимости от кода
        val imageResId = when (weatherImageCode) {
            "snow" -> R.drawable.snow_image
            "rain" -> R.drawable.cloud_image
            "snowflake" -> R.drawable.snowflake
            else -> R.drawable.sun_image
        }
        weatherImage.setImageResource(imageResId)
    }
}
