package ci.projccb.mobile.activities.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.FormationPreviewActivity
import ci.projccb.mobile.activities.infospresenters.VisiteurFormationPreviewActivity
import ci.projccb.mobile.adapters.MultipleItemAdapter
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.FormationModel
import ci.projccb.mobile.models.TypeMachineModel
import ci.projccb.mobile.models.VisiteurFormationDao
import ci.projccb.mobile.models.VisiteurFormationModel
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.FormationDao
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_evaluation_arbre.clickCancelEvaluationArbre
import kotlinx.android.synthetic.main.activity_evaluation_arbre.clickSaveEvaluationArbre
import kotlinx.android.synthetic.main.activity_evaluation_arbre.recyclerArbreListEvalArbre
import kotlinx.android.synthetic.main.activity_evaluation_arbre.selectSectionEvaluationArbre
import kotlinx.android.synthetic.main.activity_producteur_menage.containerAutreMachineMenage
import kotlinx.android.synthetic.main.activity_producteur_menage.selectLocaliteProduMenage
import kotlinx.android.synthetic.main.activity_producteur_menage.selectMachinePulMenage
import kotlinx.android.synthetic.main.activity_producteur_menage.selectProducteurMenage
import kotlinx.android.synthetic.main.activity_producteur_menage.selectSectionProducteurMenage
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.containerNbrTravSocieteInfosProducteur
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.selectTravaiSocietInfosProducteur
import kotlinx.android.synthetic.main.activity_visiteur_formation.clickCancelVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.containerAutreLienParentVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.containerVisiteurIsProducteur
import kotlinx.android.synthetic.main.activity_visiteur_formation.selectFormationVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.selectLienParentVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.selectReprProducteurVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.*
import java.util.ArrayList

class VisiteurFormationActivity : AppCompatActivity() {

    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();

    private val formationCommon: CommonData = CommonData()
    private var formationDao: FormationDao? = null
    private var draftedDataVisit: DataDraftedModel? = null
    private var visiteurFormationDao: VisiteurFormationDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visiteur_formation)

        visiteurFormationDao = CcbRoomDatabase.getDatabase(this)?.visiteurFormationDao()
        formationDao = CcbRoomDatabase.getDatabase(this)?.formationDao()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveVisitForm.setOnClickListener {
            collectDatas()
        }

        clickCancelVisitForm.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftData(draftedDataVisit ?: DataDraftedModel(uid = 0))
        }

        setOtherListenner()

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataVisit = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataVisit!!)
            }else{
                setAllSelection()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }

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
            spinner = selectSectionProducteurVisitForm,
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
            spinner = selectLocaliteProduVisitForm,
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
       val producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix du producteur !",
            "La liste des producteurs semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurVisitForm,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
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

    private fun setOtherListenner() {

    }

    private fun undraftedDatas(draftedDataVisit: DataDraftedModel) {
        val visiteurFormationDrafted = ApiClient.gson.fromJson(draftedDataVisit.datas, VisiteurFormationModel::class.java)
        setupSectionSelection(visiteurFormationDrafted.section, visiteurFormationDrafted.localite, visiteurFormationDrafted.producteurId)

        val formationList = formationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        Commons.setListenerForSpinner(this,
            "Selectionner la formation !",
            "La liste des formations semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (formationList?.size!! > 0) false else true,
            spinner = selectFormationVisitForm,
            currentVal = "Formation "+(formationList?.find { it.id == visiteurFormationDrafted.suivi_formation_id?.toInt() } as FormationModel?)?.id.toString() ?: "0",
            listIem = formationList?.map { "Formation ${it.id}" }
                ?.toList() ?: listOf(),
            onChanged = {

                val formation = formationList!![it]
                //ogUtils.d(section)
                formationCommon.nom = "Formation ${formation.id}"
                formationCommon.id = formation.id!!

            },
            onSelected = { itemId, visibility ->

            })

        Commons.setListenerForSpinner(this,
            "Representez vous un producteur ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectReprProducteurVisitForm,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            currentVal = visiteurFormationDrafted.representer ,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerVisiteurIsProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Quel lien avez vous ?",
            "La liste des éléments semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectLienParentVisitForm,
            currentVal = visiteurFormationDrafted.lien,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (resources.getStringArray(R.array.parentAffiliation)?.toList() ?: listOf()) ,
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreLienParentVisitForm.visibility = visibility
            })
    }

    private fun draftData(dataDraftedModel: DataDraftedModel) {
        val itemModelOb = getVisiteurFormationObjet(false)

        if(itemModelOb == null) return

        val formationModelDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()

                producteurId = producteurCommon.id.toString()
                suivi_formation_id = formationCommon.id.toString()
            }
        }

        Commons.showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {

                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = dataDraftedModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(formationModelDraft),
                        typeDraft = "visiteur_formation",
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

    private fun setAllSelection() {

        setupSectionSelection()

        val formationList = formationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        Commons.setListenerForSpinner(this,
            "Selectionner la formation !",
            "La liste des formations semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (formationList?.size!! > 0) false else true,
            spinner = selectFormationVisitForm,
            listIem = formationList?.map { "Formation ${it.id}" }
                ?.toList() ?: listOf(),
            onChanged = {

                val formation = formationList!![it]
                //ogUtils.d(section)
                formationCommon.nom = "Formation ${formation.id}"
                formationCommon.id = formation.id!!

            },
            onSelected = { itemId, visibility ->

            })

        Commons.setListenerForSpinner(this,
            "Representez vous un producteur ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectReprProducteurVisitForm,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerVisiteurIsProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Quel lien avez vous ?",
            "La liste des éléments semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectLienParentVisitForm,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (resources.getStringArray(R.array.parentAffiliation)?.toList() ?: listOf()) ,
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreLienParentVisitForm.visibility = visibility
            })
    }

    private fun collectDatas() {

        val itemModelOb = getVisiteurFormationObjet()

        if(itemModelOb == null) return

        val formationModel = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()

                producteurId = producteurCommon.id.toString()
                suivi_formation_id = formationCommon.id.toString()
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {

        }.map { MapEntry(it.first, it.second) }

        Commons.printModelValue(formationModel as Object, mapEntries)

        try {
            val intentVisitFormationPreview = Intent(this, VisiteurFormationPreviewActivity::class.java)
            intentVisitFormationPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentVisitFormationPreview.putExtra("preview", formationModel)
            intentVisitFormationPreview.putExtra("draft_id", draftedDataVisit?.uid)
            startActivity(intentVisitFormationPreview)
        } catch (ex: Exception) {
            ex.toString()
        }

    }

    private fun getVisiteurFormationObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<VisiteurFormationModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        var itemList = getSetupVisiteurModel(
            VisiteurFormationModel(
                uid = 0,
                isSynced = false,
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
                origin = "local",
            ), mutableListOf<Pair<String,String>>())
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

    fun getSetupVisiteurModel(
        prodModel: VisiteurFormationModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<VisiteurFormationModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_VisitForm)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupVisiteurModel(
        prodModel: VisiteurFormationModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_VisitForm)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }

}