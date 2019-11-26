package com.diep.trackme.page.record


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.diep.trackme.R
import com.diep.trackme.common.BaseViewModelFactory
import com.diep.trackme.enum.RecordState
import com.diep.trackme.model.Record
import com.diep.trackme.page.history.HistoryViewModel
import com.diep.trackme.utils.formatTimeDisplay
import com.diep.trackme.utils.setDistanceText
import com.diep.trackme.utils.setSpeedText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.fragment_record.*
import kotlin.math.roundToInt

class RecordFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private var line: Polyline? = null
    private lateinit var criteria: Criteria
    private lateinit var startLatLng: LatLng
    private lateinit var currentLatLng: LatLng
    private lateinit var startLocation: Location
    private lateinit var currentLocation: Location
    private lateinit var locationManager: LocationManager
    private val REQUEST_CODE = 101
    private var locationPermissionGranted = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private var state: RecordState = RecordState.START
    private lateinit var viewModel: HistoryViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, BaseViewModelFactory {
            HistoryViewModel(activity!!)
        }).get(HistoryViewModel::class.java)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionForGetLocation()
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        setButtonAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLocationUpdate()

    }

    private fun setButtonAction() {
        pause_btn.setOnClickListener {
            state = RecordState.PAUSE
            updateRecordState()
        }
        resume_btn.setOnClickListener {
            state = RecordState.START
            updateRecordState()
        }
        chronometer.setOnChronometerTickListener {
            it.formatTimeDisplay()

        }
        stop_btn.setOnClickListener {
            viewModel.triggerSaveRecord(
                Record(
                    record_distance_tv.text.toString(),
                    chronometer.text.toString(),
                    record_speed_tv.text.toString(),
                    startLocation.latitude,
                    startLocation.longitude,
                    currentLocation.latitude,
                    currentLocation.longitude
                )
            )
        }
    }

    private fun updateRecordState() {
        when (state) {
            RecordState.PAUSE -> {
                stopLocationUpdate()
                pausing_container.visibility = View.VISIBLE
                pausing_container.isEnabled = true
                pause_btn.visibility = View.GONE
                pause_btn.isEnabled = false
                chronometer.stop()

            }
            RecordState.START -> {
                startLocationUpdate()
                pausing_container.visibility = View.GONE
                pausing_container.isEnabled = false
                pause_btn.visibility = View.VISIBLE
                pause_btn.isEnabled = true
                chronometer.start()
            }
        }
    }


    override fun onMapReady(map: GoogleMap?) {
        MapsInitializer.initialize(context)
        this.map = map ?: return
        if (locationPermissionGranted) {
            loadMap()

        } else {
            checkPermissionForGetLocation()
        }
    }

    private fun loadMap() {
        with(this.map) {
            isMyLocationEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
        }
        showCurrentLocation()
    }

    private fun startLocationManager() {
        if (locationPermissionGranted) {
            criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isCostAllowed = false
            locationManager =
                context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkProvider =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!gpsProvider && !networkProvider) {
                enableLocationSettings()
            } else {
                startLocationUpdate()
            }
        }
    }

    private fun stopLocationUpdate() {
        locationManager.removeUpdates(this)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        locationManager.requestLocationUpdates(
            locationManager.getBestProvider(criteria, true),
            1L,
            2f,
            this
        )

    }

    private fun enableLocationSettings() {
        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity?.startActivity(settingsIntent)
    }

    private fun showCurrentLocation() {
        if (locationPermissionGranted) {
            val result = mFusedLocationProviderClient.lastLocation
            result.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        startLocation = task.result!!
                        startLatLng = LatLng(startLocation.latitude, startLocation.longitude)
                        with(this.map) {
                            moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    startLatLng, 17f
                                )
                            )
                            addMarker(MarkerOptions().position(startLatLng))
                        }
                    }
                    startLocationManager()
                }
            }
        } else {
            checkPermissionForGetLocation()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    locationPermissionGranted = true
                    loadMap()
                } else {
                }
                return
            }
        }
    }

    private fun checkPermissionForGetLocation() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_CODE
            )
        } else {
            locationPermissionGranted = true
        }
    }

    override fun onLocationChanged(location: Location?) {
        currentLocation = location!!
        if (!chronometer.isActivated) {
            chronometer.start()
        }
        drawLine()
        updateDistance()
        updateSpeed()
    }

    private fun drawLine() {
        currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        with(map) {
            line = addPolyline(PolylineOptions().add(startLatLng, currentLatLng))
            line?.color = Color.RED
        }
    }

    private fun updateSpeed() {
        currentSpeed = currentLocation.speed.roundToInt()
        kmphSpeed = (currentSpeed * 3.6).roundToInt()
        record_speed_tv.setSpeedText(kmphSpeed.toString())
    }

    private var currentSpeed = 0
    private var kmphSpeed = 0
    private var distance = 0f
    private fun updateDistance() {
        distance = startLocation.distanceTo(currentLocation)
        record_distance_tv.setDistanceText(String.format("%.1f", distance))
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

}
