package ci.projccb.mobile.activities.forms

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.SuiviApplicationPreviewActivity
import ci.projccb.mobile.activities.infospresenters.SuiviParcellePreviewActivity
import ci.projccb.mobile.adapters.InsecteAdapter
import ci.projccb.mobile.adapters.MatiereAdapter
import ci.projccb.mobile.adapters.MultipleItemAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.adapters.OnlyFieldAdapter
import ci.projccb.mobile.adapters.SixItemAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.ArbreData
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.repositories.datas.InsectesParasitesData
import ci.projccb.mobile.repositories.datas.PesticidesAnneDerniereModel
import ci.projccb.mobile.repositories.datas.PesticidesApplicationModel
import ci.projccb.mobile.repositories.datas.PresenceAutreInsecteData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.configHour
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.setOnlyOneITemSApplicRV
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.ListConverters
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_suivi_application.*
import kotlinx.android.synthetic.main.activity_suivi_parcelle.clickAddTraitInsecteParOuRavListSuiviParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.clickSaveAnimauxSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.containerArbreAgroSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.editAnimalSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.editTraitInsecteParOuRavFrequSParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.editTraitInsecteParOuRavQtSParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerAnimauxSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerArbrAgroSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerAutreInsecteParOuRavSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerInsecteAmisSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerInsecteParOuRavSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerIntantAnDerListSuiviParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerPestListSuiviParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerTraitInsecteParOuRavListSuiviParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectAgroForesterieSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectArbreSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectLocaliteSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectParcelleSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectProducteurSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectSectionSParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectTraitInsecteParOuRavContenantSParcell
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectTraitInsecteParOuRavNomSParcell
import kotlinx.android.synthetic.main.activity_suivi_parcelle.selectTraitInsecteParOuRavUniteSParcell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@SuppressWarnings("ALL")
class SuiviApplicationActivity : AppCompatActivity() {


    private var maladieList: MutableList<String>? = mutableListOf()
    private var endphotoTamponPath: String = ""
    private var endphotoDouchePath: String = ""
    var insecteAdapter: InsecteAdapter? = null
    var parasitesList: MutableList<InsecteRavageurModel>? = mutableListOf()

    var matiereAdapter: MatiereAdapter? = null
    var matieresList: MutableList<String>? = mutableListOf()

    var applicationId = ""
    var applicationNom = ""

    var localiteId = ""
    var localiteNom = ""

    var producteurId = ""
    var producteurNom = ""

    var campagneId = ""
    var campagneNom = ""

    var cultureId = ""
    var cultureNom = ""

    var zoneTamponYesNo = ""

    var doucheYesNo = ""

    var degreDangerositeLevel = ""
    var whichPhoto = 0

    var photoDouchePath = ""
    var photoTamponPath = ""

    var fileGlobal: File? = null

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_PICKED = 2
    var draftedDataApplicateur: DataDraftedModel? = null

    val sectionCommon: CommonData = CommonData()
    val localiteCommon: CommonData = CommonData()
    val producteurCommon: CommonData = CommonData()
    val parcelleCommon: CommonData = CommonData()
    val applicateurCommon: CommonData = CommonData()


    fun configDate(viewClciked: AppCompatEditText) {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
            viewClciked.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
        }, year, month, dayOfMonth)

        datePickerDialog.datePicker.maxDate = DateTime.now().millis
        datePickerDialog.show()
    }


    fun configTime(viewClicked: AppCompatEditText) {
        // Get Current Time
        val c: Calendar = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this, { timePickerView, hourOfDay, minute -> viewClicked.setText("$hourOfDay:$minute") },
            mHour,
            mMinute,
            true
        )
        timePickerDialog.show()
    }


    fun setupApplicateurSelection() {
        val applicateursList = CcbRoomDatabase.getDatabase(this)?.applicateurDao()?.getAll()!!
        val applicateurAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, applicateursList)
