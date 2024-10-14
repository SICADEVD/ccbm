package ci.progbandama.mobile.activities.forms

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.progbandama.mobile.adapters.AnimalAdapter
import ci.progbandama.mobile.adapters.InsecteAdapter
import ci.progbandama.mobile.adapters.MultipleItemAdapter
import ci.progbandama.mobile.adapters.OmbrageAdapter
import ci.progbandama.mobile.adapters.OnlyFieldAdapter
import ci.progbandama.mobile.databinding.ActivitySuiviApplicationBinding
import ci.progbandama.mobile.databinding.ActivitySuiviParcelleBinding
import ci.progbandama.mobile.models.*
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.*
import ci.progbandama.mobile.repositories.datas.ArbreData
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.repositories.datas.InsectesParasitesData
import ci.progbandama.mobile.repositories.datas.PesticidesAnneDerniereModel
import ci.progbandama.mobile.repositories.datas.PresenceAutreInsecteData
import ci.progbandama.mobile.tools.AssetFileHelper
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.configDate
import ci.progbandama.mobile.tools.Commons.Companion.toModifString
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
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
                    ToastUtils.showShort(getString(R.string.cette_vari_t_est_deja_ajout_e))
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
                    ToastUtils.showShort(getString(R.string.cet_animal_est_deja_ajout))
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
                    ToastUtils.showShort(getString(R.string.cet_insecte_est_deja_ajout))
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
        binding.editAnimalSuiviParcelle.text = null
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
            binding.recyclerAnimauxSuiviParcelle.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerAnimauxSuiviParcelle.adapter = animalAdapter
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
            producteurDao = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            producteursList = producteurDao?.getProducteursByLocalite(localite = localite) ?: mutableListOf()

            if (producteursList?.size == 0) {
                Commons.showMessage(
                    getString(R.string.la_liste_des_producteurs_de_cette_localit_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                    this,
                    finished = false,
                    callback = {},
                    positive = getString(R.string.compris),
                    deconnec = false,
                    showNo = false

                )
                return
            }

            val producteursDatas: MutableList<CommonData> = mutableListOf()

            producteursList?.map {
                if(it.isSynced){
                    CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
                }else CommonData(id = it.uid, nom = "${it.nom} ${it.prenoms}")
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
                Commons.showMessage(getString(R.string.aucun_producteur_enregistr), this, callback = {})
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun setupCampagneSelection() {
        try {
            campagneDao = ProgBandRoomDatabase.getDatabase(applicationContext)?.campagneDao()
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
//                        if (courEauYesNo == getString(R.string.oui)) {
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
            //courEauxDao = ProgBandRoomDatabase.getDatabase(applicationContext)?.courEauDoa()
            courEauxList = AssetFileHelper.getListDataFromAsset(0, this@SuiviParcelleActivity) as MutableList<CourEauModel>?
//                courEauxDao?.getAll(
//                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
//            )

            arrayCourEau.add(getString(R.string.choisir_la_source))

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
//                            getString(R.string.oui) -> {
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
            localitesList = ProgBandRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

            if (localitesList?.size == 0) {
                Commons.showMessage(
                    getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                    this,
                    finished = true,
                    callback = {},
                    positive = getString(R.string.compris),
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
            binding.selectAraigneeSuivi.onItemSelectedListener =
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
        binding.selectVerDeTerreSuivi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                presenceVer = resources.getStringArray(R.array.fullyPoor)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupManteSelection() {
        binding.selectManteReligieuseSuivi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                pesticidesAnneDerniereStr = GsonUtils.toJson((binding.recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                intrantsAnneDerniereStr = GsonUtils.toJson((binding.recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })

                insectesParasitesStr = GsonUtils.toJson((binding.recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    InsectesParasitesData(
                        nom = it.variete,
                        nombreinsectesParasites = it.nombre
                    )
                })
                presenceAutreInsecteStr = GsonUtils.toJson((binding.recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    PresenceAutreInsecteData(
                        autreInsecteNom = it.variete,
                        nombreAutreInsectesParasites = it.nombre
                    )
                })

                traitementStr = GsonUtils.toJson((binding.recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                arbreStr = GsonUtils.toJson(arbresList?.filter { (binding.selectArbreSParcelle.selectedStrings).contains(it.nom) }?.map { it.id.toString() })
                arbreItemStr = GsonUtils.toJson((binding.recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    LogUtils.d(it.variete.toString())
                    val arbre = ProgBandRoomDatabase.getDatabase(this@SuiviParcelleActivity)?.arbreDao()?.getByName(it.variete.toString())
                    ArbreData(
                        arbre = arbre?.id.toString()?:null,
                        nombre = it.nombre
                    )
                })
                insectesAmisStr = GsonUtils.toJson((binding.recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                nombreinsectesAmisStr = GsonUtils.toJson((binding.recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                animauxRencontresStringify = GsonUtils.toJson((binding.recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
//            this.add(Pair("Arbre d'ombrage", (recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
//            this.add(Pair("Insecte parasite", (recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.pesticides_utilis_s_l_an_dernier), (binding.recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Nom: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.intrants_utilis_s_l_an_dernier), (binding.recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Nom: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))

            this.add(Pair(getString(R.string.insecte_parasites_ou_ravageurs), (binding.recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.autre_insecte_parasites_ou_ravageurs), (binding.recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.traitements), (binding.recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map { "Nom: ${it.value}| Contenant: ${it.value1}| Unité: ${it.value2}| Qté: ${it.value3}| Fqe: ${it.value4}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.as_tu_b_n_fici_d_arbres_agro_forestiers), (binding.recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "Nom: ${it.variete}| Qte: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.insecte_amis), (binding.recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "Nom: ${it.variete}| Qte: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.animaux_rencontr_s), (binding.recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { "${it.nom}\n" }.toModifString() ))
        }?.map { MapEntry(it.first, it.second) }

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
        binding.editDesherbageManuelSuivi.text  = null
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
        binding.editDesherbageManuelSuivi.text    = null
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
        binding.selectAraigneeSuivi.setSelection(0)
        binding.selectVerDeTerreSuivi.setSelection(0)
        binding.selectManteReligieuseSuivi.setSelection(0)

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

                pesticidesAnneDerniereStr = GsonUtils.toJson((binding.recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                intrantsAnneDerniereStr = GsonUtils.toJson((binding.recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })

                insectesParasitesStr = GsonUtils.toJson((binding.recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    InsectesParasitesData(
                        nom = it.variete,
                        nombreinsectesParasites = it.nombre
                    )
                })
                presenceAutreInsecteStr = GsonUtils.toJson((binding.recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    PresenceAutreInsecteData(
                        autreInsecteNom = it.variete,
                        nombreAutreInsectesParasites = it.nombre
                    )
                })

                traitementStr = GsonUtils.toJson((binding.recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).getMultiItemAdded().map {
                    PesticidesAnneDerniereModel(
                        nom = it.value,
                        contenant = it.value1,
                        unite = it.value2,
                        quantite = it.value3,
                        frequence = it.value4
                    )
                })
                arbreStr = GsonUtils.toJson(arbresList?.filter { (binding.selectArbreSParcelle.selectedStrings).contains(it.nom) }?.map { it.id.toString() })
                arbreItemStr = GsonUtils.toJson((binding.recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map {
                    LogUtils.d(it.variete.toString())
//                    val idd = arbresList?.filter { item -> item.nom.toString().trim().contains(it.variete.toString()) }?.let{
//                        if(it.size > 0){
//                            it.first()?.id.toString()
//                        }else "0"
//                    }
                    val arbre = ProgBandRoomDatabase.getDatabase(this@SuiviParcelleActivity)?.arbreDao()?.getByName(it.variete.toString())
                    ArbreData(
                        arbre = arbre?.id.toString()?:null,
                        nombre = it.nombre
                    )
                })
                insectesAmisStr = GsonUtils.toJson((binding.recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
                nombreinsectesAmisStr = GsonUtils.toJson((binding.recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

                animauxRencontresStringify = GsonUtils.toJson((binding.recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
            }
        }

//        LogUtils.json(suiviParcelleDraft)

        Commons.showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(SParcelleDraft),
                        typeDraft = "suivi_parcelle",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                Commons.showMessage(
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
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        val suiviParcelleDrafted = ApiClient.gson.fromJson(draftedData.datas, SuiviParcelleModel::class.java)

        setOtherListener()

        var arbreSelected = (GsonUtils.fromJson<MutableList<String>>(suiviParcelleDrafted.arbreStr, object : TypeToken<MutableList<String>>() {}.type).map { "${it}" }).toMutableList()
        var currentStr: MutableList<String?>? = (arbresList?.filter { arbreSelected.contains(it.id.toString()) })?.map { it.nom }?.toMutableList()
        Commons.setupItemMultiSelection(this, binding.selectArbreSParcelle, "Quelle variété d’arbre ombrage souhaiterais-tu avoir ?", arbresList?.map { CommonData(0, it.nom) }?.toMutableList()?: mutableListOf() ,
            currentList = currentStr?.map { it?:"" }?.toMutableList()?: mutableListOf()
        ){

        }

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_b_n_fici_d_arbres_agro_forestiers),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectAgroForesterieSParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            currentVal = suiviParcelleDrafted.arbresAgroForestiersYesNo,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerArbreAgroSParcelle.visibility = visibility
                }
            })

        (binding.recyclerArbrAgroSuiviParcelle.adapter as OmbrageAdapter).setOmbragesList(
            (GsonUtils.fromJson<MutableList<ArbreData>>(suiviParcelleDrafted.arbreItemStr, object : TypeToken<MutableList<ArbreData>>() {}.type)).map {
                val nomm = arbresList?.filter { item -> item.id.toString().equals(it.arbre) }?.first()?.nom.toString()
                OmbrageVarieteModel(
                    uid = 0,
                    variete = nomm,
                    nombre = it.nombre
                )
            }.toMutableList()
        )

        if(!suiviParcelleDrafted.insectesAmisStr.isNullOrBlank()){
            val insectesAmisList = Commons.returnStringList(suiviParcelleDrafted.insectesAmisStr)
            val nombreinsectesAmisList = Commons.returnStringList(suiviParcelleDrafted.nombreinsectesAmisStr)
            insectesAmisList?.mapIndexed { index, s ->
                OmbrageVarieteModel(
                    uid = 0,
                    variete = s.toString(),
                    nombre = nombreinsectesAmisList?.get(index).toString()
                )
            }?.toMutableList()?.let {
                (binding.recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).setOmbragesList(
                    it
                )
            }
        }

        (binding.recyclerInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).setOmbragesList(
            (GsonUtils.fromJson<MutableList<InsectesParasitesData>>(suiviParcelleDrafted.insectesParasitesStr, object : TypeToken<MutableList<InsectesParasitesData>>() {}.type)).map {
                OmbrageVarieteModel(
                    uid = 0,
                    variete = it.nom,
                    nombre = it.nombreinsectesParasites
                )
            }.toMutableList()
        )

        (binding.recyclerAutreInsecteParOuRavSuiviParcelle.adapter as OmbrageAdapter).setOmbragesList(
            (GsonUtils.fromJson<MutableList<PresenceAutreInsecteData>>(suiviParcelleDrafted.presenceAutreInsecteStr, object : TypeToken<MutableList<PresenceAutreInsecteData>>() {}.type)).map {
                OmbrageVarieteModel(
                    uid = 0,
                    variete = it.autreInsecteNom,
                    nombre = it.nombreAutreInsectesParasites
                )
            }.toMutableList()
        )

        (binding.recyclerPestListSuiviParcel.adapter as MultipleItemAdapter).setDataToRvItem(
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

        (binding.recyclerIntantAnDerListSuiviParcel.adapter as MultipleItemAdapter).setDataToRvItem(
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

        (binding.recyclerTraitInsecteParOuRavListSuiviParcel.adapter as MultipleItemAdapter).setDataToRvItem(
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
            getString(R.string.fr_quence_activit_de_taille),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectActDTailleSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteTaille,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.fr_quence_activit_d_egourmandage),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectActDTailleSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteEgourmandage,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.fr_quence_activit_de_r_colte_sanitaire),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectActDRecolSanitSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteRecolteSanitaire,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Activité d’egourmandage dans la parcelle :",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectActDErgoSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteEgourmandage,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Activité de désherbage manuel dans la parcelle :",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectActDSerbSuiviParcel,
            currentVal = suiviParcelleDrafted.activiteDesherbageManuel,
            listIem = resources.getStringArray(R.array.lowMediumHigh)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })
//
        Commons.setListenerForSpinner(this,
            "Présence de pourriture brune :",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresencPourriBruneSuiviParcel,
            currentVal = suiviParcelleDrafted.presencePourritureBrune,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Présence de swollen shoot :",getString(R.string.la_liste_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresencShollenShotSuiviParcel,
            currentVal = suiviParcelleDrafted.presenceShooter,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_une_pr_sence_d_insectes_parasites_ou_ravageurs),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectInsecteParOuRavSuivi,
            currentVal = suiviParcelleDrafted.presenceInsectesParasites,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
//                    qdf qf
//                    containerListInsectSuivParce.visibility = visibility
                    binding.linearInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_observ_d_autres_insectes_ou_ravageur_qui_n_apparaissent_pas_dans_la_liste_pr_c_dente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectAutrInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            currentVal = suiviParcelleDrafted.autreInsecte,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.linearAutrInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_traiter_votre_parcelle),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectTraitInsecteParOuRavSuivi,
            currentVal = suiviParcelleDrafted.traiterParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
//                    qdf qf
//                    containerListInsectSuivParce.visibility = visibility
                    binding.containerTraitInsecteParOuRavListSParcel.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_rencontrer_des_animaux),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectAnimauRencontSParcell,
            currentVal = suiviParcelleDrafted.animauxRencontrer,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.linearAnimauxContainerSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.pr_sence_d_autres_types_d_insecte),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresencInsectSuiviParcel,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            currentVal = suiviParcelleDrafted.presenceAutreTypeInsecteAmi,
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerInsectAmisSuiviParcelle.visibility = visibility
                }
            })

//        Commons.setListenerForSpinner(this,
//            "Choix des insectes :",getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
            getString(R.string.y_a_t_il_une_pr_sence_de_fourmis_rouges),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectFourmisSuiviParcel,
            currentVal = suiviParcelleDrafted.presenceFourmisRouge,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_une_pr_sence_d_araign_es),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectAraigneeSuivi,
            currentVal = suiviParcelleDrafted.presenceAraignee,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_une_pr_sence_de_verre_de_terres),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectVerDeTerreSuivi,
            currentVal = suiviParcelleDrafted.presenceVerTerre,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_une_pr_sence_de_mentes_religieuses),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectManteReligieuseSuivi,
            currentVal = suiviParcelleDrafted.presenceMenteReligieuse,
            listIem = resources.getStringArray(R.array.fullyPoor)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })


        passSetupSuiviParcelleModel(suiviParcelleDrafted)
    }

    private lateinit var binding: ActivitySuiviParcelleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuiviParcelleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(com.intuit.ssp.R.dimen._6ssp),
            resources.getDimension(com.intuit.ssp.R.dimen._5ssp))

        suiviParcelleDao = ProgBandRoomDatabase.getDatabase(this)?.suiviParcelleDao()

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        binding.clickSaveSuivi.setOnClickListener {
            collectDatas()
        }

        binding.clickCancelSuivi.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        binding.imageDraftBtn.setOnClickListener {
            draftSuiviParcelle(draftedDataSuiviParcelle ?: DataDraftedModel(uid = 0))
        }

        Commons.addNotZeroAtFirstToET(binding.editSauvageonSParcelle)
        Commons.addNotZeroAtFirstToET(binding.editNombrAgroSParcelle)
        Commons.addNotZeroAtFirstToET(binding.editFrequencPestSParcel)
        Commons.addNotZeroAtFirstToET(binding.editIntantAnDerPestSParcel)
        Commons.addNotZeroAtFirstToET(binding.editFrequencIntantAnDerSParcel)
        Commons.addNotZeroAtFirstToET(binding.editDesherbageManuelSuivi)
        Commons.addNotZeroAtFirstToET(binding.editTraitInsecteParOuRavQtSParcel)
        Commons.addNotZeroAtFirstToET(binding.editTraitInsecteParOuRavFrequSParcel)

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataSuiviParcelle = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
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

        binding.editdateVisiteSuiviParcel.setOnClickListener { configDate(binding.editdateVisiteSuiviParcel) }

        Commons.setEditAndSpinnerRV(this, binding.recyclerInsecteAmisSuiviParcelle, binding.clickAddInsectAmisSuiviParcelle, binding.editInsecteNomAmisParcelle, binding.selectInsecteAmisQuantiteSuiviParcelle,"Nom d'insecte", "Quantité")
        Commons.setEditAndSpinnerRV(this, binding.recyclerAutreInsecteParOuRavSuiviParcelle, binding.clickAddAutreInsecteParOuRavSuiviParcelle, binding.editAutreInsecteParOuRavNomSParcelle, binding.selectAutrInsecteParOuRavQtSuiviParcelle, "Nom d'insecte", "Quantité")
        Commons.setSpinnerAndSpinnerRV(this, binding.recyclerInsecteParOuRavSuiviParcelle, binding.clickAddInsecteParOuRavSuiviParcelle, binding.selectInsecteParOuRavNomSuiviParcelle, binding.selectInsecteParOuRavQtSuiviParcelle, "Nom d'insecte", "Quantité")

        Commons.setEditAndSpinnerRV(this, binding.recyclerArbrAgroSuiviParcelle, binding.clickAddArbrAgroSuiviParcelle, binding.editNombrAgroSParcelle, binding.selectArbrAgroParcelle, "Nom d'arbre", "Quantité", isInverted = true)

        Commons.setFiveItremRV(this, binding.recyclerPestListSuiviParcel, binding.clickAddPestListSuiviParcel, binding.selectPestNomSParcell,binding.selectPestContenantSParcell,binding.selectPestUniteSParcell,binding.editQuantitPestSParcel,binding.editFrequencPestSParcel, libeleList = mutableListOf<String>("Nom", "Contenant", "Unité", "Quantité", "Fréquence"))
        Commons.setFiveItremRV(this, binding.recyclerIntantAnDerListSuiviParcel, binding.clickAddIntantAnDerListSuiviParcel, binding.selectIntantAnDerSParcell,binding.selectIntantAnDerContenantSParcell,binding.selectIntantAnDerUniteSParcell,binding.editIntantAnDerPestSParcel,binding.editFrequencIntantAnDerSParcel, libeleList = mutableListOf<String>("Nom", "Contenant", "Unité", "Quantité", "Fréquence"))
        Commons.setFiveItremRV(this, binding.recyclerTraitInsecteParOuRavListSuiviParcel, binding.clickAddTraitInsecteParOuRavListSuiviParcel, binding.selectTraitInsecteParOuRavNomSParcell,binding.selectTraitInsecteParOuRavContenantSParcell,binding.selectTraitInsecteParOuRavUniteSParcell,binding.editTraitInsecteParOuRavQtSParcel,binding.editTraitInsecteParOuRavFrequSParcel, libeleList = mutableListOf<String>("Nom", "Contenant", "Unité", "Quantité", "Fréquence"))

        arbresList = ProgBandRoomDatabase.getDatabase(this)?.arbreDao()?.getAll() ?: mutableListOf()

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_l_arbre),
            getString(R.string.la_liste_des_arbres_d_ombrage_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectArbrAgroParcelle,
            listIem = arbresList?.map { "${ it.nom }" }?.toList()?:listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })
//        selectArbrAgroParcelle.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, )

        setAnimauSParcelleRV()

    }

    private fun setAllSelection() {

        setOtherListener()

        setupSectionSelection()

        Commons.setupItemMultiSelection(this, binding.selectArbreSParcelle,
            getString(R.string.quelle_vari_t_d_arbre_ombrage_souhaiterais_tu_avoir), arbresList?.map { CommonData(0, it.nom) }?.toMutableList()?: mutableListOf() ){

        }

        Commons.setListenerForSpinner(this,
            getString(R.string.as_tu_b_n_fici_d_arbres_agro_forestiers),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectAgroForesterieSParcelle,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerArbreAgroSParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_rencontrer_des_animaux),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectAnimauRencontSParcell,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.linearAnimauxContainerSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.y_a_t_il_des_insectes_parasites),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.linearInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_observ_d_autres_insectes_ou_ravageur_qui_n_apparaissent_pas_dans_la_liste_pr_c_dente),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectAutrInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.linearAutrInsecteParOuRavSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.pr_sence_d_autres_types_d_insecte),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectPresencInsectSuiviParcel,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    binding.containerInsectAmisSuiviParcelle.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_traiter_votre_parcelle),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = binding.selectTraitInsecteParOuRavSuivi,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
//                    qdf qf
//                    containerListInsectSuivParce.visibility = visibility
                    binding.containerTraitInsecteParOuRavListSParcel.visibility = visibility
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
            binding.recyclerAnimauxSuiviParcelle.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerAnimauxSuiviParcelle.adapter = animauSParcelleAdapter
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        binding.clickSaveAnimauxSuiviParcelle.setOnClickListener {
            try {
                if (binding.editAnimalSuiviParcelle.text.toString()
                        .isEmpty()
                ) {
                    Commons.showMessage(getString(R.string.renseignez_des_donn_es_sur_l_animal_svp), this, callback = {})
                    return@setOnClickListener
                }

                val animaux = CommonData(
                    0,
                    binding.editAnimalSuiviParcelle.text.toString().trim(),
                )

                if(animaux.nom?.length?:0 > 0){
                    animauListSParcelle?.forEach {
                        if (it.nom?.uppercase() == animaux.nom?.uppercase()) {
                            ToastUtils.showShort(getString(R.string.cet_animal_est_d_ja_ajout))

                            return@setOnClickListener
                        }
                    }

                    animauListSParcelle?.add(animaux)
                    animauSParcelleAdapter?.notifyDataSetChanged()

                    binding.editAnimalSuiviParcelle.text?.clear()
                }

            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

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
            spinner = binding.selectSectionSParcelle,
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
            spinner = binding.selectLocaliteSParcelle,
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
        producteursList = ProgBandRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if (it.uid.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                } else {
                    if (it.id.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = binding.selectProducteurSParcelle,
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
        var parcellesList = ProgBandRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
            ?.getParcellesProducteur(producteurId = producteurId.toString(), agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

//        LogUtils.json(parcellesList)
        var libItem: String? = null
        currVal3?.let { idc ->
            parcellesList?.forEach {
                if(it.isSynced){
                    if (it.id.toString() == idc.toString()) libItem = Commons.getParcelleNotSyncLibel(it)
                }else{
                    if (it.uid.toString() == idc.toString()) libItem = Commons.getParcelleNotSyncLibel(it)
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_parcelle),
            getString(R.string.la_liste_des_parcelles_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = binding.selectParcelleSParcelle,
            listIem = parcellesList?.map { Commons.getParcelleNotSyncLibel(it) }
                ?.toList() ?: listOf(),
            onChanged = {

                parcellesList?.let { list ->
                    var parcelle = list.get(it)
                    parcelleCommon.nom = Commons.getParcelleNotSyncLibel(parcelle)

                    if(parcelle.isSynced){
                        parcelleCommon.id = parcelle.id!!
                    }else{
                        parcelleCommon.id = parcelle.uid.toString().toInt()
                    }

                    //setupParcelleSelection(parcelleCommon.id, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })
    }


}
