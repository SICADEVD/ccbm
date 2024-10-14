package ci.progbandama.mobile.activities.cartographies

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ci.progbandama.mobile.R
import ci.progbandama.mobile.databinding.ActivityParcelleMappingBinding
import ci.progbandama.mobile.models.ParcelleMappingModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ktx.utils.area



@SuppressWarnings("ALL")
class ParcelleMappingActivity : AppCompatActivity(), OnMapReadyCallback,
    OnMapClickListener, OnMyLocationChangeListener, OnMyLocationButtonClickListener,
    OnPolygonClickListener, OnMarkerClickListener {


    private var locationPermissionGranted: Boolean = false
    private var closed = false
    private var trackingGPSStarted = false
    private var producteurID = ""

    /**
     *  @return: 1 = Manual, 2 = GPS
     */
    private var mappingType = 0
    /**
     * @return:  1 = Distance, 2 = Surface
     */
    private var mappingDrawType = 0
    private var mappingOld: MutableList<ParcelleMappingModel> = mutableListOf()
    private var zonesMappingList: MutableList<Polygon> = mutableListOf()

    lateinit var gPolygonCenter: LatLng
    private var zma = 0

    // For geo mapping
    private var parcelleMapping = ParcelleMappingModel() // for save
    private var parcellePolyOptions = PolygonOptions()
    private var parcellePolylinesOptions = PolylineOptions()
    private var parcellePolygon: Polygon? = null
    private var parcellePolyline: Polyline? = null
    //  private var latLngsList: MutableList<LatLng>? = mutableListOf()
    private var markerPoint: Marker? = null
    private var markerOptions = MarkerOptions()
    //  private var markersList: MutableList<Marker> = mutableListOf()
    private var markersMap: MutableMap<Int, LatLng> = mutableMapOf()

    private var markerPerimeter: Double = 0.0

    lateinit var googleMaps: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cameraPosition: CameraPosition? = null
    private var lastKnownLocation: Location? = null

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }
    private val outToBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.out_to_button
        )
    }
    private val outToTop: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.out_to_top
        )
    }
    private val inFromTop: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.in_from_top
        )
    }
    private val inFromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.in_from_button
        )
    }
    private val inFromLeft: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.in_from_left
        )
    }
    private val outToLeft: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.out_to_left
        )
    }
    private val blink: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.blink) }


    // A Function used to set the Animation effect
