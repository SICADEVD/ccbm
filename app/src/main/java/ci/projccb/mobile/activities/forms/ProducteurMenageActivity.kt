package ci.projccb.mobile.activities.forms

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner
import ci.projccb.mobile.activities.infospresenters.MenagePreviewActivity
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.getAllTitleAndValueViews
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.showYearPickerDialog
import ci.projccb.mobile.tools.Commons.Companion.toUtilInt
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_producteur_menage.*
import java.util.ArrayList

class ProducteurMenageActivity : AppCompatActivity() {


    var localitesList: MutableList<LocaliteModel>? = mutableListOf()
    var energieDao: SourceEnergieDao? = null
    var ordureMenagereDao: OrdureMenagereDao? = null
    var toilettesDao: EauUseeDao? = null
    var vaissellesDao: EauUseeDao? = null
    var sourceEauDao: SourceEauDao? = null
    var typeMachineDao: TypeMachineDao? = null
    var gardeMachineDao: GardeMachineDao? = null
    var gardeMachinePulDao: GardeMachineDao? = null
    var producteurDao: ProducteurDao? = null
    var prodMenagereDao: ProducteurMenageDao? = null

    var sourceEauxList: MutableList<SourceEauModel>? = null;
    var orduresList: MutableList<OrdureMenagereModel>? = null;
    var toilettesList: MutableList<EauUseeModel>? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var vaissellesList: MutableList<EauUseeModel>? = null;
    var energiesList: MutableList<SourceEnergieModel>? = null;
    var typeMachinesList: MutableList<TypeMachineModel>? = null;
    var gardeMachinesList: MutableList<GardeMachineModel>? = null
    var gardeMachinePulList: MutableList<GardeMachineModel>? = null

    var producteurNomPrenoms = ""
    var producteurId = ""
    var producteurCode = ""

    var localiteNom = ""
    var localiteId = ""

    var quartierNom = ""
    var sourceEnergie = ""
    var ordureMenager = ""
    var dechetYesNo = ""
    var atomisateurYesNo = ""
    var toiletteEau = ""
    var vaisselleEau = ""
    var boisSemaine = ""
    var wcYesNo = ""
    var eauPotable = ""
    var machinePulverisationYesNo = ""
    var machinePulverisation = ""
    var machinePulveKeeper = ""
    var equipementProtectionYesNo = ""
    var traitementSelfYesNo = ""
    var traitementHolderNom = ""
    var machineEmpruntYesNo = ""
    var machineEmpruntKeeper = ""
    var femmeActiviteYesNo = ""
    var femmeActivite = ""
    var femmeCacaoSuperficie = ""
    var donFemmeCacaoYesNo = ""
    var donFemmeCacaoSuperficie = ""
    var draftedDataMenage: DataDraftedModel? = null

    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();

    fun setupEnergiesSelection() {
        val arrayEnergies: MutableList<String> = mutableListOf()
        //energieDao = CcbRoomDatabase.getDatabase(applicationContext)?.sourceEnergieDoa();
        energiesList = AssetFileHelper.getListDataFromAsset(17, this@ProducteurMenageActivity) as MutableList<SourceEnergieModel>?
                //energieDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arrayEnergies.add("Choisir la source...")

        energiesList?.map {
            arrayEnergies.add(it.nom!!)
        }

        val energiesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayEnergies)
        selectEnergieMenage!!.adapter = energiesAdapter

        selectEnergieMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                sourceEnergie = arrayEnergies[position]

                if (sourceEnergie.uppercase().contains("BOIS")) {
                    //linearBoisParSemaineContainerMenage.visibility = View.VISIBLE
                } else {
                    //linearBoisParSemaineContainerMenage.visibility = View.GONE
                    boisSemaine = ""
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupOrduresSelection() {
        val arrayOrdures: MutableList<String> = mutableListOf()
        //ordureMenagereDao = CcbRoomDatabase.getDatabase(applicationContext)?.ordureMenagereDoa();
        orduresList = AssetFileHelper.getListDataFromAsset(7, this@ProducteurMenageActivity) as MutableList<OrdureMenagereModel>?
                //ordureMenagereDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arrayOrdures.add("Choisir...")

        orduresList?.map {
            arrayOrdures.add(it.nom!!)
        }

        val orduresAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayOrdures)
        //selectOrdureMenage!!.adapter = orduresAdapter

//        selectOrdureMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                ordureMenager = arrayOrdures[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun setupEauToilettesSelection() {
        val arrayToilettes: MutableList<String> = mutableListOf()
        //toilettesDao = CcbRoomDatabase.getDatabase(applicationContext)?.eauUseeDoa();
        toilettesList = AssetFileHelper.getListDataFromAsset(1, this@ProducteurMenageActivity) as MutableList<EauUseeModel>?
            //toilettesDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arrayToilettes.add("Choisir...")

        toilettesList?.map {
            arrayToilettes.add(it.nom!!)
        }

        val toilettesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayToilettes)
        //selectEauToiletteMenage!!.adapter = toilettesAdapter

//        selectEauToiletteMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                toiletteEau = arrayToilettes[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun setupLocaliteSelection() {
        localitesList = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

        if (localitesList?.size == 0) {
            showMessage(
                getString(R.string.la_liste_des_localit_s_est_vide_refaite_une_mise_jour),
                this,
                finished = false,
                callback = {},
                getString(R.string.compris),
                false,
                showNo = false,
            )

            localiteId = ""
            localiteNom = ""
            //selectLocaliteMenage?.adapter = null
        } else {
            val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
            //selectLocaliteMenage!!.adapter = localiteAdapter

//            selectLocaliteMenage.setTitle("Choisir la localite")
//            selectLocaliteMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                    val locality = localitesList!![position]
//                    localiteNom = locality.nom!!
//
//                    localiteId = if (locality.isSynced) locality.id!!.toString() else locality.uid.toString()
//
//                    setupProducteurSelection(localiteId)
//                }
//
//                override fun onNothingSelected(arg0: AdapterView<*>) {
//                }
//            }
        }
    }


    fun setupEauVaissellesSelection() {
        val arrayVaisselles: MutableList<String> = mutableListOf()
        //vaissellesDao = //CcbRoomDatabase.getDatabase(applicationContext)?.eauUseeDoa()
        vaissellesList = AssetFileHelper.getListDataFromAsset(1, this@ProducteurMenageActivity) as MutableList<EauUseeModel>?
                //vaissellesDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arrayVaisselles.add("Choisir...")

        vaissellesList?.map {
            arrayVaisselles.add(it.nom!!)
        }

        val vaissellesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayVaisselles)
