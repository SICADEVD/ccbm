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
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner
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
import ci.projccb.mobile.tools.Commons.Companion.encodeFileToBase64Binary
import ci.projccb.mobile.tools.Commons.Companion.getAllTitleAndValueViews
import ci.projccb.mobile.tools.Commons.Companion.getSpinnerContent
import ci.projccb.mobile.tools.Commons.Companion.limitEDTMaxLength
import ci.projccb.mobile.tools.Commons.Companion.loadShakeAnimation
import ci.projccb.mobile.tools.Commons.Companion.setAllValueOfTextViews
import ci.projccb.mobile.tools.Commons.Companion.setListenerForSpinner
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.showYearPickerDialog
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.*
import com.github.gcacace.signaturepad.views.SignaturePad.OnSignedListener
import com.google.gson.reflect.TypeToken
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_parcelle.imageDraftBtn
import kotlinx.android.synthetic.main.activity_parcelle.labelTitleMenuAction
import kotlinx.android.synthetic.main.activity_producteur.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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


    private val commomUpdate = CommonData()
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
                if(it.id.toString() == idc.toString()) libItem = it.libelle
            }
        }

        setListenerForSpinner(this, getString(R.string.choix_de_la_section), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
                if(it.id.toString() == idc.toString()) libItem = it.libelle
            }
        }

        setListenerForSpinner(this,
            getString(R.string.choix_du_programme),
            getString(R.string.la_liste_des_programmes_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if(programmeListi?.size!! > 0) false else true,
            currentVal = libItem,
            itemChanged = arrayListOf(Pair(1, "Certifie")),
            spinner = selectProgramProducteur,
            listIem = programmeListi?.map { it.libelle }
            ?.toList() ?: listOf(), onChanged = {

            val programme = programmeListi!![it]
            programmeCommon.nom = programme.libelle!!
            programmeCommon.id = programme.id!!

        }, onSelected = { itemId, visibility ->
                //if(itemId == 1) containerAutreProgramProducteur.visibility = visibility
        })

    }

    private fun setLocaliteSpinner(id: Int, currVal1:String? = null) {

        var localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa();
        var localitesListi = localiteDao?.getLocaliteBySection(id)
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal1?.let { idc ->
            localitesListi?.forEach {
                if(it.id.toString() == idc.toString()) libItem = it.nom
            }
        }

        setListenerForSpinner(this, getString(R.string.choix_de_la_localit), getString(R.string.la_liste_des_localit_s_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            isEmpty = if(localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
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


    fun addCultureProducteur(cultureProducteurModel: CultureProducteurModel) {
        if (cultureProducteurModel.label?.length == 0) return

        cultureProducteurs?.forEach {
            if (it.label?.uppercase() == cultureProducteurModel.label?.uppercase() && it.superficie == cultureProducteurModel.superficie) {
                ToastUtils.showShort(getString(R.string.cette_culture_est_deja_ajout_e))
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
        arrayNationalites?.add(getString(R.string.choisir_la_nationalit))

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

                if (consentProducteur == getString(R.string.non)) finish()
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

        arrayTypePieces.add(getString(R.string.choisir_la_piece))

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



    fun collectDatas() {

        val producteurModelItem = getProducteurObjet()

        if(producteurModelItem == null) return

//        LogUtils.d(selectNationaliteProducteur.getSpinnerContent().trim())

        val producteur = producteurModelItem?.first.apply {
            this?.apply {
                photo = profilPhotoPath ?: ""
                profilPhotoPath = profilPhotoPath ?: ""
                section = sectionCommon.id.toString()
                localitesId = localiteCommon.id.toString()
                programme_id = programmeCommon.id.toString()

                nationalite = (AssetFileHelper.getListDataFromAsset(5, this@ProducteurActivity) as MutableList<NationaliteModel>)?.filter { it.nom?.contains(selectNationaliteProducteur.getSpinnerContent().trim()) == true }?.let {
//                    LogUtils.d(it)
                    if(it.size > 0){
                        it.first().id
                    }else null
                }.toString()

                certificatsStr = GsonUtils.toJson(selectCertifProducteur.selectedStrings)
            }
        }

        if(intent.getIntExtra("sync_uid", 0) != 0){
            producteur.apply {
                id = commomUpdate.listOfValue?.first()?.toInt()
                uid = commomUpdate.listOfValue?.get(1)?.toInt()?:0
                isSynced = false
                origin = "local"
            }
        }

//        Commons.logErrorToFile(producteur)

        val intentProducteurPreview = Intent(this, ProducteurPreviewActivity::class.java)
        intentProducteurPreview.putExtra("preview", producteur)
        val mapEntries: List<MapEntry>? = producteurModelItem?.second?.map { MapEntry(it.first, it.second) }
        intentProducteurPreview.putParcelableArrayListExtra("previewitem", ArrayList(mapEntries))
        intentProducteurPreview.putExtra("draft_id", draftedDataProducteur?.uid)
        startActivity(intentProducteurPreview)
    }

    private fun getProducteurObjet(isMissingDial:Boolean = true, necessaryItem: MutableList<String> = arrayListOf()): Pair<ProducteurModel, MutableList<Pair<String, String>>>? {

        var isMissingDial2 = false

        var itemList = getSetupProducteurModel(ProducteurModel(uid = 0, id = 0, isSynced = false,
                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()), mutableListOf<Pair<String,String>>())
        //LogUtils.d(.toString())
        var allField = itemList.second
        var isMissing = false
        var message = ""

        var notNecessaire = listOf<String>(
            getString(R.string.en_tant_que).lowercase(),
            getString(R.string.num_ro_de_t_l_phone).lowercase(),
            getString(R.string.n_de_la_pi_ce_cmu).lowercase(),
            getString(R.string.n_de_carte_de_s_curit_sociale).lowercase())

        LogUtils.json(allField)

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
//            consentement = getString(R.string.oui),
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
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectionnez_la_photo)), pView)
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
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }


    fun setAllSelection() {

        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTitreDUProducteur,
            itemChanged = arrayListOf(Pair(1, "Planté-partager")),
            listIem = (AssetFileHelper.getListDataFromAsset(26, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId == 1){
                    containerPrecisionTitreProducteur.visibility = visibility
                }
            })

        setupCertificatMultiSelection()
//        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
//            spinner = selectCertifProducteur,
//            itemChanged = arrayListOf(Pair(1, "Autre")),
//            listIem = (AssetFileHelper.getListDataFromAsset(20, this) as MutableList<CommonData>)?.map { it.nom }
//                ?.toList() ?: listOf(), onChanged = {
//
//            }, onSelected = { itemId, visibility ->
//                if(itemId==1){
//                    containerAutreCertifProducteur.visibility = visibility
//                }
//            })

        setupSectionSelection()
        setProgrammeSpinner()

        setListenerForSpinner(this,
            getString(R.string.habitez_vous_dans_un_campement_ou_village),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectHabitationProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(22, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->

            })

        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectStatutProducteur,
            itemChanged = arrayListOf(Pair(1, "Certifie")),
            listIem = resources.getStringArray(R.array.status)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1){
                    linearAnneeCertificationProducteur.visibility = visibility
                    //linearCodeContainerProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSexeProducteur,
            listIem = resources.getStringArray(R.array.genre)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this,
            getString(R.string.choix_du_statut), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectStatutMatProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(23, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this,
            getString(R.string.quelle_est_la_nationalit), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectNationaliteProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(5, this) as MutableList<NationaliteModel>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this,
            getString(R.string.avez_vous_des_proches), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectProcheProducteur,
            listIem = resources.getStringArray(R.array.YesOrNo).toList(),
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) {
                    containerEntantqueProducteur.visibility = visibility
                    containerMembreNumProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this,
            getString(R.string.votre_niveau_d_etude), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEtudeProducteur,
            listIem = resources.getStringArray(R.array.niveauEtude).toList(),
            onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        val listPiece = (AssetFileHelper.getListDataFromAsset(13, this) as MutableList<TypePieceModel>)
        setListenerForSpinner(this,
            getString(R.string.quel_type_de_pi_ce), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectPieceProducteur,
            listIem = listPiece?.map { it.nom },
            itemChanged = listOf(Pair(1, "Non disponible")),
            onChanged = {
                  //LogUtils.d(((listPiece.size) - 1).toString().plus(" - "+ it))
                  if((listPiece.size) - 1 > it) containerPieceProducteur.visibility = View.VISIBLE
            }, onSelected = { itemId, visibility ->
                if(itemId == 1){
                    containerPieceProducteur.visibility = View.GONE
                }
            })

        setListenerForSpinner(this,
            getString(R.string.votre_choix), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectCarteCMUProducteur,
            listIem = resources.getStringArray(R.array.YesOrNo).toList(),
            itemChanged = listOf(Pair(1, getString(R.string.oui))),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) containerNumPieceCMUProducteur.visibility = visibility
            })

        setListenerForSpinner(this, getString(R.string.votre_choix), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
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
        val dialogPicker = AlertDialog.Builder(this, R.style.DialogTheme)
        Commons.adjustTextViewSizesInDialog(this, dialogPicker, "",   this.resources.getDimension(R.dimen._6ssp)
            ,true)
        dialogPicker.setMessage(getString(R.string.source_de_la_photo))
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
    }


    fun undraftedDatas(draftedData: DataDraftedModel?, data: ProducteurModel? = null) {
        var producteurDrafted: ProducteurModel?  = null // = ApiClient.gson.fromJson(draftedData.datas, ProducteurModel::class.java)

        draftedData?.let {
            producteurDrafted = ApiClient.gson.fromJson(draftedData.datas, ProducteurModel::class.java)
            val intNullo = producteurDrafted?.section?.toIntOrNull()
            if(intNullo != null){
                setupSectionSelection(producteurDrafted!!.section, producteurDrafted!!.localite)
            }else setupSectionSelection()
        }

        data?.let {
            producteurDrafted = data
            commomUpdate.listOfValue = listOf<String>(data.id.toString(), data.uid.toString()).toMutableList()
            //product = CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.getProducteurByID(data.id?.toInt()?:0)
//            val sectionIt =  CcbRoomDatabase.getDatabase(this)?.sectionsDao()?.getById(product?.section)
//            val localiteIt =  CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.getLocalite(product?.localite?.toInt()?:0)
            setupSectionSelection(it?.section.toString(), it?.localitesId.toString())
            LogUtils.d(draftedData, it?.section.toString(), it?.localitesId.toString())
        }

        if(producteurDrafted?.sync_update == true){
            intent.putExtra("sync_uid", 1)
            labelTitleMenuAction.text = "MISE A JOUR FICHE PRODUCTEUR"
        }

        if(producteurDrafted == null) return

        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTitreDUProducteur,
            itemChanged = arrayListOf(Pair(1, "Planté-partager")),
            currentVal = producteurDrafted!!.proprietaires,
            listIem = (AssetFileHelper.getListDataFromAsset(26, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId == 1){
                    containerPrecisionTitreProducteur.visibility = visibility
                }
            })

        if(producteurDrafted!!.certificatsStr != null){

            setupCertificatMultiSelection(
                GsonUtils.fromJson(producteurDrafted!!.certificatsStr, object: TypeToken<MutableList<String>>(){}.type )
            )

        }else setupCertificatMultiSelection()

        //setupSectionSelection(producteurDrafted!!.section, producteurDrafted!!.localitesId)
        setProgrammeSpinner(producteurDrafted!!.programme_id)

        setListenerForSpinner(this, getString(R.string.habitez_vous_dans_un_campement_ou_village),getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectHabitationProducteur,
            currentVal = producteurDrafted!!.habitationProducteur,
            listIem = (AssetFileHelper.getListDataFromAsset(22, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->

            })

        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectStatutProducteur,
            itemChanged = arrayListOf(Pair(1, "Certifie")),
            currentVal = producteurDrafted!!.statutCertification,
            listIem = resources.getStringArray(R.array.status)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1){
                    linearAnneeCertificationProducteur.visibility = visibility
                    //linearCodeContainerProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectSexeProducteur,
            currentVal = producteurDrafted!!.sexeProducteur,
            listIem = resources.getStringArray(R.array.genre)
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectStatutMatProducteur,
            currentVal = producteurDrafted!!.statutMatrimonial,
            listIem = (AssetFileHelper.getListDataFromAsset(23, this) as MutableList<CommonData>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->


            })

        val nationVal = (AssetFileHelper.getListDataFromAsset(5, this) as MutableList<NationaliteModel>)?.filter { it.id.toString().equals(producteurDrafted!!.nationalite.toString()) == true }?.let {
            if(it.size > 0) it.first().nom
            else ""
        }.toString()
        setListenerForSpinner(this, getString(R.string.quelle_est_la_nationalit), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectNationaliteProducteur,
            currentVal = nationVal,
            listIem = (AssetFileHelper.getListDataFromAsset(5, this) as MutableList<NationaliteModel>)?.map { it.nom }
                ?.toList() ?: listOf(), onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        setListenerForSpinner(this, getString(R.string.avez_vous_des_proches), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectProcheProducteur,
            listIem = resources.getStringArray(R.array.YesOrNo).toList(),
            itemChanged = arrayListOf(Pair(1, getString(R.string.oui))),
            currentVal = producteurDrafted!!.autreMembre,
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) {
                    containerEntantqueProducteur.visibility = visibility
                    containerMembreNumProducteur.visibility = visibility
                }
            })

        setListenerForSpinner(this, getString(R.string.votre_choix), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectEtudeProducteur,
            currentVal = producteurDrafted!!.etude,
            listIem = resources.getStringArray(R.array.niveauEtude).toList(),
            onChanged = {

            }, onSelected = { itemId, visibility ->
            })

        val listPiece = (AssetFileHelper.getListDataFromAsset(13, this) as MutableList<TypePieceModel>)
        setListenerForSpinner(this, getString(R.string.quel_type_de_pi_ce), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectPieceProducteur,
            currentVal = producteurDrafted!!.piece,
            listIem = listPiece?.map { it.nom },
            itemChanged = listOf(Pair(1, "Non disponible")),
            onChanged = {
                //LogUtils.d(((listPiece.size) - 1).toString().plus(" - "+ it))
                if((listPiece.size) - 1 > it) containerPieceProducteur.visibility = View.VISIBLE
            }, onSelected = { itemId, visibility ->
                if(itemId == 1){
                    containerPieceProducteur.visibility = View.GONE
                }
            })

        setListenerForSpinner(this, getString(R.string.votre_choix), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectCarteCMUProducteur,
            currentVal = producteurDrafted!!.carteCMU,
            listIem = resources.getStringArray(R.array.YesOrNo).toList(),
            itemChanged = listOf(Pair(1, getString(R.string.oui))),
            onChanged = {

            }, onSelected = { itemId, visibility ->
                if(itemId==1) containerNumPieceCMUProducteur.visibility = visibility
            })

        setListenerForSpinner(this, getString(R.string.votre_choix), getString(R.string.la_liste_des_sections_semble_vide_veuillez_proc_der_la_synchronisation_des_donn_es_svp),
            spinner = selectTypeCarteCSSProducteur,
            currentVal = producteurDrafted!!.typeCarteSecuriteSociale,
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

        val producteurModelItem = getProducteurObjet(false, necessaryItem = mutableListOf<String>(
            getString(R.string.nom_du_producteur),
            getString(R.string.pr_nom_s_du_producteur)
        ))

        if(producteurModelItem == null) return

        val producteurDraft = producteurModelItem?.first.apply {
            this?.apply {
                profilPhotoPath = profilPhotoPath ?: ""
                section = sectionCommon.id.toString()
                localitesId = localiteCommon.id.toString()
                programme_id = programmeCommon.id.toString()

                nationalite = (AssetFileHelper.getListDataFromAsset(5, this@ProducteurActivity) as MutableList<NationaliteModel>)?.filter {  it.nom?.contains(selectNationaliteProducteur.getSpinnerContent().trim()) == true }?.let {
                    if(it.size > 0){
                        it.first().id
                    }else null
                }.toString()

                certificatsStr = GsonUtils.toJson(selectCertifProducteur.selectedStrings)
            }

            if(intent.getIntExtra("sync_uid", 0) != 0 || this.sync_update){
                this.id = commomUpdate.listOfValue?.first()?.toInt()
                this.uid = commomUpdate.listOfValue?.get(1)?.toInt()?:0
                this.sync_update = true
            }
        }

        //LogUtils.json(producteurDraft)

        showMessage(
            message = getString(R.string.voulez_vous_vraiment_mettre_ce_contenu_au_brouillon_afin_de_reprendre_ulterieurement),
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
                    message = getString(R.string.contenu_ajout_aux_brouillons),
                    context = this,
                    finished = true,
                    callback = {
                        Commons.playDraftSound(this)
                        imageDraftProducteur.startAnimation(loadShakeAnimation(this))
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

    fun setupCertificatMultiSelection(currentList : MutableList<String> = mutableListOf()) {
        val certificatList = (AssetFileHelper.getListDataFromAsset(20, this) as MutableList<CommonData>)?.map { it.nom }?.toList() ?: listOf()
        var listSelectCertificatPosList = mutableListOf<Int>()
        var listSelectCertificatList = mutableListOf<String>()

        var indItem = 0
        (certificatList)?.forEach {
            if(currentList.size > 0){ if(currentList.contains(it)) listSelectCertificatPosList.add(indItem) }
            indItem++
        }

        selectCertifProducteur.setTitle(getString(R.string.quels_sont_les_certificats))
        selectCertifProducteur.setItems(certificatList)
        //multiSelectSpinner.hasNoneOption(true)
        selectCertifProducteur.setSelection(listSelectCertificatPosList.toIntArray())
        selectCertifProducteur.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
            override fun selectedIndices(indices: MutableList<Int>?) {
                listSelectCertificatPosList.clear()
                listSelectCertificatPosList.addAll(indices?.toMutableList() ?: mutableListOf())
            }

            override fun selectedStrings(strings: MutableList<String>?) {
                listSelectCertificatList.clear()
                listSelectCertificatList.addAll(strings?.toMutableList() ?: arrayListOf())
                if(listSelectCertificatList.contains("Autre")) containerAutreCertifProducteur.visibility = View.VISIBLE else containerAutreCertifProducteur.visibility = View.GONE
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producteur)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

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

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(R.id.layout_producteur),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        setAllClickListener()

        setOtherListener()

        setImageObject()

        //addFilterToItem()

        if (intent.getStringExtra("from") != null) {
            if(intent.getIntExtra("drafted_uid", 0) != 0){
                fromAction = intent.getStringExtra("from") ?: ""
                draftedDataProducteur = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataProducteur!!)
            }else{
                val dataUid = intent.getIntExtra("sync_uid", 0)
                LogUtils.d(dataUid)
                if(dataUid != 0) {
                    labelTitleMenuAction.text = "MISE A JOUR FICHE PRODUCTEUR"
//                    clickSaveInspection.setOnClickListener {
//                        collectDatasUpdate(inspectUid)
//                    }
//                    imageDraftProducteur.visibility = View.GONE

                    val updateData = CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.getProducteurByUID(dataUid)
                    //LogUtils.d(updateData)
                    updateData?.let {
                        undraftedDatas(null, it)
                    }
                }
            }
        }else{
            setAllSelection()
        }
    }

    private fun setOtherListener() {

        limitEDTMaxLength(editTelOneProducteur, 10, 10)
        limitEDTMaxLength(editMembreNumProducteur, 10, 10)

        //limitEDTMaxLength(editPieceProducteur, 10)
        limitEDTMaxLength(editCarteCCCProducteur, 11, 11)
        //limitEDTMaxLength(editNumCarteCSSProducteur, 12)
        //limitEDTMaxLength(editNumPieceCMUProducteur, 12)



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
        //applyFilters(editTelOneProducteur)
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

        clickCancelProducteur.setOnClickListener {
            //ActivityUtils.startActivity(Intent(this, ProducteurActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            //ActivityUtils.getActivityByContext(this)?.finish()
            if(intent.getIntExtra("sync_uid", 0) != 0){
                ActivityUtils.getActivityByContext(this)?.finish()
            }else {
                ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                ActivityUtils.getActivityByContext(this)?.finish()
            }
        }

        imagePhotoProfilProducteur.setOnClickListener {
            whichPhoto = 0
            dialogPickerPhoto()
        }

        imageDraftProducteur.setOnClickListener {
            draftProducteur(draftedDataProducteur ?: DataDraftedModel(uid = 0))
        }

        editAnneeCertificationProducteur.setOnClickListener {
            showYearPickerDialog( it as EditText )
        }

    }

    override fun itemSelected(position: Int, item: CultureProducteurModel) {
        TODO("Not yet implemented")
    }
}
