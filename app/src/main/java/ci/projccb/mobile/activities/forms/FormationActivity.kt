package ci.projccb.mobile.activities.forms

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.FormationPreviewActivity
import ci.projccb.mobile.adapters.ProducteurPresenceAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.configDate
import ci.projccb.mobile.tools.Commons.Companion.configHour
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.FileUtils.getFileExtension
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_formation.*
import kotlinx.android.synthetic.main.activity_producteur.containerPrecisionTitreProducteur
import kotlinx.android.synthetic.main.activity_producteur.imagePhotoProfilProducteur
import kotlinx.android.synthetic.main.activity_producteur.selectTitreDUProducteur

import kotlinx.android.synthetic.main.activity_ssrt_clms.selectRaisonArretEcoleSSrte
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerAnimauxSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerInsecteAmisSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectLocaliteSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectSectionSParcelle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.lang.reflect.TypeVariable
import java.text.SimpleDateFormat
import java.util.*


@SuppressWarnings("ALL")
class FormationActivity : AppCompatActivity() {


    companion object {
        const val TAG = "FormationActivity::class"
    }


    private var producteurIdList: MutableList<CommonData> = arrayListOf()
    private var typeFormationListCm: MutableList<CommonData> = arrayListOf()
    private var themeListCm: MutableList<CommonData> = arrayListOf()
    private var sousThemeListCm: MutableList<CommonData> = arrayListOf()
    private var documentPath: String = ""
    private var endphoto: String = ""
    private var endRapport: String = ""
    private var campagnesList: MutableList<CampagneModel>? = null
    var localiteDao: LocaliteDao? = null
    var typeFormationDao: TypeFormationDao? = null
    var lieuFormationDao: LieuFormationDao? = null
    var themeFormationDao: ThemeFormationDao? = null
    var producteurDao: ProducteurDao? = null
    var formationDao: FormationDao? = null

    var producteursList: MutableList<ProducteurModel>? = null
    var typeFormationsList: MutableList<TypeFormationModel>? = null
    var producteursSelectedList: MutableList<ProducteurModel>? = null
    var localitesList: MutableList<LocaliteModel>? = null
    var lieuxList: MutableList<LieuFormationModel>? = null
    var themesList: MutableList<ThemeFormationModel>? = null
    var themesSelected = mutableListOf<String>()
    var themesIdSelected = mutableListOf<String>()

    var producteurPresenceAda: ProducteurPresenceAdapter? = null

    var typeFormationNom = ""
    var typeFormationId = ""

    var localiteSelected = ""

    var localiteIdSelected = ""
    var producteurNomPrenoms = ""
    var producteurId = ""
    var lieuId = ""
    var lieu = ""
    var dateNaissance = ""

//    var campagneNom = ""
//    var campagneId = ""
    val sectionCommon = CommonData()
    val localiteCommon = CommonData()
    val staffCommon = CommonData()

    var photoPath = ""
    var fileGlobal: File? = null

    val REQUEST_IMAGE_CAPTURE = 10
    val REQUEST_IMAGE_PICKED = 2

    var datePickerDialog: DatePickerDialog? = null
    var draftedDataFormation: DataDraftedModel? = null

    var formationPhotoPath: String = ""
    var whichPhoto = 0


    @Throws(IOException::class)
    private fun createImageFile(fileExtension: String = ""): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        var imageFileName = ""


        when (whichPhoto) {
            0 -> imageFileName = "formation_" + timeStamp + "_"
            1 -> imageFileName = "rapportFormation_" + timeStamp + "_"
        }

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val image = if(whichPhoto == 0) {
            File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )

        }else{
            File.createTempFile(
                imageFileName,  /* prefix */
                "."+fileExtension,  /* suffix */
                storageDir /* directory */
            )
        }

        // Save a file: path for use with ACTION_VIEW intents
        when (whichPhoto) {
            0 -> formationPhotoPath = image.absolutePath
            1 -> documentPath = image.absolutePath
        }

        LogUtils.d(formationPhotoPath, documentPath)

        return image
    }

