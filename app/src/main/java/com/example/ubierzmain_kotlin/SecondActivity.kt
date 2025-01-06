package com.example.ubierzmain_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Найдите кнопку LOGIN
        val loginButton = findViewById<Button>(R.id.login_button)

        // Найдите поля для ввода текста
        val usernameEditText = findViewById<EditText>(R.id.editTextText)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)

        // Установите обработчик нажатия на loginButton
        loginButton.setOnClickListener {
            // Переход на ThirdActivity
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        // Установите обработчик изменения фокуса для usernameEditText
        usernameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (usernameEditText.text.toString() == "Username") {
                    usernameEditText.setText("")
                }
            } else {
                if (usernameEditText.text.toString().isEmpty()) {
                    usernameEditText.setText("Username")
                }
            }
        }

        // Установите обработчик изменения фокуса для passwordEditText
        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (passwordEditText.text.toString() == "Password") {
                    passwordEditText.setText("")
                }
            } else {
                if (passwordEditText.text.toString().isEmpty()) {
                    passwordEditText.setText("Password")
                }
            }
        }
    }
}