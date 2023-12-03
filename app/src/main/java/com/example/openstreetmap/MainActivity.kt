package com.example.openstreetmap

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.openstreetmap.databinding.ActivityMainBinding
import com.example.openstreetmap.pojo.PointModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.BuildConfig
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val startPoint = GeoPoint(55.785098, 37.583167)
    private var pointList: List<PointModel> = listOf()
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var isLocationButtonClicked = false
    private var isRouteButtonClicked = false
    private var selectedMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurationMap()
        initMap()
        setZoomMultiTouch()
        onClickLocation()
        getLocation()
        onClickRoute()
    }

    private fun setZoomMultiTouch() {
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.overlays.add(RotationGestureOverlay(binding.mapView))
    }

    private fun initMap() {
        binding.mapView.controller.setZoom(17.5)
        binding.mapView.controller.setCenter(startPoint)
    }

    private fun configurationMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.VERSION_NAME
        Configuration.getInstance().osmdroidBasePath = filesDir
    }

    private fun getLocation() {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), binding.mapView)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        val imageDraw =
            ContextCompat.getDrawable(this, R.drawable.baseline_my_location_24)?.toBitmap()
        locationOverlay.setPersonIcon(imageDraw)
        locationOverlay.setDirectionIcon(imageDraw)

        binding.mapView.overlays.add(locationOverlay)
    }

    private fun onClickLocation() {
        binding.btnLocation.setOnClickListener {
            isLocationButtonClicked = !isLocationButtonClicked
            if (isLocationButtonClicked) {
                binding.btnLocation.setBackgroundColor(ContextCompat.getColor(this, R.color.pink))
                initPointList()
                pointList.forEach {
                    setMarker(it.geoPoint, it.name)
                }
            } else {
                binding.btnLocation.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                removeAllMarkers()
            }
            removeAllRouteOverlays()
            binding.mapView.overlays.add(locationOverlay)
            binding.mapView.invalidate()
        }
    }

    private fun onClickRoute() {
        binding.btnRoute.setOnClickListener {
            isRouteButtonClicked = !isRouteButtonClicked
            if (isRouteButtonClicked) {
                removeAllRouteOverlays()
                binding.btnRoute.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.pink
                    )
                )
            } else {
                binding.btnRoute.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                removeAllRouteOverlays()
            }
        }
    }

    private fun removeAllRouteOverlays() {
        binding.mapView.overlays.removeAll { it is Polyline }
        binding.mapView.invalidate()
    }

    private fun removeAllMarkers() {
        binding.mapView.overlays.removeAll { it is Marker }
        binding.mapView.invalidate()
    }

    private fun initPointList() {
        var id = 0
        pointList = listOf(
            PointModel(id++, "м.Белорусская", GeoPoint(55.7894451, 37.5686849)),
            PointModel(id++, "Музей русского импрессионизма", GeoPoint(55.7824449, 37.56179)),
            PointModel(id++, "Фитнес-клуб FITLAND", GeoPoint(55.7836616, 37.5796178)),
            PointModel(id++, "БЦ Ямское поле", GeoPoint(55.7827287, 37.5795343)),
            PointModel(id++, "Эколор", GeoPoint(55.7818875,37.5844531)),
            PointModel(id++, "Пятёрочка", GeoPoint(55.7820843,37.5850369)),
        )
    }

    private fun setMarker(geoPoint: GeoPoint, name: String) {
        val marker = Marker(binding.mapView)
        marker.position = geoPoint
        marker.icon = ContextCompat.getDrawable(this, R.drawable.baseline_location_on_24)
        marker.title = name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.setOnMarkerClickListener { marker, mapView ->
            handleMarkerClick(marker)
            Toast.makeText(this, marker.title, Toast.LENGTH_SHORT).show()
            return@setOnMarkerClickListener true
        }
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()
    }

    private fun handleMarkerClick(marker: Marker) {
        selectedMarker = marker
        if (isRouteButtonClicked) {
            buildRoad(marker.position)
        }
    }

    private fun buildRoad(endPoint: GeoPoint) {
        binding.btnRoute.setBackgroundColor(ContextCompat.getColor(this, R.color.pink))
        CoroutineScope(Dispatchers.IO).launch {
            val roadManager =
                OSRMRoadManager(this@MainActivity, System.getProperty("http.agent"))
            val waypoints =
                arrayListOf<GeoPoint>(locationOverlay?.myLocation ?: startPoint, endPoint)
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)

            withContext(Dispatchers.Main) {
                binding.mapView.overlays.add(0, roadOverlay)
                binding.mapView.invalidate()
            }
        }
    }
}


