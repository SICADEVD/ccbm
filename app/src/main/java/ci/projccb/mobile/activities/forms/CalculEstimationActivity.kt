package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.CalculEstimationPreviewActivity
import ci.projccb.mobile.models.CampagneModel
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.EstimationModel
import ci.projccb.mobile.models.LocaliteModel
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.models.ProducteurModel
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.TAG
import ci.projccb.mobile.tools.Commons.Companion.applyFilters
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
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
import kotlinx.android.synthetic.main.activity_calcul_estimation.imageDraftBtn
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectCampagneEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectLocaliteEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectParcelleEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectProducteurEstimation
import kotlinx.android.synthetic.main.activity_calcul_estimation.selectSectionEstimation
import kotlinx.android.synthetic.main.activity_formation.selectSectionFormation
import org.joda.time.DateTime
import java.util.Calendar

class CalculEstimationActivity : AppCompatActivity() {


    var campagnesList: MutableList<CampagneModel>? = null
    var localitesList: MutableList<LocaliteModel>? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var parcellesList: MutableList<ParcelleModel>? = null

    var producteurNom = ""
    var producteurId = ""

    var localiteNom = ""
    var localiteId = ""

    var campagneNom = ""
    var campagneId = ""

    var parcelleNom = ""
    var parcelleId = ""
    var parcelleSuperficie = ""

    val sectionCommon = CommonData()

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
        campagnesList = CcbRoomDatabase.getDatabase(applicationContext)?.campagneDao()?.getAll()

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


