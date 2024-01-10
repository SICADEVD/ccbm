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
import ci.projccb.mobile.repositories.datas.InsectesParasitesData
import ci.projccb.mobile.repositories.datas.PesticidesAnneDerniereModel
import ci.projccb.mobile.repositories.datas.PresenceAutreInsecteData
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
import kotlinx.android.synthetic.main.activity_ssrt_clms.selectLequelTravEffectSSrte
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
    var arbresList: MutableList<ArbreModel>? = null
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


//    fun addArbreAgro(arbreModel: OmbrageVarieteModel) {
//        try {
//            if (arbreModel.variete?.length == 0) return
//
//            arbresList?.forEach {
//                if (it.variete?.uppercase() == arbreModel.variete?.uppercase() && it.nombre == arbreModel.nombre) {
//                    ToastUtils.showShort("Cet arbre est deja ajouté")
//                    return
//                }
//            }
//
//            arbresList?.add(arbreModel)
//            arbreAdapter?.notifyDataSetChanged()
//
//            clearArbresFields()
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//    }


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


//    fun setArbreParcelle() {
//        try {
//            arbresList = mutableListOf()
//            arbreAdapter = OmbrageAdapter(arbresList)
////            recyclerArbreAgroSuiviParcelle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
////            recyclerArbreAgroSuiviParcelle.adapter = arbreAdapter
//        } catch (ex: Exception) {
//            LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//        }
//    }


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

        val itemModelOb = getSuiviParcelleObjet()

        if(itemModelOb == null) return

        val SParcelle = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localiteId = localiteCommon.id.toString()
                producteursId = producteurCommon.id.toString()
                parcelle_id = parcelleCommon.id.toString()

                pesticidesAnneDerniereStr = GsonUtils.toJson((recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                intrantsAnneDerniereStr = GsonUtils.toJson((recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })

                insectesParasitesStr = GsonUtils.toJson((recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    InsectesParasitesData(
                        nom = it.variete,
                        nombreinsectesParasites = it.nombre
                    )
                })
                presenceAutreInsecteStr = GsonUtils.toJson((recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    PresenceAutreInsecteData(
                        autreInsecteNom = it.variete,
                        nombreAutreInsectesParasites = it.nombre
                    )
                })

                traitementStr = GsonUtils.toJson((recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                arbreStr = GsonUtils.toJson(arbresList?.filter { (selectArbreSParcelle.selectedStrings).contains(it.nom) }?.map { it.id.toString() })
                arbreItemStr = GsonUtils.toJson((recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    LogUtils.d(it.variete.toString())
                    val idd = arbresList?.filter { item -> item.nom.toString().trim().contains(it.variete.toString()) }?.first()?.id.toString()
                    ArbreData(
                        arbre = idd,
                        nombre = it.nombre
                    )
                })
                insectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                nombreinsectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                animauxRencontresStringify = GsonUtils.toJson((recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
//            this.add(Pair("Arbre d'ombrage", (recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
//            this.add(Pair("Insecte parasite", (recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Pesticides utilisés l'an dernier", (recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Nom: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))
            this.add(Pair("Intrants utilisés l'an dernier", (recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Nom: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))

            this.add(Pair("Insecte parasites ou ravageurs", (recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Autre insecte parasites ou ravageurs", (recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Traitements", (recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Nom: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))
            this.add(Pair("As tu bénéficié d'arbres agro-forestiers", (recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "Nom: ${it.variete}| Qte: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Insecte amis", (recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "Nom: ${it.variete}| Qte: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Animaux rencontrés", (recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { "${it.nom}\n" }.toModifString() ))
        }.map { MapEntry(it.first, it.second) }

        Commons.printModelValue(SParcelle as Object, mapEntries)

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

        if(isMissing && (isMissingDial || isMissingDial2) ){
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

        val itemModelOb = getSuiviParcelleObjet(false)

        if(itemModelOb == null) return

        val SParcelleDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localiteId = localiteCommon.id.toString()
                producteursId = producteurCommon.id.toString()
                parcelle_id = parcelleCommon.id.toString()

                pesticidesAnneDerniereStr = GsonUtils.toJson((recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                intrantsAnneDerniereStr = GsonUtils.toJson((recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })

                insectesParasitesStr = GsonUtils.toJson((recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    InsectesParasitesData(
                        nom = it.variete,
                        nombreinsectesParasites = it.nombre
                    )
                })
                presenceAutreInsecteStr = GsonUtils.toJson((recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    PresenceAutreInsecteData(
                        autreInsecteNom = it.variete,
                        nombreAutreInsectesParasites = it.nombre
                    )
                })

                traitementStr = GsonUtils.toJson((recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                arbreStr = GsonUtils.toJson(arbresList?.filter { (selectArbreSParcelle.selectedStrings).contains(it.nom) }?.map { it.id.toString() })
                arbreItemStr = GsonUtils.toJson((recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    LogUtils.d(it.variete.toString())
                    val idd = arbresList?.filter { item -> item.nom.toString().trim().contains(it.variete.toString()) }?.first()?.id.toString()
                    ArbreData(
                        arbre = idd,
                        nombre = it.nombre
                    )
                })
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

        setOtherListener()

        var arbreSelected = (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.arbreStr, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }).toMutableList()
        var currentStr: MutableList<String?>? = (arbresList?.filter { arbreSelected.contains(it.id.toString()) })?.map { it.nom }?.toMutableList()
        Commons.setupItemMultiSelection(this, selectArbreSParcelle, "Quelle variété d’arbre ombrage souhaiterais-tu avoir ?", arbresList?.map { CommonData(0, it.nom) }?.toMutableList()?: mutableListOf() ,
            currentList = currentStr?.map { it?:"" }?.toMutableList()?: mutableListOf()
        ){

        }

        Commons.setListenerForSpinner(this,
            "As-tu Bénéficié D’arbres Agro-forestiers ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAgroForesterieSParcelle,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerArbreAgroSParcelle.visibility = visibility
                }
            })

        (recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).setOmbragesList(
            (GsonUtils.fromJson<MutableList<ArbreData>>(suiviParcelleDrafted.arbreItemStr, object : TypeToken<MutableList<ArbreData>>() {}.type)).map {
                val nomm = arbresList?.filter { item -> item.id.toString().equals(it.arbre) }?.first()?.nom.toString()
                OmbrageVarieteModel(
                    uid = 0,
                    variete = nomm,
                    nombre = it.nombre
                )
            }.toMutableList()
        )

        (recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).setOmbragesList(
            (GsonUtils.fromJson<MutableList<InsectesParasitesData>>(suiviParcelleDrafted.insectesParasitesStr, object : TypeToken<MutableList<InsectesParasitesData>>() {}.type)).map {
                OmbrageVarieteModel(
                    uid = 0,
                    variete = it.nom,
                    nombre = it.nombreinsectesParasites
                )
            }.toMutableList()
        )

        (recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).setOmbragesList(
            (GsonUtils.fromJson<MutableList<PresenceAutreInsecteData>>(suiviParcelleDrafted.presenceAutreInsecteStr, object : TypeToken<MutableList<PresenceAutreInsecteData>>() {}.type)).map {
                OmbrageVarieteModel(
                    uid = 0,
                    variete = it.autreInsecteNom,
                    nombre = it.nombreAutreInsectesParasites
                )
            }.toMutableList()
        )

        (recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).setDataToRvItem(
            (GsonUtils.fromJson<MutableList<PesticidesAnneDerniereModel>>(suiviParcelleDrafted.pesticidesAnneDerniereStr, object : TypeToken<MutableList<PesticidesAnneDerniereModel>>() {}.type)).map {
                AdapterItemModel(
                    id = 0,
                    value = it.nom,
                    value1 = it.contenant,
                    value2 = it.unite,
                    value3 = it.quantite,
                    value4 = it.frequence
                )
            }.toMutableList()
        )

        (recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).setDataToRvItem(
            (GsonUtils.fromJson<MutableList<PesticidesAnneDerniereModel>>(suiviParcelleDrafted.intrantsAnneDerniereStr, object : TypeToken<MutableList<PesticidesAnneDerniereModel>>() {}.type)).map {
                AdapterItemModel(
                    id = 0,
                    value = it.nom,
                    value1 = it.contenant,
                    value2 = it.unite,
                    value3 = it.quantite,
                    value4 = it.frequence
                )
            }.toMutableList()
        )

        (recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).setDataToRvItem(
            (GsonUtils.fromJson<MutableList<PesticidesAnneDerniereModel>>(suiviParcelleDrafted.traitementStr, object : TypeToken<MutableList<PesticidesAnneDerniereModel>>() {}.type)).map {
                AdapterItemModel(
                    id = 0,
                    value = it.nom,
                    value1 = it.contenant,
                    value2 = it.unite,
                    value3 = it.quantite,
                    value4 = it.frequence
                )
            }.toMutableList()
        )

        setAnimauSParcelleRV(
            (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.animauxRencontresStringify, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }.toMutableList()),
        )

        setupSectionSelection(suiviParcelleDrafted.section,
            suiviParcelleDrafted.localiteId,
            suiviParcelleDrafted.producteursId,
            suiviParcelleDrafted.parcelle_id)

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

        Commons.setListenerForSpinner(this,
            "Avez-vous observé d'autres insectes ou ravageur qui n'apparaissent pas dans la liste précédente ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAutrInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            currentVal = suiviParcelleDrafted.autreInsecte,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearAutrInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Avez-vous traiter votre parcelle ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectTraitInsecteParOuRavSuivi,
            currentVal = suiviParcelleDrafted.traiterParcelle,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
//                    qdf qf
//                    containerListInsectSuivParce.visibility = visibility
                    containerTraitInsecteParOuRavListSParcel.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Avez-vous rencontrer des animaux ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAnimauRencontSParcell,
            currentVal = suiviParcelleDrafted.animauxRencontrer,
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


        passSetupSuiviParcelleModel(suiviParcelleDrafted)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_parcelle)

        suiviParcelleDao = CcbRoomDatabase.getDatabase(this)?.suiviParcelleDao()

        clickCloseBtn.setOnClickListener {
            finish()
        }

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

        Commons.setEditAndSpinnerRV(this, recyclerArbrAgroSuiviParcelle, clickAddArbrAgroSuiviParcelle, editNombrAgroSParcelle, selectArbrAgroParcelle, "Nom d'arbre", "Quantité", isInverted = true)

        Commons.setFiveItremRV(this, recyclerPestListSuiviParcel, clickAddPestListSuiviParcel, selectPestNomSParcell,selectPestContenantSParcell,selectPestUniteSParcell,editQuantitPestSParcel,editFrequencPestSParcel, libeleList = mutableListOf<String>("Nom", "Contenant", "Unité", "Quantité", "Fréquence"))
        Commons.setFiveItremRV(this, recyclerIntantAnDerListSuiviParcel, clickAddIntantAnDerListSuiviParcel, selectIntantAnDerSParcell,selectIntantAnDerContenantSParcell,selectIntantAnDerUniteSParcell,editIntantAnDerPestSParcel,editFrequencIntantAnDerSParcel, libeleList = mutableListOf<String>("Nom", "Contenant", "Unité", "Quantité", "Fréquence"))
        Commons.setFiveItremRV(this, recyclerTraitInsecteParOuRavListSuiviParcel, clickAddTraitInsecteParOuRavListSuiviParcel, selectTraitInsecteParOuRavNomSParcell,selectTraitInsecteParOuRavContenantSParcell,selectTraitInsecteParOuRavUniteSParcell,editTraitInsecteParOuRavQtSParcel,editTraitInsecteParOuRavFrequSParcel, libeleList = mutableListOf<String>("Nom", "Contenant", "Unité", "Quantité", "Fréquence"))

        arbresList = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll() ?: mutableListOf()

        selectArbrAgroParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arbresList?.map { "${ it.nom }" }?.toList()?:listOf())

        setAnimauSParcelleRV()

    }

    private fun setAllSelection() {

        setOtherListener()

        setupSectionSelection()

        Commons.setupItemMultiSelection(this, selectArbreSParcelle, "Quelle variété d’arbre ombrage souhaiterais-tu avoir ?", arbresList?.map { CommonData(0, it.nom) }?.toMutableList()?: mutableListOf() ){

        }

        Commons.setListenerForSpinner(this,
            "As-tu bénéficié d’arbres agro-forestiers ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectAgroForesterieSParcelle,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerArbreAgroSParcelle.visibility = visibility
                }
            })

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

        Commons.setListenerForSpinner(this,
            "Avez-vous traiter votre parcelle ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectTraitInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
//                    qdf qf
//                    containerListInsectSuivParce.visibility = visibility
                    containerTraitInsecteParOuRavListSParcel.visibility = visibility
                }
            })

    }

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
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid
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
                if (it.id == idc.toInt()) libItem = "${it.codeParc}"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la parcelle !",
            "La liste des parcelles semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectParcelleSParcelle,
            listIem = parcellesList?.map { "${it.codeParc}" }
                ?.toList() ?: listOf(),
            onChanged = {

                parcellesList?.let { list ->
                    var parcelle = list.get(it)
                    parcelleCommon.nom = "${parcelle.codeParc}"
                    parcelleCommon.id = parcelle.id!!

                    //setupParcelleSelection(parcelleCommon.id, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })
    }


}
