package ci.projccb.mobile.activities.infospresenters

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.ParcelleActivity
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.ExportUtils
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_parcelle_preview.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ParcellePreviewActivity : AppCompatActivity(R.layout.activity_parcelle_preview) {


    var parcellePoo = ParcelleModel()
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0
    var labelSaving: AppCompatTextView? = null
    var labelSharing: AppCompatTextView? = null
    var whichButton = 0


    fun collecteDatas() {
        labelProducteurParcellePreview.text = parcellePoo.producteurNom
        labelCultueParcellePreview.text = parcellePoo.culture
        labelDeclarationParcellePreview.text = parcellePoo.typedeclaration
        labelSupeficieParcellePreview.text = parcellePoo.superficie
        labelLatLngParcellePreview.text = parcellePoo.latitude?.plus("/").plus(parcellePoo.longitude)
        labelAnneeParcellePreview.text = parcellePoo.anneeCreation
        labelWayPointsParcellePreview.text = parcellePoo.wayPointsString
    }


    fun buildExport(parcellePo: ParcelleModel, type: Int, action: Int) {

        MainScope().launch {
            withContext(IO) {
                try {
                    if (type == 1) ExportUtils.expotToKml(parcellePo.producteurNom?.lowercase(), parcellePo, action,this@ParcellePreviewActivity)
                    else ExportUtils.exportToGpx(parcellePo.producteurNom?.lowercase(), parcellePo, action, this@ParcellePreviewActivity)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.dialog_action, null)

        labelSaving = view.findViewById(R.id.labelSavingDialog)
        labelSharing = view.findViewById(R.id.labelSharingDialog)

        // below line is use to set cancelable to avoid
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        dialog.setContentView(view)


        intent?.let {
            try {
                parcellePoo = it.getParcelableExtra("preview")!!
                draftID = it.getIntExtra("draft_id", 0)
                collecteDatas()
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        labelSaving?.setOnClickListener {
            try {
                dialog.dismiss()
                buildExport(parcellePoo, whichButton, 2)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        labelSharing?.setOnClickListener {
            try {
                dialog.dismiss()
                buildExport(parcellePoo, whichButton, 1)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        clickSaveParcellePreview.setOnClickListener {
            try {
                showMessage(
                    "Etes-vous sur de vouloir faire ce enregistrement ?",
                    this,
                    showNo = true,
                    callback = {
                        CcbRoomDatabase.getDatabase(this)?.parcelleDao()?.insert(parcellePoo)
                        draftDao?.completeDraft(draftID)
                        Commons.synchronisation(type = "menage", this)
                        showMessage(
                            "Parcelle enregistrée avec succes !",
                            this,
                            finished = true,
                            callback = {})
                    },
                    finished = false
                )
                ActivityUtils.finishActivity(ParcelleActivity::class.java)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickKmlParcellePreview.setOnClickListener {
            whichButton = 1
            dialog.show()
        }

        clickGpxParcellePreview.setOnClickListener {
            whichButton = 2
            dialog.show()
        }

        if (parcellePoo.wayPointsString.toString().isEmpty()) {
            clickGpxParcellePreview.visibility = View.GONE
            clickKmlParcellePreview.visibility = View.GONE
        }
        
        
        if (parcellePoo.typedeclaration.toString().lowercase() == "verbale") {
            try {
                linearActionExportContainerParcellePreview.visibility = View.GONE
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