//        selectEauVaisselleMenage!!.adapter = vaissellesAdapter
//
//        selectEauVaisselleMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                vaisselleEau = arrayVaisselles[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun setupSourceEauxSelection() {
        val arraySourceEau: MutableList<String> = mutableListOf()
        //sourceEauDao = CcbRoomDatabase.getDatabase(applicationContext)?.sourceEauDoa();
        sourceEauxList = AssetFileHelper.getListDataFromAsset(16, this@ProducteurMenageActivity) as MutableList<SourceEauModel>?
                //sourceEauDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arraySourceEau.add("Choisir la source...")

        sourceEauxList?.map {
            arraySourceEau.add(it.nom!!)
        }

        val sourceEauAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arraySourceEau)
//        selectEauPotableMenage!!.adapter = sourceEauAdapter
//
//        selectEauPotableMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                eauPotable = arraySourceEau[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//
//            }
//        }
    }


    fun setupAtomisateurSelection() {
        selectAtomisateurYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                atomisateurYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupTypeMachinesSelection() {
        val arrayTypeMachines: MutableList<String> = mutableListOf()
        //typeMachineDao = CcbRoomDatabase.getDatabase(applicationContext)?.typeMachineDao();
        typeMachinesList = AssetFileHelper.getListDataFromAsset(18, this@ProducteurMenageActivity) as MutableList<TypeMachineModel>?
                //typeMachineDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arrayTypeMachines.add(getString(R.string.choisir_le_type))

        typeMachinesList?.map {
            arrayTypeMachines.add(it.nom!!)
        }

        val typeMachinesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayTypeMachines)
        selectMachinePulMenage!!.adapter = typeMachinesAdapter

        selectMachinePulMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                machinePulverisation = arrayTypeMachines[position]

                if (machinePulverisation.uppercase().contains("FECA")) {
                    //linearAtomisateurContainerMenage.visibility = View.GONE
                } else {
                    //linearAtomisateurContainerMenage.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupProducteurSelection(id: Int, currVal2: String? = null) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if(it.id == 0){
                    if(it.uid.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }else{
                    if(it.id.toString() == idc.toString()) libItem = "${it.nom} ${it.prenoms}"
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurMenage,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    if(producteur.isSynced == true){
                        producteurCommon.id = producteur.id!!
                    }else producteurCommon.id = producteur.uid

                    //setupProducteurSelection(localiteCommon.id, currVal2)
                }


            },
            onSelected = { itemId, visibility ->

            })



