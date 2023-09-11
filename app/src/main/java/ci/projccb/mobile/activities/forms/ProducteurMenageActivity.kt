package ci.projccb.mobile.activities.forms

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.MenagePreviewActivity
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_producteur_menage.*

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
                    linearBoisParSemaineContainerMenage.visibility = View.VISIBLE
                } else {
                    linearBoisParSemaineContainerMenage.visibility = View.GONE
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
        selectOrdureMenage!!.adapter = orduresAdapter

        selectOrdureMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                ordureMenager = arrayOrdures[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
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
        selectEauToiletteMenage!!.adapter = toilettesAdapter

        selectEauToiletteMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                toiletteEau = arrayToilettes[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupLocaliteSelection() {
        localitesList = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()) ?: mutableListOf()

        if (localitesList?.size == 0) {
            showMessage(
                "La liste des localités est vide ! Refaite une mise à jour.",
                this,
                finished = false,
                callback = {},
                "OKAY",
                false,
                showNo = false,
            )

            localiteId = ""
            localiteNom = ""
            selectLocaliteMenage?.adapter = null
        } else {
            val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
            selectLocaliteMenage!!.adapter = localiteAdapter

            selectLocaliteMenage.setTitle("Choisir la localite")
            selectLocaliteMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                    val locality = localitesList!![position]
                    localiteNom = locality.nom!!

                    localiteId = if (locality.isSynced) locality.id!!.toString() else locality.uid.toString()

                    setupProducteurSelection(localiteId)
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {
                }
            }
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
        selectEauVaisselleMenage!!.adapter = vaissellesAdapter

        selectEauVaisselleMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                vaisselleEau = arrayVaisselles[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
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
        selectEauPotableMenage!!.adapter = sourceEauAdapter

        selectEauPotableMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                eauPotable = arraySourceEau[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
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

        arrayTypeMachines.add("Choisir le type...")

        typeMachinesList?.map {
            arrayTypeMachines.add(it.nom!!)
        }

        val typeMachinesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayTypeMachines)
        selectMachinePulMenage!!.adapter = typeMachinesAdapter

        selectMachinePulMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                machinePulverisation = arrayTypeMachines[position]

                if (machinePulverisation.uppercase().contains("FECA")) {
                    linearAtomisateurContainerMenage.visibility = View.GONE
                } else {
                    linearAtomisateurContainerMenage.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupProducteurSelection(localite: String?) {
        producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = localite)
        val producteursDatas: MutableList<CommonData> = mutableListOf()

        producteursList?.map {
            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
        }?.let {
            producteursDatas.addAll(it)
        }

        val menageDraftedLocal = ApiClient.gson.fromJson(draftedDataMenage?.datas, ProducteurMenageModel::class.java)
        selectProducteurMenage!!.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

        if (menageDraftedLocal != null) {
            provideDatasSpinnerSelection(
                selectProducteurMenage,
                menageDraftedLocal.producteurNomPrenoms,
                producteursDatas
            )
        }

        selectProducteurMenage.setTitle("Choisir le producteur")
        selectProducteurMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val producteur = producteursList!![position]
                producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"
                producteurCode = producteur.codeProd.toString()

                producteurId = if (producteur.isSynced) {
                    producteur.id.toString()
                } else {
                    producteur.uid.toString()
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
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
        selectDechetYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                dechetYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupTraitementProtectionYesNoSelection() {
        selectEquipementProtectionYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                equipementProtectionYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupWCYesNoSelection() {
        selectWCYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                wcYesNo = resources.getStringArray(R.array.YesOrNo)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
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
                    "oui" -> {
                        linearTypeMachinePulContainerMenage.visibility = View.VISIBLE
                        linearMachinePulKeeperContainerMenage.visibility = View.VISIBLE
                        linearTraiteYourselfFarmEquipmentYesNoContainerMenage.visibility = View.VISIBLE
                        linearChampsNoNumberContainerMenage.visibility = View.GONE
                    }
                    "non" -> {
                        linearTypeMachinePulContainerMenage.visibility = View.GONE
                        linearMachinePulKeeperContainerMenage.visibility = View.GONE
                        linearAtomisateurContainerMenage.visibility = View.GONE
                        linearTraiteYourselfFarmEquipmentYesNoContainerMenage.visibility = View.GONE
                        linearChampsNoNumberContainerMenage.visibility = View.VISIBLE
                    }
                    else -> {
                        linearTraiteYourselfFarmEquipmentYesNoContainerMenage.visibility = View.GONE
                        linearTypeMachinePulContainerMenage.visibility = View.GONE
                        linearMachinePulKeeperContainerMenage.visibility = View.GONE
                        linearAtomisateurContainerMenage.visibility = View.GONE
                        linearChampsNoNumberContainerMenage.visibility = View.GONE
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
                    "OUI" -> {
                        linearFemmeActiviteContainerMenage.visibility = View.VISIBLE
                    }
                    "NON" -> {
                        linearFemmeActiviteContainerMenage.visibility = View.GONE
                    }
                    else -> {
                        linearFemmeActiviteContainerMenage.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupDonFemmeYesNoSelection() {
        selectDonCacaoYesNoMenage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                donFemmeCacaoYesNo = resources.getStringArray(R.array.YesOrNo)[position]

                if (donFemmeCacaoYesNo == "oui") {
                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.VISIBLE
                } else {
                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.GONE
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
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
        if (producteursList?.size!! < 1) {
            showMessage("Aucun producteur enregistré !", this, callback = {})
            return
        }

        quartierNom = editQuartierMenage.text?.trim().toString()
        femmeCacaoSuperficie = editSuperficieCacaoMenage.text?.trim().toString()
        donFemmeCacaoSuperficie = editDonSuperficieCacaoMenage.text?.trim().toString()

        traitementHolderNom = editTraiteurMenage.text?.trim().toString()
        femmeActivite = editFemmeActiviteMenage.text?.trim().toString()


        val producteurMenageModel = getProducteurMenageObjet()

        LogUtils.d("VAR MENAGE ACT : "+producteurMenageModel.equipements)

        try {
            val intentMenagePreview = Intent(this, MenagePreviewActivity::class.java)
            intentMenagePreview.putExtra("preview", producteurMenageModel)
            intentMenagePreview.putExtra("draft_id", draftedDataMenage?.uid)
            startActivity(intentMenagePreview)
        } catch (ex: Exception) {
            throw Exception(ex)
        }
    }

    private fun getProducteurMenageObjet(): ProducteurMenageModel {
        return  ProducteurMenageModel(
            uid = 0,
            activiteFemme = femmeActivite,
            boisChauffe = editNbreBoisSemaineMenage.text?.trim().toString(),
            sources_energies_id = sourceEnergie,
            ordures_menageres_id = ordureMenager,
            separationMenage = dechetYesNo,
            eauxToillette = toiletteEau,
            eauxVaisselle = vaisselleEau,
            wc = wcYesNo,
            sources_eaux_id = eauPotable,
            machine = machinePulverisationYesNo,
            type_machines_id = machinePulverisation,
            garde_machines_id = machinePulveKeeper,
            equipements = equipementProtectionYesNo,
            producteurNomPrenoms = producteurNomPrenoms,
            traitementChamps = traitementSelfYesNo,
            nomPersonneTraitant = traitementHolderNom,
            empruntMachine = machineEmpruntYesNo,
            gardeEmpruntMachine = machineEmpruntKeeper,
            champFemme = femmeActiviteYesNo,
            nomActiviteFemme = femmeActivite,
            superficieCacaoFemme = femmeCacaoSuperficie,
            nombreHectareFemme = donFemmeCacaoSuperficie,
            numeroPersonneTraitant = editChampsNoNumeroMenage.text.toString().trim(),
            quartier = quartierNom,
            producteurs_id = producteurId,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
            origin = "local",
            codeProducteur = producteurCode,
            localiteNom = localiteNom
        )
    }


    fun clearFields() {
        producteurNomPrenoms = ""
        producteurId = ""
        quartierNom = ""
        editQuartierMenage.text = null
        sourceEnergie = ""
        selectEnergieMenage.setSelection(0)
        ordureMenager = ""
        selectOrdureMenage.setSelection(0)
        dechetYesNo = ""
        selectDechetYesNoMenage.setSelection(0)
        eauPotable = ""
        toiletteEau = ""
        vaisselleEau = ""
        selectEauToiletteMenage.setSelection(0)
        //selectEauVaisselleMenage.setSelection(0)
        selectEauPotableMenage.setSelection(0)
        selectWCYesNoMenage.setSelection(0)
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
        selectDonCacaoYesNoMenage.setSelection(0)

        editQuartierMenage.requestFocus()

    }


    fun draftMenage(draftModel: DataDraftedModel?) {
        quartierNom = editQuartierMenage.text?.trim().toString()
        femmeCacaoSuperficie = editSuperficieCacaoMenage.text?.trim().toString()
        donFemmeCacaoSuperficie = editDonSuperficieCacaoMenage.text?.trim().toString()

        traitementHolderNom = editTraiteurMenage.text?.trim().toString()
        femmeActivite = editFemmeActiviteMenage.text?.trim().toString()


        val producteurMenageModelDraft = getProducteurMenageObjet()

        LogUtils.d("VAR MENAGE ACT 2 : "+producteurMenageModelDraft.equipements)

        showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(producteurMenageModelDraft),
                        typeDraft = "menage",
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
        val menageUndrafted = ApiClient.gson.fromJson(draftedData.datas, ProducteurMenageModel::class.java)

        //  LogUtils.json(menageUndrafted)

        try {
            // Localite
            val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val localitesDatas: MutableList<CommonData> = mutableListOf()
            localitesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                localitesDatas.addAll(it)
            }
            selectLocaliteMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
            provideDatasSpinnerSelection(
                selectLocaliteMenage,
                menageUndrafted.localiteNom,
                localitesDatas
            )

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
            val energiesLists = CcbRoomDatabase.getDatabase(this)?.sourceEnergieDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val energiesDatas: MutableList<CommonData> = mutableListOf()
            energiesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                energiesDatas.addAll(it)
            }
            selectEnergieMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, energiesDatas)
            provideDatasSpinnerSelection(
                selectEnergieMenage,
                menageUndrafted.sources_energies_id,
                energiesDatas
            )

            // Ordure
            val orduresLists = CcbRoomDatabase.getDatabase(this)?.ordureMenagereDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val orduresDatas: MutableList<CommonData> = mutableListOf()
            orduresLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                orduresDatas.addAll(it)
            }
            selectOrdureMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, orduresDatas)
            provideDatasSpinnerSelection(
                selectOrdureMenage,
                menageUndrafted.ordures_menageres_id,
                orduresDatas
            )

            // Dechets
            provideStringSpinnerSelection(
                selectDechetYesNoMenage,
                menageUndrafted.separationMenage,
                resources.getStringArray(R.array.YesOrNo)
            )

            // Eaux
            val eausLists = CcbRoomDatabase.getDatabase(this)?.eauUseeDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val eausDatas: MutableList<CommonData> = mutableListOf()
            eausLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                eausDatas.addAll(it)
            }
            selectEauToiletteMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, eausDatas)
            selectEauVaisselleMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, eausDatas)

            provideDatasSpinnerSelection(
                selectEauToiletteMenage,
                menageUndrafted.eauxToillette,
                eausDatas
            )

            provideDatasSpinnerSelection(
                selectEauVaisselleMenage,
                menageUndrafted.eauxVaisselle,
                eausDatas
            )

            provideStringSpinnerSelection(
                selectWCYesNoMenage,
                menageUndrafted.wc,
                resources.getStringArray(R.array.YesOrNo)
            )

            // Eaux portable
            val potablesLists = CcbRoomDatabase.getDatabase(this)?.sourceEauDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val potablesDatas: MutableList<CommonData> = mutableListOf()
            potablesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                potablesDatas.addAll(it)
            }
            selectEauPotableMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, potablesDatas)
            provideDatasSpinnerSelection(
                selectEauPotableMenage,
                menageUndrafted.sources_eaux_id,
                potablesDatas
            )

            // champs traiteur
            provideStringSpinnerSelection(
                selectTraitementYesNoMenage,
                menageUndrafted.traitementChamps,
                resources.getStringArray(R.array.YesOrNo)
            )

            // machine
            val machinesLists = CcbRoomDatabase.getDatabase(this)?.typeMachineDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val machinesDatas: MutableList<CommonData> = mutableListOf()
            machinesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                machinesDatas.addAll(it)
            }
            selectMachinePulMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, machinesDatas)
            provideDatasSpinnerSelection(
                selectMachinePulMenage,
                menageUndrafted.type_machines_id,
                machinesDatas
            )

            // garde machine
            val gardeMachinesLists:  MutableList<GardeMachineModel>? =
                AssetFileHelper.getListDataFromAsset(2, this@ProducteurMenageActivity) as MutableList<GardeMachineModel>?
                //CcbRoomDatabase.getDatabase(this)?.gardeMachineDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val gardeMachinesDatas: MutableList<CommonData> = mutableListOf()
            gardeMachinesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                gardeMachinesDatas.addAll(it)
            }
            selectMachineKeeperMenage.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, gardeMachinesDatas)
            provideDatasSpinnerSelection(
                selectMachineKeeperMenage,
                menageUndrafted.garde_machines_id,
                gardeMachinesDatas
            )

            /*provideStringSpinnerSelection(
                selectAtomisateurYesNoMenage,
                menageUndrafted.m
            ) */// Todo fix atomisateur etat

            // Equipement
            provideStringSpinnerSelection(
                selectEquipementProtectionYesNoMenage,
                menageUndrafted.equipements,
                resources.getStringArray(R.array.YesOrNo)
            )

            // femme activite
            provideStringSpinnerSelection(
                selectFemmeActiviteYesNoMenage,
                menageUndrafted.activiteFemme,
                resources.getStringArray(R.array.YesOrNo)
            )

            provideStringSpinnerSelection(
                selectDonCacaoYesNoMenage,
                menageUndrafted.champFemme,
                resources.getStringArray(R.array.YesOrNo)
            )

            editChampsNoNumeroMenage.setText(menageUndrafted.numeroPersonneTraitant)
            editTraiteurMenage.setText(menageUndrafted.nomPersonneTraitant)
            editQuartierMenage.setText(menageUndrafted.quartier)
            editNbreBoisSemaineMenage.setText(menageUndrafted.boisChauffe)
            editFemmeActiviteMenage.setText(menageUndrafted.nomActiviteFemme)
            editSuperficieCacaoMenage.setText(menageUndrafted.superficieCacaoFemme)
        } catch (ex: Exception) {
            throw Exception(ex)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producteur_menage)

        prodMenagereDao = CcbRoomDatabase.getDatabase(this)?.producteurMenageDoa()

        editSuperficieCacaoMenage.doAfterTextChanged {
            try {
                setupDonFemmeYesNoSelection()
                if (it.toString().isEmpty()) {
                    linearDonCacaoFemmeYesNoContainerMenage.visibility = View.GONE
                    linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.GONE
                } else {
                    /*if (data.contains(",")) {
                        data = data.replace(',', '.')
                    }*/
                    if (it.toString().trim().toDouble() == 0.0) {
                        linearDonCacaoFemmeYesNoContainerMenage.visibility = View.VISIBLE
                    } else {
                        linearDonCacaoFemmeYesNoContainerMenage.visibility = View.GONE
                        linearDonCacaoFemmeSuperficieContainerMenage.visibility = View.GONE
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        editDonSuperficieCacaoMenage.doAfterTextChanged {
            try {

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }



        setupEnergiesSelection()

        setupOrduresSelection()

        setupEauToilettesSelection()

        setupEauVaissellesSelection()

        setupSourceEauxSelection()

        setupTypeMachinesSelection()

        setupLocaliteSelection()

        setupDechetYesNoSelection()

        setupTraitementProtectionYesNoSelection()

        setupWCYesNoSelection()

        setupSelfTraitementYesNoSelection()

        setupFemmeActiviteYesNoSelection()

        setupDonFemmeYesNoSelection()

        setupAtomisateurSelection()

        setupGardeMachinesPulSelection()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveMenage.setOnClickListener {
            collectDatas()
        }

        imageDraftBtn.setOnClickListener {
            draftMenage(draftedDataMenage ?: DataDraftedModel(uid = 0))
        }

        //applyFiltersDec(editSuperficieCacaoMenage)
        //applyFiltersDec(editDonSuperficieCacaoMenage)

        if (intent.getStringExtra("from") != null) {
            draftedDataMenage = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataMenage!!)
        }
    }
}
