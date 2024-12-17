package com.example.ubierzmain_kotlin

import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var cancellationSignal: CancellationSignal? = null
    private var isAuthenticated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            authenticateUser()
        } else {
            Toast.makeText(this, "Your device does not support biometric authentication.", Toast.LENGTH_SHORT).show()
        }

        val takButton = findViewById<Button>(R.id.second_act_btn)
        takButton.setOnClickListener {
            if (isAuthenticated) {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please authenticate first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun authenticateUser() {
        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Authentication")
            .setDescription("Use your fingerprint to access")
            .setNegativeButton("Cancel", mainExecutor) { _, _ ->
                Toast.makeText(this, "Authentication cancelled", Toast.LENGTH_SHORT).show()
            }.build()

        biometricPrompt.authenticate(
            getCancellationSignal(),
            mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                    Toast.makeText(this@MainActivity, "Authentication successful!", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Toast.makeText(this, "Authentication was revoked by the user", Toast.LENGTH_SHORT).show()
        }
        return cancellationSignal!!
    }
}