//        producteursList?.map {
//            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
//        }?.let {
//            producteursDatas.addAll(it)
//        }
//
//        val menageDraftedLocal = ApiClient.gson.fromJson(draftedDataMenage?.datas, ProducteurMenageModel::class.java)
//        selectProducteurMenage!!.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)
//
//        if (menageDraftedLocal != null) {
//            provideDatasSpinnerSelection(
//                selectProducteurMenage,
//                menageDraftedLocal.producteurNomPrenoms,
//                producteursDatas
//            )
//        }
//
//        selectProducteurMenage.setTitle("Choisir le producteur")
//        selectProducteurMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val producteur = producteursList!![position]
//                producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"
//                producteurCode = producteur.codeProd.toString()
//
//                producteurId = if (producteur.isSynced) {
//                    producteur.id.toString()
//                } else {
//                    producteur.uid.toString()
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }



    /*fun setupProducteurSelection(localiteId: String) {
        producteurDao = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
        producteursList = producteurDao?.getProducteursByLocalite(localiteId)

        val producteursAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursList!!)
        selectProducteurMenage!!.adapter = producteursAdapter

        selectProducteurMenage.setTitle("Choisir le producteur")

        selectProducteurMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val producteur = producteursList!![position]

                producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"
                producteurCode = producteur.codeProd!!

                producteurId = if (producteur.isSynced) {
                    producteur.id.toString()
                } else {
                    producteur.uid.toString()
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }*/


    fun setupDechetYesNoSelection() {
//        selectDechetYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                dechetYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun setupTraitementProtectionYesNoSelection() {
//        selectEquipementProtectionYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                equipementProtectionYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun setupWCYesNoSelection() {
//        selectWCYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                wcYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//
//            }
//        }
    }


    /*fun setupMachinePulYesNoSelection() {
        selectMachineYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {

                machinePulverisationYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                if (position == 1) {
                    linearTypeMachinePulContainerMenage.visibility = View.VISIBLE
                    linearMachinePulKeeperContainerMenage.visibility = View.VISIBLE
                } else {
                    linearTypeMachinePulContainerMenage.visibility = View.GONE
                    linearMachinePulKeeperContainerMenage.visibility = View.GONE
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }*/


    /*fun setupProtectionYesNoSelection() {
        selectProtectionYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                equipementProtectionYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }*/


    fun setupSelfTraitementYesNoSelection() {
        selectTraitementYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                traitementSelfYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                when (traitementSelfYesNo) {
                    getString(R.string.oui) -> {
                        linearTypeMachinePulContainerMenage.visibility = View.VISIBLE
                        linearMachinePulKeeperContainerMenage.visibility = View.VISIBLE
//                        linearTraiteYourselfFarmEquipmentYesNoContainerMenage.visibility = View.VISIBLE
//                        linearChampsNoNumberContainerMenage.visibility = View.GONE
                    }
                    getString(R.string.non) -> {
                        linearTypeMachinePulContainerMenage.visibility = View.GONE
                        linearMachinePulKeeperContainerMenage.visibility = View.GONE
//                        linearAtomisateurContainerMenage.visibility = View.GONE
//                        linearTraiteYourselfFarmEquipmentYesNoContainerMenage.visibility = View.GONE
//                        linearChampsNoNumberContainerMenage.visibility = View.VISIBLE
                    }
                    else -> {
                        //linearTraiteYourselfFarmEquipmentYesNoContainerMenage.visibility = View.GONE
                        linearTypeMachinePulContainerMenage.visibility = View.GONE
                        linearMachinePulKeeperContainerMenage.visibility = View.GONE
//                        linearAtomisateurContainerMenage.visibility = View.GONE
//                        linearChampsNoNumberContainerMenage.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }


    fun setupFemmeActiviteYesNoSelection() {
        selectFemmeActiviteYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {

                femmeActiviteYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                when (femmeActiviteYesNo.uppercase()) {
                    getString(R.string.oui) -> {
                        //linearFemmeActiviteContainerMenage.visibility = View.VISIBLE
                    }
                    getString(R.string.non) -> {
                        //linearFemmeActiviteContainerMenage.visibility = View.GONE
                    }
                    else -> {
                        //linearFemmeActiviteContainerMenage.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupDonFemmeYesNoSelection() {
//        selectDonCacaoYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                donFemmeCacaoYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                if (donFemmeCacaoYesNo == getString(R.string.oui)) {
//                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.VISIBLE
//                } else {
//                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.GONE
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//
//            }
//        }
    }

/*
    fun setupMachineEmpruntYesNoSelection() {
        selectMachineEmpruntYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                if (position == 1) {
                    linearMachineEmpruntKeeperContainerMenage.visibility = View.VISIBLE
                } else {
                    linearMachineEmpruntKeeperContainerMenage.visibility = View.GONE
                }
                machineEmpruntYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }*/


    fun setupGardeMachinesPulSelection() {
        //gardeMachinePulDao = CcbRoomDatabase.getDatabase(applicationContext)?.gardeMachineDoa();
        gardeMachinePulList = AssetFileHelper.getListDataFromAsset(2, this@ProducteurMenageActivity) as MutableList<GardeMachineModel>?
        //gardeMachinePulDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())


        val gardeMachinePulAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, gardeMachinePulList!!)
        selectMachineKeeperMenage!!.adapter = gardeMachinePulAdapter

        selectMachineKeeperMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val gardeMachinePulverisateur = gardeMachinePulList!![position]
                machinePulveKeeper = gardeMachinePulverisateur.nom!!
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

    }


    fun collectDatas() {
        if ( editNbreEnfant0a5Menage.text.toString().toUtilInt()?:0 < editNbreEnfantSansExtrMenage.text.toString().toUtilInt()?:0 ) {
            showMessage(getString(R.string.la_valeur_des_champs_pour_les_enfants_de_0_5_ans_incorrectes), this, callback = {})
            return
        }

        if ( editNbreEnfant6a17Menage.text.toString().toUtilInt()?:0 < editNbre6A17EnfantSansExtrMenage.text.toString().toUtilInt()?:0
            || editNbreEnfant6a17Menage.text.toString().toUtilInt()?:0 < editNbreEnfantScolariseMenage.text.toString().toUtilInt()?:0
            ) {
            showMessage(getString(R.string.la_valeur_des_champs_pour_les_enfants_de_6_17_ans_incorrectes), this, callback = {})
            return
        }
//
//        quartierNom = editQuartierMenage.text?.trim().toString()
//        //femmeCacaoSuperficie = editSuperficieCacaoMenage.text?.trim().toString()
//        donFemmeCacaoSuperficie = editDonSuperficieCacaoMenage.text?.trim().toString()
//
//        //traitementHolderNom = editTraiteurMenage.text?.trim().toString()
//        femmeActivite = editFemmeActiviteMenage.text?.trim().toString()
//
//
//        val producteurMenageModel = getProducteurMenageObjet()
//
//        LogUtils.d("VAR MENAGE ACT : "+producteurMenageModel.equipements)

        val itemModel = getProducteurMenageObjet()

        if(itemModel == null) return

        val producteurMenage = itemModel?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurs_id = producteurCommon.id.toString()
                isSynced = false
                //agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()

                sources_energies_id = GsonUtils.toJson(selectEnergieMenage.selectedStrings)
                ordures_menageres_id = GsonUtils.toJson(selectOrdureMenagMenage.selectedStrings)
            }
        }

        val mapEntries: List<MapEntry>? = itemModel?.second?.map { MapEntry(it.first, it.second) }

        Commons.printModelValue(producteurMenage as Object, mapEntries)

        try {
            val intentMenagePreview = Intent(this, MenagePreviewActivity::class.java)
            intentMenagePreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentMenagePreview.putExtra("preview", producteurMenage)
            intentMenagePreview.putExtra("draft_id", draftedDataMenage?.uid)
            startActivity(intentMenagePreview)
        } catch (ex: Exception) {
            throw Exception(ex)
        }
    }

    private fun getProducteurMenageObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()):  Pair<ProducteurMenageModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

//        return  ProducteurMenageModel(
//            uid = 0,
//            activiteFemme = femmeActivite,
//            //boisChauffe = editNbreBoisSemaineMenage.text?.trim().toString(),
//            sources_energies_id = sourceEnergie,
//            ordures_menageres_id = ordureMenager,
//            separationMenage = dechetYesNo,
//            eauxToillette = toiletteEau,
//            eauxVaisselle = vaisselleEau,
//            wc = wcYesNo,
//            sources_eaux_id = eauPotable,
//            machine = machinePulverisationYesNo,
//            type_machines_id = machinePulverisation,
//            garde_machines_id = machinePulveKeeper,
//            equipements = equipementProtectionYesNo,
//            producteurNomPrenoms = producteurNomPrenoms,
//            traitementChamps = traitementSelfYesNo,
//            nomPersonneTraitant = traitementHolderNom,
//            empruntMachine = machineEmpruntYesNo,
//            gardeEmpruntMachine = machineEmpruntKeeper,
//            champFemme = femmeActiviteYesNo,
//            nomActiviteFemme = femmeActivite,
//            superficieCacaoFemme = femmeCacaoSuperficie,
//            nombreHectareFemme = donFemmeCacaoSuperficie,
//            //numeroPersonneTraitant = editChampsNoNumeroMenage.text.toString().trim(),
//            quartier = quartierNom,
//            producteurs_id = producteurId,
//            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
//            origin = "local",
//            codeProducteur = producteurCode,
//            localiteNom = localiteNom
//        )
        var itemList = getSetupProducteurMenageModel(ProducteurMenageModel(uid = 0, agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),  origin = "local",), mutableListOf<Pair<String,String>>())
        //LogUtils.d(.toString())
        var allField = itemList.second
        var isMissing = false
        var message = ""
        var notNecessaire = listOf<String>(
            getString(R.string.ann_e_certification).lowercase(),
            getString(R.string.code_producteur).lowercase(),
            getString(R.string.en_tant_que).lowercase(),
            getString(R.string.num_ro_de_t_l_phone).lowercase(),
            getString(R.string.n_de_la_pi_ce_cmu).lowercase(),
            getString(R.string.n_de_carte_de_s_curit_sociale).lowercase())
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

    private fun getSetupProducteurMenageModel(producteurMenageModel: ProducteurMenageModel, mutableListOf: MutableList<Pair<String, String>>): Pair<ProducteurMenageModel, MutableList<Pair<String, String>>> {
        val mainLayout = findViewById<ViewGroup>(R.id.layout_producteur_menage)
        getAllTitleAndValueViews(mainLayout, producteurMenageModel, false,
            mutableListOf
        )
        return Pair(producteurMenageModel, mutableListOf)
    }

    fun passSetupMenageModel(
        model: ProducteurMenageModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_producteur_menage)
        model?.let {
            Commons.setAllValueOfTextViews(mainLayout, model)
        }
    }


    fun clearFields() {
        producteurNomPrenoms = ""
        producteurId = ""
        quartierNom = ""
        //editQuartierMenage.text = null
        sourceEnergie = ""
        selectEnergieMenage.setSelection(0)
        ordureMenager = ""
        //selectOrdureMenage.setSelection(0)
        dechetYesNo = ""
        //selectDechetYesNoMenage.setSelection(0)
        eauPotable = ""
        toiletteEau = ""
        vaisselleEau = ""
        //selectEauToiletteMenage.setSelection(0)
        //selectEauVaisselleMenage.setSelection(0)
        //selectEauPotableMenage.setSelection(0)
        //selectWCYesNoMenage.setSelection(0)
        wcYesNo = ""
        machinePulverisation = ""
        machineEmpruntKeeper = ""
        machineEmpruntYesNo = ""
        machinePulveKeeper = ""
        machinePulverisationYesNo = ""
        selectMachinePulMenage.setSelection(0)
        selectMachineKeeperMenage.setSelection(0)
        equipementProtectionYesNo = ""
        traitementSelfYesNo = ""
        selectTraitementYesNoMenage.setSelection(0)
        traitementHolderNom = ""
        femmeActivite = ""
        femmeCacaoSuperficie = ""
        femmeActiviteYesNo = ""
        donFemmeCacaoSuperficie = ""

        donFemmeCacaoYesNo = ""
        selectFemmeActiviteYesNoMenage.setSelection(0)
        //selectDonCacaoYesNoMenage.setSelection(0)

        //editQuartierMenage.requestFocus()

    }


    fun draftMenage(draftModel: DataDraftedModel?) {
        //quartierNom = editQuartierMenage.text?.trim().toString()
        //femmeCacaoSuperficie = editSuperficieCacaoMenage.text?.trim().toString()
        ///donFemmeCacaoSuperficie = editDonSuperficieCacaoMenage.text?.trim().toString()

        //traitementHolderNom = editTraiteurMenage.text?.trim().toString()
        //femmeActivite = editFemmeActiviteMenage.text?.trim().toString()


        //val producteurMenageModelDraft = getProducteurMenageObjet()

        //LogUtils.d("VAR MENAGE ACT 2 : "+producteurMenageModelDraft.equipements)

        val itemModel = getProducteurMenageObjet(false)

        if(itemModel == null) return

        val producteurMeangeDraft = itemModel?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localite = localiteCommon.id.toString()
                producteurs_id = producteurCommon.id.toString()
                isSynced = false

                sources_energies_id = GsonUtils.toJson(selectEnergieMenage.selectedStrings)
                ordures_menageres_id = GsonUtils.toJson(selectOrdureMenagMenage.selectedStrings)
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
                        datas = ApiClient.gson.toJson(producteurMeangeDraft),
                        typeDraft = "menage",
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
        val menageUndrafted = ApiClient.gson.fromJson(draftedData.datas, ProducteurMenageModel::class.java)

        //  LogUtils.json(menageUndrafted)

        setupSectionSelection(menageUndrafted.section, menageUndrafted.localite, menageUndrafted.producteurs_id)

//        Commons.setListenerForSpinner(this,
//            "Choix le quartier",
//            "La liste des quartiers semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectQuartierMenage,
//            currentVal = menageUndrafted.quartier,
//            listIem = listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//
//            })

//        Commons.setListenerForSpinner(this,
//            "Choix de l'énergie",
//            "La liste des énergies semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectEnergieMenage,
//            currentVal = menageUndrafted.sources_energies_id,
//            itemChanged = arrayListOf(Pair(1, "Bois de chauffe")),
//            listIem = (AssetFileHelper.getListDataFromAsset(
//                17,
//                this
//            ) as MutableList<SourceEnergieModel>)?.map { it.nom }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if(itemId==1){
//                    containerNbreDBoisMenage.visibility = visibility
//                }
//            })

        setupSourceEnergieMultiSelection(
            GsonUtils.fromJson(menageUndrafted.sources_energies_id, object: TypeToken<MutableList<String>>(){}.type )
        )

//        Commons.setListenerForSpinner(this,
//            "Choix des ordures",
//            "La liste des ordures ménagères semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectOrdureMenagMenage,
//            currentVal = menageUndrafted.ordures_menageres_id,
//            listIem = (AssetFileHelper.getListDataFromAsset(
//                7,
//                this
//            ) as MutableList<OrdureMenagereModel>)?.map { it.nom }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//
//            })

        setupOrdurMenagMultiSelection(
            GsonUtils.fromJson(menageUndrafted.ordures_menageres_id, object: TypeToken<MutableList<String>>(){}.type )
        )

        Commons.setListenerForSpinner(this,
            getString(R.string.type_d_eaux_de_toilette),
            getString(R.string.la_liste_des_eaux_de_toilette_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEauToilMenage,
            currentVal = menageUndrafted.eauxToillette,
            listIem = (AssetFileHelper.getListDataFromAsset(
                1,
                this
            ) as MutableList<EauUseeModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.type_d_eaux_de_vaisselle),
            getString(R.string.la_liste_des_eaux_de_vaisselle_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEauVaissMenage,
            currentVal = menageUndrafted.eauxVaisselle,
            listIem = (AssetFileHelper.getListDataFromAsset(
                1,
                this
            ) as MutableList<EauUseeModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

//        Commons.setListenerForSpinner(this,
//            "Quelle est la source d'eau ?",
//            "La liste des sources d'eau semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectEauPotablMenage,
//            itemChanged = arrayListOf(Pair(1, "Autre")),
//            currentVal = menageUndrafted.sources_eaux_id,
//            listIem = (AssetFileHelper.getListDataFromAsset(
//                16,
//                this
//            ) as MutableList<SourceEauModel>)?.map { it.nom }
//                ?.toList() ?: listOf(),
//            onChanged = {
//            },
//            onSelected = { itemId, visibility ->
//                if(itemId == 1){
//                    containerAutreSourceEauMenage.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            getString(R.string.traitez_vous_vos_champs),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTraitementYesNoMenage,
            currentVal = menageUndrafted.traitementChamps,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui)), Pair(2, getString(R.string.non))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearTypeMachinePulContainerMenage.visibility = visibility
                    containerAtomisateurContainerMenage.visibility = visibility
                    linearMachinePulKeeperContainerMenage.visibility = visibility
                }else{
                    containerNomApplicateurMenage.visibility = View.GONE
                    containerTelpApplicateurMenage.visibility = View.GONE
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quelle_machine_est_utilis_e),
            getString(R.string.la_liste_des_machines_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMachinePulMenage,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            currentVal = menageUndrafted.type_machines_id,
            listIem = (AssetFileHelper.getListDataFromAsset(
                18,
                this
            ) as MutableList<TypeMachineModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreMachineMenage.visibility = visibility
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.lieu_de_la_garde_machine),
            getString(R.string.la_liste_des_gardes_machines_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMachineKeeperMenage,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            currentVal = menageUndrafted.garde_machines_id,
            listIem = (AssetFileHelper.getListDataFromAsset(
                2,
                this
            ) as MutableList<GardeMachineModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreEndroitMenage.visibility = visibility
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.votre_femme_fait_des_activit_s),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectFemmeActiviteYesNoMenage,
            currentVal = menageUndrafted.activiteFemme,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerFemmeActivitPrecisMenage.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quelle_activit),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectFemmeActiviteDefMenage,
            itemChanged = arrayListOf(Pair(1, "Agricole"), Pair(2, "Non agricole")),
            listIem = resources.getStringArray(R.array.femmeActivite)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    //setupPrecisionActiv(resources.getStringArray(R.array.agricoleActivite))
                    containerFemmeAgricoleMenage.visibility = visibility
                    editSuperfDefFemmActMenage.visibility = visibility
                    //containerFemmeNonAgricoleMenage.visibility = View.GONE
                }else if(itemId == 2){
                    //setupPrecisionActiv(resources.getStringArray(R.array.noAgricoleActivite))
                    //containerFemmeNonAgricoleMenage.visibility = visibility
                    containerFemmeAgricoleMenage.visibility = visibility
                    editSuperfDefFemmActMenage.visibility = View.GONE
                }
            })



