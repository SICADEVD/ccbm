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
import ci.projccb.mobile.adapters.ProducteurPresenceAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_formation.*
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


    private var endphoto: String = ""
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

    var campagneNom = ""
    var campagneId = ""

    var photoPath = ""
    var fileGlobal: File? = null

    val REQUEST_IMAGE_CAPTURE = 10
    val REQUEST_IMAGE_PICKED = 2

    var datePickerDialog: DatePickerDialog? = null
    var draftedDataFormation: DataDraftedModel? = null


    fun setupLocaliteSelection() {
        localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa()
        localitesList = localiteDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

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

            localiteIdSelected = ""
            localiteSelected = ""
            selectLocaliteFormation?.adapter = null

            return
        }

        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList!!)
        selectLocaliteFormation!!.adapter = localiteAdapter

        selectLocaliteFormation.setTitle("Choisir la localite")

        selectLocaliteFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val locality = localitesList!![position]
                localiteSelected = locality.nom!!

                if (locality.isSynced) {
                    localiteIdSelected = locality.id!!.toString()
                } else {
                    localiteIdSelected = locality.uid.toString()
                }

                setupProducteurSelection(localiteIdSelected)

                //LogUtils.e(TAG, localiteIdSelected)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


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
                campagneNom = campagne.campagnesNom!!
                campagneId = campagne.id.toString()
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
        //val stoppedSchoolReason = resources.getStringArray(R.array.noSchoolRaison)
