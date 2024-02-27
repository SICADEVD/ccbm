package ci.projccb.mobile.activities.forms

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.InfosProducteurPreviewActivity
import ci.projccb.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.projccb.mobile.adapters.CultureProducteurAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.adapters.OnlyFieldAdapter
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.ArbreData
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.TAG
import ci.projccb.mobile.tools.Commons.Companion.applyFilters
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.setListenerForSpinner
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.ListConverters
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.*
import java.util.ArrayList

class UniteAgricoleProducteurActivity : AppCompatActivity(), RecyclerItemListener<CultureProducteurModel> {


    var localitesList: MutableList<LocaliteModel>? = mutableListOf()
    var bankAccountYesNo = ""
    var cultureProducteurAdapter: CultureProducteurAdapter? = null
    var cultureProducteurs: MutableList<CultureProducteurModel> = mutableListOf()
    var producteursList: MutableList<ProducteurModel> = mutableListOf()

    var producteurId = ""
    var producteurNomPrenoms = ""
    var producteurCode = ""
    var localiteId = ""
    var localiteNom = ""

    var jachereYesNo = ""
    var jachereYesSuperficie = ""

    var travailleursNbre= "0"
    var travailleursPermanentsNbre = "0"
    var travailleursNonPermanentsNbre = "0"

    var nbreEnfantUnder18 = "0"
    var nbreEnfantUnder18Scolarise = "0"
    var nbreEnfantUnder18ScolariseExtrait = "0"
    var enfantMaladieOne = ""
    var enfantMaladieTwo = ""

    var gestionRecu = ""

    var mobileMoneyYesNo = ""
    var mobileMoneyYesOperateur = ""
    var mobileMoneyYesNumber = ""

    var actionPersonneBlesse = ""

    var typeDocuments = ""

    var buyMethodYesNo = ""

    var othersCulturesYesNo = ""

    var draftedDataInfosProducteur: DataDraftedModel? = null

    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();


