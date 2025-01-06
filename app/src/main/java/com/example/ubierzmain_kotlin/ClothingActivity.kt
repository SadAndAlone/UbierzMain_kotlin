package com.example.ubierzmain_kotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ClothingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing)

        val clothingRecommendation = intent.getStringExtra("clothing_recommendation")
        val textClothingRecommendation: TextView = findViewById(R.id.text_clothing_recommendation)
        textClothingRecommendation.text = "Clothing Recommendation: $clothingRecommendation"
    }
}