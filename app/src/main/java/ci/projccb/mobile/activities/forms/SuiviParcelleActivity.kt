package ci.projccb.mobile.activities.forms

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner
import ci.projccb.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.projccb.mobile.adapters.AnimalAdapter
import ci.projccb.mobile.adapters.InsecteAdapter
import ci.projccb.mobile.adapters.MultipleItemAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.adapters.OnlyFieldAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.ArbreData
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.configDate
import ci.projccb.mobile.tools.Commons.Companion.getSpinnerContent
import ci.projccb.mobile.tools.Commons.Companion.isSpinnerEmpty
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_parcelle.editQtArbrOmbrParcel
import kotlinx.android.synthetic.main.activity_parcelle.recyclerArbrOmbrListParcel
import kotlinx.android.synthetic.main.activity_parcelle.selectArbrOmbrParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.*
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.clickCancelInfosProducteur
import java.util.*



@SuppressLint("All")
class SuiviParcelleActivity : AppCompatActivity() {


    companion object {
        const val TAG = "SuiviParcelleActivity::class"
    }


    private var listSelectVarieteArbreList: MutableList<String> = arrayListOf()
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


    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();
    val parcelleCommon = CommonData();

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

            //clearInsecteFields()
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


//    fun clearInsecteFields() {
//        editInsecteNomOfSuiviParcelle.text = null
//        selectInsecteQuantiteOfSuiviParcelle.setSelection(0)
//    }


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


//    fun setInsectes() {
//        try {
//            insecteAdapter = InsecteAdapter(insectesList)
//            recyclerInsecteOfSuiviParcelle.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerInsecteOfSuiviParcelle.adapter = insecteAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//    }


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
//            selectProducteurSuivi!!.adapter =
//                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)
//
//            if (suiviParcelleDraftedLocal != null) {
//                provideDatasSpinnerSelection(
//                    selectProducteurSuivi,
//                    suiviParcelleDraftedLocal.parcelleProducteur,
//                    producteursDatas
//                )
//            }
//
//            selectProducteurSuivi.setTitle("Choisir le producteur")
//            selectProducteurSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        val producteur = producteursList!![position]
//                        producteurNom = "${producteur.nom} ${producteur.prenoms}"
//
//                        producteurId = if (producteur.isSynced) {
//                            producteur.id.toString()
//                        } else {
//                            producteur.uid.toString()
//                        }
//
//                        //LogUtils.e(TAG, "ProducteurID => $producteurId")
//                        setupParcelleSelection(producteurId)
//                    }
//
//                    override fun onNothingSelected(arg0: AdapterView<*>) {
//                    }
//                }

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


   // fun setupPourritureBruneSelection() {
//        try {
//            selectPourritureBruneOfSuiviParcelle.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        pourritureBrune = resources.getStringArray(R.array.lowMediumHigh)[position]
//                    }
//
//                    override fun onNothingSelected(arg0: AdapterView<*>) {
//                    }
//                }
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//    }


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
//            selectFourmisSuivi.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        adapterView: AdapterView<*>,
//                        view: View,
//                        position: Int,
//                        l: Long
//                    ) {
//                        presenceFourmi = resources.getStringArray(R.array.fullyPoor)[position]
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
                LogUtils.d("Mante Real : "+presenceMante)
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
//        nomInsecticide = editInsecticeNomSuivi.text?.trim().toString()
//        nombreInsecticide = editInsecticeNombreSuivi.text?.trim().toString()
//
//        nombreFongicide = editFongicideNombreSuivi.text?.trim().toString()
//        nomFongicide = editFongicideNomSuivi.text?.trim().toString()
//
//        nomHerbicide = editHerbicideNomSuivi.text?.trim().toString()
//        nombreHerbicide = editHerbicideNombreSuivi.text?.trim().toString()
//
//        nombreDesherbageAnnuel = editDesherbageManuelSuivi.text?.trim().toString()
        //arbreVariete = editVarieteArbreSuivi.text?.trim().toString()

        //nombreSauvageons = editNbreSauvageonsSuivi.text?.trim().toString()


//        if (producteurId.isEmpty()) {
//            Commons.showMessage(
//                "Choisissez un producteur svp.",
//                this,
//                finished = false,
//                callback = {},
//                positive = "Compris !",
//                deconnec = false,
//                showNo = false
//
//            )
//            return
//        }
//
//        if (parcelleId.isBlank()) {
//            Commons.showMessage(message = "Selectionnez la parcelle, svp !", context = this, finished = false, callback = {})
//            return
//        }

        val itemModelOb = getSuiviParcelleObjet()

        if(itemModelOb == null) return

        val SParcelle = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localiteId = localiteCommon.id.toString()
                producteursId = producteurCommon.id.toString()
                parcelle_id = parcelleCommon.id.toString()

