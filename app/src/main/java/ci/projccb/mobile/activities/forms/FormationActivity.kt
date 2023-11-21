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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner
import ci.projccb.mobile.activities.infospresenters.FormationPreviewActivity
import ci.projccb.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.adapters.OnlyFieldAdapter
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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_formation.*
import kotlinx.android.synthetic.main.activity_producteur.containerPrecisionTitreProducteur
import kotlinx.android.synthetic.main.activity_producteur.imagePhotoProfilProducteur
import kotlinx.android.synthetic.main.activity_producteur.selectTitreDUProducteur
import kotlinx.android.synthetic.main.activity_ssrt_clms.containerAutreRaisonArretEcole
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
import java.text.SimpleDateFormat
import java.util.*


@SuppressWarnings("ALL")
class FormationActivity : AppCompatActivity() {


    companion object {
        const val TAG = "FormationActivity::class"
    }


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
    val campagne = CommonData()
    val localite = CommonData()
    val sectionCommon = CommonData()
    val localiteCommon = CommonData()

    var photoPath = ""
    var fileGlobal: File? = null

    val REQUEST_IMAGE_CAPTURE = 10
    val REQUEST_IMAGE_PICKED = 2

    var datePickerDialog: DatePickerDialog? = null
    var draftedDataFormation: DataDraftedModel? = null

    var formationPhotoPath: String = ""
    var whichPhoto = 0


    @Throws(IOException::class)
    private fun createImageFile(): File? {
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
                "",  /* suffix */
                storageDir /* directory */
            )
        }

        // Save a file: path for use with ACTION_VIEW intents
        when (whichPhoto) {
            0 -> formationPhotoPath = image.absolutePath
            1 -> documentPath = image.absolutePath
        }

        return image
    }

