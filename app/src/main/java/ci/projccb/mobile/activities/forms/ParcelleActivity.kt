package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.cartographies.FarmDelimiterActivity
import ci.projccb.mobile.activities.infospresenters.ParcellePreviewActivity
import ci.projccb.mobile.activities.infospresenters.ProducteurPreviewActivity
import ci.projccb.mobile.adapters.CultureProducteurAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.ParcelleDao
import ci.projccb.mobile.repositories.databases.daos.ProducteurDao
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_parcelle.*
import kotlinx.android.synthetic.main.activity_producteur.selectLocaliteProducteur
import kotlinx.android.synthetic.main.activity_producteur_menage.selectProducteurMenage
import kotlinx.android.synthetic.main.activity_producteur_menage.selectSectionProducteurMenage
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.recyclerCultureInfosProducteur
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class ParcelleActivity : AppCompatActivity(R.layout.activity_parcelle){


    companion object {
        const val TAG = "ParcelleActivity.kt"
    }


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

    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();


    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null) {
        var sectionDao = CcbRoomDatabase.getDatabase(applicationContext)?.sectionsDao();
        var sectionList = sectionDao?.getAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        var libItem: String? = null
        currVal?.let { idc ->
            sectionList?.forEach {
                if(it.id == idc.toInt()) libItem = it.libelle
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la section !",
            "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = libItem ,
            spinner = selectSectionParcelle,
            listIem = sectionList?.map { it.libelle }
                ?.toList() ?: listOf(),
            onChanged = {

                val section = sectionList!![it]
                //ogUtils.d(section)
                sectionCommon.nom = section.libelle!!
                sectionCommon.id = section.id!!

                setLocaliteSpinner(sectionCommon.id!!, currVal1, currVal2)

            },
            onSelected = { itemId, visibility ->

            })
    }

    fun setLocaliteSpinner(id: Int, currVal1:String? = null, currVal2: String? = null) {

        var localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa();
        var localitesListi = localiteDao?.getLocaliteBySection(id)
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal1?.let { idc ->
            localitesListi?.forEach {
                if(it.id == idc.toInt()) libItem = it.nom
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la localité !",
            "La liste des localités semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectLocaliteProducteur,
            listIem = localitesListi?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                localitesListi?.let { list ->
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
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix du producteur !",
            "La liste des producteurs semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurMenage,
            listIem = producteursList?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    producteurCommon.id = producteur.id!!

                    //setupProducteurSelection(localiteCommon.id, currVal2)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }



//        producteursList?.map {
//            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
//        }?.let {
//            producteursDatas.addAll(it)
//        }
//
//        val menageDraftedLocal = ApiClient.gson.fromJson(draftedDataMenage?.datas, ProducteurMenageModel::class.java)
//        selectProducteurMenage!!.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)
//
//        if (menageDraftedLocal != null) {
//            provideDatasSpinnerSelection(
//                selectProducteurMenage,
//                menageDraftedLocal.producteurNomPrenoms,
//                producteursDatas
//            )
//        }
//
//        selectProducteurMenage.setTitle("Choisir le producteur")
//        selectProducteurMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val producteur = producteursList!![position]
//                producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"
//                producteurCode = producteur.codeProd.toString()
//
//                producteurId = if (producteur.isSynced) {
//                    producteur.id.toString()
//                } else {
//                    producteur.uid.toString()
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }



    fun setupTyprDeclarationSelection() {
        selectDeclarationTypeParcelle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                typeDeclaration = resources.getStringArray(R.array.declarationType)[position]
                disableField()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }



    fun collectDatas() {

//        if (producteurId.isEmpty()) {
//            showMessage(
//                "Choisissez un producteur svp.",
//                this,
//                finished = false,
//                callback = {},
//                positive = "Compris !",
//                deconnec = false,
//                showNo = false
//
//            )
//            return
//        }

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

//        if (typeDeclaration == "GPS" && wayPoints.isEmpty()) {
//            showMessage(
//                message = "Les données fournes sont insuffisantes !",
//                context = this,
//                finished = false,
//                callback = {},
//                positive = "OK",
//                deconnec = false,
//                showNo = false
//            )
//
//            return
//        }

        val itemModelOb = getParcellObjet()

        if(itemModelOb == null) return

        val parcelle = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()
                isSynced = false
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                origin = "local"

                mappingPoints = wayPoints
                wayPointsString =  ApiClient.gson.toJson(wayPoints)
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.map { MapEntry(it.first, it.second) }

        val intentParcellePreview = Intent(this, ParcellePreviewActivity::class.java)
        intentParcellePreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
        intentParcellePreview.putExtra("preview", parcelle)
        intentParcellePreview.putExtra("draft_id", draftedDataParcelle?.uid)
        startActivity(intentParcellePreview)
    }

    private fun getParcellObjet(): Pair<ParcelleModel, MutableList<Pair<String, String>>>? {
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
                message = "Le champ intitulé : `${field.first}` n'est pas renseigné !"
                isMissing = true
                break
            }
        }

        if(isMissing){
            showMessage(
                message,
                this,
                finished = false,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false
            )

            return null
        }

        return  itemList
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
        val itemModelOb = getParcellObjet()

        if(itemModelOb == null) return

        val parcelleDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()
                isSynced = false
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                origin = "local"

                mappingPoints = wayPoints
                wayPointsString =  ApiClient.gson.toJson(wayPoints)
            }
        }

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

        //if (parcelleDrafted.codeParc.toString().isNotEmpty()) {
            //linearCodeContainerParcelle.visibility = VISIBLE
            //editCodeParcelle.setText(parcelleDrafted.codeParc ?: "")
        //}

        // Localite
//        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//        val localitesDatas: MutableList<CommonData> = mutableListOf()
//        localitesLists?.map {
//            CommonData(id = it.id, nom = it.nom)
//        }?.let {
//            localitesDatas.addAll(it)
//        }
//        selectLocaliteParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//        provideDatasSpinnerSelection(
//            selectLocaliteParcelle,
//            parcelleDrafted.localiteNom,
//            localitesDatas
//        )

        // Type selection superficie
        provideStringSpinnerSelection(
            selectDeclarationTypeParcelle,
            parcelleDrafted.typedeclaration,
            resources.getStringArray(R.array.declarationType)
        )

        //editNomParcelle.setText(parcelleDrafted.culture)
//        editSuperficieParcelle.setText(parcelleDrafted.superficie)
//        editLatParcelle.setText(parcelleDrafted.latitude)
//        editLongParcelle.setText(parcelleDrafted.longitude)
        //editAnneParcelle.setText(parcelleDrafted.anneeCreation)

        setupSectionSelection(parcelleDrafted.section, parcelleDrafted.localite, parcelleDrafted.producteurId)

        Commons.setListenerForSpinner(this,
            "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = spinnerVarieteParcelle,
            currentVal = parcelleDrafted.variete,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(
                21,
                this
            ) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreVarieteProducteur.visibility = visibility
//                }
            })

        Commons.setListenerForSpinner(this,
            "La parcelle est-elle régénérée ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectParcRegenParcelle,
            currentVal = parcelleDrafted.parcelleRegenerer,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNumAnnRegenParcelle.visibility = visibility
                    containerSuperfConcernee.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Quel type de document ?","La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectDocumentParcelle,
            currentVal = parcelleDrafted.typeDoc,
            listIem = (AssetFileHelper.getListDataFromAsset(
                10,
                this
            ) as MutableList<TypeDocumentModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreVarieteProducteur.visibility = visibility
//                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il un plan d'eau ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectPresentCourEauParcelle,
            currentVal = parcelleDrafted.presenceCourDeau,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerCourEauParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il des mesures de protection ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectMesurProtectParcelle,
            currentVal = parcelleDrafted.existeMesureProtection,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerSelectProtection.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il une pente ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectYaPenteParcelle,
            currentVal = parcelleDrafted.existePente,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNiveauPente.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il des présences d'érosion ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectSignErosParcelle,
            currentVal = parcelleDrafted.erosion,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->

            })

        Commons.setListenerForSpinner(this,
            "Choix de l'arbre ?","La liste des arbres d'ombrage semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectArbrOmbrParcel,
            listIem = (AssetFileHelper.getListDataFromAsset(
                25,
                this
            ) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        passSetupParcellModel(parcelleDrafted)
    }

    fun getSetupParcelleModel(
        prodModel: ParcelleModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<ParcelleModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_parcelle)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupParcellModel(
        prodModel: ParcelleModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_parcelle)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
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

        setOtherListener()
//        setupTyprDeclarationSelection()
//        setupLocaliteSelection()

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
        }else{
            setAllListener()
        }
    }

    fun setOmbrageParcelleRV() {
        try {
            arbrOmbrListParcelle = mutableListOf<OmbrageVarieteModel>()
            arbreOmbrParcelleAdapter = OmbrageAdapter(arbrOmbrListParcelle)
            recyclerArbrOmbrListParcel.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerArbrOmbrListParcel.adapter = arbreOmbrParcelleAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        clickAddArbreOmbrParcel.setOnClickListener {
            try {
                if (selectArbrOmbrParcel.selectedItem.toString()
                        .isEmpty() || editQtArbrOmbrParcel.text.toString().isEmpty()
                ) {
                    Commons.showMessage("Renseignez des données d'ombrage, svp !", this, callback = {})
                    return@setOnClickListener
                }

                val ombrageVariete = OmbrageVarieteModel(
                    0,
                    selectArbrOmbrParcel.selectedItem.toString(),
                    editQtArbrOmbrParcel.text.toString().trim()
                )
                addOmbrageVariete(ombrageVariete)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

    }

    fun addOmbrageVariete(ombrageVarieteModel: OmbrageVarieteModel) {
        try {
            if (ombrageVarieteModel.variete?.length == 0) return

            arbrOmbrListParcelle?.forEach {
                if (it.variete?.uppercase() == ombrageVarieteModel.variete?.uppercase() && it.nombre == ombrageVarieteModel.nombre) {
                    ToastUtils.showShort("Cette variété est deja ajoutée")
                    return
                }
            }

            arbrOmbrListParcelle?.add(ombrageVarieteModel)
            arbreOmbrParcelleAdapter?.notifyDataSetChanged()

            editQtArbrOmbrParcel.text?.clear()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }




    private fun setOtherListener() {
        setupTyprDeclarationSelection()

        setOmbrageParcelleRV()
    }

    private fun setAllListener() {

        setupSectionSelection()

        Commons.setListenerForSpinner(this,
            "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = spinnerVarieteParcelle,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(
                21,
                this
            ) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreVarieteProducteur.visibility = visibility
//                }
            })

        Commons.setListenerForSpinner(this,
            "La parcelle est-elle régénérée ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectParcRegenParcelle,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNumAnnRegenParcelle.visibility = visibility
                    containerSuperfConcernee.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Quel type de document ?","La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectDocumentParcelle,
            listIem = (AssetFileHelper.getListDataFromAsset(
                10,
                this
            ) as MutableList<TypeDocumentModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreVarieteProducteur.visibility = visibility
//                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il un plan d'eau ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectPresentCourEauParcelle,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerCourEauParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il des mesures de protection ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectMesurProtectParcelle,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerSelectProtection.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il une pente ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectYaPenteParcelle,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNiveauPente.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Choix de l'arbre ?","La liste des arbres d'ombrage semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectArbrOmbrParcel,
            listIem = (AssetFileHelper.getListDataFromAsset(
                25,
                this
            ) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

    }
}
