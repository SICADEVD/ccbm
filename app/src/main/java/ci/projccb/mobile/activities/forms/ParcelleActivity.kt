package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.cartographies.FarmDelimiterActivity
import ci.projccb.mobile.activities.infospresenters.ParcellePreviewActivity
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.ParcelleDao
import ci.projccb.mobile.repositories.databases.daos.ProducteurDao
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.android.gms.maps.model.LatLng
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_parcelle.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class ParcelleActivity : AppCompatActivity(R.layout.activity_parcelle) {


    companion object {
        const val TAG = "ParcelleActivity.kt"
    }


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


    fun setupLocaliteSelection() {
        localitesList = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

        if (localitesList?.size == 0) {
            showMessage(
                "La liste des Localités semble vide, veuillez procéder à la synchronisation des données svp.",
                this,
                finished = true,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false

            )
            return
        }

        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
        selectLocaliteParcelle!!.adapter = localiteAdapter

        selectLocaliteParcelle.setTitle("Choisir la localite")
        selectLocaliteParcelle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val locality = localitesList!![position]
                localiteNom = locality.nom!!

                localiteId = if (locality.isSynced) locality.id!!.toString() else locality.uid.toString()

                setupProducteurSelection(localiteId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupTyprDeclarationSelection() {
//        selectDeclarationTypeParcelle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                typeDeclaration = resources.getStringArray(R.array.declarationType)[position]
//                disableField()
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//
//            }
//        }
    }


    fun setupProducteurSelection(localiteId: String) {
        producteurDao = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
        producteursList = producteurDao?.getProducteursByLocalite(localite = localiteId) ?: mutableListOf()
        val producteursDatas: MutableList<CommonData> = mutableListOf()


        if (producteursList?.size == 0) {
            showMessage(
                "La liste des producteurs de cette Localité semble vide, veuillez procéder à la synchronisation des données svp.",
                this,
                finished = false,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false

            )
            return
        }

        producteursList?.map {
            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
        }?.let {
            producteursDatas.addAll(it)
        }

        val parcelleDraftedLocal = ApiClient.gson.fromJson(draftedDataParcelle?.datas, ParcelleModel::class.java)
        selectProducteurParcelle!!.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)


        if (fromDatas.lowercase().contains("CONTENT", ignoreCase = true)) {
            producteurId = parcelleDraftedLocal.producteurId ?: "0"
            producteurNomPrenoms = parcelleDraftedLocal.producteurNom ?: ""
            //editProducteurNotFoundInfosParcelle.setText(parcelleDraftedLocal.producteurNom)
            //linearProducteurNotFoundInfosContainerParcelle.visibility = VISIBLE
            linearProducteurContainerParcelle.visibility = GONE
        } else {
            if (parcelleDraftedLocal != null) {
                // producteurId = parcelleDraftedLocal.producteurId.toString()
                // val producteursLists = CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                // val producteursDatas: MutableList<CommonData> = mutableListOf()
                // producteursLists?.map {
                //    CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
                // }?.let {
                //    producteursDatas.addAll(it)
                // }
                // selectProducteurParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)
                provideDatasSpinnerSelection(
                    selectProducteurParcelle,
                    parcelleDraftedLocal.producteurNom,
                    producteursDatas
                )
            }
        }

        selectProducteurParcelle.setTitle("Choisir le producteur")

        selectProducteurParcelle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val producteur = producteursList!![position]
                producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"

                    if (producteur.isSynced) {
                        producteurId = producteur.id!!.toString()
                    } else {
                        producteurId = producteur.uid.toString()
                    }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                selectProducteurParcelle.setSelection(0)
                producteurId = (selectProducteurParcelle.selectedItem as ProducteurModel).id.toString()
            }
        }
    }


    fun collectDatas() {

        if (producteurId.isEmpty()) {
            showMessage(
                "Choisissez un producteur svp.",
                this,
                finished = false,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false

            )
            return
        }

//        if (editNomParcelle.text.toString().isEmpty()) {
//            showMessage(
//                message = "Renseignez la culture de la parcelle",
//                context = this,
//                finished = false,
//                callback = {},
//                positive = "OK",
//                deconnec = false,
//                showNo = false
//            )
//            return
//        }

//        if (editAnneParcelle.text.toString().isEmpty()) {
//            showMessage(
//                message = "Renseignez l'année de la parcelle",
//                context = this,
//                finished = false,
//                callback = {},
//                positive = "OK",
//                deconnec = false,
//                showNo = false
//            )
//            return
//        }

        if (typeDeclaration == "GPS" && wayPoints.isEmpty()) {
            showMessage(
                message = "Les données fournes sont insuffisantes !",
                context = this,
                finished = false,
                callback = {},
                positive = "OK",
                deconnec = false,
                showNo = false
            )

            return
        }

        val parcelleModel = getParcellObjet()

        parcelleModel.mappingPoints = wayPoints
        parcelleModel.wayPointsString =  ApiClient.gson.toJson(wayPoints)

        val intentParcellePreview = Intent(this, ParcellePreviewActivity::class.java)
        intentParcellePreview.putExtra("preview", parcelleModel)
        intentParcellePreview.putExtra("draft_id", draftedDataParcelle?.uid)
        startActivity(intentParcellePreview)
    }

    private fun getParcellObjet(): ParcelleModel {
        return ParcelleModel(
            producteurId = producteurId,
            producteurNom = producteurNomPrenoms,
            //anneeCreation = editAnneParcelle.text?.trim().toString(),
            latitude = editLatParcelle.text?.trim().toString(),
            longitude = editLongParcelle.text?.trim().toString(),
            typedeclaration = typeDeclaration,
            superficie = editSuperficieParcelle.text?.trim().toString(),
            //culture = editNomParcelle.text?.trim().toString(),
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local"
        )
    }


    fun clearFields() {
//        editAnneParcelle.text = null
//        editNomParcelle.text = null

        editLatParcelle.text = null
        editLongParcelle.text = null

        /*if (!fromDatas.lowercase().contains("CONTENT", ignoreCase = true)) {
            setupProducteurSelection()
            linearProducteurContainerParcelle.visibility = VISIBLE
        } else {
            producteurId = ""
            linearProducteurContainerParcelle.visibility = GONE
        }*/

        setupTyprDeclarationSelection()

        typeDeclaration = ""

        editSuperficieParcelle.text = null

        producteurId = ""
        producteurNomPrenoms = ""

        //editNomParcelle.requestFocus()
    }


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
                        wayPoints.add("${latlng.longitude},${latlng.latitude},0")
                    }

                    editLatParcelle.setText(parcelle.parcelleLat)
                    editLongParcelle.setText(parcelle.parcelleLng)
                    editSuperficieParcelle.setText(parcelle.parcelleSuperficie)
                    editWayPointsParcelle.text = Editable.Factory.getInstance().newEditable(GsonUtils.toJson(wayPoints))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    fun disableField() {
        if (typeDeclaration == "GPS") {
            editSuperficieParcelle.isEnabled = false
            editLatParcelle.isEnabled = false
            editLongParcelle.isEnabled = false
            linearWayPointsMappingParcelle.visibility = VISIBLE
            editWayPointsParcelle.isEnabled = false
            clickLatLongParcelle.visibility = GONE
            clickToMappingParcelle.visibility = VISIBLE
        } else {
            editSuperficieParcelle.isEnabled = true
            editLatParcelle.isEnabled = true
            editLongParcelle.isEnabled = true
            linearWayPointsMappingParcelle.visibility = GONE
            clickLatLongParcelle.visibility = VISIBLE
            clickToMappingParcelle.visibility = GONE
        }
    }


    override fun onBackPressed() {
        SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LAT,)
        SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LNG,)
        SPUtils.getInstance().remove(Constants.PREFS_SUPERFICIE,)

        super.onBackPressed()
    }


    fun draftParcelle(draftModel: DataDraftedModel?) {
        val parcelleDraft = getParcellObjet()

        showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {

                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(parcelleDraft),
                        typeDraft = "parcelle",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                showMessage(
                    message = "Contenu ajouté aux brouillons !",
                    context = this,
                    finished = true,
                    callback = {
                        Commons.playDraftSound(this)
                        imageDraftBtn.startAnimation(Commons.loadShakeAnimation(this))
                    },
                    positive = "OK",
                    deconnec = false,
                    false
                )
            },
            positive = "OUI",
            deconnec = false,
            showNo = true
        )
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        val parcelleDrafted = ApiClient.gson.fromJson(draftedData.datas, ParcelleModel::class.java)

        if (parcelleDrafted.codeParc.toString().isNotEmpty()) {
            //linearCodeContainerParcelle.visibility = VISIBLE
            //editCodeParcelle.setText(parcelleDrafted.codeParc ?: "")
        }

        // Localite
        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val localitesDatas: MutableList<CommonData> = mutableListOf()
        localitesLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            localitesDatas.addAll(it)
        }
        selectLocaliteParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
        provideDatasSpinnerSelection(
            selectLocaliteParcelle,
            parcelleDrafted.localiteNom,
            localitesDatas
        )

        // Type selection superficie
        //provideStringSpinnerSelection(
            //selectDeclarationTypeParcelle,
            //parcelleDrafted.typedeclaration,
            //resources.getStringArray(R.array.declarationType)
        //)

        //editNomParcelle.setText(parcelleDrafted.culture)
        editSuperficieParcelle.setText(parcelleDrafted.superficie)
        editLatParcelle.setText(parcelleDrafted.latitude)
        editLongParcelle.setText(parcelleDrafted.longitude)
        //editAnneParcelle.setText(parcelleDrafted.anneeCreation)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parcelleDao = CcbRoomDatabase.getDatabase(this)?.parcelleDao()


