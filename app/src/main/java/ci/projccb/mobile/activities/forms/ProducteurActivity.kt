package ci.projccb.mobile.activities.forms

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.infospresenters.ProducteurPreviewActivity
import ci.projccb.mobile.adapters.CultureProducteurAdapter
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.applyFilters
import ci.projccb.mobile.tools.Commons.Companion.encodeFileToBase64Binary
import ci.projccb.mobile.tools.Commons.Companion.getAllTitleAndValueViews
import ci.projccb.mobile.tools.Commons.Companion.loadShakeAnimation
import ci.projccb.mobile.tools.Commons.Companion.provideDatasSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.provideStringSpinnerSelection
import ci.projccb.mobile.tools.Commons.Companion.setAllValueOfTextViews
import ci.projccb.mobile.tools.Commons.Companion.setListenerForSpinner
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.*
import com.github.gcacace.signaturepad.views.SignaturePad.OnSignedListener
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_producteur.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@SuppressWarnings("ALL")
class ProducteurActivity : AppCompatActivity(), RecyclerItemListener<CultureProducteurModel>, OnSignedListener {


    companion object {
        const val TAG = "ProducteurActivity::class"
    }


    var localiteDao: LocaliteDao? = null
    var nationaliteDao: NationaliteDao? = null
    var typePieceDao: TypePieceDao? = null
    var personneBlesseeDao: PersonneBlesseeDao? = null
    var etudeDao: NiveauDao? = null
    var producteurDao: ProducteurDao? = null
    var recuDao: RecuDao? = null

    var localitesList: MutableList<LocaliteModel>? = null
    var nationalitesList: MutableList<NationaliteModel>? = null
    var typePiecesList: MutableList<TypePieceModel>? = null
    var personBlesseesList: MutableList<PersonneBlesseeModel>? = null
    var etudesList: MutableList<NiveauModel>? = null
    var recusList: MutableList<RecuModel>? = null

    var datePickerDialog: DatePickerDialog? = null
    var cultureProducteurAdapter: CultureProducteurAdapter? = null
    var cultureProducteurs: MutableList<CultureProducteurModel>? = null

    var whichPhoto = 0
    var localiteSelected = ""
    var localiteIdSelected = ""
    var nomProducteur = ""
    var prenomsProducteur = ""
    var dateNaissance = ""
    var nationaliteSelected = ""
    var phone1 = ""
    var phoneTwo = ""
    var typePieceSelected = ""
    var numPiece = ""
    var personneBlesseeSelected = ""
    var jachereYesNo = ""
    var statutCertification = ""
    var anneeCertification = ""
    var jachereSuperficie = ""
    var cultureYesNo = ""
    var nbreTravailleurs = ""
    var nbreUnder18 = ""
    var under18School = ""
    var under18Extrait = ""
    var paperYesNo = ""
    var etude = ""
    var paperGuard = ""
    var ticketHandlerSelected = ""
    var moneyYesNo = ""
    var moneyMobile = ""
    var sexeProducteur = "Homme"
    var consentProducteur = ""
    var bankAccountYesNo = ""

    var profilPhotoPath: String? = null
    var rectoPhotoPath: String? = null
    var versoPhotoPath: String? = null
    var signaturePath: String? = null
    var fileGlobal: File? = null

