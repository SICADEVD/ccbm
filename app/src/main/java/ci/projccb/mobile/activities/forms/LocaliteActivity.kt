package ci.projccb.mobile.activities.forms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.LocalitePreviewActivity
import ci.projccb.mobile.adapters.EcoleLocaliteAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.LocaliteDao
import ci.projccb.mobile.repositories.databases.daos.SourceEauDao
import ci.projccb.mobile.repositories.databases.daos.TypeLocaliteDao
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.applyFilters
import ci.projccb.mobile.tools.Commons.Companion.playDraftSound
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.releaseDraftSound
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_localite.*
import kotlinx.android.synthetic.main.activity_producteur.*


class LocaliteActivity : AppCompatActivity() {


    var centreYesNo = ""
    var ecoleYesNo = ""
    var nbreEcole = ""
    var nomLocalite = ""
    var sousPrefecture = ""
    var nbrePopulation = ""
    var centreKm = ""
    var ecoleKm = ""
    var nomCentre = ""
    var centreStatut = ""
    var nomEcole = ""
    var typeLocalite = ""
    var sourecEau = ""
    var pompeEtatYesNo = ""
    var marketYesNo = ""
    var dayMarket = ""
    var cieYesNo = ""
    var lieuDechetYesNo = ""
    var nbreComite = ""
    var nbreAssoFemmes = ""
    var nbreAssoJeunes = ""

    var typeLocalitesList: MutableList<TypeLocaliteModel>? =null;
    var sourceEauxList: MutableList<SourceEauModel>? =null;

    var localiteDao: LocaliteDao? = null
    var typeLocaliteDao: TypeLocaliteDao? = null
    var sourceEaDao: SourceEauDao? = null

    val ecolesList = mutableListOf<String>()
    var ecoleAdapter: EcoleLocaliteAdapter? = null
    var draftModel: DataDraftedModel? = null


