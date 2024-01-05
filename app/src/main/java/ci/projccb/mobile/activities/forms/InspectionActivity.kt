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

                LogUtils.d(cQuestionnaires!![i - 1].questionnairesStringify)

                if(cQuestionnaires!![i - 1].questionnairesStringify?.contains(fromCertificat, ignoreCase = true) == true){
                    questionResponseList.add(questionResponseTitleModel)
                    cQuestionnairesReviewList?.add(questionResponseTitleModel)

                    val mQuestionsInfos: MutableList<QuestionModel> =  ApiClient.gson.fromJson(cQuestionnaires!![i - 1].questionnairesStringify!!, mQuestionsToken)

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


    fun setupProducteurSelection(localiteId: String) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = localiteId) ?: mutableListOf()

        if (producteursList.size == 0) {
            showMessage(
                "La liste des producteurs de cette Localité semble vide, veuillez procéder à la synchronisation des données svp.",
                this,
                finished = false,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false

            )
            return
        }

        val producteursDatas: MutableList<CommonData> = mutableListOf()

        producteursList.map {
            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
        }.let {
            producteursDatas.addAll(it)
        }

        val inspectionDraftedLocal = ApiClient.gson.fromJson(draftedDataInspection?.datas, InspectionDTO::class.java)
        selectProducteurInspection!!.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

        if (inspectionDraftedLocal != null) {
            provideDatasSpinnerSelection(
                selectProducteurInspection,
                inspectionDraftedLocal.producteurNomPrenoms,
                producteursDatas
            )
        }

        selectProducteurInspection.setTitle("Choisir le producteur")
        selectProducteurInspection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val producteur = producteursList[position]
                producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"

                if (producteur.isSynced) {
                    producteurId = producteur.id.toString()
                } else {
                    producteurId = producteur.uid.toString()
                }

                val listCertif = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteur(producteurID = producteurId.toInt())

                Commons.setListenerForSpinner(this@InspectionActivity,
                    "Choix du ou des certificats","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
                    spinner = selectCertifInspection,
                    isEmpty = listCertif?.certification?.split(",")
                        ?.toList()?.isEmpty() ?: true,
                    listIem = listCertif?.certification?.split(",")
                        ?.toList() ?: listOf(),
                    onChanged = {

                        val certificat = listCertif?.certification?.split(",")?.get(it).toString()

                        if(certificat.isNullOrEmpty() == false) {
                            cQuestionnairesReviewList?.clear()
                            recyclerQuesionnairesInspection.adapter?.notifyDataSetChanged()
                            fetchQuestionnairesReview(true, certificat ?: "")
                        }


                    },
                    onSelected = { itemId, visibility ->
                    })
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                // selectProducteurParcelle.setSelection(0)
                // producteurId = (selectProducteurParcelle.selectedItem as ProducteurModel).id.toString()
            }
        }
    }


    fun setupLocaliteSelection() {
        localitesList = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        if (localitesList?.size == 0) {
            showMessage(
                "La liste des Localités semble vide, veuillez procéder à la synchronisation des données svp.",
                this,
                finished = true,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false

            )
            return
        }

        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
        selectLocaliteInspection!!.adapter = localiteAdapter

        selectLocaliteInspection.setTitle("Choisir la localite")
        selectLocaliteInspection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val locality = localitesList!![position]
                localiteNom = locality.nom!!

                localiteId = if (locality.isSynced) locality.id!!.toString() else locality.uid.toString()

                setupProducteurSelection(localiteId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }

    fun setupEncareurSelection(){

        val encadreurList = CcbRoomDatabase.getDatabase(applicationContext)?.staffFormation()?.getAll( agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString() )
        val delegueDatas: MutableList<CommonData> = mutableListOf()
        encadreurList?.map {
            CommonData(id = it.id, nom = "${it.firstname} ${it.lastname}")
        }?.let {
            delegueDatas.addAll(it)
        }

        val encadrAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, delegueDatas!!)
        selectEncadreurList.adapter = encadrAdapter

        selectEncadreurList.setTitle("Choisir un encadreur")
        selectEncadreurList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val encadreur = encadreurList!![position]
                encadreurNomPrenoms = "${encadreur.firstname} ${encadreur.lastname}"
                encadreurId = encadreur.id!!.toString()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

    }


    fun draftInspection(draftModel: DataDraftedModel?) {
        val itemModelOb = getInspectObjet(false)
        if(itemModelOb == null) return
        val draftInsoection = itemModelOb?.first.apply {}

        showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
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
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            },
            positive = "OUI",
            deconnec = false,
            showNo = true
        )
    }


    fun collectDatas() {
        if (producteurId.isEmpty()) {
            showMessage(
                message = "Selectionnez le producteur, svp !",
                context = this,
                finished = false,
                callback = {},
                positive = "Ok",
                deconnec = false,
                showNo = false
            )
            return
        }

        if (campagneId.isEmpty()) {
            showMessage(
                message = "Selectionnez la campagne, svp !",
                context = this,
                finished = false,
                callback = {},
                positive = "Ok",
                deconnec = false,
                showNo = false
            )
            return
        }

        // dateInspection = DateTime.now().toString(DateTimeFormat.forPattern("dd-MM-yyyy"))

        dateInspection = editDateInspection.text.toString().trim()

        if (dateInspection.isEmpty()) {
            showMessage(
                message = "Selectionnez la date, svp !",
                context = this,
                finished = false,
                callback = {},
                positive = "Ok",
                deconnec = false,
                showNo = false
            )
            return
        }

        val itemModelOb = getInspectObjet()
        if(itemModelOb == null) return
        val questionnaireDto = itemModelOb?.first.apply {}

        questionnaireDto.producteursId = producteurId

        Commons.printModelValue(questionnaireDto as Object, (itemModelOb.second as List<MapEntry>?))

        var quizCount = 0

        for (questionResponse in (cQuestionnairesReviewList ?: mutableListOf())) {
            if (questionResponse.reponseId == 0 && questionResponse.isTitle == false) {
                quizCount += 1
            }
        }

        LogUtils.e(Commons.TAG, quizCount)

        if (quizCount > 4) {
            showMessage(
                message = "Renseignez les questionnaires svp ! \nVous etes à ${cQuestionnairesReviewList?.size?.minus(quizCount)}/${cQuestionnairesReviewList?.size?.minus(4)}",
                context = this,
                finished = false,
                callback = {},
                positive = "Ok",
                deconnec = false,
                showNo = false
            )
            return
        }

        val intentInspectionPreview = Intent(this, InspectionPreviewActivity::class.java)
        intentInspectionPreview.putExtra("preview", questionnaireDto)
        ActivityUtils.startActivity(intentInspectionPreview)
    }

    private fun getInspectObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()):  Pair<InspectionDTO, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        var itemList = getSetupInspectModel(InspectionDTO(
            uid = 0,
            id = 0,
            encadreur = encadreurId,
            localiteId = localiteId,
            campagnesId = campagneId,
            campagnesLabel = campagneLabel,
            dateEvaluation = dateInspection,
            formateursId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            producteursId = producteurId,
            producteurNomPrenoms = producteurNomPrenoms,
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
            showMessage(
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
        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val localitesDatas: MutableList<CommonData> = mutableListOf()
        localitesLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            localitesDatas.addAll(it)
        }

        selectLocaliteInspection.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
        provideDatasSpinnerSelection(
            selectLocaliteInspection,
            inspectionDrafted.localiteNom,
            localitesDatas
        )

        val listPersonne = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        Commons.setListenerForSpinner(this@InspectionActivity,
            "Selectionner un producteur","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectProducteurInspection,
            currentVal = listPersonne?.filter { it.id == inspectionDrafted.producteursId?.toInt() }?.map { "${it.nom} ${it.prenoms}" }?.let{
                if (it.isNullOrEmpty() == false) {
                    it[0]
                } else {
                    ""
                }
            } ?: "",
            listIem = listPersonne?.map { "${it.nom} ${it.prenoms}" }?.toList() ?: listOf(),
            onChanged = {

                val listCertif = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteur(producteurID = listPersonne?.get(it)?.id)

                Commons.setListenerForSpinner(this@InspectionActivity,
                    "Choix du ou des certificats","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
                    spinner = selectCertifInspection,
                    isEmpty = listCertif?.certification?.split(",")
                        ?.toList()?.isEmpty() ?: true,
                    listIem = listCertif?.certification?.split(",")
                        ?.toList() ?: listOf(),
                    onChanged = {

                        val certificat = listCertif?.certification?.split(",")?.get(it).toString()


                        if(certificat.isNullOrEmpty() == false) {
                            cQuestionnairesReviewList?.clear()
                            recyclerQuesionnairesInspection.adapter?.notifyDataSetChanged()
                            fetchQuestionnairesReview(true, certificat ?: "")
                        }



                    },
                    onSelected = { itemId, visibility ->
                    })


            },
            onSelected = { itemId, visibility ->
            })

        // Campagne

        editDateInspection.setText(inspectionDrafted.dateEvaluation)

        val mQuestionsReviewToken = object : TypeToken<MutableList<QuestionResponseModel>>(){}.type
        cQuestionnairesReviewList = GsonUtils.fromJson(inspectionDrafted.reponseStringify, mQuestionsReviewToken)

        fetchQuestionnairesReview(true, inspectionDrafted.certificat ?: "")
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
            scrollingContentContainerInspection.postDelayed({
                scrollingContentContainerInspection.scrollTo(0, 500)
            },2000)
        }

        editDateInspection.setOnClickListener {
            configDate(editDateInspection)
        }

        setupLocaliteSelection()

        setupEncareurSelection()

        if (intent.getStringExtra("from") != null) {
            LogUtils.e("From draft")
            fromAction = intent.getStringExtra("from") ?: ""
            draftedDataInspection = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataInspection!!)
        } else {

        }
    }


    override fun itemClick(item: QuestionResponseModel) {
    }


    override fun itemSelected(position: Int, item: QuestionResponseModel) {
        cQuestionnairesReviewList!![position] = item
        LogUtils.d("${position} : ${item.noteLabel}, ${item.reponseId}")
    }
}
