package ci.projccb.mobile.activities.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.DistributionArbrePreviewActivity
import ci.projccb.mobile.adapters.DistribArbreAdapter
import ci.projccb.mobile.models.ArbreModel
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.DistributionArbreDao
import ci.projccb.mobile.models.DistributionArbreModel
import ci.projccb.mobile.models.EvaluationArbreModel
import ci.projccb.mobile.models.ProducteurModel
import ci.projccb.mobile.models.QuantiteDistribuer
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.calculateTotalHeight
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_distribution_arbre.clickSaveDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.*
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectLocaliteDistributionArbre

import kotlinx.android.synthetic.main.activity_distribution_arbre.selectProducteurDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectSectionDistributionArbre
import java.util.ArrayList

class DistributionArbreActivity : AppCompatActivity() {
    var listArbreAndState: MutableList<ArbreModel>? = mutableListOf()
    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();
    val parcelleCommon = CommonData();
    private var distributionArbreDao: DistributionArbreDao? = null
    var draftedDataDistribution: DataDraftedModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution_arbre)

        distributionArbreDao = CcbRoomDatabase.getDatabase(this)?.distributionArbreDao()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveDistributionArbre.setOnClickListener {
            collectDatas()
        }

        clickCancelDistributionArbre.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftData(draftedDataDistribution ?: DataDraftedModel(uid = 0))
        }

        ///setOtherListenner()

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataDistribution = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
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
        evaluationsList: MutableList<EvaluationArbreModel>?
    ) {
        //LogUtils.d(evaluationsList, producteur_id)
        val getAllElavOfProd = evaluationsList?.filter { it.producteurId == producteur_id }
        //LogUtils.d(getAllElavOfProd)

        if(getAllElavOfProd?.isEmpty() == false){

            val listArbreEvalAndTotaux = mutableMapOf<String, Int>()
            getAllElavOfProd.map {
                val keyy : MutableList<String> = GsonUtils.fromJson(it.especesarbreStr, object : TypeToken<MutableList<String>>(){}.type)
                val valuey : MutableList<String> = GsonUtils.fromJson(it.quantiteStr, object : TypeToken<MutableList<String>>(){}.type)

                keyy.forEachIndexed { index, s ->
                    listArbreEvalAndTotaux.put(s, valuey[index].toInt())
                }
            }
            LogUtils.d(listArbreEvalAndTotaux)

            listArbreAndState = listArbreAndState?.map {
                listArbreEvalAndTotaux.get(it.id.toString())?.let { value ->
                    it.limited_count = value.toString()
                }
                it
            }?.filter { it.limited_count.equals("0") == false }?.toMutableList()

        }else{

            Commons.showMessage(
                getString(R.string.aucun_besoin_enr_gistr_pour_ce_producteur),
                this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )

            listArbreAndState?.clear()

        }

        recyclerArbreListDistrArbre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerArbreListDistrArbre.adapter = DistribArbreAdapter(listArbreAndState)
        recyclerArbreListDistrArbre.adapter?.notifyDataSetChanged()

        fixFullSizeAtRv(recyclerArbreListDistrArbre)

    }

    private fun setAllSelection() {

        setupSectionSelection()

    }

    private fun fixFullSizeAtRv(recyclerArbreListDistrArbre: RecyclerView?) {

        val totalHeight = calculateTotalHeight(this, recyclerArbreListDistrArbre!!, 80)
        val params = recyclerArbreListDistrArbre?.layoutParams
        params?.height = totalHeight
        recyclerArbreListDistrArbre?.layoutParams = params

    }

    private fun setupSelectionArbreList(listArbreADistri: MutableList<String>, currentVal: String? = null) {
//        Commons.setListenerForSpinner(this,
//            "De quel arbre s'agit-il ?",getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
//            spinner = selectChoixDeLArbreDistributionArbre,
//            listIem = listArbreADistri
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })
    }

    fun getAllRVItemInList(
        viewGroup: ViewGroup,
        mutableListOfId: MutableList<String>,
        mutableListOfNom: MutableList<String>,
        mutableListOfLimit: MutableList<String>,
        mutableListOfQte: MutableList<String>,
    ) {
        val childCount = viewGroup.childCount

        for (i in 0 until childCount) {
            val childView = viewGroup.getChildAt(i)

            if ( childView is AppCompatTextView && childView.tag != null ) {
                val value = childView.text.toString()
                //LogUtils.d("Spinner ${value} "+ childView::class.java.simpleName)
                when(childView.tag){
                    "arbreId" -> mutableListOfId.add(value)
                    "arbreNom" -> mutableListOfNom.add(value)
                    "arbreLimit" -> mutableListOfLimit.add(value)
                }
            } else if ( childView is AppCompatEditText && childView.tag != null ) {
                // You've found an EditText with the specified tag, get its value
                val editText = childView as AppCompatEditText
                val value = editText.text.toString()
                when(childView.tag){
                    "arbreQte" -> mutableListOfQte.add(value)
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
                    )
//                }
            }
        }
    }

    private fun undraftedDatas(draftedDataDistribution: DataDraftedModel) {
        val distributionArbreDraft = GsonUtils.fromJson<DistributionArbreModel>(draftedDataDistribution.datas, DistributionArbreModel::class.java)

        setupSectionSelection(distributionArbreDraft.section, distributionArbreDraft.localite, distributionArbreDraft.producteurId)

        val qteList = GsonUtils.fromJson(distributionArbreDraft.quantiteStr, QuantiteDistribuer::class.java)

        LogUtils.d(qteList)
        //LogUtils.d(qteList.variableKey.get("2"))

        val listArbreAndStatePass = listArbreAndState?.map { arbre ->
            (qteList.variableKey.get(qteList.variableKey.keys.first().toString()) as Map<String, String>).forEach {
                if(it.key.toString() == arbre.id.toString()){
                    arbre.qte_distribue = it.value.toString()
                    LogUtils.d(arbre.qte_distribue, it.key.toString())
                }
            }
            arbre
        }

        (recyclerArbreListDistrArbre.adapter as DistribArbreAdapter).setDataToRvItem(listArbreAndStatePass?.toMutableList()?: arrayListOf())

        //passSetupDistribArbrModel(distributionArbreDraft)
    }

    private fun draftData(dataDraftedModel: DataDraftedModel) {

        val idList = mutableListOf<String>()
        val nomList = mutableListOf<String>()
        val limitList = mutableListOf<String>()
        val qteList = mutableListOf<String>()
        getAllRVItemInList(recyclerArbreListDistrArbre, idList, nomList, limitList, qteList )

//        LogUtils.d( recyclerArbreListDistrArbre.childCount )
//        LogUtils.d(idList, nomList, limitList, qteList)

        if(qteList.isEmpty()){

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
        //brouad02@gmail.com

        val listApproVi = CcbRoomDatabase.getDatabase(applicationContext)?.approvisionnementDao()?.getApproBySect(sectionCommon.id)

        if(listApproVi?.isEmpty() == true){
            Commons.showMessage(
                getString(R.string.aucune_approvisionnement_selectionn_e_faite_une_mise_jour_des_evaluations),
                this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )

            return ;
        }


        val itemModelOb = getDistributArbreObjet(false)

        if(itemModelOb == null) return

        val mapQteList = idList.map { id ->
            id to qteList[idList.indexOf(id)]
        }.toMap().filter { it.value.toInt() > 0 }

        val qtelivre = qteList.sumBy { it.toInt() }.toString()
        val total = limitList.sumBy { it.toInt() }.toString()

        val quantiteDistribuer = QuantiteDistribuer(
            mapOf(
                producteurCommon.id.toString() to mapQteList
            )
        )

        val suiviDistrArbrDatasDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                this.localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()

                quantiteStr = GsonUtils.toJson(quantiteDistribuer.variableKey)
                this.qtelivre = qtelivre
                listApproVi?.first()?.let {
                    this.agroapprovisionnementsection = it.id.toString()
                }
                this.total = total
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
                        datas = ApiClient.gson.toJson(suiviDistrArbrDatasDraft),
                        typeDraft = "distribution_arbre",
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
        val nomList = mutableListOf<String>()
        val limitList = mutableListOf<String>()
        val qteList = mutableListOf<String>()
        getAllRVItemInList(recyclerArbreListDistrArbre, idList, nomList, limitList, qteList )

//        LogUtils.d( recyclerArbreListDistrArbre.childCount )
//        LogUtils.d(idList, nomList, limitList, qteList)
        if(qteList.isEmpty()){

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

        val listApproVi = CcbRoomDatabase.getDatabase(applicationContext)?.approvisionnementDao()?.getApproBySect(sectionCommon.id)

        if(listApproVi?.isEmpty() == true){
            Commons.showMessage(
                getString(R.string.aucune_approvisionnement_selectionn_e_faite_une_mise_jour_des_evaluations),
                this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )

            return ;
        }

        val itemModelOb = getDistributArbreObjet()

        if(itemModelOb == null) return

        val mapQteList = idList.map { id ->
            id to qteList[idList.indexOf(id)]
        }.toMap().filter { it.value.toInt() > 0 }

        val qtelivre = qteList.sumBy { it.toInt() }.toString()
        val total = limitList.sumBy { it.toInt() }.toString()

        val quantiteDistribuer = QuantiteDistribuer(
            mapOf(
                producteurCommon.id.toString() to mapQteList
            )
        )
        //LogUtils.d( GsonUtils.toJson(quantiteDistribuer.variableKey) )

        val suiviDistrArbrDatas = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                this.localite = localiteCommon.id.toString()
                producteurId = producteurCommon.id.toString()

                quantiteStr = GsonUtils.toJson(quantiteDistribuer.variableKey)
                this.qtelivre = qtelivre
                listApproVi?.first()?.let {
                    this.agroapprovisionnementsection = it.id.toString()
                }
                this.total = total
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
            this.add(Pair(getString(R.string.les_arbres_distribu_s), (recyclerArbreListDistrArbre.adapter as DistribArbreAdapter).getArbreListAdded().map { "Arbre: ${it.nom}/${it.nomScientifique}| Strate: ${it.strate}| Qte distribuée: ${it.qte_distribue}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.quantit_distribuer), qtelivre))
            this.add(Pair(getString(R.string.total_enregistrer), total))
        }.map { MapEntry(it.first, it.second) }

        //Commons.printModelValue(suiviDistrArbrDatas as Object, (mapEntries) )

        try {
            val intentDistribArbrPreview = Intent(this, DistributionArbrePreviewActivity::class.java)
            intentDistribArbrPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentDistribArbrPreview.putExtra("preview", suiviDistrArbrDatas)
            intentDistribArbrPreview.putExtra("draft_id", draftedDataDistribution?.uid)
            startActivity(intentDistribArbrPreview)
        } catch (ex: Exception) {
            ex.toString()
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
            spinner = selectSectionDistributionArbre,
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
            spinner = selectLocaliteDistributionArbre,
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
        var producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        val evaluationsList = CcbRoomDatabase.getDatabase(applicationContext)?.evaluationArbreDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID))
        val prodEvaluationsList = evaluationsList?.map { if(it.isSynced) it.producteurId else -1 }?.toMutableList()
        LogUtils.d(prodEvaluationsList)

        val producteursList2 = mutableListOf<ProducteurModel>()
        producteursList?.forEach {
            if( prodEvaluationsList?.contains(it.id?.toString()) == true ) producteursList2.add(it)
        }
        LogUtils.d(producteursList2)
        producteursList = producteursList2

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
            spinner = selectProducteurDistributionArbre,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid

                    listArbreAndState = CcbRoomDatabase.getDatabase(applicationContext)?.arbreDao()?.getAll()
                    setupRvOtherListenner(producteurCommon.id.toString(), evaluationsList)
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
////        LogUtils.json(parcellesList)
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
//            spinner = selectParcelleDistributionArbre,
//            listIem = parcellesList?.map { "${it.codeParc}" }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//                parcellesList?.let { list ->
//                    var parcelle = list.get(it)
//                    parcelleCommon.nom = "${it.codeParc}"
//                    parcelleCommon.id = parcelle.id!!
//
//                    //setupParcelleSelection(parcelleCommon.id, currVal3)
//                }
//
//
//            },
//            onSelected = { itemId, visibility ->
//
//            })
//    }

    fun getDistributArbreObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<DistributionArbreModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false


        var itemList = getSetupDistribArbrModel(
            DistributionArbreModel(
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

    fun getSetupDistribArbrModel(
        prodModel: DistributionArbreModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<DistributionArbreModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_DistributionArbre)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupDistribArbrModel(
        prodModel: DistributionArbreModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_DistributionArbre)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }
}