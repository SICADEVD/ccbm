package ci.progbandama.mobile.activities.infospresenters

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.LocaliteActivity
import ci.progbandama.mobile.databinding.ActivityLocalitePreviewBinding
import ci.progbandama.mobile.models.LocaliteModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.Exception



@SuppressLint("ALL")
class LocalitePreviewActivity : AppCompatActivity() {


    var localiteDatas: LocaliteModel? = null
    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    private lateinit var binding: ActivityLocalitePreviewBinding

    fun retrieveDatasFromIntent(bdle: Bundle?) {
        bdle?.let {
            try {
                localiteDatas = it.getParcelable("preview")

                draftID = it.getInt("draft_id")
                LogUtils.e(Commons.TAG, "DRAFT ID -> $draftID")

                localiteDatas?.let { localite ->
                    binding.labelNomLocalitePreview.text = localite.nom
                    binding.labelTypeLocalitePreview.text = localite.type
                    binding.labelSousPrefectureLocalitePreview.text = localite.sousPref
                    binding.labelPopulationLocalitePreview.text = localite.pop
                    binding.labelCentreSanteYesNoLocalitePreview.text = localite.centreYesNo
                    binding.labelCentreSanteYesDistanceLocalitePreview.text = localite.centreDistance
                    binding.labelCentreSanteNomLocalitePreview.text = localite.centreNom
                    binding.labelEcoleYesNoLocalitePreview.text = localite.ecoleYesNo

                    LogUtils.e(Commons.TAG, localite.ecoleYesNo)

                    binding.labelEcoleYesNomsLocalitePreview.text = "Liste d'ecoles"

                    binding.labelCentreSanteYesNoLocalitePreview.text = localite.centreYesNo

                    binding.labelEcoleNoDistanceLocalitePreview.text = localite.ecoleDistance
                    binding.labelEcoleNoNomLocalitePreview.text = localite.ecoleNom
                    binding.labelSourceEauLocalitePreview.text = localite.source
                    binding.labelSourceEauEtatLocalitePreview.text = localite.pompeYesNo

                    binding.labelElectriciteYesNoLocalitePreview.text = localite.cieYesNo
                    binding.labelMarketYesNoLocalitePreview.text = localite.marcheYesNo

                    binding.labelMarketDayLocalitePreview.text = localite.dayMarche

                    binding.labelDechetYesNoLocalitePreview.text = localite.dechetYesNo
                    binding.labelNbreComiteLocalitePreview.text = localite.comite

                    binding.labelNbreFemmeLocalitePreview.text = localite.femmeAsso
                    binding.labelNbreJeuneLocalitePreview.text = localite.jeuneAsso

                    binding.labelLatitudeLocalitePreview.text = localite.latitude
                    binding.labelLongitudeLocalitePreview.text = localite.longitude
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalitePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrieveDatasFromIntent(intent.extras)

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        binding.clickSaveLocalitePreview.setOnClickListener {
            try {
                Commons.showMessage(
                    "Etes-vous sur de vouloir faire ce enregistrement ?",
                    this,
                    showNo = true,
                    callback = {
                        ProgBandRoomDatabase.getDatabase(this)?.localiteDoa()?.insert(localiteDatas!!)
                        draftDao?.completeDraft(draftID)
//                        Commons.synchronisation(type = "localite", this)
                        Commons.showMessage(
                            "Localité enregistrée avec succes !",
                            this,
                            finished = true,
                            callback = {})
                    },
                    finished = false
                )

                ActivityUtils.finishActivity(LocaliteActivity::class.java)
            } catch (ex: Exception) {
                Commons.showMessage("Echec enregistreent !", this, callback = {})
            }
        }
    }
}
