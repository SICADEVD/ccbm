package ci.progbandama.mobile.activities.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.infospresenters.EvaluationPostPlantPreviewActivity
import ci.progbandama.mobile.adapters.EvaluationPostPlantAdapter
import ci.progbandama.mobile.models.ArbreModel
import ci.progbandama.mobile.models.DataDraftedModel
import ci.progbandama.mobile.models.DistributionArbreDao
import ci.progbandama.mobile.models.ListeEspeceArbrePostPlantModel
import ci.progbandama.mobile.models.PostPlantingArbrDistribModel
import ci.progbandama.mobile.models.PostPlantingItem
import ci.progbandama.mobile.models.PostPlantingModel
import ci.progbandama.mobile.models.QuantiteDistribuer
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.configDate
import ci.progbandama.mobile.tools.Commons.Companion.toModifString
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_postplanting.*
import kotlinx.android.synthetic.main.activity_postplanting.clickSaveEvalPostPlanting
import java.util.ArrayList

class PostPlantingEvalActivity : AppCompatActivity() {
    var listArbreAndState: MutableList<ArbreModel>? = mutableListOf()
    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();
    val parcelleCommon = CommonData();
    private var distributionArbreDao: DistributionArbreDao? = null
    var draftedDataDistribution: DataDraftedModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postplanting)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        distributionArbreDao = ProgBandRoomDatabase.getDatabase(this)?.distributionArbreDao()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveEvalPostPlanting.setOnClickListener {
            collectDatas()
        }

        clickCancelEvalPostPlant.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftData(draftedDataDistribution ?: DataDraftedModel(uid = 0))
        }

        editDateEvalPostPlant.setOnClickListener { configDate(editDateEvalPostPlant) }
        ///setOtherListenner()

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataDistribution = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(
                    uid = 0
                )
                undraftedDatas(draftedDataDistribution!!)
            }else{
                setAllSelection()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }

    private fun setupRvOtherListenner(
        producteur_id: String,
        evaluationsList: MutableList<PostPlantingArbrDistribModel>?,
        currVal3: String? = null
    ) {

        var listOfItemsPostPlantAdapt: MutableList<ListeEspeceArbrePostPlantModel> = mutableListOf()
        listArbreAndState = ProgBandRoomDatabase.getDatabase(applicationContext)?.arbreDao()?.getAll()
        val getAllElavOfProdPostPlant = evaluationsList?.filter { it.id.toString() == producteur_id }

        if(getAllElavOfProdPostPlant?.isEmpty() == false){

            (getAllElavOfProdPostPlant).map {
                var arbrePostPlant = GsonUtils.fromJson<MutableList<PostPlantingItem>>(it.arbresStr, object : TypeToken<MutableList<PostPlantingItem>>(){}.type)
                arbrePostPlant.map {item ->
                    var currArbre = listArbreAndState?.filter { it.id.toString() == item.id_arbre.toString() }
                    currArbre?.forEach {
                        listOfItemsPostPlantAdapt.add(ListeEspeceArbrePostPlantModel(it.id, it.nom, item.quantite.toString(), item.quantite.toString(), item.quantite.toString(),""))
                    }
                }
            }

        }else{

            Commons.showMessage(
                getString(R.string.aucune_distribution_post_planting_list_veuillez_faire_une_synchronisation),
                this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )

            listArbreAndState?.clear()

        }

        recyclerArbreListEvalPostPlant.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerArbreListEvalPostPlant.adapter = EvaluationPostPlantAdapter(listOfItemsPostPlantAdapt)
        recyclerArbreListEvalPostPlant.adapter?.notifyDataSetChanged()

        var view = LayoutInflater.from(this@PostPlantingEvalActivity).inflate(R.layout.evaluat_postplant_item_list, null, false)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.findViewById<RelativeLayout>(R.id.postplant_height).measure(widthMeasureSpec, heightMeasureSpec)
        val height = view.findViewById<RelativeLayout>(R.id.postplant_height).measuredHeight

        LogUtils.d(view.findViewById<RelativeLayout>(R.id.postplant_height).height, height)

        currVal3?.let { currval3 ->

            val currList = currval3.split("|")
            val qteList = GsonUtils.fromJson<Map<String, Map<String, String>>>(currList.get(0), object : TypeToken<Map<String, Map<String, String>>>(){}.type)
            val qteSurvList = GsonUtils.fromJson<Map<String, Map<String, String>>>(currList.get(1), object : TypeToken<Map<String, Map<String, String>>>(){}.type)
            val commentList = GsonUtils.fromJson<Map<String, Map<String, String>>>(currList.get(2), object : TypeToken<Map<String, Map<String, String>>>(){}.type)
            val listArbreAndStatePass = listOfItemsPostPlantAdapt?.map { eval1 ->
                (qteList).forEach {
                    (it.value).forEach { item ->
                        if(item.key.toString() == eval1.arbre_id.toString()){
                            eval1.qte_plant = item.value.toString()
                        }
                    }
                }
                eval1
            }?.map { eval2 ->
                (qteSurvList).forEach {
                    (it.value).forEach { item ->
                        if(item.key.toString() == eval2.arbre_id.toString()){
                            eval2.qte_survec = item.value.toString()
                        }
                    }
                }
                eval2
            }?.map { eval3 ->
                (commentList).forEach {
                    (it.value).forEach { item ->
                        if(item.key.toString() == eval3.arbre_id.toString()){
                            eval3.commentaire = item.value.toString()
                        }
                    }
                }
                eval3
            }

            (recyclerArbreListEvalPostPlant.adapter as EvaluationPostPlantAdapter).setDataToRvItem(listArbreAndStatePass?.toMutableList()?: arrayListOf())
        }

        fixFullSizeAtRv(recyclerArbreListEvalPostPlant, height+200)

    }

    private fun setAllSelection() {

        setupSectionSelection()

    }

    private fun fixFullSizeAtRv(recyclerArbreListDistrArbre: RecyclerView?, height:Int = 80) {

        //val totalHeight = calculateTotalHeight(this, recyclerArbreListDistrArbre!!, height)
        val params = recyclerArbreListDistrArbre?.layoutParams
        params?.height = height
        recyclerArbreListDistrArbre?.layoutParams = params

    }

    private fun setupSelectionArbreList(listArbreADistri: MutableList<String>, currentVal: String? = null) {

    }

    fun getAllRVItemInList(
        viewGroup: ViewGroup,
        mutableListOfId: MutableList<String>,
        mutableListOfNom: MutableList<String>,
        mutableListOfLimit: MutableList<String>,
        mutableListOfQte: MutableList<String>,
        mutableqteCommentList: MutableList<String>,
    ) {
        val childCount = viewGroup.childCount

        for (i in 0 until childCount) {
            val childView = viewGroup.getChildAt(i)

            if ( childView is AppCompatTextView && childView.tag != null ) {
                val value = childView.text.toString()
                //LogUtils.d("Spinner ${value} "+ childView::class.java.simpleName)
                when(childView.tag){
                    "arbreId" -> mutableListOfId.add(value)
                    "arbreQteList" -> mutableListOfNom.add(value)
                }
            } else if ( childView is AppCompatEditText && childView.tag != null ) {
                // You've found an EditText with the specified tag, get its value
                val editText = childView as AppCompatEditText
                val value = editText.text.toString()
                when(childView.tag){
                    "qtePlante" -> mutableListOfLimit.add(value)
                    "qteSurvecue" -> mutableListOfQte.add(value)
                    "qteComment" -> mutableqteCommentList.add(value)
                }
                //countField++
            } else if (childView is ViewGroup) {
                // If it's a ViewGroup, recursively call this method
//                if(childView.visibility == View.VISIBLE)
//                {
                    getAllRVItemInList(
                        viewGroup = childView,
                        mutableListOfId,
                        mutableListOfNom,
                        mutableListOfLimit,
                        mutableListOfQte,
                        mutableqteCommentList,
                    )
//                }
            }
        }
    }

    private fun undraftedDatas(draftedDataDistribution: DataDraftedModel) {
        val distributionArbreDraft = GsonUtils.fromJson<PostPlantingModel>(draftedDataDistribution.datas, PostPlantingModel::class.java)

        setupSectionSelection(
            distributionArbreDraft.section,
            distributionArbreDraft.localite,
            distributionArbreDraft.producteurId,
            "${distributionArbreDraft.quantiteStr}|${distributionArbreDraft.quantitesurvecueeStr}|${distributionArbreDraft.commentaireStr}"
        )

        passSetupEvaluationPostPlantModel(distributionArbreDraft)
    }

    private fun draftData(dataDraftedModel: DataDraftedModel) {

        val idList = mutableListOf<String>()
        val qteRecuList = mutableListOf<String>()
        val qtePlanteList = mutableListOf<String>()
        val qteSurveList = mutableListOf<String>()
        val qteCommentList = mutableListOf<String>()
        getAllRVItemInList(recyclerArbreListEvalPostPlant, idList, qteRecuList, qtePlanteList, qteSurveList, qteCommentList )

        val itemModelOb = getEvaluationPostPlantObjet(false, necessaryItem = mutableListOf(
            "Selectionner un producteur"
        ))

        if(itemModelOb == null) return

        var qteRecuListObj: MutableMap<String, String> = mutableMapOf()
        var qtePlanteListObj: MutableMap<String, String> = mutableMapOf()
        var qteSurvecuListObj: MutableMap<String, String> = mutableMapOf()
        var commentListObj: MutableMap<String, String> = mutableMapOf()
        //var idListObj: MutableMap<String, String> = mutableMapOf()
        val idListObj = idList.map {
            qteRecuListObj.put(it, qteRecuList[idList.indexOf(it)].toString())
            qtePlanteListObj.put(it, qtePlanteList[idList.indexOf(it)].toString())
            qteSurvecuListObj.put(it, qteSurveList[idList.indexOf(it)].toString())
            commentListObj.put(it, qteCommentList[idList.indexOf(it)].toString())
        }
        var totalQt = qteRecuListObj.map { it.value?:"0" }?.sumOf { it?.toInt() }
        var qtPlante = qtePlanteListObj.map { it.value?:"0" }?.sumOf { it?.toInt() }
        var qtSurvec = qteSurvecuListObj.map { it.value?:"0" }?.sumOf { it?.toInt() }
        //LogUtils.d( GsonUtils.toJson(quantiteDistribuer.variableKey) )

        val itemsDatasDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                this.localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()

                quantiterecueStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to qteRecuListObj)).variableKey)
                quantiteStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to qtePlanteListObj)).variableKey)
                quantitesurvecueeStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to qteSurvecuListObj)).variableKey)
                commentaireStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to commentListObj)).variableKey)
                this.total = totalQt.toString()
                this.qteplante = qtPlante.toString()
                this.qtesurvecue = qtSurvec.toString()
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
                        datas = ApiClient.gson.toJson(itemsDatasDraft),
                        typeDraft = "postplanting",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                Commons.showMessage(
                    message = getString(R.string.contenu_ajout_aux_brouillons),
                    context = this,
                    finished = true,
                    callback = {
                        Commons.playDraftSound(this)
                        imageDraftBtn.startAnimation(
                            Commons.loadShakeAnimation(
                                this
                            )
                        )
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

    private fun collectDatas() {

        val idList = mutableListOf<String>()
        val qteRecuList = mutableListOf<String>()
        val qtePlanteList = mutableListOf<String>()
        val qteSurveList = mutableListOf<String>()
        val qteCommentList = mutableListOf<String>()
        getAllRVItemInList(recyclerArbreListEvalPostPlant, idList, qteRecuList, qtePlanteList, qteSurveList, qteCommentList )

//        LogUtils.d( recyclerArbreListDistrArbre.childCount )
//        LogUtils.d(idList, nomList, limitList, qteList)
        if(idList.isEmpty()){

            Commons.showMessage(
                getString(R.string.aucun_arbre_n_a_t_enr_gistr_faite_une_mise_jour_des_evaluations),
                this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )

            return ;

        }

        val itemModelOb = getEvaluationPostPlantObjet()

        if(itemModelOb == null) return

        var qteRecuListObj: MutableMap<String, String> = mutableMapOf()
        var qtePlanteListObj: MutableMap<String, String> = mutableMapOf()
        var qteSurvecuListObj: MutableMap<String, String> = mutableMapOf()
        var commentListObj: MutableMap<String, String> = mutableMapOf()
        //var idListObj: MutableMap<String, String> = mutableMapOf()
        val idListObj = idList.map {
            qteRecuListObj.put(it, qteRecuList[idList.indexOf(it)].toString())
            qtePlanteListObj.put(it, qtePlanteList[idList.indexOf(it)].toString())
            qteSurvecuListObj.put(it, qteSurveList[idList.indexOf(it)].toString())
            commentListObj.put(it, qteCommentList[idList.indexOf(it)].toString())
        }
        var totalQt = qteRecuListObj.map { it.value?:"0" }?.sumOf { it?.toInt() }
        var qtPlante = qtePlanteListObj.map { it.value?:"0" }?.sumOf { it?.toInt() }
        var qtSurvec = qteSurvecuListObj.map { it.value?:"0" }?.sumOf { it?.toInt() }
        //LogUtils.d( GsonUtils.toJson(quantiteDistribuer.variableKey) )

        val suiviDistrArbrDatas = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                this.localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()

                quantiterecueStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to qteRecuListObj)).variableKey)
                quantiteStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to qtePlanteListObj)).variableKey)
                quantitesurvecueeStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to qteSurvecuListObj)).variableKey)
                commentaireStr = GsonUtils.toJson(QuantiteDistribuer(mapOf(producteurCommon.id.toString() to commentListObj)).variableKey)
                this.total = totalQt.toString()
                this.qteplante = qtPlante.toString()
                this.qtesurvecue = qtSurvec.toString()
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
            this.add(Pair(getString(R.string.liste_des_arbres_valu_s), (recyclerArbreListEvalPostPlant.adapter as EvaluationPostPlantAdapter).getArbreListAdded().map { "Arbre: ${it.nom_arbre}| Qte reçu: ${it.qte_recu}| Qte plantée: ${it.qte_plant}| Qte survécue: ${it.qte_survec}| Commentaire: ${it.commentaire}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.quantit_plant_e), qtPlante.toString()))
            this.add(Pair(getString(R.string.quantit_surv_cue), qtSurvec.toString()))
            this.add(Pair(getString(R.string.total_enregistrer), totalQt.toString()))
        }.map { MapEntry(it.first, it.second) }

        //Commons.printModelValue(suiviDistrArbrDatas as Object, (mapEntries) )

        try {
            val intentDistribArbrPreview = Intent(this, EvaluationPostPlantPreviewActivity::class.java)
            intentDistribArbrPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentDistribArbrPreview.putExtra("preview", suiviDistrArbrDatas)
            intentDistribArbrPreview.putExtra("draft_id", draftedDataDistribution?.uid)
            startActivity(intentDistribArbrPreview)
        } catch (ex: Exception) {
            ex.toString()
        }

    }

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
            spinner = selectSectionEvalPostPlant,
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
            spinner = selectLocaliteEvalPostPlant,
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
        var producteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var postPlantProducteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.postPlantingArbrDistribDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        var allListingProd = postPlantProducteursList?.map { "${it.id}" }


        producteursList = producteursList?.filter { allListingProd?.contains(it.id.toString()) == true }?.toMutableList()

        LogUtils.d(allListingProd, producteursList)
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
            spinner = selectProducteurEvalPostPlant,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid

                    listArbreAndState = ProgBandRoomDatabase.getDatabase(applicationContext)?.arbreDao()?.getAll()
                    setupRvOtherListenner(producteurCommon.id.toString(), postPlantProducteursList, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun getEvaluationPostPlantObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<PostPlantingModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false


        var itemList = getSetupEvaluationPostPlantModel(
            PostPlantingModel(
                uid = 0,
                id = 0,
                isSynced = false,
                userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
                origin = "local",
            ),
            mutableListOf<Pair<String,String>>())


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

    fun getSetupEvaluationPostPlantModel(
        prodModel: PostPlantingModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<PostPlantingModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_EvalPostPlant)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupEvaluationPostPlantModel(
        prodModel: PostPlantingModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_EvalPostPlant)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }
}