//    fun setupLocaliteSelection() {
//        localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()
//        localitesList = localiteDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//
//        if (localitesList?.size == 0) {
//            showMessage(
//                getString(R.string.la_liste_des_localit_s_est_vide_refaite_une_mise_jour),
//                this,
//                finished = false,
//                callback = {},
//                getString(R.string.compris),
//                false,
//                showNo = false,
//            )
//
//            localiteIdSelected = ""
//            localiteSelected = ""
//            selectLocaliteFormation?.adapter = null
//
//            return
//        }
//
//        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
//        selectLocaliteFormation!!.adapter = localiteAdapter
//
//        selectLocaliteFormation.setTitle("Choisir la localite")
//
//        selectLocaliteFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val locality = localitesList!![position]
//                localiteSelected = locality.nom!!
//
//                if (locality.isSynced) {
//                    localiteIdSelected = locality.id!!.toString()
//                } else {
//                    localiteIdSelected = locality.uid.toString()
//                }
//
//                //nnsetupProducteurSelection(localiteIdSelected)
//
//                //LogUtils.e(TAG, localiteIdSelected)
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


//    fun setupTypeFormationSelection() {
//        typeFormationDao = CcbRoomDatabase.getDatabase(applicationContext)?.typeFormationDao()
//        typeFormationsList = typeFormationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//
//        val typeFormationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, typeFormationsList!!)
//        selectTypeFormation!!.adapter = typeFormationAdapter
//
//        selectTypeFormation.setTitle("Choisir le type de formation")
//
//        selectTypeFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val typeFormation = typeFormationsList!![position]
//                typeFormationNom = typeFormation.nom!!
//                typeFormationId = typeFormation.id.toString()
//
//                setupThemeFormationSelection(typeFormationId)
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


