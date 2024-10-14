package ci.progbandama.mobile.activities.forms

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.cartographies.FarmDelimiterActivity
import ci.progbandama.mobile.activities.forms.views.MultiSelectSpinner
import ci.progbandama.mobile.activities.infospresenters.ParcellePreviewActivity
import ci.progbandama.mobile.adapters.MultipleItemAdapter
import ci.progbandama.mobile.adapters.OmbrageAdapter
import ci.progbandama.mobile.databinding.ActivityParcelleBinding
import ci.progbandama.mobile.models.*
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.ParcelleDao
import ci.progbandama.mobile.repositories.databases.daos.ProducteurDao
import ci.progbandama.mobile.repositories.datas.ArbreData
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.AssetFileHelper
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.checkIfContentIsList
import ci.progbandama.mobile.tools.Commons.Companion.formatCorrectlyLatLongPoint
import ci.progbandama.mobile.tools.Commons.Companion.getSpinnerContent
import ci.progbandama.mobile.tools.Commons.Companion.showMessage
import ci.progbandama.mobile.tools.Commons.Companion.showYearPickerDialog
import ci.progbandama.mobile.tools.Commons.Companion.toModifString
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken

import java.util.*

class ParcelleActivity : AppCompatActivity(){


    companion object {
        const val TAG = "ParcelleActivity.kt"
    }


    private var valueOfParcelleCode: String? = "N/A"
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var commomUpdate: CommonData = CommonData()
    private var arbreOmbrParcelleAdapter: OmbrageAdapter? = null
    private var arbrOmbrListParcelle: MutableList<OmbrageVarieteModel> = arrayListOf()
    var producteurDao: ProducteurDao? = null
    var localitesList: MutableList<LocaliteModel>? = mutableListOf()
    var parcelleDao: ParcelleDao? = null

    var draftedDataParcelle: DataDraftedModel? = null
    var datePickerDialog: DatePickerDialog? = null

