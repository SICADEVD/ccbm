package ci.progbandama.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.infospresenters.CalculEstimationPreviewActivity
import ci.progbandama.mobile.models.CampagneModel
import ci.progbandama.mobile.models.DataDraftedModel
import ci.progbandama.mobile.models.EstimationModel
import ci.progbandama.mobile.models.LocaliteModel
import ci.progbandama.mobile.models.ParcelleModel
import ci.progbandama.mobile.models.ProducteurModel
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.applyFilters
import ci.progbandama.mobile.tools.Commons.Companion.checkAndReturnZeroFloatIfEmpty
import ci.progbandama.mobile.tools.Commons.Companion.checkAndReturnZeroIfEmpty
import ci.progbandama.mobile.tools.Commons.Companion.getSpinnerContent
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_calcul_estimation.*
import kotlinx.android.synthetic.main.activity_calcul_estimation.clickCloseBtn
import kotlinx.android.synthetic.main.activity_calcul_estimation.clickReviewEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editA1Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editA2Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editA3Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editB1Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editB2Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editB3Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editC1Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editC2Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editC3Estimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editDateEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editSuperficieEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.editajustement
import kotlinx.android.synthetic.main.activity_calcul_estimation.imageDraftBtn
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectCampagneEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectLocaliteEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectParcelleEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectProducteurEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectSectionEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectTypeEstimation
import org.joda.time.DateTime
import java.util.Calendar
import kotlin.math.roundToInt

class CalculEstimationActivity : AppCompatActivity() {


    private var valRendTheorique: Int = 0
    var campagnesList: MutableList<CampagneModel>? = null
    var localitesList: MutableList<LocaliteModel>? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var parcellesList: MutableList<ParcelleModel>? = null

//    var producteurNom = ""
//    var producteurId = ""
//
//    var localiteNom = ""
//    var localiteId = ""
//
    var campagneNom = ""
    var campagneId = ""
//
//    var parcelleNom = ""
//    var parcelleId = ""
    var parcelleSuperficie = ""

    val sectionCommon = CommonData()
    val localiteCommon = CommonData()
    val producteurCommon = CommonData()
    val parcelleCommon = CommonData()

    var piedA1 = ""
    var piedA2 = ""
    var piedA3 = ""
    var piedB1 = ""
    var piedB2 = ""
    var piedB3 = ""
    var piedC1 = ""
    var piedC2 = ""
    var piedC3 = ""

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_PICKED = 2

    var draftedDataEstimation: DataDraftedModel? = null


    fun setupCampagneSelection() {
        campagnesList = ProgBandRoomDatabase.getDatabase(applicationContext)?.campagneDao()?.getAll()

        val campagneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList!!)
        selectCampagneEstimation!!.adapter = campagneAdapter

