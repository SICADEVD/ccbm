package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.LivraisonCentralPreviewActivity
import ci.projccb.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.projccb.mobile.adapters.LivraisonCentralSousModAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.adapters.OnlyFieldAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.configDate
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.Constants.CURRENT_PRICE_PER_QUANTITY
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_livraison.editAdressDestinataire
import kotlinx.android.synthetic.main.activity_livraison.editContactDestinataire
import kotlinx.android.synthetic.main.activity_livraison.editEmailDestinataire
import kotlinx.android.synthetic.main.activity_livraison.editNomDestinataire
import kotlinx.android.synthetic.main.activity_livraison_central.*
import kotlinx.android.synthetic.main.activity_parcelle.selectLocaliteParcelle
import kotlinx.android.synthetic.main.activity_parcelle.selectSectionParcelle
import kotlinx.android.synthetic.main.activity_producteur.selectProgramProducteur
import kotlinx.android.synthetic.main.activity_suivi_parcelle.linearAnimauxContainerSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerAnimauxSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerInsecteAmisSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectAnimauRencontSParcell
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectArbreSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectLocaliteSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectParcelleSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectProducteurSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectSectionSParcelle
import java.util.*

class LivraisonCentralActivity : AppCompatActivity() {


    companion object {
        const val TAG = "LivraisonCentralActivity.kt"
    }


    private val entrepriseCommon: CommonData = CommonData()
    private val transporteurCommon: CommonData = CommonData()
    private val vehiculeCommon: CommonData = CommonData()
    private val remorqueCommon: CommonData = CommonData()

    private val parcelleCommon: CommonData = CommonData()
    private var producteurCommon: CommonData = CommonData()
    private var typeCommon: CommonData = CommonData()
    private val certificatCommon: CommonData = CommonData()
    private val quantityCommon: CommonData = CommonData()

    private val magasinSectionCommon: CommonData = CommonData()
    private val magasinCentralCommon: CommonData = CommonData()
    private var livraisonCentralDrafted: LivraisonCentralModel? = null
    private var isFirstDelegue: Boolean = true
    private var livraisonCentralSousModelAdapter: LivraisonCentralSousModAdapter? = null
    private val livraisonCentralSousModelList: MutableList<LivraisonCentralSousModel> = mutableListOf()
    private val livraisonVerMagCentralModelList: MutableList<LivraisonVerMagCentralModel> = mutableListOf()
    private var currentResult: Int = 0
    private var typeProducteurParcrelle: String? = ""
    private var staffId: String = ""
    private var staffNomPrenoms: String = ""
    private var staffList: MutableList<ConcernesModel>? = null
    var localiteDao: LocaliteDao? = null
    var campagneDao: CampagneDao? = null
    var producteurDao: ProducteurDao? = null
    var parcelleDao: ParcelleDao? = null
    var livraisonCentralDao: LivraisonCentralDao? = null
    //var delegueDao: DelegueDao? = null
    var concernesDao: ConcernesDao? = null
    var typeProduitDao: TypeProduitDao? = null
    var magasinDao: MagasinDao? = null

    var localitesList: MutableList<LocaliteModel>? = null
    //var deleguesList: MutableList<DelegueModel>? = null
    var cponcerneeList: MutableList<ConcernesModel>? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var campagnesList: MutableList<CampagneModel>? = mutableListOf()
    var parcellesList: MutableList<ParcelleModel>? = mutableListOf()

    var localiteSelected = ""
    var localiteIdSelected = ""
    var producteurNomPrenoms = ""
    var typeProduit = ""
    var producteurId = ""
    var delegueID = ""
    var delegueNom = ""
    var dateLivraisonCentral = ""

    var campagneNom = ""
    var campagneId = ""

    var magasinNom = ""
    var magasinId = ""

    var parcelleNom = ""
    var parcelleSuperficie = ""
    var parcelleId = ""

    var datePickerDialog: DatePickerDialog? = null
    var draftedDataLivraisonCentral: DataDraftedModel? = null


    fun setupLocaliteSelection() {
        localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()
        localitesList = localiteDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

        if (localitesList?.size == 0) {
            showMessage(
                "La liste des localités est vide ! Refaite une mise à jour.",
                this,
                finished = false,
                callback = {},
                "Compris !",
                false,
                showNo = false,
            )

            localiteIdSelected = ""
            localiteSelected = ""
            //selectLocaliteLivraisonCentral?.adapter = null
            return
        }
    }