//    fun setupCampagneSelection() {
//        campagnesList = CcbRoomDatabase.getDatabase(applicationContext)?.campagneDao()?.getAll()
//
//        val campagneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList!!)
//        selectCampagneFormation!!.adapter = campagneAdapter
//        selectCampagneFormation.setTitle("Choisir la campagne")
//
//        selectCampagneFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                val campagne = campagnesList!![position]
//                campagne.campagnesNom = campagne.campagnesNom!!
//                campagne.id = campagne.id
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }


    fun setupLieuSelection() {
        //lieuFormationDao = CcbRoomDatabase.getDatabase(applicationContext)?.lieuFormationDoa()
        lieuxList = AssetFileHelper.getListDataFromAsset(4, this@FormationActivity) as MutableList<LieuFormationModel>?
            //lieuFormationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        val lieuAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lieuxList!!)
        selectLieuFormation!!.adapter = lieuAdapter

        selectLieuFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val lieuFormation = lieuxList!![position]
                lieu = lieuFormation.nom!!
                lieuId = lieuFormation.id.toString()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


//    fun setupThemeFormationSelection(type: String) {
//        themesList?.clear()
//        //themeFormationDao = CcbRoomDatabase.getDatabase(applicationContext)?.themeFormationDao()
//        themesList = AssetFileHelper.getListDataFromAsset(9, this@FormationActivity) as MutableList<ThemeFormationModel>?
//            //themeFormationDao?.getAllByType(type)
//
//        val newList = mutableListOf<ThemeFormationModel>()
//         themesList?.map {
//            if(it?.typeFormationsId.toString().equals(type))
//                newList.add(it)
//        }
//        themesList?.clear()
//        themesList?.addAll(newList)
//            LogUtils.d(themesList)
//
//        var listSelectLegersResources = mutableListOf<Int>()
//        val listofTtitles = mutableListOf<String>()
//        listofTtitles.addAll(themesList?.map { "${it.nom}" } ?: arrayListOf())
//
//        val multiSelectSpinner = selectThemeFormation
//        multiSelectSpinner.setTitle("Choisir les themes")
//        multiSelectSpinner.setItems(listofTtitles)
//        //multiSelectSpinner.hasNoneOption(true)
//        multiSelectSpinner.setSelection(listSelectLegersResources.toIntArray())
//        multiSelectSpinner.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
//            override fun selectedIndices(indices: MutableList<Int>?) {
//                //themesIdSelected.clear()
//
//            }
//
//            override fun selectedStrings(strings: MutableList<String>?) {
//                themesSelected.clear()
//                themesSelected.addAll(strings?.toMutableList() ?: arrayListOf())
//                themesIdSelected.clear()
//                var listIds = mutableListOf<String>()
//                strings?.forEach {
//                    for (theme in themesList?: arrayListOf()){
//                        if(theme.nom.equals(it)) listIds.add(theme.id.toString())
//                    }
//                }
//                themesIdSelected.addAll(listIds)
//            }
//
//        })
//    }


//    fun setupProducteurSelection(localite: String?) {
//        producteursList?.clear()
//        producteursList = producteurDao?.getProducteursByLocalite(localite = localite)
//
//        val producteursAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursList!!)
//        selectListePresenceFormation!!.adapter = producteursAdapter
//
//        producteursList?.add(0, ProducteurModel(uid = 0, nom = "", prenoms = "", id = 0))
//
//        selectListePresenceFormation.setTitle("Choisir le producteur")
//
//        selectListePresenceFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                if (position > 0) {
//                    val producteur = producteursList!![position]
//                    producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"
//
//                    producteurId = if (producteur.isSynced) {
//                        producteur.id!!.toString()
//                    } else {
//                        producteur.uid.toString()
//                    }
//
//                    producteursSelectedList?.add(producteur)
//                    producteurPresenceAda?.notifyDataSetChanged()
//                } else {
//                    ToastUtils.showShort("Choisir un producteur !")
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
////                selectProducteu.setSelection(0)
////                producteurId = (selectProducteurParcelle.selectedItem as ProducteurModel).id.toString()
//            }
//        }
//    }

    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {
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
            spinner = selectSectionFormation,
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
                if(it.id.toString() == idc.toString()) libItem = it.nom
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_de_la_localit),
            getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectLocaliteFormation,
            listIem = localitesListi?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                localitesListi?.let { list ->
                    var localite = list.get(it)
                    localiteCommon.nom = localite.nom!!
                    localiteCommon.id = localite.id!!

                    if(intent.getStringExtra("from") == null){
                        var producteurList = CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.getProducteursByLocalite(localite.id.toString())?.map { if(it.isSynced) CommonData(it.id, "${it.nom} ${it.prenoms}") else CommonData(it.uid, "${it.nom} ${it.prenoms}") }!!

                        Commons.setupItemMultiSelection(this, selectProducteurFormation,
                            getString(R.string.quels_sont_les_producteurs_pr_sents_la_formation),
                            producteurList
                        ){ selected ->

                            producteurList?.forEach { product ->
                                if( selected.contains(product.nom) ){
                                    producteurIdList.add(CommonData(product.id, product.nom.toString()))
                                }
                            }
                            //if(it.contains("Autre")) containerAutreRaisonArretEcole.visibility = View.VISIBLE
                        }
                    }
                }


            },
            onSelected = { itemId, visibility ->

            })

    }


    fun setAllListener() {

        setupSectionSelection()

        setupLieuSelection()

        //setupCampagneSelection()

        //setupTypeFormationSelection()

//        Commons.setListenerForSpinner(this,
//            "Quel est le pilier ?",
//            spinner = selectPilierFormation,
//            listIem = resources.getStringArray(R.array.example_pilier)
//                ?.toList() ?: listOf(),
//            onChanged = {
//            },
//            onSelected = { itemId, visibility ->
//            })

        Commons.setListenerForSpinner(this,
            getString(R.string.lieu_de_la_formation),
            spinner = selectLieuFormation,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = resources.getStringArray(R.array.lieuDeFormation)?.toList() ?: listOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreLieuFormation.visibility = visibility
//                }
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.type_de_formation),
            spinner = selectTypeFormation,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = resources.getStringArray(R.array.type_formation)?.toList() ?: listOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
//                if (itemId == 1) {
//                    containerAutreLieuFormation.visibility = visibility
//                }
            })

        val listTypeFormation = CcbRoomDatabase.getDatabase(this)?.typeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listThemeFormation = CcbRoomDatabase.getDatabase(this)?.themeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listSousThemeFormation = CcbRoomDatabase.getDatabase(this)?.sousThemeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        Commons.setupItemMultiSelection(this, selectModuleMultiFormation, getString(R.string.quels_sont_les_modules_de_la_formation),
            (listTypeFormation)?.map { CommonData(0, it.nom.toString()) }?: arrayListOf()
        ){ typeSelect ->
            //val idOfTypeForm = mutableListOf<Int?>()
            val listThemeFormationCustom = mutableListOf<ThemeFormationModel>()
            //LogUtils.json(listThemeFormation)
            typeFormationListCm.clear()
            listTypeFormation?.forEach { typeForm ->
                if( typeSelect.contains(typeForm.nom) ){
                    listThemeFormationCustom.addAll(listThemeFormation?.filter {it.typeFormationsId === typeForm.id}?.toMutableList()?: arrayListOf())
                    typeFormationListCm.add(CommonData(typeForm.id, typeForm.nom.toString()))
                }
            }

            Commons.setupItemMultiSelection(this, selectThemeMultiFormation, getString(R.string.quels_sont_les_themes_de_la_formation),
                (listThemeFormationCustom).map { CommonData(0, "${it.nom}") }){ themeList ->

                val listSousThemeFormationCustom = mutableListOf<SousThemeFormationModel>()
                themeListCm.clear()
                listThemeFormation?.forEach { themeForm ->
                    if( themeList.contains(themeForm.nom) ){
                        listSousThemeFormationCustom.addAll(listSousThemeFormation?.filter {it.themeFormationsId === themeForm.id}?.toMutableList()?: arrayListOf())
                        themeListCm.add(CommonData(themeForm.id, themeForm.nom.toString(), value = "${themeForm.typeFormationsId}-${themeForm.id}"))
                    }
                }

                Commons.setupItemMultiSelection(this, selectSousThemeMultiFormation, getString(R.string.quels_sont_les_sous_themes_de_la_formation),
                    (listSousThemeFormationCustom).map { CommonData(0, "${it.nom}") }){
                    listSousThemeFormation?.forEach { sThemeForm ->
                        if( it.contains(sThemeForm.nom) ){
                            sousThemeListCm.add(CommonData(sThemeForm.id, sThemeForm.nom.toString(), value = "${sThemeForm.themeFormationsId}-${sThemeForm.id}"))
                        }
                    }
                }

            }
        }

        Commons.setupItemMultiSelection(this, selectThemeMultiFormation, getString(R.string.quels_sont_les_themes_de_la_formation),
            arrayListOf()
        ){
        }

        Commons.setupItemMultiSelection(this, selectSousThemeMultiFormation, getString(R.string.quels_sont_les_sous_themes_de_la_formation),
            arrayListOf()){
        }

        val listDelegue = CcbRoomDatabase.getDatabase(this)?.staffFormation()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        Commons.setListenerForSpinner(this,
            getString(R.string.quel_est_le_staff_qui_a_dispens_la_formation),
            spinner = selectStaffFormation,
            listIem = listDelegue?.map { "${it.firstname} ${it.lastname}" }
                ?.toList() ?: listOf(),
            onChanged = {
                staffCommon.nom = listDelegue?.get(it)?.let{
                    "${it.firstname} ${it.lastname}"
                }
                staffCommon.id = listDelegue?.get(it)?.id!!
            },
            onSelected = { itemId, visibility ->
            })