//    fun setupLocaliteSelection() {
//        localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()
//        localitesList = localiteDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//
//        if (localitesList?.size == 0) {
//            showMessage(
//                "La liste des localités est vide ! Refaite une mise à jour.",
//                this,
//                finished = false,
//                callback = {},
//                "Compris !",
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


    fun setupTypeFormationSelection() {
        typeFormationDao = CcbRoomDatabase.getDatabase(applicationContext)?.typeFormationDao()
        typeFormationsList = typeFormationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        val typeFormationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, typeFormationsList!!)
        selectTypeFormation!!.adapter = typeFormationAdapter

        selectTypeFormation.setTitle("Choisir le type de formation")

        selectTypeFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val typeFormation = typeFormationsList!![position]
                typeFormationNom = typeFormation.nom!!
                typeFormationId = typeFormation.id.toString()

                setupThemeFormationSelection(typeFormationId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupCampagneSelection() {
        campagnesList = CcbRoomDatabase.getDatabase(applicationContext)?.campagneDao()?.getAll()

        val campagneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList!!)
        selectCampagneFormation!!.adapter = campagneAdapter
        selectCampagneFormation.setTitle("Choisir la campagne")

        selectCampagneFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val campagne = campagnesList!![position]
                campagne.campagnesNom = campagne.campagnesNom!!
                campagne.id = campagne.id
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


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


    fun setupThemeFormationSelection(type: String) {
        themesList?.clear()
        //themeFormationDao = CcbRoomDatabase.getDatabase(applicationContext)?.themeFormationDao()
        themesList = AssetFileHelper.getListDataFromAsset(9, this@FormationActivity) as MutableList<ThemeFormationModel>?
            //themeFormationDao?.getAllByType(type)

        val newList = mutableListOf<ThemeFormationModel>()
         themesList?.map {
            if(it?.typeFormationsId.toString().equals(type))
                newList.add(it)
        }
        themesList?.clear()
        themesList?.addAll(newList)
            LogUtils.d(themesList)

        var listSelectLegersResources = mutableListOf<Int>()
        val listofTtitles = mutableListOf<String>()
        listofTtitles.addAll(themesList?.map { "${it.nom}" } ?: arrayListOf())

        val multiSelectSpinner = selectThemeFormation
        multiSelectSpinner.setTitle("Choisir les themes")
        multiSelectSpinner.setItems(listofTtitles)
        //multiSelectSpinner.hasNoneOption(true)
        multiSelectSpinner.setSelection(listSelectLegersResources.toIntArray())
        multiSelectSpinner.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
            override fun selectedIndices(indices: MutableList<Int>?) {
                //themesIdSelected.clear()

            }

            override fun selectedStrings(strings: MutableList<String>?) {
                themesSelected.clear()
                themesSelected.addAll(strings?.toMutableList() ?: arrayListOf())
                themesIdSelected.clear()
                var listIds = mutableListOf<String>()
                strings?.forEach {
                    for (theme in themesList?: arrayListOf()){
                        if(theme.nom.equals(it)) listIds.add(theme.id.toString())
                    }
                }
                themesIdSelected.addAll(listIds)
            }

        })
    }


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
                if(it.id == idc.toInt()) libItem = it.libelle
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la section !",
            "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
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
                if(it.id == idc.toInt()) libItem = it.nom
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la localité !",
            "La liste des localités semble vide, veuillez procéder à la synchronisation des données svp.",
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

                }


            },
            onSelected = { itemId, visibility ->

            })

    }


    fun setAllListener() {

        setupSectionSelection()

        setupLieuSelection()

        setupCampagneSelection()

        setupTypeFormationSelection()

        Commons.setListenerForSpinner(this,
            "Quel est le pilier ?",
            spinner = selectPilierFormation,
            listIem = resources.getStringArray(R.array.example_pilier)
                ?.toList() ?: listOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            "Lieu de la formation",
            spinner = selectLieuFormation,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(
                4,
                this
            ) as MutableList<LieuFormationModel>)?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerAutreLieuFormation.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Quel est le module de la formation ?",
            spinner = selectLieuFormation,
            listIem = resources.getStringArray(R.array.entity_example).map { it.toString() }
                ?.toList() ?: listOf(),
            onChanged = {
                Commons.setupItemMultiSelection(this, selectThemeFormation,
                    "Quel est le theme de la formation ?",
                    (AssetFileHelper.getListDataFromAsset(
                        9,
                        this
                    ) as MutableList<ThemeFormationModel>)?.map { CommonData(0, it.toString()) }
                ){

                }

            },
            onSelected = { itemId, visibility ->

            })


        // setupThemeFormationSelection()
    }


    fun collectDatas() {
        if (themesSelected.isEmpty()) {
            Commons.showMessage(
                message = "Aucun theme n'est selectionné !",
                context = this,
                finished = false,
                callback = {},
                deconnec = false,
                showNo = false
            )
            return
        }

        if (editDateDebuFormation.text.isNullOrEmpty() &&  editDateFinFormation.text.isNullOrEmpty()) {
            Commons.showMessage(
                message = "La date de formation n'est pas renseignée !",
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
                localitesId = localite.id.toString()
                campagneId = campagne.id
                photoFormation = formationPhotoPath
                rapportFormation = endRapport
                photoFormation = photoFormation

                themeStringify = GsonUtils.toJson(themesSelected)
                //itemsStr = GsonUtils.toJson((recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { ArbreData(null, it.variete, it.nombre) })

//                insectesParasitesTemp = GsonUtils.toJson((recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
//                nombreInsectesParasitesTemp = GsonUtils.toJson((recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })

//                insectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.variete })
//                nombreinsectesAmisStr = GsonUtils.toJson((recyclerInsecteAmisSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { it.nombre })
//
//                animauxRencontresStringify = GsonUtils.toJson((recyclerAnimauxSuiviParcelle.adapter as OnlyFieldAdapter).getCurrenntList()?.map { it.nom })
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
//            this.add(Pair("Arbre d'ombrage", (recyclerVarieteArbrListSuiviParcel.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
//            this.add(Pair("Insecte parasite", (recyclerInsecteOfSuiviParcelle.adapter as OmbrageAdapter).getOmbragesAdded().map { "${it.variete}: ${it.nombre}\n" }.toModifString() ))
            this.add(Pair("Les themes", themesSelected.toModifString()))

            if (endRapport.isNullOrEmpty()) this.add(Pair("Status du rapport", "Non chargé"))
            else this.add(Pair("Status du rapport", "Chargé"))
        }.map { MapEntry(it.first, it.second) }

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
                usersId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
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
        setAllListener()

//        editDateFormation.text = null
//        editThemeFormation.text = null
//        editVisiteursFormation.text = null

        selectLocaliteFormation.setSelection(0)
        selectLieuFormation.setSelection(0)
        //selectListePresenceFormation.setSelection(0)

        producteursSelectedList?.clear()
        producteursList?.clear()
        producteurPresenceAda?.notifyDataSetChanged()

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

    fun testImageTaked(bundleData: Uri?) {
        // Get the dimensions of the View
        try {
            val options = BitmapFactory.Options()
            options.inSampleSize = 8

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
                documentPath = UriUtils.uri2File(bundleData).path
                when(whichPhoto){
                    0 -> {
                        endphoto = documentPath
//                        imagePhotoFormation.setImageBitmap(
//                            BitmapFactory.decodeFile(
//                                documentPath,
//                                options
//                            )
//                        )
                        imagePhotoFormation.setImageURI(bundleData)
                    }
                    1 -> {
                        endRapport = documentPath
                        imageRapportFormation.setImageResource(R.drawable.document_file_download_done)
                    }
                }
//                LogUtils.e(FormationActivity.TAG, formationPhotoPath)
//                FileUtils.copy(UriUtils.uri2File(bundleData), File(photoPath))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }


    fun draftFormation(draftModel: DataDraftedModel?) {

        val itemModelOb = getFormationObjet()

        if(itemModelOb == null) return

        val formationModelDraft = itemModelOb?.first.apply {
            this?.apply {
                localitesId = localite.id.toString()
                campagneId = campagne.id
                photoFormation = formationPhotoPath
                rapportFormation = endRapport
                photoFormation = photoFormation

                themeStringify = GsonUtils.toJson(themesSelected)
                //moduleStringify = GsonUtils.toJson(selectModuleFormation.selectedStrings)
            }
        }

        Commons.showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
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
        val formationDrafted = ApiClient.gson.fromJson(draftedData.datas, FormationModel::class.java)

        passSetupFormationModel(formationDrafted)
    }


    private fun showFileChooser(pView: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Set a general MIME type to allow any file type
        // Add specific MIME types for images, Excel, Word, and PDF
        if(whichPhoto == 1){
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // Excel (xlsx)
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // Word (docx)
                "application/pdf"   // PDF
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
            carnetFile = createImageFile()
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
                            startActivityForResult(Intent.createChooser(intent, "Selectionnez la photo"), pView)
                        }
                        1 -> {
                            startActivityForResult(Intent.createChooser(intent, "Selectionnez le document"), pView)
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
        LogUtils.e(ProducteurActivity.TAG, "OKOKOKOK")

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE)  {
                testImageTaked(null)
            } else {
                testImageTaked(data?.data)
            }
        } else {
            showMessage(
                "Aucune photo selectionnée",
                context = this,
                finished = false,
                callback = {},
                positive = "Compris !",
                deconnec = false,
                showNo = false
            )
        }
    }

    fun dialogPickerPhoto() {
        val dialogPicker = AlertDialog.Builder(this)
            .setNegativeButton("Gallerie") { dialog, _ ->
                dialog.dismiss()
                showFileChooser(11)
            }

        if(whichPhoto == 0){
            dialogPicker.setMessage("Source de la photo ?")
                .setPositiveButton("Camera") { dialog, _ ->
                    dialog.dismiss()
                    dispatchTakePictureIntent()
                }
        }else if(whichPhoto == 1){
            dialogPicker.setMessage("Source du rapport ?")
        }
        dialogPicker.create().show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formation)

        formationDao = CcbRoomDatabase.getDatabase(applicationContext)?.formationDao()
        producteurDao = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()

//        editDateFormation.setOnClickListener {
//            datePickerDialog = null
//            val calendar: Calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
//                editDateFormation.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
//                dateNaissance = editDateFormation.text?.toString()!!
//            }, year, month, dayOfMonth)
//
//            datePickerDialog!!.datePicker.maxDate = Date().time
//            datePickerDialog?.show()
//        }

//        producteursSelectedList = mutableListOf()
//        producteurPresenceAda = ProducteurPresenceAdapter(producteursSelectedList)
//
//        rvProducteurPresenceFormation.adapter = producteurPresenceAda
//        rvProducteurPresenceFormation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//
//        rvProducteurPresenceFormation.adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
//            override fun onChanged() {
//                super.onChanged()
//                if(producteursSelectedList!!.size > 0){
//                    rvProducteurPresenceFormation.layoutParams.height = 300
//                }
//            }
//        })

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

        editDateDebuFormation.setOnClickListener { configDate(editDateDebuFormation) }
        editDateFinFormation.setOnClickListener { configDate(editDateFinFormation) }

    }
}