                //itemsStr = GsonUtils.toJson((recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { ArbreData(null, it.variete, it.nombre) })

//                insectesParasitesTemp = GsonUtils.toJson((recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
//                nombreInsectesParasitesTemp = GsonUtils.toJson((recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                pesticideUtiliseAnneeDerStr = GsonUtils.toJson((recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded())
                intrantUtiliseAnneeDerStr = GsonUtils.toJson((recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded())

                presenceInsectesParasitesRavageurStr = GsonUtils.toJson((recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded())
                autreInsectesParasitesRavageurStr = GsonUtils.toJson((recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded())

                insectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                nombreinsectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                animauxRencontresStringify = GsonUtils.toJson((recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
//            this.add(Pair("Arbre d'ombrage", (recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
//            this.add(Pair("Insecte parasite", (recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Pesticides utilisés l'an dernier", (recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Type: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))
            this.add(Pair("Intrants utilisés l'an dernier", (recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Type: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))

            this.add(Pair("Autre insecte parasites ou ravageurs", (recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Insecte parasites ou ravageurs", (recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Insecte amis", (recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Animaux rencontrés", (recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { "${it.nom}\n" }.toModifString() ))
        }.map { MapEntry(it.first, it.second) }

        try {
            val intentSuiviParcellePreview = Intent(this, SuiviParcellePreviewActivity::class.java)
            intentSuiviParcellePreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentSuiviParcellePreview.putExtra("preview", SParcelle)
            intentSuiviParcellePreview.putExtra("draft_id", draftedDataSuiviParcelle?.uid)
            startActivity(intentSuiviParcellePreview)
        } catch (ex: Exception) {

        }
    }

    private fun getSuiviParcelleObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<SuiviParcelleModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false
//        val item = SuiviParcelleModel(
//            activiteDesherbageManuel = activiteDesherbage,
//            parcelleNom = parcelleNom,
//            parcelleSuperficie = parcelleSuperficie,
//            parcelleProducteur = producteurNom,
//            activiteEgourmandage = activiteEgourmandage,
//            activiteRecolteSanitaire = activiteSanitaire,
//            activiteTaille = activiteTaille,
//            coursEauxId = courEau,
//            existeCoursEaux = courEauYesNo,
//            dateVisite = dateSuivi,
//            nomFongicide = nomFongicide,
//            nombreFongicide = nombreFongicide,
//            nomHerbicide = nomHerbicide,
//            nombreHerbicide = nombreHerbicide,
//            nomInsecticide = nomInsecticide,
//            nombreInsecticide = nombreInsecticide,
//            nombreDesherbage = nombreDesherbageAnnuel,
//            nombreOmbrage = mutableListOf(),
//            //nombreOmbrage = ,
//            localiteNom = localiteNom,
//            localiteId = localiteId,
//            nombreSauvageons = nombreSauvageons,
//            parcellesId = parcelleId,
//            pente = penteYesNo,
//            presenceAraignee = presenceAraignee,
//            presenceBioAgresseur = preseceBio,
//            presenceFourmisRouge = presenceFourmi,
//            presenceInsectesRavageurs = presenceInsecte,
//            presenceMenteReligieuse = presenceMante,
//            presenceVerTerre = presenceVer,
//            producteursId = producteurId,
//            varieteAbres = arbreVariete,
//            varietesCacaoId = cacaoVariete,
//            varietesOmbrage = mutableListOf(),
//            presenceSwollen = selectSwollenSuivi.selectedItem?.toString(),
//            isSynced = false,
//            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
//            origin = "local",
//            varieteOmbragesTemp = GsonUtils.toJson(ombrageAdapter?.getOmbragesAdded()),
//            campagneNom = campagneNom,
//            campagneId = campagneId,
//            arbresAgroForestiersYesNo = agroForestierYesNo,
//            presencePourritureBrune = pourritureBrune,
//            presenceShooter = selectSwollenSuivi.selectedItem?.toString(),//swollestShoot,
//            arbreAgroForestierStringify = ApiClient.gson.toJson(arbreAdapter?.getOmbragesAdded()),
//            intrantNPK = "intrantNPK",
//            //nombresacsNPK = editIntrantNPKSuivi.text.toString().trim(),
//            intrantFiente = "intrantFiente",
//            //nombresacsFiente = editIntrantFienteSuivi.text.toString(),
//            intrantComposte = "intrantComposte",
//            //nombresacsComposte = editNombreComposteSuivi.text.toString(),
//            insectesParasitesTemp = ApiClient.gson.toJson(insecteAdapter?.getInsectesAdded()),
//            animauxRencontresStringify = ApiClient.gson.toJson(animauxList)
//        )
//        return  item
        var itemList = getSetupSuiviParcelleModel(SuiviParcelleModel(
            isSynced = false,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local",
        ), mutableListOf<Pair<String,String>>())
        //LogUtils.d(.toString())
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

        if(isMissing && (isMissingDial2 || isMissingDial2) ){
            Commons.showMessage(
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

    fun getSetupSuiviParcelleModel(
        prodModel: SuiviParcelleModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<SuiviParcelleModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_suivi_parcelle)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupSuiviParcelleModel(
        prodModel: SuiviParcelleModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_suivi_parcelle)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }

    fun clearFields() {
//        editInsecticeNomSuivi.text  = null
//        editInsecticeNombreSuivi.text   = null
//        editFongicideNombreSuivi.text   = null
//        editFongicideNomSuivi.text  = null
//        editHerbicideNomSuivi.text  = null
//        editHerbicideNombreSuivi.text   = null
        editDesherbageManuelSuivi.text  = null
        //editVarieteArbreSuivi.text  = null
//        editNbreSauvageonsSuivi.text    = null
//        editDateSuivi.text    = null
//        editVarieteArbreSuivi.text    = null
//        editInsecticeNomSuivi.text    = null
//        editInsecticeNombreSuivi.text    = null
//        editFongicideNomSuivi.text    = null
//        editFongicideNombreSuivi.text    = null
//        editHerbicideNomSuivi.text    = null
//        editHerbicideNombreSuivi.text    = null
        editDesherbageManuelSuivi.text    = null
//        editDateSuivi.text    = null

//        selectProducteurSuivi.setSelection(0)
//        selectParcelleSuivi.setSelection(0)
//        selectCoursEauYesNoSuivi.setSelection(0)
//        selectPenteYesNoSuivi.setSelection(0)
//        selectTailleSuivi.setSelection(0)
//        selectEgourmandageSuivi.setSelection(0)
//        selectDesherbageManuelSuivi.setSelection(0)
//        selectRecolteSanitaireSuivi.setSelection(0)
//        selectAgresseurSuivi.setSelection(0)
//        selectInsecteSuivi.setSelection(0)
        //selectFourmisSuivi.setSelection(0)
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
//        nomInsecticide = editInsecticeNomSuivi.text?.trim().toString()
//        nombreInsecticide = editInsecticeNombreSuivi.text?.trim().toString()
//
//        nombreFongicide = editFongicideNombreSuivi.text?.trim().toString()
//        nomFongicide = editFongicideNomSuivi.text?.trim().toString()
//
//        nomHerbicide = editHerbicideNomSuivi.text?.trim().toString()
//        nombreHerbicide = editHerbicideNombreSuivi.text?.trim().toString()
//
//        nombreDesherbageAnnuel = editDesherbageManuelSuivi.text?.trim().toString()
        //arbreVariete = editVarieteArbreSuivi.text?.trim().toString()

        //nombreSauvageons = editNbreSauvageonsSuivi.text?.trim().toString()

        val itemModelOb = getSuiviParcelleObjet(false)

        if(itemModelOb == null) return

        val SParcelleDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localiteId = localiteCommon.id.toString()
                producteursId = producteurCommon.id.toString()
                parcelle_id = parcelleCommon.id.toString()

//                itemsStr = GsonUtils.toJson((recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { ArbreData(0, it.variete, it.nombre) })
//
//                insectesParasitesTemp = GsonUtils.toJson((recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
//                nombreInsectesParasitesTemp = GsonUtils.toJson((recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                insectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                nombreinsectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                animauxRencontresStringify = GsonUtils.toJson((recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
            }
        }

//        LogUtils.json(suiviParcelleDraft)

        Commons.showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(SParcelleDraft),
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
//        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//        val localitesDatas: MutableList<CommonData> = mutableListOf()
//        localitesLists?.map {
//            CommonData(id = it.id, nom = it.nom)
//        }?.let {
//            localitesDatas.addAll(it)
//        }
//        selectLocaliteSuiviParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//        provideDatasSpinnerSelection(
//            selectLocaliteSuiviParcelle,
//            suiviParcelleDrafted.localiteNom,
//            localitesDatas
//        )

        // Campagne
//        val campagnesLists = CcbRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()
//        val campagnesDatas: MutableList<CommonData> = mutableListOf()
//        campagnesLists?.map {
//            CommonData(id = it.id, nom = it.campagnesNom)
//        }?.let {
//            campagnesDatas.addAll(it)
//        }
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
//        val ombragesType = object : TypeToken<MutableList<OmbrageVarieteModel>>(){}.type
//        val ombragesLists: MutableList<OmbrageVarieteModel> = ApiClient.gson.fromJson(suiviParcelleDrafted.varieteOmbragesTemp ?: "[]", ombragesType)
//        ombragesList?.addAll(ombragesLists)
//        ombrageAdapter?.notifyDataSetChanged()

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
//        val arbresType = object : TypeToken<MutableList<OmbrageVarieteModel>>(){}.type
//        val arbresLists: MutableList<OmbrageVarieteModel> = ApiClient.gson.fromJson(suiviParcelleDrafted.arbreAgroForestierStringify ?: "[]", arbresType)
//        arbresList?.addAll(arbresLists)
//        arbreAdapter?.notifyDataSetChanged()

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
//        provideStringSpinnerSelection(
//            selectPourritureBruneOfSuiviParcelle,
//            suiviParcelleDrafted.presencePourritureBrune,
//            resources.getStringArray(R.array.fullyPoor)
//        )

        // Insecte
//        provideStringSpinnerSelection(
//            selectInsecteSuivi,
//            suiviParcelleDrafted.presenceInsectesRavageurs,
//            resources.getStringArray(R.array.YesOrNo)
//        )

        // insectes list
//        val insectesType = object : TypeToken<MutableList<InsecteRavageurModel>>(){}.type
//        val insectesLists: MutableList<InsecteRavageurModel> = ApiClient.gson.fromJson(suiviParcelleDrafted.insectesParasitesTemp ?: "[]", insectesType)
//        insectesList.addAll(insectesLists)
//        insecteAdapter?.notifyDataSetChanged()

        // Fourni
//        provideStringSpinnerSelection(
//            selectFourmisSuivi,
//            suiviParcelleDrafted.presenceFourmisRouge,
//            resources.getStringArray(R.array.fullyPoor)
//        )

        // araignés
//        provideStringSpinnerSelection(
//            selectAraigneeSuivi,
//            suiviParcelleDrafted.presenceAraignee,
//            resources.getStringArray(R.array.fullyPoor)
//        )
//
//        // ver
//        provideStringSpinnerSelection(
//            selectVerDeTerreSuivi,
//            suiviParcelleDrafted.presenceVerTerre,
//            resources.getStringArray(R.array.fullyPoor)
//        )
//
//        // mante religieuz
//        provideStringSpinnerSelection(
//            selectManteReligieuseSuivi,
//            suiviParcelleDrafted.presenceMenteReligieuse,
//            resources.getStringArray(R.array.fullyPoor)
//        )
//
//        // Insecticide
//        editInsecticeNomSuivi.setText(suiviParcelleDrafted.nomInsecticide)
//        editInsecticeNombreSuivi.setText(suiviParcelleDrafted.nombreInsecticide)
//
//        // Fongicide
//        editFongicideNomSuivi.setText(suiviParcelleDrafted.nomFongicide)
//        editFongicideNombreSuivi.setText(suiviParcelleDrafted.nombreFongicide)
//
//        // Herbicide
//        editHerbicideNomSuivi.setText(suiviParcelleDrafted.nomHerbicide)
//        editHerbicideNombreSuivi.setText(suiviParcelleDrafted.nombreHerbicide)
//
//        // insectes amis list
//        val animauxType = object : TypeToken<MutableList<String>>(){}.type
//        val animauxLists: MutableList<String> = ApiClient.gson.fromJson(suiviParcelleDrafted.animauxRencontresStringify ?: "[]", animauxType)
//        animauxList?.addAll(animauxLists)
//        animalAdapter?.notifyDataSetChanged()
//
//        // Sholen
//        provideStringSpinnerSelection(
//            selectSwollenSuivi,
//            suiviParcelleDrafted.presenceSwollen,
//            resources.getStringArray(R.array.YesOrNo)
//        )

        // Nombre desherbage
        //editDesherbageManuelSuivi.setText(suiviParcelleDrafted.nombreDesherbage)
        //editDateSuivi.setText(suiviParcelleDrafted.dateVisite)

//        setVarieteArbrParcelleRV(
//            (GsonUtils.fromJson<MutableList<ArbreData>>(suiviParcelleDrafted.itemsStr, object : TypeToken<MutableList<ArbreData>>() {}.type).map { "${it.arbre}" }.toMutableList()),
//            (GsonUtils.fromJson<MutableList<ArbreData>>(suiviParcelleDrafted.itemsStr, object : TypeToken<MutableList<ArbreData>>() {}.type).map { "${it.nombre}" }.toMutableList())
//        )

//        setInsParasSParcelleRV(
//            (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.insectesParasitesTemp, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
//            (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.nombreInsectesParasitesTemp, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList())
//        )

//        setAutreInsParasSParcelleRV(
//            (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.insectesAmisStr, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
//            (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.nombreinsectesAmisStr, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList())
//        )

        setAnimauSParcelleRV(
            (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.animauxRencontresStringify, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
        )

        setupSectionSelection(suiviParcelleDrafted.section,
            suiviParcelleDrafted.localiteId,
            suiviParcelleDrafted.producteursId,
            suiviParcelleDrafted.parcelle_id)

        ///setupVarieteArbrMultiSelection(GsonUtils.fromJson(suiviParcelleDrafted.arbreStr, object : TypeToken<MutableList<String>>() {}.type))

//        Commons.setListenerForSpinner(this,
//            "Bénéficies tu d'arbres agro-forestiers ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectAbrAgroForesSuiviParcel,
//            currentVal = suiviParcelleDrafted.arbresAgroForestiersYesNo,
//            itemChanged = arrayListOf(Pair(1, "Oui")),
//            listIem = resources.getStringArray(R.array.YesOrNo)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerItemRecusSuiviParcel.visibility = visibility
//                    containerVarieteArbrSuiviParcel.visibility = visibility
//                }
//            })

//        Commons.setListenerForSpinner(this,
//            "Quand avez-vous reçu des arbres agro forestiers ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectArbreAgroSuiviParcel,
//            currentVal = suiviParcelleDrafted.recuArbreAgroForestier,
//            listIem = resources.getStringArray(R.array.delai_arbre)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })

        Commons.setListenerForSpinner(this,
            "Fréquence activité de taille ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectActDTailleSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteTaille,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Fréquence activité d’egourmandage ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectActDTailleSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteEgourmandage,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Fréquence activité de récolte sanitaire ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectActDRecolSanitSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteRecolteSanitaire,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

//        Commons.setListenerForSpinner(this,
//            "Type de pesticide utilisé ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectListTypPestiSuiviParcel,
//            currentVal = suiviParcelleDrafted.pesticideUtiliseAnne,
//            listIem = resources.getStringArray(R.array.lowMediumHigh)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })

//        Commons.setListenerForSpinner(this,
//            "Y'a t'il une présence de pourriture brune ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectPourritureBruneOfSuiviParcelle,
//            currentVal = suiviParcelleDrafted.presencePourritureBrune,
//            listIem = resources.getStringArray(R.array.fullyPoor)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })
//
//        Commons.setListenerForSpinner(this,
//            "Y'a t'il une présence de SholenShoot ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectSwollenSuivi,
//            currentVal = suiviParcelleDrafted.presenceShooter,
//            listIem = resources.getStringArray(R.array.fullyPoor)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il une présence d'insectes parasites ou ravageurs ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectInsecteParOuRavSuivi,
            currentVal = suiviParcelleDrafted.presenceInsectesParasites,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
//                    qdf qf
//                    containerListInsectSuivParce.visibility = visibility
                    linearInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

//        Commons.setListenerForSpinner(this,
//            "Choix des insectes :","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectListInsecteSuivParce,
//            currentVal = suiviParcelleDrafted.presenceInsectesParasitesRavageur,
//            itemChanged = arrayListOf(Pair(1, "Autre")),
//            listIem = resources.getStringArray(R.array.listeInsectes)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    linearPresenceInsecteRavageurOfSuiviParcelle.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il une présence de fourmis rouges ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectFourmisSuiviParcel,
            currentVal = suiviParcelleDrafted.presenceFourmisRouge,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il une présence d'araignées ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAraigneeSuivi,
            currentVal = suiviParcelleDrafted.presenceAraignee,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il une présence de verre de terres ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectVerDeTerreSuivi,
            currentVal = suiviParcelleDrafted.presenceVerTerre,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il une présence de mentes religieuses ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectManteReligieuseSuivi,
            currentVal = suiviParcelleDrafted.presenceMenteReligieuse,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Présence d'autres types d’insecte ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectPresencInsectSuiviParcel,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerInsectAmisSuiviParcelle.visibility = visibility
                }
            })

        passSetupSuiviParcelleModel(suiviParcelleDrafted)
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
//            setupBioSelection()
//
//            setupManteSelection()
//
//            setupAraigneeSelection()
//
//            setupVerSelection()
//
//            setupInsectesSelection()
//
//            setupFourmiSelection()
//
//            setupEgourmandageSelection()
//
//            setupDesherbageSelection()
//
//            setupCampagneSelection()
//
//            setupSanitaireSelection()
//
//            setupTailleSelection()
//
//            setupCourEauxSelection()
//
//            setupPenteYesNoSelection()
//
//            setupBenefAgroForestierYesNoSelection()
//
//            setupPourritureBruneSelection()

            //setupSwollerShootSelection()

//            setupCourEauYesNoSelection()
//
//            setupLocaliteSelection()
//
//            setupOmbragesSuiviParcelle()
//
//            setArbreParcelle()
//
//            setAnimalParcelle()
//
//            setInsectes()
            //setOtherListener()

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

//        clickSaveAnimauxSuiviParcelle.setOnClickListener {
//            try {
//                if (editAnimalSuiviParcelle.text.toString().isEmpty()) {
//                    Commons.showMessage("Renseignez un animal, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                addAnimalSauvage(editAnimalSuiviParcelle.text.toString())
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }

//        clickCancelSuivi.setOnClickListener {
//            clearFields()
//        }

        clickSaveSuivi.setOnClickListener {
            collectDatas()
        }

        clickCancelSuivi.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftSuiviParcelle(draftedDataSuiviParcelle ?: DataDraftedModel(uid = 0))
        }

        //applyFilters(editAnimalSuiviParcelle)
        //applyFilters(editBenefAgroNombreOfSuiviParcelle)

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataSuiviParcelle = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(
                    uid = 0
                )
                undraftedDatas(draftedDataSuiviParcelle!!)
            }else{
                setAllSelection()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun setOtherListener() {

        editdateVisiteSuiviParcel.setOnClickListener { configDate(editdateVisiteSuiviParcel) }

        Commons.setEditAndSpinnerRV(this, recyclerInsecteAmisSuiviParcelle, clickAddInsectAmisSuiviParcelle, editInsecteNomAmisParcelle, selectInsecteAmisQuantiteSuiviParcelle,"Nom d'insecte", "Quantité")
        Commons.setEditAndSpinnerRV(this, recyclerAutreInsecteParOuRavSuiviParcelle, clickAddAutreInsecteParOuRavSuiviParcelle, editAutreInsecteParOuRavNomSParcelle, selectAutrInsecteParOuRavQtSuiviParcelle, "Nom d'insecte", "Quantité")
        Commons.setSpinnerAndSpinnerRV(this, recyclerInsecteParOuRavSuiviParcelle, clickAddInsecteParOuRavSuiviParcelle, selectInsecteParOuRavNomSuiviParcelle, selectInsecteParOuRavQtSuiviParcelle, "Nom d'insecte", "Quantité")

        Commons.setFiveItremRV(this, recyclerPestListSuiviParcel, clickAddPestListSuiviParcel, selectPestNomSParcell,selectPestContenantSParcell,selectPestUniteSParcell,editQuantitPestSParcel,editFrequencPestSParcel, libeleList = mutableListOf<String>("Type", "Contenant", "Unité", "Quantité", "Fréquence"))
        Commons.setFiveItremRV(this, recyclerIntantAnDerListSuiviParcel, clickAddIntantAnDerListSuiviParcel, selectIntantAnDerSParcell,selectIntantAnDerContenantSParcell,selectIntantAnDerUniteSParcell,editIntantAnDerPestSParcel,editFrequencIntantAnDerSParcel, libeleList = mutableListOf<String>("Type", "Contenant", "Unité", "Quantité", "Fréquence"))
        Commons.setFiveItremRV(this, recyclerTraitInsecteParOuRavListSuiviParcel, clickAddTraitInsecteParOuRavListSuiviParcel, selectTraitInsecteParOuRavNomSParcell,selectTraitInsecteParOuRavContenantSParcell,selectTraitInsecteParOuRavUniteSParcell,editTraitInsecteParOuRavQtSParcel,editTraitInsecteParOuRavFrequSParcel, libeleList = mutableListOf<String>("Type", "Contenant", "Unité", "Quantité", "Fréquence"))

        //setVarieteArbrParcelleRV()

        //setInsParasSParcelleRV()

        //setAutreInsParasSParcelleRV()

        //setMaladieSParcelleRV()

//        setPestAnDernSParcelleRV()
//
//        setIntrantAnDernSParcelleRV()

        //setPesticideSParcelleRV()

        setAnimauSParcelleRV()

    }

//    private fun setPesticideSParcelleRV( libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val pesticideListSParcelle = mutableListOf<AdapterItemModel>()
//        var countN = 0
////        libeleList.forEach {
////            pesticideListSParcelle.add(AdapterItemModel(0, it, valueList.get(countN)))
////            countN++
////        }
//        val pesticideSParcelleAdapter = MultipleItemAdapter(pesticideListSParcelle, )
//        try {
//            recyclerIntrantListSuiviParcel.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerIntrantListSuiviParcel.adapter = pesticideSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddIntrantListSuiviParcel.setOnClickListener {
//            try {
//                if (selectIntrantNomSParcell.isSpinnerEmpty()
//                    || selectIntrantContenantSParcell.isSpinnerEmpty()
//                    || selectIntrantUniteSParcell.isSpinnerEmpty()
//                    || editQuantitIntrantSParcel.text.toString().isNullOrBlank()
//                    || editFrequencIntrantSParcel.text.toString().isNullOrBlank()
//                ) {
//                    Commons.showMessage("Renseignez des données, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val pesticideParRav = AdapterItemModel(
//                    0,
//                    selectIntrantNomSParcell.getSpinnerContent(),
//                    selectIntrantContenantSParcell.getSpinnerContent(),
//                    selectIntrantUniteSParcell.getSpinnerContent(),
//                    editQuantitIntrantSParcel.text.toString(),
//                    editFrequencIntrantSParcel.text.toString(),
//                )
//
//                if(pesticideParRav.value?.length?:0 > 0){
//                    pesticideListSParcelle?.forEach {
//                        if (it.value?.uppercase() == pesticideParRav.value?.uppercase()) {
//                            ToastUtils.showShort("Cet autre insecte est déja ajouté")
//
//                            return@setOnClickListener
//                        }
//                    }
//
//                    pesticideListSParcelle?.add(pesticideParRav)
//                    pesticideSParcelleAdapter?.notifyDataSetChanged()
//
//                    selectIntrantNomSParcell.setSelection(0)
//                    selectIntrantContenantSParcell.setSelection(0)
//                    selectIntrantUniteSParcell.setSelection(0)
//                    editQuantitIntrantSParcel.text?.clear()
//                    editFrequencIntrantSParcel.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//    }

//    private fun setMaladieSParcelleRV( libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val maladieListSParcelle = mutableListOf<OmbrageVarieteModel>()
//        var countN = 0
//        libeleList.forEach {
//            maladieListSParcelle.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
//            countN++
//        }
//        val maladieSParcelleAdapter = OmbrageAdapter(maladieListSParcelle, "Appelation", "Son état")
//        try {
//            recyclerMaladieListSuiviParcel.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerMaladieListSuiviParcel.adapter = maladieSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddMaladieListSuiviParcel.setOnClickListener {
//            try {
//                if (selectMaladieNomSParcell.isSpinnerEmpty() || selectEtatMaladieSParcell.isSpinnerEmpty()
//                ) {
//                    Commons.showMessage("Renseignez des données, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val maladieParRav = OmbrageVarieteModel(
//                    0,
//                    selectMaladieNomSParcell.getSpinnerContent(),
//                    selectEtatMaladieSParcell.getSpinnerContent()
//                )
//
//                if(maladieParRav.variete?.length?:0 > 0){
//                    maladieListSParcelle?.forEach {
//                        if (it.variete?.uppercase() == maladieParRav.variete?.uppercase() && it.nombre == maladieParRav.nombre) {
//                            ToastUtils.showShort("Cet autre insecte est déja ajouté")
//
//                            return@setOnClickListener
//                        }
//                    }
//
//                    maladieListSParcelle?.add(maladieParRav)
//                    maladieSParcelleAdapter?.notifyDataSetChanged()
//
//                    selectMaladieNomSParcell.setSelection(0)
//                    selectEtatMaladieSParcell.setSelection(0)
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//    }

    private fun setAllSelection() {

        setOtherListener()

        setupSectionSelection()

        //setupVarieteArbrMultiSelection()

//        Commons.setListenerForSpinner(this,
//            "Avez-vous traité votre parcelle ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectAtuTraiteSParcell,
//            itemChanged = arrayListOf(Pair(1, "Oui")),
//            listIem = resources.getStringArray(R.array.YesOrNo)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerPesticideListSParcel.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            "Avez-vous rencontrer des animaux ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAnimauRencontSParcell,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearAnimauxContainerSuiviParcelle.visibility = visibility
                }
            })

//        Commons.setListenerForSpinner(this,
//            "Avez-vous constaté cette maladie ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectMaladieConstSParcell,
//            itemChanged = arrayListOf(Pair(1, "Oui")),
//            listIem = resources.getStringArray(R.array.YesOrNo)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    if(visibility == View.VISIBLE)  setupEtatMaladieSelector(resources.getStringArray(R.array.fullyPoor1))
//                    else setupEtatMaladieSelector(resources.getStringArray(R.array.fullyPoor2))
//                }
//            })

//        Commons.setListenerForSpinner(this,
//            "Bénéficies tu d'arbres agro-forestiers ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectAbrAgroForesSuiviParcel,
//            itemChanged = arrayListOf(Pair(1, "Oui")),
//            listIem = resources.getStringArray(R.array.YesOrNo)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerItemRecusSuiviParcel.visibility = visibility
//                    containerVarieteArbrSuiviParcel.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            "Y'a t'il des insectes parasites ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Avez-vous observé d'autres insectes ou ravageur qui n'apparaissent pas dans la liste précédente ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAutrInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearAutrInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

//        Commons.setListenerForSpinner(this,
//            "Choix des insectes !","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectListInsecteSuivParce,
//            itemChanged = arrayListOf(Pair(1, "Autre")),
//            listIem = resources.getStringArray(R.array.listeInsectes)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    linearPresenceInsecteRavageurOfSuiviParcelle.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            "Présence d'autres types d’insecte ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectPresencInsectSuiviParcel,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerInsectAmisSuiviParcelle.visibility = visibility
                }
            })

    }