//        Commons.setupItemMultiSelection(this, selectStaffFormation, "Selectionner les délégués présent à la formation !",
//            (CcbRoomDatabase.getDatabase(this)?.delegueDao()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()))?.map { CommonData(0, "${it.nom}") }
//                ?: arrayListOf()){
//            //if(it.contains("Autre")) containerAutreRaisonArretEcole.visibility = View.VISIBLE
//        }

        // setupThemeFormationSelection()
    }


    fun collectDatas() {

        if (editDateDebuFormation.text.isNullOrEmpty() &&  editDateFinFormation.text.isNullOrEmpty()) {
            Commons.showMessage(
                message = getString(R.string.la_date_de_formation_n_est_pas_renseign_e),
                context = this,
                finished = false,
                callback = {},
                deconnec = false,
                showNo = false
            )
            return
        }

        val itemModelOb = getFormationObjet()

        if(itemModelOb == null) return

        val formationModel = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localitesId = localiteCommon.id.toString()
                staffId = staffCommon.id.toString()

                producteursIdStr = GsonUtils.toJson(producteurIdList.map { it.id.toString() }.toMutableList())
                typeFormationStr = GsonUtils.toJson(typeFormationListCm?.map { it.id.toString() }.toMutableList())
                themeStr = GsonUtils.toJson(themeListCm.map { it.value.toString() }.toMutableList())
                sousThemeStr = GsonUtils.toJson(sousThemeListCm.map { it.value.toString() }.toMutableList())

                photoFormation = endphoto
                rapportFormation = endRapport

            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {

            this.add(Pair("Les participants", producteurIdList.map { "${it.nom}" }.toModifString(commaReplace = "\n")))
            this.add(Pair("Le nombre total de participant", producteurIdList.size.toString()))
            this.add(Pair(getString(R.string.les_types_de_formation), typeFormationListCm.map { "${it.nom}" }.toModifString(commaReplace = "\n") ))
            this.add(Pair(getString(R.string.les_themes), themeListCm.map { "${it.nom}" }.toModifString(commaReplace = "\n")))
            this.add(Pair(getString(R.string.les_sous_themes), sousThemeListCm.map { "${it.nom}" }.toModifString(commaReplace = "\n")))

            if(endRapport.isNullOrEmpty()) this.add(Pair(getString(R.string.le_rapport),
                getString(R.string.aucun_rapport_n_est_fourni)))
            else this.add(Pair(getString(R.string.le_rapport), getString(R.string.un_rapport_est_fourni)))

        }.map { MapEntry(it.first, it.second) }

        //Commons.printModelValue(formationModel as Object, mapEntries)

        try {
            val intentFormationPreview = Intent(this, FormationPreviewActivity::class.java)
            intentFormationPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentFormationPreview.putExtra("preview", formationModel)
            intentFormationPreview.putExtra("draft_id", draftedDataFormation?.uid)
            startActivity(intentFormationPreview)
        } catch (ex: Exception) {
            ex.toString()
        }
    }

    private fun getFormationObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<FormationModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false