//        Commons.setListenerForSpinner(this,
//            "Donnes tu une partie de ton cacao ?",
//            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
//            spinner = selectDonCacaoYesNoMenage,
//            currentVal = menageUndrafted.champFemme,
//            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
//            listIem = resources.getStringArray(R.array.YesOrNo)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_des_quipements_de_protection_individuel_epi),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEquiDProtIndivMenage,
            currentVal = menageUndrafted.equipements,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerEquiEtatMenage.visibility = visibility
                }
            })

        passSetupMenageModel(menageUndrafted)

 //       try {
//            // Localite
//            val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val localitesDatas: MutableList<CommonData> = mutableListOf()
//            localitesLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                localitesDatas.addAll(it)
//            }
            //selectLocaliteMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//            provideDatasSpinnerSelection(
//                selectLocaliteMenage,
//                menageUndrafted.localiteNom,
//                localitesDatas
//            )

            // Producteur
            /*val producteursLists = CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val producteursDatas: MutableList<CommonData> = mutableListOf()
            producteursLists?.map {
                CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
            }?.let {
                producteursDatas.addAll(it)
            }
            selectProducteurMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)
            provideDatasSpinnerSelection(
                selectProducteurMenage,
                menageUndrafted.producteurNomPrenoms,
                producteursDatas
            )*/

            // energie