        selectCampagneEstimation.setTitle(getString(R.string.choisir_la_campagne))
        selectCampagneEstimation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val campagne = campagnesList!![position]
                campagneNom = campagne.campagnesNom!!
                campagneId = campagne.id.toString()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


//    fun setupParcellesProducteurSelection(producteurId: String?) {
//        parcellesList = ProgBandRoomDatabase.getDatabase(applicationContext)?.parcelleDao()?.getParcellesProducteur(
//            producteurId = producteurId,
//            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
//        )
//
//        val parcellesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parcellesList?.map { Commons.getParcelleNotSyncLibel(it) }?.toList()?: arrayListOf())
//        selectParcelleEstimation!!.adapter = parcellesAdapter
//
//        selectParcelleEstimation.setTitle(getString(R.string.choisir_la_parcelle))
//        selectParcelleEstimation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val parcelle = parcellesList!![position]
//
//                parcelleNom = Commons.getParcelleNotSyncLibel(parcelle).toString()
//                parcelleSuperficie = parcelle.superficie ?: "0.0"
//                editSuperficieEstimation.text = Editable.Factory.getInstance().newEditable(parcelleSuperficie)
//
//                parcelleId = if (parcelle.isSynced) {
//                    parcelle.id.toString()
//                } else {
//                    parcelle.uid.toString()
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//
//    }


    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {
        var sectionDao = ProgBandRoomDatabase.getDatabase(applicationContext)?.sectionsDao();
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
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = libItem ,
            spinner = selectSectionEstimation,
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
            spinner = selectLocaliteEstimation,
            listIem = localitesListi.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                localitesListi.let { list ->
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
            spinner = selectProducteurEstimation,
            listIem = producteursList?.map { "${ it.nom } ${ it.prenoms }" }?.toList() ?: listOf(),
            onChanged = {
                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if (producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    } else producteurCommon.id = producteur.uid

                    setupParcelleSelection(producteurCommon.id.toString(), currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupParcelleSelection(producteurId: String?, currVal3: String? = null) {
        var parcellesList = ProgBandRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
            ?.getParcellesProducteur(producteurId = producteurId.toString(), agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

//        LogUtils.json(parcellesList)
        var libItem: String? = null
        currVal3?.let { idc ->
            parcellesList?.forEach {
                if(it.isSynced){
                    if (it.id.toString() == idc.toString()) libItem = Commons.getParcelleNotSyncLibel(it)
                }else{
                    if (it.uid.toString() == idc.toString()) libItem = Commons.getParcelleNotSyncLibel(it)
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_parcelle),
            getString(R.string.la_liste_des_parcelles_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectParcelleEstimation,
            listIem = parcellesList?.map { Commons.getParcelleNotSyncLibel(it) }
                ?.toList() ?: listOf(),
            onChanged = {

                parcellesList?.let { list ->
                    var parcelle = list.get(it)
                    parcelleCommon.nom = Commons.getParcelleNotSyncLibel(parcelle)

                    parcelleSuperficie = parcelle.superficie ?: "0.0"
                    editSuperficieEstimation.text = Editable.Factory.getInstance().newEditable(parcelleSuperficie)

                    if(parcelle.isSynced){
                        parcelleCommon.id = parcelle.id!!
                    }else{
                        parcelleCommon.id = parcelle.uid.toString().toInt()
                    }

                    //setupParcelleSelection(parcelleCommon.id, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })
    }

//    fun setupLocaliteSelection(id: Int) {
//        localitesList = ProgBandRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getLocaliteBySection(id)
//
//        if (localitesList?.size == 0) {
//            Commons.showMessage(
//                getString(R.string.la_liste_des_localit_s_est_vide_refaite_une_mise_jour),
//                this,
//                finished = false,
//                callback = {},
//                getString(R.string.compris),
//                false,
//                showNo = false,
//            )
//
//            localiteId = ""
//            localiteNom = ""
//            selectLocaliteEstimation?.adapter = null
//
//            return
//        }
//
//        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
//        selectLocaliteEstimation!!.adapter = localiteAdapter
//
//        selectLocaliteEstimation.setTitle(getString(R.string.choisir_la_localite))
//        selectLocaliteEstimation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val locality = localitesList!![position]
//                localiteNom = locality.nom!!
//
//                localiteId = if (locality.isSynced) {
//                    locality.id!!.toString()
//                } else {
//                    locality.uid.toString()
//                }
//
//                LogUtils.e(TAG, "Local -> $localiteId")
//                setupProducteurSelection(localiteId)
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


//    fun setupProducteurSelection(localite: String?) {
//        // Producteur
//        producteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = localite)
//        val producteursDatas: MutableList<CommonData> = mutableListOf()
//        producteursList?.map {
//            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
//        }?.let {
//            producteursDatas.addAll(it)
//        }
//
//        val estimationDrafted = ApiClient.gson.fromJson(draftedDataEstimation?.datas, EstimationModel::class.java)
//        selectProducteurEstimation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)
//
//        if (draftedDataEstimation != null) {
//            provideDatasSpinnerSelection(
//                selectProducteurEstimation,
//                estimationDrafted.producteurNom,
//                producteursDatas
//            )
//        }
//
//        selectProducteurEstimation.setTitle(getString(R.string.choisir_le_producteur))
//        selectProducteurEstimation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val producteur = producteursList!![position]
//                producteurNom = "${producteur.nom} ${producteur.prenoms}"
//
//                producteurId = if (producteur.isSynced) {
//                    producteur.id!!.toString()
//                } else {
//                    producteur.uid.toString()
//                }
//
//                editSuperficieEstimation.text = null
//
//                setupParcellesProducteurSelection(producteurId)
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


    fun configDate(viewClciked: AppCompatEditText) {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
            viewClciked.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
        }, year, month, dayOfMonth)

        datePickerDialog.datePicker.maxDate = DateTime.now().millis
        datePickerDialog.show()
    }


    fun setAll() {
        setupSectionSelection()
        setupCampagneSelection()
    }


    fun collectDatasReview() {
        val estimationModel = getEstimationObjet()

        try {
            val intentEstimationPreview = Intent(this, CalculEstimationPreviewActivity::class.java)
            intentEstimationPreview.putExtra("preview", estimationModel)
            intentEstimationPreview.putExtra("draft_id", draftedDataEstimation?.uid)
            startActivity(intentEstimationPreview)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Commons.showMessage("Une erreur est survenue", context = this, finished = false, callback = {}, deconnec = false)
        }
    }

    fun passSetupEstimationModel(
        prodModel: EstimationModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_estimation)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }

    fun getSetupEstimationModel(
        prodModel: EstimationModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<EstimationModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_estimation)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    private fun getEstimationObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): EstimationModel? {
        var isMissingDial2 = false

        var itemList = getSetupEstimationModel(
            EstimationModel(
            uid = 0,
            isSynced = false,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            origin = "local",
        ), mutableListOf<Pair<String,String>>())
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

        return EstimationModel(
            campagnesId = campagneId,
            campagnesNom = campagneNom,
            dateEstimation = editDateEstimation.text.toString(),
            ea1 = editA1Estimation.checkAndReturnZeroIfEmpty(),
            ea2 = editA2Estimation.checkAndReturnZeroIfEmpty(),
            ea3 = editA3Estimation.checkAndReturnZeroIfEmpty(),
            eb1 = editB1Estimation.checkAndReturnZeroIfEmpty(),
            eb2 = editB2Estimation.checkAndReturnZeroIfEmpty(),
            eb3 = editB3Estimation.checkAndReturnZeroIfEmpty(),
            ec1 = editC1Estimation.checkAndReturnZeroIfEmpty(),
            ec2 = editC2Estimation.checkAndReturnZeroIfEmpty(),
            ec3 = editC3Estimation.checkAndReturnZeroIfEmpty(),
            ajustement = editajustement.checkAndReturnZeroFloatIfEmpty(),
            typeEstimation = selectTypeEstimation.getSpinnerContent(),
            recolteEstime = editRecolteEstimee.checkAndReturnZeroFloatIfEmpty(),
            rendFinal = editRendementFinal.checkAndReturnZeroIfEmpty(),
            parcelleId = parcelleCommon.id.toString(),
            section = sectionCommon.id.toString(),
            superficie = parcelleSuperficie,
            parcelleNom = parcelleCommon.nom.toString(),
            producteurNom = producteurCommon.nom.toString(),
            producteurId = producteurCommon.id.toString(),
            localiteNom = localiteCommon.nom.toString(),
            localiteId = localiteCommon.id.toString(),
            uid = 0,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            isSynced = false,
        )
    }


    fun draftEstimation(draftModel: DataDraftedModel?) {
        val estimationModelDraft = getEstimationObjet(false, necessaryItem = mutableListOf(
            "Parcelle"
        ))

        if(estimationModelDraft == null) return

        Commons.showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(estimationModelDraft),
                        typeDraft = "estimation",
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
        val estimationDrafted = ApiClient.gson.fromJson(draftedData.datas, EstimationModel::class.java)

        Commons.debugModelToJson(estimationDrafted)
        setupSectionSelection(estimationDrafted.section, estimationDrafted.localiteId, estimationDrafted.producteurId, estimationDrafted.parcelleId)

        passSetupEstimationModel(estimationDrafted)

        // Localite
//        val localitesLists = ProgBandRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//        val localitesDatas: MutableList<CommonData> = mutableListOf()
//        localitesLists?.map {
//            CommonData(id = it.id, nom = it.nom)
//        }?.let {
//            localitesDatas.addAll(it)
//        }
//        selectLocaliteEstimation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//        provideDatasSpinnerSelection(
//            selectLocaliteEstimation,
//            estimationDrafted.localiteNom,
//            localitesDatas
//        )
//
//        // Campagne
//        val campagnesLists = ProgBandRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()
//        val campagnesDatas: MutableList<CommonData> = mutableListOf()
//        campagnesLists?.map {
//            CommonData(id = it.id, nom = it.campagnesNom)
//        }?.let {
//            campagnesDatas.addAll(it)
//        }
//        selectCampagneEstimation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesDatas)
//        provideDatasSpinnerSelection(
//            selectCampagneEstimation,
//            estimationDrafted.campagnesNom,
//            campagnesDatas
//        )
//
//        editSuperficieEstimation.setText(estimationDrafted.superficie)
//        //applyFiltersDec(editSuperficieEstimation, withZero = false)
//
        editA1Estimation.setText(estimationDrafted.ea1)
        editA2Estimation.setText(estimationDrafted.ea2)
        editA3Estimation.setText(estimationDrafted.ea3)

        editB1Estimation.setText(estimationDrafted.eb1)
        editB2Estimation.setText(estimationDrafted.eb2)
        editB3Estimation.setText(estimationDrafted.eb3)

        editC1Estimation.setText(estimationDrafted.ec1)
        editC2Estimation.setText(estimationDrafted.ec2)
        editC3Estimation.setText(estimationDrafted.ec3)

        estimationDrafted.typeEstimation?.let{
            var curr = 0
            for (item in resources.getStringArray(R.array.type_estimation)){
                if (it.equals(item, ignoreCase = true)) selectTypeEstimation.setSelection(curr)
                curr++
            }
        }
        editajustement.setText(estimationDrafted.ajustement)
        editRendementFinal.setText(estimationDrafted.rendFinal)
        editRecolteEstimee.setText(estimationDrafted.recolteEstime)
        calculLeRendementTheorique()

        editDateEstimation.setText(estimationDrafted.dateEstimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcul_estimation)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        setAll()

        clickReviewEstimation.setOnClickListener {
            collectDatasReview()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        editDateEstimation.setOnClickListener {
            configDate(editDateEstimation)
        }

        imageDraftBtn.setOnClickListener {
            draftEstimation(draftedDataEstimation ?: DataDraftedModel(uid = 0))
        }

        applyFilters(editDateEstimation)

        Commons.addNotZeroAtFirstToET(editA1Estimation)
        Commons.addNotZeroAtFirstToET(editA2Estimation)
        Commons.addNotZeroAtFirstToET(editA3Estimation)

        Commons.addNotZeroAtFirstToET(editB1Estimation)
        Commons.addNotZeroAtFirstToET(editB2Estimation)
        Commons.addNotZeroAtFirstToET(editB3Estimation)

        Commons.addNotZeroAtFirstToET(editC1Estimation)
        Commons.addNotZeroAtFirstToET(editC2Estimation)


        selectTypeEstimation.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if(p2==0){
//                    LogUtils.d("valRendementTheorique")
                  //R calculé
                    carreeA.visibility = View.VISIBLE
                    carreeB.visibility = View.VISIBLE
                    carreeC.visibility = View.VISIBLE

                    Commons.addNotZeroAtFirstToET(editC3Estimation, onTextChange = { text ->
                        if(
                            editA1Estimation.text.isNullOrEmpty() ||
                            editA2Estimation.text.isNullOrEmpty() ||
                            editA3Estimation.text.isNullOrEmpty() ||
                            editB1Estimation.text.isNullOrEmpty() ||
                            editB2Estimation.text.isNullOrEmpty() ||
                            editB3Estimation.text.isNullOrEmpty() ||
                            editC1Estimation.text.isNullOrEmpty() ||
                            editC2Estimation.text.isNullOrEmpty() ||
                            editC3Estimation.text.isNullOrEmpty()
                        ){
                            Commons.showMessage("Veuillez renseigner tous les carrés d'estimation", this@CalculEstimationActivity, finished = false, callback = {})
                        }else{
                            if (text.isNotEmpty()) {

                                calculLeRendementTheorique()
                                //val editC3EstimationVal = text.toString().toIntOrNull()

                            }
                        }

                    })

                    editajustement.doOnTextChanged { text, start, before, count ->
                        if(
                            editA1Estimation.text.isNullOrEmpty() ||
                            editA2Estimation.text.isNullOrEmpty() ||
                            editA3Estimation.text.isNullOrEmpty() ||
                            editB1Estimation.text.isNullOrEmpty() ||
                            editB2Estimation.text.isNullOrEmpty() ||
                            editB3Estimation.text.isNullOrEmpty() ||
                            editC1Estimation.text.isNullOrEmpty() ||
                            editC2Estimation.text.isNullOrEmpty() ||
                            editC3Estimation.text.isNullOrEmpty()
                        ){
                            Commons.showMessage("Veuillez renseigner tous les carrés d'estimation", this@CalculEstimationActivity, finished = false, callback = {})
                        }

                        if(!text.isNullOrEmpty()){
                            val textVal = text.toString().toIntOrNull()
                            textVal?.let {
                                if(it < -20){
                                    Commons.showMessage("Pourcentage d'ajustement est inférieur à -20%", this@CalculEstimationActivity, finished = false, callback = {})
                                }else if(it > 20){
                                    Commons.showMessage("Pourcentage d'ajustement est supérieur à 20%", this@CalculEstimationActivity, finished = false, callback = {})
                                }
                                editRendementFinal.setText("${valRendTheorique+(valRendTheorique*textVal)}")
                            }
                        }
                    }

                    editRendementFinal.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                            //TODO("Not yet implemented")
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            //TODO("Not yet implemented")
                        }

                        override fun afterTextChanged(text: Editable?) {
                            //TODO("Not yet implemented")
                            if(!text.toString().isNullOrEmpty()){
                                val RfVal = text.toString().toIntOrNull()
                                RfVal?.let {
                                    val superVal = editSuperficieEstimation.text.toString().toFloatOrNull()
                                    superVal?.let {
                                        editRecolteEstimee.setText("${(RfVal*superVal).roundToInt()}")
                                    }
                                }
                            }
                        }

                    })

                    editRendementFinal.isEnabled = false
                    editRendementFinal.removeTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                            //TODO("Not yet implemented")
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            //TODO("Not yet implemented")
                        }

                        override fun afterTextChanged(p0: Editable?) {
                            //TODO("Not yet implemented")
                        }

                    })

//                    editRendementFinal.doAfterTextChanged { text ->
//
//                    }

                }else{
                    LogUtils.d("editRendementFinal")
                    //R estimé
                    carreeA.visibility = View.GONE
                    carreeB.visibility = View.GONE
                    carreeC.visibility = View.GONE

                    editRendementFinal.isEnabled = true
                    editRendementFinal.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                            //TODO("Not yet implemented")
                        }

                        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            //TODO("Not yet implemented")
                            if(!text.isNullOrEmpty()){
                                val RfVal = text.toString().toIntOrNull()
                                RfVal?.let {
                                    val superVal = editSuperficieEstimation.text.toString().toFloatOrNull()
                                    superVal?.let {
                                        editRecolteEstimee.setText("${(RfVal*superVal).toFloat()}")
                                    }
                                }
                            }
                        }

                        override fun afterTextChanged(text: Editable?) {
                            //TODO("Not yet implemented")
                        }

                    })
//                    editRendementFinal.doOnTextChanged { text, start, before, count ->
//
//                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


        if (intent.getStringExtra("from") != null) {
                draftedDataEstimation = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataEstimation!!)
        }
    }

    private fun calculLeRendementTheorique() {
        var nbrArbreABCc20 = editA1Estimation.text.toString().toInt()+editB1Estimation.text.toString().toInt()+editC1Estimation.text.toString().toInt()
        var nbrArbreABCc10 = editA2Estimation.text.toString().toInt()+editB2Estimation.text.toString().toInt()+editC2Estimation.text.toString().toInt()
        var nbrArbreABCc0 = editA3Estimation.text.toString().toInt()+editB3Estimation.text.toString().toInt()+editC3Estimation.text.toString().toInt()

        var volABCc20 = ((nbrArbreABCc20*1)/3)
        var volABCc10 = ((nbrArbreABCc10*0.6)/3)
        var volABCc0 = ((nbrArbreABCc0*0.2)/3)

        var valRendementTheorique = ((volABCc20+volABCc10+volABCc0)*100)

        valRendTheorique = valRendementTheorique.roundToInt()
    }
}
