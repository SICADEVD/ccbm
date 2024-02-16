package ci.projccb.mobile.activities.infospresenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.ProducteurActivity
import ci.projccb.mobile.adapters.PreviewItemAdapter
import ci.projccb.mobile.models.ProducteurModel
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.synchronisation
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ApiUtils.Api
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_livraison_preview.recyclerInfoLivraison
import kotlinx.android.synthetic.main.activity_producteur_preview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


class ProducteurPreviewActivity : AppCompatActivity() {


    private val producteurItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
    var producteurInfos: ProducteurModel? = null
    var whichPhoto = 0
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producteur_preview)

        intent?.let {
            try {

                producteurInfos = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                val producteurItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                producteurItemListData?.forEach {
                   if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value.replace(", ", "\n"),
                            producteurItemsListPrev
                        )
                    }
                }
//                LogUtils.d(producteurItemListData)
//                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(producteurItemsListPrev)
                recyclerInfoPrev.adapter = rvPrevAdapter
                recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                producteurInfos?.also { producteur ->

//                    labelLocaliteProdPreview.text = producteur.localite
//                    labelCodeProdPreview.text = producteur.codeProd ?: ""
//                    labelNomProdPreview.text = producteur.nom
//                    labelPrenomProdPreview.text = producteur.prenoms
//                    labelSexeProdPreview.text = producteur.sexeProducteur
////                    labelNaissanceProdPreview.text = producteur.naissance
////                    labelNationnaliteProdPreview.text = producteur.nationalite
////                    labelContactOneProdPreview.text = producteur.phoneOne
////                    labelContactTwoProdPreview.text = producteur.phoneTwo ?: ""
////                    labelPieceNumProdPreview.text = producteur.pieceNumber
//                    labelPieceProdPreview.text = producteur.piece
//                    labelEtudeProdPreview.text = producteur.etude
//
//                    labelStatutCertificationProdPreview.text = producteur.statutCertification
//                    labelAnneeCertificationProdPreview.text = producteur.anneeCertification ?: ""
//
                    CoroutineScope(Dispatchers.IO).launch {
                        whichPhoto = 0
                        loadFileToBitmap(producteur.photo)
                    }
//
////                    producteur.rectoPath?.let {
////                        CoroutineScope(Dispatchers.IO).launch {
////                            whichPhoto = 1
////                            loadFileToBitmap1(producteur.rectoPath)
////                        }
////                    }
//
////                    producteur.versoPath?.let {
////                        CoroutineScope(Dispatchers.IO).launch {
////                            whichPhoto = 2
////                            loadFileToBitmap2(producteur.versoPath)
////                        }
////                    }
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        whichPhoto = 3
//                        loadFileToBitmap3(producteur.esignaturePath)
//                    }
//
                    clickSavePreview.setOnClickListener {
                        try {
                            showMessage(
                                "Etes-vous sur de vouloir faire cet enregistrement ?",
                                this,
                                showNo = true,
                                callback = {
                                    CcbRoomDatabase.getDatabase(this)?.producteurDoa()?.insert(producteur)
                                    draftDao?.completeDraft(draftID)
                                    synchronisation(type = "producteur", this)
                                    showMessage(
                                        "Producteur enregistré avec succès !",
                                        this,
                                        finished = true,
                                        callback = {})
                                },
                                finished = false
                            )

                            ActivityUtils.finishActivity(ProducteurActivity::class.java)
                        } catch (ex: Exception) {
                            showMessage("Echec d'enrégistrement !", this, callback = {})
                        }
                    }
//
//                    clickCloseBtn.setOnClickListener {
//                        finish()
//                    }
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }


    suspend fun loadFileToBitmap(pPath: String?) {
        if (pPath?.isEmpty()!!) return

        val imgFile = File(pPath)

        if (imgFile.exists()) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 8
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath, options)


            MainScope().launch {
                    imageProfileProdPreview.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 80, 80, false))
            }
        }
    }


//    suspend fun loadFileToBitmap1(pPath: String?) {
//        if (pPath?.isEmpty()!!) return
//
//        val imgFile = File(pPath)
//
//        if (imgFile.exists()) {
//            val options = BitmapFactory.Options()
//            options.inSampleSize = 8
//            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath, options)
//
//
//            MainScope().launch {
//                    imageRectoProdPreview.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 80, 80, false))
//            }
//        }
//    }


//    suspend fun loadFileToBitmap2(pPath: String?) {
//        if (pPath?.isEmpty()!!) return
//
//        val imgFile = File(pPath)
//
//        if (imgFile.exists()) {
//            val options = BitmapFactory.Options()
//            options.inSampleSize = 8
//            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath, options)
//
//
//            MainScope().launch {
//                    imageVersoProdPreview.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 80, 80, false))
//            }
//        }
//    }


//    suspend fun loadFileToBitmap3(pPath: String?) {
//        if (pPath?.isEmpty()!!) return
//
//        val imgFile = File(pPath)
//
//        if (imgFile.exists()) {
//            //val options = BitmapFactory.Options()
//            //options.inSampleSize = 8
//            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//
//
//            MainScope().launch {
//                imageSignatureProdPreview.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 80, 80, false))
//            }
//        }
//    }
}