//    fun setAnimation(closed: Boolean) {
//        if (!closed) {
//            linearDistanceMappingContainer.startAnimation(fromBottom)
//            linearSurfaceMappingContainer.startAnimation(fromBottom)
//            fabMenuMapping.startAnimation(rotateOpen)
//        } else {
//            linearDistanceMappingContainer.startAnimation(toBottom)
//            linearSurfaceMappingContainer.startAnimation(toBottom)
//            fabMenuMapping.startAnimation(rotateClose)
//        }
//    }


    fun setVisibility(closed: Boolean) {
        if (!closed) {
            binding.linearDistanceMappingContainer.visibility = VISIBLE
            binding.linearSurfaceMappingContainer.visibility = VISIBLE
        } else {
            binding.linearDistanceMappingContainer.visibility = GONE
            binding.linearSurfaceMappingContainer.visibility = GONE
        }
    }


    fun onAddButtonClick() {
        setVisibility(closed)
        closed = !closed
    }


    override fun onMapReady(pMaps: GoogleMap) {
        googleMaps = pMaps
        googleMaps.mapType = MAP_TYPE_TERRAIN
        googleMaps.uiSettings.isRotateGesturesEnabled = false

        googleMaps.setOnMapClickListener(this)
        googleMaps.setOnMyLocationButtonClickListener(this)
        googleMaps.setOnPolygonClickListener(this)
        googleMaps.setOnMarkerClickListener(this)

        // for marker on maps
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_point_marker))

        //  for polygon on maps
        parcellePolyOptions.clickable(true)
        parcellePolyOptions.strokeWidth(8.0f)
        parcellePolyOptions.fillColor(R.color.gray)
        parcellePolyOptions.geodesic(true)
        parcellePolyOptions.strokeJointType(JointType.ROUND)

        //  for polyline on maps
        parcellePolylinesOptions.clickable(true)
        parcellePolylinesOptions.width(8.0f)
        parcellePolylinesOptions.color(R.color.gray)
        parcellePolylinesOptions.geodesic(true)

        getLocationPermission()
        loadParcellesMapped()
    }


    fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            getDeviceLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                2022
            )
        }
    }


    fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        googleMaps.moveCamera(
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
                    googleMaps.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            LogUtils.e("TAG", "Current location is null. Using defaults.")
            e.printStackTrace()
        }
    }


    fun cancelWorkMapping() {
        binding.relativeActionMarkerBottomMappingContainer.startAnimation(outToBottom)
        binding.relativeActionMarkerBottomMappingContainer.visibility = GONE

        binding.linearActionMarkerBottomMapping.visibility = GONE
        binding.linearActionGPSMarkerBottomMapping.visibility = GONE

        binding.linealToolbarActionMappingParcelle.visibility = GONE
        binding.linealToolbarActionMappingParcelle.startAnimation(outToTop)

        binding.linearSnippetInfosMapping.visibility = GONE
        binding.linearSnippetInfosMapping.startAnimation(outToLeft)

        binding.fabMenuMapping.startAnimation(inFromLeft)
        binding.fabMenuMapping.visibility = VISIBLE

        binding.linealToolbarMappingParcelle.startAnimation(inFromTop)
        binding.linealToolbarMappingParcelle.visibility = VISIBLE

        markersMap.clear()

        parcelleMapping.mutableWayPoints?.clear()
        parcellePolyOptions.points.clear()
        parcellePolygon?.remove()
        googleMaps.clear()
    }


    fun startWorkManualMapping() {
        binding.linealToolbarMappingParcelle.startAnimation(outToTop)
        binding.linealToolbarMappingParcelle.visibility = GONE

        binding.fabMenuMapping.startAnimation(outToLeft)
        binding.fabMenuMapping.visibility = GONE

        binding.linearActionMarkerBottomMapping.visibility = VISIBLE

        binding.relativeActionMarkerBottomMappingContainer.startAnimation(inFromBottom)
        binding.relativeActionMarkerBottomMappingContainer.visibility = VISIBLE


        binding.linealToolbarActionMappingParcelle.startAnimation(inFromTop)
        binding.linealToolbarActionMappingParcelle.visibility = VISIBLE

        binding.linearSnippetInfosMapping.startAnimation(inFromLeft)
        binding.linearSnippetInfosMapping.visibility = VISIBLE
    }


    fun startWorkGpsMapping() {
        binding.linealToolbarMappingParcelle.startAnimation(outToTop)
        binding.linealToolbarMappingParcelle.visibility = GONE

        binding.fabMenuMapping.startAnimation(outToLeft)
        binding.fabMenuMapping.visibility = GONE

        binding.linealToolbarActionMappingParcelle.startAnimation(inFromTop)
        binding.linealToolbarActionMappingParcelle.visibility = VISIBLE

        binding.linearSnippetInfosMapping.startAnimation(inFromLeft)
        binding.linearSnippetInfosMapping.visibility = VISIBLE

        binding.linearActionGPSMarkerBottomMapping.visibility = VISIBLE

        binding.relativeActionMarkerBottomMappingContainer.startAnimation(inFromBottom)
        binding.relativeActionMarkerBottomMappingContainer.visibility = VISIBLE
    }


