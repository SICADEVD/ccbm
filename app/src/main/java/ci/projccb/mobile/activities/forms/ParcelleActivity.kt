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
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner
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
import ci.projccb.mobile.repositories.datas.ArbreData
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.getSpinnerContent
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.showYearPickerDialog
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_parcelle.*
import kotlinx.android.synthetic.main.activity_producteur.clickCancelProducteur
import kotlinx.android.synthetic.main.activity_producteur.containerAutreCertifProducteur
import kotlinx.android.synthetic.main.activity_producteur.editAnneeCertificationProducteur
import kotlinx.android.synthetic.main.activity_producteur.selectCertifProducteur
import kotlinx.android.synthetic.main.activity_producteur.selectLocaliteProducteur
import kotlinx.android.synthetic.main.activity_producteur_menage.selectProducteurMenage
import kotlinx.android.synthetic.main.activity_producteur_menage.selectSectionProducteurMenage
import kotlinx.android.synthetic.main.activity_suivi_parcelle.containerArbreAgroSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectAgroForesterieSParcelle

import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectInsecteParOuRavSuivi
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
            getString(R.string.choix_de_la_section),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
            getString(R.string.choix_de_la_localit),
            getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectLocaliteParcelle,
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
                if(it.id == 0){
                    if (it.uid == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
                } else {
                    if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurParcelle,
            listIem = producteursList?.map { "${ it.nom } ${ it.prenoms }" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid

                    //setupPa
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



//    fun setupTyprDeclarationSelection() {
//        selectDeclarationTypeParcelle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                typeDeclaration = resources.getStringArray(R.array.declarationType)[position]
//                disableField(typeDeclaration)
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//
//            }
//        }
//    }



    fun collectDatas() {

//        if (producteurId.isEmpty()) {
//            showMessage(
//                "Choisissez un producteur svp.",
//                this,
//                finished = false,
//                callback = {},
//                positive = getString(R.string.compris),
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
//                positive = getString(R.string.ok),
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
//                positive = getString(R.string.ok),
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
//                positive = getString(R.string.ok),
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

                //mappingPoints = wayPoints
                protectionStr = GsonUtils.toJson(selectProtectionParcelle.selectedStrings)
                arbreStr = GsonUtils.toJson((recyclerArbrOmbrListParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { ArbreData(null, it.uid.toString(), it.nombre) })
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

        //setupTyprDeclarationSelection()

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


    fun disableField(typeDeclaration: String = "") {
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
        val itemModelOb = getParcellObjet(false)

        if(itemModelOb == null) return

        val parcelleDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()
                isSynced = false
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                origin = "local"

                protectionStr = GsonUtils.toJson(selectProtectionParcelle.selectedStrings)
                arbreStr = GsonUtils.toJson((recyclerArbrOmbrListParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { ArbreData(null, it.uid.toString(), it.nombre) })
                wayPointsString =  ApiClient.gson.toJson(wayPoints)
            }
        }

        showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
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
                    message = getString(R.string.contenu_ajout_aux_brouillons),
                    context = this,
                    finished = true,
                    callback = {
                        Commons.playDraftSound(this)
                        imageDraftBtn.startAnimation(Commons.loadShakeAnimation(this))
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
//        provideStringSpinnerSelection(
//            selectDeclarationTypeParcelle,
//            parcelleDrafted.typedeclaration,
//            resources.getStringArray(R.array.declarationType)
//        )

        Commons.setListenerForSpinner(this,
            getString(R.string.type_de_d_claration_superficie),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectDeclarationTypeParcelle,
            itemChanged = arrayListOf(Pair(1, "Verbal"), Pair(2, "Gps")),
            currentVal = parcelleDrafted.typedeclaration,
            listIem = resources.getStringArray(R.array.declarationType)
                ?.toList() ?: listOf(),
            onChanged = {
                typeDeclaration = resources.getStringArray(R.array.declarationType)[it]
                disableField(typeDeclaration)
            },
            onSelected = { itemId, visibility ->
            })

        //editNomParcelle.setText(parcelleDrafted.culture)
//        editSuperficieParcelle.setText(parcelleDrafted.superficie)
//        editLatParcelle.setText(parcelleDrafted.latitude)
//        editLongParcelle.setText(parcelleDrafted.longitude)
        //editAnneParcelle.setText(parcelleDrafted.anneeCreation)

        setupSectionSelection(parcelleDrafted.section, parcelleDrafted.localite, parcelleDrafted.producteurId)

        setupMoyProtectMultiSelection(GsonUtils.fromJson(parcelleDrafted.protectionStr, object : TypeToken<MutableList<String>>() {}.type))

        Commons.setListenerForSpinner(this,
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
            getString(R.string.la_parcelle_est_elle_r_g_n_r_e),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectParcRegenParcelle,
            currentVal = parcelleDrafted.parcelleRegenerer,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.quel_type_de_document_poss_des_tu),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
            getString(R.string.d_finit_le_niveau_de_la_pente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectNiveauPente,
            listIem = resources.getStringArray(R.array.niveau_pente)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_un_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectPresentCourEauParcelle,
            currentVal = parcelleDrafted.presenceCourDeau,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.quel_est_le_cour_ou_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectCourEauParcelle,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(0, this) as MutableList<CourEauModel>).map { "${it.nom}" }?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerAutreCourDeau.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_mesures_de_protection),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMesurProtectParcelle,
            currentVal = parcelleDrafted.existeMesureProtection,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.y_a_t_il_une_pente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectYaPenteParcelle,
            currentVal = parcelleDrafted.existePente,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.y_a_t_il_des_pr_sences_d_rosion),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSignErosParcelle,
            currentVal = parcelleDrafted.erosion,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->

            })

        val listArbres = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_l_arbre),
            getString(R.string.la_liste_des_arbres_d_ombrage_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectArbrOmbrParcel,
            listIem = listArbres?.map { "${ it.nom+" |"} ${it.nomScientifique}" }
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

    fun setupMoyProtectMultiSelection(currentList : MutableList<String> = mutableListOf()) {
        val protectList = resources.getStringArray(R.array.mesure_protection)
        var listSelectProtectPosList = mutableListOf<Int>()
        var listSelectProtectList = mutableListOf<String>()

        var indItem = 0
        (protectList)?.forEach {
            if(currentList.size > 0){ if(currentList.contains(it)) listSelectProtectPosList.add(indItem) }
            indItem++
        }

        selectProtectionParcelle.setTitle(getString(R.string.s_lectionner_les_m_sures_de_protections))
        selectProtectionParcelle.setItems(protectList)
        //multiSelectSpinner.hasNoneOption(true)
        selectProtectionParcelle.setSelection(listSelectProtectPosList.toIntArray())
        selectProtectionParcelle.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
            override fun selectedIndices(indices: MutableList<Int>?) {
                listSelectProtectPosList.clear()
                listSelectProtectPosList.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                listSelectProtectList.clear()
                listSelectProtectList.addAll(strings?.toMutableList() ?: arrayListOf())
                if(listSelectProtectList.contains("Autre")) containerAutreProtectParcelle.visibility = View.VISIBLE else containerAutreProtectParcelle.visibility = View.GONE
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._8ssp),
            resources.getDimension(R.dimen._8ssp))

        parcelleDao = CcbRoomDatabase.getDatabase(this)?.parcelleDao()

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
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        clickSaveParcelle.setOnClickListener {
            collectDatas()
        }

        clickLatLongParcelle.setOnClickListener {
            editLatParcelle.setText("0.0")
            editLongParcelle.setText("-0.0")
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
       // setupTyprDeclarationSelection()
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

        val listArbres = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()

        try {
            arbrOmbrListParcelle = mutableListOf<OmbrageVarieteModel>()
            arbreOmbrParcelleAdapter = OmbrageAdapter(arbrOmbrListParcelle,
                getString(R.string.arbre), getString(R.string.nombre))
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
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_d_ombrage_svp), this, callback = {})
                    return@setOnClickListener
                }

                val arbreLibel = selectArbrOmbrParcel.getSpinnerContent().split("|")

                listArbres?.forEach {
                    if(it.nomScientifique?.contains(arbreLibel[1].trim(), ignoreCase = true) == true){
                        val ombrageVariete = OmbrageVarieteModel(
                            it.id?:0,
                            selectArbrOmbrParcel.getSpinnerContent().replace(" | ", "/").replace("| ", "/"),
                            editQtArbrOmbrParcel.text.toString().trim()
                        )
                        addOmbrageVariete(ombrageVariete)

                        editQtArbrOmbrParcel.text?.clear()
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

            arbrOmbrListParcelle?.forEach {
                if (it.variete?.uppercase() == ombrageVarieteModel.variete?.uppercase() && it.nombre == ombrageVarieteModel.nombre) {
                    ToastUtils.showShort(getString(R.string.cette_vari_t_est_deja_ajout_e))
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
        //setupTyprDeclarationSelection()

        setOmbrageParcelleRV()

//        .setOnClickListener {
//            datePickerDialog = null
//            val calendar: Calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//
//
//            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
//
//                editAnneeCreationParcelle.setText("$year")
//                //anneeCertification = editAnneeCreationParcelle.text?.toString()!!
//            }, year, month, dayOfMonth)
//
//            datePickerDialog!!.datePicker.minDate = DateTime.parse("01/01/1960", DateTimeFormat.forPattern("dd/MM/yyyy")).millis
//            datePickerDialog!!.datePicker.maxDate = DateTime.now().millis
//            datePickerDialog?.show()
//        }

        editNumAnneeRegenParcelle.setOnClickListener { showYearPickerDialog(editNumAnneeRegenParcelle) }
        editAnneeCreationParcelle.setOnClickListener { showYearPickerDialog(editAnneeCreationParcelle) }
    }

    private fun setAllListener() {

        setupSectionSelection()

        setupMoyProtectMultiSelection()

        Commons.setListenerForSpinner(this,
            getString(R.string.type_de_d_claration_superficie),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectDeclarationTypeParcelle,
            itemChanged = arrayListOf(Pair(1, "Verbal"), Pair(2, "Gps")),
            listIem = resources.getStringArray(R.array.declarationType)
                ?.toList() ?: listOf(),
            onChanged = {
                typeDeclaration = resources.getStringArray(R.array.declarationType)[it]
                disableField(typeDeclaration)
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
            getString(R.string.la_parcelle_est_elle_r_g_n_r_e),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectParcRegenParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.quel_type_de_document_poss_des_tu),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
            getString(R.string.y_a_t_il_un_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectPresentCourEauParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.quel_est_le_cour_ou_plan_d_eau),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectCourEauParcelle,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(0, this) as MutableList<CourEauModel>).map { "${it.nom}" }?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerAutreCourDeau.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_mesures_de_protection),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMesurProtectParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.y_a_t_il_une_pente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectYaPenteParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNiveauPente.visibility = visibility
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

        val listArbres = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_l_arbre), getString(R.string.la_liste_des_arbres_d_ombrage_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectArbrOmbrParcel,
            listIem = listArbres?.map { "${ it.nom+" |"} ${it.nomScientifique}" }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

    }
}