//        selectApplicateurSuiviApplication!!.adapter = applicateurAdapter
//
//        selectApplicateurSuiviApplication.setTitle("Choisir l'applicateur")
//
//        selectApplicateurSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//
//                val applicateurModel = applicateursList[position]
//                applicationId = applicateurModel.id.toString()
//                applicationNom = applicateurModel.nom!!
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun setMatiereParcelle() {
        matieresList = mutableListOf()
        matiereAdapter = MatiereAdapter(matieresList!!)
        recyclerMatiereListSuiviApplication.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerMatiereListSuiviApplication.adapter = matiereAdapter
    }


    fun setParasitesList() {
        parasitesList = mutableListOf()
        insecteAdapter = InsecteAdapter(parasitesList!!)
//        recyclerParasiteListSuiviApplication.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        recyclerParasiteListSuiviApplication.adapter = insecteAdapter
    }

//    fun setupCampagneSelection() {
//        val campagnesList = CcbRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()!!
//        val campagneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList)
//        selectCampagneSuiviApplication!!.adapter = campagneAdapter
//
//        selectCampagneSuiviApplication.setTitle("Choisir la campagne")
//
//        selectCampagneSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//
//                val campagneModel = campagnesList[position]
//                campagneId = campagneModel.id.toString()
//                campagneNom = campagneModel.campagnesNom!!
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
//    }

    fun setupZoneTemponSelection() {
//        selectZoneTampoSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                zoneTamponYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                when (zoneTamponYesNo.uppercase()) {
//                    getString(R.string.oui) -> {
//                        linearZoneTamponPhotoContainerSuiviApplication.visibility = View.VISIBLE
//                    }
//                    getString(R.string.non) -> {
//                        linearZoneTamponPhotoContainerSuiviApplication.visibility = View.GONE
//                    }
//                    else -> {
//                        linearZoneTamponPhotoContainerSuiviApplication.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun setupDoucheYesNoSelection() {
//        selectPresenceDoucheSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                doucheYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                when (doucheYesNo.uppercase()) {
//                    getString(R.string.oui) -> {
//                        linearZoneDouchePhotoContainerSuiviApplication.visibility = View.VISIBLE
//                    }
//                    getString(R.string.non) -> {
//                        linearZoneDouchePhotoContainerSuiviApplication.visibility = View.GONE
//                    }
//                    else -> {
//                        linearZoneDouchePhotoContainerSuiviApplication.visibility = View.GONE
//                    }
//                }
//
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//            }
//        }
    }


    fun collectDatas() {
        val itemModelOb = getSuiviApplicationObjet()

        if(itemModelOb == null) return

        val suiviApplicationDatas = itemModelOb?.first.apply {
            this?.apply {
                section = sectionCommon.id.toString()
                this.localite = localiteCommon.id.toString()
                producteur = producteurCommon.id.toString()
                parcelle_id = parcelleCommon.id.toString()
                applicateur = applicateurCommon.id.toString()

                maladiesStr = GsonUtils.toJson(selectListMaladieSuiviApplication.selectedStrings)

                pesticidesStr = GsonUtils.toJson( (recyclerPestListSApplic.adapter as SixItemAdapter).getMultiItemAdded().map {
                    PesticidesApplicationModel(
                        nom = it.value,
                        toxicicologie = it.value1,
                        nomCommercial = it.value2,
                        matiereActive = it.value3,
                        dose = it.value4,
                        frequence = it.value5
                    )
                } )
            }
        }

        val mapEntries: List<MapEntry>? = itemModelOb?.second?.apply {
            this.add(Pair(getString(R.string.produits_pythos_enr_gistr_s), (recyclerPestListSApplic.adapter as SixItemAdapter).getMultiItemAdded().map { "Pesticide: ${it.value}| Toxicicologie: ${it.value1}| Nom commercial: ${it.value2}| Matières actives: ${it.value3}| Dose: ${it.value4}| Fqe: ${it.value5}\n" }.toModifString() ))
            this.add(Pair(getString(R.string.maladies_observ_es_dans_la_parcelle), selectListMaladieSuiviApplication.selectedStrings.toModifString(false) ))
        }.map { MapEntry(it.first, it.second) }

        Commons.printModelValue(suiviApplicationDatas as Object, mapEntries)

        try {
            val intentSuiviApplicationPreview = Intent(this, SuiviApplicationPreviewActivity::class.java)
            intentSuiviApplicationPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
            intentSuiviApplicationPreview.putExtra("preview", suiviApplicationDatas)
            intentSuiviApplicationPreview.putExtra("draft_id", draftedDataApplicateur?.uid)
            startActivity(intentSuiviApplicationPreview)
        } catch (ex: Exception) {

        }
    }

    private fun getSuiviApplicationObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<SuiviApplicationModel, MutableList<Pair<String, String>>>? {
        var isMissingDial2 = false

        var itemList = getSetupSuiviApplicationModel(SuiviApplicationModel(
            uid = 0,
            isSynced = false,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            origin = "local",
        ), mutableListOf<Pair<String,String>>())
        //LogUtils.d(.toString())
        var allField = itemList.second
        var isMissing = false
        var message = ""
        var notNecessaire = listOf<String>()
        for (field in allField){
            if(field.second.isNullOrBlank() && notNecessaire.contains(field.first.lowercase()) == false){
                message = getString(R.string.le_champ_intitul_n_est_pas_renseign)
                isMissing = true
                break
            }
        }

        for (field in allField){
            if(field.second.isNullOrBlank() && necessaryItem.contains(field.first)){
                message = getString(R.string.le_champ_intitul_n_est_pas_renseign)
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

    fun getSetupSuiviApplicationModel(
        prodModel: SuiviApplicationModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<SuiviApplicationModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_suivi_application)
        Commons.getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupSuiviApplicationModel(
        prodModel: SuiviApplicationModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_suivi_application)
        prodModel?.let {
            Commons.setAllValueOfTextViews(mainLayout, prodModel)
        }
    }


    fun addMatiere(matiere: String) {
        if (matiere.isEmpty()) return

        matieresList?.forEach {
            if (it.uppercase() == matiere.uppercase()) {
                ToastUtils.showShort(getString(R.string.cette_matiere_est_deja_ajout_e))
                return
            }
        }

        matieresList?.add(matiere)
        matiereAdapter?.notifyDataSetChanged()

        editMatiereActiveSuiviApplication.text = null
    }


    fun addParasite(parasite: InsecteRavageurModel) {
        if (parasite.nom?.isEmpty()!!) return

        parasitesList?.forEach {
            if (it.nom?.uppercase() == parasite.nom.uppercase()) {
                ToastUtils.showShort(getString(R.string.ce_parasite_ou_insecte_est_deja_ajout))
                return
            }
        }

        parasitesList?.add(parasite)
        insecteAdapter?.notifyDataSetChanged()

        //editParasiteSuiviApplication.text = null
    }


    fun setAllConfigurations() {
        try {
            setupDoucheYesNoSelection()
            setupZoneTemponSelection()
            //degreDangerositeLevel()
            //setupCampagneSelection()
            setupApplicateurSelection()
            //setupLocaliteSelection()
            setMatiereParcelle()
            setParasitesList()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }




    private fun createImageFileCompressed() {
        var compressedFile: File? = null
        try {
            when (whichPhoto) {
                1 -> fileGlobal = File(photoTamponPath)
                2 -> fileGlobal = File(photoDouchePath)
            }


            CoroutineScope(Dispatchers.IO).launch {
                compressedFile = Compressor.compress(this@SuiviApplicationActivity, fileGlobal!!) {
                    quality(75)
                    format(Bitmap.CompressFormat.JPEG)
                }
            }

            LogUtils.e("TAG -> enter here")

            if (FileUtils.isFileExists(compressedFile)) {
                LogUtils.e("TAG -> enter here")
                val finalCompressedFile = compressedFile
                FileUtils.copy(compressedFile, fileGlobal) { srcFile, destFile ->

                    when(whichPhoto) {
                        1 -> {
                            SPUtils.getInstance().put("tamponZone_",
                                Commons.encodeFileToBase64Binary(finalCompressedFile)
                            )
                        }
                        2 -> {
                            SPUtils.getInstance().put("doucheApplicateur_",
                                Commons.encodeFileToBase64Binary(finalCompressedFile)
                            )
                        }
                    }

                    true
                }
            }
        } catch (e: IOException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var carnetFile: File? = null
        try {
            carnetFile = createImageFile()
        } catch (ex: IOException) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
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
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }


    private fun createImageFile(): File? {
        try {
            // Create an image file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            var imageFileName = ""


            when (whichPhoto) {
                1 -> imageFileName = "tamponZone_" + timeStamp + "_"
                2 -> imageFileName = "doucheApplicateur_" + timeStamp + "_"
            }

            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )

            // Save a file: path for use with ACTION_VIEW intents
            when (whichPhoto) {
                1 -> photoTamponPath = image.absolutePath
                2 -> photoDouchePath = image.absolutePath
            }

            return image
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }

        return null
    }

//    fun copyFileFromUriToAnotherFile(context: Context, sourceUri: Uri, destinationFilePath: String) {
//        context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
//            context.openFileOutput(FileUtils.getFileName(destinationFilePath), Context.MODE_PRIVATE)?.use { outputStream ->
//                inputStream.copyTo(outputStream)
//            }
//        }
//    }

    fun testImageTaked(bundleData: Uri?) {
        // Get the dimensions of the View
        try {
            when (whichPhoto) { // Laiss zemoi yan !
                1 -> {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 8

                    if (bundleData == null) {
                        endphotoTamponPath = photoTamponPath
                        //imageZoneTamponPhotoSuiviApplication.setImageBitmap(BitmapFactory.decodeFile(photoTamponPath, options))
                    } else {
                        options.inJustDecodeBounds = true
                        options.inPurgeable = true

                        LogUtils.d(photoTamponPath)
                        endphotoTamponPath = photoTamponPath
                        FileUtils.copy(UriUtils.uri2File(bundleData), File(photoTamponPath))
                        //imageZoneTamponPhotoSuiviApplication.setImageURI(bundleData)
                    }
                }
                2 -> {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 8

                    if (bundleData == null) {
                        endphotoDouchePath = photoDouchePath
                        //imagePresenceDoucheSuiviApplication.setImageBitmap(BitmapFactory.decodeFile(photoDouchePath, options))
                    } else {
                        options.inJustDecodeBounds = true
                        options.inPurgeable = true
                        //photoDouchePath = UriUtils.uri2File(bundleData).path
                        endphotoDouchePath = photoDouchePath
                        FileUtils.copy(UriUtils.uri2File(bundleData), File(photoDouchePath))
                        //imagePresenceDoucheSuiviApplication.setImageURI(bundleData)
                    }
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
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
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.selectionnez_la_photo)), pView)
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // super.onActivityResult(requestCode, resultCode, data)
        LogUtils.e(FormationActivity.TAG, "OKOKOKOK")

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    testImageTaked(null)
                } else {
                    testImageTaked(data?.data)
                }
            } else {
                Commons.showMessage(
                    getString(R.string.aucune_photo_selectionn_e),
                    context = this,
                    finished = false,
                    callback = {},
                    positive = getString(R.string.compris),
                    deconnec = false,
                    showNo = false
                )
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun dialogPickerPhoto() {
        try {
            val dialogPicker = AlertDialog.Builder(this)
                .setMessage(getString(R.string.source_de_la_photo))
                .setPositiveButton("Camera") { dialog, _ ->
                    dialog.dismiss()
                    dispatchTakePictureIntent()
                }
                .setNegativeButton(getString(R.string.gallerie)) { dialog, _ ->
                    dialog.dismiss()
                    showFileChooser(11)
                }
                .create()

            dialogPicker.show()
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun draftSuiviApplication(draftModel: DataDraftedModel?) {
        try {
            val itemModelOb = getSuiviApplicationObjet(false)

            if(itemModelOb == null) return

            val suiviApplicationDatasDraft = itemModelOb?.first.apply {
                this?.apply {
                    section = sectionCommon.id.toString()
                    this.localite = localiteCommon.id.toString()
                    producteur = producteurCommon.id.toString()
                    parcelle_id = parcelleCommon.id.toString()
                    applicateur = applicateurCommon.id.toString()

                    maladiesStr = GsonUtils.toJson(selectListMaladieSuiviApplication.selectedStrings)

                    pesticidesStr = GsonUtils.toJson( (recyclerPestListSApplic.adapter as SixItemAdapter).getMultiItemAdded().map {
                        PesticidesApplicationModel(
                            nom = it.value,
                            toxicicologie = it.value1,
                            nomCommercial = it.value2,
                            matiereActive = it.value3,
                            dose = it.value4,
                            frequence = it.value5
                        )
                    } )
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
                            datas = ApiClient.gson.toJson(suiviApplicationDatasDraft),
                            typeDraft = "suivi_application",
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                        )
                    )

                    Commons.showMessage(
                        message = getString(R.string.contenu_ajout_aux_brouillons),
                        context = this,
                        finished = true,
                        callback = {
                            Commons.playDraftSound(this)
                            imageDraftBtn.startAnimation(
                                Commons.loadShakeAnimation(
                                    this
                                )
                            )
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
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun undraftedDatas(draftedData: DataDraftedModel) {
        try {
            val applicationDrafted =
                ApiClient.gson.fromJson(draftedData.datas, SuiviApplicationModel::class.java)

            setupSectionSelection(applicationDrafted.section, applicationDrafted.localite, applicationDrafted.producteur, applicationDrafted.parcelle_id)

            Commons.setListenerForSpinner(this,
                getString(R.string.qui_a_r_alis_l_application),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectPersonApplicSApplic,
                currentVal = applicationDrafted.personneApplication,
                listIem = resources.getStringArray(R.array.personne_applicant)
                    ?.toList() ?: listOf(),
                onChanged = {
                    if(it == 1){
                        containerListApplicSApplic.visibility = View.VISIBLE
                    }else if(it == 2){
                        containerIndepenSApplic.visibility = View.VISIBLE
                    }else{
                        containerListApplicSApplic.visibility = View.GONE
                        containerIndepenSApplic.visibility = View.GONE
                    }
                },
                onSelected = { itemId, visibility ->
                })

            val listApplicateur = CcbRoomDatabase.getDatabase(this)?.concernesDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            Commons.setListenerForSpinner(this,
                getString(R.string.qui_est_l_applicateur),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectListApplicSApplic,
                currentVal = listApplicateur?.filter { it.id.toString() == applicationDrafted.applicateur }?.map { "${it.firstname} ${it.lastname}" }?.firstOrNull(),
                listIem = listApplicateur?.map { "${it.firstname} ${it.lastname}" }
                    ?.toList() ?: listOf(),
                onChanged = {
                    applicateurCommon.id = listApplicateur?.get(it)?.id!!
                    applicateurCommon.nom = "${listApplicateur[it].firstname} ${listApplicateur[it].lastname}"
                },
                onSelected = { itemId, visibility ->
                })

            Commons.setListenerForSpinner(this,
                getString(R.string.poss_de_t_il_un_epi),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
                spinner = selectIndependantEpiSApplic,
                currentVal = applicationDrafted.independantEpi,
                itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
                listIem = resources.getStringArray(R.array.YesOrNo)
                    ?.toList() ?: listOf(),
                onChanged = {
                },
                onSelected = { itemId, visibility ->
                    if(itemId==1){
                        containerEtatEpiSApplic.visibility = visibility
                    }
                })

            Commons.setupItemMultiSelection(this, selectListMaladieSuiviApplication, "Quelles sont les maladies observées ?", maladieList?.map { CommonData(0, it) }?.toMutableList()?: mutableListOf(),
                ){

            }

            selectListMaladieSuiviApplication.setItems(GsonUtils.fromJson<List<String>>(applicationDrafted.maladiesStr, object : TypeToken<List<String>>() {}.type))

            (recyclerPestListSApplic.adapter as SixItemAdapter).setDataToRvItem(
                (GsonUtils.fromJson<MutableList<PesticidesApplicationModel>>(applicationDrafted.pesticidesStr, object : TypeToken<MutableList<PesticidesApplicationModel>>() {}.type)).map {
                    AdapterItemModel(
                        id=0,
                        value = it.nom,
                        value1 = it.toxicicologie,
                        value2 = it.nomCommercial,
                        value3 = it.matiereActive,
                        value4 = it.dose,
                        value5 = it.frequence
                    )
                }.toMutableList()
            )

            passSetupSuiviApplicationModel(applicationDrafted)

        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_application)

        //setAllConfigurations()

        editHeureDebutSuiviApplication.setOnClickListener {
            configHour(editHeureDebutSuiviApplication)
        }

        editDateSuiviApplication.setOnClickListener {
            configDate(editDateSuiviApplication)
        }

        setOnlyOneITemSApplicRV(this,
            recyclerMatiereListSuiviApplication,
            clickSaveMatiereSuiviApplication,
            editMatiereActiveSuiviApplication){

            Commons.setupItemMultiSelection(this, selectMatActivPestSApplic, "Liste des matières actives", (recyclerMatiereListSuiviApplication.adapter as OnlyFieldAdapter).getOnlyItemAdded().toMutableList() ){

            }

        }

        Commons.setupItemMultiSelection(this, selectMatActivPestSApplic, "Liste des matières actives", arrayListOf() ){

        }

        Commons.setSixItremRV(this,
            recyclerPestListSApplic,
            clickAddPestListSApplic,
            selectPestNomSApplic,
            selectPestToxicoSApplic,
            selectMatActivPestSApplic,
            editNomComPestSApplic,
            editDosePestSApplic,
            editFrequencPestSApplic,
            libeleList = arrayListOf())

        clickSaveSuiviApplication.setOnClickListener {
            collectDatas()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageDraftBtn.setOnClickListener {
            draftSuiviApplication(draftedDataApplicateur ?: DataDraftedModel(uid = 0))
        }

        maladieList = resources.getStringArray(R.array.maladieList).toMutableList()

        //applyFilters(editSuperficieSuiviApplication)

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataApplicateur = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(
                    uid = 0
                )
                undraftedDatas(draftedDataApplicateur!!)
            }else{
                setAllSelection()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun setAllSelection() {

        setupSectionSelection()

        Commons.setListenerForSpinner(this,
            getString(R.string.qui_a_r_alis_l_application),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectPersonApplicSApplic,
            listIem = resources.getStringArray(R.array.personne_applicant)
                ?.toList() ?: listOf(),
            onChanged = {
                        if(it == 1){
                            containerListApplicSApplic.visibility = View.VISIBLE
                        }else if(it == 2){
                            containerIndepenSApplic.visibility = View.VISIBLE
                        }else{
                            containerListApplicSApplic.visibility = View.GONE
                            containerIndepenSApplic.visibility = View.GONE
                        }
            },
            onSelected = { itemId, visibility ->
            })

        val listApplicateur = CcbRoomDatabase.getDatabase(this)?.concernesDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        Commons.setListenerForSpinner(this,
            getString(R.string.qui_est_l_applicateur),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectListApplicSApplic,
            listIem = listApplicateur?.map { "${it.firstname} ${it.lastname}" }
                ?.toList() ?: listOf(),
            onChanged = {
                applicateurCommon.id = listApplicateur?.get(it)?.id!!
                applicateurCommon.nom = "${listApplicateur[it].firstname} ${listApplicateur[it].lastname}"
            },
            onSelected = { itemId, visibility ->
            })

        Commons.setListenerForSpinner(this,
            getString(R.string.poss_de_t_il_un_epi),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectIndependantEpiSApplic,
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {
            },
            onSelected = { itemId, visibility ->
                if(itemId==1){
                    containerEtatEpiSApplic.visibility = visibility
                }
            })

        Commons.setupItemMultiSelection(this, selectListMaladieSuiviApplication, "Quelles sont les maladies observées ?", maladieList?.map { CommonData(0, it) }?.toMutableList()?: mutableListOf() ){

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
            getString(R.string.choix_de_la_section),
            getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = libItem ,
            spinner = selectSectionSApplic,
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
            getString(R.string.choix_de_la_localit),
            getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectLocaliteSApplic,
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
        val producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            getString(R.string.choix_du_producteur),
            getString(R.string.la_liste_des_producteurs_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurSApplic,
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
            getString(R.string.choisir_sa_parcelle_concern_e),
            getString(R.string.la_liste_des_parcelles_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectParcelleSApplic,
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
