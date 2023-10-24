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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.SuiviApplicationPreviewActivity
import ci.projccb.mobile.adapters.InsecteAdapter
import ci.projccb.mobile.adapters.MatiereAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.ListConverters
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_suivi_application.*
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


    fun setupLocaliteSelection() {
        val localitesList = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()) ?: mutableListOf()

        if (localitesList.size == 0) {
            Commons.showMessage(
                "La liste des localités est vide ! Refaite une mise à jour.",
                this,
                finished = false,
                callback = {},
                "Compris !",
                false,
                showNo = false,
            )

            localiteId = ""
            localiteNom = ""
            selectLocaliteSuiviApplication?.adapter = null

            return
        }

        val localiteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesList)
        selectLocaliteSuiviApplication!!.adapter = localiteAdapter

        selectLocaliteSuiviApplication.setTitle("Choisir la localité")

        selectLocaliteSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {

                val localiteModel = localitesList[position]
                localiteId = localiteModel.id.toString()
                localiteNom = localiteModel.nom!!

                setupProducteurSelection(localiteId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
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


    fun setupProducteurSelection(pLocaliteId: String) {
        val producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()?.getProducteursByLocalite(localite = pLocaliteId)
        val producteursDatas: MutableList<CommonData> = mutableListOf()
        producteursList?.map {
            CommonData(id = it.id, nom = "${it.nom} ${it.prenoms}")
        }?.let {
            producteursDatas.addAll(it)
        }

        val estimationDrafted = ApiClient.gson.fromJson(draftedDataApplicateur?.datas, EstimationModel::class.java)
        selectProducteurSuiviApplication.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, producteursDatas)

        if (draftedDataApplicateur != null) {
            provideDatasSpinnerSelection(
                selectProducteurSuiviApplication,
                estimationDrafted.producteurNom,
                producteursDatas
            )
        }

        selectProducteurSuiviApplication.setTitle("Choisir le producteur")
        selectProducteurSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                val producteur = producteursList!![position]
                producteurNom = "${producteur.nom} ${producteur.prenoms}"

                producteurId = if (producteur.isSynced) {
                    producteur.id!!.toString()
                } else {
                    producteur.uid.toString()
                }

                setupCulturePulveriseeSelection(producteurId)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupCampagneSelection() {
        val campagnesList = CcbRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()!!
        val campagneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, campagnesList)
        selectCampagneSuiviApplication!!.adapter = campagneAdapter

        selectCampagneSuiviApplication.setTitle("Choisir la campagne")

        selectCampagneSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {

                val campagneModel = campagnesList[position]
                campagneId = campagneModel.id.toString()
                campagneNom = campagneModel.campagnesNom!!
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupCulturePulveriseeSelection(producteurId: String?) {
        val culturesList = CcbRoomDatabase.getDatabase(this)?.parcelleDao()?.getParcellesProducteur(producteurId, SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())!!
        val cultureAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, culturesList)
        selectCulturePuveriseeSuiviApplication!!.adapter = cultureAdapter

        selectCulturePuveriseeSuiviApplication.setTitle("Choisir la culture")

        selectCulturePuveriseeSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {

                val cultureModel = culturesList[position]
                cultureId = cultureModel.id.toString()
                cultureNom = cultureModel.culture?:Constants.VIDE
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun degreDangerositeLevel() {
        selectDegreDangerosiousSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                degreDangerositeLevel = resources.getStringArray(R.array.lowMediumHigh)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun setupZoneTemponSelection() {
//        selectZoneTampoSuiviApplication.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
//                zoneTamponYesNo = resources.getStringArray(R.array.YesOrNo)[position]
//
//                when (zoneTamponYesNo.uppercase()) {
//                    "OUI" -> {
//                        linearZoneTamponPhotoContainerSuiviApplication.visibility = View.VISIBLE
//                    }
//                    "NON" -> {
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
//                    "OUI" -> {
//                        linearZoneDouchePhotoContainerSuiviApplication.visibility = View.VISIBLE
//                    }
//                    "NON" -> {
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
        val suiviApplicationDatas = getSuiviApplicationObjet()

        LogUtils.json(suiviApplicationDatas)

        try {
            val intentSuiviApplicationPreview = Intent(this, SuiviApplicationPreviewActivity::class.java)
            intentSuiviApplicationPreview.putExtra("preview", suiviApplicationDatas)
            intentSuiviApplicationPreview.putExtra("draft_id", draftedDataApplicateur?.uid)
            startActivity(intentSuiviApplicationPreview)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getSuiviApplicationObjet(): SuiviApplicationModel {
        val item = SuiviApplicationModel(
            applicateursId = applicationId,
            campagnesId = campagneId.toInt(),
            dateApplication = editDateSuiviApplication.text.toString(),
            degreDangerosite = degreDangerositeLevel,
            delaisReentree = editDelaiProduitJourSuiviApplication.text.toString(),
            heureApplication = editHeureDebutSuiviApplication.text.toString(),
            //heureFinApplication = editHeureFinSuiviApplication.text.toString(),
            marqueProduitPulverise = editCommercialProduitSuiviApplication.text.toString(),
            parcellesId = cultureId,
            presenceDouche = doucheYesNo,
            zoneTampons = zoneTamponYesNo,
            cultureNom = cultureNom,
            localiteNom = localiteNom,
            producteurNom = producteurNom,
            campagneNom = campagneNom,
            applicateurNom = applicationNom,
            //raisonApplication = editRaisonApplicationSuiviApplication.text.toString(),
            superficiePulverisee = editSuperficieSuiviApplication.text.toString(),
            uid = 0,
            photoDouchePath = endphotoDouchePath,
            photoTamponPath = endphotoTamponPath,
            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0),
            matieresActivesStringify = ApiClient.gson.toJson(matiereAdapter?.getMatieresAdded()),
            nomInsectesCiblesStringify = ApiClient.gson.toJson(insecteAdapter?.getInsectesAdded())
        )
        return item
    }


    fun addMatiere(matiere: String) {
        if (matiere.isEmpty()) return

        matieresList?.forEach {
            if (it.uppercase() == matiere.uppercase()) {
                ToastUtils.showShort("Cette matiere est deja ajoutée")
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
                ToastUtils.showShort("Ce parasite ou insecte est deja ajouté")
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
            degreDangerositeLevel()
            setupCampagneSelection()
            setupApplicateurSelection()
            setupLocaliteSelection()
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
                    startActivityForResult(Intent.createChooser(intent, "Selectionnez la photo"), pView)
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
                    "Aucune photo selectionnée",
                    context = this,
                    finished = false,
                    callback = {},
                    positive = "Compris !",
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
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun draftSuiviApplication(draftModel: DataDraftedModel?) {
        try {
            val suiviApplicationDatasDraft = getSuiviApplicationObjet()

            Commons.showMessage(
                message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
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
                        message = "Contenu ajouté aux brouillons !",
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
                        positive = "OK",
                        deconnec = false,
                        false
                    )
                },
                positive = "OUI",
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

            // Applicateur
            val applicateursLists =
                CcbRoomDatabase.getDatabase(this)?.applicateurDao()?.getAll()
            val applicateursDatas: MutableList<CommonData> = mutableListOf()
            applicateursLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                applicateursDatas.addAll(it)
            }
//            selectApplicateurSuiviApplication.adapter =
//                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, applicateursDatas)
//            provideDatasSpinnerSelection(
//                selectApplicateurSuiviApplication,
//                applicationDrafted.applicateurNom,
//                applicateursDatas
//            )

            // Localite
            val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()
                ?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            val localitesDatas: MutableList<CommonData> = mutableListOf()
            localitesLists?.map {
                CommonData(id = it.id, nom = it.nom)
            }?.let {
                localitesDatas.addAll(it)
            }
            selectLocaliteSuiviApplication.adapter =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
            provideDatasSpinnerSelection(
                selectLocaliteSuiviApplication,
                applicationDrafted.localiteNom,
                localitesDatas
            )

            // Superficie
            editSuperficieSuiviApplication.setText(applicationDrafted.superficiePulverisee)

            //Commons.applyFiltersDec(editSuperficieSuiviApplication, withZero = false)

            // Nom com
            editCommercialProduitSuiviApplication.setText(applicationDrafted.marqueProduitPulverise)

            // Matieres
            matieresList?.addAll(
                ListConverters.stringToMutableList(applicationDrafted.matieresActivesStringify)
                    ?: mutableListOf()
            )
            matiereAdapter?.notifyDataSetChanged()

            // Dangerosite
            provideStringSpinnerSelection(
                selectDegreDangerosiousSuiviApplication,
                applicationDrafted.degreDangerosite,
                resources.getStringArray(R.array.lowMediumHigh)
            )

            // Tampon
//            provideStringSpinnerSelection(
//                selectZoneTampoSuiviApplication,
//                applicationDrafted.zoneTampons,
//                resources.getStringArray(R.array.YesOrNo)
//            )
//
//            // Douche
//            provideStringSpinnerSelection(
//                selectPresenceDoucheSuiviApplication,
//                applicationDrafted.presenceDouche,
//                resources.getStringArray(R.array.YesOrNo)
//            )

            // Insectes
            val insectesType = object : TypeToken<MutableList<InsecteRavageurModel>>() {}.type
            val insectesLists: MutableList<InsecteRavageurModel> = ApiClient.gson.fromJson(
                applicationDrafted.nomInsectesCiblesStringify ?: "[]",
                insectesType
            )
            parasitesList?.addAll(insectesLists)
            insecteAdapter?.notifyDataSetChanged()

            editDelaiProduitJourSuiviApplication.setText(applicationDrafted.delaisReentree)
            //editRaisonApplicationSuiviApplication.setText(applicationDrafted.raisonApplication)

            editDateSuiviApplication.setText(applicationDrafted.dateApplication)
            editHeureDebutSuiviApplication.setText(applicationDrafted.heureApplication)
            //editHeureFinSuiviApplication.setText(applicationDrafted.heureApplication)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_application)

        setAllConfigurations()

        editHeureDebutSuiviApplication.setOnClickListener {
            configTime(editHeureDebutSuiviApplication)
        }

//        editHeureFinSuiviApplication.setOnClickListener {
//            configTime(editHeureFinSuiviApplication)
//        }

        editDateSuiviApplication.setOnClickListener {
            configDate(editDateSuiviApplication)
        }

//        imageZoneTamponPhotoSuiviApplication.setOnClickListener {
//            whichPhoto = 1
//            dialogPickerPhoto()
//        }

//        imagePresenceDoucheSuiviApplication.setOnClickListener {
//            whichPhoto = 2
//            dialogPickerPhoto()
//        }

        clickSaveSuiviApplication.setOnClickListener {
            collectDatas()
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveMatiereSuiviApplication.setOnClickListener {
            try {
                if (editMatiereActiveSuiviApplication.text.toString()
                        .isEmpty() || editMatiereActiveSuiviApplication.text.toString().isEmpty()
                ) {
                    Commons.showMessage("Renseignez une matiere, svp !", this, callback = {})
                    return@setOnClickListener
                }

                addMatiere(editMatiereActiveSuiviApplication.text.toString())
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

//        clickSaveParasiteSuiviApplication.setOnClickListener {
//            try {
//                if (editParasiteSuiviApplication.text.toString().isEmpty()) {
//                    Commons.showMessage(
//                        "Renseignez un insecte ou parasite, svp !",
//                        this,
//                        callback = {})
//                    return@setOnClickListener
//                }
//
//                addParasite(
//                    InsecteRavageurModel(
//                        nom = editParasiteSuiviApplication.text.toString(),
//                        uid = 0,
//                        quantite = ""
//                    )
//                )
//            } catch (ex: Exception) {
//                LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//            }
//        }

        imageDraftBtn.setOnClickListener {
            draftSuiviApplication(draftedDataApplicateur ?: DataDraftedModel(uid = 0))
        }

        //applyFilters(editSuperficieSuiviApplication)

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataApplicateur = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(
                    uid = 0
                )
                undraftedDatas(draftedDataApplicateur!!)
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }
}