//    private fun setupEtatMaladieSelector(stringArray: Array<String>, currentVal: String? = null) {
//        Commons.setListenerForSpinner(this,
//            "Choix de l'état !","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectEtatMaladieSParcell,
//            currentVal = currentVal,
//            listIem = stringArray
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })
//    }

//    fun setupVarieteArbrMultiSelection(currentList : MutableList<String> = mutableListOf()) {
//        val varieteArbreList = AssetFileHelper.getListDataFromAsset(25, this) as MutableList<CommonData>
//        var listSelectVarieteArbrePosList = mutableListOf<Int>()
//        listSelectVarieteArbreList = mutableListOf<String>()
//
//        var indItem = 0
//        (varieteArbreList)?.forEach {
//            if(currentList.size > 0){ if(currentList.contains(it.nom)) listSelectVarieteArbrePosList.add(indItem) }
//            indItem++
//        }
//
//        selectVarAbrOmbSuivParcel.setTitle("Quelle variété d’arbre ombrage souhaiterais-tu avoir ?")
//        selectVarAbrOmbSuivParcel.setItems(varieteArbreList.map { it.nom })
//        //multiSelectSpinner.hasNoneOption(true)
//        selectVarAbrOmbSuivParcel.setSelection(listSelectVarieteArbrePosList.toIntArray())
//        selectVarAbrOmbSuivParcel.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
//            override fun selectedIndices(indices: MutableList<Int>?) {
//                listSelectVarieteArbrePosList.clear()
//                listSelectVarieteArbrePosList.addAll(indices?.toMutableList() ?: mutableListOf())
//            }
//
//            override fun selectedStrings(strings: MutableList<String>?) {
//                listSelectVarieteArbreList.clear()
//                listSelectVarieteArbreList.addAll(strings?.toMutableList() ?: arrayListOf())
//            }
//
//        })
//    }

//    fun setVarieteArbrParcelleRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val varieteArbrListSParcelle = mutableListOf<OmbrageVarieteModel>()
//        var countN = 0
//        libeleList.forEach {
//            varieteArbrListSParcelle.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
//            countN++
//        }
//        val varieteArbrSParcelleAdapter = OmbrageAdapter(varieteArbrListSParcelle)
//
//
//        try {
//            recyclerVarieteArbrListSuiviParcel.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerVarieteArbrListSuiviParcel.adapter = varieteArbrSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddVarieteArbrSuiviParcel.setOnClickListener {
//            try {
//                if (selectVarieteArbrSuiviParcel.selectedItem.toString()
//                        .isEmpty() || editQtVarieteArbrSuiviParcel.text.toString().isEmpty()
//                ) {
//                    Commons.showMessage("Renseignez des données sur la variété, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val varieteArbre = OmbrageVarieteModel(
//                    0,
//                    selectVarieteArbrSuiviParcel.selectedItem.toString(),
//                    editQtVarieteArbrSuiviParcel.text.toString().trim()
//                )
//
//                if(varieteArbre.variete?.length?:0 > 0){
//                    varieteArbrListSParcelle?.forEach {
//                        if (it.variete?.uppercase() == varieteArbre.variete?.uppercase() && it.nombre == varieteArbre.nombre) {
//                            ToastUtils.showShort("Cette variété est deja ajoutée")
//                            return@setOnClickListener
//                        }
//                    }
//
//                    varieteArbrListSParcelle?.add(varieteArbre)
//                    varieteArbrSParcelleAdapter?.notifyDataSetChanged()
//
//                    editQtVarieteArbrSuiviParcel.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//    }

