package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner.OnMultipleItemsSelectedListener
import ci.projccb.mobile.activities.infospresenters.EnqueteSsrtPreviewActivity
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.configHour
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_ssrt_clms.*
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.selectLocaliteUniteAgricole
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.selectProducteurInfosProducteur
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.selectSectionInfProducteur
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
            "Choix de la section !",
            "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
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
            "Choix de la localité !",
            "La liste des localités semble vide, veuillez procéder à la synchronisation des données svp.",
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
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())?: arrayListOf<ProducteurModel>()

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if ("${it.nom} ${it.prenoms}".equals(idc, ignoreCase = true)) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix du producteur !",
            "La liste des producteurs semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurSsrte,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    producteurCommon.id = producteur.id!!

                    //setupParcelleSelection(producteurCommon.id.toString(), currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }


//    fun setupLienParenteSelection() {
//        liensParenteList = CcbRoomDatabase.getDatabase(applicationContext)?.lienParenteDao()?.getAll(agentID = SPUtils.getInstance().getInt(
//            Constants.AGENT_ID, 0).toString())
//
//        val lienParenteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, liensParenteList!!)
//        selectLienParentSsrt!!.adapter = lienParenteAdapter
//
//        selectLienParentSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val lienParent = liensParenteList!![position]
//                lienParente = lienParent.nom!!
//
//                if (lienParente.uppercase().contains("AUTRE")) linearAutreLienParentContainerSsrte.visibility = VISIBLE
//                else linearAutreLienParentContainerSsrte.visibility = GONE
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


    fun setupProducteurSelection(localite: String?) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = localite) ?: mutableListOf()
        val producteursDatas: MutableList<CommonData> = mutableListOf()

        LogUtils.e("Liste des producteurs par Localité $localite")

        if (producteursList!!.isEmpty()) {
            showMessage(
                "Aucun producteur dans cette localité ! Refaite une mise à jour.",
                this,
                finished = false,
                callback = {},
                "Compris !",
                false,
                showNo = false,
            )

            producteurId = ""
            producteurNom = ""
            //selectProducteurSsrt.adapter = null
        } else {
            producteursList?.map {
                CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
            }?.let {
                producteursDatas.addAll(it)
            }

            val ssrteDraftedLocal = ApiClient.gson.fromJson(draftedSsrteModel?.datas, EnqueteSsrtModel::class.java)
            //selectProducteurSsrt!!.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

            if (ssrteDraftedLocal != null) {
//                provideDatasSpinnerSelection(
//                    selectProducteurSsrt,
//                    ssrteDraftedLocal.producteurNom,
//                    producteursDatas
//                )
            }

            //selectProducteurSsrt.setTitle("Choisir le producteur")
//            selectProducteurSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                    val producteur = producteursList!![position]
//                    producteurNom = "${producteur.nom} ${producteur.prenoms}"
//
//                    producteurId = if (producteur.isSynced) {
//                        producteur.id!!.toString()
//                    } else {
//                        producteur.uid.toString()
//                    }
//                }
//
//                override fun onNothingSelected(arg0: AdapterView<*>) {
//                }
//            }
        }
    }


//    fun setupDejaScholariseYesNoSelection() {
//        selectDejaScolariseSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                schoolOldYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                when (schoolOldYesNo.uppercase()) {
//                    "OUI" -> {
//                        setupOldSchoolLevelSelection()
//                        linearNiveauEtudeAtteintContainerSsrt.visibility = VISIBLE
//                    }
//                    "NON" -> {
//                        schoolOldLevel = ""
//                        linearNiveauEtudeAtteintContainerSsrt.visibility = GONE
//                    }
//                    else -> {
//                        linearNiveauEtudeAtteintContainerSsrt.visibility = GONE
//                    }
//                }
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//        }
//    }


