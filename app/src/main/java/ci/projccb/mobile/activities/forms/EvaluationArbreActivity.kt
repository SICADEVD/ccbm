package ci.projccb.mobile.activities.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.EvaluationBesoinPreviewActivity
import ci.projccb.mobile.activities.infospresenters.VisiteurFormationPreviewActivity
import ci.projccb.mobile.adapters.MultipleItemAdapter
import ci.projccb.mobile.adapters.PreviewItemAdapter
import ci.projccb.mobile.models.ArbreModel
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.EvaluationArbreDao
import ci.projccb.mobile.models.EvaluationArbreModel
import ci.projccb.mobile.models.VisiteurFormationModel
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
//import kotlinx.android.synthetic.main.activity_distribution_arbre.clickAddArbreDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.clickCancelDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.clickSaveDistributionArbre
//import kotlinx.android.synthetic.main.activity_distribution_arbre.editQuantitArbreDistribut
//import kotlinx.android.synthetic.main.activity_distribution_arbre.recyclerArbreListDistributionArbre
//import kotlinx.android.synthetic.main.activity_distribution_arbre.selectChoixDeLArbreDistributionArbre
//import kotlinx.android.synthetic.main.activity_distribution_arbre.selectChoixStateArbrDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectLocaliteDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectProducteurDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectSectionDistributionArbre

import kotlinx.android.synthetic.main.activity_evaluation_arbre.*
import java.util.ArrayList