//    fun clearAllAniviations() {
//        linearActionGPSMarkerBottomMapping.clearAnimation()
//        linearSnippetInfosMapping.clearAnimation()
//        linealToolbarActionMappingParcelle.clearAnimation()
//        linealToolbarMappingParcelle.clearAnimation()
//        linearActionMarkerBottomMapping.clearAnimation()
//        fabMenuMapping.clearAnimation()
//    }


    fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                googleMaps.isMyLocationEnabled = true
                googleMaps.uiSettings.isMyLocationButtonEnabled = true
                googleMaps.uiSettings.isZoomControlsEnabled = true
            } else {
                googleMaps.isMyLocationEnabled = false
                googleMaps.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            e.printStackTrace()
        }
    }


    override fun onMapClick(pLatLng: LatLng) {
        if (binding.cardPopupMenuMapsTypeMapping.visibility == VISIBLE) {
            binding.cardPopupMenuMapsTypeMapping.visibility = GONE
            return
        }

        if (mappingType == 1) {
            placeMarker(pLatLng)
        }
    }


    fun drawPolygon(markers: MutableList<LatLng>?) {
        parcellePolyOptions.points.clear()
        parcellePolygon?.remove()

        markers?.map {
            parcellePolyOptions.add(it)
        }

        parcellePolygon = googleMaps.addPolygon(parcellePolyOptions)
        gPolygonCenter = getPolygonCenterPoint(markers!!)
        computeSurface()
    }


    fun drawPolylines(markers: MutableList<LatLng>?) {
        parcellePolylinesOptions.points.clear()
        parcellePolyline?.remove()

        markers?.map {
            parcellePolylinesOptions.add(it)
        }

        parcellePolyline = googleMaps.addPolyline(parcellePolylinesOptions)
    }


    fun placeMarker(pickedLatLng: LatLng?) {
        if (markerPoint != null) {
            markerPerimeter += computePerimeter(markerPoint?.position!!, pickedLatLng!!)
            parcelleMapping.parcellePerimeter = Commons.convertDoubleToString(markerPerimeter)
            binding.labelDistanceMapping.text = parcelleMapping.parcellePerimeter.plus(" m")
        }

        markerOptions
            .position(pickedLatLng!!)
            .draggable(false)

        markerPoint = googleMaps.addMarker(markerOptions)!!

        //  markersList.add(markerPoint!!)
        //  latLngsList?.add(pickedLatLng)

        markersMap[markersMap.size + 1] = pickedLatLng

        if (markersMap.size > 2) {
            val polylinesList = mutableListOf<LatLng>()
            markersMap.map {
                polylinesList.add(it.value)
            }

            if (mappingDrawType == 1) {
                drawPolylines(polylinesList)
            } else {
                drawPolygon(polylinesList)
            }
        }

        //  markersMap.putIfAbsent(markersMap.size + 1, pickedLatLng)
        showDatasJson()
    }


    fun showDatasJson() {
        LogUtils.json(markersMap)
    }


    fun drawGPSShapes() {

    }


    override fun onMyLocationChange(pLocation: Location) {
        googleMaps.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    pLocation.latitude,
                    pLocation.longitude
                ), 18F
            )
        )

        markersMap[markersMap.size + 1] = LatLng(pLocation.latitude, pLocation.longitude)

        val pointsList = mutableListOf<LatLng>()
        markersMap.map {
            pointsList.add(it.value)
        }

        binding.labelDistanceMapping.text =
            Commons.convertDoubleToString(SphericalUtil.computeLength(pointsList)).plus(" m")

        if (mappingType == 2) {
            if (mappingDrawType == 1) {
                drawPolylines(pointsList)
            } else {
                drawPolygon(pointsList)
            }
        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        return true
    }


    fun showMappingTypeDialog() {
        val dialog = Dialog(this, R.style.DialogTheme)
//        Commons.adjustTextViewSizesInDialog(this, dialogBuild, "",   this.resources.getDimension(com.intuit.ssp.R.dimen._6ssp)
//            ,true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.linear_maps_type_maneul_gps_view)

        val linearManualType = dialog.findViewById<LinearLayout>(R.id.linearManuelleMesureType)
        val linearGPSType = dialog.findViewById<LinearLayout>(R.id.linearGpsMesureType)

        linearManualType.setOnClickListener {
            mappingType = 1
            dialog.dismiss()
            onAddButtonClick()
            startWorkManualMapping()

        }

        linearGPSType.setOnClickListener {
            mappingType = 2
            dialog.dismiss()
            onAddButtonClick()

            startWorkGpsMapping()

        }
        dialog.show()
    }


    private fun getPolygonCenterPoint(polygonPointsList: MutableList<LatLng>): LatLng {
        var centerLatLng: LatLng? = null
        val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        for (i in 0 until polygonPointsList.size) {
            builder.include(polygonPointsList[i])
        }
        val bounds: LatLngBounds = builder.build()
        centerLatLng = bounds.center
        return centerLatLng
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
    }


    fun loadParcellesMapped() {
        mappingOld = ProgBandRoomDatabase.getDatabase(this)?.parcelleMappingDao()
            ?.getProducteurParcellesList(producteurID)!!
        mappingOld.forEach {

        }
    }


    fun computePerimeter(markerA: LatLng, markerB: LatLng): Double {
        return SphericalUtil.computeDistanceBetween(markerA, markerB)
    }


    fun computeSurface() {
        parcelleMapping.parcelleSuperficie = Commons.convertDoubleToString((parcellePolygon?.area!!) * 0.0001)
        binding.labelSurfaceMapping.text = parcelleMapping.parcelleSuperficie.plus(" Ha")
    }


    override fun onPolygonClick(pPolygon: Polygon) {
        parcellePolygon = pPolygon
    }


    override fun onMarkerClick(pMarker: Marker): Boolean {
        LogUtils.e(Commons.TAG, "${pMarker.position.latitude} : ${pMarker.position.longitude}")
        return true // m²
    }


    fun undoMarker() {
        if (markersMap.isEmpty()) {
            ToastUtils.showShort("Vous n'avez pas assez de points")
            return
        }

        /*for (position in 0..markersList.size) {
            val marker = markersList[position]

            if (marker.position.longitude == markerPoint?.position?.longitude && marker.position.latitude == markerPoint?.position?.latitude) {
                markersList[position].remove()
                markersList.removeAt(position)
                latLngsList?.removeAt(position)

                if (mappingDrawType == 1) {
                    parcellePolylinesOptions.points.removeAt(position)
                } else {
                    parcellePolyOptions.points.removeAt(position)
                }
                break
            }
        }*/

        if (mappingDrawType == 1) {
            parcellePolylinesOptions.points.remove(parcellePolylinesOptions.points.last())
        } else {
            parcellePolyOptions.points.remove(parcellePolyOptions.points.last())
        }

        removePoints()

        val points = mutableListOf<LatLng>()
        markersMap.map {
            points.add(it.value)
        }

        if (markersMap.size > 2) {
            if (mappingDrawType == 1) {
                drawPolylines(points)
            } else {
                drawPolygon(points)
            }
        }

        //markerPoint = markersList.last()
        markersMap.remove(markersMap.size)
        showDatasJson()
    }


    fun removePoints() {
        googleMaps.clear()
    }


    fun saveWorkringParcelle() {
        markersMap.map {
            parcelleMapping.mutableWayPoints?.add(it.value)
        }

        parcelleMapping.parcellePerimeter = binding.labelDistanceMapping.text.toString().trim()
        parcelleMapping.parcelleNameTag = mappingType.toString()

        parcelleMapping.parcelleLat = gPolygonCenter.latitude.toString()
        parcelleMapping.parcelleLng = gPolygonCenter.longitude.toString()

        parcelleMapping.parcelleWayPoints = GsonUtils.toJson(parcelleMapping.mutableWayPoints)
        ProgBandRoomDatabase.getDatabase(this)?.parcelleMappingDao()?.insert(parcelleMapping)

        val returnIntent = Intent()
        returnIntent.putExtra("data", parcelleMapping)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private lateinit var binding:ActivityParcelleMappingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_parcelle_mapping)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.labelOwnerMapping.text = intent.getStringExtra("producteur_nom")

        val mapsFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMapsParcelle) as SupportMapFragment
        mapsFragment.getMapAsync(this)

        binding.fabMenuMapping.setOnClickListener {
            onAddButtonClick()
        }

        binding.fabSurfaceMapping.setOnClickListener {
            mappingDrawType = 2
            binding.linearSurfaceInfosContainer.visibility = VISIBLE
            showMappingTypeDialog()
        }

        binding.linearSurfaceMappingContainer.setOnClickListener {
            mappingDrawType = 2
            binding.linearSurfaceInfosContainer.visibility = VISIBLE
            showMappingTypeDialog()
        }

        binding.fabDistanceMapping.setOnClickListener {
            mappingDrawType = 1

            binding.linearSurfaceInfosContainer.visibility = GONE
            showMappingTypeDialog()
        }

        binding.linearDistanceMappingContainer.setOnClickListener {
            mappingDrawType = 1

            binding.linearSurfaceInfosContainer.visibility = GONE
            showMappingTypeDialog()
        }

        binding.imageMapTypeActionMapping.setOnClickListener {
            binding.cardPopupMenuMapsTypeMapping.visibility = VISIBLE
        }

        binding.imageMapTypeMapping.setOnClickListener {
            binding.cardPopupMenuMapsTypeMapping.visibility = VISIBLE
        }

        binding.imageBackMappingParcelle.setOnClickListener {
            finish()
        }

        binding.linearSatelliteTypeMappingContainer.setOnClickListener {
            googleMaps.mapType = MAP_TYPE_SATELLITE
            binding.cardPopupMenuMapsTypeMapping.visibility = GONE
        }

        binding.linearTerrainTypeMappingContainer.setOnClickListener {
            googleMaps.mapType = MAP_TYPE_TERRAIN
            binding.cardPopupMenuMapsTypeMapping.visibility = GONE
        }

        binding.linearOrdinaireTypeMappingContainer.setOnClickListener {
            googleMaps.mapType = MAP_TYPE_NORMAL
            binding.cardPopupMenuMapsTypeMapping.visibility = GONE
        }

        binding.imageCancelMappingParcelle.setOnClickListener {
            Commons.showMessage(
                message = "Arreter le mapping ?",
                context = this,
                finished = false,
                deconnec = false,
                callback = ::cancelWorkMapping
            )
        }

        binding.imageDeleteMarkerMapping.setOnClickListener {
            markersMap?.clear()
            parcelleMapping.mutableWayPoints?.clear()
            parcellePolyOptions.points.clear()
            parcellePolygon?.remove()
            googleMaps.clear()
        }

        binding.imageDeleteMarkerMapping.setOnClickListener {
            parcellePolygon?.remove()
        }

        binding.imageUndoMarkerMapping.setOnClickListener {
            undoMarker()
        }

        binding.imageStartTrackGPSMapping.setOnClickListener {
            if (trackingGPSStarted) {
                googleMaps.setOnMyLocationChangeListener(null)
                binding.imageStartTrackGPSMapping.setImageResource(R.drawable.ic_start_gps_mapping)
                binding.imageStartTrackGPSMapping.clearAnimation()
            } else {
                googleMaps.setOnMyLocationChangeListener(this)
                binding.imageStartTrackGPSMapping.setImageResource(R.drawable.ic_stop_gps_mapping)
                binding.imageStartTrackGPSMapping.startAnimation(blink)
            }

            trackingGPSStarted = !trackingGPSStarted
        }

        binding.imagePlaceMarkerGPSMapping.setOnClickListener {
            placeMarker(googleMaps.cameraPosition.target)
        }

        binding.imageSaveMarkerGPSMapping.setOnClickListener {
            Commons.showMessage(
                message = "Enregistrer le tracé ?",
                context = this,
                finished = true,
                deconnec = false,
                callback = ::saveWorkringParcelle
            )
        }
        binding.imageSaveWorkMapping.setOnClickListener {
            Commons.showMessage(
                message = "Enregistrer le tracé ?",
                context = this,
                finished = true,
                deconnec = false,
                callback = ::saveWorkringParcelle
            )
        }

        // showMappingTypeDialog()
        val pgs = ProgressDialog(this, R.style.DialogTheme)
        Commons.adjustTextViewSizesInDialog(this, pgs, "Chargement des parcelles",   this.resources.getDimension(com.intuit.ssp.R.dimen._6ssp)
            ,false)
        pgs.setCancelable(false)
        //pgs.setMessage("Chargement des parcelles")
        // pgs.show()

        // Chargement des parcelles deja enregistrees
    }
}