//    fun setIntrantSParcelleRV() {
//        val intrantListSParcelle = mutableListOf<OmbrageVarieteModel>()
//        val intrantSParcelleAdapter = OmbrageAdapter(intrantListSParcelle)
//        try {
//            recyclerIntrantListSuiviParcel.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerIntrantListSuiviParcel.adapter = intrantSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddIntrantListSuiviParcel.setOnClickListener {
//            try {
//                if (selectIntrantSuiviParcel.selectedItem.toString()
//                        .isEmpty() || editQtIntrantSuiviParcel.text.toString().isEmpty()
//                ) {
//                    Commons.showMessage("Renseignez des données sur l'intrant, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val intrant = OmbrageVarieteModel(
//                    0,
//                    selectIntrantSuiviParcel.selectedItem.toString(),
//                    editQtIntrantSuiviParcel.text.toString().trim()
//                )
//
//                if(intrant.variete?.length?:0 > 0){
//                    intrantListSParcelle?.forEach {
//                        if (it.variete?.uppercase() == intrant.variete?.uppercase() && it.nombre == intrant.nombre) {
//                            ToastUtils.showShort("Cet intrant est déja ajoutée")
//                            return@setOnClickListener
//                        }
//                    }
//
//                    intrantListSParcelle?.add(intrant)
//                    intrantSParcelleAdapter?.notifyDataSetChanged()
//
//                    selectIntrantSuiviParcel.setSelection(0)
//                    editQtIntrantSuiviParcel.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//    }

//    fun setPestAnDernSParcelleRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val pesticideListSParcelle = mutableListOf<AdapterItemModel>()
//        var countN = 0
////        libeleList.forEach {
////            pesticideListSParcelle.add(AdapterItemModel(0, it, valueList.get(countN)))
////            countN++
////        }
//        val pesticideSParcelleAdapter = MultipleItemAdapter(pesticideListSParcelle, )
//        try {
//            recyclerPestListSuiviParcel.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerPestListSuiviParcel.adapter = pesticideSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddPestListSuiviParcel.setOnClickListener {
//            try {
//                if (selectPestNomSParcell.isSpinnerEmpty()
//                    || selectPestContenantSParcell.isSpinnerEmpty()
//                    || selectPestUniteSParcell.isSpinnerEmpty()
//                    || editQuantitPestSParcel.text.toString().isNullOrBlank()
//                    || editFrequencPestSParcel.text.toString().isNullOrBlank()
//                ) {
//                    Commons.showMessage("Renseignez des données, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val pesticideParRav = AdapterItemModel(
//                    0,
//                    selectPestNomSParcell.getSpinnerContent(),
//                    selectPestContenantSParcell.getSpinnerContent(),
//                    selectPestUniteSParcell.getSpinnerContent(),
//                    editQuantitPestSParcel.text.toString(),
//                    editFrequencPestSParcel.text.toString(),
//                )
//
//                if(pesticideParRav.value?.length?:0 > 0){
//                    pesticideListSParcelle?.forEach {
//                        if (it.value?.uppercase() == pesticideParRav.value?.uppercase()) {
//                            ToastUtils.showShort("Cet autre insecte est déja ajouté")
//
//                            return@setOnClickListener
//                        }
//                    }
//
//                    pesticideListSParcelle?.add(pesticideParRav)
//                    pesticideSParcelleAdapter?.notifyDataSetChanged()
//
//                    selectPestNomSParcell.setSelection(0)
//                    selectPestContenantSParcell.setSelection(0)
//                    selectPestUniteSParcell.setSelection(0)
//                    editQuantitPestSParcel.text?.clear()
//                    editFrequencPestSParcel.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//    }

//    fun setIntrantAnDernSParcelleRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val intranAnDernListSParcelle = mutableListOf<AdapterItemModel>()
//        var countN = 0
////        libeleList.forEach {
////            pesticideListSParcelle.add(AdapterItemModel(0, it, valueList.get(countN)))
////            countN++
////        }
//        val intranAnDernSParcelleAdapter = MultipleItemAdapter(intranAnDernListSParcelle, )
//        try {
//            recyclerIntantAnDerListSuiviParcel.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerIntantAnDerListSuiviParcel.adapter = intranAnDernSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddIntantAnDerListSuiviParcel.setOnClickListener {
//            try {
//                if (selectIntantAnDerSParcell.isSpinnerEmpty()
//                    || selectIntantAnDerContenantSParcell.isSpinnerEmpty()
//                    || selectIntantAnDerUniteSParcell.isSpinnerEmpty()
//                    || editIntantAnDerPestSParcel.text.toString().isNullOrBlank()
//                    || editFrequencIntantAnDerSParcel.text.toString().isNullOrBlank()
//                ) {
//                    Commons.showMessage("Renseignez des données, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val pesticideParRav = AdapterItemModel(
//                    0,
//                    selectIntantAnDerSParcell.getSpinnerContent(),
//                    selectIntantAnDerContenantSParcell.getSpinnerContent(),
//                    selectIntantAnDerUniteSParcell.getSpinnerContent(),
//                    editIntantAnDerPestSParcel.text.toString(),
//                    editFrequencIntantAnDerSParcel.text.toString(),
//                )
//
//                if(pesticideParRav.value?.length?:0 > 0){
//                    intranAnDernListSParcelle?.forEach {
//                        if (it.value?.uppercase() == pesticideParRav.value?.uppercase()) {
//                            ToastUtils.showShort("Cet élement est déja ajouté")
//
//                            return@setOnClickListener
//                        }
//                    }
//
//                    intranAnDernListSParcelle?.add(pesticideParRav)
//                    intranAnDernSParcelleAdapter?.notifyDataSetChanged()
//
//                    selectIntantAnDerSParcell.setSelection(0)
//                    selectIntantAnDerContenantSParcell.setSelection(0)
//                    selectIntantAnDerUniteSParcell.setSelection(0)
//                    editIntantAnDerPestSParcel.text?.clear()
//                    editFrequencIntantAnDerSParcel.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//    }

