package ci.progbandama.mobile.activities.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.infospresenters.VisiteurFormationPreviewActivity
import ci.progbandama.mobile.models.DataDraftedModel
import ci.progbandama.mobile.models.FormationModel
import ci.progbandama.mobile.models.VisiteurFormationDao
import ci.progbandama.mobile.models.VisiteurFormationModel
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.FormationDao
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.limitListByCount
import ci.progbandama.mobile.tools.Commons.Companion.toModifString
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
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

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        visiteurFormationDao = ProgBandRoomDatabase.getDatabase(this)?.visiteurFormationDao()
        formationDao = ProgBandRoomDatabase.getDatabase(this)?.formationDao()

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
                draftedDataVisit = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
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

        var localiteDao = ProgBandRoomDatabase.getDatabase(applicationContext)?.localiteDoa();
        var localitesListi = localiteDao?.getLocaliteBySection(id)
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal1?.let { idc ->
            localitesListi?.forEach {
                if(it.id.toString() == idc.toString()) libItem = it.nom
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_localit),
            getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
       val producteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if(it.uid.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }else{
                    if(it.id.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurVisitForm,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid

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

        val listTypeFormation = ProgBandRoomDatabase.getDatabase(this)?.typeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listThemeFormation = ProgBandRoomDatabase.getDatabase(this)?.themeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listSousThemeFormation = ProgBandRoomDatabase.getDatabase(this)?.sousThemeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val typeTok = object : TypeToken<MutableList<String>>(){}.type
        val formationList = formationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        LogUtils.d(formationList)

        val currFormation = (formationList?.find { it.id.toString() == visiteurFormationDrafted.suivi_formation_id } as FormationModel?)
        val currparticipCount = GsonUtils.fromJson<MutableList<String>>(currFormation?.producteursIdStr, object : TypeToken<MutableList<String>>(){}.type).let {
            if(it.isNullOrEmpty())
                0
            else it.size
        }
        val currthemeFit = GsonUtils.fromJson<MutableList<String>>(currFormation?.themeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
        val currsousThemeFit = GsonUtils.fromJson<MutableList<String>>(currFormation?.sousThemeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
        val currtypeF = listTypeFormation?.filter { currFormation?.typeFormationStr?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
        val currthemeF = listThemeFormation?.filter {  currthemeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
        val currsousThemeF = listSousThemeFormation?.filter { currsousThemeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }

        Commons.setListenerForSpinner(this,
            getString(R.string.selectionner_la_formation),
            getString(R.string.la_liste_des_formations_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (formationList?.size!! > 0) false else true,
            currentVal = "Date de formation: ${currFormation?.multiStartDate}\nParticipants: ${currparticipCount}\nModules: \n${currtypeF?.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${currtypeF?.size}"}\nThemes: \n${currthemeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${currthemeF?.size}"}\nSous Themes: \n${currsousThemeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${currsousThemeF?.size}"}",
            spinner = selectFormationVisitForm,
            listIem = formationList?.map { formM ->
                val participCount = GsonUtils.fromJson<MutableList<String>>(formM.producteursIdStr, object : TypeToken<MutableList<String>>(){}.type).let {
                    if(it.isNullOrEmpty())
                        0
                    else it.size
                }
                val themeFit = GsonUtils.fromJson<MutableList<String>>(formM.themeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val sousThemeFit = GsonUtils.fromJson<MutableList<String>>(formM.sousThemeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val typeF = listTypeFormation?.filter { formM.typeFormationStr.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val themeF = listThemeFormation?.filter {  themeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val sousThemeF = listSousThemeFormation?.filter { sousThemeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                "Date de formation: ${formM.multiStartDate}\nParticipants: ${participCount}\nModules: \n${typeF?.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${typeF?.size}"}\nThemes: \n${themeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${themeF?.size}"}\nSous Themes: \n${sousThemeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${sousThemeF?.size}"}"
            }.toList() ?: listOf(),
            onChanged = {

                val formation = formationList!![it]
                //ogUtils.d(section)
                val participCount = GsonUtils.fromJson<MutableList<String>>(formation.producteursIdStr, object : TypeToken<MutableList<String>>(){}.type).let {
                    if(it.isNullOrEmpty())
                        0
                    else it.size
                }
                val themeFit = GsonUtils.fromJson<MutableList<String>>(formation.themeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val sousThemeFit = GsonUtils.fromJson<MutableList<String>>(formation.sousThemeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val typeF = listTypeFormation?.filter { formation.typeFormationStr.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val themeF = listThemeFormation?.filter { themeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val sousThemeF = listSousThemeFormation?.filter { sousThemeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }

                formationCommon.nom = "Date de formation: ${formation.multiStartDate}\nParticipants: ${participCount}\nModules: \n${typeF.toModifString(true, "\n", "-")}\nThemes: \n${themeF.toModifString(true, "\n", "-")}\nSous Themes: \n${sousThemeF.toModifString(true, "\n", "-")}"
                editCurrentFormVisitForm.setText(formationCommon.nom)
                formation.isSynced?.let {
                    if(it){
                        formationCommon.id = formation.id!!
                    }else formationCommon.id = formation.uid
                }

            },
            onSelected = { itemId, visibility ->

            })

        Commons.setListenerForSpinner(this,
            getString(R.string.representez_vous_un_producteur),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectReprProducteurVisitForm,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.quel_est_leur_lien),
            getString(R.string.la_liste_des_l_ments_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectLienParentVisitForm,
            currentVal = visiteurFormationDrafted.lien,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (resources.getStringArray(R.array.parentAffiliation)?.toList() ?: listOf()) ,
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreLienParentVisitForm.visibility = visibility
            })

        passSetupVisiteurModel(visiteurFormationDrafted)
    }

    private fun draftData(dataDraftedModel: DataDraftedModel) {
        val itemModelOb = getVisiteurFormationObjet(false, necessaryItem = mutableListOf(
            "Selectionner la formation"
        ))

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
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {

                ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = dataDraftedModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(formationModelDraft),
                        typeDraft = "visiteur_formation",
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

    private fun setAllSelection() {

        setupSectionSelection()

        val listTypeFormation = ProgBandRoomDatabase.getDatabase(this)?.typeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listThemeFormation = ProgBandRoomDatabase.getDatabase(this)?.themeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listSousThemeFormation = ProgBandRoomDatabase.getDatabase(this)?.sousThemeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val typeTok = object : TypeToken<MutableList<String>>(){}.type
        val formationList = formationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        LogUtils.d(formationList)
        Commons.setListenerForSpinner(this,
            getString(R.string.selectionner_la_formation),
            getString(R.string.la_liste_des_formations_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (formationList?.size!! > 0) false else true,
            spinner = selectFormationVisitForm,
            listIem = formationList?.map { formM ->
                val participCount = GsonUtils.fromJson<MutableList<String>>(formM.producteursIdStr, object : TypeToken<MutableList<String>>(){}.type).let {
                    if(it.isNullOrEmpty())
                        0
                    else it.size
                }
                val themeFit = GsonUtils.fromJson<MutableList<String>>(formM.themeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val sousThemeFit = GsonUtils.fromJson<MutableList<String>>(formM.sousThemeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val typeF = listTypeFormation?.filter { formM.typeFormationStr.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val themeF = listThemeFormation?.filter {  themeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val sousThemeF = listSousThemeFormation?.filter { sousThemeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                "Date de formation: ${formM.multiStartDate}\nParticipants: ${participCount}\nModules: \n${typeF?.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${typeF?.size}"}\nThemes: \n${themeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${themeF?.size}"}\nSous Themes: \n${sousThemeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${sousThemeF?.size}"}"
            }.toList() ?: listOf(),
            onChanged = {

                val formation = formationList!![it]
                //ogUtils.d(section)
                val participCount = GsonUtils.fromJson<MutableList<String>>(formation.producteursIdStr, object : TypeToken<MutableList<String>>(){}.type).let {
                    if(it.isNullOrEmpty())
                        0
                    else it.size
                }
                val themeFit = GsonUtils.fromJson<MutableList<String>>(formation.themeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val sousThemeFit = GsonUtils.fromJson<MutableList<String>>(formation.sousThemeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
                val typeF = listTypeFormation?.filter { formation.typeFormationStr.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val themeF = listThemeFormation?.filter { themeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
                val sousThemeF = listSousThemeFormation?.filter { sousThemeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }

                formationCommon.nom = "Date de formation: ${formation.multiStartDate}\nParticipants: ${participCount}\nModules: \n${typeF.limitListByCount(2).toModifString(true, "\n", "-")}\nThemes: \n${themeF.limitListByCount(2).toModifString(true, "\n", "-")}\nSous Themes: \n${sousThemeF.limitListByCount(2).toModifString(true, "\n", "-")}"
                editCurrentFormVisitForm.setText(formationCommon.nom)
                formation.isSynced?.let {
                    if(it){
                        formationCommon.id = formation.id!!
                    }else formationCommon.id = formation.uid
                }

            },
            onSelected = { itemId, visibility ->

            })

        Commons.setListenerForSpinner(this,
            getString(R.string.representez_vous_un_producteur),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectReprProducteurVisitForm,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
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
            getString(R.string.quel_est_leur_lien),
            getString(R.string.la_liste_des_l_ments_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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

//        Commons.printModelValue(formationModel as Object, mapEntries)
//        Commons.debugModelToJson(formationModel)

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
                message = getString(R.string.le_champ_intitul_n_est_pas_renseign, field.first)
                isMissing = true
                break
            }
        }

        for (field in allField){
            LogUtils.d(field.second, field.second.isNullOrBlank(), necessaryItem.contains(field.first))
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