//    fun setupSchoolStatusSelection() {
//        selectSchoolStatusSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                schoolYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//                setSchoolLevelSelection()
//                when (schoolYesNo.uppercase()) {
//                    "OUI" -> {
//                        setSchoolLevelSelection()
//                        linearSchoolLevelContainerSsrt.visibility = VISIBLE
//                        linearSchoolNameContainerSsrt.visibility = VISIBLE
//                        linearVillageDistanceContainerSsrt.visibility = VISIBLE
//                        linearMoyenDeplacementContainerSsrt.visibility = VISIBLE
//                        linearClasseLevelContainerSsrt.visibility = VISIBLE
//                        // linearSchoolInPlaceYesNoContainerSsrt.visibility = VISIBLE
//
//                        linearDejaScolariseContainerSsrt.visibility = GONE
//                        linearNoSchoolRaisonContainerSsrt.visibility = GONE
//                        linearNiveauEtudeAtteintContainerSsrt.visibility = GONE
//                    }
//                    "NON" -> {
//                        linearDejaScolariseContainerSsrt.visibility = VISIBLE
//                        linearNoSchoolRaisonContainerSsrt.visibility = VISIBLE
//                        linearNiveauEtudeAtteintContainerSsrt.visibility = VISIBLE
//
//                        linearSchoolLevelContainerSsrt.visibility = GONE
//                        linearSchoolInPlaceYesNoContainerSsrt.visibility = GONE
//                        linearClasseLevelContainerSsrt.visibility = GONE
//                        linearSchoolNameContainerSsrt.visibility = GONE
//                        linearVillageDistanceContainerSsrt.visibility = GONE
//                        linearMoyenDeplacementContainerSsrt.visibility = GONE
//
//                        setupDejaScholariseYesNoSelection()
//                    }
//                    else -> {
//                        linearSchoolLevelContainerSsrt.visibility = GONE
//                        linearSchoolInPlaceYesNoContainerSsrt.visibility = GONE
//                        linearMoyenDeplacementContainerSsrt.visibility = GONE
//                        linearDejaScolariseContainerSsrt.visibility = GONE
//                        linearSchoolNameContainerSsrt.visibility = GONE
//                        linearVillageDistanceContainerSsrt.visibility = GONE
//
//
//                        linearDejaScolariseContainerSsrt.visibility = GONE
//                        linearNoSchoolRaisonContainerSsrt.visibility = GONE
//                        linearNiveauEtudeAtteintContainerSsrt.visibility = GONE
//
//
//                        linearSchoolLevelContainerSsrt.visibility = GONE
//                        linearSchoolNameContainerSsrt.visibility = GONE
//                        linearVillageDistanceContainerSsrt.visibility = GONE
//                        linearMoyenDeplacementContainerSsrt.visibility = GONE
//                    }
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//                linearSchoolLevelContainerSsrt.visibility = GONE
//                linearClasseLevelContainerSsrt.visibility = GONE
//                linearSchoolInPlaceYesNoContainerSsrt.visibility = GONE
//            }
//        }
//    }


    fun setupSexeSsrtSelection() {
        selectSexeSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                sexeSsrtValue = resources.getStringArray(R.array.genre_membre)[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }
//    fun setupOldSchoolLevelSelection() {
//        selectNiveauEtudeAtteintSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                schoolOldLevel = resources.getStringArray(R.array.schoolLevelFull)[position]
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//        }
//    }


//    fun setupStoppedSchoolRaisonSelection() {
//        val stoppedSchoolReason = resources.getStringArray(R.array.noSchoolRaison)
        //val multiSelectionReason: MutableList<KeyPairBoolData> = mutableListOf()

//        for (i in stoppedSchoolReason.indices) {
//            val h = KeyPairBoolData()
//            h.id = (i + 1).toLong()
//            h.name = stoppedSchoolReason[i]
//            h.isSelected = false
//            multiSelectionReason.add(h)
//        }
//
//        selectNoSchoolRaisonSsrt.setItems(multiSelectionReason) { items ->
//            for (i in items.indices) {
//                if (items[i].isSelected) {
//                    stoppedSchoolReasonList.add(items[i].name)
//                }
//            }
//        }

//        var listSelectRaison = mutableListOf<Int>()
//
//        val multiSelectSpinner = selectNoSchoolRaisonSsrt
//        multiSelectSpinner.setTitle("Choisir la raison")
//        multiSelectSpinner.setItems(stoppedSchoolReason)
//        //multiSelectSpinner.hasNoneOption(true)
//        multiSelectSpinner.setSelection(listSelectRaison.toIntArray())
//        multiSelectSpinner.setListener(object : OnMultipleItemsSelectedListener{
//            override fun selectedIndices(indices: MutableList<Int>?) {
//                listSelectRaison.clear()
//                listSelectRaison.addAll(indices?.toMutableList() ?: mutableListOf())
//            }
//
//            override fun selectedStrings(strings: MutableList<String>?) {
//                stoppedSchoolReasonList.clear()
//                stoppedSchoolReasonList.addAll(strings?.toMutableList() ?: arrayListOf())
//            }
//
//        })
//
//    }


//    fun setupTravauxDangereuxSelection() {
//        val travauxDangereuxResources = resources.getStringArray(R.array.recentWork)
////        val multiSelectionTravauxDangereux: MutableList<KeyPairBoolData> = mutableListOf()
////
////        for (i in travauxDangereuxResources.indices) {
////            val h = KeyPairBoolData()
////            h.id = (i + 1).toLong()
////            h.name = travauxDangereuxResources[i]
////            h.isSelected = false
////            multiSelectionTravauxDangereux.add(h)
////        }
////
////        selectRecentWorkSsrt.setItems(multiSelectionTravauxDangereux) { items ->
////            for (i in items.indices) {
////                if (items[i].isSelected) {
////                    travauxDangereuxList.add(items[i].name)
////                }
////            }
////        }
//
//        var listSelectTravauxDangereux = mutableListOf<Int>()
//
//        val multiSelectSpinner = selectRecentWorkSsrt
//        multiSelectSpinner.setTitle("Selectionnez les travaux")
//        multiSelectSpinner.setItems(travauxDangereuxResources)
//        //multiSelectSpinner.hasNoneOption(true)
//        multiSelectSpinner.setSelection(listSelectTravauxDangereux.toIntArray())
//        multiSelectSpinner.setListener(object : OnMultipleItemsSelectedListener{
//            override fun selectedIndices(indices: MutableList<Int>?) {
//                listSelectTravauxDangereux.clear()
//                listSelectTravauxDangereux.addAll(indices?.toMutableList() ?: mutableListOf())
//            }
//
//            override fun selectedStrings(strings: MutableList<String>?) {
//                travauxDangereuxList.clear()
//                travauxDangereuxList.addAll(strings?.toMutableList() ?: arrayListOf())
//            }
//
//        })
//
//    }


    fun setupTravauxDangereuxLieuSelection() {
        val travauxDangereuxResourcesLieu = resources.getStringArray(R.array.recentWorkLieu)
//        val multiSelectionTravauxDangereuxLieu: MutableList<KeyPairBoolData> = mutableListOf()
//
//        for (i in travauxDangereuxResourcesLieu.indices) {
//            val h = KeyPairBoolData()
//            h.id = (i + 1).toLong()
//            h.name = travauxDangereuxResourcesLieu[i]
//            h.isSelected = false
//            multiSelectionTravauxDangereuxLieu.add(h)
//        }
//
//        selectRecentWorkLieuSsrt.setItems(multiSelectionTravauxDangereuxLieu) { items ->
//            for (i in items.indices) {
//                if (items[i].isSelected) {
//                    lieuTravauxDangereuxList.add(items[i].name)
//                }
//            }
//        }

        var listSelectRecentWorkLieu = mutableListOf<Int>()

        val multiSelectSpinner = MultiSelectSpinner(this)//selectRecentWorkLieuSsrt
        multiSelectSpinner.setTitle("Selectionnez les lieux")
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


    fun setupTravauxLegersSelection() {
        val travauxLegersResources = resources.getStringArray(R.array.recentWorkLight)
//        val multiSelectionTravauxLegers: MutableList<KeyPairBoolData> = mutableListOf()
//
//        for (i in travauxLegersResources.indices) {
//            val h = KeyPairBoolData()
//            h.id = (i + 1).toLong()
//            h.name = travauxLegersResources[i]
//            h.isSelected = false
//            multiSelectionTravauxLegers.add(h)
//        }
//
//        selectRecentWorkLightSsrt.setItems(multiSelectionTravauxLegers) { items ->
//            for (i in items.indices) {
//                if (items[i].isSelected) {
//                    travauxLegersList.add(items[i].name)
//                }
//            }
//        }

//        var listSelectLegersResources = mutableListOf<Int>()
//
//        val multiSelectSpinner = selectRecentWorkLightSsrt
//        multiSelectSpinner.setTitle("Selectionnez les travaux")
//        multiSelectSpinner.setItems(travauxLegersResources)
//        //multiSelectSpinner.hasNoneOption(true)
//        multiSelectSpinner.setSelection(listSelectLegersResources.toIntArray())
//        multiSelectSpinner.setListener(object : OnMultipleItemsSelectedListener{
//            override fun selectedIndices(indices: MutableList<Int>?) {
//                listSelectLegersResources.clear()
//                listSelectLegersResources.addAll(indices?.toMutableList() ?: mutableListOf())
//            }
//
//            override fun selectedStrings(strings: MutableList<String>?) {
//                travauxLegersList.clear()
//                travauxLegersList.addAll(strings?.toMutableList() ?: arrayListOf())
//            }
//
//        })

    }


    fun setupTravauxLegersLieuSelection() {
        val travauxLegersLieuResources = resources.getStringArray(R.array.recentWorkLieu)
//        val multiSelectionTravauxLegersLieu: MutableList<KeyPairBoolData> = mutableListOf()
//
//        for (i in travauxLegersLieuResources.indices) {
//            val h = KeyPairBoolData()
//            h.id = (i + 1).toLong()
//            h.name = travauxLegersLieuResources[i]
//            h.isSelected = false
//            multiSelectionTravauxLegersLieu.add(h)
//        }
//
//        selectRecentWorkLieuLightSsrt.setItems(multiSelectionTravauxLegersLieu) { items ->
//            for (i in items.indices) {
//                if (items[i].isSelected) {
//                    lieuTravauxLegerList.add(items[i].name)
//                }
//            }
//        }
        var listSelectLegersLieu = mutableListOf<Int>()

        val multiSelectSpinner = MultiSelectSpinner(this)//selectRecentWorkLieuLightSsrt
        multiSelectSpinner.setTitle("Selectionnez les lieux")
        multiSelectSpinner.setItems(travauxLegersLieuResources)
        //multiSelectSpinner.hasNoneOption(true)
        multiSelectSpinner.setSelection(listSelectLegersLieu.toIntArray())
        multiSelectSpinner.setListener(object : OnMultipleItemsSelectedListener{
            override fun selectedIndices(indices: MutableList<Int>?) {
                listSelectLegersLieu.clear()
                listSelectLegersLieu.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                lieuTravauxLegerList.clear()
                lieuTravauxLegerList.addAll(strings?.toMutableList() ?: arrayListOf())
            }

        })
    }


//    fun setSchoolLevelSelection() {
//        selectSchoolLevelSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                schoolLevel = resources.getStringArray(R.array.schoolLevel)[position]
//
//                var classLevels = arrayOf<String>()
//
//                when (schoolLevel.uppercase()) {
//                    "Maternelle".uppercase() -> {
//                        classLevels = resources.getStringArray(R.array.maternelleLevel)
//                    }
//                    "PRIMAIRE" -> {
//                        classLevels = resources.getStringArray(R.array.primaireLevel)
//                    }
//                    "1er CYCLE",
//                    "1ER CYCLE",
//                    "1er Cycle" -> {
//                        classLevels = resources.getStringArray(R.array.oneCycleLevel)
//                    }
//                    "2nd CYCLE",
//                    "2ND CYCLE",
//                    "2nd Cycle" -> {
//                        classLevels = resources.getStringArray(R.array.twoCycleLevel)
//                    }
//                }
//
//                setSchoolClassSelection(classLevels)
//                linearClasseLevelContainerSsrt.visibility = VISIBLE
//
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


//    fun setSchoolClassSelection(datas: Array<String>) {
//        val schoolClassesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, datas)
//        selectClasseLevelSsrt!!.adapter = schoolClassesAdapter
//
//        selectClasseLevelSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                schoolClass = datas[position]
//                // val ssrteDraftedLocal =  ApiClient.gson.fromJson(draftedSsrteModel?.datas, EnqueteSsrtModel::class.java)
//
//                provideStringSpinnerSelection(
//                    selectClasseLevelSsrt,
//                    schoolClass,
//                    datas
//                )
//
//                linearSchoolInPlaceYesNoContainerSsrt.visibility = VISIBLE
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


//    fun setupSchoolLocationVillageYesNoSelection() {
//
//        selectSchoolInPlaceYesNoSsrt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                schoolPlaceYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                when (schoolPlaceYesNo.uppercase()) {
//                    "OUI" -> {
//                        linearVillageDistanceContainerSsrt.visibility = GONE
//                    }
//                    "NON" -> {
//                        linearVillageDistanceContainerSsrt.visibility = VISIBLE
//                    }
//                    else -> {
//                        linearVillageDistanceContainerSsrt.visibility = GONE
//                    }
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//                linearVillageDistanceContainerSsrt.visibility = GONE
//            }
//        }
//    }


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
            "Quel est le lien de parenté avec le producteur ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
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
            "uel niveau d’étude as-tu atteint ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectSchoolLevelSsrt,
            listIem = resources.getStringArray(R.array.schoolLevelFull)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })


        Commons.setListenerForSpinner(this,
            "Va-t-il à l'école ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectSchoolStatusSsrt,
            itemChanged = arrayListOf(Pair(1, "Non"), Pair(2, "Oui")),
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
            "As-tu été à l’école par le passé ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAvoirFrequPasseSsrt,
            itemChanged = arrayListOf(Pair(1, "Oui")),
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
            "Ton école est-elle située dans le village ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectSchoolInPlaceYesNoSsrt,
            itemChanged = arrayListOf(Pair(1, "Non")),
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
            "Quel niveau d’étude as-tu atteint ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
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

//        Commons.setListenerForSpinner(this,
//            "Pourquoi ne vas-tu pas à l’école ou arrêté l’école ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectRaisonArretEcoleSSrte,
//            itemChanged = arrayListOf(Pair(1, "Oui")),
//            listIem = resources.getStringArray(R.array.YesOrNo)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreRaisonArretEcole.visibility = visibility
//                }
//            })

        Commons.setupItemMultiSelection(this, selectRaisonArretEcoleSSrte, "Pourquoi ne vas-tu pas à l’école ou arrêté l’école ?", resources.getStringArray(R.array.noSchoolRaison).map { CommonData(0, it.toString()) }){
            if(it.contains("Autre")) containerAutreRaisonArretEcole.visibility = View.VISIBLE
        }

        Commons.setupItemMultiSelection(this, selectLequelTravEffectSSrte, "Au cours de ces 2 dernières années, lequel de ces travaux dangereux as-tu effectué ?", resources.getStringArray(R.array.recentWorkHard).map { CommonData(0, it.toString()) }){

        }

        Commons.setupItemMultiSelection(this, selectEndroitTravEffectSSrte, "Où as-tu effectué ces travaux ?", resources.getStringArray(R.array.recentWorkLieu).map { CommonData(0, it.toString()) }){

        }

        Commons.setupItemMultiSelection(this, selectLequelTravEffectSSrte, "Au cours de ces 2 dernières années, lequel de ces travaux légers as-tu effectué ?", resources.getStringArray(R.array.recentWorkHard).map { CommonData(0, it.toString()) }){

        }

        Commons.setupItemMultiSelection(this, selectLequelTrav2EffectSSrte, "Au cours de ces 2 dernières années lequel de ces travaux a tu effectués ?", resources.getStringArray(R.array.recentWorkLight).map { CommonData(0, it.toString()) }){

        }

        Commons.setupItemMultiSelection(this, selectEndroitTrav2EffectSSrte, "Où as-tu effectué ces travaux ?", resources.getStringArray(R.array.recentWorkLieu).map { CommonData(0, it.toString()) }){

        }

//        setupLocaliteSelection()
//        setupSchoolStatusSelection()
//
//        setupSchoolLocationVillageYesNoSelection()
//
//        setupTravauxLegersSelection()
//        setupTravauxDangereuxSelection()
//
//        setupTravauxDangereuxLieuSelection()
//        setupTravauxLegersLieuSelection()
//
//        setupStoppedSchoolRaisonSelection()
//        setupLienParenteSelection()
//
//        setupSexeSsrtSelection()
    }

    private fun setupClasseSelectedListen(
        selectClasseLevelSsrt: AppCompatSpinner?,
        classLevels: Array<String>,
        currentVal: String? = null
    ) {

        Commons.setListenerForSpinner(this,
            "Quelle est sa classe actuelle ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
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
        if (producteurId.isEmpty()) {
            showMessage("Selectionnez le producteur", this, false, {}, deconnec = false)
            return
        }

        if (editMembreNomlSsrt.text.toString().isEmpty()) {
            showMessage("Renseignez le nom du membre", this, false, {}, deconnec = false)
            return
        }

        /*
        producteurs_id
        nomMembre
        prenomMembre
        sexeMembre
        datenaissMembre
        lienParente
        frequente
        travauxDangereux
        lieuTravauxDangereux
        travauxLegers
        lieuTravauxLegers
        dateEnquete
         */

        if (editMembrePrenomlSsrt.text.toString().isEmpty()) {
            showMessage("Renseignez le prenom du membre", this, false, {}, deconnec = false)
            return
        }

        if (editDateNaissancelSsrt.text.toString().isEmpty()) {
            showMessage("Renseignez la date de naissance de ${editMembrePrenomlSsrt.text.toString()}", this, false, {}, deconnec = false)
            return
        }

        if (schoolYesNo.lowercase().contains("svp")) {
            showMessage("Selectionnez le statut scolaire ${editMembrePrenomlSsrt.text.toString()}", this, false, {}, deconnec = false)
            return
        }

        if (travauxDangereuxList.isEmpty()) {
            showMessage("Renseignez un travail deja fait par ${editMembrePrenomlSsrt.text.toString()}", this, false, {}, deconnec = false)
            return
        }

        if (lieuTravauxDangereuxList.isEmpty()) {
            showMessage("Renseignez le lieu ou le travail est fait", this, false, {}, deconnec = false)
            return
        }

        if (lieuTravauxLegerList.isEmpty()) {
            showMessage("Renseignez le lieu ou le travail est fait", this, false, {}, deconnec = false)
            return
        }

        if (travauxLegersList.isEmpty()) {
            showMessage("Renseignez un travail fait par ${editMembrePrenomlSsrt.text.toString()}", this, false, {}, deconnec = false)
            return
        }

        if (editDateEnqueteSsrt.text.toString().isEmpty()) {
            showMessage("Renseignez la date de l'enquete svp", this, false, {}, deconnec = false)
            return
        }

        val enqueteModel = getEnqueteSsrtObjet()

        try {
            val intentEnquetePreview = Intent(this, EnqueteSsrtPreviewActivity::class.java)
            intentEnquetePreview.putExtra("preview", enqueteModel)
            startActivity(intentEnquetePreview)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun getEnqueteSsrtObjet(): EnqueteSsrtModel {

        return  EnqueteSsrtModel(
            autreLienParente = editAutreLienParentSsrt.text.toString().trim(),
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
            uid = 0,
        )

    }


    fun draftSsrte(draftModel: DataDraftedModel?) {
        val ssrteDraft = getEnqueteSsrtObjet()

        showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(ssrteDraft),
                        typeDraft = "ssrte",
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
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        val ssrteDrafted =  ApiClient.gson.fromJson(draftedData.datas, EnqueteSsrtModel::class.java)

        // Localite
        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val localitesDatas: MutableList<CommonData> = mutableListOf()
        localitesLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            localitesDatas.addAll(it)
        }
//        selectLocaliteSsrt.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//        provideDatasSpinnerSelection(
//            selectLocaliteSsrt,
//            ssrteDrafted.localiteNom,
//            localitesDatas
//        )

        // Parents
        val parentsList = CcbRoomDatabase.getDatabase(this)?.lienParenteDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val parentsDatas: MutableList<CommonData> = mutableListOf()
        parentsList?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            parentsDatas.addAll(it)
        }
        selectLienParentSsrt.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parentsList!!)
        provideDatasSpinnerSelection(
            selectLienParentSsrt,
            ssrteDrafted.lienParente,
            parentsDatas
        )

        // School YesNo
        provideStringSpinnerSelection(
            selectSchoolStatusSsrt,
            ssrteDrafted.frequente,
            resources.getStringArray(R.array.YesOrNo)
        )

        //Genre Masculin ou Feminin
        provideStringSpinnerSelection(
            selectSexeSsrt,
            ssrteDrafted.sexeMembre,
            resources.getStringArray(R.array.genre_membre)
        )

        // selectSchoolLevelSsrt
        provideStringSpinnerSelection(
            selectSchoolLevelSsrt,
            ssrteDrafted.niveauEtude,
            resources.getStringArray(R.array.schoolLevel)
        )


        editMembreNomlSsrt.setText(ssrteDrafted.nomMembre)
        editMembrePrenomlSsrt.setText(ssrteDrafted.prenomMembre)
        editDateNaissancelSsrt.setText(ssrteDrafted.datenaissMembre)
        editAutreLienParentSsrt.setText(ssrteDrafted.autreLienParente)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //scrollableMarquee()

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

        /*val tempList = resources.getStringArray(R.array.primaireLevel)

        val listArray0: MutableList<KeyPairBoolData> = ArrayList()
        for (i in 0 until resources.getStringArray(R.array.primaireLevel).size) {
            val h = KeyPairBoolData()
            h.id = (i + 1).toLong()
            h.name = tempList[i]
            h.isSelected = false
            listArray0.add(h)
        }

        multipleItemSelectionSpinner.setItems(listArray0) { items ->
            for (i in items.indices) {
                if (items[i].isSelected) {
                    Log.i(
                        Commons.TAG,
                        i.toString() + " : " + items[i].name + " : " + items[i].isSelected
                    )
                }
            }
        }*/

        setOtherListener()

        if (intent.getStringExtra("from") != null) {
            fromAction = intent.getStringExtra("from") ?: ""
            draftedSsrteModel = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedSsrteModel!!)
        }else{
            setAllListener()
        }

        //applyFilters(editVillageDistanceSsrt)
    }

    private fun setOtherListener() {

        editDateNaissancelSsrt.setOnClickListener { configDate(editDateNaissancelSsrt) }
        editDateEnqueteSsrt.setOnClickListener { configDate(editDateEnqueteSsrt) }
        editDureeFormation.setOnClickListener { configHour(editDureeFormation) }

    }
}
