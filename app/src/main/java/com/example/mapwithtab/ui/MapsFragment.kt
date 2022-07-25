package com.example.mapwithtab.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapwithtab.R
import com.example.mapwithtab.data.remote.RetrofitHelper
import com.example.mapwithtab.data.remote.api.APIInterface
import com.example.mapwithtab.databinding.FragmentMapsBinding
import com.example.mapwithtab.databinding.LayoutPopupDetailsBinding
import com.example.mapwithtab.repository.APIRepository
import com.example.mapwithtab.viewmodel.LocationViewModel
import com.example.mapwithtab.viewmodel.LocationViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task


class MapsFragment : Fragment() {

    private lateinit var binding: FragmentMapsBinding

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    lateinit var mLocationRequest: LocationRequest

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    internal var mCurrLocationMarker: Marker? = null
    private var lastKnownLocation: Location? = null

    lateinit var locationViewModel: LocationViewModel
    lateinit var locationManager: LocationManager;

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //Toast.makeText(requireContext(), "Turn On Locarion", Toast.LENGTH_LONG) .show()
                locationSetting()
            } else
                fetchLocation()
        } else {
            Toast.makeText(
                requireContext(),
                "Location Permission is required...",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Log.i(
                    "MapsActivity",
                    "Location: " + location.getLatitude() + " " + location.getLongitude()
                )
                lastKnownLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrLocationMarker = map?.addMarker(markerOptions)

                //move map camera
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0F))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap
        mLocationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 50
            priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
            maxWaitTime = 100
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Toast.makeText(requireContext(), "Turn On Locarion", Toast.LENGTH_LONG).show()
                    locationSetting()
                } else
                    fetchLocation()
                /*  fusedLocationClient?.requestLocationUpdates(
                      mLocationRequest,
                      mLocationCallback,
                      Looper.myLooper()
                  )*/
                map!!.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Toast.makeText(requireContext(), "Turn On Locarion", Toast.LENGTH_LONG).show()
                locationSetting()
            } else
                fetchLocation()
            map!!.isMyLocationEnabled = true


        }

        getSavedData()
        map!!.setOnMapClickListener {
            openAddDialog(it)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)

        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val apiInterface = RetrofitHelper.getInstance().create(APIInterface::class.java)
        val apiRepository = APIRepository(apiInterface)

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModelFactory(apiRepository)
        ).get(LocationViewModel::class.java)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        Toast.makeText(requireContext(), "Fetching Location...", Toast.LENGTH_LONG).show();
        val tokenSource = CancellationTokenSource()
        val token = tokenSource.token
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            token
        )
            .addOnSuccessListener {
                if (it != null) {
                    lastKnownLocation = it

                    val latLng = LatLng(it.latitude, it.longitude)

                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng)
                    markerOptions.title("Current Position")
                    markerOptions.icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_MAGENTA
                        )
                    )
                    mCurrLocationMarker = map?.addMarker(markerOptions)
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0F))
                    map!!.isMyLocationEnabled = true

                }
            }

    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->

                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .create()
                    .show()


            } else {

                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            //Toast.makeText(requireContext(), "Turn On Locarion", Toast.LENGTH_LONG) .show()
                            locationSetting()
                        } else
                            fetchLocation()
                        //fetchLocation()
                        /*fusedLocationClient?.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )*/
                        map?.setMyLocationEnabled(true)
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }

        }// other 'case' lines to check for other
        // permissions this app might request
    }

    fun openAddDialog(latLang: LatLng) {
        val builder = AlertDialog.Builder(requireContext()).create()
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBinding: LayoutPopupDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_popup_details, null, false)

        val Distance = String.format(
            "%.2f", distance(
                lastKnownLocation?.latitude ?: 0.0,
                lastKnownLocation?.longitude ?: 0.0, latLang.latitude, latLang.longitude
            )
        )

        alertBinding.apply {

            edittextLatlong.setText(
                String.format(
                    "%.2f",
                    latLang.latitude
                ) + "," + String.format("%.2f", latLang.longitude)
            )

            btnAdd.setOnClickListener {
                if (edittextName.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Please enter name", Toast.LENGTH_LONG).show()
                } else if (edittextEmail.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_LONG).show()
                } else if (!isValidEmail(edittextEmail.text.toString())) {
                    Toast.makeText(requireContext(), "Please enter valid email", Toast.LENGTH_LONG)
                        .show()
                } else if (edittextPhone.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Please enter phone", Toast.LENGTH_LONG).show()
                } else if (edittextPhone.text!!.length != 10) {
                    Toast.makeText(
                        requireContext(),
                        "phone should be of 10 digit",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {

                    locationViewModel.insertData(
                        requireContext(),
                        edittextName.text.toString(),
                        edittextEmail.text.toString(),
                        edittextPhone.text.toString(),
                        edittextLatlong.text.toString(),
                        Distance.toString()
                    );
                    builder.dismiss()
                    Toast.makeText(requireContext(), "Added Successfully", Toast.LENGTH_LONG).show()

                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLang)
                    markerOptions.title(edittextName.text.toString())
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    mCurrLocationMarker = map?.addMarker(markerOptions)
                }
                //
            }
        }
        builder.setView(alertBinding.root)

        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
        val LOCATION_REQUEST = 100
    }

    fun getSavedData() {

        locationViewModel.getLocationDetails(requireContext())!!
            .observe(requireActivity(), Observer {

                if (it == null) {
                    //Toast.makeText(requireContext(), "Data Not Found", Toast.LENGTH_LONG).show()
                } else {
                    for (data in it) {

                        val latlong = data.LatLand.split(",").toTypedArray()

                        val latitude = latlong[0].toDouble()
                        val longitude = latlong[1].toDouble()
                        val location = LatLng(latitude, longitude)

                        val markerOptions = MarkerOptions()
                        markerOptions.position(location)
                        markerOptions.title(data.Name)
                        markerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_CYAN
                            )
                        )
                        map?.addMarker(markerOptions)
                    }
                }
            })
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515

        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun locationSetting() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            requireActivity(),
            OnSuccessListener { locationSettingsResponse: LocationSettingsResponse? ->
                fetchLocation()
            })
        task.addOnFailureListener(requireActivity(), OnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    startIntentSenderForResult(
                        e.resolution.intentSender,
                        LOCATION_REQUEST,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (sendEx: SendIntentException) {

                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Called", "Fragment")

        if (requestCode == LOCATION_REQUEST) {

            if (resultCode == PackageManager.PERMISSION_GRANTED) {
                locationSetting()
                Toast.makeText(requireContext(), "Please turn on location!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                fetchLocation();
            }

        }
    }

}