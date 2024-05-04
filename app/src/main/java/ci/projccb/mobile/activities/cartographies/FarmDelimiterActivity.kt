package ci.projccb.mobile.activities.cartographies

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.RvMapHistoAdapt
import ci.projccb.mobile.models.ParcelleMappingModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolygon
import com.google.maps.android.ktx.addPolyline
import kotlinx.android.synthetic.main.activity_farm_delimiter.*
import kotlinx.android.synthetic.main.activity_parcelle_mapping.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class FarmDelimiterActivity : AppCompatActivity(R.layout.activity_farm_delimiter), OnMapReadyCallback, OnMapClickListener, OnPolygonClickListener, OnMyLocationChangeListener, OnMarkerClickListener {


    private var labelProducteurNomText: String? = null
    private var labelParcelleCodeText: String? = null
    private var dialogMapHito: AlertDialog? = null
    private val RESULT_ENABLE_GPS_FEATURE: Int = 101
    private var mapsDelimiter: GoogleMap? = null
    private var marker: Marker? =  null
    private var polygon: Polygon? =  null
    private var closed = false
    private var locationPermissionGranted: Boolean = false
    private var trackingGPSStarted = false
    private var polyline: Polyline? =  null
    lateinit var gPolygonCenter: LatLng
    private var markersPolygoneOption: PolygonOptions? = null
    private var markersPolylineOption: PolylineOptions? = null
    private var markersMap: MutableMap<Int, Marker> = mutableMapOf()
    private var markerPerimeter: Double = 0.0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cameraPosition: CameraPosition? = null
    private var lastKnownLocation: Location? = null
    private var producteurID = ""
    /**
     *  @return: 1 = Manual, 2 = GPS
     */
    private var manualOrGpsTrack = 0
    /**
     * @return:  1 = Distance, 2 = Surface
     */
    private var lineOrZoneDelimiter = 0
    private var parcelleMapping = ParcelleMappingModel()
    private var mapPointsList: MutableList<LatLng> = mutableListOf()

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private val outToBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.out_to_button)}
    private val outToTop: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.out_to_top) }
    private val inFromTop: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.in_from_top) }
    private val inFromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.in_from_button) }
    private val inFromLeft: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.in_from_left) }
    private val outToLeft: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.out_to_left) }
    private val blink: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.blink) }


    fun setAnimation(closed: Boolean) {
        try {
            if (!closed) {
                linearDistanceMappingContainerFarmDelimiter.startAnimation(fromBottom)
                linearSurfaceMappingContainerFarmDelimiter.startAnimation(fromBottom)
                fabMenuFarmDelimiter.startAnimation(rotateOpen)
            } else {
                linearDistanceMappingContainerFarmDelimiter.startAnimation(toBottom)
                linearSurfaceMappingContainerFarmDelimiter.startAnimation(toBottom)
                fabMenuFarmDelimiter.startAnimation(rotateClose)
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setVisibility(closed: Boolean) {
        try {
            if (!closed) {
                linearDistanceMappingContainerFarmDelimiter.visibility = View.VISIBLE
                linearSurfaceMappingContainerFarmDelimiter.visibility = View.VISIBLE
            } else {
                linearDistanceMappingContainerFarmDelimiter.visibility = View.GONE
                linearSurfaceMappingContainerFarmDelimiter.visibility = View.GONE
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun onAddButtonClick() {
        setVisibility(closed)
        closed = !closed
    }

    fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        mapsDelimiter?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), 15F
                            )
                        )
                    }

                    updateLocationUI()
                } else {
                    mapsDelimiter?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            LogUtils.e(e.message)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun getDeviceLocationWithAccurancy() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        labelPrécisionFarmDelimiter.text = lastKnownLocation?.accuracy.toString()?:"N/A"
                    }
                } else {
                    mapsDelimiter?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            LogUtils.e(e.message)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setupAreaOfCurrentMapping() {

        val listPoint = mutableListOf<LatLng>()
        markersMap.map {
            listPoint.add(it.value.position)
        }

//        Commons.debugModelToJson(markersMap.map { it.value.position })

        if(listPoint.size > 2){
            labelSuperficiFarmDelimiter.text = Commons.convertDoubleToString(SphericalUtil.computeArea(listPoint) * 0.0001)
        }
    }


    fun startWorkManualMapping() {
        relativeToolbarFarmDelimiter.startAnimation(outToTop)
        relativeToolbarFarmDelimiter.visibility = View.GONE

        fabMenuFarmDelimiter.startAnimation(outToLeft)
        fabMenuFarmDelimiter.visibility = View.GONE

        linearActionMarkerBottomFarmDelimiter.visibility = View.VISIBLE

        relativeActionMarkerBottomContainerFarmDelimiter.startAnimation(inFromBottom)
        relativeActionMarkerBottomContainerFarmDelimiter.visibility = View.VISIBLE

        relativeToolbarFarmDelimiter.startAnimation(inFromTop)
        relativeToolbarFarmDelimiter.visibility = View.VISIBLE

        linearSnippetInfosFarmDelimiter.startAnimation(inFromLeft)
        linearSnippetInfosFarmDelimiter.visibility = View.VISIBLE
    }


    fun startWorkGpsMapping() {
        relativeToolbarFarmDelimiter.startAnimation(outToTop)
        relativeToolbarFarmDelimiter.visibility = View.GONE

        fabMenuFarmDelimiter.startAnimation(outToLeft)
        fabMenuFarmDelimiter.visibility = View.GONE

        relativeToolbarActionFarmDelimiter.startAnimation(inFromTop)
        relativeToolbarActionFarmDelimiter.visibility = View.VISIBLE

        linearSnippetInfosFarmDelimiter.startAnimation(inFromLeft)
        linearSnippetInfosFarmDelimiter.visibility = View.VISIBLE

        linearActionGPSMarkerBottomFarmDelimiter.visibility = View.VISIBLE

        relativeActionMarkerBottomContainerFarmDelimiter.startAnimation(inFromBottom)
        relativeActionMarkerBottomContainerFarmDelimiter.visibility = View.VISIBLE
    }


    fun cancelWorkMapping() {
        relativeActionMarkerBottomContainerFarmDelimiter.startAnimation(outToBottom)
        relativeActionMarkerBottomContainerFarmDelimiter.visibility = View.GONE

        linearActionMarkerBottomFarmDelimiter.visibility = View.GONE
        linearActionGPSMarkerBottomFarmDelimiter.visibility = View.GONE

        relativeToolbarActionFarmDelimiter.visibility = View.GONE
        relativeToolbarActionFarmDelimiter.startAnimation(outToTop)

        linearSnippetInfosFarmDelimiter.visibility = View.GONE
        linearSnippetInfosFarmDelimiter.startAnimation(outToLeft)

        fabMenuFarmDelimiter.startAnimation(inFromLeft)
        fabMenuFarmDelimiter.visibility = View.VISIBLE

        relativeToolbarFarmDelimiter.startAnimation(inFromTop)
        relativeToolbarFarmDelimiter.visibility = View.VISIBLE

        markersMap.clear()

        parcelleMapping.mutableWayPoints?.clear()
        mapsDelimiter?.clear()
    }


    fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                mapsDelimiter?.isMyLocationEnabled = true
                mapsDelimiter?.uiSettings?.isMyLocationButtonEnabled = true
                mapsDelimiter?.uiSettings?.isZoomControlsEnabled = true
            } else {
                mapsDelimiter?.isMyLocationEnabled = false
                mapsDelimiter?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null

                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    fun clearAllAniviations() {
        linearActionGPSMarkerBottomFarmDelimiter.clearAnimation()
        linearSnippetInfosFarmDelimiter.clearAnimation()
        relativeToolbarFarmDelimiter.clearAnimation()
        relativeToolbarFarmDelimiter.clearAnimation()
        linearActionMarkerBottomFarmDelimiter.clearAnimation()
        fabMenuFarmDelimiter.clearAnimation()
    }


    private fun getPolygonCenterPoint(polygonPointsList: MutableList<LatLng>): LatLng {
        try {
            var centerLatLng: LatLng? = null
            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
            for (i in 0 until polygonPointsList.size) {
                builder.include(polygonPointsList[i])
            }
            val bounds: LatLngBounds = builder.build()
            centerLatLng = bounds.center
            return centerLatLng
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
        
        return LatLng(0.0, 0.0)
    }


    private fun showMappingTypeDialog() {
        try {
            val dialog = Dialog(this, R.style.DialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.linear_maps_type_maneul_gps_view)

            val linearManualType = dialog.findViewById<LinearLayout>(R.id.linearManuelleMesureType)
            val linearGPSType = dialog.findViewById<LinearLayout>(R.id.linearGpsMesureType)

            linearManualType.setOnClickListener {
                manualOrGpsTrack = 1
                dialog.dismiss()
                onAddButtonClick()
                startWorkManualMapping()
            }

            linearGPSType.setOnClickListener {
                manualOrGpsTrack = 2
                dialog.dismiss()
                onAddButtonClick()

                startWorkGpsMapping()
            }
            dialog.show()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    private fun getLocationPermission() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true

                checkLocationServices()

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    2022
                )
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun checkLocationServices() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as (LocationManager)
        if (locationManager != null) {
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                showLocationSettings()
            } else getDeviceLocation()
        }
    }


    // Method to show the location settings screen if location is disabled
    fun showLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, RESULT_ENABLE_GPS_FEATURE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            locationPermissionGranted = false
            when (requestCode) {
                2022 -> {
                    if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationPermissionGranted = true
                    }
                }
            }
            updateLocationUI()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    fun computePerimeter(markerA: LatLng, markerB: LatLng): Double {
        try {
            return SphericalUtil.computeDistanceBetween(markerA, markerB)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
        
        return 0.0
    }


    fun computeSurface() {
//        try {
//            parcelleMapping.parcelleSuperficie = Commons.convertDoubleToString((polygon?.area!!) * 0.0001)
//            labelSurfaceFarmDelimiter.text = parcelleMapping.parcelleSuperficie.plus(" ha")
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
    }


    override fun onMyLocationChange(pLocation: Location) {

        getDeviceLocationWithAccurancy()

        try {
            mapsDelimiter?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(pLocation.latitude, pLocation.longitude), 15F)
            )

            MainScope().launch {
                addMarker(LatLng(pLocation.latitude, pLocation.longitude))
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    private fun deleteAnyMarker() {
        MainScope().launch {
            removeMarker()
        }
    }


    fun saveWorkringParcelle() {
        markersMap.map {
            parcelleMapping.mutableWayPoints?.add(it.value.position)
        }

        parcelleMapping.parcellePerimeter = labelDistanceFarmDelimiter.text.toString().trim()
        parcelleMapping.parcelleNameTag = manualOrGpsTrack.toString()
        parcelleMapping.parcelleName = labelParcelleCodeText
        parcelleMapping.producteurId = labelProducteurNomText

        if (lineOrZoneDelimiter == 2) {
            parcelleMapping.parcelleLat = gPolygonCenter.latitude.toString()
            parcelleMapping.parcelleLng = gPolygonCenter.longitude.toString()
        } else {
            parcelleMapping.parcelleLat = parcelleMapping.mutableWayPoints?.first()?.latitude.toString()
            parcelleMapping.parcelleLng = parcelleMapping.mutableWayPoints?.first()?.longitude.toString()
        }

        parcelleMapping.parcelleSuperficie = Commons.convertDoubleToString(SphericalUtil.computeArea(parcelleMapping.mutableWayPoints) * 0.0001)
        parcelleMapping.parcelleWayPoints = GsonUtils.toJson(parcelleMapping.mutableWayPoints)
        CcbRoomDatabase.getDatabase(this)?.parcelleMappingDao()?.insert(parcelleMapping)

        val returnIntent = Intent()
        returnIntent.putExtra("data", parcelleMapping)
        setResult(RESULT_OK, returnIntent)
        finish()
    }


    suspend fun removeMarker() {
        try {
            if (marker == null) {
                Commons.showMessage(
                    message = "Pas assez de point pour supprimer !",
                    finished = false,
                    callback = {},
                    context = this,
                    positive = getString(R.string.compris),
                    deconnec = false,
                    showNo = false
                )
                return
            }

            var keyDeletion = 0

            markersMap.map {
                if (it.value.tag == marker?.tag) {
                    keyDeletion = it.key
                }
            }

            marker?.remove()
            markersMap.remove(keyDeletion)
            marker = null

            if (markersMap.isNotEmpty()) {
                //MainScope().launch {
                when (lineOrZoneDelimiter) {
                    1 -> { // Line
                        drawPolyline(markersMap)
                    }

                    2 -> {  // Zone
                        drawPolygone(markersMap)
                    }
                    else -> { // Nothing
                        // Do nothing
                    }
                }
                // }

                //  mapsDelimiter?.animateCamera(CameraUpdateFactory.newLatLng(marker?.position!!))
//                focusOnCurrentDevices()
                val cameraPositionPolygon = CameraPosition.fromLatLngZoom(marker?.position!!, 15.0f)
                mapsDelimiter?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionPolygon))

                setupAreaOfCurrentMapping()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    suspend fun removeMarker(marker: Marker) {
        try {

            var keyDeletion = 0

            markersMap.map {
                if (it.value.tag == marker.tag) {
                    keyDeletion = it.key
                }
            }

            marker?.remove()
            markersMap.remove(keyDeletion)
//            marker = null

            if (markersMap.isNotEmpty()) {
                //MainScope().launch {
                when (lineOrZoneDelimiter) {
                    1 -> { // Line
                        drawPolyline(markersMap)
                    }

                    2 -> {  // Zone
                        drawPolygone(markersMap)
                    }
                    else -> { // Nothing
                        // Do nothing
                    }
                }
                val cameraPositionPolygon = CameraPosition.fromLatLngZoom(marker?.position!!, 15.0f)
                mapsDelimiter?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionPolygon))
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    fun focusOnCurrentDevices(callback: () -> Unit) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val cameraPositionPolygon = CameraPosition.fromLatLngZoom(currentLatLng, 17.0f)
                mapsDelimiter?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionPolygon), object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                        // Animation finished, now execute the callback
                        callback.invoke()
                    }

                    override fun onCancel() {
                        // Animation cancelled, if needed handle it
                    }
                })
            }
        }
    }


    suspend fun addMarker(latLng: LatLng) {
        try {
            marker = null
            marker = mapsDelimiter?.addMarker {
                position(latLng)
                title("Pilier ${if (markersMap.keys.isEmpty()) 1 else markersMap.keys.last() + 1}")
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_point_marker))
            }

            marker?.tag = "p_${if (markersMap.keys.isEmpty()) 1 else markersMap.keys.last() + 1}"
            marker?.showInfoWindow()

            markersMap[if (markersMap.keys.isEmpty()) 1 else markersMap.keys.last() + 1] = marker!!
            mapsDelimiter?.animateCamera(CameraUpdateFactory.newLatLng(marker?.position!!))

            LogUtils.d(lineOrZoneDelimiter, "ADD MARKER")

            when (lineOrZoneDelimiter) {
                1 -> { // Line
                    if (markersMap.size > 1) {
                        drawPolyline(markersMap)
                    }
                }

                2 -> {  // Surface
                    if (markersMap.size > 2) {
                        drawPolygone(markersMap)
                    }
                }
                else -> { // Nothing

                }
            }

            setupAreaOfCurrentMapping()

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    private fun drawPolygone(pMarkersPolygon: MutableMap<Int, Marker>) {
       try {
           mapPointsList.clear()
           polygon?.remove()
           polygon = null

           polygon = mapsDelimiter?.addPolygon {
               pMarkersPolygon.map {
                   add(it.value.position)
                   strokeWidth(8.0f)
                   fillColor(R.color.gray)
                   clickable(true)
               }
           }

           pMarkersPolygon.mapValues {
               mapPointsList.add(it.value.position)
           }

           gPolygonCenter = getPolygonCenterPoint(mapPointsList)

           parcelleMapping.parcellePerimeter = Commons.convertDoubleToString(SphericalUtil.computeLength(mapPointsList))
           LogUtils.d(parcelleMapping.parcellePerimeter)
           // Compute perimeter
           labelDistanceFarmDelimiter.text = parcelleMapping.parcellePerimeter.plus("m")

           marker = markersMap.values.last()
           // Compute area
           computeSurface()
       } catch (ex: Exception) {
           ex.printStackTrace()
           LogUtils.e(ex.message)
           FirebaseCrashlytics.getInstance().recordException(ex)
       }
    }


    private fun drawPolyline(pMarkersPolyline: MutableMap<Int, Marker>) {
        try {
            polyline?.remove()
            mapPointsList.clear()
            polyline = null

            polyline = mapsDelimiter?.addPolyline {
                pMarkersPolyline.map {
                    add(it.value.position)
                    clickable(true)
                }
            }

            pMarkersPolyline.mapValues {
                LogUtils.e(Commons.TAG, "Pilier ${it.value.title}")
                mapPointsList.add(it.value.position)
            }

            marker = markersMap.values.last()
            parcelleMapping.parcellePerimeter =
                Commons.convertDoubleToString(SphericalUtil.computeLength(mapPointsList))
            LogUtils.d(parcelleMapping.parcellePerimeter)
            // Compute perimeter
            labelDistanceFarmDelimiter.text = parcelleMapping.parcellePerimeter.plus("m")
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    suspend fun selectMaker() {
    }


    suspend fun animateCamera() {
    }


    suspend fun <T> updateMap(drawType: Int, datas: MutableList<T>) {
    }


    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(pGoogleMap: GoogleMap) {
        try {
            mapsDelimiter = pGoogleMap

            mapsDelimiter?.mapType = MAP_TYPE_TERRAIN
            mapsDelimiter?.uiSettings?.isRotateGesturesEnabled = false

            mapsDelimiter?.setOnMapClickListener(this)
            mapsDelimiter?.setOnPolygonClickListener(this)
            mapsDelimiter?.setOnMarkerClickListener(this)
            getLocationPermission()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onMapClick(pLatLng: LatLng) {

        getDeviceLocationWithAccurancy()

        try {
            if (cardPopupMenuMapsTypeFarmDelimiter.visibility == View.VISIBLE) {
                cardPopupMenuMapsTypeFarmDelimiter.visibility = View.GONE
                return
            }

            //ToastUtils.showShort("manualOrGpsTrack $manualOrGpsTrack")

            if (manualOrGpsTrack == 1) {
                MainScope().launch {
                    addMarker(pLatLng)
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onPolygonClick(pPolygon: Polygon) {
    }


    override fun onMarkerClick(markerClicked: Marker): Boolean {
        try {
            marker = markerClicked
            Commons.showMessage(
                message = "Supprimer ce ${markerClicked.title}?",
                context = this,
                finished = false,
                callback = {
                    deleteAnyMarker()
                },
                positive = "Supprimer",
                deconnec = false,
                showNo = true
            )

            return true
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }

        return false
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        getDeviceLocation()
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    val itemParcelleMapp = CcbRoomDatabase.getDatabase(this)?.parcelleMappingDao()?.getParcellesMappingList()
    val currentHistoWayppointList: MutableList<LatLng> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val agentDoa = CcbRoomDatabase.getDatabase(this)?.agentDoa()

//        val item = CcbRoomDatabase.getDatabase(this)?.parcelleMappingDao()?.getParcellesMappingList()

//        LogUtils.d(item)

        labelProducteurNomText = intent.getStringExtra("producteur_nom")?:"N/A"
        labelParcelleCodeText = intent.getStringExtra("parcelle_code")?:"N/A"

        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            labelOwnerFarmDelimiter.text = agentDoa?.getAgent(SPUtils.getInstance().getInt(Constants.AGENT_ID)).let { "${it?.firstname} ${it?.lastname}" }.toString() //"Didier BOKA"

            val mapsDelimiterFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.googleMapsFarmDelimiter) as SupportMapFragment
            mapsDelimiterFragment.getMapAsync(this)

            fabMenuFarmDelimiter.setOnClickListener {
                onAddButtonClick()
            }

            setUpMappHistorie()
        
            getDeviceLocationWithAccurancy()

            fabSurfaceFarmDelimiter.setOnClickListener {
                lineOrZoneDelimiter = 2
                //linearSurfaceInfosContainerFarmDelimiter.visibility = View.VISIBLE
                showMappingTypeDialog()
            }

            linearSurfaceMappingContainerFarmDelimiter.setOnClickListener {
                lineOrZoneDelimiter = 2
                //linearSurfaceInfosContainerFarmDelimiter.visibility = View.VISIBLE
                showMappingTypeDialog()
            }

            fabDistanceFarmDelimiter.setOnClickListener {
                lineOrZoneDelimiter = 1

                //linearSurfaceInfosContainerFarmDelimiter.visibility = View.GONE
                showMappingTypeDialog()
            }

            linearDistanceMappingContainerFarmDelimiter.setOnClickListener {
                lineOrZoneDelimiter = 1

//                linearSurfaceInfosContainerFarmDelimiter.visibility = View.GONE
                showMappingTypeDialog()
            }


            imageMapTypeActionFarmDelimiter.setOnClickListener {
                cardPopupMenuMapsTypeFarmDelimiter.visibility = View.VISIBLE
            }

            imageMapTypeFarmDelimiter.setOnClickListener {
                cardPopupMenuMapsTypeFarmDelimiter.visibility = View.VISIBLE
            }

            imageBackFarmDelimiter.setOnClickListener {
                finish()
            }

            linearSatelliteTypeMappingContainerFarmDelimiter.setOnClickListener {
                mapsDelimiter?.mapType = MAP_TYPE_SATELLITE
                cardPopupMenuMapsTypeFarmDelimiter.visibility = View.GONE
            }

            linearTerrainTypeMappingContainerFarmDelimiter.setOnClickListener {
                mapsDelimiter?.mapType = MAP_TYPE_TERRAIN
                cardPopupMenuMapsTypeFarmDelimiter.visibility = View.GONE
            }

            linearOrdinaireTypeMappingContainerFarmDelimiter.setOnClickListener {
                mapsDelimiter?.mapType = MAP_TYPE_NORMAL
                cardPopupMenuMapsTypeFarmDelimiter.visibility = View.GONE
            }

            imageCancelFarmDelimiter.setOnClickListener {
                Commons.showMessage(
                    message = "Arreter le mapping ?",
                    context = this,
                    finished = false,
                    deconnec = false,
                    showNo = true,
                    callback = ::cancelWorkMapping
                )
            }

            imageDeleteMarkerFarmDelimiter.setOnClickListener {
                markersMap.clear()
                mapPointsList.clear()
                polyline?.remove()
                polygon?.remove()

                polyline = null
                polygon = null

                parcelleMapping.mutableWayPoints?.clear()
                mapsDelimiter?.clear()
            }

            imageUndoMarkerFarmDelimiter.setOnClickListener {
                LogUtils.e("TAGGG")
                MainScope().launch {
                    removeMarker()
                }
            }

            imageStartTrackGPSFarmDelimiter.setOnClickListener {
                if (trackingGPSStarted) {
                    mapsDelimiter?.setOnMyLocationChangeListener(null)
                    imageStartTrackGPSFarmDelimiter.setImageResource(R.drawable.ic_start_gps_mapping)
                    imageStartTrackGPSFarmDelimiter.clearAnimation()
                } else {
                    mapsDelimiter?.setOnMyLocationChangeListener(this)
                    imageStartTrackGPSFarmDelimiter.setImageResource(R.drawable.ic_stop_gps_mapping)
                    imageStartTrackGPSFarmDelimiter.startAnimation(blink)
                }

                trackingGPSStarted = !trackingGPSStarted
            }

            imagePlaceMarkerGPSFarmDelimiter.setOnClickListener {
                    MainScope().launch {
                        focusOnCurrentDevices {
                            launchAddMarker()
                        }
                    }
            }

            imageSaveMarkerGPSFarmDelimiter.setOnClickListener {
                Commons.showMessage(
                    message = "Enregistrer le tracé ?",
                    context = this,
                    showNo = true,
                    finished = true,
                    callback = ::saveWorkringParcelle
                )
            }
            imageSaveWorkFarmDelimiter.setOnClickListener {
                Commons.showMessage(
                    message = "Enregistrer le tracé ?",
                    context = this,
                    finished = true,
                    showNo = true,
                    callback = ::saveWorkringParcelle
                )
            }
        } catch (ex: Exception) { // Exception for maps initialization
            ex.printStackTrace()
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }

        fabMenuFarmDelimiter.setImageResource(R.drawable.baseline_add_white_24);
        // Set background tint color (adjust this according to your design)
        fabMenuFarmDelimiter.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        // Set ripple color (optional, adjust according to your design)
        fabMenuFarmDelimiter.setRippleColor(getResources().getColor(R.color.white));
        // Set other attributes as needed (e.g., elevation, translationZ)
        fabMenuFarmDelimiter.setElevation(0.8f);
    }

    private fun launchAddMarker() {
        MainScope().launch {
            addMarker(mapsDelimiter?.cameraPosition?.target!!)
        }
    }

    private fun setUpMappHistorie() {

        val dialogView = layoutInflater.inflate(R.layout.item_mapping_histo, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.mapp_historie_list_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        val items = listOf("Item 1", "Item 2", "Item 3") // Replace with your data


        var listHisto = itemParcelleMapp?.filter { labelProducteurNomText.equals(it.producteurId, ignoreCase = true) == true }?.map {
            CommonData(it.id, nom = "Code Parcelle: ${it.parcelleName} - ${it.parcelleSuperficie} HA", value = "PRODUCTEUR: ${it.producteurId}")
        }

        recyclerView.adapter = RvMapHistoAdapt(this@FarmDelimiterActivity, listHisto?.toList()?: arrayListOf())

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Liste des maps enrégistrés")
            .setNegativeButton("Annuler") { dialog, _ ->
                // Handle negative button click
                dialog.dismiss()
            }

        dialogMapHito = dialogBuilder.create()

        imageHistoFarmDelimiter.setOnClickListener {
            dialogMapHito?.show()
        }


    }

    fun onItemHistoSelected(position: Int) {

//        getDeviceLocationWithAccurancy()

        val histoMap = itemParcelleMapp?.get(position)

        val wayppointList = GsonUtils.fromJson<List<LatLng>>(histoMap?.parcelleWayPoints, object : TypeToken<List<LatLng>>(){}.type)

        if(currentHistoWayppointList.size > 0){

            callMarkerRemover({
                callMarkerDrawing(wayppointList)
                currentHistoWayppointList.addAll(wayppointList)
            })

            currentHistoWayppointList.clear()

        }else{
            callMarkerDrawing(wayppointList)
            currentHistoWayppointList.addAll(wayppointList)

        }

        dialogMapHito?.dismiss()
    }

    private fun callMarkerRemover(function: () -> Unit) {

        val jobs = mutableListOf<Job>()
        markersMap?.forEach {
            val job = MainScope().launch {
                removeMarker(it.value)
            }
            jobs.add(job)
        }
        MainScope().launch {
            jobs.forEach { it.join() }
            function.invoke()
        }
    }

    private fun callMarkerDrawing(wayppointList: List<LatLng>) {
        wayppointList?.forEach {
            MainScope().launch {
                addMarker(it)
            }
        }
    }

}