    fun setupCampagneSelection() {
        campagneDao = CcbRoomDatabase.getDatabase(applicationContext)?.campagneDao()
        campagnesList = campagneDao?.getAll()

        val campagneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList!!)
//        selectCampagneLivraisonCentral!!.adapter = campagneAdapter
//
//        selectCampagneLivraisonCentral.setTitle("Choisir la campagne")
//
//        selectCampagneLivraisonCentral.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val campagne = campagnesList!![position]
//                campagneNom = campagne.campagnesNom!!
//                campagneId = campagne.id.toString()
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


//    fun setupConcerneeSelection() {
//        concernesDao = CcbRoomDatabase.getDatabase(applicationContext)?.concernesDao()
//        cponcerneeList = concernesDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//
//        val concernesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cponcerneeList!!)
//        selectDelegueLivraisonCentral!!.adapter = delegueAdapter
//
//        selectDelegueLivraisonCentral.setTitle("Choisir le délégué")
//
//        selectDelegueLivraisonCentral.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val delegue = deleguesList!![position]
//                delegueID = delegue.id.toString()
//                delegueNom = delegue.nom!!
//
//                setupMagasinSelection(delegueID.toInt())
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


    fun setupTypeProduitSelection() {
        //typeProduitDao = CcbRoomDatabase.getDatabase(applicationContext)?.typeProduitDao()
        val typeProduitssList = AssetFileHelper.getListDataFromAsset(12, this@LivraisonCentralActivity) as MutableList<TypeProduitModel>?
            //typeProduitDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        val tyeProduitAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, typeProduitssList!!)
//        selectTypeProduitLivraisonCentral!!.adapter = tyeProduitAdapter
//
//        selectTypeProduitLivraisonCentral.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val typeProduitData = typeProduitssList[position]
//                typeProduit = typeProduitData.nom ?: ""
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }

