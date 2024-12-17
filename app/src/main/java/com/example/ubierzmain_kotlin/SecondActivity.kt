package com.example.ubierzmain_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Найдите кнопку LOGIN
        val loginButton = findViewById<Button>(R.id.login_button)

        // Установите обработчик нажатия
        loginButton.setOnClickListener {
            // Переход на ThirdActivity
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
    }
}
