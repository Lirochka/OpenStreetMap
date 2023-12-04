package com.example.openstreetmap.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.openstreetmap.R
import com.example.openstreetmap.databinding.FragmentMapBinding
import com.example.openstreetmap.model.PointModel
import com.example.openstreetmap.viewModel.MapViewModel
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

class MapFragment : Fragment() {

    private lateinit var viewModel: MapViewModel
    private val startPoint = GeoPoint(55.785098, 37.583167)
    private var pointList: List<PointModel> = listOf()
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var selectedMarker: Marker? = null

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding
        get() = _binding ?: throw RuntimeException("FragmentMapBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        viewModel.isLocationButtonClicked.observe(viewLifecycleOwner, Observer { isClicked ->
            handleLocationButtonState(isClicked)
        })

        viewModel.isRouteButtonClicked.observe(viewLifecycleOwner, Observer { isClicked ->
            handleRouteButtonState(isClicked)
        })

        viewModel.routePoints.observe(viewLifecycleOwner, Observer { points ->
            updateMarkersBasedOnRoutePoints(points)
            if (viewModel.isRouteButtonClicked.value == true) {
                drawRouteFromPoints(points)
            }
        })

        configurationMap()
        initMap()
        setZoomMultiTouch()
        onClickLocation()
        getLocation()
        onClickRoute()
        navigation()
    }

    private fun navigation() {
        binding.menu.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_categoryFragment)
        }
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
        Configuration.getInstance().osmdroidBasePath = requireContext().filesDir
    }

    private fun getLocation() {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        locationOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.mapView)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        val imageDraw =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_my_location_24)
                ?.toBitmap()
        locationOverlay.setPersonIcon(imageDraw)
        locationOverlay.setDirectionIcon(imageDraw)

        binding.mapView.overlays.add(locationOverlay)
    }

    private fun handleLocationButtonState(isClicked: Boolean) {
        if (isClicked) {
            binding.btnLocation.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.pink
                )
            )
            initPointList()
            pointList.forEach {
                setMarker(it.geoPoint, it.name)
            }
        } else {
            binding.btnLocation.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray
                )
            )
            removeAllMarkers()
        }
        removeAllRouteOverlays()
        binding.mapView.overlays.add(locationOverlay)
        binding.mapView.invalidate()
    }
    private fun onClickLocation() {
        binding.btnLocation.setOnClickListener {
            viewModel.setLocationButtonClicked(!viewModel.isLocationButtonClicked.value!!)
        }
    }
    private fun handleRouteButtonState(isClicked: Boolean) {
        if (isClicked) {
            removeAllRouteOverlays()
            binding.btnRoute.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.pink
                )
            )
        } else {
            binding.btnRoute.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray
                )
            )
            removeAllRouteOverlays()
        }
    }

    private fun onClickRoute() {
        binding.btnRoute.setOnClickListener {
            viewModel.setRouteButtonClicked(!viewModel.isRouteButtonClicked.value!!)
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
            PointModel(id++, "Эколор", GeoPoint(55.7818875, 37.5844531)),
            PointModel(id++, "Пятёрочка", GeoPoint(55.7820843, 37.5850369)),
        )
    }
    private fun setMarker(geoPoint: GeoPoint, name: String) {
        val marker = Marker(binding.mapView)
        marker.position = geoPoint
        marker.icon =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_location_on_24)
        marker.title = name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.setOnMarkerClickListener { marker, mapView ->
            handleMarkerClick(marker)
            Toast.makeText(requireContext(), marker.title, Toast.LENGTH_SHORT).show()
            return@setOnMarkerClickListener true
        }
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()
    }

    private fun handleMarkerClick(marker: Marker) {
        selectedMarker = marker
        if (viewModel.isRouteButtonClicked.value == true) {
            buildRoad(marker.position)
        }
    }
    private fun drawRouteFromPoints(points: List<GeoPoint>) {
        viewModel.setRoutePoints(points)
    }
    private fun updateMarkersBasedOnRoutePoints(points: List<GeoPoint>) {
        for ((index, point) in points.withIndex()) {
            setMarker(point, "Marker $index")
        }
    }
    private fun buildRoad(endPoint: GeoPoint) {
        binding.btnRoute.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink))
        val startLocation = locationOverlay?.myLocation ?: startPoint

        CoroutineScope(Dispatchers.IO).launch {
            val roadManager =
                OSRMRoadManager(requireContext(), System.getProperty("http.agent"))
            val waypoints =
                arrayListOf<GeoPoint>(startLocation, endPoint)
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)

            withContext(Dispatchers.Main) {
                binding.mapView.overlays.add(0, roadOverlay)
                binding.mapView.invalidate()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}