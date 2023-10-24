package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.LivraisonPreviewActivity
import ci.projccb.mobile.adapters.LivraisonSousModAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.Constants.CURRENT_PRICE_PER_QUANTITY
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_livraison.*
import kotlinx.android.synthetic.main.activity_producteur_menage.clickCloseBtn
import java.util.*

class LivraisonActivity : AppCompatActivity() {


    companion object {
        const val TAG = "LivraisonActivity.kt"
    }


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
    var magasinsList: MutableList<MagasinModel>? = mutableListOf()
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


    fun setupMagasinSelection(concernes: String) {
        magasinDao = CcbRoomDatabase.getDatabase(applicationContext)?.magasinSectionDao()
        magasinsList = magasinDao?.getConcerneeMagasins(concernes.toInt())

        val magasinAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, magasinsList!!)
        selectMagasinSectionLivraison!!.adapter = magasinAdapter
        selectMagasinSectionLivraison.setTitle("Choisir le magasin")
        selectMagasinSectionLivraison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val magasin = magasinsList!![position]
                magasinNom = magasin.nomMagasinsections!!
                magasinId = magasin.id.toString()

                editNomDestinataire.setText("${magasin.nomMagasinsections}")
                editContactDestinataire.setText("${magasin.phone}")
                editEmailDestinataire.setText("${magasin.email}")
                editAdressDestinataire.setText("${magasin.adresse?:"Inconnu"}")

                LogUtils.e(Commons.TAG, "ID -> $magasinId")
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

        var unDraftedCounter = 0
        if(magasinsList != null){
            magasinsList!!.forEach { magasin ->
                if(livraisonDrafted?.magasinSectionId.toString().equals(magasin.id)){
                    selectMagasinSectionLivraison.setSelection( unDraftedCounter, true)
                }
                unDraftedCounter++
            }
        }
    }


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
            //selectLocaliteLivraison?.adapter = null
            return
        }

        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
        //selectLocaliteLivraison!!.adapter = localiteAdapter

        //selectLocaliteLivraison.setTitle("Choisir la localite")

