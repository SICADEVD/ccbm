package ci.projccb.mobile.activities.infospresenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.SuiviApplicationActivity
import ci.projccb.mobile.models.SuiviApplicationModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_suivi_application_preview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class SuiviApplicationPreviewActivity : AppCompatActivity() {


    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0


    suspend fun loadFileToBitmap(pPath: String?, viewTarget: AppCompatImageView) {
        try {
            if (pPath?.isEmpty()!!) return

            val imgFile = File(pPath)
            LogUtils.d(pPath)

            if (imgFile.exists()) {
                val options = BitmapFactory.Options()
                options.inSampleSize = 8
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath, options)

                MainScope().launch {
                    viewTarget.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 80, 80, false))
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_application_preview)

        intent?.let {
            try {
                val suiviApplication: SuiviApplicationModel? = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)


                suiviApplication?.let { suiviApp ->
                    try {
                        labelApplicateurNomSuiviApplicationPreview.text = suiviApp.applicateurNom
                        labelLocaliteNomSuiviApplicationPreview.text = suiviApp.localiteNom
                        labelProducteurNomSuiviApplicationPreview.text = suiviApp.producteurNom

                        labelCampagneNomSuiviApplicationPreview.text = suiviApp.campagneNom
                        labelCultureNomSuiviApplicationPreview.text = suiviApp.cultureNom

                        labelSuperficieSuiviApplicationPreview.text = suiviApp.superficiePulverisee
                        labelProduitNomSuiviApplicationPreview.text = suiviApp.marqueProduitPulverise

                        labelDegreDangerositeSuiviApplicationPreview.text = suiviApp.degreDangerosite
                        labelRaisonApplicationSuiviApplicationPreview.text = suiviApp.raisonApplication

                        labelDelaiReentreSuiviApplicationPreview.text = suiviApp.delaisReentree
                        labelTamponYesNoSuiviApplicationPreview.text = suiviApp.zoneTampons

                        if (suiviApp.zoneTampons == "oui") linearTamponPhotoContainerSuiviApplicationPreview.visibility = View.VISIBLE
                        else linearTamponPhotoContainerSuiviApplicationPreview.visibility = View.GONE

                        labelDoucheApplicateurYesNoSuiviApplicationPreview.text = suiviApp.presenceDouche

                        if (suiviApp.presenceDouche == "oui") linearDouchePhotoContainerSuiviApplicationPreview.visibility = View.VISIBLE
                        else linearDouchePhotoContainerSuiviApplicationPreview.visibility = View.GONE

                        labelDateApplicationSuiviApplicationPreview.text = suiviApp.dateApplication
                        labelHeureDebutApplicationSuiviApplicationPreview.text = suiviApp.heureApplication
                        labelHeureFinApplicationSuiviApplicationPreview.text = suiviApp.heureFinApplication

                        labelRaisonApplicationSuiviApplicationPreview

                        suiviApp.photoTamponPath?.let {
                            CoroutineScope(Dispatchers.IO).launch {
                                loadFileToBitmap(suiviApp.photoTamponPath, imageTamponPhotoSuiviApplicationPreview)
                            }
                        }

                        suiviApp.photoDouchePath?.let {
                            CoroutineScope(Dispatchers.IO).launch {
                                loadFileToBitmap(suiviApp.photoDouchePath, imageDouchePhotoSuiviApplicationPreview)
                            }
                        }
                    } catch (ex: Exception) {
                        LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                    }
                }

                clickCloseBtn.setOnClickListener {
                    finish()
                }

                clickSaveApplicationSuiviPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            CcbRoomDatabase.getDatabase(this)?.suiviApplicationDao()?.insert(suiviApplication!!)
                            draftDao?.completeDraft(draftID)
                            Commons.synchronisation(type = "suiviapplication", this)
                            Commons.showMessage(
                                "Suivi d'application effectué avec succes !",
                                this,
                                finished = true,
                                callback = {})
                        },
                        finished = false
                    )

                    ActivityUtils.finishActivity(SuiviApplicationActivity::class.java)
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