//        return FormationModel(
////            dateFormation = editDateFormation.text?.trim().toString(),
//            lieuFormationsId = lieu,
//            isSynced = false,
//            typeFormationId = typeFormationId,
//            //theme = editThemeFormation.text?.trim().toString(),
//            themesLabelStringify = ApiClient.gson.toJson(themesSelected),
//            themeStringify = ApiClient.gson.toJson(themesIdSelected),
//            localitesId = localiteIdSelected,
//            usersId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
//            uid = 0,
//            visiteurs = editVisiteursFormation.text?.trim().toString(),
//            origin = "local",
//            campagneNom = campagneNom,
//            campagneId = campagneId.toInt(),
//            lieuFormationNom = lieu,
//            localiteNom = localiteSelected,
//            themeNom = "",
//            photoPath = photoPath,
//        )
        var itemList = getSetupFormationModel(
            FormationModel(
                uid = 0,
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

    fun getSetupFormationModel(
        prodModel: FormationModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<FormationModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_formation)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupFormationModel(
        prodModel: FormationModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_formation)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }


    fun clearFields() {
        //setAllListener()

//        editDateFormation.text = null
//        editThemeFormation.text = null
//        editVisiteursFormation.text = null

//        selectLocaliteFormation.setSelection(0)
//        selectLieuFormation.setSelection(0)
//        //selectListePresenceFormation.setSelection(0)
//
//        producteursSelectedList?.clear()
//        producteursList?.clear()
//        producteurPresenceAda?.notifyDataSetChanged()

//        editThemeFormation.requestFocus()
    }


    private fun createImageFileCompressed() {
        var compressedFile: File? = null
        try {
            fileGlobal = File(photoPath)

            CoroutineScope(Dispatchers.IO).launch {
                compressedFile = Compressor.compress(this@FormationActivity, fileGlobal!!) {
                    quality(75)
                    format(Bitmap.CompressFormat.JPEG)
                }
            }

            if (FileUtils.isFileExists(compressedFile)) {
                val finalCompressedFile = compressedFile
                FileUtils.copy(compressedFile, fileGlobal) { srcFile, destFile ->
                    SPUtils.getInstance().put("formation_",
                        Commons.encodeFileToBase64Binary(finalCompressedFile)
                    )
                    true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            LogUtils.e("ERREUR -> enter here")
        }
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var carnetFile: File? = null
        try {
            carnetFile = createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        // Continue only if the File was successfully created
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                if (carnetFile != null) {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "ci.projccb.mobile.fileprovider",
                        carnetFile
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun testImageTaked(bundleData: Uri?, fileExtension: String = "") {
        // Get the dimensions of the View
        try {
            val options = BitmapFactory.Options()
            options.inSampleSize = 8
            //LogUtils.d((bundleData == null), whichPhoto)
            if (bundleData == null) {
                when(whichPhoto){
                    0 -> {
                        endphoto = formationPhotoPath
                        imagePhotoFormation.setImageBitmap(
                            BitmapFactory.decodeFile(
                                formationPhotoPath,
                                options
                            )
                        )
                    }
                }
            } else {
                options.inJustDecodeBounds = true
                options.inPurgeable = true
                when(whichPhoto){
                    0 -> {
                        Commons.copyFile(bundleData, (formationPhotoPath), this@FormationActivity)
                    }
                    1 -> {
                        Commons.copyFile(bundleData, (documentPath), this@FormationActivity)
                    }
                }

                //documentPath = UriUtils.uri2File(bundleData).path
                when(whichPhoto){
                    0 -> {
                        endphoto = formationPhotoPath
                        imagePhotoFormation.setImageURI(bundleData)
                    }
                    1 -> {
                        endRapport = documentPath
                        imageRapportFormation.setImageResource(R.drawable.document_file_download_done)
                    }
                }
//                LogUtils.e(FormationActivity.TAG, formationPhotoPath)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }


    fun draftFormation(draftModel: DataDraftedModel?) {

        val itemModelOb = getFormationObjet(false)

        if(itemModelOb == null) return

        val formationModelDraft = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                localitesId = localiteCommon.id.toString()
                staffId = staffCommon.id.toString()

                producteursIdStr = GsonUtils.toJson(producteurIdList.map { it.id.toString() }.toMutableList())
                typeFormationStr = GsonUtils.toJson(typeFormationListCm?.map { it.id.toString() }.toMutableList())
                themeStr = GsonUtils.toJson(themeListCm.map { it.value.toString() }.toMutableList())
                sousThemeStr = GsonUtils.toJson(sousThemeListCm.map { it.value.toString() }.toMutableList())

                photoFormation = endphoto
                rapportFormation = endRapport
            }
        }

        Commons.showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(formationModelDraft),
                        typeDraft = "formation",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                Commons.showMessage(
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
        val formationDrafted = ApiClient.gson.fromJson(draftedData.datas, FormationModel::class.java)

        setupSectionSelection(formationDrafted.section, formationDrafted.localitesId)

        var producteurList = CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.getProducteursByLocalite(formationDrafted.localitesId.toString())?.map { if(it.isSynced) CommonData(it.id, "${it.nom} ${it.prenoms}") else CommonData(it.uid, "${it.nom} ${it.prenoms}") }!!
        var selectStr: MutableList<String> = GsonUtils.fromJson(formationDrafted.producteursIdStr, object : TypeToken<MutableList<String>>(){}.type)
        var selectProd = producteurList.filter { selectStr.contains(it.id.toString()) == true }.toMutableList()
        LogUtils.d(selectStr, selectProd)
        Commons.setupItemMultiSelection(this, selectProducteurFormation,
            getString(R.string.quels_sont_les_producteurs_pr_sents_la_formation),
            producteurList,
            currentList = selectProd.map { "${it.nom}" }.toMutableList()
        ){ selected ->

            producteurList?.forEach { product ->
                if( selected.contains(product.nom) ){
                    producteurIdList.add(CommonData(product.id, product.nom.toString()))
                }
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.lieu_de_la_formation),
            spinner = selectLieuFormation,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            currentVal = formationDrafted.lieuFormation,
            listIem = resources.getStringArray(R.array.lieuDeFormation)?.toList() ?: listOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.type_de_formation),
            spinner = selectTypeFormation,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            currentVal = formationDrafted.formationType,
            listIem = resources.getStringArray(R.array.type_formation)?.toList() ?: listOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
            })

        val listTypeFormation = CcbRoomDatabase.getDatabase(this)?.typeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        var currentZip = GsonUtils.fromJson<MutableList<String>>(formationDrafted.typeFormationStr, object : TypeToken<MutableList<String>>(){}.type)
        val currentTypeList = listTypeFormation?.filter { currentZip.contains(it.id.toString()) == true }
        val listThemeFormation = CcbRoomDatabase.getDatabase(this)?.themeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        var currentZip2 = GsonUtils.fromJson<MutableList<String>>(formationDrafted.themeStr, object : TypeToken<MutableList<String>>(){}.type)
        val currentThemeList = listThemeFormation?.filter { currentZip2.map { it.split("-")[1] }.contains(it.id.toString()) == true }
        val listSousThemeFormation = CcbRoomDatabase.getDatabase(this)?.sousThemeFormationDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        var currentZip3 = GsonUtils.fromJson<MutableList<String>>(formationDrafted.sousThemeStr, object : TypeToken<MutableList<String>>(){}.type)
        val currentSousThemeList = listSousThemeFormation?.filter { currentZip3.map { it.split("-")[1] }.contains(it.id.toString()) == true }
        Commons.setupItemMultiSelection(this, selectModuleMultiFormation,
            getString(R.string.quels_sont_les_modules_de_la_formation),
            (listTypeFormation)?.map { CommonData(0, it.nom.toString())
            }?: arrayListOf(),
            currentList = currentTypeList?.map { "${ it.nom }" }?.toMutableList()?: arrayListOf()
        ){ typeSelect ->

            val listThemeFormationCustom = mutableListOf<ThemeFormationModel>()

            typeFormationListCm.clear()
            listTypeFormation?.forEach { typeForm ->
                if( typeSelect.contains(typeForm.nom) ){
                    listThemeFormationCustom.addAll(listThemeFormation?.filter {it.typeFormationsId === typeForm.id}?.toMutableList()?: arrayListOf())
                    typeFormationListCm.add(CommonData(typeForm.id, typeForm.nom.toString()))
                }
            }

            Commons.setupItemMultiSelection(this, selectThemeMultiFormation,
                getString(R.string.quels_sont_les_themes_de_la_formation),
                (listThemeFormationCustom).map { CommonData(0, "${it.nom}") },
                currentList = currentThemeList?.map { "${ it.nom }" }?.toMutableList()?: arrayListOf()
            ){ themeList ->

                val listSousThemeFormationCustom = mutableListOf<SousThemeFormationModel>()
                themeListCm.clear()
                listThemeFormation?.forEach { themeForm ->
                    if( themeList.contains(themeForm.nom) ){
                        listSousThemeFormationCustom.addAll(listSousThemeFormation?.filter {it.themeFormationsId === themeForm.id}?.toMutableList()?: arrayListOf())
                        themeListCm.add(CommonData(themeForm.id, themeForm.nom.toString(), value = "${themeForm.typeFormationsId}-${themeForm.id}"))
                    }
                }

                Commons.setupItemMultiSelection(this, selectSousThemeMultiFormation,
                    getString(R.string.quels_sont_les_sous_themes_de_la_formation),
                    (listSousThemeFormationCustom).map { CommonData(0, "${it.nom}") },
                    currentList = currentSousThemeList?.map { "${ it.nom }" }?.toMutableList()?: arrayListOf()){
                    listSousThemeFormation?.forEach { sThemeForm ->
                        if( it.contains(sThemeForm.nom) ){
                            sousThemeListCm.add(CommonData(sThemeForm.id, sThemeForm.nom.toString(), value = "${sThemeForm.themeFormationsId}-${sThemeForm.id}"))
                        }
                    }
                }

            }
        }

        val listThemeFormationCustom = mutableListOf<ThemeFormationModel>()
        typeFormationListCm.clear()
        listTypeFormation?.forEach { typeForm ->
            if( currentTypeList?.map { "${it.nom}" }?.contains(typeForm.nom) == true ){
                listThemeFormationCustom.addAll(listThemeFormation?.filter {it.typeFormationsId === typeForm.id}?.toMutableList()?: arrayListOf())
                typeFormationListCm.add(CommonData(typeForm.id, typeForm.nom.toString()))
            }
        }
        Commons.setupItemMultiSelection(this, selectThemeMultiFormation,
            getString(R.string.quels_sont_les_themes_de_la_formation),
            (listThemeFormationCustom).map { CommonData(0, "${it.nom}") },
            currentList = currentThemeList?.map { "${ it.nom }" }?.toMutableList()?: arrayListOf()
        ){ themeList ->



        }

        val listSousThemeFormationCustom = mutableListOf<SousThemeFormationModel>()
        themeListCm.clear()
        listThemeFormation?.forEach { themeForm ->
            if( currentThemeList?.map { "${it.nom}" }?.contains(themeForm.nom) == true){
                listSousThemeFormationCustom.addAll(listSousThemeFormation?.filter {it.themeFormationsId === themeForm.id}?.toMutableList()?: arrayListOf())
                themeListCm.add(CommonData(themeForm.id, themeForm.nom.toString(), value = "${themeForm.typeFormationsId}-${themeForm.id}"))
            }
        }
        Commons.setupItemMultiSelection(this, selectSousThemeMultiFormation,
            getString(R.string.quels_sont_les_sous_themes_de_la_formation),
            (listSousThemeFormationCustom).map { CommonData(0, "${it.nom}") },
            currentList = currentSousThemeList?.map { "${ it.nom }" }?.toMutableList()?: arrayListOf()){
            listSousThemeFormation?.forEach { sThemeForm ->
                if( it.contains(sThemeForm.nom) ){
                    sousThemeListCm.add(CommonData(sThemeForm.id, sThemeForm.nom.toString(), value = "${sThemeForm.themeFormationsId}-${sThemeForm.id}"))
                }
            }
        }

        val listDelegue = CcbRoomDatabase.getDatabase(this)?.staffFormation()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        Commons.setListenerForSpinner(this,
            getString(R.string.quel_est_le_staff_qui_a_dispens_la_formation),
            spinner = selectStaffFormation,
            currentVal = listDelegue?.filter { formationDrafted.staffId == it.id.toString() }?.map { "${it.firstname} ${it.lastname}" }?.firstOrNull() ?: "",
            listIem = listDelegue?.map { "${it.firstname} ${it.lastname}" }
                ?.toList() ?: listOf(),
            onChanged = {
                staffCommon.nom = listDelegue?.get(it)?.let {
                    "${it.firstname} ${it.lastname}"
                }!!
                staffCommon.id = listDelegue?.get(it)?.id!!
            },
            onSelected = { itemId, visibility ->
            })

        passSetupFormationModel(formationDrafted)
    }


    private fun showFileChooser(pView: Int, typeFile:String = "application/pdf", fileExtension: String = "") {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Set a general MIME type to allow any file type
        // Add specific MIME types for images, Excel, Word, and PDF
        if(whichPhoto == 1){
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                typeFile  // PDF
            ))
        }else if(whichPhoto == 0) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "image/*",  // Excel (xlsx)
            ))
        }

        if (intent.resolveActivity(packageManager) != null) {

        } else {
            LogUtils.e(ProducteurActivity.TAG, "Error launcher photo")
        }

        var carnetFile: File? = null
        try {
            carnetFile = createImageFile(fileExtension)
        } catch (ex: IOException) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

        // Continue only if the File was successfully created
        if (intent.resolveActivity(packageManager) != null) {
            try {
                if (carnetFile != null) {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "ci.projccb.mobile.fileprovider",
                        carnetFile
                    )

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    when(whichPhoto){
                        0 -> {
                            startActivityForResult(Intent.createChooser(intent,
                                getString(R.string.selectionnez_la_photo)), pView)
                        }
                        1 -> {
                            startActivityForResult(Intent.createChooser(intent,
                                getString(R.string.selectionnez_le_document)), pView)
                        }
                    }
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LogUtils.d(ProducteurActivity.TAG, "OKOKOKOK")

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE)  {
                testImageTaked(null)
            } else {

                var fileExtension = ""
                testImageTaked(data?.data, fileExtension)
            }
        } else {
            showMessage(
                getString(R.string.aucune_photo_selectionn_e),
                context = this,
                finished = false,
                callback = {},
                positive = getString(R.string.compris),
                deconnec = false,
                showNo = false
            )
        }
    }

    fun dialogPickerPhoto() {
        val dialogPicker = AlertDialog.Builder(this, R.style.DialogTheme)
        Commons.adjustTextViewSizesInDialog(this, dialogPicker, "",   this.resources.getDimension(R.dimen._6ssp)
            ,true)
        if(whichPhoto == 0){
            dialogPicker.setMessage(getString(R.string.source_de_la_photo))
                .setPositiveButton("Camera") { dialog, _ ->
                    dialog.dismiss()
                    dispatchTakePictureIntent()
                }
                .setNegativeButton(getString(R.string.gallerie)) { dialog, _ ->
                    dialog.dismiss()
                    showFileChooser(11)
                }
        }else if(whichPhoto == 1){
            dialogPicker.setMessage(getString(R.string.source_du_rapport))
                .setPositiveButton(getString(R.string.importer_csv)) { dialog, _ ->
                    dialog.dismiss()
                    showFileChooser(11, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // CSV
                        "xlsx")
                }
                .setNegativeButton(getString(R.string.importer_pdf)) { dialog, _ ->
                    dialog.dismiss()
                    showFileChooser(11, "application/pdf",  // PDF
                        "pdf")
                }
                .setNeutralButton(getString(R.string.importer_doc)) { dialog, _ ->
                    dialog.dismiss()
                    showFileChooser(11, "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // PDF
                        "docx")
                }
        }
        val alerte = dialogPicker.create()

        alerte.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formation)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        formationDao = CcbRoomDatabase.getDatabase(applicationContext)?.formationDao()
        producteurDao = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()

        clickCancelFormation.setOnClickListener {
//            clearFields()
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        clickSaveFormation.setOnClickListener {
            collectDatas()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageDraftBtn.setOnClickListener {
            draftFormation(draftedDataFormation ?: DataDraftedModel(uid = 0))
        }

        setOtherListener()

        if (intent.getStringExtra("from") != null) {
            draftedDataFormation = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataFormation!!)
        }else{
            setAllListener()
        }
    }

    private fun setOtherListener() {

        imagePhotoFormation.setOnClickListener {
            whichPhoto = 0
            dialogPickerPhoto()
        }

        imageRapportFormation.setOnClickListener {
            whichPhoto = 1
            dialogPickerPhoto()
        }

        editDateDebuFormation.setOnClickListener { configDate(editDateDebuFormation, false) }
        editDateFinFormation.setOnClickListener { configDate(editDateFinFormation, false) }
        editDureeFormation.setOnClickListener { configHour(editDureeFormation) }

    }
}
