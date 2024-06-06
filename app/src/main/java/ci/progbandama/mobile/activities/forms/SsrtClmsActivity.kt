package ci.progbandama.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.views.MultiSelectSpinner
import ci.progbandama.mobile.activities.forms.views.MultiSelectSpinner.OnMultipleItemsSelectedListener
import ci.progbandama.mobile.activities.infospresenters.EnqueteSsrtPreviewActivity
import ci.progbandama.mobile.models.*
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.configHour
import ci.progbandama.mobile.tools.Commons.Companion.showMessage
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_ssrt_clms.*
import org.joda.time.DateTime
import java.util.*


class SsrtClmsActivity : AppCompatActivity(R.layout.activity_ssrt_clms) {


    var liensParenteList: MutableList<LienParenteModel>? = mutableListOf()
    var producteurNom = ""
    var producteurId = ""

    var parentSexe = ""

    var localiteNom = ""
    var localiteId = ""

    var schoolYesNo = ""
    var schoolOldYesNo = ""
    var schoolLevel = ""
    var schoolOldLevel = ""
    var sexeSsrtValue = ""
    var schoolClass = ""
    var schoolPlaceYesNo = ""
    var schoolNoPlaceDistance = ""
    var schoolNoPlaceName = ""
    var schoolNoPlaceMoyenTransport = ""

    var lienParente = ""
    var flagBirthday = false

    var stoppedSchoolReasonList = mutableListOf<String>()
    var travauxDangereuxList = mutableListOf<String>()
    var travauxLegersList = mutableListOf<String>()
    var lieuTravauxLegerList = mutableListOf<String>()
    var lieuTravauxDangereuxList = mutableListOf<String>()

