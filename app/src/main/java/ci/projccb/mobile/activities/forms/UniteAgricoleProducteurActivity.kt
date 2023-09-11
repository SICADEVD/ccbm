package ci.projccb.mobile.activities.forms

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.InfosProducteurPreviewActivity
import ci.projccb.mobile.adapters.CultureProducteurAdapter
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.TAG
import ci.projccb.mobile.tools.Commons.Companion.applyFilters
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.ListConverters
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.*

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


    fun setupLocaliteSelection() {
        try {
            localitesList = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa() ?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()


            if (localitesList?.size == 0) {
                showMessage(
                    "La liste des Localités semble vide, veuillez procéder à la synchronisation des données svp.",
                    this,
                    finished = true,
                    callback = {},
                    positive = "OKAY",
                    deconnec = false,
                    showNo = false

                )
                return
            }

            val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
            selectLocaliteUniteAgricole!!.adapter = localiteAdapter

            selectLocaliteUniteAgricole.setTitle("Choisir la localite")
            selectLocaliteUniteAgricole.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        val locality = localitesList!![position]
                        localiteNom = locality.nom!!
                        localiteId =
                            if (locality.isSynced) locality.id!!.toString() else locality.uid.toString()

                        setupProducteurSelection(localiteId = localiteId)
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addCultureProducteur(cultureProducteurModel: CultureProducteurModel) {
        if (cultureProducteurModel.label?.length == 0) return

        try {
            cultureProducteurs.forEach {
                if (it.label?.uppercase() == cultureProducteurModel.label?.uppercase() && it.superficie == cultureProducteurModel.superficie) {
                    ToastUtils.showShort("Cette culture est deja ajoutée")
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

                        if (jachereYesNo == "oui") {
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

                    if (othersCulturesYesNo == "oui") {
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
            selectMobileMoneyYesOperateurInfosProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                        mobileMoneyYesOperateur = resources.getStringArray(R.array.operateur)[position]
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun itemClick(item: CultureProducteurModel) {
    }


    fun setupProducteurSelection(localiteId: String? = null) {
        try {
            val producteursList = if (localiteId == null)
                    CcbRoomDatabase.getDatabase(this)?.producteurDoa()
                        ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())!!
                else CcbRoomDatabase.getDatabase(this)?.producteurDoa()
                    ?.getProducteursByLocalite(localite = localiteId)!!

            val producteursDatas: MutableList<CommonData> = mutableListOf()

            if (producteursList.size == 0) {
                showMessage(
                    "La liste des producteurs de cette localité semble vide, veuillez procéder à la synchronisation des données svp.",
                    this,
                    finished = false,
                    callback = {},
                    positive = "OKAY",
                    deconnec = false,
                    showNo = false
                )

                producteurId = ""
                producteurNomPrenoms = ""
                selectProducteurInfosProducteur.adapter = null
                return
            }

            producteursList.map {
                CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
            }.let {
                producteursDatas.addAll(it)
            }

            val infosDraftedLocal = ApiClient.gson.fromJson(
                draftedDataInfosProducteur?.datas,
                InfosProducteurDTO::class.java
            )
            selectProducteurInfosProducteur!!.adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

            if (infosDraftedLocal != null) {
                provideDatasSpinnerSelection(
                    selectProducteurInfosProducteur,
                    infosDraftedLocal.producteursNom,
                    producteursDatas
                )
            }

            selectProducteurInfosProducteur.setTitle("Choisir le producteur")
            selectProducteurInfosProducteur.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        val producteurModel = producteursList[position]
                        producteurNomPrenoms =
                            producteurModel.nom?.plus(" ")?.plus(producteurModel.prenoms.toString())
                                ?: ""
                        producteurCode = producteurModel.codeProd ?: ""

                        producteurId = if (producteurModel.isSynced) {
                            producteurModel.id!!.toString()
                        } else {
                            producteurModel.uid.toString()
                        }

                        editCodeInfosProducteur.setText(producteurModel.codeProd)
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupBlesseeSelection() {
        try {
            val arrayBlessees = AssetFileHelper.getListDataFromAsset(14, this@UniteAgricoleProducteurActivity) as MutableList<PersonneBlesseeModel>
                //CcbRoomDatabase.getDatabase(applicationContext)?.persBlesseeDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())!!

            val blesseeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayBlessees)
            selectBlesseeInfosProducteur!!.adapter = blesseeAdapter

            selectBlesseeInfosProducteur.setTitle("Choisir l'action")
            selectBlesseeInfosProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                    actionPersonneBlesse = arrayBlessees[position].nom!!
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }
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
            selectPaperInfosProducteur!!.adapter = typeDocumentAdapter

            selectPaperInfosProducteur.setTitle("Choisir le type")
            selectPaperInfosProducteur.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        typeDocuments = arrayTypeDocuments[position].nom!!
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
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
            selectTicketInfosProducteur!!.adapter = recuAdapter

            selectTicketInfosProducteur.setTitle("Choisir l'action")
            selectTicketInfosProducteur.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        gestionRecu = arrayRecus[position].nom!!
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
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
                            linearMobileMoneyYesNumberContainerInfosProducteur.visibility =
                                View.VISIBLE
                            linearMoneyYesOperateurContainerProducteur.visibility = View.VISIBLE
                        } else {
                            mobileMoneyYesNumber = ""
                            linearMobileMoneyYesNumberContainerInfosProducteur.visibility =
                                View.GONE
                            linearMoneyYesOperateurContainerProducteur.visibility = View.GONE
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


    fun setupBuyMethpdYesNoSelection() {
        try {
            selectBuyInfosProducteur.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        buyMethodYesNo = resources.getStringArray(R.array.bank_paiement)[position]
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


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
            jachereYesSuperficie = editForetYesSuperficieInfosProducteur.text.toString()

            if (producteurId.isEmpty()) {
                showMessage(
                    message = "Choisissez un producteur svp.",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (jachereYesNo.contains("Choisir", ignoreCase = true)) {
                showMessage(
                    message = "Repondez a la question (Jachere ou foret) svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (jachereYesNo == "oui") {
                if (jachereYesSuperficie.isEmpty()) {
                    showMessage(
                        message = "Renseignez la superficie de Jachere ou Forêt svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "Ok",
                        deconnec = false,
                        showNo = false
                    )
                    return
                }
            }

            if (othersCulturesYesNo.contains("Choisir", ignoreCase = true)) {
                showMessage(
                    message = "Repondez a la question (autre culture) svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (othersCulturesYesNo == "oui") {
                if (cultureProducteurs.isEmpty()) {
                    showMessage(
                        message = "Renseignez les autres cultures svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "Ok",
                        deconnec = false,
                        showNo = false
                    )
                    return
                }
            }

            if (travailleursNbre.toInt() < 0) {
                showMessage(
                    message = "Renseignez le nombre de travailleurs svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (travailleursPermanentsNbre.toInt()
                    .plus(travailleursNonPermanentsNbre.toInt()) > travailleursNbre.toInt()
            ) {
                showMessage(
                    message = "Le cumul des travailleurs ne correspond au total renseigné !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (actionPersonneBlesse.contains("Choisir", ignoreCase = true)) {
                showMessage(
                    message = "Repondez à la question (en cas de personne blessée) svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (enfantMaladieOne.isEmpty()) {
                showMessage(
                    message = "Renseignez la maladie n°1 svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (enfantMaladieTwo.isEmpty()) {
                showMessage(
                    message = "Renseignez la maladie n°2 svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (typeDocuments.contains("Choisir", ignoreCase = true)) {
                showMessage(
                    message = "Repondez à la question (Type de document) svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (mobileMoneyYesNo.contains("Choisir", ignoreCase = true)) {
                showMessage(
                    message = "Repondez à la question (Mobile money) svp !",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Ok",
                    deconnec = false,
                    showNo = false
                )
                return
            }

            if (mobileMoneyYesNo == "oui") {
                if (mobileMoneyYesOperateur.isEmpty()) {
                    showMessage(
                        message = "Repondez à la question (quel operateur) svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "Ok",
                        deconnec = false,
                        showNo = false
                    )
                    return
                }
                if (mobileMoneyYesNumber.isEmpty()) {
                    showMessage(
                        message = "Renseignez le numéro mobile money svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "Ok",
                        deconnec = false,
                        showNo = false
                    )
                    return
                }
            }

            val infosProducteursDTO = getUniteAgricoleProducteurObject()


            val intentInfosProducteurPreview = Intent(this, InfosProducteurPreviewActivity::class.java)
            intentInfosProducteurPreview.putExtra("preview", infosProducteursDTO)
            intentInfosProducteurPreview.putExtra("draft_id", draftedDataInfosProducteur?.uid)
            startActivity(intentInfosProducteurPreview)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun getUniteAgricoleProducteurObject(): InfosProducteurDTO {

        val maladiesEnfant = mutableListOf<String>()
        maladiesEnfant.add(enfantMaladieOne)
        maladiesEnfant.add(enfantMaladieTwo)

        val item = InfosProducteurDTO(
            age18 = nbreEnfantUnder18,
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            autresCultures = othersCulturesYesNo,
            compteBanque = bankAccountYesNo,
            foretsjachere = jachereYesNo,
            isSynced = false,
            maladiesenfantsStringify = ApiClient.gson.toJson(maladiesEnfant),
            mobileMoney = mobileMoneyYesNo,
            numeroCompteMM = mobileMoneyYesNumber,
            operateurMM = mobileMoneyYesOperateur,
            paiementMM = buyMethodYesNo,
            persEcole = nbreEnfantUnder18Scolarise,
            personneBlessee = actionPersonneBlesse,
            producteursId = producteurId,
            recuAchat = gestionRecu,
            scolarisesExtrait = nbreEnfantUnder18ScolariseExtrait,
            superficie = jachereYesSuperficie,
            superficiecultureStringify = ApiClient.gson.toJson(cultureProducteurs.map { culture -> culture.superficie }),
            travailleurs = travailleursNbre,
            travailleurspermanents = travailleursPermanentsNbre,
            travailleurstemporaires = travailleursNonPermanentsNbre,
            producteursNom = producteurNomPrenoms,
            typeDocuments = typeDocuments,
            producteursCode = producteurCode,
            typecultureStringify = ApiClient.gson.toJson(cultureProducteurs.map { culture -> culture.label }),
            uid = 0,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            id = 0,
            origin = "local"
        )

        return item

    }


    fun draftInfosProducteur(draftModel: DataDraftedModel?) {
        try {
            val infosProducteursDraft = getUniteAgricoleProducteurObject()

            LogUtils.json(infosProducteursDraft)

            showMessage(
                message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
                context = this,
                finished = false,
                callback = {
                    CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                        DataDraftedModel(
                            uid = draftModel?.uid ?: 0,
                            datas = ApiClient.gson.toJson(infosProducteursDraft),
                            typeDraft = "infos_producteur",
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
                },
                positive = "OUI",
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
            val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()
                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val localitesDatas: MutableList<CommonData> = mutableListOf()
            localitesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                localitesDatas.addAll(it)
            }
            selectLocaliteUniteAgricole.adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
            provideDatasSpinnerSelection(
                selectLocaliteUniteAgricole,
                infosProducteurDrafted.localiteNom,
                localitesDatas
            )

            // Jachere
            provideStringSpinnerSelection(
                selectJachereInfosProducteur,
                infosProducteurDrafted.foretsjachere,
                resources.getStringArray(R.array.YesOrNo)
            )

            // other fams
            provideStringSpinnerSelection(
                selectCulturesInfosProducteur,
                infosProducteurDrafted.autresCultures,
                resources.getStringArray(R.array.YesOrNo)
            )

            // Blesses
            val blessesLists = AssetFileHelper.getListDataFromAsset(14, this@UniteAgricoleProducteurActivity) as MutableList<PersonneBlesseeModel>?
//                CcbRoomDatabase.getDatabase(this)?.persBlesseeDoa()
//                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val blessesDatas: MutableList<CommonData> = mutableListOf()
            blessesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                blessesDatas.addAll(it)
            }
            selectBlesseeInfosProducteur.adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, blessesDatas)
            provideDatasSpinnerSelection(
                selectBlesseeInfosProducteur,
                infosProducteurDrafted.personneBlessee,
                blessesDatas
            )

            // Documents
            val documentsLists = AssetFileHelper.getListDataFromAsset(10, this@UniteAgricoleProducteurActivity) as MutableList<TypeDocumentModel>?
//                CcbRoomDatabase.getDatabase(this)?.typeDocumentDao()
//                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val documentsDatas: MutableList<CommonData> = mutableListOf()
            documentsLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                documentsDatas.addAll(it)
            }
            selectPaperInfosProducteur.adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, documentsDatas)
            provideDatasSpinnerSelection(
                selectPaperInfosProducteur,
                infosProducteurDrafted.typeDocuments,
                documentsDatas
            )

            // Recus
            val recusLists = AssetFileHelper.getListDataFromAsset(3, this@UniteAgricoleProducteurActivity) as MutableList<RecuModel>?
//                CcbRoomDatabase.getDatabase(this)?.recuDao()
//                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val recusDatas: MutableList<CommonData> = mutableListOf()
            recusLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                recusDatas.addAll(it)
            }
            selectTicketInfosProducteur.adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, recusDatas)
            provideDatasSpinnerSelection(
                selectTicketInfosProducteur,
                infosProducteurDrafted.recuAchat,
                recusDatas
            )

            // Mobile operateur
            provideStringSpinnerSelection(
                selectMobileMoneyYesOperateurInfosProducteur,
                infosProducteurDrafted.operateurMM,
                resources.getStringArray(R.array.operateur)
            )

            // Maladie enfants
            if (ListConverters.stringToMutableList(infosProducteurDrafted.maladiesenfantsStringify)
                    ?.isNotEmpty()!!
            ) {
                val maladies =
                    ListConverters.stringToMutableList(infosProducteurDrafted.maladiesenfantsStringify)
                editMaladieOneInfosProducteur.setText(maladies?.first())
                editMaladieTwoInfosProducteur.setText(maladies?.last())
            }

            // mobile money yes no
            provideStringSpinnerSelection(
                selectMoneyInfosProducteur,
                infosProducteurDrafted.mobileMoney,
                resources.getStringArray(R.array.YesOrNo)
            )

            // mobile money yes no
            provideStringSpinnerSelection(
                selectMoneyInfosProducteur,
                infosProducteurDrafted.mobileMoney,
                resources.getStringArray(R.array.YesOrNo)
            )

            // Method paiement
            provideStringSpinnerSelection(
                selectBuyInfosProducteur,
                infosProducteurDrafted.paiementMM,
                resources.getStringArray(R.array.bank_paiement)
            )

            // Bank paiement yes no
            provideStringSpinnerSelection(
                selectBanqueInfosProducteur,
                infosProducteurDrafted.compteBanque,
                resources.getStringArray(R.array.YesOrNo)
            )

            editNbreTravailleursInfosProducteur.setText(infosProducteurDrafted.travailleurs)
            editNbreTravailleursPermanentsInfosProducteur.setText(infosProducteurDrafted.travailleurspermanents)
            editNbreTravailleursNonPermanentInfosProducteur.setText(infosProducteurDrafted.travailleurstemporaires)

            editNbreUnder18InfosProducteur.setText(infosProducteurDrafted.age18)
            editNbreScolariseInfosProducteur.setText(infosProducteurDrafted.persEcole)
            editNbreExtraitInfosProducteur.setText(infosProducteurDrafted.scolarisesExtrait)
            editMobileYesNumberInfosProducteur.setText(infosProducteurDrafted.numeroCompteMM)
            editForetYesSuperficieInfosProducteur.setText(infosProducteurDrafted.superficie)

            // Cultures
            if (ListConverters.stringToMutableList(infosProducteurDrafted.typecultureStringify)
                    ?.isNotEmpty()!!
            ) {
                cultureProducteurs.clear()

                val cultures =
                    ListConverters.stringToMutableList(infosProducteurDrafted.typecultureStringify)
                val culturesSuperficies =
                    ListConverters.stringToMutableList(infosProducteurDrafted.superficiecultureStringify)

                cultures?.zip(culturesSuperficies!!)?.let { culturesSuperficieDatas ->
                    culturesSuperficieDatas.map { cultureSuperficie ->
                        cultureProducteurs.add(
                            CultureProducteurModel(
                                uid = 0,
                                producteurId = infosProducteurDrafted.producteursId.toString()
                                    .toInt(),
                                label = cultureSuperficie.first,
                                superficie = cultureSuperficie.second,
                                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)
                                    .toString()
                            )
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unite_agricole_producteur)

        clickAddFarmInfosProducteur.setOnClickListener {
            try {
                if (producteurId.isEmpty()) {
                    showMessage(
                        "Selectionnez le producteur, svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "OK",
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                if (editCultureInfosProducteur.text.toString()
                        .isEmpty() || editSuperficeInfosProducteur.text.toString().isEmpty()
                ) {
                    showMessage(
                        "Renseignez une culture, svp !",
                        context = this,
                        finished = false,
                        callback = {},
                        positive = "OK",
                        deconnec = false,
                        showNo = false
                    )
                    return@setOnClickListener
                }

                val cultureProducteur = CultureProducteurModel(
                    0,
                    producteurId.toInt(),
                    editCultureInfosProducteur.text.toString().trim(),
                    editSuperficeInfosProducteur.text.toString().trim(),
                    SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )
                addCultureProducteur(cultureProducteur)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        try {
            editForetYesSuperficieInfosProducteur.doAfterTextChanged { editable ->
                LogUtils.e(TAG, editable.toString().trim())
                jachereYesSuperficie = editable.toString().trim()
            }

            editNbreTravailleursInfosProducteur.doAfterTextChanged { editable ->
                travailleursNbre =
                    if (editable?.toString()?.isEmpty()!!) "0" else editable.toString().trim()
            }

            editNbreTravailleursPermanentsInfosProducteur.doAfterTextChanged { editable ->
                travailleursPermanentsNbre =
                    if (editable?.toString()?.isEmpty()!!) "0" else editable.toString().trim()
            }

            editNbreTravailleursNonPermanentInfosProducteur.doAfterTextChanged { editable ->
                travailleursNonPermanentsNbre =
                    if (editable?.toString()?.isEmpty()!!) "0" else editable.toString().trim()
            }

            editNbreUnder18InfosProducteur.doAfterTextChanged {
                nbreEnfantUnder18 = if (it?.toString()?.isEmpty()!!) "0" else it.toString().trim()
            }

            editNbreScolariseInfosProducteur.doAfterTextChanged {
                nbreEnfantUnder18Scolarise =
                    if (it?.toString()?.isEmpty()!!) "0" else it.toString().trim()
            }

            editNbreExtraitInfosProducteur.doAfterTextChanged {
                nbreEnfantUnder18ScolariseExtrait =
                    if (it?.toString()?.isEmpty()!!) "0" else it.toString().trim()
            }

            editMaladieOneInfosProducteur.doAfterTextChanged {
                enfantMaladieOne = it.toString().trim()
            }

            editMobileYesNumberInfosProducteur.doAfterTextChanged {
                mobileMoneyYesNumber = it.toString().trim()
            }

            editMaladieTwoInfosProducteur.doAfterTextChanged {
                enfantMaladieTwo = it.toString().trim()
            }

            clickReviewInfosProducteur.setOnClickListener {
                collectDatas()
            }

            clickCloseBtn.setOnClickListener {
                finish()
            }

            imageDraftBtn.setOnClickListener {
                draftInfosProducteur(draftedDataInfosProducteur ?: DataDraftedModel(uid = 0))
            }

            setCultureProducteurs()
            setupCultureYesNoSelection()
            setupJachereYesNoSelection()
            //setupProducteurSelection()
            setupBlesseeSelection()
            setupLocaliteSelection()
            setupGestionRecusSelection()
            setupTypeDocumentsSelection()
            setupOperateursSelection()
            setupBankAccountYesNoSelection()
            setupMoneyYesNoSelection()
            setupBuyMethpdYesNoSelection()

            applyFilters(editNbreUnder18InfosProducteur)
            applyFilters(editNbreTravailleursInfosProducteur)
            applyFilters(editNbreTravailleursNonPermanentInfosProducteur)
            applyFilters(editNbreTravailleursPermanentsInfosProducteur)
            applyFilters(editNbreScolariseInfosProducteur)

            if (intent.getStringExtra("from") != null) {
                draftedDataInfosProducteur =
                    CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                        ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0))
                        ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataInfosProducteur!!)
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    override fun itemSelected(position: Int, item: CultureProducteurModel) {
        TODO("Not yet implemented")
    }
}
