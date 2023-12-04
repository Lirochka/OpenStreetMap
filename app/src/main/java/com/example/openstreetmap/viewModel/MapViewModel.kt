package com.example.openstreetmap.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.osmdroid.util.GeoPoint

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLocationButtonClicked = MutableLiveData<Boolean>()
    val isLocationButtonClicked: LiveData<Boolean>
        get() = _isLocationButtonClicked

    private val _isRouteButtonClicked = MutableLiveData<Boolean>()
    val isRouteButtonClicked: LiveData<Boolean>
        get() = _isRouteButtonClicked


    private val _routePoints = MutableLiveData<List<GeoPoint>>()
    val routePoints: LiveData<List<GeoPoint>>
        get() = _routePoints
    init {
        _isLocationButtonClicked.value = false
        _isRouteButtonClicked.value = false
        _routePoints.value = arrayListOf()
    }
    fun setLocationButtonClicked(isClicked: Boolean) {
        _isLocationButtonClicked.value = isClicked
    }
    fun setRouteButtonClicked(isClicked: Boolean) {
        _isRouteButtonClicked.value = isClicked
    }
    fun setRoutePoints(points: List<GeoPoint>) {
        _routePoints.value = points
    }
}