    fun setupMagSectSelection(currVal: String? = null, currVal2: String? = null){

        val magasinSecList = CcbRoomDatabase.getDatabase(applicationContext)?.magasinSectionDao()
            ?.getAll()

        Commons.setListenerForSpinner(this,
            "Sélectionner un magasin de section","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectMagSecListLivraiCentr,
            currentVal = magasinSecList?.filter { it.id.toString() == currVal }.let {
                  if(it?.size!! > 0) it.first().let { "${it?.nomMagasinsections}" } else null
            },
            listIem = magasinSecList?.map {
                "${it.nomMagasinsections}"
            }?.toList() ?: listOf(),
            onChanged = {
                val magasinSec = magasinSecList!![it]
                magasinSectionCommon.nom = "${magasinSec.nomMagasinsections}"
                magasinSectionCommon.id = magasinSec.id

                //if(!isFirstDelegue){
                CcbRoomDatabase.getDatabase(this)?.staffFormation()?.getStaffFormationById(magasinSec.staffId!!)?.let { staff ->
                    editNomExpediteur.setText("${staff.firstname} ${staff.lastname}")
                    editContactExpediteur.setText("${staff.mobile}")
                    editEmailExpediteur.setText("${staff.email}")
                    editAdressExpediteur.setText("${staff.adresse?:"Inconnu"}")
                }
                //rstDelegue = false

            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupMagCentralSelection(currVal: String? = null, currVal2: String? = null){

        val magasinCentralList = CcbRoomDatabase.getDatabase(applicationContext)?.magasinCentralDao()
            ?.getAll()

        LogUtils.d(magasinCentralList)

        Commons.setListenerForSpinner(this,
            "Sélectionner un magasin central","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectMagCentralLivraisonCentral,
            currentVal = magasinCentralList?.filter { it.id.toString() == currVal }.let {
                if(it?.size!! > 0) it.first().let { "${it?.nomMagasinsections}" } else null
            },
            listIem = magasinCentralList?.map {
                "${it.nomMagasinsections}"
            }?.toList() ?: listOf(),
            onChanged = {
                val magasin = magasinCentralList!![it]
                magasinCentralCommon.nom = "${magasin.nomMagasinsections}"
                magasinCentralCommon.id = magasin.id

                //if(!isFirstDelegue){
                CcbRoomDatabase.getDatabase(this)?.staffFormation()?.getStaffFormationById(magasin.staffId!!)?.let { staff ->
                    editNomDestinataire.setText("${staff.firstname} ${staff.lastname}")
                    editContactDestinataire.setText("${staff.mobile}")
                    editEmailDestinataire.setText("${staff.email}")
                    editAdressDestinataire.setText("${staff.adresse?:"Inconnu"}")
                }
                //rstDelegue = false

            },
            onSelected = { itemId, visibility ->

            })

    }


    fun setupProducteurSelection(pLocaliteId: String) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = pLocaliteId)
        val producteursDatas: MutableList<CommonData> = mutableListOf()
        producteursList?.map {
            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
        }?.let {
            producteursDatas.addAll(it)
        }

        val livraisonCentralDrafted = ApiClient.gson.fromJson(draftedDataLivraisonCentral?.datas, LivraisonCentralModel::class.java)
        //selectProducteurList.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

//        if (livraisonCentralDrafted != null) {
//            provideDatasSpinnerSelection(
//                selectStaffList,
//                livraisonCentralDrafted.producteurNom,
//                producteursDatas
//            )
//        }

//        selectProducteurList.setTitle("Choisir du producteur")
//        selectProducteurList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val producteur = producteursList!![position]
//                producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"
//
//                producteurId = if (producteur.isSynced) {
//                    producteur.id!!.toString()
//                } else {
//                    producteur.uid.toString()
//                }
//
//                if(!producteurId.isNullOrBlank()){
//                    editQuantity.isEnabled = true
//                }
//
//                setupParcellesProducteurSelection(producteurId)
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }

    fun setListener(){
//        editQuantity.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                val valueNum = s.toString()
//                val result = (valueNum.toInt())*CURRENT_PRICE_PER_QUANTITY
//                editResultatQuantity.setText("${result.toString()}")
//                currentResult = result
//            }
//        })
//
//        editReduction.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                val valueNum2 = s.toString()
//                if (!editResultatQuantity.text.isNullOrBlank() && !valueNum2.isNullOrBlank()){
//                    LogUtils.d("Cureent float : "+ (valueNum2.toDouble()/100).toDouble())
//                    var sousTotal = 0; var total = 0
//                    livraisonCentralSousModelList.map {
//                        total += it.amountNb ?: 0
//                    }
//                    val reduce = (total) - (total*(valueNum2.toDouble()/100).toDouble())
//                    tvSousTotal.text = "${reduce}"
//                    tvTotalReduce.setText("${total}")
//                }
//            }
//        })
    }

    private fun setProgrammeSpinner(currVal2:String? = null) {

        var programmesDao = CcbRoomDatabase.getDatabase(applicationContext)?.programmesDao();
        var programmeListi = programmesDao?.getAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal2?.let { idc ->
            programmeListi?.forEach {
                if(it.id == idc.toInt()) libItem = it.libelle
            }
        }

//        Commons.setListenerForSpinner(this,
//            "Choix du programme !",
//            "La liste des programmes semble vide, veuillez procéder à la synchronisation des données svp.",
//            isEmpty = if (programmeListi?.size!! > 0) false else true,
//            currentVal = libItem,
//            spinner = selectProgramLivraisonCentral,
//            listIem = programmeListi?.map { it.libelle }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//                val programme = programmeListi!![it]
//                programmeCommon.nom = programme.libelle!!
//                programmeCommon.id = programme.id!!
//
//            },
//            onSelected = { itemId, visibility ->
//                //if(itemId == 1) containerAutreProgramProducteur.visibility = visibility
//            })

    }



    fun setupProducteurSelection(id: Int, currVal2: String? = null, currVal3: String? = null) {
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
            spinner = selectProducLivraisonCentral,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    producteurCommon.id = producteur.id!!

                    setupParcelleSelection(producteurCommon.id.toString(), currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupParcelleSelection(producteurId: String?, currVal3: String? = null) {
        var parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
            ?.getParcellesProducteur(producteurId = producteurId.toString(), agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        LogUtils.json(parcellesList)
        var libItem: String? = null
        currVal3?.let { idc ->
            parcellesList?.forEach {
                if (it.id == idc.toInt()) libItem = "(${it.anneeCreation}) ${it.superficieConcerne} ha"
            }
        }

//        Commons.setListenerForSpinner(this,
//            "Choix de la parcelle !",
//            "La liste des parcelles semble vide, veuillez procéder à la synchronisation des données svp.",
//            isEmpty = if (parcellesList?.size!! > 0) false else true,
//            currentVal = libItem,
//            spinner = selectParcelleLivraisonCentral,
//            listIem = parcellesList?.map { "(${it.anneeCreation}) ${it.superficieConcerne} ha" }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//                parcellesList?.let { list ->
//                    var parcelle = list.get(it)
//                    parcelleCommon.nom = "(${parcelle.anneeCreation}) ${parcelle.superficieConcerne} ha"
//                    parcelleCommon.id = parcelle.id!!
//
//                    //setupParcelleSelection(parcelleCommon.id, currVal3)
//                }
//
//
//            },
//            onSelected = { itemId, visibility ->
//
//            })
    }

    fun setAllListener() {
        //setupSectionSelection()

        setupMagSectSelection()
        setupMagCentralSelection()

        val entrepList = CcbRoomDatabase.getDatabase(this)?.entrepriseDao()?.getAll()
        Commons.setListenerForSpinner(this,
            "Choix de l'entreprise","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectEntrepriseLivraisonCentral,
            listIem = entrepList?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {
                entrepriseCommon.nom = entrepList!![it].nom
                entrepriseCommon.id = entrepList[it].id
                setupTransporteurSelection(entrId = entrepList!![it].id.toString())
            },
            onSelected = { itemId, visibility ->
            })

        val vehiculeList = CcbRoomDatabase.getDatabase(this)?.vehiculeDao()?.getAll()
        Commons.setListenerForSpinner(this,
            "Choix du véhicule","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectVehiculeLivraisonCentral,
            listIem = vehiculeList?.map { "${it.marque_id} (${it.vehicule_immat})" }
                ?.toList() ?: listOf(),
            onChanged = {
                vehiculeCommon.nom = "${vehiculeList!![it].marque_id} (${vehiculeList!![it].vehicule_immat})"
                vehiculeCommon.id = vehiculeList[it].id
            },
            onSelected = { itemId, visibility ->
            })

        val remorqueList = CcbRoomDatabase.getDatabase(this)?.remorqueDao()?.getAll()
        Commons.setListenerForSpinner(this,
            "Choix de la remorque","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectRemorqueLivraisonCentral,
            listIem = remorqueList?.map { "Immatriculation: ${it.remorque_immat}" }
                ?.toList() ?: listOf(),
            onChanged = {
                  remorqueCommon.nom = "Immatriculation: "+remorqueList!![it].remorque_immat
                  remorqueCommon.id = remorqueList[it].id
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setupItemMultiSelection(this, selectTypProduitLivraisonCentral, "Désignez le type de produit ?", resources.getStringArray(R.array.type_produit)?.map { CommonData(0, it) }?.toMutableList()?: mutableListOf() ){
            typeCommon.nom = it.toModifString()
            livraisonVerMagCentralModelList.clear()
            it.forEach {produit ->
                LogUtils.d(produit, magasinSectionCommon.nom)
                livraisonVerMagCentralModelList.addAll(CcbRoomDatabase.getDatabase(this)?.livraisonVerMagCentralDao()?.getLivraisonByTypeProdAndMagSection(produit, magasinSectionCommon.nom)?.toMutableList()?: arrayListOf())
            }
            setupProducteurSelection()
        }


    }

    fun setupProducteurSelection() {
//        val producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
//            ?.getProducteursByLocalite(localite = id.toString())

//        var libItem: String? = null
//        currVal2?.let { idc ->
//            producteursList?.forEach {
//                if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
//            }
//        }

        Commons.setListenerForSpinner(this,
            "Choix du producteur !",
            "La liste des producteurs semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (livraisonVerMagCentralModelList?.size!! > 0) false else true,
            spinner = selectProducLivraisonCentral,
            listIem = livraisonVerMagCentralModelList?.map { "${it.nom!!} ${it.prenoms!!} - ${it.typeProduit}" }
                ?.toList() ?: listOf(),
            onChanged = {

                livraisonVerMagCentralModelList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!} - ${producteur.typeProduit}"
                    producteurCommon.id = producteur.id!!

                    setupProducteurView(producteur)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    private fun setupProducteurView(producteur: LivraisonVerMagCentralModel) {

        editCertificatLivraisonCentral.setText(if(producteur.typeProduit.equals("Ordinaire", ignoreCase = true) == false) producteur.certificat?:"" else "")
        editTypProduitLivraisonCentral.setText(producteur.typeProduit?:"")
        var qteEnStock = if(producteur.quantiteMagasinSection?.toInt()?:0 > 0) producteur.quantiteMagasinSection?.toInt()  else 0
        editQuantityLivraisonCentral.hint = qteEnStock.toString()
        editQuantityLivraisonCentral.setText(qteEnStock.toString()?:"")

    }

    fun setupTransporteurSelection(currVal: String? = null, entrId: String? = "0") {
        val transporteurList = CcbRoomDatabase.getDatabase(applicationContext)?.transporteurDao()
            ?.getListByEntrpriseId(entrId?.toInt() ?: 0)

        Commons.setListenerForSpinner(this,
            "Choix du transporteur !",
            "La liste des transporteurs semble vide, veuillez procéder à la synchronisation des données svp.",
            currentVal = currVal,
            spinner = selectTransporteurLivraisonCentral,
            listIem = transporteurList?.map { "${it.nom} ${it.prenoms}" }
                ?.toList() ?: listOf(),
            onChanged = {

                val transporteur = transporteurList!![it]
                transporteurCommon.nom = "${transporteur.nom} ${transporteur.prenoms}"
                transporteurCommon.id = transporteur.id!!

            },
            onSelected = { itemId, visibility ->

            })
    }


    fun collectDatas() {
        if (magasinSectionCommon.id.toString().isNullOrEmpty()) {
            showMessage(
                "Aucun magasin de section selectionné !",
                this,
                finished = false,
                callback = {},
                deconnec =    false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        if (magasinCentralCommon.id.toString().isNullOrEmpty()) {
            showMessage(
                "Aucun magasin central selectionné !",
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        if(livraisonCentralSousModelList.size == 0){
            showMessage(
                "Aucune information de livraisonCentral saisie !",
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        val itemModelOb = getLivraisonCentralObjet()

        if(itemModelOb == null) return

        val livraisonCentralModel = itemModelOb?.first.apply {
            this?.apply {
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                senderMagasin = magasinSectionCommon.id.toString()
                magasinCentral = magasinCentralCommon.id.toString()

                itemsStringify = GsonUtils.toJson(livraisonCentralSousModelList)
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {

            var valueMod = ""
            livraisonCentralSousModelList.forEach {
                valueMod += "Producteur: ${it.producteurIdName} | Type: ${it.typeproduit} | Certificat: ${it.typeproduit} | Qté: ${it.quantite}\n"
            }
            this.add(Pair("Information sur la livraison", valueMod) as Pair<String, String>)

        }.map { MapEntry(it.first, it.second) }

       try {
           val intentLivraisonCentralPreview = Intent(this, LivraisonCentralPreviewActivity::class.java)
           intentLivraisonCentralPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
           intentLivraisonCentralPreview.putExtra("preview", livraisonCentralModel)
           intentLivraisonCentralPreview.putExtra("draft_id", draftedDataLivraisonCentral?.uid)
           startActivity(intentLivraisonCentralPreview)
       } catch (ex: Exception) {
           ex.toString()
       }
    }


//    fun clearFields() {
//        setAllListener()
//
//        editDateLivraisonCentral.text = null
//        //clearInfoLivraisonCentralTable()
//
//        staffId = ""
//        magasinId = ""
//        producteurId = ""
//        parcelleId = ""
//
//        selectStaffList.setSelection(0)
//        selectParcelleLivraisonCentral.setSelection(0)
//        //selectLocaliteLivraisonCentral.setSelection(0)
//    }

    fun getLivraisonCentralObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<LivraisonCentralModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        val typeList = arrayListOf<String>()
        val producteur_idList = arrayListOf<String>()
        val producteurIdNameList = arrayListOf<String>()
        val producteursList = arrayListOf<String>()
        val parcelleList = arrayListOf<String>()
        val certificatList = arrayListOf<String>()
        val typeproduitList = arrayListOf<String>()

        livraisonCentralSousModelList.forEach {
            typeList.add("${it.type}")
            producteur_idList.add("${it.producteur_id}")
            producteurIdNameList.add("${it.producteurIdName}")
            producteursList.add("${it.producteurs}")
            parcelleList.add("${it.parcelle}")
            certificatList.add("${it.certificat}")
            typeproduitList.add("${it.typeproduit}")
        }


        var itemList = getSetupLivraisonCentralModel(LivraisonCentralModel(
            uid = 0,
            id = 0,
            cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID, 1).toString(),
            isSynced = false,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local",
        ), mutableListOf<Pair<String,String>>())

        itemList.first.apply {
            typeStr = ApiClient.gson.toJson(typeList)
            producteur_idStr = ApiClient.gson.toJson(producteur_idList)
            producteursStr = ApiClient.gson.toJson(producteursList)
            parcelleStr = ApiClient.gson.toJson(parcelleList)
            certificatStr = ApiClient.gson.toJson(certificatList)
            typeproduitStr = ApiClient.gson.toJson(typeproduitList)
            quantiteStr = ApiClient.gson.toJson(quantiteList)
        }

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

        for (field in allField){
            if(field.second.isNullOrBlank() && necessaryItem.contains(field.first)){
                message = "Le champ intitulé : `${field.first}` n'est pas renseigné !"
                isMissing = true
                isMissingDial2 = true
                break
            }
        }

        if(isMissing && (isMissingDial || isMissingDial2) ){
            Commons.showMessage(
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

    fun getSetupLivraisonCentralModel(
        prodModel: LivraisonCentralModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<LivraisonCentralModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_livraisonCentral)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupLivraisonCentralModel(
        prodModel: LivraisonCentralModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_livraisonCentral)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }

    fun draftLivraisonCentral(draftModel: DataDraftedModel?) {

        val itemModelOb = getLivraisonCentralObjet(false)

        if(itemModelOb == null) return

        val livraisonCentralModelDraft = itemModelOb?.first.apply {
            this?.apply {
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                senderMagasin = magasinSectionCommon.id.toString()
                magasinCentral = magasinCentralCommon.id.toString()

                itemsStringify = GsonUtils.toJson(livraisonCentralSousModelList)
            }
        }

        Commons.showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(livraisonCentralModelDraft),
                        typeDraft = "livraison_central",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                Commons.showMessage(
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
        livraisonCentralDrafted = ApiClient.gson.fromJson(draftedData.datas, LivraisonCentralModel::class.java)

        setupMagSectSelection(livraisonCentralDrafted?.senderMagasin)
        setupMagCentralSelection(livraisonCentralDrafted?.magasinCentral)

        val entrepList = CcbRoomDatabase.getDatabase(this)?.entrepriseDao()?.getAll()
        Commons.setListenerForSpinner(this,
            "Choix de l'entreprise","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectEntrepriseLivraisonCentral,
            currentVal = entrepList?.filter { it.id.toString() == livraisonCentralDrafted?.entreprise_id }?.let {
                if(it.size > 0) it.first().nom else null
            },
            listIem = entrepList?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {
                entrepriseCommon.nom = entrepList!![it].nom
                entrepriseCommon.id = entrepList[it].id
                setupTransporteurSelection(livraisonCentralDrafted?.sender_transporteur, entrId = entrepList!![it].id.toString())
            },
            onSelected = { itemId, visibility ->
            })

        val vehiculeList = CcbRoomDatabase.getDatabase(this)?.vehiculeDao()?.getAll()
        Commons.setListenerForSpinner(this,
            "Choix du véhicule","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectVehiculeLivraisonCentral,
            currentVal = vehiculeList?.filter { it.id.toString() == livraisonCentralDrafted?.sender_vehicule }?.let {
                if(it.size > 0) "${it.first().marque_id} (${it.first().vehicule_immat})" else null
            },
            listIem = vehiculeList?.map { "${it.marque_id} (${it.vehicule_immat})" }
                ?.toList() ?: listOf(),
            onChanged = {
                vehiculeCommon.nom = "${vehiculeList!![it].marque_id} (${vehiculeList!![it].vehicule_immat})"
                vehiculeCommon.id = vehiculeList[it].id
            },
            onSelected = { itemId, visibility ->
            })

        val remorqueList = CcbRoomDatabase.getDatabase(this)?.remorqueDao()?.getAll()
        Commons.setListenerForSpinner(this,
            "Choix de la remorque","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectRemorqueLivraisonCentral,
            currentVal = remorqueList?.filter { it.id.toString() == livraisonCentralDrafted?.sender_remorque }?.let {
                if(it.size > 0) "Immatriculation: ${it.first().remorque_immat}" else null
            },
            listIem = remorqueList?.map { "Immatriculation: ${it.remorque_immat}" }
                ?.toList() ?: listOf(),
            onChanged = {
                remorqueCommon.nom = "Immatriculation: "+remorqueList!![it].remorque_immat
                remorqueCommon.id = remorqueList[it].id
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setupItemMultiSelection(this, selectTypProduitLivraisonCentral, "Désignez le type de produit ?", resources.getStringArray(R.array.type_produit)?.map { CommonData(0, it) }?.toMutableList()?: mutableListOf() ){
            typeCommon.nom = it.toModifString()

            livraisonVerMagCentralModelList.clear()
            it.forEach {produit ->
                livraisonVerMagCentralModelList.addAll(CcbRoomDatabase.getDatabase(this)?.livraisonVerMagCentralDao()?.getLivraisonByTypeProdAndMagSection(produit, magasinSectionCommon.nom)?.toMutableList()?: arrayListOf())
            }
            setupProducteurSelection()
        }

        var itemList = GsonUtils.fromJson<MutableList<LivraisonCentralSousModel>>(livraisonCentralDrafted?.itemsStringify, object : TypeToken<MutableList<LivraisonCentralSousModel>>() {}.type)
        livraisonCentralSousModelList.addAll(itemList)
        livraisonCentralSousModelAdapter?.notifyDataSetChanged()

        passSetupLivraisonCentralModel(livraisonCentralDrafted)
        
    }

    fun setupTypeProducteurParcrelleSelection() {
        try {
//            selectTypeProducteurParcrelle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                    typeProducteurParcrelle = resources.getStringArray(R.array.type_produit)[position]
//                }
//
//                override fun onNothingSelected(arg0: AdapterView<*>) {
//                }
//            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livraison_central)
        livraisonCentralDao = CcbRoomDatabase.getDatabase(this)?.livraisonCentralDao()


        clickCancelLivraisonCentral.setOnClickListener {
            //clearFields()
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        clickSaveLivraisonCentral.setOnClickListener {
            collectDatas()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageDraftBtn.setOnClickListener {
            draftLivraisonCentral(draftedDataLivraisonCentral ?: DataDraftedModel(uid = 0))
        }

        setOtherListener()

        if (intent.getStringExtra("from") != null) {
            draftedDataLivraisonCentral = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataLivraisonCentral!!)
        }else{
            setAllListener()
        }
    }

    private fun setOtherListener() {
        editDateLivraisonCentral.setOnClickListener { configDate(editDateLivraisonCentral, false) }
        setListener()

        clickAddLivraisonInfo.setOnClickListener {
            try{
                if (producteurCommon.id.toString().isNullOrEmpty()
                    || editTypProduitLivraisonCentral.text.toString().isNullOrEmpty()
                    || editQuantityLivraisonCentral.text.isNullOrEmpty()
                    ) {
                    showMessage(
                        "Vous avez omis d'ajouté des données, svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "OK",
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                val livraisonCentralSousModel = LivraisonCentralSousModel(
                    type= typeCommon.nom.toString(),
                    producteur_id= producteurCommon.id.toString(),
                    producteurIdName= producteurCommon.nom.toString(),
                    producteurs= producteurCommon.id.toString(),
                    parcelle= parcelleCommon.id.toString(),
                    certificat = if(editTypProduitLivraisonCentral.text.toString().equals("Ordinaire", ignoreCase = true) == false) editCertificatLivraisonCentral.text.toString() else "",
                    typeproduit = editTypProduitLivraisonCentral.text.toString(),
                    quantite = editQuantityLivraisonCentral.text.toString(),

                )

                livraisonCentralSousModelList.add(livraisonCentralSousModel)
                livraisonCentralSousModelAdapter!!.notifyDataSetChanged()

                //clearInfoLivraisonCentralTable()
                producteurCommon = CommonData()
                typeCommon = CommonData()

            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        setupLivraisonCentralSousModRv()
    }

    fun setupLivraisonCentralSousModRv(){
        livraisonCentralSousModelAdapter = LivraisonCentralSousModAdapter(livraisonCentralSousModelList)
        recyclerInfoLivraisonCentral.adapter = livraisonCentralSousModelAdapter
        livraisonCentralSousModelAdapter!!.notifyDataSetChanged()
    }

}