    @SuppressLint("NotifyDataSetChanged")
    fun addCultureProducteur(cultureProducteurModel: CultureProducteurModel) {
        if (cultureProducteurModel.label?.length == 0) return

        try {
            cultureProducteurs.forEach {
                if (it.label?.uppercase() == cultureProducteurModel.label?.uppercase() && it.superficie == cultureProducteurModel.superficie) {
                    ToastUtils.showShort(getString(R.string.cette_culture_est_deja_ajout_e))
                    return
                }
            }

            cultureProducteurs.add(cultureProducteurModel)
            cultureProducteurAdapter?.notifyDataSetChanged()

            clearCultureProducteurFields()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun clearCultureProducteurFields() {
        editCultureInfosProducteur.text = null
        editSuperficeInfosProducteur.text = null
    }


    fun setCultureProducteurs() {
        try {
            cultureProducteurs = mutableListOf()
            cultureProducteurAdapter = CultureProducteurAdapter(cultureProducteurs)
            recyclerCultureInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerCultureInfosProducteur.adapter = cultureProducteurAdapter

            cultureProducteurAdapter?.cultureProducteurListener = this
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupJachereYesNoSelection() {
        try {
            selectJachereInfosProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                        jachereYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                        if (jachereYesNo == getString(R.string.oui)) {
                            linearForetYesSuperficieContainerInfosProducteur.visibility =
                                View.VISIBLE
                        } else {
                            linearForetYesSuperficieContainerInfosProducteur.visibility = View.GONE
                        }

                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupCultureYesNoSelection() {
        try {
            selectCulturesInfosProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                    othersCulturesYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                    if (othersCulturesYesNo == getString(R.string.oui)) {
                        linearCultureContainerInfosProducteur.visibility = View.VISIBLE
                    } else {
                        linearCultureContainerInfosProducteur.visibility = View.GONE
                    }

                }

                override fun onNothingSelected(arg0: AdapterView<*>) {
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupOperateursSelection() {
        try {
//            selectMobileMoneyYesOperateurInfosProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                        mobileMoneyYesOperateur = resources.getStringArray(R.array.operateur)[position]
//                    }
//
//                    override fun onNothingSelected(arg0: AdapterView<*>) {
//                    }
//                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun itemClick(item: CultureProducteurModel) {
    }


    fun setupProducteurSelection(id: Int, currVal2: String? = null) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())?: arrayListOf<ProducteurModel>()

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if (it.uid == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
                }else{
                    if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurInfosProducteur,
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


    fun setupBlesseeSelection() {
        try {
            val arrayBlessees = AssetFileHelper.getListDataFromAsset(14, this@UniteAgricoleProducteurActivity) as MutableList<PersonneBlesseeModel>
                //CcbRoomDatabase.getDatabase(applicationContext)?.persBlesseeDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())!!

            val blesseeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayBlessees)

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupTypeDocumentsSelection() {
        try {
            val arrayTypeDocuments = AssetFileHelper.getListDataFromAsset(10, this@UniteAgricoleProducteurActivity) as MutableList<TypeDocumentModel>
//                CcbRoomDatabase.getDatabase(applicationContext)?.typeDocumentDao()?.getAll(
//                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
//                )!!

            val typeDocumentAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayTypeDocuments)

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupGestionRecusSelection() { // Todo : Persist type document
        try {
            val arrayRecus = AssetFileHelper.getListDataFromAsset(3, this@UniteAgricoleProducteurActivity) as MutableList<RecuModel>
//                CcbRoomDatabase.getDatabase(applicationContext)?.recuDao()
//                ?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())!!

            val recuAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayRecus)
//            selectTicketInfosProducteur!!.adapter = recuAdapter
//
//            selectTicketInfosProducteur.setTitle("Choisir l'action")
//            selectTicketInfosProducteur.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        gestionRecu = arrayRecus[position].nom!!
//                    }
//
//                    override fun onNothingSelected(arg0: AdapterView<*>) {
//
//                    }
//                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupMoneyYesNoSelection() {
        try {
            selectMoneyInfosProducteur.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        if (position == 0) {
                            setupOperateursSelection()
//                            linearMobileMoneyYesNumberContainerInfosProducteur.visibility =
//                                View.VISIBLE
//                            linearMoneyYesOperateurContainerProducteur.visibility = View.VISIBLE
                        } else {
                            mobileMoneyYesNumber = ""
//                            linearMobileMoneyYesNumberContainerInfosProducteur.visibility =
//                                View.GONE
//                            linearMoneyYesOperateurContainerProducteur.visibility = View.GONE
                        }

                        mobileMoneyYesNo = resources.getStringArray(R.array.YesOrNo)[position]
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


//    fun setupBuyMethpdYesNoSelection() {
//        try {
//            selectBuyInfosProducteur.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        buyMethodYesNo = resources.getStringArray(R.array.bank_paiement)[position]
//                    }
//
//                    override fun onNothingSelected(arg0: AdapterView<*>) {
//
//                    }
//                }
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//    }


    fun setupBankAccountYesNoSelection() {
        try {
            selectBanqueInfosProducteur.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        bankAccountYesNo = resources.getStringArray(R.array.YesOrNo)[position]
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun collectDatas() {
        try {

            val itemModelOb = getUniteAgricoleProducteurObject()

            if(itemModelOb == null) return

            val infosProducteursDTO = itemModelOb?.first.apply {
                this?.apply {
                    section = sectionCommon.id.toString()
                    localite = localiteCommon.id.toString()
                    producteursId = producteurCommon.id.toString()

                    typecultureStringify = GsonUtils.toJson((recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    superficiecultureStringify = GsonUtils.toJson((recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                    operateurMMStr = GsonUtils.toJson((recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    numerosMMStr = GsonUtils.toJson((recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                    typeactiviteStr = GsonUtils.toJson((recyclerActiviteOrCacaoInfosProducteur.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
                }
            }

            val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
                this.add(Pair(getString(R.string.les_types_de_culture), (recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
                this.add(Pair(getString(R.string.les_op_rateurs_mobile), (recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
                this.add(Pair(getString(R.string.les_types_d_activit_s), (recyclerActiviteOrCacaoInfosProducteur.adapter as OnlyFieldAdapter).getCurrenntList()?.map { "${it.nom}\n" }.toModifString() ))
            }.map { MapEntry(it.first, it.second) }

            val intentInfosProducteurPreview = Intent(this, InfosProducteurPreviewActivity::class.java)
            intentInfosProducteurPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentInfosProducteurPreview.putExtra("preview", infosProducteursDTO)
            intentInfosProducteurPreview.putExtra("draft_id", draftedDataInfosProducteur?.uid)
            startActivity(intentInfosProducteurPreview)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun getUniteAgricoleProducteurObject(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<InfosProducteurDTO, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        if( ((editNbrTravPermanInfosProducteur.text.toString().toInt()?:0) + (editNbrTravNotPermanInfosProducteur.text.toString().toInt()?:0) ) < (editNbrTravRemunInfosProducteur.text.toString().toInt()?:0)){

            Commons.showMessage(
                getString(R.string.v_rifiez_le_nombre_de_travailleur_permanent_et_non_permanent),
                this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )
            return null

        }

        var itemList = getSetupInfoProdModel(InfosProducteurDTO(
            uid = 0,
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            id = 0,
            origin = "local"
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

        return  itemList

    }

    fun getSetupInfoProdModel(
        prodModel: InfosProducteurDTO,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<InfosProducteurDTO, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_info_prods)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupInfoProdModel(
        prodModel: InfosProducteurDTO?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_info_prods)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }


    fun draftInfosProducteur(draftModel: DataDraftedModel?) {
        try {
//            val infosProducteursDraft = getUniteAgricoleProducteurObject()
//
//            LogUtils.json(infosProducteursDraft)

            val itemModelOb = getUniteAgricoleProducteurObject(false)

            if(itemModelOb == null) return

            val infosProducteursDTO = itemModelOb?.first.apply {
                this?.apply {
                    section = sectionCommon.id.toString()
                    localite = localiteCommon.id.toString()
                    producteursId = producteurCommon.id.toString()

                    typecultureStringify = GsonUtils.toJson((recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    superficiecultureStringify = GsonUtils.toJson((recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                    operateurMMStr = GsonUtils.toJson((recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    numerosMMStr = GsonUtils.toJson((recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })
                    typeactiviteStr = GsonUtils.toJson((recyclerActiviteOrCacaoInfosProducteur.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
                }
            }

            showMessage(
                message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
                context = this,
                finished = false,
                callback = {
                    CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                        DataDraftedModel(
                            uid = draftModel?.uid ?: 0,
                            datas = ApiClient.gson.toJson(infosProducteursDTO),
                            typeDraft = "infos_producteur",
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
                },
                positive = getString(R.string.oui),
                deconnec = false,
                showNo = true
            )
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        try {
            val infosProducteurDrafted =
                ApiClient.gson.fromJson(draftedData.datas, InfosProducteurDTO::class.java)

            // Localite
//            val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()
//                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val localitesDatas: MutableList<CommonData> = mutableListOf()
//            localitesLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                localitesDatas.addAll(it)
//            }
//            selectLocaliteUniteAgricole.adapter =
//                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//            provideDatasSpinnerSelection(
//                selectLocaliteUniteAgricole,
//                infosProducteurDrafted.localiteNom,
//                localitesDatas
//            )
//
//            // Jachere
//            provideStringSpinnerSelection(
//                selectJachereInfosProducteur,
//                infosProducteurDrafted.foretsjachere,
//                resources.getStringArray(R.array.YesOrNo)
//            )
//
//            // other fams
//            provideStringSpinnerSelection(
//                selectCulturesInfosProducteur,
//                infosProducteurDrafted.autresCultures,
//                resources.getStringArray(R.array.YesOrNo)
//            )
//
//            // Blesses
//            val blessesLists = AssetFileHelper.getListDataFromAsset(14, this@UniteAgricoleProducteurActivity) as MutableList<PersonneBlesseeModel>?
//                CcbRoomDatabase.getDatabase(this)?.persBlesseeDoa()
//                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val blessesDatas: MutableList<CommonData> = mutableListOf()
//            blessesLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                blessesDatas.addAll(it)
//            }
//            selectBlesseeInfosProducteur.adapter =
//                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, blessesDatas)
//            provideDatasSpinnerSelection(
//                selectBlesseeInfosProducteur,
//                infosProducteurDrafted.personneBlessee,
//                blessesDatas
//            )

            // Documents
//            val documentsLists = AssetFileHelper.getListDataFromAsset(10, this@UniteAgricoleProducteurActivity) as MutableList<TypeDocumentModel>?
//                CcbRoomDatabase.getDatabase(this)?.typeDocumentDao()
//                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val documentsDatas: MutableList<CommonData> = mutableListOf()
//            documentsLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                documentsDatas.addAll(it)
//            }
//            selectPaperInfosProducteur.adapter =
//                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, documentsDatas)
//            provideDatasSpinnerSelection(
//                selectPaperInfosProducteur,
//                infosProducteurDrafted.typeDocuments,
//                documentsDatas
//            )

            // Recus
//            val recusLists = AssetFileHelper.getListDataFromAsset(3, this@UniteAgricoleProducteurActivity) as MutableList<RecuModel>?
//                CcbRoomDatabase.getDatabase(this)?.recuDao()
//                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val recusDatas: MutableList<CommonData> = mutableListOf()
//            recusLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                recusDatas.addAll(it)
//            }
//            selectTicketInfosProducteur.adapter =
//                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, recusDatas)
//            provideDatasSpinnerSelection(
//                selectTicketInfosProducteur,
//                infosProducteurDrafted.recuAchat,
//                recusDatas
//            )
//
//            // Mobile operateur
//            provideStringSpinnerSelection(
//                selectMobileMoneyYesOperateurInfosProducteur,
//                infosProducteurDrafted.operateurMM,
//                resources.getStringArray(R.array.operateur)
//            )

            // Maladie enfants
//            if (ListConverters.stringToMutableList(infosProducteurDrafted.maladiesenfantsStringify)
//                    ?.isNotEmpty()!!
//            ) {
//                val maladies =
//                    ListConverters.stringToMutableList(infosProducteurDrafted.maladiesenfantsStringify)
//                editMaladieOneInfosProducteur.setText(maladies?.first())
//                editMaladieTwoInfosProducteur.setText(maladies?.last())
//            }

            // mobile money yes no
//            provideStringSpinnerSelection(
//                selectMoneyInfosProducteur,
//                infosProducteurDrafted.mobileMoney,
//                resources.getStringArray(R.array.YesOrNo)
//            )

            // mobile money yes no
//            provideStringSpinnerSelection(
//                selectMoneyInfosProducteur,
//                infosProducteurDrafted.mobileMoney,
//                resources.getStringArray(R.array.YesOrNo)
//            )

            // Method paiement
//            provideStringSpinnerSelection(
//                selectBuyInfosProducteur,
//                infosProducteurDrafted.paiementMM,
//                resources.getStringArray(R.array.bank_paiement)
//            )

            // Bank paiement yes no
//            provideStringSpinnerSelection(
//                selectBanqueInfosProducteur,
//                infosProducteurDrafted.compteBanque,
//                resources.getStringArray(R.array.YesOrNo)
//            )

//            editNbreTravailleursInfosProducteur.setText(infosProducteurDrafted.travailleurs)
//            editNbreTravailleursPermanentsInfosProducteur.setText(infosProducteurDrafted.travailleurspermanents)
//            editNbreTravailleursNonPermanentInfosProducteur.setText(infosProducteurDrafted.travailleurstemporaires)
//
//            editNbreUnder18InfosProducteur.setText(infosProducteurDrafted.age18)
//            editNbreScolariseInfosProducteur.setText(infosProducteurDrafted.persEcole)
//            editNbreExtraitInfosProducteur.setText(infosProducteurDrafted.scolarisesExtrait)
//            editMobileYesNumberInfosProducteur.setText(infosProducteurDrafted.numeroCompteMM)
//            editForetYesSuperficieInfosProducteur.setText(infosProducteurDrafted.superficie)

            // Cultures
//            if (ListConverters.stringToMutableList(infosProducteurDrafted.typecultureStringify)
//                    ?.isNotEmpty()!!
//            ) {
//                cultureProducteurs.clear()
//
//                val cultures =
//                    ListConverters.stringToMutableList(infosProducteurDrafted.typecultureStringify)
//                val culturesSuperficies =
//                    ListConverters.stringToMutableList(infosProducteurDrafted.superficiecultureStringify)
//
//                cultures?.zip(culturesSuperficies!!)?.let { culturesSuperficieDatas ->
//                    culturesSuperficieDatas.map { cultureSuperficie ->
//                        cultureProducteurs.add(
//                            CultureProducteurModel(
//                                uid = 0,
//                                producteurId = infosProducteurDrafted.producteursId.toString()
//                                    .toInt(),
//                                label = cultureSuperficie.first,
//                                superficie = cultureSuperficie.second,
//                                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)
//                                    .toString()
//                            )
//                        )
//                    }
//                }
//            }
            setAutreCulturInfoProdRV(
                (GsonUtils.fromJson<MutableList<String>>(infosProducteurDrafted.typecultureStringify, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
                (GsonUtils.fromJson<MutableList<String>>(infosProducteurDrafted.superficiecultureStringify, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
            )

            setOperateurInfoProdRV(
                (GsonUtils.fromJson<MutableList<String>>(infosProducteurDrafted.operateurMMStr, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
                (GsonUtils.fromJson<MutableList<String>>(infosProducteurDrafted.numerosMMStr, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
            )

            setTypActivSParcelleRV(
                (GsonUtils.fromJson<MutableList<String>>(infosProducteurDrafted.typeactiviteStr, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
            )

            setupSectionSelection(infosProducteurDrafted.section,
                infosProducteurDrafted.localite,
                infosProducteurDrafted.producteursId)

            Commons.setListenerForSpinner(this,
                getString(R.string.avez_vous_des_for_ts_ou_jacheres),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectJachereInfosProducteur,
                currentVal = infosProducteurDrafted.foretsjachere,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        linearForetYesSuperficieContainerInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.y_a_t_il_d_autres_cultures),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectCulturesInfosProducteur,
                currentVal = infosProducteurDrafted.autresCultures,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        linearCultureContainerInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.y_a_t_il_d_autres_activit_except_le_cacao),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectActiviteOrCacaoInfosProducteur,
                currentVal = infosProducteurDrafted.autreActivite,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        containerActiviteOrCacaoInfoProd.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.y_a_t_il_des_travailleurs_dans_la_famille),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectTravaiFamilleInfosProducteur,
                currentVal = infosProducteurDrafted.mainOeuvreFamilial,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        containerNbrTravFamilleInfoProd.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.etes_vous_membre_d_une_soci_t_de_travail),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectTravaiSocietInfosProducteur,
                currentVal = infosProducteurDrafted.membreSocieteTravail,//infosProducteurDrafted.mainOeuvreFamilial,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        containerNbrTravSocieteInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.as_tu_un_compte_mobile_money),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectMoneyInfosProducteur,
                currentVal = infosProducteurDrafted.mobileMoney,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        containerMobileMoneyInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.as_tu_un_compte_dans_une_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectBanqueInfosProducteur,
                currentVal = infosProducteurDrafted.compteBanque,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        containerBuyInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.s_lectionner_la_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectNomBanqueInfosProducteur,
                currentVal = infosProducteurDrafted.nomBanque,
                itemChanged = arrayListOf(Pair(1, "Autre")),
                listIem = resources.getStringArray(R.array.bank_list)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        containerNomBanqueInfoProd.visibility = visibility
                    }
                })

            passSetupInfoProdModel(infosProducteurDrafted)

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    fun setAutreCulturInfoProdRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
        val autrCultListInfoProd = mutableListOf<OmbrageVarieteModel>()
        var countN = 0
        libeleList.forEach {
            autrCultListInfoProd.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
            countN++
        }
        val autrCultInfoProdAdapter = OmbrageAdapter(autrCultListInfoProd,
            getString(R.string.culture), getString(R.string.superficie))


        try {
            recyclerCultureInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerCultureInfosProducteur.adapter = autrCultInfoProdAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        clickAddFarmInfosProducteur.setOnClickListener {
            try {
                if (editCultureInfosProducteur.text.toString()
                        .isEmpty() || editSuperficeInfosProducteur.text.toString().isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_sur_la_vari_t_svp), this, callback = {})
                    return@setOnClickListener
                }

                val varieteArbre = OmbrageVarieteModel(
                    0,
                    editCultureInfosProducteur.text.toString(),
                    editSuperficeInfosProducteur.text.toString().trim()
                )

                if(varieteArbre.variete?.length?:0 > 0){
                    autrCultListInfoProd?.forEach {
                        if (it.variete?.uppercase() == varieteArbre.variete?.uppercase() && it.nombre == varieteArbre.nombre) {
                            ToastUtils.showShort(getString(R.string.cette_culture_est_deja_ajout_e))
                            return@setOnClickListener
                        }
                    }

                    autrCultListInfoProd?.add(varieteArbre)
                    autrCultInfoProdAdapter?.notifyDataSetChanged()

                    editCultureInfosProducteur.text?.clear()
                    editSuperficeInfosProducteur.text?.clear()
                }
                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

    }

    fun setOperateurInfoProdRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
        val operatListInfoProd = mutableListOf<OmbrageVarieteModel>()
        var countN = 0
        libeleList.forEach {
            operatListInfoProd.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
            countN++
        }
        val operatInfoProdAdapter = OmbrageAdapter(operatListInfoProd,
            getString(R.string.op_rateur), getString(R.string.num_ro))


        try {
            recyclerNumMobileInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerNumMobileInfosProducteur.adapter = operatInfoProdAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        clickAddMobileNumInfosProducteur.setOnClickListener {
            try {
                if (selectMobileOperatInfoProducteur.selectedItem.toString()
                        .isEmpty() || editNumMobileInfosProducteur.text.toString().isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_sur_la_vari_t_svp), this, callback = {})
                    return@setOnClickListener
                }

                val varieteArbre = OmbrageVarieteModel(
                    0,
                    selectMobileOperatInfoProducteur.selectedItem.toString(),
                    editNumMobileInfosProducteur.text.toString().trim()
                )

                if(varieteArbre.variete?.length?:0 > 0){
                    operatListInfoProd?.forEach {
                        if (it.variete?.uppercase() == varieteArbre.variete?.uppercase() && it.nombre == varieteArbre.nombre) {
                            ToastUtils.showShort(getString(R.string.cet_op_rateur_est_deja_ajout_e))
                            return@setOnClickListener
                        }
                    }

                    operatListInfoProd?.add(varieteArbre)
                    operatInfoProdAdapter?.notifyDataSetChanged()

                    selectMobileOperatInfoProducteur.setSelection(0)
                    editNumMobileInfosProducteur.text?.clear()
                }
                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

    }

    fun setTypActivSParcelleRV(libeleList:MutableList<String> = arrayListOf()) {
        val typActivListInfoProd = mutableListOf<CommonData>()
        var countN = 0
        libeleList.forEach {
            typActivListInfoProd.add(CommonData(0, it))
            countN++
        }

        val typActivInfoProdAdapter = OnlyFieldAdapter(typActivListInfoProd, "Libellé")
        try {
            recyclerActiviteOrCacaoInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerActiviteOrCacaoInfosProducteur.adapter = typActivInfoProdAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        clickAddActiviteOrCacaonfosProducteur.setOnClickListener {
            try {
                if (editActiviteOrCacaoInfosProducteur.text.toString()
                        .isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_sur_les_activit_s_svp), this, callback = {})
                    return@setOnClickListener
                }

                val item = CommonData(
                    0,
                    editActiviteOrCacaoInfosProducteur.text.toString().trim(),
                )

                if(item.nom?.length?:0 > 0){
                    typActivListInfoProd?.forEach {
                        if (it.nom?.uppercase() == item.nom?.uppercase()) {
                            ToastUtils.showShort(getString(R.string.cette_activit_est_d_ja_ajout_e))

                            return@setOnClickListener
                        }
                    }

                    typActivListInfoProd?.add(item)
                    typActivInfoProdAdapter?.notifyDataSetChanged()

                    editActiviteOrCacaoInfosProducteur.text?.clear()
                }
                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

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
            spinner = selectLocaliteUniteAgricole,
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

    private fun setProducteurSpinner(id: Int?) {

        var producteurDoa = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa();
        var producteurLis = producteurDoa?.getProducteursByLocalite(
            localite = id.toString()
        )
        //LogUtils.d(localitesListi)
        setListenerForSpinner(this, getString(R.string.choix_du_programme),
            getString(R.string.la_liste_des_roducteur_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if(producteurLis?.size!! > 0) false else true,
            spinner = selectProducteurInfosProducteur, listIem = producteurLis?.map { it.nom+" "+it.prenoms }
                ?.toList() ?: listOf(), onChanged = {

                val producteurLis = producteurLis!![it]
//                programmeCommon.nom = programme.libelle!!
//                programmeCommon.id = programme.id!!

            }, onSelected = { itemId, visibility ->

            })

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
            spinner = selectSectionInfProducteur,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unite_agricole_producteur)

        //setupSectionSelection()

//        clickAddFarmInfosProducteur.setOnClickListener {
//            try {
//                if (producteurId.isEmpty()) {
//                    showMessage(
//                        "Selectionnez le producteur, svp !",
//                        context = this,
//                        finished = false,
//                        callback = {},
//                        positive = getString(R.string.ok),
//                        deconnec = false,
//                        showNo = false
//                    )
//                    return@setOnClickListener
//                }
//
//                if (editCultureInfosProducteur.text.toString()
//                        .isEmpty() || editSuperficeInfosProducteur.text.toString().isEmpty()
//                ) {
//                    showMessage(
//                        "Renseignez une culture, svp !",
//                        context = this,
//                        finished = false,
//                        callback = {},
//                        positive = getString(R.string.ok),
//                        deconnec = false,
//                        showNo = false
//                    )
//                    return@setOnClickListener
//                }
//
//                val cultureProducteur = CultureProducteurModel(
//                    0,
//                    producteurId.toInt(),
//                    editCultureInfosProducteur.text.toString().trim(),
//                    editSuperficeInfosProducteur.text.toString().trim(),
//                    SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
//                )
//                addCultureProducteur(cultureProducteur)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }

        try {
//            editForetYesSuperficieInfosProducteur.doAfterTextChanged { editable ->
//                LogUtils.e(TAG, editable.toString().trim())
//                jachereYesSuperficie = editable.toString().trim()
//            }

//            editNbreTravailleursInfosProducteur.doAfterTextChanged { editable ->
//                travailleursNbre =
//                    if (editable?.toString()?.isEmpty()!!) "0" else editable.toString().trim()
//            }
//
//            editNbreTravailleursPermanentsInfosProducteur.doAfterTextChanged { editable ->
//                travailleursPermanentsNbre =
//                    if (editable?.toString()?.isEmpty()!!) "0" else editable.toString().trim()
//            }
//
//            editNbreTravailleursNonPermanentInfosProducteur.doAfterTextChanged { editable ->
//                travailleursNonPermanentsNbre =
//                    if (editable?.toString()?.isEmpty()!!) "0" else editable.toString().trim()
//            }
//
//            editNbreUnder18InfosProducteur.doAfterTextChanged {
//                nbreEnfantUnder18 = if (it?.toString()?.isEmpty()!!) "0" else it.toString().trim()
//            }
//
//            editNbreScolariseInfosProducteur.doAfterTextChanged {
//                nbreEnfantUnder18Scolarise =
//                    if (it?.toString()?.isEmpty()!!) "0" else it.toString().trim()
//            }
//
//            editNbreExtraitInfosProducteur.doAfterTextChanged {
//                nbreEnfantUnder18ScolariseExtrait =
//                    if (it?.toString()?.isEmpty()!!) "0" else it.toString().trim()
//            }
//
//            editMaladieOneInfosProducteur.doAfterTextChanged {
//                enfantMaladieOne = it.toString().trim()
//            }
//
//            editMobileYesNumberInfosProducteur.doAfterTextChanged {
//                mobileMoneyYesNumber = it.toString().trim()
//            }
//
//            editMaladieTwoInfosProducteur.doAfterTextChanged {
//                enfantMaladieTwo = it.toString().trim()
//            }

            clickReviewInfosProducteur.setOnClickListener {
                collectDatas()
            }

            clickCloseBtn.setOnClickListener {
                finish()
            }

            clickCancelInfosProducteur.setOnClickListener {
                ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                ActivityUtils.getActivityByContext(this)?.finish()
            }

            imageDraftBtn.setOnClickListener {
                draftInfosProducteur(draftedDataInfosProducteur ?: DataDraftedModel(uid = 0))
            }

//            setCultureProducteurs()
//            setupCultureYesNoSelection()
//            setupJachereYesNoSelection()
            //setupProducteurSelection()
//            setupBlesseeSelection()
//            setupLocaliteSelection()
//            setupGestionRecusSelection()
//            setupTypeDocumentsSelection()
//            setupOperateursSelection()
//            setupBankAccountYesNoSelection()
//            setupMoneyYesNoSelection()
//            setupBuyMethpdYesNoSelection()

//            applyFilters(editNbreUnder18InfosProducteur)
//            applyFilters(editNbreTravailleursInfosProducteur)
//            applyFilters(editNbreTravailleursNonPermanentInfosProducteur)
//            applyFilters(editNbreTravailleursPermanentsInfosProducteur)
//            applyFilters(editNbreScolariseInfosProducteur)

            if (intent.getStringExtra("from") != null) {
                draftedDataInfosProducteur =
                    CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                        ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0))
                        ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataInfosProducteur!!)
            }else{
                setAllListener()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun setAllListener() {

        setAutreCulturInfoProdRV()

        setOperateurInfoProdRV()

        setTypActivSParcelleRV()

        setupSectionSelection()

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_des_for_ts_ou_jacheres),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectJachereInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearForetYesSuperficieContainerInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_d_autres_cultures),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectCulturesInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearCultureContainerInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_d_autres_activit_except_le_cacao),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectActiviteOrCacaoInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerActiviteOrCacaoInfoProd.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_travailleurs_dans_la_famille),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTravaiFamilleInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNbrTravFamilleInfoProd.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.etes_vous_membre_d_une_soci_t_de_travail),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTravaiSocietInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNbrTravSocieteInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_un_compte_mobile_money),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMoneyInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerMobileMoneyInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_un_compte_dans_une_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectBanqueInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerBuyInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.s_lectionner_la_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectNomBanqueInfosProducteur,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = resources.getStringArray(R.array.bank_list)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerNomBanqueInfoProd.visibility = visibility
                }
            })


    }

    override fun itemSelected(position: Int, item: CultureProducteurModel) {
        TODO("Not yet implemented")
    }
}
