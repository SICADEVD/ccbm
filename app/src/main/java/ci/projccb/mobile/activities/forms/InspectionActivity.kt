package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.InspectionPreviewActivity
import ci.projccb.mobile.adapters.QuestionnaireReviewAdapter
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.interfaces.SectionCallback
import ci.projccb.mobile.itemviews.RecyclerItemDecoration
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_calcul_estimation.imageDraftBtn
import kotlinx.android.synthetic.main.activity_evaluation.*
import kotlinx.android.synthetic.main.activity_parcelle.selectLocaliteParcelle
import kotlinx.android.synthetic.main.activity_parcelle.selectProducteurParcelle
import kotlinx.android.synthetic.main.activity_parcelle.selectSectionParcelle
import kotlinx.android.synthetic.main.activity_producteur_menage.clickCloseBtn
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.HashMap

class InspectionActivity : AppCompatActivity(), SectionCallback,
    RecyclerItemListener<QuestionResponseModel> {


    private var encadreurId: String? = null
    private var encadreurNomPrenoms: String? = null
    private var encadreurList: MutableList<DelegueModel>? = null
    var localitesList: MutableList<LocaliteModel>? = mutableListOf()
    private var producteursList: MutableList<ProducteurModel> = mutableListOf()
    private var campagnesList: MutableList<CampagneModel> = mutableListOf()
    var cQuestionnaires: MutableList<InspectionQuestionnairesModel>? = mutableListOf()
    //  var cQuestionnaireAdapter: QuestionnaireAdapter? = null
    var cQuestionnaireAdapter: QuestionnaireReviewAdapter? = null
    var cQuestionnairesMap: MutableList<HashMap<String, String>>? = mutableListOf()
    var cQuestionnairesReviewList: MutableList<QuestionResponseModel>? = mutableListOf()

    var producteurNomPrenoms = ""
    var producteurId = ""

    var localiteNom = ""
    var localiteId = ""

    var campagneLabel = ""
    var campagneId = ""

    var dateInspection = ""
    var draftedDataInspection: DataDraftedModel? = null
    var fromAction = ""

    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();
    val encadreurCommon = CommonData();

    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {
        var sectionDao = CcbRoomDatabase.getDatabase(applicationContext)?.sectionsDao();
        var sectionList = sectionDao?.getAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_section),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = sectionList?.filter { it.id == currVal?.toInt() }?.map { it.libelle }?.let{
                if (it.size > 0) {
                    it[0]
                } else {
                    ""
                }
            } ?: "" ,
            spinner = selectSectionInspection,
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
            spinner = selectLocaliteInspection,
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
                if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurInspection,
            listIem = producteursList?.map { "${ it.nom } ${ it.prenoms }" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)

                    var listCertif: ProducteurModel? = null

                    if (producteur.isSynced) {
                        producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                        producteurCommon.id = producteur.id
                        listCertif = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteur(producteurID = producteur.id)
                    } else {
                        producteurId = producteur.uid.toString()
                        producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                        producteurCommon.id = producteur.uid
                        listCertif = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteurByUID(producteurUID = producteur.uid)
                    }

                        LogUtils.d(listCertif?.certification?.split(",")?.filter { currVal3 == it }?.toString())

                        Commons.setListenerForSpinner(this@InspectionActivity,
                            getString(R.string.inspection_text),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                            spinner = selectCertifInspection,
                            currentVal = listCertif?.certification?.split(",")?.filter { currVal3 == it }?.toString(),
                            listIem = listCertif?.certification?.split(",")
                                ?.toList() ?: listOf(),
                            onChanged = {

                                if(draftedDataInspection == null){
                                    val certificat = listCertif?.certification?.split(",")?.get(it).toString()

                                    if(certificat.isNullOrEmpty() == false) {
                                        cQuestionnairesReviewList?.clear()
                                        recyclerQuesionnairesInspection.adapter?.notifyDataSetChanged()
                                        fetchQuestionnairesReview(false, certificat ?: "")
                                    }
                                }else{

                                    val inpectDraft = ApiClient.gson.fromJson(draftedDataInspection?.datas, InspectionDTO::class.java)
                                    if(inpectDraft.producteursId == producteurCommon.id.toString()){
                                        val certif = inpectDraft.certificatStr
                                        LogUtils.d(certif)
                                        if(certif.isNullOrEmpty() == false) {
                                            cQuestionnairesReviewList?.clear()
                                            recyclerQuesionnairesInspection.adapter?.notifyDataSetChanged()
                                            fetchQuestionnairesReview(true, certif ?: "")
                                        }
                                        val cQuestionnairesReviewList: MutableList<QuestionResponseModel> = GsonUtils.fromJson(inpectDraft.reponseStringify, object : TypeToken<MutableList<QuestionResponseModel>>(){}.type)
                                        (recyclerQuesionnairesInspection.adapter as QuestionnaireReviewAdapter).setListQuestion(cQuestionnairesReviewList?.toMutableList()?: arrayListOf())
                                    }

                                }

                            },
                            onSelected = { itemId, visibility ->
                            })

                    //setupPa
                }


            },
            onSelected = { itemId, visibility ->

            })

    }


    fun configDate(viewClciked: AppCompatEditText) {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, _, _, day ->
            viewClciked.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
        }, year, month, dayOfMonth)

        datePickerDialog.datePicker.maxDate = DateTime.now().millis
        datePickerDialog.show()
    }


    fun fetchQuestionnairesReview(fromDraft: Boolean, fromCertificat: String) {

        if (!fromDraft) {
            cQuestionnaires = CcbRoomDatabase.getDatabase(this)?.questionnaireDao()?.getAll()

            val mQuestionsToken = object : TypeToken<MutableList<QuestionModel>>(){}.type

            val questionResponseList = mutableListOf<QuestionResponseModel>()

            var questionNumber = 0
            for (i in 1..(cQuestionnaires ?: mutableListOf()).size) {
                questionNumber += 1

                val questionResponseTitleModel = QuestionResponseModel(
                    id = questionNumber.toString(),
                    label = cQuestionnaires!![i - 1].titre!!,
                    note = "",
                    reponseId = 0,
                    isTitle = true
                )

//                LogUtils.d(cQuestionnaires!![i - 1].questionnairesStringify, fromCertificat)
                val questList = GsonUtils.fromJson<MutableList<QuestionModel>>(cQuestionnaires!![i - 1].questionnairesStringify, object : TypeToken<MutableList<QuestionModel>>(){}.type)
                val listCertName = questList.map { it.certificat }.toList()

                if(questList.isNotEmpty()){
                    if(listCertName.contains(fromCertificat) == true){
                        questionResponseList.add(questionResponseTitleModel)
                        cQuestionnairesReviewList?.add(questionResponseTitleModel)

                        val mQuestionsInfos: MutableList<QuestionModel> =  ApiClient.gson.fromJson(cQuestionnaires!![i - 1].questionnairesStringify!!, mQuestionsToken)

//                        if(draftedDataInspection != null){
//                            val inpectDraft = ApiClient.gson.fromJson(draftedDataInspection?.datas, InspectionDTO::class.java)
//                        }

                        for (questionIndex in 0 until mQuestionsInfos.size) {
                            questionNumber += 1

                            val questionResponseInfoModel = QuestionResponseModel(
                                id = questionNumber.toString(),
                                label = mQuestionsInfos[questionIndex].libelle!!,
                                note = "",
                                reponseId = 0,
                                isTitle = false
                            )

                            questionResponseList.add(questionResponseInfoModel)
                            cQuestionnairesReviewList?.add(questionResponseInfoModel)
                        }
                    }
                }

            }
        }

        initRvList(cQuestionnairesReviewList)
    }

    private fun initRvList(cQuestionnairesReviewList: MutableList<QuestionResponseModel>?) {
        val notationsList = mutableListOf<NotationModel>()
        notationsList.add(NotationModel(id = 0, uid = 0, nom = "Choisir la note", point = 0))
        notationsList.addAll(
            AssetFileHelper.getListDataFromAsset(19, this@InspectionActivity) as MutableList<NotationModel>
            //CcbRoomDatabase.getDatabase(this)?.notationDao()?.getAll()!!
        )

        cQuestionnaireAdapter = QuestionnaireReviewAdapter(this, cQuestionnairesReviewList!!, notationsList)
        recyclerQuesionnairesInspection.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerQuesionnairesInspection.adapter = cQuestionnaireAdapter

        cQuestionnaireAdapter?.questionsListener = this

        val recyclerDecoration = RecyclerItemDecoration(this, 40, true, this)
        recyclerQuesionnairesInspection.addItemDecoration(recyclerDecoration)
    }


    /*fun fetchQuestionnaires() {
        cQuestionnaires = CcbRoomDatabase.getDatabase(this)?.questionnaireDao()?.getAll()

        val mQuestionsToken = object : TypeToken<MutableList<QuestionModel>>(){}.type
        val mutableData: MutableMap<String, String> = mutableMapOf()

        for (i in 1..(cQuestionnaires ?: mutableListOf()).size) {
            val mQuestionMap = hashMapOf<String, String>()
            mQuestionMap["Title"] = cQuestionnaires!![i - 1].titre!!
            cQuestionnairesMap?.add(mQuestionMap)

            val mQuestionsInfos: MutableList<QuestionModel> =  ApiClient.gson.fromJson(cQuestionnaires!![i - 1].questionnairesStringify!!, mQuestionsToken)

            for (questionIndex in 0 until mQuestionsInfos.size) {
                val mQuestionInfosMap = hashMapOf<String, String>()
                mQuestionInfosMap["${mQuestionsInfos[questionIndex].id}"] = mQuestionsInfos[questionIndex].libelle!!
                mutableData[mQuestionsInfos[questionIndex].id.toString()] = ""
                cQuestionnairesMap?.add(mQuestionInfosMap)
            }
        }

        cQuestionnaireAdapter = QuestionnaireAdapter(this, cQuestionnairesMap!!, mutableData)
        recyclerQuesionnairesInspection.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerQuesionnairesInspection.adapter = cQuestionnaireAdapter

        val recyclerDecoration = RecyclerItemDecoration(this, 40, true, this)
        recyclerQuesionnairesInspection.addItemDecoration(recyclerDecoration)
    }*/


    override fun isSectionHeader(position: Int): Boolean {
        return position == 0 || cQuestionnairesReviewList?.get(position)?.isTitle!!
        // return position == 0 || cQuestionnairesMap?.get(position)?.get("Title") != cQuestionnairesMap?.get(position - 1)?.get("Title")
    }


    override fun getSectionHeaderName(postion: Int): String {
        val mQuestionMapped = hashMapOf<String, String>()
        return mQuestionMapped["Title"] ?: "Size"
    }

    fun setupEncareurSelection(currVal: String? = null){

        val encadreurList = CcbRoomDatabase.getDatabase(applicationContext)?.staffFormation()?.getAll( agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString() )

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_l_encadreur),
            getString(R.string.la_liste_des_encadreurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (encadreurList?.size!! > 0) false else true,
            currentVal = encadreurList.filter { it.id == currVal?.toInt() }?.map { "${it.firstname} ${it.lastname}" }?.let{
                if (it.size > 0) {
                    it[0]
                } else {
                    ""
                }
            } ?: "",
            spinner = selectEncadreurList,
            listIem = encadreurList?.map { "${ it.firstname } ${ it.lastname }" }
                ?.toList() ?: listOf(),
            onChanged = {

                encadreurList?.let { list ->
                    var encadr = list.get(it)
                    encadreurCommon.nom = "${encadr.firstname!!} ${encadr.lastname!!}"
                    encadreurCommon.id = encadr.id!!

                    //setupPa
                }


            },
            onSelected = { itemId, visibility ->

            })

    }


    fun draftInspection(draftModel: DataDraftedModel?) {
        val itemModelOb = getInspectObjet(false)
        if(itemModelOb == null) return
        val draftInsoection = itemModelOb?.first.apply {
            section = sectionCommon.id.toString()
            localiteId = localiteCommon.id.toString()
            producteursId = producteurCommon.id.toString()
            encadreur = encadreurCommon.id.toString()
            noteInspection = cQuestionnairesReviewList?.filter { it.isTitle == false }?.map { it.note?.toInt() ?: 0 }?.sum()?.let {
                if(it > 0) {
                    it.div(cQuestionnairesReviewList?.filter { it.isTitle == false }?.size ?: 1).toString()
                } else {
                    "0"
                }
            }
            total_question = cQuestionnairesReviewList?.filter { it.isTitle == false }?.size.toString()
            total_question_conforme = cQuestionnairesReviewList?.filter { it.isTitle == false && it.note == "1" }?.size.toString()
            total_question_non_conforme = cQuestionnairesReviewList?.filter { it.isTitle == false && it.note == "-1" }?.size.toString()
            total_question_non_applicable = cQuestionnairesReviewList?.filter { it.isTitle == false && it.note == "0" }?.size.toString()

        }

        LogUtils.d(draftInsoection)

        showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                try {
                    CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                        DataDraftedModel(
                            uid = draftModel?.uid ?: 0,
                            datas = ApiClient.gson.toJson(draftInsoection),
                            typeDraft = "inspection",
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
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            },
            positive = getString(R.string.oui),
            deconnec = false,
            showNo = true
        )
    }


    fun collectDatas() {
//        if (producteurId.isEmpty()) {
//            showMessage(
//                message = "Selectionnez le producteur, svp !",
//                context = this,
//                finished = false,
//                callback = {},
//                positive = getString(R.string.ok),
//                deconnec = false,
//                showNo = false
//            )
//            return
//        }
//
//        if (campagneId.isEmpty()) {
//            showMessage(
//                message = "Selectionnez la campagne, svp !",
//                context = this,
//                finished = false,
//                callback = {},
//                positive = getString(R.string.ok),
//                deconnec = false,
//                showNo = false
//            )
//            return
//        }

        // dateInspection = DateTime.now().toString(DateTimeFormat.forPattern("dd-MM-yyyy"))

        dateInspection = editDateInspection.text.toString().trim()

        if (dateInspection.isEmpty()) {
            showMessage(
                message = getString(R.string.selectionnez_la_date_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        val itemModelOb = getInspectObjet()
        if(itemModelOb == null) return
        val questionnaireDto = itemModelOb?.first.apply {
            section = sectionCommon.id.toString()
            localiteId = localiteCommon.id.toString()
            producteursId = producteurCommon.id.toString()
            encadreur = encadreurCommon.id.toString()
            noteInspection = cQuestionnairesReviewList?.filter { it.isTitle == false }?.map { it.note?.toInt() ?: 0 }?.sum()?.let {
                if(it > 0) {
                    it.div(cQuestionnairesReviewList?.filter { it.isTitle == false }?.size ?: 1).toString()
                } else {
                    "0"
                }
            }
            total_question = cQuestionnairesReviewList?.filter { it.isTitle == false }?.size.toString()
            total_question_conforme = cQuestionnairesReviewList?.filter { it.isTitle == false && it.note == "1" }?.size.toString()
            total_question_non_conforme = cQuestionnairesReviewList?.filter { it.isTitle == false && it.note == "-1" }?.size.toString()
            total_question_non_applicable = cQuestionnairesReviewList?.filter { it.isTitle == false && it.note == "0" }?.size.toString()
        }

        itemModelOb?.second.apply {
            cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
                if (questionResponseModel.label.isNullOrEmpty() == false) {
                    this?.add(
                        "${questionResponseModel.label}" to
                                "${questionResponseModel.noteLabel}"
                    )
                }
            }
        }

        //Commons.printModelValue(questionnaireDto as Object, (itemModelOb.second as List<MapEntry>?))

        var quizCount = 0

        for (questionResponse in (cQuestionnairesReviewList ?: mutableListOf())) {
            if (questionResponse.reponseId == 0 && questionResponse.isTitle == false) {
                quizCount += 1
            }
        }

        //LogUtils.e(Commons.TAG, quizCount)

        if (quizCount > 4) {
            showMessage(
                message = getString(R.string.renseignez_les_questionnaires_svp_vous_etes)+"${cQuestionnairesReviewList?.size?.minus(quizCount)}/${cQuestionnairesReviewList?.size?.minus(4)}",
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        val intentInspectionPreview = Intent(this, InspectionPreviewActivity::class.java)
        intentInspectionPreview.putExtra("preview", questionnaireDto)
        intentInspectionPreview.putExtra("draft_id", draftedDataInspection?.uid)
        ActivityUtils.startActivity(intentInspectionPreview)
    }

    private fun getInspectObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()):  Pair<InspectionDTO, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        var itemList = getSetupInspectModel(InspectionDTO(
            uid = 0,
            id = 0,
            encadreur = encadreurCommon.id.toString(),
            localiteId = localiteCommon.id.toString(),
            section = sectionCommon.id.toString(),
            dateEvaluation = dateInspection,
            formateursId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            producteursId = producteurCommon.id.toString(),
            producteurNomPrenoms = producteurCommon.nom.toString(),
            reponseStringify = ApiClient.gson.toJson(cQuestionnairesReviewList),
            origin = "local",
            isSynced = false,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        ), mutableListOf<Pair<String,String>>())

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

    fun getSetupInspectModel(
        prodModel: InspectionDTO,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<InspectionDTO, MutableList<Pair<String, String>>> {
        val mainLayout = findViewById<ViewGroup>(R.id.container_inspection)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupInspectModel(
        prodModel: InspectionDTO?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.container_inspection)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        val inspectionDrafted = ApiClient.gson.fromJson(draftedData.datas, InspectionDTO::class.java)

        // Localite
        setupSectionSelection(inspectionDrafted.section ?: "",inspectionDrafted.localiteId ?: "", inspectionDrafted.producteursId ?: "", inspectionDrafted.certificatStr ?: "")
        setupEncareurSelection(inspectionDrafted.encadreur ?: "")

        editDateInspection.setText(inspectionDrafted.dateEvaluation)

        val mQuestionsReviewToken = object : TypeToken<MutableList<QuestionResponseModel>>(){}.type
        cQuestionnairesReviewList = GsonUtils.fromJson(inspectionDrafted.reponseStringify, mQuestionsReviewToken)

        //LogUtils.d(inspectionDrafted.certificatStr.toString())
        //fetchQuestionnairesReview(true, inspectionDrafted.certificatStr.toString())

//        val inspectionsToken = object : TypeToken<MutableList<QuestionResponseModel>>(){}.type
//        var counter = 0;
//        ApiClient.gson.fromJson<MutableList<QuestionResponseModel>>(inspectionDrafted.reponseStringify, inspectionsToken).map {
//            if(it.isTitle == false) {
//                inspectionDrafted.reponse[counter.toString()] = it.note!!
//                counter++
//            }
//        }

//        initRvList(cQuestionnairesReviewList)
//
        //(recyclerQuesionnairesInspection.adapter as QuestionnaireReviewAdapter).setListQuestion(cQuestionnairesReviewList?.toMutableList()?: arrayListOf())

        passSetupInspectModel(inspectionDrafted)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageDraftBtn.setOnClickListener {
            draftInspection(draftedDataInspection ?: DataDraftedModel(uid = 0))
        }

        clickSaveInspection.setOnClickListener {
            collectDatas()
        }

        clickCancelInspection.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        editDateInspection.setOnClickListener {
            configDate(editDateInspection)
        }

        Commons.setListenerForSpinner(this@InspectionActivity,
            getString(R.string.choix_du_ou_des_certificats),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectCertifInspection,
            listIem = arrayListOf(),
            onChanged = {

//                val certificat = listCertif?.certification?.split(",")?.get(it).toString()
//
//                if(certificat.isNullOrEmpty() == false) {
//                    cQuestionnairesReviewList?.clear()
//                    recyclerQuesionnairesInspection.adapter?.notifyDataSetChanged()
//                    fetchQuestionnairesReview(false, certificat ?: "")
//                }


            },
            onSelected = { itemId, visibility ->
            })

        if (intent.getStringExtra("from") != null) {
            LogUtils.e("From draft")
            fromAction = intent.getStringExtra("from") ?: ""
            draftedDataInspection = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataInspection!!)
        } else {
            setAllSelection()
        }
    }

    private fun setAllSelection() {

        setupSectionSelection()

        setupEncareurSelection()

    }


    override fun itemClick(item: QuestionResponseModel) {
    }


    override fun itemSelected(position: Int, item: QuestionResponseModel) {
        cQuestionnairesReviewList!![position] = item
//        LogUtils.d("${position} : ${item.noteLabel}, ${item.reponseId}")
    }
}
