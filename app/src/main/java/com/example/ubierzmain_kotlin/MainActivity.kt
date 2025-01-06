package com.example.ubierzmain_kotlin

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cancellationSignal: CancellationSignal? = null
    private var isAuthenticated = false

    // Register for activity result for permission request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                proceedAfterPermissions()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                proceedAfterPermissions()
            }
            else -> {
                // No location access granted.
                Toast.makeText(this, "Location access denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Apply window insets for edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Authenticate user using fingerprint
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            authenticateUser()
        } else {
            Toast.makeText(this, "Your device does not support biometric authentication.", Toast.LENGTH_SHORT).show()
        }

        // Button to navigate to the second activity
        val takButton = findViewById<Button>(R.id.second_act_btn)
        takButton.setOnClickListener {
            if (isAuthenticated && checkGpsStatus() && checkLocationPermission()) {
                navigateToSecondActivity()
            } else {
                Toast.makeText(this, "Please enable GPS and grant location permission", Toast.LENGTH_SHORT).show()
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
                    if (checkGpsStatus()) {
                        requestLocationPermission()
                    } else {
                        showLocationPermissionDialog()
                    }
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
            Toast.makeText(this, "Authentication was cancelled by the user", Toast.LENGTH_SHORT).show()
        }
        return cancellationSignal!!
    }

    private fun showLocationPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable GPS and Location Permission")
        builder.setMessage("This app requires your location to provide better services. Please enable GPS and allow location access.")
        builder.setPositiveButton("Enable Location") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Location access denied", Toast.LENGTH_SHORT).show()
        }
        builder.create().show()
    }

    private fun checkGpsStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            true
        } else {
            Toast.makeText(this, "GPS is disabled", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun proceedAfterPermissions() {
        // Additional logic after permissions are granted, if needed
    }

    private fun navigateToSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
}