    fun setupParcellesProducteurSelection(producteurId: String?) {
        parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()?.getParcellesProducteur(
            producteurId = producteurId,
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        val parcellesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parcellesList?.map { Commons.getParcelleNotSyncLibel(it) }?.toList()?: arrayListOf())
        selectParcelleEstimation!!.adapter = parcellesAdapter

        selectParcelleEstimation.setTitle(getString(R.string.choisir_la_parcelle))
        selectParcelleEstimation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val parcelle = parcellesList!![position]

                parcelleNom = Commons.getParcelleNotSyncLibel(parcelle).toString()
                parcelleSuperficie = parcelle.superficie ?: "0.0"
                editSuperficieEstimation.text = Editable.Factory.getInstance().newEditable(parcelleSuperficie)

                parcelleId = if (parcelle.isSynced) {
                    parcelle.id.toString()
                } else {
                    parcelle.uid.toString()
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

    }


    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {
        var sectionDao = CcbRoomDatabase.getDatabase(applicationContext)?.sectionsDao();
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

                setupLocaliteSelection(sectionCommon.id!!)

            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupLocaliteSelection(id: Int) {
        localitesList = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getLocaliteBySection(id)

        if (localitesList?.size == 0) {
            Commons.showMessage(
                getString(R.string.la_liste_des_localit_s_est_vide_refaite_une_mise_jour),
                this,
                finished = false,
                callback = {},
                getString(R.string.compris),
                false,
                showNo = false,
            )

            localiteId = ""
            localiteNom = ""
            selectLocaliteEstimation?.adapter = null

            return
        }

        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
        selectLocaliteEstimation!!.adapter = localiteAdapter

        selectLocaliteEstimation.setTitle(getString(R.string.choisir_la_localite))
        selectLocaliteEstimation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val locality = localitesList!![position]
                localiteNom = locality.nom!!

                localiteId = if (locality.isSynced) {
                    locality.id!!.toString()
                } else {
                    locality.uid.toString()
                }

                LogUtils.e(TAG, "Local -> $localiteId")
                setupProducteurSelection(localiteId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupProducteurSelection(localite: String?) {
        // Producteur
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = localite)
        val producteursDatas: MutableList<CommonData> = mutableListOf()
        producteursList?.map {
            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
        }?.let {
            producteursDatas.addAll(it)
        }

        val estimationDrafted = ApiClient.gson.fromJson(draftedDataEstimation?.datas, EstimationModel::class.java)
        selectProducteurEstimation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

        if (draftedDataEstimation != null) {
            provideDatasSpinnerSelection(
                selectProducteurEstimation,
                estimationDrafted.producteurNom,
                producteursDatas
            )
        }

        selectProducteurEstimation.setTitle(getString(R.string.choisir_le_producteur))
        selectProducteurEstimation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val producteur = producteursList!![position]
                producteurNom = "${producteur.nom} ${producteur.prenoms}"

                producteurId = if (producteur.isSynced) {
                    producteur.id!!.toString()
                } else {
                    producteur.uid.toString()
                }

                editSuperficieEstimation.text = null

                setupParcellesProducteurSelection(producteurId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


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

    private fun getEstimationObjet(): EstimationModel {
        return EstimationModel(
            campagnesId = campagneId,
            campagnesNom = campagneNom,
            dateEstimation = editDateEstimation.text.toString(),
            ea1 = editA1Estimation.text.toString(),
            ea2 = editA2Estimation.text.toString(),
            ea3 = editA3Estimation.text.toString(),
            eb1 = editB1Estimation.text.toString(),
            eb2 = editB2Estimation.text.toString(),
            eb3 = editB3Estimation.text.toString(),
            ec1 = editC1Estimation.text.toString(),
            ec2 = editC2Estimation.text.toString(),
            ec3 = editC3Estimation.text.toString(),
            parcelleId = parcelleId,
            superficie = parcelleSuperficie,
            parcelleNom = parcelleNom,
            producteurNom = producteurNom,
            producteurId = producteurId,
            localiteNom = localiteNom,
            localiteId = localiteId,
            uid = 0,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            isSynced = false,
        )
    }


    fun draftEstimation(draftModel: DataDraftedModel?) {
        val estimationModelDraft = getEstimationObjet()

        Commons.showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(estimationModelDraft),
                        typeDraft = "calcul_estimation",
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

        // Localite
        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val localitesDatas: MutableList<CommonData> = mutableListOf()
        localitesLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            localitesDatas.addAll(it)
        }
        selectLocaliteEstimation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
        provideDatasSpinnerSelection(
            selectLocaliteEstimation,
            estimationDrafted.localiteNom,
            localitesDatas
        )

        // Campagne
        val campagnesLists = CcbRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()
        val campagnesDatas: MutableList<CommonData> = mutableListOf()
        campagnesLists?.map {
            CommonData(id = it.id, nom = it.campagnesNom)
        }?.let {
            campagnesDatas.addAll(it)
        }
        selectCampagneEstimation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesDatas)
        provideDatasSpinnerSelection(
            selectCampagneEstimation,
            estimationDrafted.campagnesNom,
            campagnesDatas
        )

        editSuperficieEstimation.setText(estimationDrafted.superficie)
        //applyFiltersDec(editSuperficieEstimation, withZero = false)

        editA1Estimation.setText(estimationDrafted.ea1)
        editA2Estimation.setText(estimationDrafted.ea2)
        editA3Estimation.setText(estimationDrafted.ea3)

        editB1Estimation.setText(estimationDrafted.eb1)
        editB2Estimation.setText(estimationDrafted.eb2)
        editB3Estimation.setText(estimationDrafted.eb3)

        editC1Estimation.setText(estimationDrafted.ec1)
        editC2Estimation.setText(estimationDrafted.ec2)
        editC3Estimation.setText(estimationDrafted.ec3)

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

        applyFilters(editA1Estimation)
        applyFilters(editA2Estimation)
        applyFilters(editA3Estimation)

        applyFilters(editB1Estimation)
        applyFilters(editB2Estimation)
        applyFilters(editB3Estimation)

        applyFilters(editC1Estimation)
        applyFilters(editC2Estimation)
        applyFilters(editC3Estimation)

        editA1Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editA2Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editA3Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editB1Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editB2Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editB3Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editC1Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editC2Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editC3Estimation.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        if (intent.getStringExtra("from") != null) {
            draftedDataEstimation = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataEstimation!!)
        }
    }
}