//            val energiesLists = CcbRoomDatabase.getDatabase(this)?.sourceEnergieDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val energiesDatas: MutableList<CommonData> = mutableListOf()
//            energiesLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                energiesDatas.addAll(it)
//            }
//            selectEnergieMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, energiesDatas)
//            provideDatasSpinnerSelection(
//                selectEnergieMenage,
//                menageUndrafted.sources_energies_id,
//                energiesDatas
//            )
//
//            // Ordure
//            val orduresLists = CcbRoomDatabase.getDatabase(this)?.ordureMenagereDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val orduresDatas: MutableList<CommonData> = mutableListOf()
//            orduresLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                orduresDatas.addAll(it)
//            }
            //selectOrdureMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, orduresDatas)
//            provideDatasSpinnerSelection(
//                selectOrdureMenage,
//                menageUndrafted.ordures_menageres_id,
//                orduresDatas
//            )

            // Dechets
//            provideStringSpinnerSelection(
//                selectDechetYesNoMenage,
//                menageUndrafted.separationMenage,
//                resources.getStringArray(R.array.YesOrNo)
//            )

            // Eaux
//            val eausLists = CcbRoomDatabase.getDatabase(this)?.eauUseeDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val eausDatas: MutableList<CommonData> = mutableListOf()
//            eausLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                eausDatas.addAll(it)
//            }
//            selectEauToiletteMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, eausDatas)
//            selectEauVaisselleMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, eausDatas)