    var localiteNom = ""
    var localiteId = ""
    var producteursList: MutableList<ProducteurModel>? = null
    var wayPoints = mutableListOf<String>()
    var producteurNomPrenoms = ""
    var producteurId = ""
    var typeDeclaration = ""
    var parcelleMappingModel: ParcelleMappingModel? = null
    private val flip : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.flip) }
    var fromDatas = ""

    val sectionCommon = CommonData()
    val localiteCommon = CommonData()
    val producteurCommon = CommonData()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    // Function to check if location permissions are granted
    private fun isLocationPermissionGranted(): Boolean {
        if(PermissionUtils.getPermissions().containsAll(listOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)) == false){
            PermissionUtils.permission(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION).callback(object :
                PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    setupLocationPoint()
                }

                override fun onDenied(
                    deniedForever: MutableList<String>,
                    denied: MutableList<String>
                ) {

                }

            })


            return false
        }

        return true
    }

    private fun setupLocationPoint() {
        try {
            val locationResult = fusedLocationProviderClient?.lastLocation
            locationResult?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    val location = task.result
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // Do something with latitude and longitude
                        binding.editLatParcelle.setText(latitude.toString().formatCorrectlyLatLongPoint())
                        binding.editLongParcelle.setText(longitude.toString().formatCorrectlyLatLongPoint())
                    }else{
                        binding.editLatParcelle.setText("0.0")
                        binding.editLongParcelle.setText("-0.0")
                    }
                } else {
                    binding.editLatParcelle.setText("0.0")
                    binding.editLongParcelle.setText("-0.0")
                }
            }
        } catch (e: SecurityException) {
            LogUtils.e(e.message)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Function to request location permissions
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    // Function to check if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    // Function to retrieve current location
    private fun getCurrentLocation(): Location? {
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (isLocationEnabled() && isLocationPermissionGranted()) {
            setupLocationPoint()
        }else{
//            isLocationPermissionGranted()
            ToastUtils.showShort("ACTIVER LA LOCALISATION DANS LA BARRE DES TACHES")
        }
        return null
    }


    // Function to handle location permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can now get the location
                    val location = getCurrentLocation()
                    // Use the location as needed

                } else {
                    // Permission denied, handle this case accordingly
                    binding.editLatParcelle.setText("0.0")
                    binding.editLongParcelle.setText("-0.0")
                }
            }
        }
    }


    // Example usage
    private fun getLocation() {
        if (isLocationPermissionGranted()) {
            val location = getCurrentLocation()
            // Use the location as needed
//            if (location != null) {
//                val latitude = location.latitude
//                val longitude = location.longitude
//                // Do something with latitude and longitude
//                editLatParcelle.setText(latitude.toString().formatCorrectlyLatLongPoint())
//                editLongParcelle.setText(longitude.toString().formatCorrectlyLatLongPoint())
//            }else{
//                editLatParcelle.setText("0.0")
//                editLongParcelle.setText("-0.0")
//            }
        } else {
            requestLocationPermission()
            ToastUtils.showShort("ACTIVER LA LOCALISATION DANS LA BARRE DES TACHES")
        }
    }


    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null) {
        var sectionDao = ProgBandRoomDatabase.getDatabase(applicationContext)?.sectionsDao()
        var sectionList = sectionDao?.getAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        var libItem: String? = null
        currVal?.let { idc ->
            sectionList?.forEach {
                if(it.id.toString() == idc.toString()) libItem = it.libelle
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_section),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = sectionList?.size!! <= 0,
            currentVal = libItem ,
            spinner = binding.selectSectionParcelle,
            listIem = sectionList.map { it.libelle }
                ?.toList() ?: listOf(),
            onChanged = {

                val section = sectionList[it]
                //ogUtils.d(section)
                sectionCommon.nom = section.libelle!!
                sectionCommon.id = section.id!!

                setLocaliteSpinner(sectionCommon.id!!, currVal1, currVal2)

            },
            onSelected = { itemId, visibility ->

            })
    }


    fun setLocaliteSpinner(id: Int, currVal1:String? = null, currVal2: String? = null) {

        var localiteDao = ProgBandRoomDatabase.getDatabase(applicationContext)?.localiteDoa()
        var localitesListi = localiteDao?.getLocaliteBySection(id)
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal1?.let { idc ->
            localitesListi?.forEach {
                if(it.id.toString().equals(idc)) libItem = it.nom
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_localit),
            getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = localitesListi?.size!! <= 0,
            currentVal = libItem,
            spinner = binding.selectLocaliteParcelle,
            listIem = localitesListi.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                localitesListi.let { list ->
                    var localite = list.get(it)
                    localiteCommon.nom = localite.nom!!
                    localiteCommon.id = localite.id!!

                    setupProducteurSelection(localiteCommon.id!!, currVal2)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }


    fun setupProducteurSelection(id: Int, currVal2: String? = null) {
        producteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if (it.uid.toString().equals(idc)) libItem = "${it.nom} ${it.prenoms}"
                } else {
                    if (it.id.toString().equals(idc)) libItem = "${it.nom} ${it.prenoms}"
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = producteursList?.size!! <= 0,
            currentVal = libItem,
            spinner = binding.selectProducteurParcelle,
            listIem = producteursList?.map { "${ it.nom } ${ it.prenoms }" }?.toList() ?: listOf(),
            onChanged = {
                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if (producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    } else producteurCommon.id = producteur.uid

                    //setupPa
                }


            },
            onSelected = { itemId, visibility ->

            })

    }


    fun collectDatas() {

        val itemModelOb = getParcellObjet()

        if(itemModelOb == null) return

        val parcelle = itemModelOb.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()
                isSynced = false
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                origin = "local"

                varieteStr = GsonUtils.toJson(binding.spinnerVarieteParcelle.selectedStrings)
                //mappingPoints = wayPoints
                protectionStr = GsonUtils.toJson(binding.selectProtectionParcelle.selectedStrings)
                arbreStr = GsonUtils.toJson((binding.recyclerArbrOmbrListParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { ArbreData(null, it.uid.toString(), it.nombre) })
                wayPointsString =  ApiClient.gson.toJson(wayPoints)
                arbreStrateStr = GsonUtils.toJson((binding.recyclerAutreArbrOmbrParcelle.adapter as MultipleItemAdapter).getMultiItemAdded().map { ParcAutreOmbrag(it.id, nom = it.value1, strate = it.value.toString(), qte = it.value2) })
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb.second?.apply {
            this.add(
                Pair(
                    "Les mésures de protection",
                    binding.selectProtectionParcelle.selectedStrings.map { "${it}" }.toModifString(commaReplace = "\n")
                )
            )
            this.add(
                Pair(
                    "Les arbres de la parcelle",
                    (binding.recyclerArbrOmbrListParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete} | ${it.nombre}" }.toModifString(commaReplace = "\n")
                )
            )
            this.add(
                Pair(
                    "Les autres arbres de la parcelle",
                    (binding.recyclerAutreArbrOmbrParcelle.adapter as MultipleItemAdapter).getMultiItemAdded().map { "${it.value} | ${it.value1} | ${it.value2}" }.toModifString(commaReplace = "\n")
                )
            )
        }?.map {
            MapEntry(it.first, it.second)
        }

        if(intent?.getIntExtra("sync_uid", 0) != 0){
            parcelle?.apply {
                id = commomUpdate.listOfValue?.first()?.toInt()
                uid = commomUpdate.listOfValue?.get(1)?.toLong()?:0
                sync_update = true
                isSynced = false
                origin = "local"
            }
        }

//        LogUtils.json(ApiClient.gson.toJson(parcelle))
//        LogUtils.d(commomUpdate.listOfValue)
//        debugModelToJson(parcelle)

        val intentParcellePreview = Intent(this, ParcellePreviewActivity::class.java)
        intentParcellePreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
        intentParcellePreview.putExtra("preview", parcelle)
        intentParcellePreview.putExtra("draft_id", draftedDataParcelle?.uid)
        startActivity(intentParcellePreview)
    }


    private fun getParcellObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()):  Pair<ParcelleModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false