class EvaluationArbreActivity : AppCompatActivity() {
    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();
    val parcelleCommon = CommonData();
    private var evaluationArbreDao: EvaluationArbreDao? = null
    var draftedDataEval: DataDraftedModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation_arbre)

        evaluationArbreDao = CcbRoomDatabase.getDatabase(this)?.evaluationArbreDao()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveEvaluationArbre.setOnClickListener {
            collectDatas()
        }

        clickCancelEvaluationArbre.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftData(draftedDataEval ?: DataDraftedModel(uid = 0))
        }

        setOtherListenner()

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataEval = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataEval!!)
            }else{
                setAllSelection()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }

    private fun draftData(dataDraftedModel: DataDraftedModel) {

        val itemModelOb = getEvalBesObjet(false)

        if(itemModelOb == null) return

        val listArbre = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()

        val formationModelDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()

                especesarbreStr = GsonUtils.toJson((recyclerArbreListEvalArbre?.adapter as MultipleItemAdapter).getMultiItemAdded().map { adapterIt ->
                    listArbre?.filter { "${it.nom}".trim().equals(adapterIt.value?.split("|")?.get(0)?.trim()) }?.let {
                        if(it.size > 0) it[0].id else -1
                    }
                })
                quantiteStr = GsonUtils.toJson((recyclerArbreListEvalArbre?.adapter as MultipleItemAdapter).getMultiItemAdded().map { it.value2 })
            }
        }

        Commons.showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {

                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = dataDraftedModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(formationModelDraft),
                        typeDraft = "evaluation_besoin",
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

    private fun setOtherListenner() {

//        editNbrCacaoHecEvaluationArbre.addTextChangedListener {
//            caculateNbrArbre()
//        }
//
//        editSuperficieEvaluationArbre.addTextChangedListener {
//            caculateNbrArbre()
//        }
        Commons.setFiveItremRV(this, recyclerArbreListEvalArbre, clickAddArbreEvalArbre,
            selectChoixDeLArbreEvalArbre,
            selectChoixStateArbrEvalArbre,
            selectChoixDeLArbreEvalArbre,
            editQuantitEvalArbre,
            editQuantitEvalArbre,
            defaultItemSize = 3,
            libeleList = arrayListOf(
                getString(R.string.arbre_concern),
                getString(R.string.strate),
                getString(R.string.quantit),
                "",
                "",
            )
        )

    }

    private fun caculateNbrArbre() {
//        if(!editNbrCacaoHecEvaluationArbre.text.toString().isNullOrEmpty() &&
//            !editSuperficieEvaluationArbre.text.toString().isNullOrEmpty()){
//
//            editNbreDAbreEvaluationArbre.setText( ((editSuperficieEvaluationArbre.text.toString().toDouble()*25) - editNbrCacaoHecEvaluationArbre.text.toString().toInt()).toString() )
//
//        }
    }

    private fun setupSelectionArbreList(listArbreADistri: MutableList<ArbreModel>, currentVal: String? = null) {
        Commons.setListenerForSpinner(this,
            getString(R.string.evalarbre_text),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectChoixDeLArbreEvalArbre,
            currentVal = currentVal,
            listIem = listArbreADistri?.map { "${it.nom+"|"} ${it.nomScientifique}" }?.toMutableList() ?: listOf(),
            onChanged = {
                val  listofstrate = mutableListOf<String>()
                val value = getString(R.string.strate)+listArbreADistri[it].strate
                listofstrate.add("${value}")
                //LogUtils.d(listofstrate)
                setupSelectionStrate(listofstrate)
            },
            onSelected = { itemId, visibility ->
            })
    }

    private fun setupSelectionStrate(listStrate: MutableList<String>, currentVal: String? = null) {
        Commons.setListenerForSpinner(this,
            getString(R.string.quelle_est_sa_strate),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectChoixStateArbrEvalArbre,
            currentVal = currentVal,
            listIem = listStrate
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })
    }

    private fun undraftedDatas(evaluationArbreModel: DataDraftedModel) {
        val evaluationArbreModelDraft = evaluationArbreModel.datas?.let {
            GsonUtils.fromJson(it, EvaluationArbreModel::class.java)
        }

        setupSectionSelection(evaluationArbreModelDraft?.section, evaluationArbreModelDraft?.localite)

        val listArbre = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()
        setupSelectionArbreList(listArbre?: mutableListOf())

    }

    private fun setAllSelection() {

        setupSectionSelection()

        val listArbre = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()
        setupSelectionArbreList(listArbre?: mutableListOf())

    }

    private fun collectDatas() {

        val itemModelOb = getEvalBesObjet()

        if(itemModelOb == null) return

        val listArbre = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()

        val formationModel = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()

                especesarbreStr = GsonUtils.toJson((recyclerArbreListEvalArbre?.adapter as MultipleItemAdapter).getMultiItemAdded().map { adapterIt ->
                    listArbre?.filter { "${it.nom}".trim().equals(adapterIt.value?.split("|")?.get(0)?.trim()) }?.let {
                        if(it.size > 0) it[0].id else -1
                    }
                })
                quantiteStr = GsonUtils.toJson((recyclerArbreListEvalArbre?.adapter as MultipleItemAdapter).getMultiItemAdded().map { it.value2 })
            }
        }

        //val listArbre = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
            (recyclerArbreListEvalArbre?.adapter as MultipleItemAdapter).getMultiItemAdded().forEach { adapItem ->
                //val arbreId = listArbre?.filter { it.nom+" | "+it.nomScientifique == adapItem.value }?.get(0)?.id ?: -1
                add(Pair(adapItem.value.toString(), adapItem.value2.toString()))
            }
        }.map { MapEntry(it.first, it.second) }

        //Commons.printModelValue(formationModel as Object, mapEntries)

        try {
            val intentVisitFormationPreview = Intent(this, EvaluationBesoinPreviewActivity::class.java)
            intentVisitFormationPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentVisitFormationPreview.putExtra("preview", formationModel)
            intentVisitFormationPreview.putExtra("draft_id", draftedDataEval?.uid)
            startActivity(intentVisitFormationPreview)
        } catch (ex: Exception) {
            ex.toString()
        }

    }

    private fun getEvalBesObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<EvaluationArbreModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        var itemList = getSetupEvalBesModel(
            EvaluationArbreModel(
                uid = 0,
                isSynced = false,
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
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
                message = getString(R.string.le_champ_intitul_n_est_pas_renseign)
                isMissing = true
                break
            }
        }

        for (field in allField){
            if(field.second.isNullOrBlank() && necessaryItem.contains(field.first)){
                message = getString(R.string.le_champ_intitul_n_est_pas_renseign)
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

    fun getSetupEvalBesModel(
        prodModel: EvaluationArbreModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<EvaluationArbreModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_EvaluationArbre)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupEvalBesModel(
        prodModel: EvaluationArbreModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_EvaluationArbre)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
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
                if(it.id == idc.toInt()) libItem = it.libelle
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_section),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = libItem ,
            spinner = selectSectionEvaluationArbre,
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
            getString(R.string.choix_de_la_localit),
            getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectLocaliteEvaluationArbre,
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
        val producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
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
            spinner = selectProducteurEvaluationArbre,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid

                    //setupParcelleSelection(producteurCommon.id.toString(), currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

//    fun setupParcelleSelection(producteurId: String?, currVal3: String? = null) {
//        var parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
//            ?.getParcellesProducteur(producteurId = producteurId.toString(), agentID = SPUtils.getInstance().getInt(
//                Constants.AGENT_ID, 0).toString())
//
//        LogUtils.json(parcellesList)
//        var libItem: String? = null
//        currVal3?.let { idc ->
//            parcellesList?.forEach {
//                if (it.id == idc.toInt()) libItem = "${it.codeParc}"
//            }
//        }
//
//        Commons.setListenerForSpinner(this,
//            getString(R.string.choix_de_la_parcelle),
//            getString(R.string.la_liste_des_parcelles_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
//            isEmpty = if (parcellesList?.size!! > 0) false else true,
//            currentVal = libItem,
//            spinner = selectParcelleEvaluationArbre,
//            listIem = parcellesList?.map { "${it.codeParc}" }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//                parcellesList?.let { list ->
//                    var parcelle = list.get(it)
//                    parcelleCommon.nom = "${it.codeParc}"
//                    parcelleCommon.id = parcelle.id!!
//
//                    //editSuperficieEvaluationArbre.setText("${parcelle.superficieConcerne}")
//                    //setupParcelleSelection(parcelleCommon.id, currVal3)
//                }
//
//
//            },
//            onSelected = { itemId, visibility ->
//
//            })
//    }
}