//        editAnneParcelle.doAfterTextChanged {
//            val textFiedl = it?.toString()
//
//            if (textFiedl.toString().length == 4) {
//                if (textFiedl.toString().trim().toInt() < 1960) {
//                    showMessage(
//                        "La date ne doit pas etre inferieur à 1960",
//                        this,
//                        finished = false,
//                        {},
//                        "OK",
//                        deconnec = false,
//                        showNo = false
//                    )
//                    return@doAfterTextChanged
//                }
//            }
//        }

        clickCloseBtn.setOnClickListener {
            SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LAT,)
            SPUtils.getInstance().remove(Constants.PREFS_POLYGON_CENTER_LNG,)
            SPUtils.getInstance().remove(Constants.PREFS_SUPERFICIE,)
            finish()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickCancelParcelle.setOnClickListener {
            clearFields()
        }

        clickSaveParcelle.setOnClickListener {
            collectDatas()
        }

        clickLatLongParcelle.setOnClickListener {
            editLatParcelle.text = Editable.Factory.getInstance().newEditable(SPUtils.getInstance().getString(Constants.PREFS_COMMON_LAT, "0.0"))
            editLongParcelle.text = Editable.Factory.getInstance().newEditable(SPUtils.getInstance().getString(Constants.PREFS_COMMON_LNG, "0.0"))
        }

        clickToMappingParcelle.setOnClickListener {
            val intentParcelleMaker = Intent(this@ParcelleActivity, FarmDelimiterActivity::class.java)
            intentParcelleMaker.putExtra("producteur_nom", producteurNomPrenoms)
            startActivityForResult(intentParcelleMaker, 202)
        }

        imageDraftBtn.setOnClickListener {
            draftParcelle(draftedDataParcelle ?: DataDraftedModel(uid = 0))
        }

        setupTyprDeclarationSelection()
        setupLocaliteSelection()

//        editAnneParcelle.setOnClickListener {
//            datePickerDialog = null
//            val calendar: Calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = 0
//            val dayOfMonth = 0
//            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
//                editAnneParcelle.setText("$year")
//            }, year, month, dayOfMonth)
//
//            datePickerDialog!!.datePicker.minDate = DateTime.parse("01/01/1960", DateTimeFormat.forPattern("dd/MM/yyyy")).millis
//            datePickerDialog!!.datePicker.maxDate = DateTime.now().millis
//            datePickerDialog?.show()
//        }

        //applyFiltersDec(editSuperficieParcelle, withZero = true)


        if (intent.getStringExtra("from") != null) {
            fromDatas = intent.getStringExtra("from") ?: ""
            draftedDataParcelle = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataParcelle!!)
        }
    }
}