//        return ParcelleModel(
//            producteurId = producteurId,
//            producteurNom = producteurNomPrenoms,
//            //anneeCreation = editAnneParcelle.text?.trim().toString(),
//            latitude = editLatParcelle.text?.trim().toString(),
//            longitude = editLongParcelle.text?.trim().toString(),
//            typedeclaration = typeDeclaration,
//            superficie = editSuperficieParcelle.text?.trim().toString(),
//            //culture = editNomParcelle.text?.trim().toString(),
//            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
//            origin = "local"
//        )

        var itemList = getSetupParcelleModel(ParcelleModel(), mutableListOf<Pair<String,String>>())
        //LogUtils.d(.toString())
        var allField = itemList.second
        var isMissing = false
        var message = ""
        var notNecessaire = listOf<String>()
        for (field in allField){
            if(field.second.isNullOrBlank() && notNecessaire.contains(field.first.lowercase()) == false){
                message = getString(R.string.le_champ_intitul_n_est_pas_renseign, field.first)
                isMissing = true
                break
            }
        }

        for (field in allField){
            if(field.second.isNullOrBlank() && necessaryItem.contains(field.first)){
                message = getString(R.string.le_champ_intitul_n_est_pas_renseign, field.first)
                isMissing = true
                isMissingDial2 = true
                break
            }
        }

        if(isMissing && (isMissingDial || isMissingDial2) ){
            showMessage(
                message,
                this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )

            return null
        }

        return  itemList
    }


