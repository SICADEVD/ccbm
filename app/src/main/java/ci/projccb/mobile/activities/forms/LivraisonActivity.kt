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
import ci.projccb.mobile.activities.infospresenters.LivraisonPreviewActivity
import ci.projccb.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.projccb.mobile.adapters.LivraisonSousModAdapter
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
import kotlinx.android.synthetic.main.activity_livraison.*
import kotlinx.android.synthetic.main.activity_parcelle.selectLocaliteParcelle
import kotlinx.android.synthetic.main.activity_parcelle.selectSectionParcelle
import kotlinx.android.synthetic.main.activity_producteur.selectProgramProducteur
import kotlinx.android.synthetic.main.activity_producteur_menage.clickCloseBtn
import kotlinx.android.synthetic.main.activity_suivi_parcelle.linearAnimauxContainerSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerAnimauxSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerInsecteAmisSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectAnimauRencontSParcell
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectLocaliteSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectParcelleSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectProducteurSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectSectionSParcelle
import java.util.*

class LivraisonActivity : AppCompatActivity() {


    companion object {
        const val TAG = "LivraisonActivity.kt"
    }


    private val parcelleCommon: CommonData = CommonData()
    private val producteurCommon: CommonData = CommonData()
    private val sectionCommon: CommonData = CommonData()
    private val localiteCommon: CommonData = CommonData()
    private val senderStaffCommon: CommonData = CommonData()
    private val magasinSectionCommon: CommonData = CommonData()
    private var livraisonDrafted: LivraisonModel? = null
    private var isFirstDelegue: Boolean = true
    private var livraisonSousModelAdapter: LivraisonSousModAdapter? = null
    private val livraisonSousModelList: MutableList<LivraisonSousModel> = mutableListOf()
    private var currentResult: Int = 0
    private var typeProducteurParcrelle: String? = ""
    private var staffId: String = ""
    private var staffNomPrenoms: String = ""
    private var staffList: MutableList<ConcernesModel>? = null
    var localiteDao: LocaliteDao? = null
    var campagneDao: CampagneDao? = null
    var producteurDao: ProducteurDao? = null
    var parcelleDao: ParcelleDao? = null
    var livraisonDao: LivraisonDao? = null
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
    var dateLivraison = ""

    var campagneNom = ""
    var campagneId = ""

    var magasinNom = ""
    var magasinId = ""

    var parcelleNom = ""
    var parcelleSuperficie = ""
    var parcelleId = ""

    var datePickerDialog: DatePickerDialog? = null
    var draftedDataLivraison: DataDraftedModel? = null


    fun setupMagasinSelection(concernes: String, currVal: String? = null) {
        magasinDao = CcbRoomDatabase.getDatabase(this)?.magasinSectionDao()
        val magasinsList = magasinDao?.getAll()
        LogUtils.json(magasinsList)

        Commons.setListenerForSpinner(this,
            getString(R.string.livraison_text),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMagasinSectionLivraison,
            currentVal = magasinsList?.filter { it.id.toString() == currVal }.let {
                if(it?.size!! > 0) it.first().let { "${it?.nomMagasinsections}" } else null
            },
            listIem = magasinsList?.map { it.nomMagasinsections }
                ?.toList() ?: listOf(),
            onChanged = {

                val magasin = magasinsList!![it]
                magasinSectionCommon.nom = magasin.nomMagasinsections!!
                magasinSectionCommon.id = magasin.id
                LogUtils.d(magasin.staffId.toString())
                CcbRoomDatabase.getDatabase(this)?.staffFormation()?.getStaffFormationById(magasin.staffId!!)?.let { staff ->
                    editNomDestinataire.setText("${staff.firstname} ${staff.lastname}")
                    editContactDestinataire.setText("${staff.mobile}")
                    editEmailDestinataire.setText("${staff.email}")
                    editAdressDestinataire.setText("${staff.adresse?:getString(R.string.inconnu)}")
                }
//
//                editNomDestinataire.setText("${magasin.nomMagasinsections}")
//                editContactDestinataire.setText("${magasin.phone}")
//                editEmailDestinataire.setText("${magasin.email}")
//                editAdressDestinataire.setText("${magasin.adresse?:getString(R.string.inconnu)}")

            },
            onSelected = { itemId, visibility ->
            })
    }