//        val multiSelectionTheme: MutableList<KeyPairBoolData> = mutableListOf()
//
//        for (theme in (themesList ?: mutableListOf())) {
//            val themeKeyPair = KeyPairBoolData()
//            themeKeyPair.id = theme.id!!.toLong()
//            themeKeyPair.name = theme.nom
//            themeKeyPair.isSelected = false
//            multiSelectionTheme.add(themeKeyPair)
//        }
//
//        selectThemeFormation.setItems(multiSelectionTheme) { items ->
//            for (i in items.indices) {
//                if (items[i].isSelected) {
//                    themesSelected.add(items[i].name)
//                    themesIdSelected.add(items[i].id.toString())
//                }
//            }
//        }
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


    fun setupProducteurSelection(localite: String?) {
        producteursList?.clear()
        producteursList = producteurDao?.getProducteursByLocalite(localite = localite)

        val producteursAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursList!!)
        selectListePresenceFormation!!.adapter = producteursAdapter

        producteursList?.add(0, ProducteurModel(uid = 0, nom = "", prenoms = "", id = 0))

        selectListePresenceFormation.setTitle("Choisir le producteur")

        selectListePresenceFormation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                if (position > 0) {
                    val producteur = producteursList!![position]
                    producteurNomPrenoms = "${producteur.nom} ${producteur.prenoms}"

                    producteurId = if (producteur.isSynced) {
                        producteur.id!!.toString()
                    } else {
                        producteur.uid.toString()
                    }

                    producteursSelectedList?.add(producteur)
                    producteurPresenceAda?.notifyDataSetChanged()
                } else {
                    ToastUtils.showShort("Choisir un producteur !")
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
//                selectProducteu.setSelection(0)
//                producteurId = (selectProducteurParcelle.selectedItem as ProducteurModel).id.toString()
            }
        }
    }


    fun setAll() {
        setupLocaliteSelection()

        setupLieuSelection()

        setupCampagneSelection()

        setupTypeFormationSelection()
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

        if (dateNaissance.isEmpty()) {
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

        val formationModel = getFormationObjet()

        formationModel.producteursId = mutableListOf()
        formationModel.producteursNom = mutableListOf()

        producteursSelectedList?.forEach {
            LogUtils.e(Commons.TAG, "${it.nom} ${it.prenoms}")
            if (it.isSynced) {
                formationModel.producteursId?.add("${it.id.toString()}-id")
            } else {
                formationModel.producteursId?.add("${it.uid}-uid")
            }
            formationModel.producteursNom?.add("${it.nom} ${it.prenoms}")
        }

        formationModel.producteursIdStringify = GsonUtils.toJson(formationModel.producteursId)
        formationModel.producteursNomStringify = GsonUtils.toJson(formationModel.producteursNom)

        try {
            val intentFormationPreview = Intent(this, FormationPreviewActivity::class.java)
            intentFormationPreview.putExtra("preview", formationModel)
            intentFormationPreview.putExtra("draft_id", draftedDataFormation?.uid)
            startActivity(intentFormationPreview)
        } catch (ex: Exception) {
            ex.toString()
        }
    }

    private fun getFormationObjet(): FormationModel {
        return FormationModel(
            dateFormation = editDateFormation.text?.trim().toString(),
            lieuFormationsId = lieu,
            isSynced = false,
            typeFormationId = typeFormationId,
            //theme = editThemeFormation.text?.trim().toString(),
            themesLabelStringify = ApiClient.gson.toJson(themesSelected),
            themeStringify = ApiClient.gson.toJson(themesIdSelected),
            localitesId = localiteIdSelected,
            usersId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            uid = 0,
            visiteurs = editVisiteursFormation.text?.trim().toString(),
            origin = "local",
            campagneNom = campagneNom,
            campagneId = campagneId.toInt(),
            lieuFormationNom = lieu,
            localiteNom = localiteSelected,
            themeNom = "",
            photoPath = photoPath,
        )
    }


    fun clearFields() {
        setAll()

        editDateFormation.text = null
        editThemeFormation.text = null
        editVisiteursFormation.text = null

        selectLocaliteFormation.setSelection(0)
        selectLieuFormation.setSelection(0)
        //selectListePresenceFormation.setSelection(0)

        producteursSelectedList?.clear()
        producteursList?.clear()
        producteurPresenceAda?.notifyDataSetChanged()

        editThemeFormation.requestFocus()
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


    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        var imageFileName = ""

        imageFileName = "formation_" + timeStamp + "_"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.absolutePath
        return image
    }


    fun testImageTaked(bundleData: Uri?) {
        // Get the dimensions of the View
        try {
            val options = BitmapFactory.Options()
            options.inSampleSize = 8

            if (bundleData == null) {
                endphoto = photoPath
                imagePhotoFormation.setImageBitmap(BitmapFactory.decodeFile(photoPath, options))
            } else {
                options.inJustDecodeBounds = true
                options.inPurgeable = true
                //photoPath = UriUtils.uri2File(bundleData).path
                endphoto = photoPath
                LogUtils.e(FormationActivity.TAG, photoPath)
                FileUtils.copy(UriUtils.uri2File(bundleData), File(photoPath))
                imagePhotoFormation.setImageURI(bundleData)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE)  {
                testImageTaked(null)
            } else {
                testImageTaked(data?.data)
            }
        } else {
            Commons.showMessage(
                "Aucune photo selectionnée",
                context = this,
                finished = false,
                callback = {},
                positive = "OKAY",
                deconnec = false,
                showNo = false
            )
        }
    }


    fun draftFormation(draftModel: DataDraftedModel?) {
        val formationModelDraft = getFormationObjet()

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

        // Localite
        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val localitesDatas: MutableList<CommonData> = mutableListOf()
        localitesLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            localitesDatas.addAll(it)
        }
        selectLocaliteFormation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
        provideDatasSpinnerSelection(
            selectLocaliteFormation,
            formationDrafted.localiteNom,
            localitesDatas
        )

        // Campagne
        val campagnesLists = CcbRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()
        val campagnesDatas: MutableList<CommonData> = mutableListOf()
        campagnesLists?.map {
            CommonData(id = it.id, nom = it.campagnesNom)
        }?.let {
            campagnesDatas.addAll(it)
        }
        selectCampagneFormation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesDatas)
        provideDatasSpinnerSelection(
            selectCampagneFormation,
            formationDrafted.campagneNom,
            campagnesDatas
        )

        // Type de formation // Todo type de formation
        /*val typeFormationsLists = CcbRoomDatabase.getDatabase(this)?.typeFormationDao()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val typeFormationsDatas: MutableList<CommonData> = mutableListOf()
        typeFormationsLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            typeFormationsDatas.addAll(it)
        }
        selectTypeFormation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesDatas)
        provideDatasSpinnerSelection(
            selectTypeFormation,
            formationDrafted.typeFormationId,
            campagnesDatas
        )*/

        // Liste presence
        val producteursType = object : TypeToken<MutableList<ProducteurModel>>(){}.type
        val producteursLists: MutableList<ProducteurModel> = ApiClient.gson.fromJson(formationDrafted.producteursStringify ?: "[]", producteursType)
        producteursSelectedList?.addAll(producteursLists)
        producteurPresenceAda?.notifyDataSetChanged()

        // Lieu de formation
        val lieusLists = AssetFileHelper.getListDataFromAsset(4, this@FormationActivity) as MutableList<LieuFormationModel>?
            //CcbRoomDatabase.getDatabase(this)?.lieuFormationDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        val lieusDatas: MutableList<CommonData> = mutableListOf()
        lieusLists?.map {
            CommonData(id = it.id, nom = it.nom)
        }?.let {
            lieusDatas.addAll(it)
        }
        selectLieuFormation.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lieusDatas)
        provideDatasSpinnerSelection(
            selectLieuFormation,
            formationDrafted.lieuFormationNom,
            lieusDatas
        )

        // Visiteurs
        editVisiteursFormation.setText(formationDrafted.visiteurs)

        // Date formation
        editDateFormation.setText(formationDrafted.dateFormation)
    }


    private fun showFileChooser(pView: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
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
                    startActivityForResult(Intent.createChooser(intent, "Selectionnez la photo"), pView)
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }


    fun dialogPickerPhoto() {
        val dialogPicker = AlertDialog.Builder(this)
            .setMessage("Source de la photo ?")
            .setPositiveButton("Camera") { dialog, _ ->
                dialog.dismiss()
                dispatchTakePictureIntent()
            }
            .setNegativeButton("Gallerie") { dialog, _ ->
                dialog.dismiss()
                showFileChooser(11)
            }
            .create()

        dialogPicker.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formation)

        formationDao = CcbRoomDatabase.getDatabase(applicationContext)?.formationDao()
        producteurDao = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()

        editDateFormation.setOnClickListener {
            datePickerDialog = null
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
                editDateFormation.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
                dateNaissance = editDateFormation.text?.toString()!!
            }, year, month, dayOfMonth)

            datePickerDialog!!.datePicker.maxDate = Date().time
            datePickerDialog?.show()
        }

        producteursSelectedList = mutableListOf()
        producteurPresenceAda = ProducteurPresenceAdapter(producteursSelectedList)

        rvProducteurPresenceFormation.adapter = producteurPresenceAda
        rvProducteurPresenceFormation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvProducteurPresenceFormation.adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                super.onChanged()
                if(producteursSelectedList!!.size > 0){
                    rvProducteurPresenceFormation.layoutParams.height = 300
                }
            }
        })

        clickCancelFormation.setOnClickListener {
            clearFields()
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

        imagePhotoFormation.setOnClickListener {
            dialogPickerPhoto()
        }

        setAll()

        if (intent.getStringExtra("from") != null) {
            draftedDataFormation = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataFormation!!)
        }
    }
}