    var localitesList: MutableList<LocaliteModel>? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var draftedSsrteModel: DataDraftedModel? = null
    var fromAction = ""

    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();

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
            spinner = selectSectionSsrte,
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
            spinner = selectLocaliteSsrte,
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
        producteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())?: arrayListOf<ProducteurModel>()

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if(it.uid.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }else{
                    if(it.id.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }
                //if ("${it.nom} ${it.prenoms}".equals(idc, ignoreCase = true)) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurSsrte,
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


    fun setupTravauxDangereuxLieuSelection() {
        val travauxDangereuxResourcesLieu = resources.getStringArray(R.array.recentWorkLieu)

        var listSelectRecentWorkLieu = mutableListOf<Int>()

        val multiSelectSpinner = MultiSelectSpinner(this)//selectRecentWorkLieuSsrt
        multiSelectSpinner.setTitle(getString(R.string.selectionnez_les_lieux))
        multiSelectSpinner.setItems(travauxDangereuxResourcesLieu)
        //multiSelectSpinner.hasNoneOption(true)
        multiSelectSpinner.setSelection(listSelectRecentWorkLieu.toIntArray())
        multiSelectSpinner.setListener(object : OnMultipleItemsSelectedListener{
            override fun selectedIndices(indices: MutableList<Int>?) {
                listSelectRecentWorkLieu.clear()
                listSelectRecentWorkLieu.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                lieuTravauxDangereuxList.clear()
                lieuTravauxDangereuxList.addAll(strings?.toMutableList() ?: arrayListOf())
            }

        })
    }


    fun configDate(viewClciked: AppCompatEditText) {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
            viewClciked.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
        }, year, month, dayOfMonth)

        if (flagBirthday) {
            datePickerDialog.datePicker.minDate = 1104534000000
            datePickerDialog.datePicker.maxDate = 1575154800000
        } else {
            datePickerDialog.datePicker.maxDate = DateTime.now().millis
        }

        datePickerDialog.show()
    }


    fun setAllListener() {


        setupSectionSelection()

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_est_le_lien_de_parent_avec_le_producteur),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectLienParentSsrt,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = resources.getStringArray(R.array.parentAffiliation)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearAutreLienParentContainerSsrte.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_niveau_d_tude_as_tu_atteint),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSchoolLevelSsrt,
            listIem = resources.getStringArray(R.array.schoolLevelFull)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })


        Commons.setListenerForSpinner(this,
            getString(R.string.va_t_il_l_cole),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSchoolStatusSsrt,
            itemChanged = arrayListOf(Pair(1, getString(R.string.non)), Pair(2, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerAvoirFrequPasseSsrt.visibility = visibility
                    containerVaDejaALecole.visibility = GONE
                }else if (itemId == 2) {
                    containerVaDejaALecole.visibility = visibility
                    containerAvoirFrequPasseSsrt.visibility = GONE
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_t_l_cole_par_le_pass),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectAvoirFrequPasseSsrt,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerAvoirFaitEcole.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.ton_cole_est_elle_situ_e_dans_le_village),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSchoolInPlaceYesNoSsrt,
            itemChanged = arrayListOf(Pair(1, getString(R.string.non))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearVillageDistanceContainerSsrt.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_niveau_d_tude_as_tu_atteint),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectNiveauEtudeAtteintSsrt,
            listIem = resources.getStringArray(R.array.schoolLevelFull)
                ?.toList() ?: listOf(),
            onChanged = {
                when (it) {
                    0 -> {
                        val classLevels = resources.getStringArray(R.array.maternelleLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels, )
                    }
                    1 -> {
                        val classLevels = resources.getStringArray(R.array.primaireLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels)
                    }
                    2 -> {
                        val classLevels = resources.getStringArray(R.array.oneCycleLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels)
                    }
                    3 -> {
                        val classLevels = resources.getStringArray(R.array.twoCycleLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels)
                    }
                    4 -> {
                        val classLevels = arrayOf<String>()
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels)
                    }
                }
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setupItemMultiSelection(this, selectRaisonArretEcoleSSrte,
            getString(R.string.pourquoi_ne_vas_tu_pas_l_cole_ou_arr_t_l_cole), resources.getStringArray(R.array.noSchoolRaison).map { CommonData(0, it.toString()) }){
            //if(it.contains("Autre")) containerAutreRaisonArretEcole.visibility = View.VISIBLE
        }

        Commons.setupItemMultiSelection(this, selectLequelTravEffectSSrte,
            getString(R.string.au_cours_de_ces_2_derni_res_ann_es_lequel_de_ces_travaux_dangereux_as_tu_effectu), resources.getStringArray(R.array.recentWorkHard).map { CommonData(0, it.toString()) },
            currentList = arrayListOf()){

        }

        Commons.setupItemMultiSelection(this, selectEndroitTravEffectSSrte,
            getString(R.string.o_as_tu_effectu_ces_travaux_dangereux), resources.getStringArray(R.array.recentWorkLieu).map { CommonData(0, it.toString()) },
            currentList = arrayListOf()){

        }

        Commons.setupItemMultiSelection(this, selectLequelTrav2EffectSSrte,
            getString(R.string.au_cours_de_ces_2_derni_res_ann_es_lequel_de_ces_travaux_l_gers_as_tu_effectu), resources.getStringArray(R.array.recentWorkLight).map { CommonData(0, it.toString()) },
            currentList = arrayListOf()){

        }

        Commons.setupItemMultiSelection(this, selectEndroitTrav2EffectSSrte,
            getString(R.string.o_as_tu_effectu_ces_travaux_l_gers), resources.getStringArray(R.array.recentWorkLieu).map { CommonData(0, it.toString()) },
            currentList = arrayListOf()
        ){

        }

    }

    private fun setupClasseSelectedListen(
        selectClasseLevelSsrt: AppCompatSpinner?,
        classLevels: Array<String>,
        currentVal: String? = null
    ) {

        LogUtils.d(currentVal)
        Commons.setListenerForSpinner(this,
            getString(R.string.quelle_est_sa_classe_actuelle),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectClasseLevelSsrt!!,
            currentVal = currentVal,
            listIem = classLevels
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrollableMarquee() {
        // labelInfosScrollerSsrt?.focusable = View.FOCUSABLE
        labelInfosScrollerSsrt.isFocusableInTouchMode = true
        labelInfosScrollerSsrt.marqueeRepeatLimit = 3
        labelInfosScrollerSsrt.ellipsize = TextUtils.TruncateAt.MARQUEE
    }


    fun  collectDatas() {

        val itemModel = getEnqueteSsrtObjet()

        if(itemModel == null) return

        val enqueteModel = itemModel?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localiteId = localiteCommon.id.toString()
                producteursId = producteurCommon.id.toString()

                travauxDangereuxStringify = GsonUtils.toJson(selectLequelTravEffectSSrte.selectedStrings)
                travauxLegersStringify = GsonUtils.toJson(selectLequelTrav2EffectSSrte.selectedStrings)

                lieuTravauxDangereuxStringify = GsonUtils.toJson(selectEndroitTravEffectSSrte.selectedStrings)
                lieuTravauxLegersStringify = GsonUtils.toJson(selectEndroitTrav2EffectSSrte.selectedStrings)
            }
        }

        val mapEntries: List<MapEntry>? = itemModel?.second?.apply {

        }?.map { MapEntry(it.first, it.second) }.apply {

        }

        Commons.printModelValue(enqueteModel as Object, mapEntries)

        try {
            val intentEnquetePreview = Intent(this, EnqueteSsrtPreviewActivity::class.java)
            intentEnquetePreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentEnquetePreview.putExtra("preview", enqueteModel)
            intentEnquetePreview.putExtra("draft_id", draftedSsrteModel?.uid)
            startActivity(intentEnquetePreview)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun getEnqueteSsrtObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()):  Pair<EnqueteSsrtModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

//        return  EnqueteSsrtModel(
//            autreLienParente = editAutreLienParentSsrt.text.toString().trim(),
//            avoirFrequente = schoolOldYesNo,
//            classe = schoolClass,
//            codeMembre = "",
//            dateEnquete = DateTime.now().toString(DateTimeFormat.forPattern("dd-MM-yyyy")),
//            datenaissMembre = editDateNaissancelSsrt.text.toString(),
//            distanceEcole = schoolNoPlaceDistance,
//            ecoleVillage = schoolPlaceYesNo,
//            frequente = schoolYesNo,
//            endpoint = "",
//            lienParente = lienParente,
//            moyenTransport = schoolNoPlaceMoyenTransport,
//            niveauEtude = schoolLevel,
//            niveauEtudeAtteint = schoolOldLevel,
//            nomEcole = editSchoolNameSsrt.text.toString(),
//            nomMembre = editMembreNomlSsrt.text.toString(),
//            prenomMembre = editMembrePrenomlSsrt.text.toString(),
//            producteursId = producteurId,
//            lieuTravauxDangereuxStringify = ApiClient.gson.toJson(lieuTravauxDangereuxList),
//            lieuTravauxLegersStringify = ApiClient.gson.toJson(lieuTravauxLegerList),
//            raisonArretEcoleStringify = ApiClient.gson.toJson(stoppedSchoolReasonList),
//            travauxDangereuxStringify = ApiClient.gson.toJson(travauxDangereuxList),
//            travauxLegersStringify = ApiClient.gson.toJson(travauxLegersList),
//            sexeMembre = sexeSsrtValue,
//            isSynced = false,
//            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID),
//            localiteNom = localiteNom,
//            producteurNom = producteurNom,
//            uid = 0,
//        )

        var itemList = getSetupSsteModel(EnqueteSsrtModel(isSynced = false, uid = 0, userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),  origin = "local",), mutableListOf<Pair<String,String>>())
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

        return itemList

    }

    private fun getSetupSsteModel(enqueteSsrtModel: EnqueteSsrtModel, mutableListOf: MutableList<Pair<String, String>>): Pair<EnqueteSsrtModel, MutableList<Pair<String, String>>> {
        val mainLayout = findViewById<ViewGroup>(R.id.layout_ssrte)
        Commons.getAllTitleAndValueViews(
            mainLayout, enqueteSsrtModel, false,
            mutableListOf
        )
        return Pair(enqueteSsrtModel, mutableListOf)
    }

    fun passSetupSsteModel(
        model: EnqueteSsrtModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_ssrte)
        model?.let {
            Commons.setAllValueOfTextViews(mainLayout, model)
        }
    }


    fun draftSsrte(draftModel: DataDraftedModel?) {
        val itemModel = getEnqueteSsrtObjet(false, necessaryItem = mutableListOf(
            "Selectionner un producteur"
        ))

        if(itemModel == null) return

        val enqueteModel = itemModel?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localiteId = localiteCommon.id.toString()
                producteursId = producteurCommon.id.toString()

                travauxDangereuxStringify = GsonUtils.toJson(selectLequelTravEffectSSrte.selectedStrings)
                travauxLegersStringify = GsonUtils.toJson(selectLequelTrav2EffectSSrte.selectedStrings)

                lieuTravauxDangereuxStringify = GsonUtils.toJson(selectEndroitTravEffectSSrte.selectedStrings)
                lieuTravauxLegersStringify = GsonUtils.toJson(selectEndroitTrav2EffectSSrte.selectedStrings)
            }
        }
        LogUtils.d(enqueteModel)


        showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(enqueteModel),
                        typeDraft = "ssrte",
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
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        val ssrteDrafted =  ApiClient.gson.fromJson(draftedData.datas, EnqueteSsrtModel::class.java)

        LogUtils.d(draftedData)

        setupSectionSelection(ssrteDrafted.section, ssrteDrafted.localiteId, ssrteDrafted.producteursId)

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_est_le_lien_de_parent_avec_le_producteur),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectLienParentSsrt,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            currentVal = ssrteDrafted.lienParente,
            listIem = resources.getStringArray(R.array.parentAffiliation)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearAutreLienParentContainerSsrte.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_niveau_d_tude_as_tu_atteint),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSchoolLevelSsrt,
            currentVal = ssrteDrafted.niveauEtudeAtteint,
            listIem = resources.getStringArray(R.array.schoolLevelFull)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })


        Commons.setListenerForSpinner(this,
            getString(R.string.va_t_il_l_cole),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSchoolStatusSsrt,
            itemChanged = arrayListOf(Pair(1, getString(R.string.non)), Pair(2, getString(R.string.oui))),
            currentVal = ssrteDrafted.frequente,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerAvoirFrequPasseSsrt.visibility = visibility
                    containerVaDejaALecole.visibility = GONE
                }else if (itemId == 2) {
                    containerVaDejaALecole.visibility = visibility
                    containerAvoirFrequPasseSsrt.visibility = GONE
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_t_l_cole_par_le_pass),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectAvoirFrequPasseSsrt,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            currentVal = ssrteDrafted.avoirFrequente,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerAvoirFaitEcole.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.ton_cole_est_elle_situ_e_dans_le_village),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSchoolInPlaceYesNoSsrt,
            itemChanged = arrayListOf(Pair(1, getString(R.string.non))),
            currentVal = ssrteDrafted.ecoleVillage,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearVillageDistanceContainerSsrt.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quel_niveau_d_tude_as_tu_atteint),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectNiveauEtudeAtteintSsrt,
            currentVal = ssrteDrafted.niveauEtude,
            listIem = resources.getStringArray(R.array.schoolLevelFull)
                ?.toList() ?: listOf(),
            onChanged = {
                when (it) {
                    0 -> {
                        val classLevels = resources.getStringArray(R.array.maternelleLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels, currentVal = ssrteDrafted.classe)
                    }
                    1 -> {
                        val classLevels = resources.getStringArray(R.array.primaireLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels, currentVal = ssrteDrafted.classe)
                    }
                    2 -> {
                        val classLevels = resources.getStringArray(R.array.oneCycleLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels, currentVal = ssrteDrafted.classe)
                    }
                    3 -> {
                        val classLevels = resources.getStringArray(R.array.twoCycleLevel)
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels, currentVal = ssrteDrafted.classe)
                    }
                    4 -> {
                        val classLevels = arrayOf<String>()
                        setupClasseSelectedListen(selectClasseLevelSsrt, classLevels, currentVal = ssrteDrafted.classe)
                    }
                }
            },
            onSelected = { itemId, visibility ->
            })

        (if(!ssrteDrafted.autreRaisonArretEcole.isNullOrEmpty()) Commons.returnStringList(ssrteDrafted.autreRaisonArretEcole)?.toMutableList() else mutableListOf<String>())?.let {
            Commons.setupItemMultiSelection(this, selectRaisonArretEcoleSSrte,
                getString(R.string.pourquoi_ne_vas_tu_pas_l_cole_ou_arr_t_l_cole), resources.getStringArray(R.array.noSchoolRaison).map { CommonData(0, it.toString()) },
                currentList = it
            ){
                //if(it.contains("Autre")) containerAutreRaisonArretEcole.visibility = View.VISIBLE
            }
        }

        (if(!ssrteDrafted.travauxDangereuxStringify.isNullOrEmpty()) Commons.returnStringList(ssrteDrafted.travauxDangereuxStringify)?.toMutableList() else mutableListOf<String>())?.let {

            Commons.setupItemMultiSelection(this, selectLequelTravEffectSSrte,
                getString(R.string.au_cours_de_ces_2_derni_res_ann_es_lequel_de_ces_travaux_dangereux_as_tu_effectu), resources.getStringArray(R.array.recentWorkHard).map { CommonData(0, it.toString()) },
                currentList = it){

            }
        }

        (if(!ssrteDrafted.lieuTravauxDangereuxStringify.isNullOrEmpty()) Commons.returnStringList(ssrteDrafted.lieuTravauxDangereuxStringify)?.toMutableList() else mutableListOf<String>())?.let {

            Commons.setupItemMultiSelection(this, selectEndroitTravEffectSSrte,
                getString(R.string.o_as_tu_effectu_ces_travaux_dangereux), resources.getStringArray(R.array.recentWorkLieu).map { CommonData(0, it.toString()) },
                currentList = it){

            }
        }

        (if(!ssrteDrafted.travauxLegersStringify.isNullOrEmpty()) Commons.returnStringList(ssrteDrafted.travauxLegersStringify)?.toMutableList() else mutableListOf<String>())?.let {

            Commons.setupItemMultiSelection(this, selectLequelTrav2EffectSSrte,
                getString(R.string.au_cours_de_ces_2_derni_res_ann_es_lequel_de_ces_travaux_l_gers_as_tu_effectu), resources.getStringArray(R.array.recentWorkLight).map { CommonData(0, it.toString()) },
                currentList = it){

            }
        }

        (if(!ssrteDrafted.lieuTravauxLegersStringify.isNullOrEmpty()) Commons.returnStringList(ssrteDrafted.lieuTravauxLegersStringify)?.toMutableList() else mutableListOf<String>())?.let {

            Commons.setupItemMultiSelection(this, selectEndroitTrav2EffectSSrte,
                getString(R.string.o_as_tu_effectu_ces_travaux_l_gers), resources.getStringArray(R.array.recentWorkLieu).map { CommonData(0, it.toString()) },
                currentList = it){

            }
        }

        passSetupSsteModel(ssrteDrafted)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        labelInfosScrollerSsrt.setOnClickListener {
            // scrollableMarquee()
        }

        editDateNaissancelSsrt.setOnClickListener {
            flagBirthday = true
            configDate(editDateNaissancelSsrt)
        }

        editDateEnqueteSsrt.setOnClickListener {
            flagBirthday = false
            configDate(editDateEnqueteSsrt)
        }

        clickReviewEnqueteSsrt.setOnClickListener {
            collectDatas()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageDraftBtn.setOnClickListener {
            draftSsrte(draftedSsrteModel?: DataDraftedModel(uid = 0))
        }

        setOtherListener()

        if (intent.getStringExtra("from") != null) {
            fromAction = intent.getStringExtra("from") ?: ""
            draftedSsrteModel = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedSsrteModel!!)
        }else{
            setAllListener()
        }

        //applyFilters(editVillageDistanceSsrt)
    }

    private fun setOtherListener() {

        Commons.addNotZeroAtFirstToET(editVillageDistanceSsrt)

        editDateNaissancelSsrt.setOnClickListener { configDate(editDateNaissancelSsrt) }
        editDateEnqueteSsrt.setOnClickListener { configDate(editDateEnqueteSsrt) }
        editDureeFormation.setOnClickListener { configHour(editDureeFormation) }

    }
}