    fun setupLocaliteSelection() {
        localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()
        localitesList = localiteDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

        if (localitesList?.size == 0) {
            showMessage(
                getString(R.string.la_liste_des_localit_s_est_vide_refaite_une_mise_jour),
                this,
                finished = false,
                callback = {},
                getString(R.string.compris),
                false,
                showNo = false,
            )

            localiteIdSelected = ""
            localiteSelected = ""
            //selectLocaliteLivraison?.adapter = null
            return
        }
    }


    fun setupCampagneSelection() {
        campagneDao = CcbRoomDatabase.getDatabase(applicationContext)?.campagneDao()
        campagnesList = campagneDao?.getAll()

        val campagneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList!!)
//        selectCampagneLivraison!!.adapter = campagneAdapter
//
//        selectCampagneLivraison.setTitle("Choisir la campagne")
//
//        selectCampagneLivraison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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


    fun setupParcellesProducteurSelection(producteurId: String?) {
        parcelleDao = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
        parcellesList = parcelleDao?.getParcellesProducteur(producteurId = producteurId, agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        val parcellesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parcellesList?.map { Commons.getParcelleNotSyncLibel(it) }!!)
        selectParcelleLivraison!!.adapter = parcellesAdapter

        selectParcelleLivraison.setTitle("Choisir la parcelle")
        selectParcelleLivraison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val parcelle = parcellesList!![position]

                parcelleNom = Commons.getParcelleNotSyncLibel(parcelle).toString()
                parcelleSuperficie = parcelle.superficie!!

                if (parcelle.isSynced) {
                    parcelleId = parcelle.id.toString()
                } else {
                    parcelleId = parcelle.uid.toString()
                }

                setupTypeProducteurParcrelleSelection()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

    }


//    fun setupConcerneeSelection() {
//        concernesDao = CcbRoomDatabase.getDatabase(applicationContext)?.concernesDao()
//        cponcerneeList = concernesDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//
//        val concernesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cponcerneeList!!)
//        selectDelegueLivraison!!.adapter = delegueAdapter
//
//        selectDelegueLivraison.setTitle("Choisir le délégué")
//
//        selectDelegueLivraison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        val typeProduitssList = AssetFileHelper.getListDataFromAsset(12, this@LivraisonActivity) as MutableList<TypeProduitModel>?
            //typeProduitDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        val tyeProduitAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, typeProduitssList!!)
//        selectTypeProduitLivraison!!.adapter = tyeProduitAdapter
//
//        selectTypeProduitLivraison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val typeProduitData = typeProduitssList[position]
//                typeProduit = typeProduitData.nom ?: ""
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }

    fun setupStaffSelection(currVal: String? = null, currVal2: String? = null){

        val staffList = CcbRoomDatabase.getDatabase(applicationContext)?.delegueDao()
            ?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

//        val concernessDatas: MutableList<CommonData> = mutableListOf()
//        staffList?.map {
//            CommonData(id = it.id, nom = "${it.firstname} ${it.lastname}")
//        }?.let {
//            concernessDatas.addAll(it)
//        }

        Commons.setListenerForSpinner(this,
            getString(R.string.livraison_text2),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectStaffList,
            currentVal = staffList?.filter { it.id.toString() == currVal }.let {
                  if(it?.size!! > 0) it.first().let { "${it?.nom}" } else null
            },
            listIem = staffList?.map {
                "${it.nom}"
            }?.toList() ?: listOf(),
            onChanged = {
                val staff = staffList!![it]
                senderStaffCommon.nom = "${staff.nom}"
                senderStaffCommon.id = staff.id

                //if(!isFirstDelegue){
                editNomExpediteur.setText("${staff.nom}")
                editContactExpediteur.setText("${staff.mobile}")
                editEmailExpediteur.setText("${staff.email}")
                editAdressExpediteur.setText("${staff.adresse?:getString(R.string.inconnu)}")
                //rstDelegue = false

                setupMagasinSelection(staffId, currVal2)

            },
            onSelected = { itemId, visibility ->

            })

    }


    fun setupProducteurSelection(pLocaliteId: String) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = pLocaliteId)
        val producteursDatas: MutableList<CommonData> = mutableListOf()
        producteursList?.map {
            if(it.isSynced){
                CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
            }else CommonData(id = it.uid, nom = "${it.nom} ${it.prenoms}")
        }?.let {
            producteursDatas.addAll(it)
        }

        val livraisonDrafted = ApiClient.gson.fromJson(draftedDataLivraison?.datas, LivraisonModel::class.java)
        //selectProducteurList.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

        if (livraisonDrafted != null) {
            provideDatasSpinnerSelection(
                selectStaffList,
                livraisonDrafted.producteurNom,
                producteursDatas
            )
        }

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
//                    livraisonSousModelList.map {
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
//            getString(R.string.choix_du_programme),
//            "La liste des programmes semble vide, veuillez procéder à la synchronisation des données svp.",
//            isEmpty = if (programmeListi?.size!! > 0) false else true,
//            currentVal = libItem,
//            spinner = selectProgramLivraison,
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

    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {
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
            getString(R.string.dans_quel_section_livrez_vous),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = libItem ,
            spinner = selectSectionLivraison,
            listIem = sectionList?.map { it.libelle }
                ?.toList() ?: listOf(),
            onChanged = {

                val section = sectionList!![it]
                //ogUtils.d(section)
                sectionCommon.nom = section.libelle!!
                sectionCommon.id = section.id!!

                setLocaliteSpinner(sectionCommon.id!!, currVal1, currVal2, currVal3)

            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setLocaliteSpinner(id: Int, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {

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
            "De quelle localité s'agit-il ?",
            getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectLocaliteLivraison,
            listIem = localitesListi?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                localitesListi?.let { list ->
                    var localite = list.get(it)
                    localiteCommon.nom = localite.nom!!
                    localiteCommon.id = localite.id!!

                    setupProducteurSelection(localiteCommon.id!!, currVal2, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupProducteurSelection(id: Int, currVal2: String? = null, currVal3: String? = null) {
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
            spinner = selectProducLivraison,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid

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
                if (it.id == idc.toInt()) libItem = Commons.getParcelleNotSyncLibel(it)
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_parcelle),
            getString(R.string.la_liste_des_parcelles_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectParcelleLivraison,
            listIem = parcellesList?.map { "${it.codeParc}" }
                ?.toList() ?: listOf(),
            onChanged = {

                parcellesList?.let { list ->
                    var parcelle = list.get(it)
                    parcelleCommon.nom = "${parcelle.codeParc}"
                    parcelleCommon.id = parcelle.id!!

                    //setupParcelleSelection(parcelleCommon.id, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })
    }

    fun setAllListener() {
        setupSectionSelection()

        setupStaffSelection()

        //setProgrammeSpinner()
        //setupConcerneeSelection()
        //setupCampagneSelection()
        //setupTypeProduitSelection()
        //For RecycleView
        Commons.setListenerForSpinner(this,
            getString(R.string.livraison_text3),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTypeLivraison,
            itemChanged = arrayListOf(Pair(1, "Certifie")),
            listIem = resources.getStringArray(R.array.type_produit)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerTypeCertifLivraison.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.livraison_text4),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTypeCertifLivraison,
            listIem = (AssetFileHelper.getListDataFromAsset(20, this@LivraisonActivity) as MutableList<CommonData>)?.map { it.nom }.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        setupLivraisonSousModRv()
    }


    fun collectDatas() {
        if (senderStaffCommon.id.toString().isNullOrEmpty()) {
            showMessage(
                getString(R.string.aucun_d_l_gu_ou_staff_selectionn),
                this,
                finished = false,
                callback = {},
                deconnec =    false,
                positive = getString(R.string.compris),
                showNo = false
            )
            return
        }

        if (magasinSectionCommon.id.toString().isNullOrEmpty()) {
            showMessage(
                getString(R.string.aucun_magasin_selectionn),
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = getString(R.string.compris),
                showNo = false
            )
            return
        }

        if(livraisonSousModelList.size == 0){
            showMessage(
                getString(R.string.aucune_information_de_livraison_saisie),
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = getString(R.string.compris),
                showNo = false
            )
            return
        }

        val itemModelOb = getLivraisonObjet()

        if(itemModelOb == null) return

        val livraisonModel = itemModelOb?.first.apply {
            this?.apply {
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                senderStaff = senderStaffCommon.id.toString()
                magasinSection = magasinSectionCommon.id.toString()

                itemsStringify = GsonUtils.toJson(livraisonSousModelList)
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
//            this.add(Pair("Arbre d'ombrage", (recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            var valueMod = ""
            livraisonSousModelList.forEach {
                valueMod += "${it.producteurIdName} | ${it.parcelleIdName} | ${it.typeName} | ${it.certificat} | ${it.quantityNb}\n"
            }
            this.add(Pair(getString(R.string.les_produits_livr_s), valueMod) as Pair<String, String>)
        }.map { MapEntry(it.first, it.second) }

       try {
           val intentLivraisonPreview = Intent(this, LivraisonPreviewActivity::class.java)
           intentLivraisonPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
           intentLivraisonPreview.putExtra("preview", livraisonModel)
           intentLivraisonPreview.putExtra("draft_id", draftedDataLivraison?.uid)
           startActivity(intentLivraisonPreview)
       } catch (ex: Exception) {
           ex.toString()
       }
    }


    fun clearFields() {
        setAllListener()

        editDateLivraison.text = null
        //clearInfoLivraisonTable()

        staffId = ""
        magasinId = ""
        producteurId = ""
        parcelleId = ""

        selectStaffList.setSelection(0)
        selectParcelleLivraison.setSelection(0)
        //selectLocaliteLivraison.setSelection(0)
    }

    fun getLivraisonObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<LivraisonModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        val listOflivraisonSousModelProdName = arrayListOf<String>()
        val listOflivraisonSousModelProdId = arrayListOf<String>()
        val listOflivraisonSousModelParcelle = arrayListOf<String>()
        val listOflivraisonSousModelParcelleId = arrayListOf<String>()
        val listOflivraisonSousModelType = arrayListOf<String>()
        val listOflivraisonSousModelCertificat = arrayListOf<String>()
        val listOflivraisonSousModelQuantity = arrayListOf<String>()
        val listOflivraisonSousModelAmount = arrayListOf<String>()
        val listOflivraisonSousModelScelle = arrayListOf<String>()

        livraisonSousModelList.forEach {
            listOflivraisonSousModelProdName.add("${it.producteurIdName}")
            listOflivraisonSousModelProdId.add("${it.producteurId}")
            listOflivraisonSousModelParcelle.add("${it.parcelleIdName}")
            listOflivraisonSousModelParcelleId.add("${it.parcelleId}")
            listOflivraisonSousModelType.add("${it.typeName}")
            listOflivraisonSousModelCertificat.add("${it.certificat}")
            listOflivraisonSousModelQuantity.add("${it.quantityNb}")
            listOflivraisonSousModelAmount.add("${it.amountNb}")
            listOflivraisonSousModelScelle.add("${it.numScelle}")

        }


        var itemList = getSetupLivraisonModel(LivraisonModel(
            uid = 0,
            id = 0,
            cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID, 1).toString(),
            isSynced = false,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local",
        ), mutableListOf<Pair<String,String>>())

        itemList.first.apply {
            livraisonSousModelProdNamesStringify = ApiClient.gson.toJson(listOflivraisonSousModelProdName)
            livraisonSousModelProdIdsStringify = ApiClient.gson.toJson(listOflivraisonSousModelProdId)
            livraisonSousModelParcellesStringify = ApiClient.gson.toJson(listOflivraisonSousModelParcelle)
            livraisonSousModelParcelleIdsStringify = ApiClient.gson.toJson(listOflivraisonSousModelParcelleId)
            livraisonSousModelTypesStringify = ApiClient.gson.toJson(listOflivraisonSousModelType)
            livraisonSousModelCertifStringify = ApiClient.gson.toJson(listOflivraisonSousModelCertificat)
            livraisonSousModelQuantitysStringify = ApiClient.gson.toJson(listOflivraisonSousModelQuantity)
            livraisonSousModelAmountsStringify = ApiClient.gson.toJson(listOflivraisonSousModelAmount)
            livraisonSousModelScellesStringify = ApiClient.gson.toJson(listOflivraisonSousModelScelle)
        }

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
            Commons.showMessage(
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

    fun getSetupLivraisonModel(
        prodModel: LivraisonModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<LivraisonModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_livraison)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupLivraisonModel(
        prodModel: LivraisonModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_livraison)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }

    fun draftLivraison(draftModel: DataDraftedModel?) {

        val itemModelOb = getLivraisonObjet(false)

        if(itemModelOb == null) return

        val livraisonModelDraft = itemModelOb?.first.apply {
            this?.apply {
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                senderStaff = senderStaffCommon.id.toString()
                magasinSection = magasinSectionCommon.id.toString()

                itemsStringify = GsonUtils.toJson(livraisonSousModelList)
            }
        }

        Commons.showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(livraisonModelDraft),
                        typeDraft = "livraison",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                Commons.showMessage(
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
        livraisonDrafted = ApiClient.gson.fromJson(draftedData.datas, LivraisonModel::class.java)

        Commons.setListenerForSpinner(this,
            getString(R.string.livraison_text5),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTypeLivraison,
            itemChanged = arrayListOf(Pair(1, "Certifie")),
            listIem = resources.getStringArray(R.array.type_produit)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerTypeCertifLivraison.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.livraison_text6),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTypeCertifLivraison,
            listIem = (AssetFileHelper.getListDataFromAsset(20, this@LivraisonActivity) as MutableList<CommonData>)?.map { it.nom }.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        var countero = 0;
        livraisonDrafted?.let {
            setupSectionSelection()

            setupStaffSelection(livraisonDrafted?.senderStaff, livraisonDrafted?.magasinSection)

            setupLivraisonSousModRv()

            var itemList = GsonUtils.fromJson<MutableList<LivraisonSousModel>>(livraisonDrafted?.itemsStringify, object : TypeToken<MutableList<LivraisonSousModel>>() {}.type)
            livraisonSousModelList.addAll(itemList)
            livraisonSousModelAdapter?.notifyDataSetChanged()

            true
        }

        passSetupLivraisonModel(livraisonDrafted)
        
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
        setContentView(R.layout.activity_livraison)
        livraisonDao = CcbRoomDatabase.getDatabase(this)?.livraisonDao()


        clickCancelLivraison.setOnClickListener {
            //clearFields()
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        clickSaveLivraison.setOnClickListener {
            collectDatas()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageDraftBtn.setOnClickListener {
            draftLivraison(draftedDataLivraison ?: DataDraftedModel(uid = 0))
        }

        setOtherListener()

        if (intent.getStringExtra("from") != null) {
            draftedDataLivraison = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataLivraison!!)
        }else{
            setAllListener()
        }
    }

    private fun setOtherListener() {
        editDateLivraison.setOnClickListener { configDate(editDateLivraison, false) }
        setListener()

        clickAddLivraisonInfo.setOnClickListener {
            try{
                if (producteurCommon.id.toString().isNullOrEmpty()) {
                    showMessage(
                        getString(R.string.selectionnez_le_producteur_svp),
                        context = this,
                        finished = false,
                        callback = {},
                        positive = getString(R.string.ok),
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                if(parcelleCommon.id.toString().isEmpty()){
                    showMessage(
                        getString(R.string.selectionnez_une_parcelle_svp),
                        context = this,
                        finished = false,
                        callback = {},
                        positive = getString(R.string.ok),
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                if(editQuantity.text.toString().isEmpty()){
                    showMessage(
                        getString(R.string.passer_la_quantit_svp),
                        context = this,
                        finished = false,
                        callback = {},
                        positive = getString(R.string.ok),
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                val livraisonSousModel = LivraisonSousModel(
                    producteurId= producteurCommon.id.toString(),
                    producteurIdName= producteurCommon.nom.toString(),
                    parcelleId= parcelleCommon.id.toString(),
                    parcelleIdName = parcelleCommon.nom.toString(),
                    typeName = selectTypeLivraison.selectedItem.toString(),
                    certificat =  if(selectTypeLivraison.selectedItem.toString().equals("Ordinaire", ignoreCase = true) == false) selectTypeCertifLivraison.selectedItem.toString() else "",
                    quantityNb = editQuantity.text.toString().toInt(),
                    //numScelle = editNumScelle.text.toString()
                )

                livraisonSousModelList.add(livraisonSousModel)
                livraisonSousModelAdapter!!.notifyDataSetChanged()

                //clearInfoLivraisonTable()

            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }

    fun setupLivraisonSousModRv(){
        livraisonSousModelAdapter = LivraisonSousModAdapter(livraisonSousModelList)
        recyclerInfoLivraison.adapter = livraisonSousModelAdapter
        livraisonSousModelAdapter!!.notifyDataSetChanged()
    }

    private fun clearInfoLivraisonTable() {
        editQuantity.setText("")
        editResultatQuantity.setText("")
        //editNumScelle.setText("")
    }
}
