package ci.progbandama.mobile.activities.forms

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.infospresenters.InfosProducteurPreviewActivity
import ci.progbandama.mobile.adapters.CultureProducteurAdapter
import ci.progbandama.mobile.adapters.OmbrageAdapter
import ci.progbandama.mobile.adapters.OnlyFieldAdapter
import ci.progbandama.mobile.databinding.ActivityUniteAgricoleProducteurBinding
import ci.progbandama.mobile.interfaces.RecyclerItemListener
import ci.progbandama.mobile.models.*
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.AssetFileHelper
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.limitEDTMaxLength
import ci.progbandama.mobile.tools.Commons.Companion.setListenerForSpinner
import ci.progbandama.mobile.tools.Commons.Companion.showMessage
import ci.progbandama.mobile.tools.Commons.Companion.toModifString
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
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
        binding.editCultureInfosProducteur.text = null
        binding.editSuperficeInfosProducteur.text = null
    }


    fun setCultureProducteurs() {
        try {
            cultureProducteurs = mutableListOf()
            cultureProducteurAdapter = CultureProducteurAdapter(cultureProducteurs)
            binding.recyclerCultureInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerCultureInfosProducteur.adapter = cultureProducteurAdapter

            cultureProducteurAdapter?.cultureProducteurListener = this
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupJachereYesNoSelection() {
        try {
            binding.selectJachereInfosProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                        jachereYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                        if (jachereYesNo == getString(R.string.oui)) {
                            binding.linearForetYesSuperficieContainerInfosProducteur.visibility =
                                View.VISIBLE
                        } else {
                            binding.linearForetYesSuperficieContainerInfosProducteur.visibility = View.GONE
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
            binding.selectCulturesInfosProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                    othersCulturesYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                    if (othersCulturesYesNo == getString(R.string.oui)) {
                        binding.linearCultureContainerInfosProducteur.visibility = View.VISIBLE
                    } else {
                        binding.linearCultureContainerInfosProducteur.visibility = View.GONE
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
        producteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())?: arrayListOf<ProducteurModel>()

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if (it.uid.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }else{
                    if (it.id.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = binding.selectProducteurInfosProducteur,
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
                //ProgBandRoomDatabase.getDatabase(applicationContext)?.persBlesseeDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())!!

            val blesseeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayBlessees)

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupTypeDocumentsSelection() {
        try {
            val arrayTypeDocuments = AssetFileHelper.getListDataFromAsset(10, this@UniteAgricoleProducteurActivity) as MutableList<TypeDocumentModel>
//                ProgBandRoomDatabase.getDatabase(applicationContext)?.typeDocumentDao()?.getAll(
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
//                ProgBandRoomDatabase.getDatabase(applicationContext)?.recuDao()
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
            binding.selectMoneyInfosProducteur.onItemSelectedListener =
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
            binding.selectBanqueInfosProducteur.onItemSelectedListener =
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

            val vv1 = binding.editNbrTravPermanInfosProducteur.text.toString().toIntOrNull()?:0
            val vv2 = binding.editNbrTravNotPermanInfosProducteur.text.toString().toIntOrNull()?:0
            val vv3 = binding.editNbrTravRemunInfosProducteur.text.toString().toIntOrNull()?:0

            if( (vv1+vv2) > vv3 || (vv1+vv2) < vv3 ){

                Commons.showMessage(
                    getString(R.string.v_rifiez_le_nombre_de_travailleur_permanent_et_non_permanent),
                    this,
                    finished = false,
                    callback = {},
                    positive = getString(R.string.compris),
                    deconnec = false,
                    showNo = false
                )

                return

            }

            if(itemModelOb == null) return

            val infosProducteursDTO = itemModelOb?.first.apply {
                this?.apply {
                    section = sectionCommon.id.toString()
                    localite = localiteCommon.id.toString()
                    producteursId = producteurCommon.id.toString()

                    typecultureStringify = GsonUtils.toJson((binding.recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    superficiecultureStringify = GsonUtils.toJson((binding.recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                    operateurMMStr = GsonUtils.toJson((binding.recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    numerosMMStr = GsonUtils.toJson((binding.recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                    typeactiviteStr = GsonUtils.toJson((binding.recyclerActiviteOrCacaoInfosProducteur.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
                }
            }

            val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
                this.add(Pair(getString(R.string.les_types_de_culture), (binding.recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
                this.add(Pair(getString(R.string.les_op_rateurs_mobile), (binding.recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
                this.add(Pair(getString(R.string.les_types_d_activit_s), (binding.recyclerActiviteOrCacaoInfosProducteur.adapter as OnlyFieldAdapter).getCurrenntList()?.map { "${it.nom}\n" }.toModifString() ))
            }?.map { MapEntry(it.first, it.second) }

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
//        try {
//            val infosProducteursDraft = getUniteAgricoleProducteurObject()
//
//            LogUtils.json(infosProducteursDraft)

            val itemModelOb = getUniteAgricoleProducteurObject(false, necessaryItem = mutableListOf(
                "Selectionner un producteur"
            ))

            LogUtils.d(itemModelOb?.first)
            LogUtils.d(itemModelOb?.second)

            if(itemModelOb == null) return

            val infosProducteursDTO = itemModelOb?.first.apply {
                this?.apply {
                    section = sectionCommon.id.toString()
                    localite = localiteCommon.id.toString()
                    producteursId = producteurCommon.id.toString()

                    typecultureStringify = GsonUtils.toJson((binding.recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    superficiecultureStringify = GsonUtils.toJson((binding.recyclerCultureInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                    operateurMMStr = GsonUtils.toJson((binding.recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                    numerosMMStr = GsonUtils.toJson((binding.recyclerNumMobileInfosProducteur.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })
                    typeactiviteStr = GsonUtils.toJson((binding.recyclerActiviteOrCacaoInfosProducteur.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
                }
            }

            showMessage(
                message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
                context = this,
                finished = false,
                callback = {
                    ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
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
                            binding.imageDraftBtn.startAnimation(Commons.loadShakeAnimation(this))
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
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//        }
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        try {
            val infosProducteurDrafted =
                ApiClient.gson.fromJson(draftedData.datas, InfosProducteurDTO::class.java)

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
                spinner = binding.selectJachereInfosProducteur,
                currentVal = infosProducteurDrafted.foretsjachere,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.linearForetYesSuperficieContainerInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.y_a_t_il_d_autres_cultures),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = binding.selectCulturesInfosProducteur,
                currentVal = infosProducteurDrafted.autresCultures,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.linearCultureContainerInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.y_a_t_il_d_autres_activit_except_le_cacao),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = binding.selectActiviteOrCacaoInfosProducteur,
                currentVal = infosProducteurDrafted.autreActivite,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.containerActiviteOrCacaoInfoProd.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.y_a_t_il_des_travailleurs_dans_la_famille),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = binding.selectTravaiFamilleInfosProducteur,
                currentVal = infosProducteurDrafted.mainOeuvreFamilial,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.containerNbrTravFamilleInfoProd.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.etes_vous_membre_d_une_soci_t_de_travail),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = binding.selectTravaiSocietInfosProducteur,
                currentVal = infosProducteurDrafted.membreSocieteTravail,//infosProducteurDrafted.mainOeuvreFamilial,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.containerNbrTravSocieteInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.as_tu_un_compte_mobile_money),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = binding.selectMoneyInfosProducteur,
                currentVal = infosProducteurDrafted.mobileMoney,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.containerMobileMoneyInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.as_tu_un_compte_dans_une_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = binding.selectBanqueInfosProducteur,
                currentVal = infosProducteurDrafted.compteBanque,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.containerBuyInfosProducteur.visibility = visibility
                    }
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.s_lectionner_la_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = binding.selectNomBanqueInfosProducteur,
                currentVal = infosProducteurDrafted.nomBanque,
                itemChanged = arrayListOf(Pair(1, "Autre")),
                listIem = resources.getStringArray(R.array.bank_list)
                    ?.toList() ?: listOf(),
                onChanged = {

                },
                onSelected = { itemId, visibility ->
                    if (itemId == 1) {
                        binding.containerNomBanqueInfoProd.visibility = visibility
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
            binding.recyclerCultureInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerCultureInfosProducteur.adapter = autrCultInfoProdAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        binding.clickAddFarmInfosProducteur.setOnClickListener {
            try {
                if (binding.editCultureInfosProducteur.text.toString()
                        .isEmpty() || binding.editSuperficeInfosProducteur.text.toString().isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_sur_la_vari_t_svp), this, callback = {})
                    return@setOnClickListener
                }

                val varieteArbre = OmbrageVarieteModel(
                    0,
                    binding.editCultureInfosProducteur.text.toString(),
                    binding.editSuperficeInfosProducteur.text.toString().trim()
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

                    binding.editCultureInfosProducteur.text?.clear()
                    binding.editSuperficeInfosProducteur.text?.clear()
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
            binding.recyclerNumMobileInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerNumMobileInfosProducteur.adapter = operatInfoProdAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        binding.clickAddMobileNumInfosProducteur.setOnClickListener {
            try {
                if (binding.selectMobileOperatInfoProducteur.selectedItem.toString()
                        .isEmpty() || binding.editNumMobileInfosProducteur.text.toString().isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_sur_la_vari_t_svp), this, callback = {})
                    return@setOnClickListener
                }

                val varieteArbre = OmbrageVarieteModel(
                    0,
                    binding.selectMobileOperatInfoProducteur.selectedItem.toString(),
                    binding.editNumMobileInfosProducteur.text.toString().trim()
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

                    binding.selectMobileOperatInfoProducteur.setSelection(0)
                    binding.editNumMobileInfosProducteur.text?.clear()
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
            binding.recyclerActiviteOrCacaoInfosProducteur.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerActiviteOrCacaoInfosProducteur.adapter = typActivInfoProdAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        binding.clickAddActiviteOrCacaonfosProducteur.setOnClickListener {
            try {
                if (binding.editActiviteOrCacaoInfosProducteur.text.toString()
                        .isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_sur_les_activit_s_svp), this, callback = {})
                    return@setOnClickListener
                }

                val item = CommonData(
                    0,
                    binding.editActiviteOrCacaoInfosProducteur.text.toString().trim(),
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

                    binding.editActiviteOrCacaoInfosProducteur.text?.clear()
                }
                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

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
            spinner = binding.selectLocaliteUniteAgricole,
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

        var producteurDoa = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa();
        var producteurLis = producteurDoa?.getProducteursByLocalite(
            localite = id.toString()
        )
        //LogUtils.d(localitesListi)
        setListenerForSpinner(this, getString(R.string.choix_du_programme),
            getString(R.string.la_liste_des_roducteur_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if(producteurLis?.size!! > 0) false else true,
            spinner = binding.selectProducteurInfosProducteur, listIem = producteurLis?.map { it.nom+" "+it.prenoms }
                ?.toList() ?: listOf(), onChanged = {

                val producteurLis = producteurLis!![it]
//                programmeCommon.nom = programme.libelle!!
//                programmeCommon.id = programme.id!!

            }, onSelected = { itemId, visibility ->

            })

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
            spinner = binding.selectSectionInfProducteur,
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

    private lateinit var binding: ActivityUniteAgricoleProducteurBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniteAgricoleProducteurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(com.intuit.ssp.R.dimen._6ssp),
            resources.getDimension(com.intuit.ssp.R.dimen._5ssp))

        try {

            limitEDTMaxLength(binding.editNumMobileInfosProducteur, 10, 10)

            binding.clickReviewInfosProducteur.setOnClickListener {
                collectDatas()
            }

            binding.clickCloseBtn.setOnClickListener {
                finish()
            }

            binding.clickCancelInfosProducteur.setOnClickListener {
                ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                ActivityUtils.getActivityByContext(this)?.finish()
            }

            binding.imageDraftBtn.setOnClickListener {
                draftInfosProducteur(draftedDataInfosProducteur ?: DataDraftedModel(uid = 0))
            }

            if (intent.getStringExtra("from") != null) {
                draftedDataInfosProducteur =
                    ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
                        ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0))
                        ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataInfosProducteur!!)
            }else{
                setAllListener()
            }

            setOtherListener()

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun setOtherListener() {
        Commons.addNotZeroAtFirstToET(binding.editNbrTravFamilleInfosProducteur)
        Commons.addNotZeroAtFirstToET(binding.editNbrTravRemunInfosProducteur)
        Commons.addNotZeroAtFirstToET(binding.editNbrTravPermanInfosProducteur)
        Commons.addNotZeroAtFirstToET(binding.editNbrTravNotPermanInfosProducteur)
        Commons.addNotZeroAtFirstToET(binding.editNbrTravSocieteInfosProducteur)

    }

    private fun setAllListener() {

        setAutreCulturInfoProdRV()

        setOperateurInfoProdRV()

        setTypActivSParcelleRV()

        setupSectionSelection()

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_des_for_ts_ou_jacheres),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectJachereInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.linearForetYesSuperficieContainerInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_d_autres_cultures),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectCulturesInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.linearCultureContainerInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_d_autres_activit_except_le_cacao),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectActiviteOrCacaoInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerActiviteOrCacaoInfoProd.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_travailleurs_dans_la_famille),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectTravaiFamilleInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerNbrTravFamilleInfoProd.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.etes_vous_membre_d_une_soci_t_de_travail),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectTravaiSocietInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerNbrTravSocieteInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_un_compte_mobile_money),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectMoneyInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerMobileMoneyInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_un_compte_dans_une_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectBanqueInfosProducteur,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerBuyInfosProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.s_lectionner_la_banque),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectNomBanqueInfosProducteur,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = resources.getStringArray(R.array.bank_list)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerNomBanqueInfoProd.visibility = visibility
                }
            })


    }

    override fun itemSelected(position: Int, item: CultureProducteurModel) {
        TODO("Not yet implemented")
    }
}
