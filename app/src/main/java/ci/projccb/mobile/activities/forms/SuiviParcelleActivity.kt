package ci.projccb.mobile.activities.forms

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.projccb.mobile.adapters.AnimalAdapter
import ci.projccb.mobile.adapters.InsecteAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.applyFilters
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_suivi_parcelle.*
import java.util.*



@SuppressLint("All")
class SuiviParcelleActivity : AppCompatActivity() {


    companion object {
        const val TAG = "SuiviParcelleActivity::class"
    }


    var localitesList: MutableList<LocaliteModel>? = mutableListOf()
    var parcelleDao: ParcelleDao? = null
    var producteurDao: ProducteurDao? = null
    var courEauxDao: CourEauDao? = null
    var varieteDao: VarieteCacaoDao? = null
    var intrantDao: IntrantDao? = null
    var suiviParcelleDao: SuiviParcelleDao? = null
    var campagneDao: CampagneDao? = null

    var parcellesList: MutableList<ParcelleModel>? = null
    var courEauxList: MutableList<CourEauModel>? = null
    var cacaossList: MutableList<VarieteCacaoModel>? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var intrantsList: MutableList<IntrantModel>? = null
    var campagnesList: MutableList<CampagneModel>? = mutableListOf()


    var ombrageAdapter: OmbrageAdapter? = null
    var arbreAdapter: OmbrageAdapter? = null
    var animalAdapter: AnimalAdapter? = null
    var insecteAdapter: InsecteAdapter? = null

    var ombragesList: MutableList<OmbrageVarieteModel>? = null
    var arbresList: MutableList<OmbrageVarieteModel>? = null
    var animauxList: MutableList<String>? = null

    var insectesList = mutableListOf<InsecteRavageurModel>()

    var datePickerDialog: DatePickerDialog? = null

    var localiteNom = ""
    var localiteId = ""

    var campagneNom = ""
    var campagneId = ""

    var parcelleNom = ""
    var parcelleId = ""
    var parcelleSuperficie = ""
    var producteurId = ""
    var producteurNom = ""
    var cacaoVariete = ""
    var cacaoVarieteAutre = ""
    var courEauYesNo = ""
    var courEau = ""
    var penteYesNo = ""
    var agroForestierYesNo = ""
    var swollestShoot = ""
    var pourritureBrune = ""
    var arbreVariete = ""
    var nombreSauvageons = ""
    var activiteTaille = ""
    var activiteEgourmandage = ""
    var activiteDesherbage = ""
    var activiteSanitaire = ""
    var preseceBio = ""
    var presenceInsecte = ""
    var presenceFourmi = ""
    var presenceAraignee = ""
    var presenceVer = ""
    var presenceMante = ""
    var nomInsecticide = ""
    var nombreInsecticide = ""
    var nomFongicide = ""
    var nombreFongicide = ""
    var nombreHerbicide = ""
    var nomHerbicide = ""
    var dateSuivi = ""
    var nombreDesherbageAnnuel = ""

    var draftedDataSuiviParcelle: DataDraftedModel? = null