//            provideDatasSpinnerSelection(
//                selectEauToiletteMenage,
//                menageUndrafted.eauxToillette,
//                eausDatas
//            )

//            provideDatasSpinnerSelection(
//                selectEauVaisselleMenage,
//                menageUndrafted.eauxVaisselle,
//                eausDatas
//            )

//            provideStringSpinnerSelection(
//                selectWCYesNoMenage,
//                menageUndrafted.wc,
//                resources.getStringArray(R.array.YesOrNo)
//            )

            // Eaux portable
//            val potablesLists = CcbRoomDatabase.getDatabase(this)?.sourceEauDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val potablesDatas: MutableList<CommonData> = mutableListOf()
//            potablesLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                potablesDatas.addAll(it)
//            }
            //selectEauPotableMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, potablesDatas)
//            provideDatasSpinnerSelection(
//                selectEauPotableMenage,
//                menageUndrafted.sources_eaux_id,
//                potablesDatas
//            )

            // champs traiteur
//            provideStringSpinnerSelection(
//                selectTraitementYesNoMenage,
//                menageUndrafted.traitementChamps,
//                resources.getStringArray(R.array.YesOrNo)
//            )
//
//            // machine
//            val machinesLists = CcbRoomDatabase.getDatabase(this)?.typeMachineDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val machinesDatas: MutableList<CommonData> = mutableListOf()
//            machinesLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                machinesDatas.addAll(it)
//            }
//            selectMachinePulMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, machinesDatas)
//            provideDatasSpinnerSelection(
//                selectMachinePulMenage,
//                menageUndrafted.type_machines_id,
//                machinesDatas
//            )
//
//            // garde machine
//            val gardeMachinesLists:  MutableList<GardeMachineModel>? =
//                AssetFileHelper.getListDataFromAsset(2, this@ProducteurMenageActivity) as MutableList<GardeMachineModel>?
//                //CcbRoomDatabase.getDatabase(this)?.gardeMachineDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//            val gardeMachinesDatas: MutableList<CommonData> = mutableListOf()
//            gardeMachinesLists?.map {
//                CommonData(id = it.id, nom = it.nom)
//            }?.let {
//                gardeMachinesDatas.addAll(it)
//            }
//            selectMachineKeeperMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, gardeMachinesDatas)
//            provideDatasSpinnerSelection(
//                selectMachineKeeperMenage,
//                menageUndrafted.garde_machines_id,
//                gardeMachinesDatas
//            )

            /*provideStringSpinnerSelection(
                selectAtomisateurYesNoMenage,
                menageUndrafted.m
            ) */// Todo fix atomisateur etat

            // Equipement
//            provideStringSpinnerSelection(
//                selectEquipementProtectionYesNoMenage,
//                menageUndrafted.equipements,
//                resources.getStringArray(R.array.YesOrNo)
//            )

            // femme activite
//            provideStringSpinnerSelection(
//                selectFemmeActiviteYesNoMenage,
//                menageUndrafted.activiteFemme,
//                resources.getStringArray(R.array.YesOrNo)
//            )
//
//            provideStringSpinnerSelection(
//                selectDonCacaoYesNoMenage,
//                menageUndrafted.champFemme,
//                resources.getStringArray(R.array.YesOrNo)
//            )

//            editChampsNoNumeroMenage.setText(menageUndrafted.numeroPersonneTraitant)
//            editTraiteurMenage.setText(menageUndrafted.nomPersonneTraitant)
            //editQuartierMenage.setText(menageUndrafted.quartier)
            //editNbreBoisSemaineMenage.setText(menageUndrafted.boisChauffe)
            //editFemmeActiviteMenage.setText(menageUndrafted.nomActiviteFemme)
            //editSuperficieCacaoMenage.setText(menageUndrafted.superficieCacaoFemme)
//        } catch (ex: Exception) {
//            throw Exception(ex)
//        }
    }

    fun setupSourceEauPotableMultiSelection(currentList : MutableList<String> = mutableListOf()) {
        val eauPotableList = AssetFileHelper.getListDataFromAsset(16, this) as MutableList<SourceEauModel>
        var listSelectEauPotablePosList = mutableListOf<Int>()
        var listSelectEauPotableList = mutableListOf<String>()

        var indItem = 0
        (eauPotableList)?.forEach {
            if(currentList.size > 0){ if(currentList.contains(it.nom)) listSelectEauPotablePosList.add(indItem) }
            indItem++
        }

        selectEauPotablMenage.setTitle(getString(R.string.o_procurez_vous_l_eau_potable))
        selectEauPotablMenage.setItems(eauPotableList.map { it.nom })
        //multiSelectSpinner.hasNoneOption(true)
        selectEauPotablMenage.setSelection(listSelectEauPotablePosList.toIntArray())
        selectEauPotablMenage.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
            override fun selectedIndices(indices: MutableList<Int>?) {
                listSelectEauPotablePosList.clear()
                listSelectEauPotablePosList.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                listSelectEauPotableList.clear()
                listSelectEauPotableList.addAll(strings?.toMutableList() ?: arrayListOf())
                if(listSelectEauPotableList.contains("Autre")) containerAutreSourceEauMenage.visibility = View.VISIBLE else containerAutreSourceEauMenage.visibility = View.GONE
            }

        })
    }

    private fun setupPrecisionActiv(stringArray: Array<String>, currentVal: String? = null) {

//        Commons.setListenerForSpinner(this,
//            "Précisez le type de l'activité :",
//            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
//            spinner = selectFemmeActivitPrecisMenage,
//            currentVal = currentVal,
//            listIem = stringArray
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerFemmeActivitPrecisMenage.visibility = visibility
//                }
//            })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producteur_menage)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        prodMenagereDao = CcbRoomDatabase.getDatabase(this)?.producteurMenageDoa()

//        editSuperficieCacaoMenage.doAfterTextChanged {
//            try {
//                setupDonFemmeYesNoSelection()
//                if (it.toString().isEmpty()) {
//                    linearDonCacaoFemmeYesNoContainerMenage.visibility = View.GONE
//                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.GONE
//                } else {
//                    /*if (data.contains(",")) {
//                        data = data.replace(',', '.')
//                    }*/
//                    if (it.toString().trim().toDouble() == 0.0) {
//                        linearDonCacaoFemmeYesNoContainerMenage.visibility = View.VISIBLE
//                    } else {
//                        linearDonCacaoFemmeYesNoContainerMenage.visibility = View.GONE
//                        linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.GONE
//                    }
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }

//        editDonSuperficieCacaoMenage.doAfterTextChanged {
//            try {
//
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }



//        setupEnergiesSelection()
//
//        setupOrduresSelection()
//
//        setupEauToilettesSelection()
//
//        setupEauVaissellesSelection()
//
//        setupSourceEauxSelection()
//
//        setupTypeMachinesSelection()
//
//        setupLocaliteSelection()
//
//        setupDechetYesNoSelection()
//
//        setupTraitementProtectionYesNoSelection()
//
//        setupWCYesNoSelection()
//
//        setupSelfTraitementYesNoSelection()
//
//        setupFemmeActiviteYesNoSelection()
//
//        setupDonFemmeYesNoSelection()
//
//        setupAtomisateurSelection()
//
//        setupGardeMachinesPulSelection()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveMenage.setOnClickListener {
            collectDatas()
        }

        clickCancelMenage.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftMenage(draftedDataMenage ?: DataDraftedModel(uid = 0))
        }

        setOtherListener()

        //applyFiltersDec(editSuperficieCacaoMenage)
        //applyFiltersDec(editDonSuperficieCacaoMenage)

        if (intent.getStringExtra("from") != null) {
            draftedDataMenage = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataMenage!!)
        }else{
            setAllSelection()
        }
    }

    private fun setOtherListener() {

        editAnneeFormatFemmeMenage.setOnClickListener { showYearPickerDialog(editAnneeFormatFemmeMenage) }

    }

    private fun setAllSelection() {

        setupSourceEauPotableMultiSelection()

        setupSectionSelection()

//        Commons.setListenerForSpinner(this,
//            "Choix le quartier",
//            "La liste des quartiers semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectQuartierMenage,
//            listIem = listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//
//            })

//        Commons.setListenerForSpinner(this,
//            "Choix de l'énergie",
//            "La liste des énergies semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectEnergieMenage,
//            itemChanged = arrayListOf(Pair(1, "Bois de chauffe")),
//            listIem = (AssetFileHelper.getListDataFromAsset(
//                17,
//                this
//            ) as MutableList<SourceEnergieModel>)?.map { it.nom }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if(itemId==1){
//                    containerNbreDBoisMenage.visibility = visibility
//                }
//            })

        setupSourceEnergieMultiSelection()

//        Commons.setListenerForSpinner(this,
//            "Choix des ordures",
//            "La liste des ordures ménagères semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectOrdureMenagMenage,
//            listIem = (AssetFileHelper.getListDataFromAsset(
//                7,
//                this
//            ) as MutableList<OrdureMenagereModel>)?.map { it.nom }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//
//            })

        setupOrdurMenagMultiSelection()

        Commons.setListenerForSpinner(this,
            getString(R.string.type_d_eaux_de_toilette),
            getString(R.string.la_liste_des_eaux_de_toilette_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEauToilMenage,
            listIem = (AssetFileHelper.getListDataFromAsset(
                1,
                this
            ) as MutableList<EauUseeModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.type_d_eaux_de_vaisselle),
            getString(R.string.la_liste_des_eaux_de_vaisselle_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEauVaissMenage,
            listIem = (AssetFileHelper.getListDataFromAsset(
                1,
                this
            ) as MutableList<EauUseeModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
            })

//        Commons.setListenerForSpinner(this,
//            "Quelle est la source d'eau ?",
//            "La liste des sources d'eau semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectEauPotablMenage,
//            itemChanged = arrayListOf(Pair(1, "Autre")),
//            listIem = (AssetFileHelper.getListDataFromAsset(
//                16,
//                this
//            ) as MutableList<SourceEauModel>)?.map { it.nom }
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if(itemId == 1){
//                    containerAutreSourceEauMenage.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            getString(R.string.traitez_vous_vos_champs),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTraitementYesNoMenage,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui)), Pair(2, getString(R.string.non))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    linearTypeMachinePulContainerMenage.visibility = visibility
                    containerAtomisateurContainerMenage.visibility = visibility
                    linearMachinePulKeeperContainerMenage.visibility = visibility
                }else{
                    containerNomApplicateurMenage.visibility = View.GONE
                    containerTelpApplicateurMenage.visibility = View.GONE
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quelle_machine_est_utilis_e),
            getString(R.string.la_liste_des_machines_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMachinePulMenage,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(
                18,
                this
            ) as MutableList<TypeMachineModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreMachineMenage.visibility = visibility
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.lieu_de_la_garde_machine),
            getString(R.string.la_liste_des_gardes_machines_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectMachineKeeperMenage,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(
                2,
                this
            ) as MutableList<GardeMachineModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreEndroitMenage.visibility = visibility
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.est_ce_que_votre_conjoint_exerce_une_activit_g_n_ratrice_de_revenu),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectFemmeActiviteYesNoMenage,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerFemmeActivitPrecisMenage.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.quelle_activit),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectFemmeActiviteDefMenage,
            itemChanged = arrayListOf(Pair(1, "Agricole"), Pair(2, "Non agricole")),
            listIem = resources.getStringArray(R.array.femmeActivite)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                LogUtils.d(itemId, visibility)
                if (itemId == 1) {
                    //setupPrecisionActiv(resources.getStringArray(R.array.agricoleActivite))
                    containerFemmeAgricoleMenage.visibility = visibility
                    editSuperfDefFemmActMenage.visibility = View.VISIBLE
                    //containerFemmeNonAgricoleMenage.visibility = View.GONE
                    setupNomFemActiviteList(selectNomFemmeActiviteDefMenage, resources.getStringArray(R.array.nomActiviteAgr))
                }else if(itemId == 2){
                    //setupPrecisionActiv(resources.getStringArray(R.array.noAgricoleActivite))
                    //containerFemmeNonAgricoleMenage.visibility = visibility
                    containerFemmeAgricoleMenage.visibility = visibility
                    editSuperfDefFemmActMenage.visibility = View.GONE
                    setupNomFemActiviteList(selectNomFemmeActiviteDefMenage, resources.getStringArray(R.array.nomActiviteNAgr))
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.comment_avez_vous_obtenu_le_capital_de_d_marrage),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectParcelObtenuDefMenage,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = resources.getStringArray(R.array.capitalDemarrag)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    conatinerFemmeAutrCapiDemaMenage.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_b_n_ficiez_d_une_formation),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectFormatFemmBenefMenage,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerBenefDFormatMenage.visibility = visibility
                }
            })