//    fun clearFields() {
////        editAnneParcelle.text = null
////        editNomParcelle.text = null
//
//        editLatParcelle.text = null
//        editLongParcelle.text = null
//
//        /*if (!fromDatas.lowercase().contains("CONTENT", ignoreCase = true)) {
//            setupProducteurSelection()
//            linearProducteurContainerParcelle.visibility = VISIBLE
//        } else {
//            producteurId = ""
//            linearProducteurContainerParcelle.visibility = GONE
//        }*/
//
//        //setupTyprDeclarationSelection()
//
//        typeDeclaration = ""
//
//        editSuperficieParcelle.text = null
//
//        producteurId = ""
//        producteurNomPrenoms = ""
//
//        //editNomParcelle.requestFocus()
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val parcelleWayPointsMappedToken = object : TypeToken<MutableList<LatLng>>() {}.type

        data?.let {
            try {
                parcelleMappingModel = it.getParcelableExtra("data")

                parcelleMappingModel?.let { parcelle ->
                    LogUtils.d(Commons.TAG, parcelle.parcelleNameTag)
                    parcelle.mutableWayPoints = GsonUtils.fromJson(parcelle.parcelleWayPoints, parcelleWayPointsMappedToken)

                    parcelle.mutableWayPoints?.map { latlng ->
                        wayPoints.add("${latlng.longitude.toString().formatCorrectlyLatLongPoint()}, ${latlng.latitude.toString().formatCorrectlyLatLongPoint()}, 0")
                    }

                    binding.editLatParcelle.setText(parcelle.parcelleLat.toString().formatCorrectlyLatLongPoint())
                    binding.editLongParcelle.setText(parcelle.parcelleLng.toString().formatCorrectlyLatLongPoint())
                    binding.editSuperficieParcelle.setText(parcelle.parcelleSuperficie)
                    binding.editWayPointsParcelle.text = Editable.Factory.getInstance().newEditable(GsonUtils.toJson(wayPoints))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    fun disableField(typeDeclaration: String = "") {
        if (typeDeclaration == "GPS") {
            binding.editSuperficieParcelle.isEnabled = false
            binding.editLatParcelle.isEnabled = false
            binding.editLongParcelle.isEnabled = false
            binding.linearWayPointsMappingParcelle.visibility = VISIBLE
            binding.editWayPointsParcelle.isEnabled = false
            binding.clickLatLongParcelle.visibility = GONE
            binding.clickToMappingParcelle.visibility = VISIBLE
        } else if(typeDeclaration == "Verbale") {
            binding.editSuperficieParcelle.isEnabled = true
            binding.editLatParcelle.isEnabled = true
            binding.editLongParcelle.isEnabled = true
            binding.linearWayPointsMappingParcelle.visibility = GONE
            binding.clickLatLongParcelle.visibility = VISIBLE
            binding.clickToMappingParcelle.visibility = GONE
        }

    }


    override fun onBackPressed() {
        SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LAT,)
        SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LNG,)
        SPUtils.getInstance().remove(Constants.PREFS_SUPERFICIE,)

        super.onBackPressed()
    }


    fun draftParcelle(draftModel: DataDraftedModel?) {
        val itemModelOb = getParcellObjet(false, necessaryItem = mutableListOf(
            "Selectionner un producteur"
        ))

        if(itemModelOb == null) return

        val parcelleDraft = itemModelOb.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()
                isSynced = false
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                origin = "local"

                varieteStr = GsonUtils.toJson(binding.spinnerVarieteParcelle.selectedStrings)
                protectionStr = GsonUtils.toJson(binding.selectProtectionParcelle.selectedStrings)
                arbreStr = GsonUtils.toJson((binding.recyclerArbrOmbrListParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { ArbreData(null, it.uid.toString(), it.nombre) })
                wayPointsString =  ApiClient.gson.toJson(wayPoints)
                arbreStrateStr = GsonUtils.toJson((binding.recyclerAutreArbrOmbrParcelle.adapter as MultipleItemAdapter).getMultiItemAdded().map { ParcAutreOmbrag(it.id, nom = it.value.toString(), strate = it.value1, qte = it.value2) })
            }

        }

        val newDraft = parcelleDraft?.apply {
            if(intent?.getIntExtra("sync_uid", 0) != 0 || parcelleDraft.sync_update){
                this.id = commomUpdate.listOfValue?.get(0)?.toInt()
                this.uid = commomUpdate.listOfValue?.get(1)?.toLong()!!
                this.sync_update = true
            }
        }
//        LogUtils.json((parcelleDraft))
        LogUtils.json(ApiClient.gson.toJson(newDraft))
        LogUtils.d(commomUpdate.listOfValue)

        showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {

                ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(newDraft),
                        typeDraft = "parcelle",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                showMessage(
                    message = getString(R.string.contenu_ajout_aux_brouillons),
                    context = this,
                    finished = true,
                    callback = {
                        Commons.playDraftSound(this)
                        binding.imageDraftBtn.startAnimation(Commons.loadShakeAnimation(this))
                    },
                    positive = getString(R.string.ok),
                    deconnec = false,
                    false
                )
            },
            positive = getString(R.string.oui),
            deconnec = false,
            showNo = true
        )
    }


    fun undraftedDatas(draftedData: DataDraftedModel?, data: ParcelleModel? = null) {
        var parcelleDrafted: ParcelleModel = ParcelleModel()
        var product: ProducteurModel?  = null


        draftedData?.let {
            parcelleDrafted = ApiClient.gson.fromJson(draftedData.datas, ParcelleModel::class.java)
            val intNullo = parcelleDrafted.section?.toIntOrNull()
            if(intNullo != null){
//                LogUtils.i(intNullo)
                setupSectionSelection(parcelleDrafted.section, parcelleDrafted.localite, parcelleDrafted.producteurId)
            }else setupSectionSelection()
        }

        data?.let {
            parcelleDrafted = data
            product = ProgBandRoomDatabase.getDatabase(this)?.producteurDoa()?.getProducteurByID(data.producteurId?.toInt()?:0)
//            val sectionIt =  ProgBandRoomDatabase.getDatabase(this)?.sectionsDao()?.getById(product?.section)
//            val localiteIt =  ProgBandRoomDatabase.getDatabase(this)?.localiteDoa()?.getLocalite(product?.localite?.toInt()?:0)
            setupSectionSelection(product?.section.toString(), product?.localitesId.toString(), product?.id.toString())
        }

//        LogUtils.d(parcelleDrafted, commomUpdate)
//        Commons.debugModelToJson(parcelleDrafted)

        if(parcelleDrafted.sync_update == true || intent?.getIntExtra("sync_uid", 0) != 0){
            intent.putExtra("sync_uid", parcelleDrafted.uid.toInt())
            commomUpdate.listOfValue = listOf<String>(parcelleDrafted.id.toString(), parcelleDrafted.uid.toString()).toMutableList()
            binding.labelTitleMenuAction.text = "MISE A JOUR FICHE PARCELLE"
        }

//        LogUtils.d(parcelleDrafted.sync_update, commomUpdate.listOfValue)
        
        valueOfParcelleCode = parcelleDrafted.codeParc

        parcelleDrafted.wayPointsString?.let {
            wayPoints.addAll(GsonUtils.fromJson<MutableList<String>>(it, object : TypeToken<MutableList<String>>(){}.type))
        }

        val listArbresOth = ProgBandRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()
        parcelleDrafted.arbreStr?.let {
            if(it.isNotEmpty()) {
                val listIt = GsonUtils.fromJson<MutableList<ArbreData>>(it, object : TypeToken<MutableList<ArbreData>>(){}.type )
                val newArbreLi = listIt.map {ito->
//                    LogUtils.d(ito)
                    listArbresOth?.filter { it.id.toString().equals(ito.arbre) == true }?.let {
                        if(it.size > 0){
                            ito.id = it.first().id
                            ito.arbre = it.first().nom?.trim()+" | "+ it.first().nomScientifique
                        }
                    }
//                    LogUtils.json(ito)
                    OmbrageVarieteModel(ito.id?:0, "${ ito.arbre?.replace(" | ", "/")?.replace("| ", "/") }", ito.nombre)
                }
                (binding.recyclerArbrOmbrListParcel.adapter as OmbrageAdapter).setOmbragesList(newArbreLi.toMutableList())
            }
        }

//        LogUtils.d(parcelleDrafted.autreArbreStr)
        parcelleDrafted.arbreStrateStr?.let {
            if(!it.isNullOrEmpty()){
                val listIt = GsonUtils.fromJson<MutableList<ParcAutreOmbrag>>(it, object : TypeToken<MutableList<ParcAutreOmbrag>>(){}.type )
//                LogUtils.d(listIt)
                if(listIt != null){
                    val newAutreArbreLi = listIt.map { ito->
                        AdapterItemModel(0, value = ito.nom, value1 = ito.strate, value2 = ito.qte)
                    }
                    (binding.recyclerAutreArbrOmbrParcelle.adapter as MultipleItemAdapter).setDataToRvItem(newAutreArbreLi.toMutableList())
                }
            }
        }

        Commons.setListenerForSpinner(this,
            "Y'a t'il d'autres arbres à ombrage dans la parcelle ?",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectYesNoAutreArbrOmbragParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            currentVal = parcelleDrafted.yesnoautrearbreombrag,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerAutreArbrOmbragParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.type_de_d_claration_superficie),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectDeclarationTypeParcelle,
            itemChanged = arrayListOf(Pair(1, "Verbal"), Pair(2, "Gps")),
            currentVal = parcelleDrafted.typedeclaration,
            listIem = resources.getStringArray(R.array.declarationType)
                .toList(),
            onChanged = {
                typeDeclaration = resources.getStringArray(R.array.declarationType)[it]
                disableField(typeDeclaration)
            },
            onSelected = { itemId, visibility ->
            })

        if(parcelleDrafted.protectionStr.isNullOrEmpty() == false){
            setupMoyProtectMultiSelection(GsonUtils.fromJson(parcelleDrafted.protectionStr, object : TypeToken<MutableList<String>>() {}.type))
            if(parcelleDrafted.protectionStr?.contains("Autre", ignoreCase = true) == true) binding.containerAutreProtectParcelle.visibility = View.VISIBLE
        }else{
            setupMoyProtectMultiSelection()
        }


        Commons.setupItemMultiSelection(this, binding.spinnerVarieteParcelle,
            "Quelles sont les variétés de culture ?",
            (AssetFileHelper.getListDataFromAsset(
                21,
                this
            ) as MutableList<CommonData>).toList(),
            currentList = GsonUtils.fromJson(parcelleDrafted.varieteStr.checkIfContentIsList(), object : TypeToken<MutableList<String>>(){}.type)){

        }

        Commons.setListenerForSpinner(this,
            getString(R.string.la_parcelle_est_elle_r_g_n_r_e),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectParcRegenParcelle,
            currentVal = parcelleDrafted.parcelleRegenerer,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerNumAnnRegenParcelle.visibility = visibility
                    binding.containerSuperfConcernee.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_type_de_document_poss_des_tu),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectDocumentParcelle,
            currentVal = parcelleDrafted.typeDoc,
            listIem = (AssetFileHelper.getListDataFromAsset(
                10,
                this
            ) as MutableList<TypeDocumentModel>).map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreVarieteProducteur.visibility = visibility
