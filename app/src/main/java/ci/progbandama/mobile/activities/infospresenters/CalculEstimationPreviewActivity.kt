package ci.progbandama.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.CalculEstimationActivity
import ci.progbandama.mobile.databinding.ActivityCalculEstimationPreviewBinding
import ci.progbandama.mobile.models.EstimationModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CalculEstimationPreviewActivity : AppCompatActivity() {


    var estimationDatas: EstimationModel? = null
    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    private lateinit var binding: ActivityCalculEstimationPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculEstimationPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        intent?.let {
            try {
                estimationDatas = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                estimationDatas?.let { estimation ->
                    binding.labelLocaliteNomEstimationPreview.text = estimation.localiteNom
                    binding.labelCampagneNomEstimationPreview.text = estimation.campagnesNom
                    binding.labelProducteurNomEstimationPreview.text = estimation.producteurNom
                    binding.labelParclelleNomEstimationPreview.text = estimation.parcelleNom
                    binding.labelTypEstEstimationPreview.text = estimation.typeEstimation
                    binding.labelSuperficieEstimationPreview.text = estimation.superficie
                    binding.labelPAjustEstimationPreview.text = estimation.ajustement
                    binding.labelRFinEstimationPreview.text = estimation.rendFinal
                    binding.labelREstiEstimationPreview.text = estimation.recolteEstime
                    binding.labelPiedA1EstimationPreview.text = estimation.ea1
                    binding.labelPiedA2EstimationPreview.text = estimation.ea2
                    binding.labelPiedA3EstimationPreview.text = estimation.ea3
                    binding.labelPiedB1EstimationPreview.text = estimation.eb1
                    binding.labelPiedB2EstimationPreview.text = estimation.eb2
                    binding.labelPiedB3EstimationPreview.text = estimation.eb3
                    binding.labelPiedC1EstimationPreview.text = estimation.ec1
                    binding.labelPiedC2EstimationPreview.text = estimation.ec2
                    binding.labelPiedC3EstimationPreview.text = estimation.ec3
                    binding.labelDateEstimationPreview.text = estimation.dateEstimation
                }

                binding.clickSaveEstimationPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            ProgBandRoomDatabase.getDatabase(this)?.estimationDao()
                                ?.insert(estimationDatas!!)
                            draftDao?.completeDraft(draftID)
//                            Commons.synchronisation(type = "estimation", this)
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