    fun setupParcelleSelection(producteurId: String?) {
        try {
            parcelleDao = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
            parcellesList = parcelleDao?.getParcellesProducteur(
                producteurId = producteurId,
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            )

            val parcellesAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parcellesList!!)
            selectParcelleSuivi!!.adapter = parcellesAdapter

            selectParcelleSuivi.setTitle("Choisir la parcelle")

            selectParcelleSuivi.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        val parcelle = parcellesList!![position]

                        parcelleNom = "${parcelle.culture?:Constants.VIDE} (${parcelle.anneeCreation?:Constants.VIDE})"
                        parcelleSuperficie = parcelle.superficie?:Constants.VIDE

                        if (parcelle.isSynced) {
                            parcelleId = parcelle.id.toString()
                        } else {
                            parcelleId = parcelle.id.toString()
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


    fun addOmbrageVariete(ombrageVarieteModel: OmbrageVarieteModel) {
        try {
            if (ombrageVarieteModel.variete?.length == 0) return

            ombragesList?.forEach {
                if (it.variete?.uppercase() == ombrageVarieteModel.variete?.uppercase() && it.nombre == ombrageVarieteModel.nombre) {
                    ToastUtils.showShort("Cette variété est deja ajoutée")
                    return
                }
            }

            ombragesList?.add(ombrageVarieteModel)
            ombrageAdapter?.notifyDataSetChanged()

            clearCultureProducteurFields()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun addAnimalSauvage(animalSauvageModel: String) {
        try {
            if (animalSauvageModel.isEmpty()) return

            animauxList?.forEach {
                if (it.uppercase() == animalSauvageModel.uppercase()) {
                    ToastUtils.showShort("Cet animal est deja ajouté")
                    return
                }
            }

            animauxList?.add(animalSauvageModel)
            animalAdapter?.notifyDataSetChanged()

            clearAnimauxFields()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun addArbreAgro(arbreModel: OmbrageVarieteModel) {
        try {
            if (arbreModel.variete?.length == 0) return

            arbresList?.forEach {
                if (it.variete?.uppercase() == arbreModel.variete?.uppercase() && it.nombre == arbreModel.nombre) {
                    ToastUtils.showShort("Cet arbre est deja ajouté")
                    return
                }
            }

            arbresList?.add(arbreModel)
            arbreAdapter?.notifyDataSetChanged()

            clearArbresFields()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun addInsectesParasites(insecteRavageur: InsecteRavageurModel) {
        try {
            if (insecteRavageur.nom?.length == 0) return

            insectesList.forEach { insecte ->
                if (insecte.nom?.uppercase() == insecteRavageur.nom?.uppercase() && insecte.quantite == insecteRavageur.quantite) {
                    ToastUtils.showShort("Ce insecte est deja ajouté")
                    return
                }
            }

            insectesList.add(insecteRavageur)
            insecteAdapter?.notifyDataSetChanged()

            clearInsecteFields()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun clearCultureProducteurFields() {
//        editVarieteOmbrageSuivi.text = null
//        editVarieteOmbrageNombreSuivi.text = null
    }


    fun clearArbresFields() {
//        editArbreNombreSuivi.text = null
//        editAgroArbreSuivi.text = null
    }


    fun clearAnimauxFields() {
        editAnimalSuiviParcelle.text = null
    }


    fun clearInsecteFields() {
        editInsecteNomOfSuiviParcelle.text = null
        selectInsecteQuantiteOfSuiviParcelle.setSelection(0)
    }


    fun setupOmbragesSuiviParcelle() {
        try {
            ombragesList = mutableListOf()
            ombrageAdapter = OmbrageAdapter(ombragesList)
//            recyclerVarieteOmbrageSuiviParcelle.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerVarieteOmbrageSuiviParcelle.adapter = ombrageAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setArbreParcelle() {
        try {
            arbresList = mutableListOf()
            arbreAdapter = OmbrageAdapter(arbresList)
//            recyclerArbreAgroSuiviParcelle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerArbreAgroSuiviParcelle.adapter = arbreAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setAnimalParcelle() {
        try {
            animauxList = mutableListOf()
            animalAdapter = AnimalAdapter(animauxList!!)
            recyclerAnimauxSuiviParcelle.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerAnimauxSuiviParcelle.adapter = animalAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setInsectes() {
        try {
            insecteAdapter = InsecteAdapter(insectesList)
            recyclerInsecteOfSuiviParcelle.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerInsecteOfSuiviParcelle.adapter = insecteAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupProducteursSelection(localite: String?) {
        try {
            producteurDao = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            producteursList = producteurDao?.getProducteursByLocalite(localite = localite) ?: mutableListOf()

            if (producteursList?.size == 0) {
                Commons.showMessage(
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

            producteursList?.map {
                CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
            }?.let {
                producteursDatas.addAll(it)
            }

            val suiviParcelleDraftedLocal = ApiClient.gson.fromJson(
                draftedDataSuiviParcelle?.datas,
                SuiviParcelleModel::class.java
            )
            selectProducteurSuivi!!.adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

            if (suiviParcelleDraftedLocal != null) {
                provideDatasSpinnerSelection(
                    selectProducteurSuivi,
                    suiviParcelleDraftedLocal.parcelleProducteur,
                    producteursDatas
                )
            }

            selectProducteurSuivi.setTitle("Choisir le producteur")
            selectProducteurSuivi.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        val producteur = producteursList!![position]
                        producteurNom = "${producteur.nom} ${producteur.prenoms}"

                        producteurId = if (producteur.isSynced) {
                            producteur.id.toString()
                        } else {
                            producteur.uid.toString()
                        }

                        //LogUtils.e(TAG, "ProducteurID => $producteurId")
                        setupParcelleSelection(producteurId)
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }

            if (producteursList?.size == 0) {
                //LogUtils.e("TAG -> ${producteursList?.size}")
                Commons.showMessage("Aucun producteur enregistré", this, callback = {})
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupCampagneSelection() {
        try {
            campagneDao = CcbRoomDatabase.getDatabase(applicationContext)?.campagneDao()
            campagnesList = campagneDao?.getAll()

            val campagneAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList!!)
//            selectCampagneOfSuiviParcelle!!.adapter = campagneAdapter
//
//            selectCampagneOfSuiviParcelle.setTitle("Choisir la campagne")
//
//            selectCampagneOfSuiviParcelle.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        val campagne = campagnesList!![position]
//                        campagneNom = campagne.campagnesNom!!
//                        campagneId = campagne.id.toString()
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


    fun setupCourEauYesNoSelection() {
        try {
//            selectCoursEauYesNoSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                        courEauYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                        if (courEauYesNo == "oui") {
//                            linearCoursEauContainerSuivi.visibility = View.VISIBLE
//                        } else {
//                            linearCoursEauContainerSuivi.visibility = View.GONE
//                        }
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


    fun setupPenteYesNoSelection() {
        try {
//            selectPenteYesNoSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        penteYesNo = resources.getStringArray(R.array.YesOrNo)[position]
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


    fun setupPourritureBruneSelection() {
        try {
            selectPourritureBruneOfSuiviParcelle.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        pourritureBrune = resources.getStringArray(R.array.lowMediumHigh)[position]
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupBenefAgroForestierYesNoSelection() {
        try {
//            selectBeneficiareAgroOfSuiviParcelle.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        agroForestierYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                        when (agroForestierYesNo) {
//                            "oui" -> {
//                                linearArbreAgroContainerSuivi.visibility = View.VISIBLE
//                            }
//                            "non" -> {
//                                arbresList?.clear()
//                                linearArbreAgroContainerSuivi.visibility = View.GONE
//                            }
//                            else -> {
//                                arbresList?.clear()
//                                linearArbreAgroContainerSuivi.visibility = View.GONE
//                            }
//                        }
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


    fun setupTailleSelection() {
        try {
//            selectTailleSuivi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    adapterView: AdapterView<*>,
//                    view: View,
//                    position: Int,
//                    l: Long
//                ) {
//                    activiteTaille = resources.getStringArray(R.array.lowMediumHigh)[position]
//                }
//
//                override fun onNothingSelected(arg0: AdapterView<*>) {
//                }
//            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupEgourmandageSelection() {
        try {
//            selectEgourmandageSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        activiteEgourmandage =
//                            resources.getStringArray(R.array.lowMediumHigh)[position]
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


    fun setupDesherbageSelection() {
        try {
//            selectDesherbageManuelSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        activiteDesherbage =
//                            resources.getStringArray(R.array.lowMediumHigh)[position]
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


    fun setupSanitaireSelection() {
        try {
//            selectRecolteSanitaireSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        activiteSanitaire =
//                            resources.getStringArray(R.array.lowMediumHigh)[position]
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


    fun setupCourEauxSelection() {
        try {
            val arrayCourEau: MutableList<String> = mutableListOf()
            //courEauxDao = CcbRoomDatabase.getDatabase(applicationContext)?.courEauDoa()
            courEauxList = AssetFileHelper.getListDataFromAsset(0, this@SuiviParcelleActivity) as MutableList<CourEauModel>?
//                courEauxDao?.getAll(
//                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
//            )

            arrayCourEau.add("Choisir la source...")

            courEauxList?.map {
                arrayCourEau.add(it.nom!!)
            }

            val courEauAdapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayCourEau)
//            selectCoursEauSuivi!!.adapter = courEauAdapter
//
//            selectCoursEauSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        courEau = arrayCourEau[position]
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


    fun setupBioSelection() {
        try {
//            selectAgresseurSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        preseceBio = resources.getStringArray(R.array.fullyPoor)[position]
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


    fun setupInsectesSelection() {
        try {
//            selectInsecteSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        presenceInsecte = resources.getStringArray(R.array.YesOrNo)[position]
//
//                        when (presenceInsecte) {
//                            "oui" -> {
//                                linearPresenceInsecteRavageurOfSuiviParcelle.visibility =
//                                    View.VISIBLE
//                            }
//                            else -> {
//                                linearPresenceInsecteRavageurOfSuiviParcelle.visibility = View.GONE
//                            }
//                        }
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


    fun setupLocaliteSelection() {
        try {
            localitesList = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

            if (localitesList?.size == 0) {
                Commons.showMessage(
                    "La liste des Localités semble vide, veuillez procéder à la synchronisation des données svp.",
                    this,
                    finished = true,
                    callback = {},
                    positive = "Compris !",
                    deconnec = false,
                    showNo = false

                )
                return
            } else {
                val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
//                selectLocaliteSuiviParcelle!!.adapter = localiteAdapter
//
//                selectLocaliteSuiviParcelle.setTitle("Choisir la localite")
//                selectLocaliteSuiviParcelle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected( adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                        val locality = localitesList!![position]
//                        localiteNom = locality.nom ?: ""
//                        localiteId = if (locality.isSynced) locality.id!!.toString() else locality.uid.toString()
//
//                        setupProducteursSelection(localiteId)
//                    }
//
//                    override fun onNothingSelected(arg0: AdapterView<*>) {
//                    }
//                }
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    fun setupFourmiSelection() {
        try {
            selectFourmisSuivi.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        presenceFourmi = resources.getStringArray(R.array.fullyPoor)[position]
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupAraigneeSelection() {
        try {
            selectAraigneeSuivi.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        l: Long
                    ) {
                        presenceAraignee = resources.getStringArray(R.array.fullyPoor)[position]
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupVerSelection() {
        selectVerDeTerreSuivi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                presenceVer = resources.getStringArray(R.array.fullyPoor)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupManteSelection() {
        selectManteReligieuseSuivi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                presenceMante = resources.getStringArray(R.array.fullyPoor)[position]
                LogUtils.d("Mante Reali : "+presenceMante)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }

//    fun setupSwollerShootSelection() {
//        selectSwollenSuivi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                swollestShoot = resources.getStringArray(R.array.fullyPoor)[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


    fun collectDatas() {
        nomInsecticide = editInsecticeNomSuivi.text?.trim().toString()
        nombreInsecticide = editInsecticeNombreSuivi.text?.trim().toString()

        nombreFongicide = editFongicideNombreSuivi.text?.trim().toString()
        nomFongicide = editFongicideNomSuivi.text?.trim().toString()

        nomHerbicide = editHerbicideNomSuivi.text?.trim().toString()
        nombreHerbicide = editHerbicideNombreSuivi.text?.trim().toString()

        nombreDesherbageAnnuel = editDesherbageManuelSuivi.text?.trim().toString()
        //arbreVariete = editVarieteArbreSuivi.text?.trim().toString()

        //nombreSauvageons = editNbreSauvageonsSuivi.text?.trim().toString()


        if (producteurId.isEmpty()) {
            Commons.showMessage(
                "Choisissez un producteur svp.",
                this,
                finished = false,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false

            )
            return
        }

        if (parcelleId.isBlank()) {
            Commons.showMessage(message = "Selectionnez la parcelle, svp !", context = this, finished = false, callback = {})
            return
        }

        val suiviParcelle = getSuiviParcelleObjet()

        try {
            val intentSuiviParcellePreview = Intent(this, SuiviParcellePreviewActivity::class.java)
            intentSuiviParcellePreview.putExtra("preview", suiviParcelle)
            intentSuiviParcellePreview.putExtra("draft_id", draftedDataSuiviParcelle?.uid)
            startActivity(intentSuiviParcellePreview)
        } catch (ex: Exception) {

        }
    }

    private fun getSuiviParcelleObjet(): SuiviParcelleModel {
        val item = SuiviParcelleModel(
            activiteDesherbageManuel = activiteDesherbage,
            parcelleNom = parcelleNom,
            parcelleSuperficie = parcelleSuperficie,
            parcelleProducteur = producteurNom,
            activiteEgourmandage = activiteEgourmandage,
            activiteRecolteSanitaire = activiteSanitaire,
            activiteTaille = activiteTaille,
            coursEauxId = courEau,
            existeCoursEaux = courEauYesNo,
            dateVisite = dateSuivi,
            nomFongicide = nomFongicide,
            nombreFongicide = nombreFongicide,
            nomHerbicide = nomHerbicide,
            nombreHerbicide = nombreHerbicide,
            nomInsecticide = nomInsecticide,
            nombreInsecticide = nombreInsecticide,
            nombreDesherbage = nombreDesherbageAnnuel,
            nombreOmbrage = mutableListOf(),
            //nombreOmbrage = ,
            localiteNom = localiteNom,
            localiteId = localiteId,
            nombreSauvageons = nombreSauvageons,
            parcellesId = parcelleId,
            pente = penteYesNo,
            presenceAraignee = presenceAraignee,
            presenceBioAgresseur = preseceBio,
            presenceFourmisRouge = presenceFourmi,
            presenceInsectesRavageurs = presenceInsecte,
            presenceMenteReligieuse = presenceMante,
            presenceVerTerre = presenceVer,
            producteursId = producteurId,
            varieteAbres = arbreVariete,
            varietesCacaoId = cacaoVariete,
            varietesOmbrage = mutableListOf(),
            presenceSwollen = selectSwollenSuivi.selectedItem?.toString(),
            isSynced = false,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local",
            varieteOmbragesTemp = GsonUtils.toJson(ombrageAdapter?.getOmbragesAdded()),
            campagneNom = campagneNom,
            campagneId = campagneId,
            arbresAgroForestiersYesNo = agroForestierYesNo,
            presencePourritureBrune = pourritureBrune,
            presenceShooter = selectSwollenSuivi.selectedItem?.toString(),//swollestShoot,
            arbreAgroForestierStringify = ApiClient.gson.toJson(arbreAdapter?.getOmbragesAdded()),
            intrantNPK = "intrantNPK",
            //nombresacsNPK = editIntrantNPKSuivi.text.toString().trim(),
            intrantFiente = "intrantFiente",
            //nombresacsFiente = editIntrantFienteSuivi.text.toString(),
            intrantComposte = "intrantComposte",
            //nombresacsComposte = editNombreComposteSuivi.text.toString(),
            insectesParasitesTemp = ApiClient.gson.toJson(insecteAdapter?.getInsectesAdded()),
            animauxRencontresStringify = ApiClient.gson.toJson(animauxList)
        )
        return  item
    }


    fun clearFields() {
        editInsecticeNomSuivi.text  = null
        editInsecticeNombreSuivi.text   = null
        editFongicideNombreSuivi.text   = null
        editFongicideNomSuivi.text  = null
        editHerbicideNomSuivi.text  = null
        editHerbicideNombreSuivi.text   = null
        editDesherbageManuelSuivi.text  = null
        //editVarieteArbreSuivi.text  = null
//        editNbreSauvageonsSuivi.text    = null
//        editDateSuivi.text    = null
//        editVarieteArbreSuivi.text    = null
        editInsecticeNomSuivi.text    = null
        editInsecticeNombreSuivi.text    = null
        editFongicideNomSuivi.text    = null
        editFongicideNombreSuivi.text    = null
        editHerbicideNomSuivi.text    = null
        editHerbicideNombreSuivi.text    = null
        editDesherbageManuelSuivi.text    = null
//        editDateSuivi.text    = null

        selectProducteurSuivi.setSelection(0)
        selectParcelleSuivi.setSelection(0)
//        selectCoursEauYesNoSuivi.setSelection(0)
//        selectPenteYesNoSuivi.setSelection(0)
//        selectTailleSuivi.setSelection(0)
//        selectEgourmandageSuivi.setSelection(0)
//        selectDesherbageManuelSuivi.setSelection(0)
//        selectRecolteSanitaireSuivi.setSelection(0)
//        selectAgresseurSuivi.setSelection(0)
//        selectInsecteSuivi.setSelection(0)
        selectFourmisSuivi.setSelection(0)
        selectAraigneeSuivi.setSelection(0)
        selectVerDeTerreSuivi.setSelection(0)
        selectManteReligieuseSuivi.setSelection(0)

        ombragesList?.clear()
        ombrageAdapter?.notifyDataSetChanged()

        insectesList.clear()
        insecteAdapter?.notifyDataSetChanged()

        parcelleNom = ""
        parcelleId = ""
        producteurId = ""
        cacaoVariete = ""
        cacaoVarieteAutre = ""
        pourritureBrune = ""
        courEauYesNo = ""
        courEau = ""
        penteYesNo = ""
        arbreVariete = ""
        nombreSauvageons = ""
        activiteTaille = ""
        activiteEgourmandage = ""
        activiteDesherbage = ""
        activiteSanitaire = ""
        preseceBio = ""
        presenceInsecte = ""
        presenceFourmi = ""
        presenceAraignee = ""
        presenceVer = ""
        presenceMante = ""
        nomInsecticide = ""
        nombreInsecticide = ""
        nomFongicide = ""
        nombreFongicide = ""
        nombreHerbicide = ""
        nomHerbicide = ""
        nombreDesherbageAnnuel = ""

        //editVarieteArbreSuivi.requestFocus()

    }


    fun draftSuiviParcelle(draftModel: DataDraftedModel?) {
        nomInsecticide = editInsecticeNomSuivi.text?.trim().toString()
        nombreInsecticide = editInsecticeNombreSuivi.text?.trim().toString()

        nombreFongicide = editFongicideNombreSuivi.text?.trim().toString()
        nomFongicide = editFongicideNomSuivi.text?.trim().toString()

        nomHerbicide = editHerbicideNomSuivi.text?.trim().toString()
        nombreHerbicide = editHerbicideNombreSuivi.text?.trim().toString()

        nombreDesherbageAnnuel = editDesherbageManuelSuivi.text?.trim().toString()
        //arbreVariete = editVarieteArbreSuivi.text?.trim().toString()

        //nombreSauvageons = editNbreSauvageonsSuivi.text?.trim().toString()

        val suiviParcelleDraft = getSuiviParcelleObjet()

        LogUtils.json(suiviParcelleDraft)

        Commons.showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(suiviParcelleDraft),
                        typeDraft = "suivi_parcelle",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                Commons.showMessage(
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
        val suiviParcelleDrafted = ApiClient.gson.fromJson(draftedData.datas, SuiviParcelleModel::class.java)

        // Localite
        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val localitesDatas: MutableList<CommonData> = mutableListOf()
        localitesLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            localitesDatas.addAll(it)
        }
//        selectLocaliteSuiviParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//        provideDatasSpinnerSelection(
//            selectLocaliteSuiviParcelle,
//            suiviParcelleDrafted.localiteNom,
//            localitesDatas
//        )

        // Campagne
        val campagnesLists = CcbRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()
        val campagnesDatas: MutableList<CommonData> = mutableListOf()
        campagnesLists?.map {
            CommonData(id = it.id, nom = it.campagnesNom)
        }?.let {
            campagnesDatas.addAll(it)
        }
//        selectCampagneOfSuiviParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesDatas)
//        provideDatasSpinnerSelection(
//            selectCampagneOfSuiviParcelle,
//            suiviParcelleDrafted.campagneNom,
//            campagnesDatas
//        )

        // cours d'eaux
//        provideStringSpinnerSelection(
//            selectCoursEauSuivi,
//            suiviParcelleDrafted.existeCoursEaux,
//            resources.getStringArray(R.array.YesOrNo)
//        )

        // Pente
//        provideStringSpinnerSelection(
//            selectPenteYesNoSuivi,
//            suiviParcelleDrafted.pente,
//            resources.getStringArray(R.array.YesOrNo)
//        )

        // Ombrage
        val ombragesType = object : TypeToken<MutableList<OmbrageVarieteModel>>(){}.type
        val ombragesLists: MutableList<OmbrageVarieteModel> = ApiClient.gson.fromJson(suiviParcelleDrafted.varieteOmbragesTemp ?: "[]", ombragesType)
        ombragesList?.addAll(ombragesLists)
        ombrageAdapter?.notifyDataSetChanged()

        // Variete arbres
//        editVarieteArbreSuivi.setText(suiviParcelleDrafted.varieteAbres)
//        editNbreSauvageonsSuivi.setText(suiviParcelleDrafted.nombreSauvageons)

        // Agro yes no
//        provideStringSpinnerSelection(
//            selectBeneficiareAgroOfSuiviParcelle,
//            suiviParcelleDrafted.arbresAgroForestiersYesNo,
//            resources.getStringArray(R.array.YesOrNo)
//        )

        // Arbres agros
        val arbresType = object : TypeToken<MutableList<OmbrageVarieteModel>>(){}.type
        val arbresLists: MutableList<OmbrageVarieteModel> = ApiClient.gson.fromJson(suiviParcelleDrafted.arbreAgroForestierStringify ?: "[]", arbresType)
        arbresList?.addAll(arbresLists)
        arbreAdapter?.notifyDataSetChanged()

        // Taille
//        provideStringSpinnerSelection(
//            selectTailleSuivi,
//            suiviParcelleDrafted.activiteTaille,
//            resources.getStringArray(R.array.lowMediumHigh)
//        )
//
//        // Engourmandage
//        provideStringSpinnerSelection(
//            selectEgourmandageSuivi,
//            suiviParcelleDrafted.activiteEgourmandage,
//            resources.getStringArray(R.array.lowMediumHigh)
//        )
//
//        // Desherbage manuel
//        provideStringSpinnerSelection(
//            selectDesherbageManuelSuivi,
//            suiviParcelleDrafted.activiteDesherbageManuel,
//            resources.getStringArray(R.array.lowMediumHigh)
//        )

        // Recolte sanitaire
//        provideStringSpinnerSelection(
//            selectRecolteSanitaireSuivi,
//            suiviParcelleDrafted.activiteRecolteSanitaire,
//            resources.getStringArray(R.array.lowMediumHigh)
//        )
//
//        // Intarnt NPK
//        editIntrantNPKSuivi.setText(suiviParcelleDrafted.nombresacsNPK)
//
//        // Intarnt Fiente
//        editIntrantFienteSuivi.setText(suiviParcelleDrafted.nombresacsFiente)
//
//        // Intarnt Compost
//        editNombreComposteSuivi.setText(suiviParcelleDrafted.nombresacsComposte)
//
//        // Bio agresseur
//        provideStringSpinnerSelection(
//            selectAgresseurSuivi,
//            suiviParcelleDrafted.presenceBioAgresseur,
//            resources.getStringArray(R.array.fullyPoor)
//        )

        // Pourriture
        provideStringSpinnerSelection(
            selectPourritureBruneOfSuiviParcelle,
            suiviParcelleDrafted.presencePourritureBrune,
            resources.getStringArray(R.array.fullyPoor)
        )

        // Insecte
//        provideStringSpinnerSelection(
//            selectInsecteSuivi,
//            suiviParcelleDrafted.presenceInsectesRavageurs,
//            resources.getStringArray(R.array.YesOrNo)
//        )

        // insectes list
        val insectesType = object : TypeToken<MutableList<InsecteRavageurModel>>(){}.type
        val insectesLists: MutableList<InsecteRavageurModel> = ApiClient.gson.fromJson(suiviParcelleDrafted.insectesParasitesTemp ?: "[]", insectesType)
        insectesList.addAll(insectesLists)
        insecteAdapter?.notifyDataSetChanged()

        // Fourni
        provideStringSpinnerSelection(
            selectFourmisSuivi,
            suiviParcelleDrafted.presenceFourmisRouge,
            resources.getStringArray(R.array.fullyPoor)
        )

        // araignés
        provideStringSpinnerSelection(
            selectAraigneeSuivi,
            suiviParcelleDrafted.presenceAraignee,
            resources.getStringArray(R.array.fullyPoor)
        )

        // ver
        provideStringSpinnerSelection(
            selectVerDeTerreSuivi,
            suiviParcelleDrafted.presenceVerTerre,
            resources.getStringArray(R.array.fullyPoor)
        )

        // mante religieuz
        provideStringSpinnerSelection(
            selectManteReligieuseSuivi,
            suiviParcelleDrafted.presenceMenteReligieuse,
            resources.getStringArray(R.array.fullyPoor)
        )

        // Insecticide
        editInsecticeNomSuivi.setText(suiviParcelleDrafted.nomInsecticide)
        editInsecticeNombreSuivi.setText(suiviParcelleDrafted.nombreInsecticide)

        // Fongicide
        editFongicideNomSuivi.setText(suiviParcelleDrafted.nomFongicide)
        editFongicideNombreSuivi.setText(suiviParcelleDrafted.nombreFongicide)

        // Herbicide
        editHerbicideNomSuivi.setText(suiviParcelleDrafted.nomHerbicide)
        editHerbicideNombreSuivi.setText(suiviParcelleDrafted.nombreHerbicide)

        // insectes amis list
        val animauxType = object : TypeToken<MutableList<String>>(){}.type
        val animauxLists: MutableList<String> = ApiClient.gson.fromJson(suiviParcelleDrafted.animauxRencontresStringify ?: "[]", animauxType)
        animauxList?.addAll(animauxLists)
        animalAdapter?.notifyDataSetChanged()

        // Sholen
        provideStringSpinnerSelection(
            selectSwollenSuivi,
            suiviParcelleDrafted.presenceSwollen,
            resources.getStringArray(R.array.YesOrNo)
        )

        // Nombre desherbage
        editDesherbageManuelSuivi.setText(suiviParcelleDrafted.nombreDesherbage)
        //editDateSuivi.setText(suiviParcelleDrafted.dateVisite)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_parcelle)

        suiviParcelleDao = CcbRoomDatabase.getDatabase(this)?.suiviParcelleDao()

//        editDateSuivi.setOnClickListener {
//            datePickerDialog = null
//            val calendar: Calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
//                editDateSuivi.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
//                dateSuivi = editDateSuivi.text?.toString()!!
//            }, year, month, dayOfMonth)
//
//            datePickerDialog!!.datePicker.maxDate = Date().time
//            datePickerDialog?.show()
//        }

        try {
            setupBioSelection()

            setupManteSelection()

            setupAraigneeSelection()

            setupVerSelection()

            setupInsectesSelection()

            setupFourmiSelection()

            setupEgourmandageSelection()

            setupDesherbageSelection()

            setupCampagneSelection()

            setupSanitaireSelection()

            setupTailleSelection()

            setupCourEauxSelection()

            setupPenteYesNoSelection()

            setupBenefAgroForestierYesNoSelection()

            setupPourritureBruneSelection()

            //setupSwollerShootSelection()

            setupCourEauYesNoSelection()

            setupLocaliteSelection()

            setupOmbragesSuiviParcelle()

            setArbreParcelle()

            setAnimalParcelle()

            setInsectes()
        } catch (ex: Exception) {
            LogUtils.e("SParc "+ex.message)
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

//        clickAddOmbrage.setOnClickListener {
//            try {
//                if (editVarieteOmbrageSuivi.text.toString()
//                        .isEmpty() || editVarieteOmbrageNombreSuivi.text.toString().isEmpty()
//                ) {
//                    Commons.showMessage("Renseignez une culture, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val ombrageVariete = OmbrageVarieteModel(
//                    0,
//                    editVarieteOmbrageSuivi.text.toString().trim(),
//                    editVarieteOmbrageNombreSuivi.text.toString().trim()
//                )
//                addOmbrageVariete(ombrageVariete)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }

//        clickAddAgroArbreSuiviParcelle.setOnClickListener {
//            try {
//                if (editAgroArbreSuivi.text.toString()
//                        .isEmpty() || editArbreNombreSuivi.text.toString().isEmpty()
//                ) {
//                    Commons.showMessage("Renseignez un arbre, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val arbreModel = OmbrageVarieteModel(
//                    0,
//                    editAgroArbreSuivi.text.toString().trim(),
//                    editArbreNombreSuivi.text.toString().trim()
//                )
//                addArbreAgro(arbreModel)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//        clickSaveInsecteQuantiteOfSuiviParcelle.setOnClickListener {
//            try {
//                if (editInsecteNomOfSuiviParcelle.text.toString().isEmpty()) {
//                    Commons.showMessage("Renseignez un insecte, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val insecteModel = InsecteRavageurModel(
//                    uid = 0,
//                    nom = editInsecteNomOfSuiviParcelle.text.toString().trim(),
//                    selectInsecteQuantiteOfSuiviParcelle.selectedItem.toString(),
//                )
//
//                addInsectesParasites(insecteModel)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }

        clickSaveAnimauxSuiviParcelle.setOnClickListener {
            try {
                if (editAnimalSuiviParcelle.text.toString().isEmpty()) {
                    Commons.showMessage("Renseignez un animal, svp !", this, callback = {})
                    return@setOnClickListener
                }

                addAnimalSauvage(editAnimalSuiviParcelle.text.toString())
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        clickCancelSuivi.setOnClickListener {
            clearFields()
        }

        clickSaveSuivi.setOnClickListener {
            collectDatas()
        }

        imageDraftBtn.setOnClickListener {
            draftSuiviParcelle(draftedDataSuiviParcelle ?: DataDraftedModel(uid = 0))
        }

        applyFilters(editAnimalSuiviParcelle)
        //applyFilters(editBenefAgroNombreOfSuiviParcelle)

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataSuiviParcelle = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(
                    uid = 0
                )
                undraftedDatas(draftedDataSuiviParcelle!!)
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }
}