//        Commons.setListenerForSpinner(this,
//            "Donnes tu une partie de ton cacao ?",
//            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
//            spinner = selectDonCacaoYesNoMenage,
//            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
//            listIem = resources.getStringArray(R.array.YesOrNo)
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = visibility
//                }
//            })

        Commons.setListenerForSpinner(this,
            getString(R.string.avez_vous_des_quipements_de_protection_individuel_epi),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEquiDProtIndivMenage,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerEquiEtatMenage.visibility = visibility
                }
            })

    }

    private fun setupNomFemActiviteList(selectNomFemmeActiviteDefMenage: Spinner?, stringArray: Array<String>, currentVal: String? = null) {

        Commons.setListenerForSpinner(this,
            getString(R.string.quelle_est_le_nom_de_l_activit),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectNomFemmeActiviteDefMenage!!,
            currentVal = currentVal,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = stringArray?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1){
                    conatinerFemmeAutrNomActivitMenage.visibility = visibility
                }
            })

    }

    fun setupOrdurMenagMultiSelection(currentList : MutableList<String> = mutableListOf()) {
        val ordurMenagList: List<String?> = (AssetFileHelper.getListDataFromAsset(7, this) as MutableList<OrdureMenagereModel>).map { it.nom }
        var listordurMenagPosList = mutableListOf<Int>()
        var listordurMenagList = mutableListOf<String>()

        var indItem = 0
        (ordurMenagList)?.forEach {
            if(currentList.size > 0){ if(currentList.contains(it)) listordurMenagPosList.add(indItem) }
            indItem++
        }

        selectOrdureMenagMenage.setTitle(getString(R.string.comment_g_rez_vous_les_ordures_m_nag_res))
        selectOrdureMenagMenage.setItems(ordurMenagList)
        //multiSelectSpinner.hasNoneOption(true)
        selectOrdureMenagMenage.setSelection(listordurMenagPosList.toIntArray())
        selectOrdureMenagMenage.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
            override fun selectedIndices(indices: MutableList<Int>?) {
                listordurMenagPosList.clear()
                listordurMenagPosList.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                listordurMenagList.clear()
                listordurMenagList.addAll(strings?.toMutableList() ?: arrayListOf())

                //if(listSourceEnergieList.contains("Bois de chauffe")) containerNbreDBoisMenage.visibility = View.VISIBLE else containerNbreDBoisMenage.visibility = View.GONE
            }

        })
    }

    fun setupSourceEnergieMultiSelection(currentList : MutableList<String> = mutableListOf()) {
        val sourceEnergieList: List<String?> = (AssetFileHelper.getListDataFromAsset(17, this) as MutableList<SourceEnergieModel>).map { it.nom }
        var listSourceEnergiePosList = mutableListOf<Int>()
        var listSourceEnergieList = mutableListOf<String>()

        var indItem = 0
        (sourceEnergieList)?.forEach {
            if(currentList.size > 0){ if(currentList.contains(it)) listSourceEnergiePosList.add(indItem) }
            indItem++
        }

        selectEnergieMenage.setTitle(getString(R.string.choix_des_sources_d_nergies_du_m_nage))
        selectEnergieMenage.setItems(sourceEnergieList)
        //multiSelectSpinner.hasNoneOption(true)
        selectEnergieMenage.setSelection(listSourceEnergiePosList.toIntArray())
        selectEnergieMenage.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
            override fun selectedIndices(indices: MutableList<Int>?) {
                listSourceEnergiePosList.clear()
                listSourceEnergiePosList.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                listSourceEnergieList.clear()
                listSourceEnergieList.addAll(strings?.toMutableList() ?: arrayListOf())

                if(listSourceEnergieList.contains("Bois de chauffe")) containerNbreDBoisMenage.visibility = View.VISIBLE else containerNbreDBoisMenage.visibility = View.GONE
            }

        })
    }

    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null) {
        var sectionDao = CcbRoomDatabase.getDatabase(applicationContext)?.sectionsDao();
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
            spinner = selectSectionProducteurMenage,
            listIem = sectionList?.map { it.libelle }
                ?.toList() ?: listOf(),
            onChanged = {

                val section = sectionList!![it]
                //ogUtils.d(section)
                sectionCommon.nom = section.libelle!!
                sectionCommon.id = section.id!!

                setLocaliteSpinner(sectionCommon.id!!, currVal1, currVal2)

            },
            onSelected = { itemId, visibility ->

            })
    }

    fun setLocaliteSpinner(id: Int, currVal1:String? = null, currVal2: String? = null) {

        var localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa();
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
            spinner = selectLocaliteProduMenage,
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

}