//        selectLocaliteLivraison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val locality = localitesList!![position]
//                localiteSelected = locality.nom!!
//
//                localiteIdSelected = if (locality.isSynced) {
//                    locality.id!!.toString()
//                } else {
//                    locality.uid.toString()
//                }
//
//                setupProducteurSelection(localiteIdSelected)
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
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

        val parcellesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parcellesList!!)
        selectParcelleLivraison!!.adapter = parcellesAdapter

        selectParcelleLivraison.setTitle("Choisir la parcelle")
        selectParcelleLivraison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val parcelle = parcellesList!![position]

                parcelleNom = "${parcelle.culture?:Constants.VIDE} (${parcelle.anneeCreation?:Constants.VIDE})"
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

    fun setupStaffSelection(){

        staffList = CcbRoomDatabase.getDatabase(applicationContext)?.concernesDao()
            ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val concernessDatas: MutableList<CommonData> = mutableListOf()
        staffList?.map {
            CommonData(id = it.id, nom = "${it.firstname} ${it.lastname}")
        }?.let {
            concernessDatas.addAll(it)
        }

        val staffAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, concernessDatas!!)
        selectStaffList.adapter = staffAdapter

        selectStaffList.setTitle("Choisir le délégué")
        selectStaffList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val staff = staffList!![position]
                staffNomPrenoms = "${staff.firstname} ${staff.lastname}"
                staffId = staff.id!!.toString()

                if(!isFirstDelegue){
                    editNomExpediteur.setText("${staff.firstname} ${staff.lastname}")
                    editContactExpediteur.setText("${staff.mobile}")
                    editEmailExpediteur.setText("${staff.email}")
                    editAdressExpediteur.setText("${staff.adresse?:"Inconnu"}")
                }
                isFirstDelegue = false

                setupMagasinSelection(staffId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

    }


    fun setupProducteurSelection(pLocaliteId: String) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = pLocaliteId)
        val producteursDatas: MutableList<CommonData> = mutableListOf()
        producteursList?.map {
            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
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
        editQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val valueNum = s.toString()
                val result = (valueNum.toInt())*CURRENT_PRICE_PER_QUANTITY
                editResultatQuantity.setText("${result.toString()}")
                currentResult = result
            }
        })

        editReduction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val valueNum2 = s.toString()
                if (!editResultatQuantity.text.isNullOrBlank() && !valueNum2.isNullOrBlank()){
                    LogUtils.d("Cureent float : "+ (valueNum2.toDouble()/100).toDouble())
                    var sousTotal = 0; var total = 0
                    livraisonSousModelList.map {
                        total += it.amountNb ?: 0
                    }
                    val reduce = (total) - (total*(valueNum2.toDouble()/100).toDouble())
                    tvSousTotal.text = "${reduce}"
                    tvTotalReduce.setText("${total}")
                }
            }
        })
    }

    fun setAll() {
        setupLocaliteSelection()
        setupStaffSelection()
        //setupConcerneeSelection()
        //setupCampagneSelection()
        //setupTypeProduitSelection()
        //For RecycleView

        setupLivraisonSousModRv()
    }


    fun collectDatas() {
        if (staffId.isBlank()) {
            showMessage(
                "Aucun délégué ou staff selectionné !",
                this,
                finished = false,
                callback = {},
                deconnec =    false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        if (magasinId.isBlank()) {
            showMessage(
                "Aucun magasin selectionné !",
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        if(livraisonSousModelList.size == 0){
            showMessage(
                "Aucune information de livraison saisie !",
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        if( editNomExpediteur.text.isNullOrBlank() or editNomDestinataire.text.isNullOrBlank()){
            showMessage(
                "L'une des cases chargées d'entrer le nom est vide !",
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        if( editContactExpediteur.text.isNullOrBlank() or editContactDestinataire.text.isNullOrBlank()){
            showMessage(
                "L'une des cases chargées d'entrer le contact est vide !",
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        if( editEmailExpediteur.text.isNullOrBlank() or editEmailDestinataire.text.isNullOrBlank()){
            showMessage(
                "L'une des cases chargées d'entrer l'email est vide !",
                this,
                finished = false,
                callback = {},
                deconnec = false,
                positive = "Compris !",
                showNo = false
            )
            return
        }

        val livraisonModel = getLivraisonObjet()

       try {
           val intentLivraisonPreview = Intent(this, LivraisonPreviewActivity::class.java)
           intentLivraisonPreview.putExtra("preview", livraisonModel)
           intentLivraisonPreview.putExtra("draft_id", draftedDataLivraison?.uid)
           startActivity(intentLivraisonPreview)
       } catch (ex: Exception) {
           ex.toString()
       }
    }


    fun clearFields() {
        setAll()

        editDateLivraison.text = null
        clearInfoLivraisonTable()

        staffId = ""
        magasinId = ""
        producteurId = ""
        parcelleId = ""

        selectStaffList.setSelection(0)
        selectParcelleLivraison.setSelection(0)
        //selectLocaliteLivraison.setSelection(0)
    }

    fun getLivraisonObjet(): LivraisonModel {

        val listOflivraisonSousModelProdName = arrayListOf<String>()
        val listOflivraisonSousModelProdId = arrayListOf<String>()
        val listOflivraisonSousModelParcelle = arrayListOf<String>()
        val listOflivraisonSousModelParcelleId = arrayListOf<String>()
        val listOflivraisonSousModelType = arrayListOf<String>()
        val listOflivraisonSousModelQuantity = arrayListOf<String>()
        val listOflivraisonSousModelAmount = arrayListOf<String>()
        val listOflivraisonSousModelScelle = arrayListOf<String>()

        livraisonSousModelList.forEach {
            listOflivraisonSousModelProdName.add("${it.producteurIdName}")
            listOflivraisonSousModelProdId.add("${it.producteurId}")
            listOflivraisonSousModelParcelle.add("${it.parcelleIdName}")
            listOflivraisonSousModelParcelleId.add("${it.parcelleId}")
            listOflivraisonSousModelType.add("${it.typeName}")
            listOflivraisonSousModelQuantity.add("${it.quantityNb}")
            listOflivraisonSousModelAmount.add("${it.amountNb}")
            listOflivraisonSousModelScelle.add("${it.numScelle}")

        }

        val livraisonModel = LivraisonModel(
            uid = 0,    id = 0,
            cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID, 1).toString(),
            estimatDate = dateLivraison,
            paymentStatus = resources.getStringArray(R.array.statuPaiement).get(selectStatuPaiementList.selectedItemPosition),
            delegueId = staffId,
            producteursId = producteurId,
            isSynced = false,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local",
            delegueNom = staffNomPrenoms,
            senderStaff = staffId,
            senderName = editNomExpediteur.text.toString(),
            senderPhone = editContactExpediteur.text.toString(),
            senderEmail = editEmailExpediteur.text.toString(),
            senderAddress = editAdressExpediteur.text.toString(),
            receiverName = editNomDestinataire.text.toString(),
            receiverPhone = editContactDestinataire.text.toString(),
            receiverEmail = editEmailDestinataire.text.toString(),
            receiverAddress = editAdressDestinataire.text.toString(),
            magasinSectionId = magasinId.toString(),
            magasinSection = magasinNom.toString(),
            reduction = editReduction.text.toString(),
            sousTotalReduce = tvSousTotal.text.toString(),
            totalReduce = tvTotalReduce.text.toString(),
            livraisonSousModelProdNamesStringify = ApiClient.gson.toJson(listOflivraisonSousModelProdName),
            livraisonSousModelProdIdsStringify = ApiClient.gson.toJson(listOflivraisonSousModelProdId),
            livraisonSousModelParcellesStringify = ApiClient.gson.toJson(listOflivraisonSousModelParcelle),
            livraisonSousModelParcelleIdsStringify = ApiClient.gson.toJson(listOflivraisonSousModelParcelleId),
            livraisonSousModelTypesStringify = ApiClient.gson.toJson(listOflivraisonSousModelType),
            livraisonSousModelQuantitysStringify = ApiClient.gson.toJson(listOflivraisonSousModelQuantity),
            livraisonSousModelAmountsStringify = ApiClient.gson.toJson(listOflivraisonSousModelAmount),
            livraisonSousModelScellesStringify = ApiClient.gson.toJson(listOflivraisonSousModelScelle),
        )

        return livraisonModel
    }


    fun draftLivraison(draftModel: DataDraftedModel?) {

        val livraisonModelDraft = getLivraisonObjet()

        Commons.showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
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
        livraisonDrafted = ApiClient.gson.fromJson(draftedData.datas, LivraisonModel::class.java)
        var countero = 0;
        livraisonDrafted?.let {
            staffList!!.forEach { delegue ->
                if(delegue.id.toString().equals(it?.delegueId)){
                    selectStaffList.setSelection(countero , true)

                    editNomExpediteur.setText("${it?.senderName}")
                    editContactExpediteur.setText("${it?.senderPhone}")
                    editEmailExpediteur.setText("${it?.senderEmail}")
                    editAdressExpediteur.setText("${it?.senderAddress ?:"Inconnu"}")

                    setupMagasinSelection(it?.delegueId.toString())
                }
                countero++
            }

            countero = 0
            val listProdId: MutableList<String> = ApiClient.gson.fromJson(it.livraisonSousModelProdIdsStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type)
            val listParce: MutableList<String> = ApiClient.gson.fromJson(it.livraisonSousModelParcellesStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type)
            val listParcelId: MutableList<String> = ApiClient.gson.fromJson(it.livraisonSousModelParcelleIdsStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type)
            val listType: MutableList<String> = ApiClient.gson.fromJson(it.livraisonSousModelTypesStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type)
            val listQuantity: MutableList<String> = ApiClient.gson.fromJson(it.livraisonSousModelQuantitysStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type)
            val listAmount: MutableList<String> = ApiClient.gson.fromJson(it.livraisonSousModelAmountsStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type)
            val listScelle: MutableList<String> = ApiClient.gson.fromJson(it.livraisonSousModelScellesStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type)
            (ApiClient.gson.fromJson(it.livraisonSousModelProdNamesStringify?:"[]", object : TypeToken<MutableList<String>>() {}.type) as MutableList<String>).forEach {

                livraisonSousModelList.add(
                    LivraisonSousModel(
                        producteurId = listProdId.get(countero)?:"",
                        producteurIdName = it,
                        parcelleId = listParcelId.get(countero)?:"",
                        parcelleIdName = listParce.get(countero)?:"",
                        typeName = listType.get(countero)?:"",
                        quantityNb = listQuantity.get(countero).toInt(),
                        amountNb = listAmount.get(countero).toInt(),
                        numScelle = listScelle.get(countero)?:""
                    )
                )
                countero++
            }

            livraisonSousModelAdapter?.notifyDataSetChanged()

            countero = 0
            resources.getStringArray(R.array.statuPaiement).forEach { stPay ->
                if(stPay.equals("${it.paymentStatus}")) {
                    selectStatuPaiementList.setSelection(countero)
                    countero++
                }
            }

            editReduction.setText("${ it.reduction }")
            tvSousTotal.setText("${ it.sousTotalReduce.toString() }")
            tvTotalReduce.setText("${ it.totalReduce.toString() }")
            LogUtils.d(it.estimatDate)
            editDateLivraison.setText("${it.estimatDate}")

            true
        }


        
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



        editDateLivraison.setOnClickListener {
            datePickerDialog = null
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->

                //LogUtils.e(TAG, Commons.convertDate("${day}-${(month + 1)}-$year", false))
                editDateLivraison.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
                dateLivraison = editDateLivraison.text?.toString()!!
            }, year, month, dayOfMonth)

            datePickerDialog!!.datePicker.maxDate = Date().time
            datePickerDialog?.show()
        }

        clickCancelLivraison.setOnClickListener {
            clearFields()
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

        setAll()
        setListener()

        clickAddLivraisonInfo.setOnClickListener {
            try{
                if (producteurId.isEmpty()) {
                    showMessage(
                        "Selectionnez le producteur, svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "OK",
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                if(parcelleId.isEmpty()){
                    showMessage(
                        "Selectionnez une parcelle, svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "OK",
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                if(editQuantity.text.toString().isEmpty()){
                    showMessage(
                        "Passer la quantité, svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "OK",
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                val livraisonSousModel = LivraisonSousModel(
                    producteurId= producteurId,
                    producteurIdName= producteurNomPrenoms,
                    parcelleId= parcelleId,
                    parcelleIdName = parcelleNom,
                    typeName = typeProducteurParcrelle,
                    quantityNb = editQuantity.text.toString().toInt(),
                    amountNb = editResultatQuantity.text.toString().toInt(),
                    numScelle = editNumScelle.text.toString()
                )

                livraisonSousModelList.add(livraisonSousModel)
                livraisonSousModelAdapter!!.notifyDataSetChanged()

                clearInfoLivraisonTable()

            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

//        editVolumeLivraison.doAfterTextChanged {
//            try {
//
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
//
//        editNombreSacsLivraison.doAfterTextChanged {
//            try {
//
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }

//        applyFilters(editNombreSacsLivraison, withZero = true)
//        applyFilters(editVolumeLivraison, withZero = true)

        if (intent.getStringExtra("from") != null) {
            draftedDataLivraison = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataLivraison!!)
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
        editNumScelle.setText("")
    }
}
