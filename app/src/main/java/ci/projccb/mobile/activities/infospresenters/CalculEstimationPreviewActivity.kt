package ci.projccb.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.CalculEstimationActivity
import ci.projccb.mobile.models.EstimationModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_calcul_estimation_preview.*

class CalculEstimationPreviewActivity : AppCompatActivity() {


    var estimationDatas: EstimationModel? = null
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcul_estimation_preview)


        clickCloseBtn.setOnClickListener {
            finish()
        }

        intent?.let {
            try {
                estimationDatas = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                estimationDatas?.let { estimation ->
                    labelLocaliteNomEstimationPreview.text = estimation.localiteNom
                    labelCampagneNomEstimationPreview.text = estimation.campagnesNom
                    labelProducteurNomEstimationPreview.text = estimation.producteurNom
                    labelParclelleNomEstimationPreview.text = estimation.parcelleNom
                    labelSuperficieEstimationPreview.text = estimation.superficie
                    labelPiedA1EstimationPreview.text = estimation.ea1
                    labelPiedA2EstimationPreview.text = estimation.ea2
                    labelPiedA3EstimationPreview.text = estimation.ea3
                    labelPiedB1EstimationPreview.text = estimation.eb1
                    labelPiedB2EstimationPreview.text = estimation.eb2
                    labelPiedB3EstimationPreview.text = estimation.eb3
                    labelPiedC1EstimationPreview.text = estimation.ec1
                    labelPiedC2EstimationPreview.text = estimation.ec2
                    labelPiedC3EstimationPreview.text = estimation.ec3
                    labelDateEstimationPreview.text = estimation.dateEstimation
                }

                clickSaveEstimationPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            CcbRoomDatabase.getDatabase(this)?.estimationDao()
                                ?.insert(estimationDatas!!)
                            draftDao?.completeDraft(draftID)
                            Commons.synchronisation(type = "estimation", this)
                            Commons.showMessage(
                                "Estimation enregistrée !",
                                this,
                                finished = true,
                                callback = {})
                        },
                        finished = false
                    )

                    ActivityUtils.finishActivity(CalculEstimationActivity::class.java)
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