//    fun setInsParasSParcelleRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val insParasListSParcelle = mutableListOf<OmbrageVarieteModel>()
//        var countN = 0
//        libeleList.forEach {
//            insParasListSParcelle.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
//            countN++
//        }
//        val insParasSParcelleAdapter = OmbrageAdapter(insParasListSParcelle)
//        try {
//            recyclerInsecteOfSuiviParcelle.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerInsecteOfSuiviParcelle.adapter = insParasSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddInsecteRavagParaQuantiteOfSuiviParcelle.setOnClickListener {
//            try {
//                if (editInsecteNomOfSuiviParcelle.text.toString()
//                        .isEmpty() || selectInsecteQuantiteOfSuiviParcelle.selectedItem.toString().isNullOrBlank()
//                ) {
//                    Commons.showMessage("Renseignez des données sur l'intrant, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val insecteParRav = OmbrageVarieteModel(
//                    0,
//                    editInsecteNomOfSuiviParcelle.text.toString().trim(),
//                    selectInsecteQuantiteOfSuiviParcelle.selectedItem.toString().trim()
//                )
//
//                if(insecteParRav.variete?.length?:0 > 0){
//                    insParasListSParcelle?.forEach {
//                        if (it.variete?.uppercase() == insecteParRav.variete?.uppercase() && it.nombre == insecteParRav.nombre) {
//                            ToastUtils.showShort("Cet insecte est déja ajouté")
//                            return@setOnClickListener
//                        }
//                    }
//
//                    insParasListSParcelle?.add(insecteParRav)
//                    insParasSParcelleAdapter?.notifyDataSetChanged()
//
//                    selectInsecteQuantiteOfSuiviParcelle.setSelection(0)
//                    editInsecteNomOfSuiviParcelle.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//    }

//    fun setAutreInsParasSParcelleRV(libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
//        val autrInsParasListSParcelle = mutableListOf<OmbrageVarieteModel>()
//        var countN = 0
//        libeleList.forEach {
//            autrInsParasListSParcelle.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
//            countN++
//        }
//        val autrInsParasSParcelleAdapter = OmbrageAdapter(autrInsParasListSParcelle)
//        try {
//            recyclerInsecteAmisSuiviParcelle.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//            recyclerInsecteAmisSuiviParcelle.adapter = autrInsParasSParcelleAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//            FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//
//        clickAddInsectAmisSuiviParcelle.setOnClickListener {
//            try {
//                if (editInsecteNomAmisParcelle.text.toString()
//                        .isEmpty() || selectInsecteAmisQuantiteSuiviParcelle.selectedItem.toString().isNullOrBlank()
//                ) {
//                    Commons.showMessage("Renseignez des données sur l'autre insecte, svp !", this, callback = {})
//                    return@setOnClickListener
//                }
//
//                val autreInsecteParRav = OmbrageVarieteModel(
//                    0,
//                    editInsecteNomAmisParcelle.text.toString().trim(),
//                    selectInsecteAmisQuantiteSuiviParcelle.selectedItem.toString().trim()
//                )
//
//                if(autreInsecteParRav.variete?.length?:0 > 0){
//                    autrInsParasListSParcelle?.forEach {
//                        if (it.variete?.uppercase() == autreInsecteParRav.variete?.uppercase() && it.nombre == autreInsecteParRav.nombre) {
//                            ToastUtils.showShort("Cet autre insecte est déja ajouté")
//
//                            return@setOnClickListener
//                        }
//                    }
//
//                    autrInsParasListSParcelle?.add(autreInsecteParRav)
//                    autrInsParasSParcelleAdapter?.notifyDataSetChanged()
//
//                    selectInsecteAmisQuantiteSuiviParcelle.setSelection(0)
//                    editInsecteNomAmisParcelle.text?.clear()
//                }
//                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }
//
//    }

    fun setAnimauSParcelleRV(libeleList:MutableList<String> = arrayListOf()) {
        val animauListSParcelle = mutableListOf<CommonData>()
        var countN = 0
        libeleList.forEach {
            animauListSParcelle.add(CommonData(0, it))
            countN++
        }

        val animauSParcelleAdapter = OnlyFieldAdapter(animauListSParcelle)
        try {
            recyclerAnimauxSuiviParcelle.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerAnimauxSuiviParcelle.adapter = animauSParcelleAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        clickSaveAnimauxSuiviParcelle.setOnClickListener {
            try {
                if (editAnimalSuiviParcelle.text.toString()
                        .isEmpty()
                ) {
                    Commons.showMessage("Renseignez des données sur l'animal, svp !", this, callback = {})
                    return@setOnClickListener
                }

                val animaux = CommonData(
                    0,
                    editAnimalSuiviParcelle.text.toString().trim(),
                )

                if(animaux.nom?.length?:0 > 0){
                    animauListSParcelle?.forEach {
                        if (it.nom?.uppercase() == animaux.nom?.uppercase()) {
                            ToastUtils.showShort("Cet animal est déja ajouté")

                            return@setOnClickListener
                        }
                    }

                    animauListSParcelle?.add(animaux)
                    animauSParcelleAdapter?.notifyDataSetChanged()

                    editAnimalSuiviParcelle.text?.clear()
                }
                //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
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
            "Choix de la section !",
            "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = libItem ,
            spinner = selectSectionSParcelle,
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
            spinner = selectLocaliteSParcelle,
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
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix du producteur !",
            "La liste des producteurs semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurSParcelle,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    producteurCommon.id = producteur.id!!

                    setupParcelleSelection(producteurCommon.id.toString(), currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupParcelleSelection(producteurId: String?, currVal3: String? = null) {
        var parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
            ?.getParcellesProducteur(producteurId = producteurId.toString(), agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        LogUtils.json(parcellesList)
        var libItem: String? = null
        currVal3?.let { idc ->
            parcellesList?.forEach {
                if (it.id == idc.toInt()) libItem = "(${it.anneeCreation}) ${it.superficieConcerne} ha"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la parcelle !",
            "La liste des parcelles semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectParcelleSParcelle,
            listIem = parcellesList?.map { "(${it.anneeCreation}) ${it.superficieConcerne} ha" }
                ?.toList() ?: listOf(),
            onChanged = {

                parcellesList?.let { list ->
                    var parcelle = list.get(it)
                    parcelleCommon.nom = "(${parcelle.anneeCreation}) ${parcelle.superficieConcerne} ha"
                    parcelleCommon.id = parcelle.id!!

                    //setupParcelleSelection(parcelleCommon.id, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })
    }


}
