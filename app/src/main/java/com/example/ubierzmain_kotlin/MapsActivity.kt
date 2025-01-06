package com.example.ubierzmain_kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapsActivity : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Налаштування конфігурації osmdroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Ініціалізація MapView
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK) // Переконайтеся, що використовуєте правильне джерело тайлів
        map.setMultiTouchControls(true)
        map.controller.setZoom(15.0)
        map.controller.setCenter(GeoPoint(49.6215, 20.6970)) // Nowy Sącz, Polska

        // Додавання маркера
        val startPoint = GeoPoint(49.6215, 20.6970)
        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Nowy Sącz, Polska"
        map.overlays.add(startMarker)
    }

    override fun onResume() {
        super.onResume()
        map.onResume() // Це потрібно для роботи osmdroid
    }

    override fun onPause() {
        super.onPause()
        map.onPause() // Це потрібно для роботи osmdroid
    }
}