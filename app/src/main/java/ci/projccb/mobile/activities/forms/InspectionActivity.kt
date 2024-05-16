package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.InspectionPreviewActivity
import ci.projccb.mobile.activities.infospresenters.InspectionPreviewUpdateActivity
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
import ci.projccb.mobile.tools.Commons.Companion.convertDate
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.toCheckEmptyItem
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.LoadProgressListener
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.*
import com.google.gson.reflect.TypeToken
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import kotlinx.android.synthetic.main.activity_calcul_estimation.imageDraftBtn
import kotlinx.android.synthetic.main.activity_evaluation.*
import kotlinx.android.synthetic.main.activity_parcelle.editNbrCacaoHecParcelle
import kotlinx.android.synthetic.main.activity_parcelle.labelTitleMenuAction
import kotlinx.android.synthetic.main.activity_producteur_menage.clickCloseBtn
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.HashMap

class InspectionActivity : AppCompatActivity(), SectionCallback,
    RecyclerItemListener<QuestionResponseModel> {


    private var commomUpdate: CommonData = CommonData()
    private var dateInspectionParm: String? = null
    private var counterMainView: Int = 0
    private var snackProgressBarManager: SnackProgressBarManager? = null
    private var inspectionData: MutableList<InspectionDTO>? = null
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
    val parcelleCommon = CommonData();
    val encadreurCommon = CommonData();

    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null, currVal4: String? = null) {
        var sectionDao = CcbRoomDatabase.getDatabase(applicationContext)?.sectionsDao();
        var sectionList = sectionDao?.getAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_section),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = sectionList?.filter { it.id.toString() == currVal }?.map { it.libelle }?.let{
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

                setLocaliteSpinner(sectionCommon.id!!, currVal1, currVal2, currVal3, currVal4)

            },
            onSelected = { itemId, visibility ->

            })
    }

    fun setLocaliteSpinner(id: Int, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null, currVal4: String? = null) {

        var localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa();
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
            spinner = selectLocaliteInspection,
            listIem = localitesListi?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                localitesListi?.let { list ->
                    var localite = list.get(it)
                    localiteCommon.nom = localite.nom!!
                    localiteCommon.id = localite.id!!

                    setupProducteurSelection(localiteCommon.id!!, currVal2, currVal3, currVal4)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupProducteurSelection(id: Int, currVal2: String? = null, currVal3: String? = null, currVal4: String? = null) {
        val producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if (it.uid.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                } else {
                    if (it.id.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }
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

                            setupParcelleSelection(producteurCommon.id.toString(), currVal4)

//                            LogUtils.d(listCertif?.certification?.split(",")?.filter { currVal3 == it }?.toModifString())

                            Commons.setListenerForSpinner(this@InspectionActivity,
                                getString(R.string.inspection_text),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                                spinner = selectCertifInspection,
                                currentVal = listCertif?.certification?.split(",")?.filter { currVal3 == it }?.let {
                                    if(it.isNotEmpty())
                                        it.first()
                                    else ""
                                },
                                listIem = listCertif?.certification?.split(",")
                                    ?.toList() ?: listOf(),
                                onChanged = {

                                    if(intent.getIntExtra("sync_uid",  0) == 0){
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
                                                //LogUtils.d(certif)
                                                if(certif.isNullOrEmpty() == false) {
                                                    cQuestionnairesReviewList?.clear()
                                                    recyclerQuesionnairesInspection.adapter?.notifyDataSetChanged()
                                                    fetchQuestionnairesReview(true, certif ?: "")
                                                }
                                                val cQuestionnairesReviewList: MutableList<QuestionResponseModel> = GsonUtils.fromJson(inpectDraft.reponseStringify, object : TypeToken<MutableList<QuestionResponseModel>>(){}.type)
                                                (recyclerQuesionnairesInspection.adapter as QuestionnaireReviewAdapter).setListQuestion(cQuestionnairesReviewList?.toMutableList()?: arrayListOf())
                                            }

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

    fun setupParcelleSelection(producteurId: String?, currVal3: String? = null) {
        var parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
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
            getString(R.string.choisir_sa_parcelle_concern_e),
            getString(R.string.la_liste_des_parcelles_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectParcelleInspection,
            listIem = parcellesList?.map { Commons.getParcelleNotSyncLibel(it) }
                ?.toList() ?: listOf(),
            onChanged = {

                parcellesList?.let { list ->
                    var parcelle = list.get(it)
                    parcelleCommon.nom = Commons.getParcelleNotSyncLibel(parcelle)

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
                questionResponseList.add(questionResponseTitleModel)
                cQuestionnairesReviewList?.add(questionResponseTitleModel)

                val questList = GsonUtils.fromJson<MutableList<QuestionModel>>(cQuestionnaires!![i - 1].questionnairesStringify, object : TypeToken<MutableList<QuestionModel>>(){}.type)
                val listCertName = questList.map { it.certificat }.toList()

                questList.forEach { dbQuestion ->
                    if(listCertName.contains(fromCertificat) == true){

                        questionNumber += 1

                        val questionResponseInfoModel = QuestionResponseModel(
                            id = questionNumber.toString(),
                            label = dbQuestion.libelle!!,
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

        initRvList(cQuestionnairesReviewList)
    }

    fun fetchQuestionnairesReviewUpdate(questionResponseModelList: MutableList<QuestionResponseModel>, fromCertif:String? = null) {

        cQuestionnaires = CcbRoomDatabase.getDatabase(this)?.questionnaireDao()?.getAll()

        val listIdNoConforme = questionResponseModelList.map { "${ it.id }" }

        val mQuestionsToken = object : TypeToken<MutableList<QuestionModel>>(){}.type

        val questionResponseList = mutableListOf<QuestionResponseModel>()

        var questionNumber = 0
        for (i in 1..(cQuestionnaires ?: mutableListOf()).size) {
            questionNumber += 1

            val questions = cQuestionnaires!![i - 1]

            val questionResponseTitleModel = QuestionResponseModel(
                id = questionNumber.toString(),
                label = cQuestionnaires!![i - 1].titre!!,
                note = "",
                reponseId = 0,
                isTitle = true
            )

            cQuestionnairesReviewList?.add(questionResponseTitleModel)
            questionResponseList.add(questionResponseTitleModel)

            val questList = GsonUtils.fromJson<MutableList<QuestionModel>>(cQuestionnaires!![i - 1].questionnairesStringify, object : TypeToken<MutableList<QuestionModel>>(){}.type)
            //val listCertName = questList.map { it.certificat }.toList()

            questList.forEach { dbQuestion ->
                questionNumber += 1

                if(dbQuestion.certificat.equals(fromCertif, ignoreCase = true) && listIdNoConforme.contains(dbQuestion.id.toString())){

                    val indexList = listIdNoConforme.indexOf(dbQuestion.id.toString())
                    val questionFromServ = questionResponseModelList.get(indexList)

                    val questionResponseInfoModel = QuestionResponseModel(
                        id = questionNumber.toString(),
                        id_en_base = questionFromServ.id_en_base,
                        label = dbQuestion.libelle!!,
                        noteLabel = questionFromServ.noteLabel,
                        commentaireLast = questionFromServ.commentaireLast,
                        note = "-1",
                        reponseId = 1,
                        isTitle = false
                    )

//                    LogUtils.d(questionResponseInfoModel)

                    questionResponseList.add(questionResponseInfoModel)
                    cQuestionnairesReviewList?.add(questionResponseInfoModel)

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

        cQuestionnaireAdapter = QuestionnaireReviewAdapter(this, cQuestionnairesReviewList!!, notationsList, dateInspectionParm)
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
            currentVal = encadreurList.filter { it.id.toString() == currVal }?.map { "${it.firstname} ${it.lastname}" }?.let{
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
        val itemModelOb = getInspectObjet(false, necessaryItem = mutableListOf(
            "Choisir sa parcelle concernée"
        ))

        if(itemModelOb == null) return
        val draftInsoection = itemModelOb.first.apply {
            section = sectionCommon.id.toString()
            localiteId = localiteCommon.id.toString()
            producteursId = producteurCommon.id.toString()
            encadreur = encadreurCommon.id.toString()
            parcelle = parcelleCommon.id.toString()
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

            if(intent.getIntExtra("sync_uid", 0) != 0 || this.sync_update){
                this.id = commomUpdate.listOfValue?.first()?.toInt()
                this.uid = commomUpdate.listOfValue?.get(1)?.toInt()?:0
                this.sync_update = true
            }
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

        checkCommentField()

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
        val questionnaireDto = itemModelOb.first.apply {
            section = sectionCommon.id.toString()
            localiteId = localiteCommon.id.toString()
            producteursId = producteurCommon.id.toString()
            encadreur = encadreurCommon.id.toString()
            parcelle = parcelleCommon.id.toString()
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

        var allNamed : MutableList<Pair<String, String>> = mutableListOf()

        itemModelOb.second.forEach {
            allNamed.add(it)
        }

        itemModelOb.second.apply {
            cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
                if (questionResponseModel.label.isNullOrEmpty() == false) {
                    this.add(
                        "${questionResponseModel.label}" to
                                "${questionResponseModel.noteLabel}"
                    )
                }
            }
        }

        allNamed.apply {
            cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
                if (questionResponseModel.label.isNullOrEmpty() == false && questionResponseModel.isTitle == false) {
                    this?.add(
                        "${questionResponseModel.label}" to
                                "${questionResponseModel.noteLabel.toCheckEmptyItem()}"
                    )
                    //Log.i("INFO", "${questionResponseModel.label} - ${questionResponseModel.noteLabel.toCheckEmptyItem()} - ${questionResponseModel.noteLabel}")
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

        var dataCountKey = 0
        var dataCountVal = 0
        allNamed?.forEach {
            if(it.first.isNullOrEmpty() == false){
                dataCountKey++
                if(it.second.isNullOrBlank() == false) dataCountVal++
            }
            //Log.i("INFO", "${it.first} - ${it.second}")
        }
        LogUtils.d(dataCountKey, dataCountVal)
        Commons.showCircularIndicator(snackProgressBarManager = snackProgressBarManager, positionBario = Pair(dataCountKey, dataCountVal), displayId = 2512, buttonLib = "CONFIRMER", callback = {

            val intentInspectionPreview = Intent(this, InspectionPreviewActivity::class.java)
            intentInspectionPreview.putExtra("preview", questionnaireDto)
            intentInspectionPreview.putExtra("draft_id", draftedDataInspection?.uid)
            ActivityUtils.startActivity(intentInspectionPreview)

            Unit
        })

    }

    fun collectDatasUpdate(intExtraUid: Int) {

        //LogUtils.d(intExtraUid)
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

//        var listApprob = (AssetFileHelper.getListDataFromAsset(
//            29,
//            this
//        ) as MutableList<CommonData>)

        val itemModelOb = getInspectObjet(id = intExtraUid)
        //LogUtils.d(intExtraUid, itemModelOb)

        var dataInspectionUpdateDTO: InspectionUpdateDTO = InspectionUpdateDTO()
        var nonConformingResponse: NonConformingResponse = NonConformingResponse()
        val inspectId = itemModelOb?.first?.id
        val recommandations: MutableMap<String, String> = mutableMapOf()
        val delai: MutableMap<String, String> = mutableMapOf()
        val dateVerification: MutableMap<String, String> = mutableMapOf()
        val statuts: MutableMap<String, String> = mutableMapOf()

        cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
            if (questionResponseModel.label.isNullOrEmpty() == false && questionResponseModel.isTitle == false) {
                recommandations.put(questionResponseModel?.id_en_base.toString(), questionResponseModel.commentaire.toString())
                delai.put(questionResponseModel?.id_en_base.toString(), convertDate(questionResponseModel.delai, true))
                dateVerification.put(questionResponseModel?.id_en_base.toString(), convertDate(questionResponseModel.date_verification, true))
                statuts.put(questionResponseModel?.id_en_base.toString(), questionResponseModel.statuts.toString())
            }
        }
        nonConformingResponse = NonConformingResponse(inspectId?.toInt().toString(), recommandations, delai, dateVerification, statuts)
//        var indexApprob = "0"
////        LogUtils.d(listApprob, selectApprobationInspection.selectedItemPosition)
//        (listApprob.size > 0)?.let {
//            if(it == true) indexApprob =  (listApprob.filterIndexed { index, commonData -> commonData.id?.equals((selectApprobationInspection.selectedItemPosition)) == true }?.first()?.id.toString())
//        }
        dataInspectionUpdateDTO = InspectionUpdateDTO(inspectId.toString(), nonConformingResponse, null)

//        LogUtils.d(dataList)

        if(itemModelOb == null) return
        val questionnaireDto = itemModelOb.first.apply {
            section = sectionCommon.id.toString()
            localiteId = localiteCommon.id.toString()
            producteursId = producteurCommon.id.toString()
            encadreur = encadreurCommon.id.toString()
            parcelle = parcelleCommon.id.toString()
            noteInspection = itemModelOb?.first?.noteInspection
            total_question = itemModelOb?.first?.total_question
            total_question_conforme = itemModelOb?.first?.total_question_conforme
            total_question_non_conforme = itemModelOb?.first?.total_question_non_conforme
            total_question_non_applicable = itemModelOb?.first?.total_question_non_applicable
//            approbation = selectApprobationInspection.getSpinnerContent()?.let { content ->
//                var value: String? = "0"
//                if(content.equals("Faites un choix", ignoreCase = true) == false) value = listApprob?.filter { it.nom.equals(content, ignoreCase = true) == true }?.first()?.id?.toString()
//                value
//            }
            update_content = GsonUtils.toJson(dataInspectionUpdateDTO)
        }

        itemModelOb?.second.apply {

            this?.add(
                "Note" to "${itemModelOb?.first?.noteInspection}%"
            )
            this?.add(
                "Total" to "${itemModelOb?.first?.total_question}"
            )
            this?.add(
                "Total Conforme" to "${itemModelOb?.first?.total_question_conforme}"
            )
            this?.add(
                "Total Non Conforme" to "${itemModelOb?.first?.total_question_non_conforme}"
            )
            this?.add(
                "Total Non Applicable" to "${itemModelOb?.first?.total_question_non_applicable}"
            )
            cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
                if (questionResponseModel.label.isNullOrEmpty() == false && questionResponseModel.isTitle == false) {
                    this?.add(
                        "${questionResponseModel.label}" to
                                "Note: ${questionResponseModel.noteLabel}\nCommentaire: ${questionResponseModel.commentaire}\nLe delai: ${questionResponseModel.delai}\nDate verification: ${questionResponseModel.date_verification}\nStatus: ${questionResponseModel.statuts}\n"
                    )
                }
            }
//            this?.add(
//                "Approbation" to "${selectApprobationInspection.getSpinnerContent()}"
//            )
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.map { MapEntry(it.first, it.second) }

        var listData :MutableList<Pair<String, String>> = mutableListOf()
        cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
            if (questionResponseModel.label.isNullOrEmpty() == false && questionResponseModel.isTitle == false) {
                listData.add("Commentaire" to "${questionResponseModel.commentaire}")
                listData.add("Delai" to "${questionResponseModel.delai}")
                listData.add("Date" to "${questionResponseModel.date_verification}")
                listData.add("Status" to "${questionResponseModel.statuts}")
            }
        }

        var dataCountKey = 0
        var dataCountVal = 0
        listData?.forEach {
            if(it.first.isNullOrEmpty() == false){
                dataCountKey++
                if(it.second.isNullOrBlank() == false) dataCountVal++
            }
        }
        //LogUtils.json(itemModelOb?.second)
        if(dataCountKey>=dataCountVal && dataCountKey!=0){
//            val divide = (dataCountVal.toDouble()/dataCountKey.toDouble())
//            var tauxFif = (divide.times(100))
//            var positionBar = tauxFif
//            firstBarprogress.setProgressPercentage(positionBar.toDouble(), true)
//            firstBarprogress.showProgressText(true)
        }

        Commons.showCircularIndicator(snackProgressBarManager = snackProgressBarManager, positionBario = Pair(dataCountKey, dataCountVal), displayId = 2512, buttonLib = "CONFIRMER", callback = {

            val intentInspectionPreview = Intent(this, InspectionPreviewUpdateActivity::class.java)
            intentInspectionPreview.putExtra("preview", questionnaireDto)
            intentInspectionPreview.putExtra("previewitem", ArrayList(mapEntries))
            ActivityUtils.startActivity(intentInspectionPreview)

            Unit
        })

    }

    private fun getInspectObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf(), id:Int = 0):  Pair<InspectionDTO, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        var inpectMod = InspectionDTO(
            uid = 0,
            id = 0,
            encadreur = encadreurCommon.id.toString(),
            localiteId = localiteCommon.id.toString(),
            section = sectionCommon.id.toString(),
            dateEvaluation = dateInspection,
            formateursId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            producteursId = producteurCommon.id.toString(),
            parcelle = parcelleCommon.id.toString(),
            producteurNomPrenoms = producteurCommon.nom.toString(),
            parcelleLib = parcelleCommon.nom.toString(),
            reponseStringify = ApiClient.gson.toJson(cQuestionnairesReviewList),
            origin = "local",
            isSynced = false,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(id != 0){
            inpectMod = CcbRoomDatabase.getDatabase(this@InspectionActivity)?.inspectionDao()?.getByUid(id)?.first()!!
        }

        var itemList = getSetupInspectModel(inpectMod, mutableListOf<Pair<String,String>>())

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
        prodModel: InspectionDTO?,
        makeDisable: Boolean = false,
        ignoreDisable: MutableList<String> = arrayListOf()
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.container_inspection)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel, makeDisable, ignoreDisable)
        }
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        val inspectionDrafted = ApiClient.gson.fromJson(draftedData.datas, InspectionDTO::class.java)

        dateInspectionParm = inspectionDrafted.dateEvaluation

        if(inspectionDrafted.parcelle.equals("0")){
            var parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
                ?.getParcellesProducteur(producteurId = inspectionDrafted.producteursId.toString(), agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            inspectionDrafted.parcelle = parcellesList?.let { if(it.size>0) it.first().id.toString() else "0" }?.toString()
        }
        // Localite
        setupSectionSelection(inspectionDrafted.section ?: "",inspectionDrafted.localiteId ?: "", inspectionDrafted.producteursId ?: "", inspectionDrafted.certificatStr ?: "", inspectionDrafted.parcelle ?: "")
        setupEncareurSelection(inspectionDrafted.encadreur ?: "")

        editDateInspection.setText(inspectionDrafted.dateEvaluation)

        val mQuestionsReviewToken = object : TypeToken<MutableList<QuestionResponseModel>>(){}.type
        cQuestionnairesReviewList = GsonUtils.fromJson(inspectionDrafted.reponseStringify, mQuestionsReviewToken)

        passSetupInspectModel(inspectionDrafted)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)

        snackProgressBarManager = SnackProgressBarManager(findViewById(android.R.id.content), lifecycleOwner = this)
        snackProgressBarManager = Commons.defineSnackBarManager(snackProgressBarManager!!, linearActionContainerInspection, this)

        //counterMainView = 0

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

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
            if(intent.getIntExtra("sync_uid", 0) != 0){
                ActivityUtils.getActivityByContext(this)?.finish()
            }else {
                ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                ActivityUtils.getActivityByContext(this)?.finish()
            }
        }

        editDateInspection.setOnClickListener {
            configDate(editDateInspection)
        }

        Commons.addNotZeroAtFirstToET(editNbrProductionEvalBesoin)

        Commons.setListenerForSpinner(this@InspectionActivity,
            getString(R.string.choix_du_ou_des_certificats),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectCertifInspection,
            listIem = arrayListOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
            })

        if (intent.getStringExtra("from") != null) {

//            LogUtils.d(intent.getStringExtra("from"))
            if(intent.getIntExtra("drafted_uid", 0) != 0){
                LogUtils.e("From draft")
                fromAction = intent.getStringExtra("from") ?: ""
                draftedDataInspection = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataInspection!!)
                LogUtils.d(intent.getIntExtra("drafted_uid", 0) )
            }else{
                val inspectUid = intent.getIntExtra("sync_uid", 0)
                //LogUtils.d(inspectUid)
                if(inspectUid != 0) {
                    labelTitleMenuAction.text = "MISE A JOUR\n FICHE INSPECTION"
                    clickSaveInspection.setOnClickListener {
                        collectDatasUpdate(inspectUid)
                    }
//                    imageDraftBtn.visibility = View.GONE

                    inspectionData = CcbRoomDatabase.getDatabase(this)?.inspectionDao()?.getByUid(inspectUid)
                    inspectionData?.forEach {
                        lauchForUpdate(it)
                    }
                }
            }
        } else {
            setAllSelection()
        }

        var listenProgressOn = findViewById<ViewGroup>(android.R.id.content)

        if(intent.getIntExtra("sync_uid", 0) != 0){

        }else{

            Commons.setListenerForViewsChange(this, listenProgressOn, object :
                LoadProgressListener {
                override fun startLoadProgress(content: String) {

//                    LogUtils.d("CLICKIK")
                    if(intent.getIntExtra("sync_uid", 0) != 0){
                        var listData :MutableList<Pair<String, String>> = mutableListOf()
                        cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
                            if (questionResponseModel.label.isNullOrEmpty() == false && questionResponseModel.isTitle == false) {
                                listData.add("Commentaire" to "${questionResponseModel.commentaire}")
                                listData.add("Delai" to "${questionResponseModel.delai}")
                                listData.add("Date" to "${questionResponseModel.date_verification}")
                                listData.add("Status" to "${questionResponseModel.statuts}")
                            }
                        }

                        var dataCountKey = 0
                        var dataCountVal = 0
                        listData?.forEach {
                            if(it.first.isNullOrEmpty() == false){
                                dataCountKey++
                                if(it.second.isNullOrBlank() == false) dataCountVal++
                            }
                        }
                        //LogUtils.json(itemModelOb?.second)
                        if(dataCountKey>=dataCountVal && dataCountKey!=0){
                            val divide = (dataCountVal.toDouble()/dataCountKey.toDouble())
                            var tauxFif = (divide.times(100))
                            var positionBar = tauxFif
                            firstBarprogress.setProgressPercentage(positionBar.toDouble(), true)
                            firstBarprogress.showProgressText(true)
                        }
                    }else{

                        val itemModelOb = getInspectObjet(false)
                        itemModelOb?.second.apply {
                            cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
                                if (questionResponseModel.label.isNullOrEmpty() == false && questionResponseModel.isTitle == false) {
                                    this?.add(
                                        "${questionResponseModel.label}" to
                                                "${questionResponseModel.noteLabel.toCheckEmptyItem()}"
                                    )
                                }
                            }
                        }
                        var dataCountKey = 0
                        var dataCountVal = 0
                        itemModelOb?.second?.forEach {
                            if(it.first.isNullOrEmpty() == false){
                                dataCountKey++
                                if(it.second.isNullOrBlank() == false) dataCountVal++
                            }
                        }
                        //LogUtils.json(itemModelOb?.second)
                        if(dataCountKey>=dataCountVal && dataCountKey!=0){
                            val divide = (dataCountVal.toDouble()/dataCountKey.toDouble())
                            var tauxFif = (divide.times(100))
                            var positionBar = tauxFif
                            firstBarprogress.setProgressPercentage(positionBar.toDouble(), true)
                            firstBarprogress.showProgressText(true)
                        }
                    }

                }
            })
        }


    }

    fun checkCommentField() {
        val message = arrayListOf<String>()
        cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
            if(questionResponseModel.isTitle == false){
                if (questionResponseModel.note.equals("-1") && questionResponseModel.commentaire.toString().isNullOrEmpty()){
                    message.add("Le commentaire 'Pas conforme' de la question, ${questionResponseModel.label} n'est pas renseigné !")
                }
            }
        }

        if(message.size > 0){
            Commons.showMessage(
                message = message.toModifString(true, "\n"),
                context = this@InspectionActivity,
                finished = false,
                callback = {
                },
                positive = "Compris !",
                deconnec = false,
                textSizeDim = R.dimen._5ssp
            )
        }

    }

    fun updatProgressBar() {

//        checkCommentField()

        var listData :MutableList<Pair<String, String>> = mutableListOf()
        cQuestionnairesReviewList?.forEachIndexed { index, questionResponseModel ->
            if (questionResponseModel.label.isNullOrEmpty() == false && questionResponseModel.isTitle == false) {
                listData.add("Commentaire" to "${questionResponseModel.commentaire}")
                listData.add("Delai" to "${questionResponseModel.delai}")
                listData.add("Date" to "${questionResponseModel.date_verification}")
                listData.add("Status" to "${questionResponseModel.statuts}")
            }
        }

        var dataCountKey = 0
        var dataCountVal = 0
        listData?.forEach {
            if(it.first.isNullOrEmpty() == false){
                dataCountKey++
                if(it.second.isNullOrBlank() == false) dataCountVal++
            }
        }
        //LogUtils.json(itemModelOb?.second)
        if(dataCountKey>=dataCountVal && dataCountKey!=0){
            val divide = (dataCountVal.toDouble()/dataCountKey.toDouble())
            var tauxFif = (divide.times(100))
            var positionBar = tauxFif
            firstBarprogress.setProgressPercentage(positionBar.toDouble(), true)
            firstBarprogress.showProgressText(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        snackProgressBarManager?.disable()
    }

    private fun lauchForUpdate(inspectionDrafted: InspectionDTO) {

        val product = CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.getProducteurByID(inspectionDrafted.producteursId?.toInt()?:0)


        if(inspectionDrafted.sync_update){
            intent.putExtra("sync_uid", 1)
            labelTitleMenuAction.text = "MISE A JOUR FICHE INSPECTION"
        }
//        Commons.debugModelToJson(inspectionDrafted)
        dateInspectionParm = inspectionDrafted.dateEvaluation

        if(inspectionDrafted.parcelle.equals("0") || inspectionDrafted.parcelle == null){
            var parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
                ?.getParcellesProducteur(producteurId = inspectionDrafted.producteursId.toString(), agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            inspectionDrafted.parcelle = parcellesList?.let { if(it.size>0) it.first().id.toString() else "0" }?.toString()
//            LogUtils.d(parcellesList)
        }
        LogUtils.d(inspectionDrafted.parcelle, inspectionDrafted.producteursId)

        setupSectionSelection(product?.section ?: "",product?.localitesId ?: "", inspectionDrafted.producteursId ?: "", inspectionDrafted.certificatStr ?: "", inspectionDrafted.parcelle ?: "")
        setupEncareurSelection(inspectionDrafted.formateursId ?: "")

        editDateInspection.setText(inspectionDrafted.dateEvaluation)

        //val mQuestionsReviewToken = object : TypeToken<MutableList<QuestionResponseModel>>(){}.type
        val questionResponseModelList = mutableListOf<QuestionResponseModel>()
        val quest_non_conformeOrnon_applicTok = object : TypeToken<MutableList<QuestionnaireNoteModel>>(){}.type
        val questNonConformList = GsonUtils.fromJson<MutableList<QuestionnaireNoteModel>>(inspectionDrafted.reponse_non_conformeStr, quest_non_conformeOrnon_applicTok)
        val questNonApplicableList = GsonUtils.fromJson<MutableList<QuestionnaireNoteModel>>(inspectionDrafted.reponse_non_applicaleStr, quest_non_conformeOrnon_applicTok)

//        LogUtils.d(questNonConformList)
        questNonConformList?.let { item ->
            item.forEach {
                LogUtils.d(it.commentaire)
                questionResponseModelList.add(QuestionResponseModel(
                    id = it.questionnaire_id?.toString(),
                    id_en_base = it.id.toString(),
                    noteLabel = "Pas Conforme",
                    commentaireLast = it.commentaire?:"N/A"
                ))
            }
        }

        fetchQuestionnairesReviewUpdate(questionResponseModelList, inspectionDrafted.certificatStr)

        passSetupInspectModel(inspectionDrafted, true, arrayListOf("approbation"))

    }

    private fun setAllSelection() {

        setupSectionSelection()

        setupEncareurSelection()

    }


    override fun itemClick(item: QuestionResponseModel) {
    }


    override fun itemSelected(position: Int, item: QuestionResponseModel) {
        cQuestionnairesReviewList!![position] = item
        updatProgressBar()
//        LogUtils.d("${position} : ${item.noteLabel}, ${item.reponseId}")
    }
}