    fun setupSourceEauxSelection()  {
        val arraySourceEau: MutableList<String> = mutableListOf()
        //sourceEaDao = CcbRoomDatabase.getDatabase(applicationContext)?.sourceEauDoa();
        sourceEauxList = AssetFileHelper.getListDataFromAsset(16, this@LocaliteActivity) as MutableList<SourceEauModel>?
                //sourceEaDao?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

        arraySourceEau.add(getString(R.string.choisir_le_type))

        if (sourceEauxList!!.isEmpty()) {
            showMessage(
                getString(R.string.la_liste_des_sources_d_eau_est_vide_refaite_une_mise_jour),
                this,
                finished = false,
                callback = {},
                getString(R.string.compris),
                false,
                showNo = false,
            )
        } else {
            sourceEauxList?.map {
                arraySourceEau.add(it.nom!!)
            }

            val sourceEauAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arraySourceEau)
            selectSourceEauLocalite!!.adapter = sourceEauAdapter

            selectSourceEauLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                    sourecEau = arraySourceEau[position]

                    if (sourecEau.uppercase().contains("POMPE")) {
                        linearEtatPompeContainerLocalite.visibility = View.VISIBLE
                    } else {
                        linearEtatPompeContainerLocalite.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }
        }
    }


    fun setupLocalitesTypeSelection() {
        val arrayTypeLocalite: MutableList<String> = mutableListOf()
        //typeLocaliteDao = CcbRoomDatabase.getDatabase(applicationContext)?.typeLocaliteDao()
        typeLocalitesList = AssetFileHelper.getListDataFromAsset(15, this@LocaliteActivity) as MutableList<TypeLocaliteModel>?
                //typeLocaliteDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

        arrayTypeLocalite.add(getString(R.string.choisir_le_type))

        if (typeLocalitesList!!.isEmpty()) {
            showMessage(
                getString(R.string.la_liste_du_type_de_localit_est_vide_refaite_une_mise_jour),
                this,
                finished = false,
                callback = {},
                getString(R.string.compris),
                false,
                showNo = false,
            )
        } else {
            typeLocalitesList?.map {
                arrayTypeLocalite.add(it.nom!!)
            }

            val typeLocaliteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayTypeLocalite)
            selectTypeLocalite!!.adapter = typeLocaliteAdapter

            selectTypeLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                    typeLocalite = arrayTypeLocalite[position]
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }
        }
    }


    fun setupCieYesNoSelection() {
        selectCieYesNoLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                cieYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupDayMarketSelection() {
        selectMarketDayLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                dayMarket = resources.getStringArray(R.array.dayMarket)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupPompeEtatYesNoSelection() {
        selectEtatPompeYesNoLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                pompeEtatYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupCentreStatutSelection() {
        selectCentreSanteNoTypeLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                centreStatut = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupMarketYesNoSelection() {
        selectMarketYesNoLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                marketYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                when (marketYesNo) {
                    getString(R.string.oui) -> {
                        linearMarketDayContainerLocalite.visibility = View.VISIBLE
                        linearMarketDistanceContainerLocalite.visibility = View.GONE
                    }
                    getString(R.string.non) -> {
                        linearMarketDistanceContainerLocalite.visibility = View.VISIBLE
                        linearMarketDayContainerLocalite.visibility = View.GONE
                    }
                    else -> {
                        linearMarketDistanceContainerLocalite.visibility = View.GONE
                        linearMarketDayContainerLocalite.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupDechetsYesNoSelection() {
        selectDechetYesyNoLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                lieuDechetYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupCentreYesNoSelection() {
        selectCentreYesNoLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                centreYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                when (centreYesNo) {
                    getString(R.string.oui) -> {
                        linearCentreKmContainerLocalite.visibility = View.GONE
                        linearCentreSanteNoTypeContainerLocalite.visibility = View.VISIBLE
                        linearCentreSanteYesNomContainerLocalite.visibility = View.VISIBLE
                    }
                    getString(R.string.non) -> {
                        linearCentreKmContainerLocalite.visibility = View.VISIBLE
                        linearCentreSanteYesNomContainerLocalite.visibility = View.VISIBLE
                        linearCentreSanteNoTypeContainerLocalite.visibility = View.VISIBLE
                    }
                    else -> {
                        linearCentreKmContainerLocalite.visibility = View.GONE
                        linearCentreSanteYesNomContainerLocalite.visibility = View.GONE
                        linearCentreSanteNoTypeContainerLocalite.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupEcoleYesNoSelection() {
        selectEcoleYesNoLocalite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                ecoleYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                when (ecoleYesNo) {
                    getString(R.string.oui) -> {
                        linearNbreEcoleContainerLocalite.visibility = View.VISIBLE
                        linearEcoleDistanceContainerLocalite.visibility = View.GONE

                        nbreEcole = ""
                    }
                    getString(R.string.non) -> {
                        linearNbreEcoleContainerLocalite.visibility = View.GONE
                        linearEcoleDistanceContainerLocalite.visibility = View.VISIBLE

                        ecoleKm = ""
                    }
                    else -> {
                        linearNbreEcoleContainerLocalite.visibility = View.GONE
                        linearEcoleDistanceContainerLocalite.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun clearFields() {
        nomLocalite  = ""
        sousPrefecture   = ""
        nbrePopulation   = ""
        centreKm     = ""
        nomCentre    = ""
        ecoleKm  = ""
        nbreEcole    = ""
        nomEcole     = ""
        nbreComite   = ""
        nbreAssoFemmes = ""
        nbreAssoJeunes = ""
        dayMarket = ""
        marketYesNo = ""

        editNomLocalite.text    = null
        editSousPrefectureLocalite.text = null
        editPopulationLocalite.text = null
        editCentreKmLocalite.text   = null
        editNomCentreSanteLocalite.text = null
        editEcoleDistanceLocalite.text  = null
        editNbreEcolesLocalite.text = null
        editNomEcoleLocalite.text   = null
        editNbreComiteLocalite.text = null
        editNbreAssoFemmeLocalite.text  = null
        editNbreAssoJeuneLocalite.text  = null

        selectSourceEauLocalite.setSelection(0)
        selectMarketYesNoLocalite.setSelection(0)
        selectMarketDayLocalite.setSelection(0)
        selectTypeLocalite.setSelection(0)
        selectCieYesNoLocalite.setSelection(0)
        selectEtatPompeYesNoLocalite.setSelection(0)
        selectDechetYesyNoLocalite.setSelection(0)
        selectCentreYesNoLocalite.setSelection(0)
        selectEcoleYesNoLocalite.setSelection(0)

        editNomLocalite.requestFocus()
    }


    fun collectDatas() {
        nomLocalite = editNomLocalite.text?.trim().toString()
        sousPrefecture = editSousPrefectureLocalite.text?.trim().toString()
        nbrePopulation = editPopulationLocalite.text?.trim().toString()
        centreKm = editCentreKmLocalite.text?.trim().toString()
        nomCentre = editNomCentreSanteLocalite.text?.trim().toString()
        ecoleKm = editEcoleDistanceLocalite.text?.trim().toString()
        nbreEcole = editNbreEcolesLocalite.text?.trim().toString()
        nomEcole = editNomEcoleLocalite.text?.trim().toString()
        nbreComite = editNbreComiteLocalite.text?.trim().toString()
        nbreAssoFemmes = editNbreAssoFemmeLocalite.text?.trim().toString()
        nbreAssoJeunes = editNbreAssoJeuneLocalite.text?.trim().toString()

        if (nomLocalite.isEmpty()) {
            showMessage(
                getString(R.string.renseignez_la_localite_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (typeLocalite.contains(getString(R.string.choisir), ignoreCase = true)) {
            showMessage(
                getString(R.string.renseignez_le_type_de_la_localit_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (sousPrefecture.isEmpty()) {
            showMessage(
                getString(R.string.renseignez_la_sous_prefecture_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (centreYesNo.contains(getString(R.string.choisir), ignoreCase = true)) {
            showMessage(
                getString(R.string.repondez_la_question_sur_le_centre_de_sant_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (centreStatut.contains(getString(R.string.choisir), ignoreCase = true)) {
            showMessage(
                getString(R.string.repondez_la_question_sur_du_statut_centre_de_sant_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (ecoleYesNo.contains(getString(R.string.choisir), ignoreCase = true)) {
            showMessage(
                getString(R.string.repondez_la_question_sur_l_cole_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (sourecEau.contains(getString(R.string.choisir), ignoreCase = true)) {
            showMessage(
                getString(R.string.repondez_la_question_source_d_eau_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (cieYesNo.contains(getString(R.string.choisir), ignoreCase = true)) {
            showMessage(
                getString(R.string.repondez_la_question_de_source_d_lectricit_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        if (lieuDechetYesNo.contains(getString(R.string.choisir), ignoreCase = true)) {
            showMessage(
                getString(R.string.repondez_la_question_de_dechet_svp),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.ok),
                deconnec = false,
                showNo = false
            )
            return
        }

        val localModel = getLocaliteObjet()

        val ecolesToken = object : TypeToken<MutableList<String>>() {}.type
        localModel.ecolesNomsList = ApiClient.gson.fromJson(localModel.nomsEcolesStringify, ecolesToken)

        LogUtils.e(Commons.TAG, ApiClient.gson.toJson(localModel))

        val intentLocalitePreview = Intent(this, LocalitePreviewActivity::class.java)
        intentLocalitePreview.putExtra("preview", localModel)
        intentLocalitePreview.putExtra("draft_id", draftModel?.uid)
        startActivity(intentLocalitePreview)
    }

    private fun getLocaliteObjet(): LocaliteModel {

        return LocaliteModel(
            uid = 0,
            id = 0,
            nom = nomLocalite,
            cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID, 1).toString(),
            source = sourecEau,
            type = typeLocalite,
            sousPref = sousPrefecture,
            pop = nbrePopulation,
            marcheYesNo = marketYesNo,
            dayMarche = dayMarket,
            centreYesNo = centreYesNo,
            centreDistance = centreKm,
            centreNom = nomCentre,
            ecoleNom = nomEcole,
            ecoleNbre = nbreEcole,
            ecoleDistance = ecoleKm,
            ecoleYesNo = ecoleYesNo,
            nomsEcolesStringify = ApiClient.gson.toJson(ecolesList),
            dechetYesNo = lieuDechetYesNo,
            femmeAsso = nbreAssoFemmes,
            jeuneAsso = nbreAssoJeunes,
            comite = nbreComite,
            pompeYesNo = pompeEtatYesNo,
            cieYesNo = cieYesNo,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local",
            latitude = editLatLocalite.text.toString(),
            longitude = editLongLocalite.text.toString(),
            distanceMarche = editMarketDistanceLocalite.text.toString().trim(),
        )

    }


    @SuppressLint("NotifyDataSetChanged")
    fun addEcole(ecole: String) {
        if (ecole.isEmpty()) return

        if (ecolesList.size < editNbreEcolesLocalite.text.toString().toInt()) {
            ecolesList.forEach {
                if (it.trim().uppercase() == ecole.trim().uppercase()) {
                    ToastUtils.showShort(getString(R.string.cette_ecole_est_deja_ajout_e))
                    return
                }
            }

            ecolesList.add(ecole)
            ecoleAdapter?.notifyDataSetChanged()

            editNomCustomEcoleLocalite.text = null
        } else {

            showMessage(
                message = getString(R.string.nombre_d_ecoles_saisi_atteint),
                context = this,
                finished = false,
                callback = {},
                deconnec = false,
                showNo = false
            )
        }
    }


    fun draftLocalite(draftModel: DataDraftedModel?) {
        val localDraft = getLocaliteObjet()

        showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(localDraft),
                        typeDraft = "localite",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                showMessage(
                    message = getString(R.string.contenu_ajout_aux_brouillons),
                    context = this,
                    finished = true,
                    callback = {
                        playDraftSound(this)
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


    fun undraftedDatas(drafftedData: DataDraftedModel?) {
        // unpacked datas
        //  val localiteToken = object : TypeToken<>() {}.type
        val localiteDrafted = ApiClient.gson.fromJson(drafftedData?.datas, LocaliteModel::class.java)

        editNomLocalite.setText(localiteDrafted.nom.toString())
        editSousPrefectureLocalite.setText(localiteDrafted.sousPref.toString())
        editPopulationLocalite.setText(localiteDrafted.pop.toString())

        editNbreComiteLocalite.setText(localiteDrafted.comite.toString().trim())
        editNbreEcolesLocalite.setText(localiteDrafted.ecoleNbre.toString().trim())
        editNbreAssoFemmeLocalite.setText(localiteDrafted.femmeAsso.toString().trim())

        editNbreAssoJeuneLocalite.setText(localiteDrafted.jeuneAsso.toString().trim())
        editLatLocalite.setText(localiteDrafted.latitude.toString())
        editLongLocalite.setText(localiteDrafted.longitude.toString())

        // Spinner
        provideStringSpinnerSelection(spinner = selectCentreYesNoLocalite, value = localiteDrafted.centreYesNo, resources.getStringArray(R.array.YesOrNo))

        // Localite type
        val typeLists = CcbRoomDatabase.getDatabase(this)?.typeLocaliteDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val typeLocalitesDatas: MutableList<CommonData> = mutableListOf()
        typeLocalitesDatas.add(CommonData(id = 0, nom = getString(R.string.choisir_le_type)))
        typeLists?.map {CommonData(id = 0, nom = it.nom)}?.let { typeLocalitesDatas.addAll(it) }
        provideDatasSpinnerSelection(selectTypeLocalite, localiteDrafted.type, typeLocalitesDatas)


        // Centre type
        provideStringSpinnerSelection(
            spinner = selectCentreSanteNoTypeLocalite,
            value = localiteDrafted.typeCentre,
            list = resources.getStringArray(R.array.typeCentre)
        )

        // Ecole primaire Yes no
        provideStringSpinnerSelection(
            spinner = selectEcoleYesNoLocalite,
            value = localiteDrafted.ecoleYesNo,
            list = resources.getStringArray(R.array.YesOrNo)
        )

        //  Source d'eau
        val eauLists = CcbRoomDatabase.getDatabase(this)?.sourceEauDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val eauxDatas: MutableList<CommonData> = mutableListOf()
        eauxDatas.add(CommonData(id = 0, nom = getString(R.string.choisir_la_source)))
        eauLists?.map {CommonData(id = 0, nom = it.nom)}?.let { eauxDatas.addAll(it) }
        provideDatasSpinnerSelection(selectSourceEauLocalite, localiteDrafted.source, eauxDatas)

        // Eclairage
        provideStringSpinnerSelection(
            spinner = selectCieYesNoLocalite,
            value = localiteDrafted.cieYesNo,
            list = resources.getStringArray(R.array.YesOrNo)
        )

        // Marche
        provideStringSpinnerSelection(
            spinner = selectMarketYesNoLocalite,
            value = localiteDrafted.marcheYesNo,
            list = resources.getStringArray(R.array.YesOrNo)
        )

        // Market Day
        provideStringSpinnerSelection(
            spinner = selectMarketDayLocalite,
            value = localiteDrafted.dayMarche,
            list = resources.getStringArray(R.array.dayMarket)
        )

        // Endroit dechets
        provideStringSpinnerSelection(
            spinner = selectDechetYesyNoLocalite,
            value = localiteDrafted.dechetYesNo,
            list = resources.getStringArray(R.array.YesOrNo)
        )

    }


    override fun onDestroy() {
        super.onDestroy()
        releaseDraftSound()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localite)

        localiteDao = CcbRoomDatabase.getDatabase(this)?.localiteDoa()
        setupLocalitesTypeSelection()

        setupCentreYesNoSelection()

        setupEcoleYesNoSelection()

        setupSourceEauxSelection()

        setupCieYesNoSelection()

        setupPompeEtatYesNoSelection()

        setupCieYesNoSelection()

        setupDechetsYesNoSelection()

        setupMarketYesNoSelection()

        setupDayMarketSelection()

        setupCentreStatutSelection()

        ecoleAdapter = EcoleLocaliteAdapter(ecolesList)
        recyclerEcolesListLocalite.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerEcolesListLocalite.adapter = ecoleAdapter

        imageLocationLocalite.setOnClickListener {
            editLatLocalite.setText(SPUtils.getInstance().getString(Constants.PREFS_COMMON_LAT, "0.0"))
            editLongLocalite.setText(SPUtils.getInstance().getString(Constants.PREFS_COMMON_LNG, "0.0"))
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageDraftBtn.setOnClickListener {
            draftLocalite(draftModel ?: DataDraftedModel(uid = 0))
        }

        clickSaveLocalite.setOnClickListener {
            collectDatas()
        }

        clickAddEcoleLocalite.setOnClickListener {
            if (editNomCustomEcoleLocalite.text.toString().isEmpty()) {
                return@setOnClickListener
            }

            addEcole(editNomCustomEcoleLocalite.text.toString())
        }

        applyFilters(editPopulationLocalite, withZero = true)
        applyFilters(editCentreKmLocalite, withZero = true)
        applyFilters(editEcoleDistanceLocalite, withZero = true)
        applyFilters(editMarketDistanceLocalite, withZero = true)
        applyFilters(editNbreAssoJeuneLocalite, withZero = true)
        applyFilters(editNbreEcolesLocalite, withZero = true)
        applyFilters(editNbreComiteLocalite, withZero = true)

        if (intent.getStringExtra("from") != null) {
            draftModel = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftModel)
        }
    }
}