//                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.d_finit_le_niveau_de_la_pente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectNiveauPente,
            currentVal = parcelleDrafted.niveauPente,
            listIem = resources.getStringArray(R.array.niveau_pente)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_un_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresentCourEauParcelle,
            currentVal = parcelleDrafted.presenceCourDeau,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerCourEauParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_est_le_cour_ou_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectCourEauParcelle,
            currentVal = parcelleDrafted.courDeau,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(0, this) as MutableList<CourEauModel>).map { "${it.nom}" }
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerAutreCourDeau.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_mesures_de_protection),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectMesurProtectParcelle,
            currentVal = parcelleDrafted.existeMesureProtection,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerSelectProtection.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_une_pente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectYaPenteParcelle,
            currentVal = parcelleDrafted.existePente,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerNiveauPente.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_pr_sences_d_rosion),getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectSignErosParcelle,
            currentVal = parcelleDrafted.erosion,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->

            })

        Commons.setListenerForSpinner(this,
            "Avez-vous des arbres à ombrage dans la parcelle ?",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresenceArbreOmbrParcelle,
            currentVal = parcelleDrafted.yesornoarbreombrage,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1){
                    binding.containerArOmbragebrParcelle.visibility = visibility
                }
            })

        val listArbres = mutableListOf<ArbreModel>()
        listArbres.add(0, ArbreModel(uid = 0, nom = "Autres", nomScientifique = "Autres", strate = null))
        listArbres.addAll(ProgBandRoomDatabase.getDatabase(this)?.arbreDao()?.getAll() ?: mutableListOf())

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_l_arbre),
            getString(R.string.la_liste_des_arbres_d_ombrage_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectArbrOmbrParcel,
            listIem = listArbres?.map { "${ it.nom+" |"} ${it.nomScientifique}" }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        passSetupParcellModel(parcelleDrafted)
    }


    fun getSetupParcelleModel(prodModel: ParcelleModel, mutableListOf: MutableList<Pair<String, String>>): Pair<ParcelleModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_parcelle)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }


    fun passSetupParcellModel(prodModel: ParcelleModel?) {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_parcelle)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }


    fun setupMoyProtectMultiSelection(currentList : MutableList<String> = mutableListOf()) {
        val protectList = resources.getStringArray(R.array.mesure_protection)
        var listSelectProtectPosList = mutableListOf<Int>()
        var listSelectProtectList = mutableListOf<String>()

        var indItem = 0
        (protectList).forEach {
            if(currentList.size > 0){ if(currentList.contains(it)) listSelectProtectPosList.add(indItem) }
            indItem++
        }

        binding.selectProtectionParcelle.setTitle(getString(R.string.s_lectionner_les_m_sures_de_protections))
        binding.selectProtectionParcelle.setItems(protectList)
        //multiSelectSpinner.hasNoneOption(true)
        binding.selectProtectionParcelle.setSelection(listSelectProtectPosList.toIntArray())
        binding.selectProtectionParcelle.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
            override fun selectedIndices(indices: MutableList<Int>?) {
                listSelectProtectPosList.clear()
                listSelectProtectPosList.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                listSelectProtectList.clear()
                listSelectProtectList.addAll(strings?.toMutableList() ?: arrayListOf())
                if(listSelectProtectList.contains("Autre")) binding.containerAutreProtectParcelle.visibility = View.VISIBLE else binding.containerAutreProtectParcelle.visibility = View.GONE
            }

        })
    }

    private lateinit var binding: ActivityParcelleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParcelleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(com.intuit.ssp.R.dimen._6ssp),
            resources.getDimension(com.intuit.ssp.R.dimen._5ssp))

        parcelleDao = ProgBandRoomDatabase.getDatabase(this)?.parcelleDao()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.clickCloseBtn.setOnClickListener {
            SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LAT,)
            SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LNG,)
            SPUtils.getInstance().remove(Constants.PREFS_SUPERFICIE,)
            finish()
        }

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        binding.clickCancelParcelle.setOnClickListener {
            if(intent?.getLongExtra("sync_uid", 0L) != 0L){
                ActivityUtils.getActivityByContext(this)?.finish()
            }else {
                ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                ActivityUtils.getActivityByContext(this)?.finish()
            }
        }

        binding.clickSaveParcelle.setOnClickListener {
            collectDatas()
        }

        binding.clickLatLongParcelle.setOnClickListener {
            getLocation()
        }

        binding.clickToMappingParcelle.setOnClickListener {
            val intentParcelleMaker = Intent(this@ParcelleActivity, FarmDelimiterActivity::class.java)
            intentParcelleMaker.putExtra("producteur_nom", producteurCommon.nom)
            intentParcelleMaker.putExtra("parcelle_code", valueOfParcelleCode)
            startActivityForResult(intentParcelleMaker, 202)
        }

        binding.imageDraftBtn.setOnClickListener {
            draftParcelle(draftedDataParcelle ?: DataDraftedModel(uid = 0))
        }

        setOtherListener()

        if (intent.getStringExtra("from") != null) {
            if(intent.getIntExtra("drafted_uid", 0) != 0){
                fromDatas = intent.getStringExtra("from") ?: ""
                draftedDataParcelle = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataParcelle!!)
            }else{
                val dataUid = intent?.getIntExtra("sync_uid", 0)
                //LogUtils.d(inspectUid)
                if(dataUid != 0) {
                    binding.labelTitleMenuAction.text = "MISE A JOUR FICHE PARCELLE"
//                    clickSaveInspection.setOnClickListener {
//                        collectDatasUpdate(inspectUid)
//                    }
//                    imageDraftBtn.visibility = View.GONE

                    val updateData = ProgBandRoomDatabase.getDatabase(this)?.parcelleDao()?.getByUid(dataUid?.toInt()?:0)
                    updateData?.forEach {
                        undraftedDatas(null, it)
                    }
                }
            }
        }else{
            setAllListener()
        }
    }


    fun setOmbrageParcelleRV() {
        val listArbres = mutableListOf<ArbreModel>()
        listArbres.add(0, ArbreModel(uid = 0, nom = "Autres", nomScientifique = "Autres", strate = null))
        listArbres.addAll(ProgBandRoomDatabase.getDatabase(this)?.arbreDao()?.getAll() ?: mutableListOf())

        try {
            arbrOmbrListParcelle = mutableListOf<OmbrageVarieteModel>()
            arbreOmbrParcelleAdapter = OmbrageAdapter(arbrOmbrListParcelle, getString(R.string.arbre), getString(R.string.nombre))
            binding.recyclerArbrOmbrListParcel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerArbrOmbrListParcel.adapter = arbreOmbrParcelleAdapter

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        binding.clickAddArbreOmbrParcel.setOnClickListener {
            try {
                if (binding.selectArbrOmbrParcel.selectedItem.toString()
                        .isEmpty() || binding.editQtArbrOmbrParcel.text.toString().isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_d_ombrage_svp), this, callback = {})
                    return@setOnClickListener
                }

                val arbreLibel = binding.selectArbrOmbrParcel.getSpinnerContent().split("|")

                listArbres?.forEach {
                    if(it.nomScientifique?.contains(arbreLibel[1].trim(), ignoreCase = true) == true){
                        val ombrageVariete = OmbrageVarieteModel(
                            it.id?:0,
                            binding.selectArbrOmbrParcel.getSpinnerContent().replace(" | ", "/").replace("| ", "/"),
                            binding.editQtArbrOmbrParcel.text.toString().trim()
                        )
                        addOmbrageVariete(ombrageVariete)

                        binding.editQtArbrOmbrParcel.text?.clear()
                    }
                }

            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

    }


    fun addOmbrageVariete(ombrageVarieteModel: OmbrageVarieteModel) {
        try {
            if (ombrageVarieteModel.variete?.length == 0) return

            arbrOmbrListParcelle.forEach {
                if (it.variete?.uppercase() == ombrageVarieteModel.variete?.uppercase() && it.nombre == ombrageVarieteModel.nombre) {
                    ToastUtils.showShort(getString(R.string.cette_vari_t_est_deja_ajout_e))
                    return
                }
            }

            arbrOmbrListParcelle.add(ombrageVarieteModel)
            arbreOmbrParcelleAdapter?.notifyDataSetChanged()

            binding.editQtArbrOmbrParcel.text?.clear()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    private fun setOtherListener() {
        //setupTyprDeclarationSelection()

        Commons.addNotZeroAtFirstToET(binding.editAgeCacaoParcelle)
        Commons.addNotZeroAtFirstToET(binding.editQtArbrOmbrParcel)
        Commons.addNotZeroAtFirstToET(binding.editNbrCacaoHecParcelle)

        setOmbrageParcelleRV()

//        setAutreArbrOmbragRV()

        Commons.setFiveItremRV(this, binding.recyclerAutreArbrOmbrParcelle, binding.clickAddAutreArbrOmbrParcelle, binding.selectStrateAutreArbrOmbrParcelle,null,null,binding.editNomAutreArbrOmbrParcelle,binding.editQteAutreArbrOmbrParcelle, defaultItemSize = 3, nbSpinner = 1, nbEdit = 2, libeleList = mutableListOf<String>("Strate", "Arbre", "Quantité", "", ""))

        binding.editNumAnneeRegenParcelle.setOnClickListener { showYearPickerDialog(binding.editNumAnneeRegenParcelle) }
        binding.editAnneeCreationParcelle.setOnClickListener { showYearPickerDialog(binding.editAnneeCreationParcelle) }
    }

//    fun setAutreArbrOmbragRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val operatListInfoProd = mutableListOf<OmbrageVarieteModel>()
//        var countN = 0
//        libeleList.forEach {
//            operatListInfoProd.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
//            countN++
//        }
//        val operatInfoProdAdapter = OmbrageAdapter(operatListInfoProd,
//            "Strate", "Nom d'Arbre")
//
//
//        try {
//            recyclerAutreArbrOmbrParcelle.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerAutreArbrOmbrParcelle.adapter = operatInfoProdAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddAutreArbrOmbrParcelle.setOnClickListener {
//            try {
//                if (selectStrateAutreArbrOmbrParcelle.selectedItem.toString()
//                        .isEmpty() || editNomAutreArbrOmbrParcelle.text.toString().isEmpty()
//                ) {
//                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_svp), this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val varieteArbre = OmbrageVarieteModel(
//                    0,
//                    selectStrateAutreArbrOmbrParcelle.selectedItem.toString(),
//                    editNomAutreArbrOmbrParcelle.text.toString().trim()
//                )
//
//                if(varieteArbre.variete?.length?:0 > 0){
//                    operatListInfoProd?.forEach {
//                        if (it.variete?.uppercase() == varieteArbre.variete?.uppercase() && it.nombre == varieteArbre.nombre) {
//                            ToastUtils.showShort(getString(R.string.cet_donnees_est_deja_ajout_e))
//                            return@setOnClickListener
//                        }
//                    }
//
//                    operatListInfoProd?.add(varieteArbre)
//                    operatInfoProdAdapter?.notifyDataSetChanged()
//
//                    selectStrateAutreArbrOmbrParcelle.setSelection(0)
//                    editNomAutreArbrOmbrParcelle.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//    }


    private fun setAllListener() {

        setupSectionSelection()

        setupMoyProtectMultiSelection()

        Commons.setListenerForSpinner(this,
            getString(R.string.type_de_d_claration_superficie),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectDeclarationTypeParcelle,
            itemChanged = arrayListOf(Pair(1, "Verbal"), Pair(2, "Gps")),
            listIem = resources.getStringArray(R.array.declarationType)
                .toList(),
            onChanged = {
                typeDeclaration = resources.getStringArray(R.array.declarationType)[it]
                disableField(typeDeclaration)
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Avez-vous des arbres à ombrage dans la parcelle ?",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresenceArbreOmbrParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1){
                    binding.containerArOmbragebrParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il d'autres arbres à ombrage dans la parcelle ?",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectYesNoAutreArbrOmbragParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerAutreArbrOmbragParcelle.visibility = visibility
                }
            })

        Commons.setupItemMultiSelection(this, binding.spinnerVarieteParcelle,
            "Quelles sont les variétés de culture ?",
            (AssetFileHelper.getListDataFromAsset(
                21,
                this
            ) as MutableList<CommonData>).toList(),
            currentList = arrayListOf()
        ){

        }

        Commons.setListenerForSpinner(this,
            getString(R.string.la_parcelle_est_elle_r_g_n_r_e),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectParcRegenParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerNumAnnRegenParcelle.visibility = visibility
                    binding.containerSuperfConcernee.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_type_de_document_poss_des_tu),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectDocumentParcelle,
            listIem = (AssetFileHelper.getListDataFromAsset(
                10,
                this
            ) as MutableList<TypeDocumentModel>).map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreVarieteProducteur.visibility = visibility
//                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_un_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresentCourEauParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerCourEauParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_est_le_cour_ou_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectCourEauParcelle,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(0, this) as MutableList<CourEauModel>).map { "${it.nom}" }
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerAutreCourDeau.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_mesures_de_protection),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectMesurProtectParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerSelectProtection.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_une_pente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectYaPenteParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                .toList(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerNiveauPente.visibility = visibility
                }
            })

//        Commons.setListenerForSpinner(this,
//            "Choix de l'arbre ?","La liste des arbres d'ombrage semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectArbrOmbrParcel,
//            listIem = (AssetFileHelper.getListDataFromAsset(
//                25,
//                this
//            ) as MutableList<CommonData>)?.map { it.nom }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })

        val listArbres = mutableListOf<ArbreModel>()
        listArbres.add(0, ArbreModel(uid = 0, nom = "Autres", nomScientifique = "Autres", strate = null))
        listArbres.addAll(ProgBandRoomDatabase.getDatabase(this)?.arbreDao()?.getAll() ?: mutableListOf())

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_l_arbre), getString(R.string.la_liste_des_arbres_d_ombrage_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectArbrOmbrParcel,
            listIem = listArbres?.map { "${ it.nom+" |"} ${it.nomScientifique}" }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

    }
}
