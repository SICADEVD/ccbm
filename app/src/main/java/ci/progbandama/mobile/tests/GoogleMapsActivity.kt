package ci.progbandama.mobile.tests

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ci.progbandama.mobile.R
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_google_maps.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import kotlin.math.*

import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import org.apache.commons.lang3.StringUtils


class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMapClickListener,
    OnMyLocationButtonClickListener, OnMyLocationClickListener, OnMyLocationChangeListener {


    var producteur: String? = null
    lateinit var googleMap: GoogleMap
    private var cameraPosition: CameraPosition? = null
    private var lastKnownLocation: Location? = null
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var whichAction = 1;
    var actionDefined = false;
    var wayPoints: MutableList<LatLng> = mutableListOf()
    var gPoligon: Polygon? = null
    var gMarker: Marker? = null
    var gLatLngCenter: LatLng? = null


    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), 17F))
                    }
                } else {
                    googleMap.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            LogUtils.e("TAG", "Current location is null. Using defaults.")
        }
    }


    @SuppressLint("ResourceAsColor")
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_maps)

        labelDelimiterOwner.text = intent.getStringExtra("producteur_nom")

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapsFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapsFragment) as SupportMapFragment
        mapsFragment.getMapAsync(this)

        imageMarkAction.setOnClickListener {
            imageMarkAction.setBackgroundColor(R.color.gray)
            imageWalkingAction.setBackgroundColor(0)

            actionDefined = true
        }

        imageWalkingAction.setOnClickListener {
            imageWalkingAction.setBackgroundColor(R.color.gray)
            imageMarkAction.setBackgroundColor(0)

            actionDefined = true

        }

        imageMapsTypeAction.setOnClickListener {
            when (googleMap.mapType) {
                MAP_TYPE_TERRAIN     -> googleMap.mapType = MAP_TYPE_SATELLITE
                MAP_TYPE_SATELLITE  -> googleMap.mapType = MAP_TYPE_TERRAIN
                else -> googleMap.mapType = MAP_TYPE_NONE
            }
        }

        imageMarkClearAll.setOnClickListener {
            wayPoints.map {
                gPoligon?.remove()
            }
            wayPoints.clear()
            googleMap.clear()

            labelDelimiterDistance.text = "0m"
            labelDelimiterSurface.text = "0m\u00B2"
        }

        imagePolygonMaker.setOnClickListener {
            LogUtils.e("TAG", GsonUtils.toJson(wayPoints))

            SPUtils.getInstance().put("waypoints", GsonUtils.toJson(wayPoints))
            drawDelimiter()
        }


        clickCloseMakerParcelle.setOnClickListener {
            SPUtils.getInstance().put("center_lat", gLatLngCenter?.latitude.toString())
            SPUtils.getInstance().put("center_lng", gLatLngCenter?.longitude.toString())
            finish()
        }


        linearMapsRegisterParcelle.setOnClickListener {
            SPUtils.getInstance().put("center_lat", gLatLngCenter?.latitude.toString())
            SPUtils.getInstance().put("center_lng", gLatLngCenter?.longitude.toString())
            finish()
        }


        // imagePolygonMaker.isEnabled = false
    }


    private fun updateLocationUI() {
        try {
            locationPermissionGranted = true

            if (locationPermissionGranted) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
                googleMap.uiSettings.isZoomControlsEnabled = true
            } else {
                googleMap.isMyLocationEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    fun drawMarker(pickedLatLng: LatLng?) {
        val pickMarkerOptions = MarkerOptions()
            .position(pickedLatLng!!)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_marker))
            .draggable(true)
            .title("Pick-${wayPoints.size}")

        gMarker = googleMap.addMarker(pickMarkerOptions)
        gMarker?.tag = pickMarkerOptions.title

        wayPoints.add(pickedLatLng)

        if (wayPoints.size == 1) {
            imageMarkClearAllCard.visibility = View.VISIBLE
            imageMarkClearMarkerCard.visibility = View.VISIBLE
        }

        if (wayPoints.size > 2) drawDelimiter()


        if (wayPoints.size > 1) {
            labelDelimiterDistance.text = SphericalUtil.computeLength(wayPoints).roundToInt().toString().plus(" m")
        }
    }


    fun drawDelimiter() {
        if (wayPoints.size == 0) return

        val pickedPolygonOptions = PolygonOptions()
            .geodesic(true)
            .strokeWidth(5f)
            .strokeJointType(JointType.ROUND)
            .fillColor(R.color.cardview_shadow_end_color)
            .addAll(wayPoints)
            .clickable(true)

        googleMap.addPolygon(pickedPolygonOptions)

        labelDelimiterSurface.text = SphericalUtil.computeArea(wayPoints).roundToInt().toString().plus(" m²")
        SPUtils.getInstance().put(Constants.PREFS_SUPERFICIE, StringUtils.remove(labelDelimiterSurface.text.toString(),  " m²"))
        imageMarkAction.setBackgroundColor(0)

        gLatLngCenter = getPolygonCenterPoint(wayPoints)
        drawMarker(gLatLngCenter)

        actionDefined = false
    }


    private fun getPolygonCenterPoint(polygonPointsList: MutableList<LatLng>): LatLng? {
        var centerLatLng: LatLng? = null
        val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        for (i in 0 until polygonPointsList.size) {
            builder.include(polygonPointsList[i])
        }
        val bounds: LatLngBounds = builder.build()
        centerLatLng = bounds.center
        return centerLatLng
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            2022 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }



    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2022)
        }
    }


    override fun onMapReady(gm: GoogleMap) {
        googleMap = gm
        googleMap.mapType = MAP_TYPE_SATELLITE
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.setOnMapClickListener(this)
        googleMap.setOnMyLocationChangeListener(this)

        getLocationPermission()

        getDeviceLocation()

        updateLocationUI()

        imageMarkClearAllCard.visibility = View.INVISIBLE
        imageMarkClearMarkerCard.visibility = View.INVISIBLE

        AwesomeDialog.build(this)
            .title(title = "INFO")
            .body("Delimitez la parcelle, assurez-vous d\'aligner correctement les picks afin d\'avoir un meilleur tracé")
            .onPositive("OK") {
            }
            .setCancelable(true)

    }


    override fun onMapClick(clickLatLng: LatLng) {
        if (!actionDefined) {
            ToastUtils.showLong("Veuillez selectionner le mode de delimitation svp !")
            return
        }

        if (clickLatLng.latitude == 0.0) {
            ToastUtils.showShort("Veuillez zoomer pour mieux placer le pick")
            return
        }

        drawMarker(pickedLatLng = clickLatLng)
    }


    override fun onMyLocationClick(myLocation: Location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(myLocation.latitude, myLocation.longitude)))
    }


    override fun onMyLocationButtonClick(): Boolean {
        return true
    }


    override fun onMyLocationChange(myLocation: Location) {
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(myLocation.latitude, myLocation.longitude)))
    }


}