    var rectoBase64 = ""
    var versoBase64 = ""
    var profileBase64 = ""
    var signatureBase64 = ""
    var draftedDataProducteur: DataDraftedModel? = null

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_PICKED = 2
    var fromAction = ""

    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val programmeCommon = CommonData();


    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null) {
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

        setListenerForSpinner(this, "Choix de la section !", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if(sectionList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectSectionProducteur, listIem = sectionList?.map { it.libelle }
            ?.toList() ?: listOf(),
            onChanged = {

                val section = sectionList!![it]
                //ogUtils.d(section)
                sectionCommon.nom = section.libelle!!
                sectionCommon.id = section.id!!

                setLocaliteSpinner(sectionCommon.id!!, currVal1)

            }, onSelected = { itemId, visibility ->

            })
    }

    private fun setProgrammeSpinner(currVal2:String? = null) {

        var programmesDao = CcbRoomDatabase.getDatabase(applicationContext)?.programmesDao();
        var programmeListi = programmesDao?.getAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal2?.let { idc ->
            programmeListi?.forEach {
                if(it.id == idc.toInt()) libItem = it.libelle
            }
        }

        setListenerForSpinner(this, "Choix du programme !", "La liste des programmes semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if(programmeListi?.size!! > 0) false else true,
            currentVal = currVal2,
            spinner = selectProgramProducteur, listIem = programmeListi?.map { it.libelle }
            ?.toList() ?: listOf(), onChanged = {

            val programme = programmeListi!![it]
            programmeCommon.nom = programme.libelle!!
            programmeCommon.id = programme.id!!

        }, onSelected = { itemId, visibility ->

        })

    }

    private fun setLocaliteSpinner(id: Int, currVal1:String? = null) {

        var localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa();
        var localitesListi = localiteDao?.getLocaliteBySection(id)
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal1?.let { idc ->
            localitesListi?.forEach {
                if(it.id == idc.toInt()) libItem = it.nom
            }
        }

        setListenerForSpinner(this, "Choix de la localité !", "La liste des localités semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if(localitesListi?.size!! > 0) false else true,
            currentVal = currVal1,
            spinner = selectLocaliteProducteur, listIem = localitesListi?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

                localitesListi?.let { list ->
                    var localite = list.get(it)
                    localiteCommon.nom = localite.nom!!
                    localiteCommon.id = localite.id!!

                    //setProgrammeSpinner(localiteCommon.id)
                }


            }, onSelected = { itemId, visibility ->

            })

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LogUtils.e(TAG, "OKOKOKOK")

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


    fun addCultureProducteur(cultureProducteurModel: CultureProducteurModel) {
        if (cultureProducteurModel.label?.length == 0) return

        cultureProducteurs?.forEach {
            if (it.label?.uppercase() == cultureProducteurModel.label?.uppercase() && it.superficie == cultureProducteurModel.superficie) {
                ToastUtils.showShort("Cette culture est deja ajoutée")
                return
            }
        }

        cultureProducteurs?.add(cultureProducteurModel)
        cultureProducteurAdapter?.notifyDataSetChanged()

        // clearCultureProducteurFields()
    }


    fun setupNationaliteSelection(currVal:String? = null) {
        val arrayNationalites: MutableList<String>? = mutableListOf()
        //nationaliteDao = CcbRoomDatabase.getDatabase(applicationContext)?.nationaliteDoa();
        //val nationaliteype = object : TypeToken<MutableList<NationaliteModel>>() {}.type
        nationalitesList = AssetFileHelper.getListDataFromAsset(5, this@ProducteurActivity) as MutableList<NationaliteModel>
        //nationaliteDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        arrayNationalites?.add("Choisir la nationalité...")

        nationalitesList?.map {
            arrayNationalites?.add(it.nom!!)
        }

        val nationaliteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayNationalites!!)
        selectNationaliteProducteur!!.adapter = nationaliteAdapter

        selectNationaliteProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                nationaliteSelected = arrayNationalites[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }


    fun setupSexeSelection() {
        selectSexeProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                sexeProducteur = resources.getStringArray(R.array.genre)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }


    fun setupConsentementSelection() {
        selectConsentementProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                consentProducteur = resources.getStringArray(R.array.YesOrNo)[position]

                if (consentProducteur == "non") finish()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }


    fun setupTypePieceSelection() {
        val arrayTypePieces: MutableList<String> = mutableListOf()
        //typePieceDao = CcbRoomDatabase.getDatabase(applicationContext)?.typePieceDao();
        //val typePieceType = object : TypeToken<MutableList<TypePieceModel>>() {}.type
        typePiecesList = AssetFileHelper.getListDataFromAsset(13, this@ProducteurActivity) as MutableList<TypePieceModel>
        //typePieceDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arrayTypePieces.add("Choisir la piece...")

        typePiecesList?.map {
            arrayTypePieces.add(it.nom!!)
        }

        val typePieceAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayTypePieces)
        selectPieceProducteur!!.adapter = typePieceAdapter

        selectPieceProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                typePieceSelected = arrayTypePieces[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }


    fun setupStatutCertificationSelection() {
        selectStatutProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                anneeCertification = ""
                if (position == 0) {
//                    linearCodeContainerProducteur.visibility = VISIBLE
//                    linearAnneeCertificationProducteur.visibility = VISIBLE
                } else {
//                    linearAnneeCertificationProducteur.visibility = GONE
//                    linearCodeContainerProducteur.visibility = GONE
                }

                statutCertification = resources.getStringArray(R.array.status)[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }


    fun setupEtudesSelection() {
        val arrayEtudes: MutableList<String> = mutableListOf()
        //etudeDao = CcbRoomDatabase.getDatabase(applicationContext)?.niveauDoa()
        //val niveauType = object : TypeToken<MutableList<NiveauModel>>() {}.type
        etudesList = AssetFileHelper.getListDataFromAsset(6, this@ProducteurActivity) as MutableList<NiveauModel>
        //etudeDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        arrayEtudes.add("Choisir le niveau...")

        etudesList?.map {
            arrayEtudes.add(it.nom!!)
        }

        val etudeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayEtudes)
        selectEtudeProducteur!!.adapter = etudeAdapter

        selectEtudeProducteur.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                etude = arrayEtudes[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }


    fun collectDatas() {
//        nomProducteur = editNomProducteur.text.toString().trim()
//        prenomsProducteur = editPrenomsProducteur.text.toString().trim()
//        phone1 = editTelOneProducteur.text.toString().trim()
        //phoneTwo = editTelTwoProducteur.text.toString().trim()

//        numPiece = editPieceProducteur.text.toString().trim()

//        if (localiteSelected.isEmpty()) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Localité non renseignée")
//            return
//        }
//        if (nomProducteur.isEmpty()) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Nom non renseigné")
//            return
//        }
//        if (prenomsProducteur.isEmpty()) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Prenoms non renseigné")
//            return
//        }
//        if (selectNationaliteProducteur.selectedItemPosition == 0) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Nationalité non renseignée")
//            return
//        }
//        if (dateNaissance.isEmpty()) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Date de naissance non renseignée")
//            return
//        }

        /*if (phone1.isEmpty()) {
            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Contact n°1 non renseigné")
            return
        }*/

//        if (selectPieceProducteur.selectedItemPosition == 0) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Type de piece non renseigné")
//            return
//        }
//        if (numPiece.isEmpty()) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("numero piece non renseigné")
//            return
//        }
//        if (selectEtudeProducteur.selectedItemPosition == 0) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Niveau etude non renseigné")
//            return
//        }
//            if (FileUtils.getLength(profilPhotoPath).toInt() == 0) {
//                ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Photo de profil non renseignée")
//                return
//            }

//        if (FileUtils.getLength(signaturePath).toInt() == 0) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Signature non renseignée")
//            return
//        }
//        if (statutCertification.contains("statut", ignoreCase = true)) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Statut non renseigné")
//            return
//        }

//        if (statutCertification.lowercase().startsWith("certifie", ignoreCase = true) && editAnneeCertificationProducteur.text.toString().isEmpty()) {
//            ToastUtils.make().setGravity(Gravity.CENTER, 0, 0).setTextColor(R.color.red_title).show("Année de certification non renseignée")
//            return
//        }

        val producteurModelItem = getProducteurObjet()

        if(producteurModelItem == null) return

        val producteur = producteurModelItem?.first.apply {
            this?.apply {
                photo = profilPhotoPath ?: ""
                section = sectionCommon.id.toString()
                localitesId = localiteCommon.id.toString()
                programme_id = programmeCommon.id.toString()
                isSynced = false
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            }
        }

        val intentProducteurPreview = Intent(this, ProducteurPreviewActivity::class.java)
        intentProducteurPreview.putExtra("preview", producteur)
        val mapEntries: List<MapEntry>? = producteurModelItem?.second?.map { MapEntry(it.first, it.second) }
        intentProducteurPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
        intentProducteurPreview.putExtra("draft_id", draftedDataProducteur?.uid)
        startActivity(intentProducteurPreview)
    }

    private fun getProducteurObjet(): Pair<ProducteurModel, MutableList<Pair<String, String>>>? {

        var itemList = getSetupProducteurModel(ProducteurModel(uid = 0, id = 0,), mutableListOf<Pair<String,String>>())
        //LogUtils.d(.toString())
        var allField = itemList.second
        var isMissing = false
        var message = ""
        var notNecessaire = listOf<String>(
            "En tant que:".lowercase(),
            "Numéro de téléphone".lowercase(),
            "N° de la pièce CMU".lowercase(),
            "N° de carte de sécurité sociale".lowercase())
        for (field in allField){
            if(field.second.isNullOrBlank() && notNecessaire.contains(field.first.lowercase()) == false){
                message = "Le champ intitulé : `${field.first}` n'est pas renseigné !"
                isMissing = true
                break
            }
        }

        if(isMissing){
            showMessage(
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

        return itemList

//        return ProducteurModel(
//            uid = 0,
//            id = 0,
//            //codeProd = editCodeProducteur.text?.trim().toString(),
//            localitesId = localiteIdSelected.toInt().toString(),
//            nom = nomProducteur,
//            prenoms = prenomsProducteur,
//            dateNaiss = dateNaissance,
//            nationalite = nationaliteSelected,
//            phone1 = phone1,
//            phone2 = phoneTwo,
//            piece = typePieceSelected,
//            numPiece = numPiece,
//            statutCertification = statutCertification,
//            //anneeCertification = editAnneeCertificationProducteur.text.toString().trim(),
//            hasForest = jachereYesNo,
//            forestSuperficy = jachereSuperficie,
//            hasOtherFarms = cultureYesNo,
//            etude = etude,
//            under18Count = nbreUnder18,
//            under18SchooledCount = under18School,
//            under18SchooledNoPaperCount = under18Extrait,
//            farmersCount = nbreTravailleurs,
//            blessed = personneBlesseeSelected,
//            sexeProducteur = sexeProducteur,
//            hasFarmsPapers = paperYesNo,
//            paperGuards = paperGuard,
//            recuAchat = ticketHandlerSelected,
//            hasMobileMoney = moneyYesNo,
//            mobileMoney = moneyMobile,
//            banqueAccount = bankAccountYesNo,
//            isSynced = false,
//            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
//            cultures = GsonUtils.toJson(cultureProducteurs),
//            origin = "local",
//            consentement = "Oui",
//            rectoPath = rectoPhotoPath ?: "",
//            versoPath = versoPhotoPath ?: "",
//            photo = profilPhotoPath ?: "",
//            esignaturePath = signaturePath ?: "",
//        )
    }


    fun clearFields() {
        setAllSelection()

        //editCodeProducteur.text = null
        editNomProducteur.text = null
        editPrenomsProducteur.text = null
        editNaissanceProducteur.text = null
        editTelOneProducteur.text = null
        //editTelTwoProducteur.text = null
        editPieceProducteur.text = null
        //editNbreTravailleursProducteur.text = null
        //editNbreUnder18Producteur.text = null
        //editNbreScolariseProducteur.text = null
        //editNbreExtraitProducteur.text = null
        //editMobileMoneyProducteur.text = null
        //editPaperGuardProducteur.text = null
        //editSupJachereProducteur.text = null

        cultureProducteurs?.clear()
        editNomProducteur.requestFocus()
    }


    private fun showFileChooser(pView: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Selectionnez la photo"), pView)
        if (intent.resolveActivity(packageManager) != null) {

        } else {
            LogUtils.e(TAG, "Error launcher photo")
        }
    }


    private fun createImageFileCompressed() {
        var compressedFile: File? = null
        try {
            when (whichPhoto) {
                0 -> fileGlobal = File(profilPhotoPath!!)
                1 -> fileGlobal = File(rectoPhotoPath!!)
                2 -> fileGlobal = File(versoPhotoPath!!)
            }


            CoroutineScope(Dispatchers.IO).launch {
                compressedFile = Compressor.compress(this@ProducteurActivity, fileGlobal!!) {
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
                        0 -> {
                            SPUtils.getInstance().put("profile_", encodeFileToBase64Binary(finalCompressedFile))
                        }
                        1 -> {
                            SPUtils.getInstance().put("recto_", encodeFileToBase64Binary(finalCompressedFile))
                        }
                        2 -> {
                            SPUtils.getInstance().put("verso_", encodeFileToBase64Binary(finalCompressedFile))
                        }
                    }

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
                   startActivityForResult(takePictureIntent, 1)
               }
           } catch (ex: Exception) {
               ex.printStackTrace()
           }
       }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        var imageFileName = ""


        when (whichPhoto) {
            0 -> imageFileName = "profile_" + timeStamp + "_"
            1 -> imageFileName = "recto_" + timeStamp + "_"
            2 -> imageFileName = "verso_" + timeStamp + "_"
            3 -> imageFileName = "sign_" + timeStamp + "_"
        }

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        when (whichPhoto) {
            0 -> profilPhotoPath = image.absolutePath
            1 -> rectoPhotoPath = image.absolutePath
            2 -> versoPhotoPath = image.absolutePath
            3 -> signaturePath = image.absolutePath
        }

        return image
    }


    fun testImageTaked(bundleData: Uri?) {
        // Get the dimensions of the View

        try {
            when (whichPhoto) { // Laiss zemoi yan !
                0 -> {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 8

                    if (bundleData == null) {
                        imagePhotoProfilProducteur.setImageBitmap(BitmapFactory.decodeFile(profilPhotoPath, options))
                    } else {
                        options.inJustDecodeBounds = true
                        options.inPurgeable = true
                        profilPhotoPath = UriUtils.uri2File(bundleData).path
                        LogUtils.e(TAG, profilPhotoPath)
                        imagePhotoProfilProducteur.setImageURI(bundleData)
                    }
                }
//                1 -> {
//                    val options = BitmapFactory.Options()
//                    options.inSampleSize = 8
//
//                    if (bundleData == null) {
//                        imagePhotoRectoProducteur.setImageBitmap(BitmapFactory.decodeFile(rectoPhotoPath, options))
//                    } else {
//                        options.inJustDecodeBounds = true
//                        options.inPurgeable = true
//                        rectoPhotoPath = UriUtils.uri2File(bundleData).path
//                        LogUtils.e(TAG, rectoPhotoPath)
//                        imagePhotoRectoProducteur.setImageURI(bundleData)
//                    }
//                }
//                2 -> {
//                    val options = BitmapFactory.Options()
//                    options.inSampleSize = 8
//
//                    if (bundleData == null) {
//                        imagePhotoVersoProducteur.setImageBitmap(BitmapFactory.decodeFile(versoPhotoPath, options))
//                    } else {
//                        options.inJustDecodeBounds = true
//                        options.inPurgeable = true
//                        versoPhotoPath = UriUtils.uri2File(bundleData).path
//                        LogUtils.e(TAG, versoPhotoPath)
//                        imagePhotoVersoProducteur.setImageURI(bundleData)
//                    }
//                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }


    fun setAllSelection() {

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectTitreDUProducteur,
            itemChanged = arrayListOf(Pair(1, "Planté-partager")),
            listIem = (AssetFileHelper.getListDataFromAsset(26, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId == 1){
                    containerPrecisionTitreProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectCertifProducteur,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (AssetFileHelper.getListDataFromAsset(20, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1){
                    containerAutreCertifProducteur.visibility = visibility
                }
            })

        setupSectionSelection()
        setProgrammeSpinner()

        setListenerForSpinner(this, "Choix du lieu","La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectHabitationProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(22, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->

            })

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectStatutProducteur,
            itemChanged = arrayListOf(Pair(1, "Certifie")),
            listIem = resources.getStringArray(R.array.status)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1){
                    linearAnneeCertificationProducteur.visibility = visibility
                    linearCodeContainerProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectSexeProducteur,
            listIem = resources.getStringArray(R.array.genre)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Choix de votre statut", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectStatutMatProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(23, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Choix de votre nationlité", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectNationaliteProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(5, this) as MutableList<NationaliteModel>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Avez vous des proches", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectProcheProducteur,
            listIem = listOf(),
            itemChanged = arrayListOf(Pair(1, "Oui")),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) {
                    containerEntantqueProducteur.visibility = visibility
                    containerMembreNumProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, "Votre niveau d'etude ?", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectEtudeProducteur,
            listIem = resources.getStringArray(R.array.niveauEtude).toList(),
            onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Quel type de pièce ?", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectPieceProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(13, this) as MutableList<TypePieceModel>)?.map { it.nom },
            onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Votre choix", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectCarteCMUProducteur,
            listIem = resources.getStringArray(R.array.YesOrNo).toList(),
            itemChanged = listOf(Pair(1, "Oui")),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) containerNumPieceCMUProducteur.visibility = visibility
            })

        setListenerForSpinner(this, "Votre choix", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectTypeCarteCSSProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(27, this) as MutableList<CommonData>)?.map { it.nom },
            itemChanged = listOf(Pair(1, "CNPS"), Pair(2, "CMU")),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                containerNumCarteCSSProducteur.visibility = visibility
            })

//        setupNationaliteSelection()
//
//        setupTypePieceSelection()
//
//        setupEtudesSelection()
//
//        setupSexeSelection()
//
//        setupConsentementSelection()
//
//        setupStatutCertificationSelection()
    }

    private fun setupTitreSelection() {



    }

    fun getSetupProducteurModel(
        prodModel: ProducteurModel,
        mutableListOf: MutableList<Pair<String, String>>
    ): Pair<ProducteurModel, MutableList<Pair<String, String>>> {
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_producteur)
        getAllTitleAndValueViews(mainLayout, prodModel, false, mutableListOf)
        return Pair(prodModel, mutableListOf)
    }

    fun passSetupProducteurModel(
        prodModel: ProducteurModel?
    ){
        //LogUtils.d(prodModel.nom)
        val mainLayout = findViewById<ViewGroup>(R.id.layout_producteur)
        prodModel?.let {
            setAllValueOfTextViews(mainLayout, prodModel)
        }
    }


    override fun itemClick(item: CultureProducteurModel) {

    }


    override fun onStartSigning() {
        // FileUtils.createFileByDeleteOldFile(signaturePath)
    }


    override fun onSigned() {

    }


    override fun onClear() {
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


    fun undraftedDatas(draftedData: DataDraftedModel) {
        val producteurDrafted = ApiClient.gson.fromJson(draftedData.datas, ProducteurModel::class.java)

//        if (producteurDrafted.codeProdApp?.isNotEmpty() == true) {
//            linearCodeContainerProducteur.visibility = VISIBLE
//            editCodeProducteur.setText(producteurDrafted.codeProdApp ?: "")
//        }

        // Consentement
//        provideStringSpinnerSelection(
//            selectConsentementProducteur,
//            producteurDrafted.consentement,
//            resources.getStringArray(R.array.YesOrNo)
//        )

        // Localite
//        val localitesLists = CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//        val localitesDatas: MutableList<CommonData> = mutableListOf()
//        localitesLists?.map {
//            CommonData(id = it.id, nom = it.nom)
//        }?.let {
//            localitesDatas.addAll(it)
//        }
//        selectLocaliteProducteur.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, localitesDatas)
//        provideDatasSpinnerSelection(
//            selectLocaliteProducteur,
//            producteurDrafted.localite,
//            localitesDatas
//        )

        // Statut Certification
//        provideStringSpinnerSelection(
//            selectStatutProducteur,
//            producteurDrafted.statutCertification,
//            resources.getStringArray(R.array.status)
//        )

        //  Genre
//        provideStringSpinnerSelection(
//            selectSexeProducteur,
//            producteurDrafted.sexeProducteur,
//            resources.getStringArray(R.array.genre)
//        )

        // Nationalie // Todo fix (selection of n-1 datas)
//        val nationalitesLists = CcbRoomDatabase.getDatabase(this)?.nationaliteDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//        val nationalitesDatas: MutableList<CommonData> = mutableListOf()
//        nationalitesLists?.map {
//            CommonData(id = it.id, nom = it.nom)
//        }?.let {
//            nationalitesDatas.addAll(it)
//        }
//        selectNationaliteProducteur.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nationalitesDatas)
//        provideDatasSpinnerSelection(
//            selectNationaliteProducteur,
//            producteurDrafted.nationalite,
//            nationalitesDatas
//        )

        // Piece // Todo fix (selection of n-1 datas)
//        val piecesLists = CcbRoomDatabase.getDatabase(this)?.typePieceDao()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//        val piecesDatas: MutableList<CommonData> = mutableListOf()
//        piecesLists?.map {
//            CommonData(id = it.id, nom = it.nom)
//        }?.let {
//            piecesDatas.addAll(it)
//        }
//        selectPieceProducteur.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, piecesDatas)
//        provideDatasSpinnerSelection(
//            selectPieceProducteur,
//            producteurDrafted.piece,
//            piecesDatas
//        )

        // Etude // Todo fix (selection of n-1 datas)
//        val etudesLists = CcbRoomDatabase.getDatabase(this)?.niveauDoa()?.getAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
//        val etudesDatas: MutableList<CommonData> = mutableListOf()
//        etudesLists?.map {
//            CommonData(id = it.id, nom = it.nom)
//        }?.let {
//            etudesDatas.addAll(it)
//        }
//        selectEtudeProducteur.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, etudesDatas)
//        provideDatasSpinnerSelection(
//            selectEtudeProducteur,
//            producteurDrafted.etude,
//            etudesDatas
//        )

        //editAnneeCertificationProducteur.setText(producteurDrafted.anneeCertification ?: "0")
        //editCodeProducteur.setText(producteurDrafted.codeProd)
//        editNomProducteur.setText(producteurDrafted.nom)
//        editPrenomsProducteur.setText(producteurDrafted.prenoms)
//        editNaissanceProducteur.setText(producteurDrafted.dateNaiss)
//        editTelOneProducteur.setText(producteurDrafted.phone1)
//        //editTelTwoProducteur.setText(producteurDrafted.phoneTwo)
//        editPieceProducteur.setText(producteurDrafted.numPiece)
//
//        LogUtils.e(TAG, "from $fromAction")

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectTitreDUProducteur,
            itemChanged = arrayListOf(Pair(1, "Planté-partager")),
            currentVal = producteurDrafted.proprietaires,
            listIem = (AssetFileHelper.getListDataFromAsset(26, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId == 1){
                    containerPrecisionTitreProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectCertifProducteur,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            currentVal = producteurDrafted.certificats,
            listIem = (AssetFileHelper.getListDataFromAsset(20, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1){
                    containerAutreCertifProducteur.visibility = visibility
                }
            })

//        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = spinnerVarieteProducteur,
//            itemChanged = arrayListOf(Pair(1, "Autre")),
//            currentVal = producteurDrafted.variete,
//            listIem = (AssetFileHelper.getListDataFromAsset(21, this) as MutableList<CommonData>)?.map { it.nom }
//                ?.toList() ?: listOf(), onChanged = {
//
//            }, onSelected = { itemId, visibility ->
//                if(itemId==1){
//                    containerAutreVarieteProducteur.visibility = visibility
//                }
//            })

        setupSectionSelection(producteurDrafted.section, producteurDrafted.localitesId)
        setProgrammeSpinner(producteurDrafted.programme_id)

        setListenerForSpinner(this, "Choix du lieu","La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectHabitationProducteur,
            currentVal = producteurDrafted.habitationProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(22, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->

            })

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectStatutProducteur,
            itemChanged = arrayListOf(Pair(1, "Certifie")),
            currentVal = producteurDrafted.statutCertification,
            listIem = resources.getStringArray(R.array.status)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1){
                    linearAnneeCertificationProducteur.visibility = visibility
                    linearCodeContainerProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectSexeProducteur,
            currentVal = producteurDrafted.sexeProducteur,
            listIem = resources.getStringArray(R.array.genre)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectStatutMatProducteur,
            currentVal = producteurDrafted.statutMatrimonial,
            listIem = (AssetFileHelper.getListDataFromAsset(23, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->


            })

        setListenerForSpinner(this, "Choix de votre nationlité", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectNationaliteProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(5, this) as MutableList<NationaliteModel>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Choix de votre nationlité", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectProcheProducteur,
            listIem = listOf(),
            itemChanged = arrayListOf(Pair(1, "Oui")),
            currentVal = producteurDrafted.autreMembre,
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) {
                    containerEntantqueProducteur.visibility = visibility
                    containerMembreNumProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, "Votre choix", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectEtudeProducteur,
            currentVal = producteurDrafted.etude,
            listIem = resources.getStringArray(R.array.niveauEtude).toList(),
            onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Votre choix", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectPieceProducteur,
            currentVal = producteurDrafted.piece,
            listIem = (AssetFileHelper.getListDataFromAsset(13, this) as MutableList<TypePieceModel>)?.map { it.nom },
            onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, "Votre choix", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectCarteCMUProducteur,
            currentVal = producteurDrafted.carteCMU,
            listIem = resources.getStringArray(R.array.YesOrNo).toList(),
            itemChanged = listOf(Pair(1, "Oui")),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) containerNumPieceCMUProducteur.visibility = visibility
            })

        setListenerForSpinner(this, "Votre choix", "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectTypeCarteCSSProducteur,
            currentVal = producteurDrafted.typeCarteSecuriteSociale,
            listIem = (AssetFileHelper.getListDataFromAsset(27, this) as MutableList<CommonData>)?.map { it.nom },
            itemChanged = listOf(Pair(1, "CNPS"), Pair(2, "CMU")),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                containerNumCarteCSSProducteur.visibility = visibility
            })

        //LogUtils.json(producteurDrafted)
        passSetupProducteurModel(producteurDrafted)

    }


    fun draftProducteur(draftModel: DataDraftedModel?) {

        val producteurModelItem = getProducteurObjet()

        if(producteurModelItem == null) return

        val producteurDraft = producteurModelItem?.first.apply {
            this?.apply {
                photo = profilPhotoPath ?: ""
                section = sectionCommon.id.toString()
                localitesId = localiteCommon.id.toString()
                programme_id = programmeCommon.id.toString()
            }
        }

        //LogUtils.json(producteurDraft)

        showMessage(
            message = "Voulez-vous vraiment mettre ce contenu au brouillon afin de reprendre ulterieurement ?",
            context = this,
            finished = false,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.insert(
                    DataDraftedModel(
                        uid = draftModel?.uid ?: 0,
                        datas = ApiClient.gson.toJson(producteurDraft),
                        typeDraft = "producteur",
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )
                )

                showMessage(
                    message = "Contenu ajouté aux brouillons !",
                    context = this,
                    finished = true,
                    callback = {
                        Commons.playDraftSound(this)
                        imageDraftProducteur.startAnimation(loadShakeAnimation(this))
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producteur)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1010
            )
        }

        producteurDao = CcbRoomDatabase.getDatabase(this)?.producteurDoa()

        setAllClickListener()

        setOtherListener()

        setImageObject()

        addFilterToItem()

        if (intent.getStringExtra("from") != null) {
            fromAction = intent.getStringExtra("from") ?: ""
            draftedDataProducteur = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
            undraftedDatas(draftedDataProducteur!!)
        }else{
            setAllSelection()
        }
    }

    private fun setOtherListener() {
//        editAnneeCertificationProducteur.doAfterTextChanged {
//            val textFiedl = it?.toString()
//
//            if (textFiedl.toString().length == 4) {
//                if (textFiedl.toString().trim().toInt() < 1960) {
//                    showMessage(
//                        "La date ne doit pas etre inferieur à 1960",
//                        this,
//                        finished = false,
//                        {},
//                        "OK",
//                        deconnec = false,
//                        showNo = false
//                    )
//                    return@doAfterTextChanged
//                }
//            }
//        }

        //signatureProducteur.setOnSignedListener(this)

        /*clickAddFarm.setOnClickListener {
            if (editCultureProducteur.text.toString().isEmpty() || editSuperficeProducteur.text.toString().isEmpty()) {
                showMessage("Renseignez une culture, svp !", this)
                return@setOnClickListener
            }

            val cultureProducteur = CultureProducteurModel(
                0,
                12,
                editCultureProducteur.text.toString().trim(),
                editSuperficeProducteur.text.toString().trim(), SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            addCultureProducteur(cultureProducteur)
        }*/
    }

    private fun addFilterToItem() {
        applyFilters(editTelOneProducteur)
        //applyFilters(editTelTwoProducteur)
    }

    private fun setImageObject() {
        whichPhoto = 3
        createImageFile()
    }

    private fun setAllClickListener() {

        editNaissanceProducteur.setOnClickListener {
            datePickerDialog = null
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
                editNaissanceProducteur.setText(Commons.convertDate("${day}-${(month + 1)}-$year", false))
                dateNaissance = editNaissanceProducteur.text?.toString()!!
            }, year, month, dayOfMonth)

            datePickerDialog!!.datePicker.maxDate = 1104534000000
            datePickerDialog?.show()
        }

        clickSaveProducteur.setOnClickListener {
            //Commons.convertBitmap2File(signatureProducteur.signatureBitmap, signaturePath)
            collectDatas()
        }

        clickCloseProducteur.setOnClickListener {
            finish()
        }

        imagePhotoProfilProducteur.setOnClickListener {
            whichPhoto = 0
            dialogPickerPhoto()
        }

//        imagePhotoRectoProducteur.setOnClickListener {
//            whichPhoto = 1
//            dialogPickerPhoto()
//        }
//
//        imagePhotoVersoProducteur.setOnClickListener {
//            whichPhoto = 2
//            dialogPickerPhoto()
//        }
//
//        labelSignatureClearProducteur.setOnClickListener {
//            signatureProducteur.clear()
//        }

        imageDraftProducteur.setOnClickListener {
            draftProducteur(draftedDataProducteur ?: DataDraftedModel(uid = 0))
        }

        editAnneeCertificationProducteur.setOnClickListener {
            datePickerDialog = null
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)


            datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->

                editAnneeCertificationProducteur.setText("$year")
                anneeCertification = editAnneeCertificationProducteur.text?.toString()!!
            }, year, month, dayOfMonth)

            datePickerDialog!!.datePicker.minDate = DateTime.parse("01/01/1960", DateTimeFormat.forPattern("dd/MM/yyyy")).millis
            datePickerDialog!!.datePicker.maxDate = DateTime.now().millis
            datePickerDialog?.show()
        }

    }

    override fun itemSelected(position: Int, item: CultureProducteurModel) {
        TODO("Not yet implemented")
    }
}
