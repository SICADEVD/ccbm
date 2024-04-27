package ci.projccb.mobile.activities.infospresenters

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.LocaliteActivity
import ci.projccb.mobile.models.LocaliteModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_localite_preview.*
import java.lang.Exception



@SuppressLint("ALL")
class LocalitePreviewActivity : AppCompatActivity() {


    var localiteDatas: LocaliteModel? = null
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0


    fun retrieveDatasFromIntent(bdle: Bundle?) {
        bdle?.let {
            try {
                localiteDatas = it.getParcelable("preview")

                draftID = it.getInt("draft_id")
                LogUtils.e(Commons.TAG, "DRAFT ID -> $draftID")

                localiteDatas?.let { localite ->
                    labelNomLocalitePreview.text = localite.nom
                    labelTypeLocalitePreview.text = localite.type
                    labelSousPrefectureLocalitePreview.text = localite.sousPref
                    labelPopulationLocalitePreview.text = localite.pop
                    labelCentreSanteYesNoLocalitePreview.text = localite.centreYesNo
                    labelCentreSanteYesDistanceLocalitePreview.text = localite.centreDistance
                    labelCentreSanteNomLocalitePreview.text = localite.centreNom
                    labelEcoleYesNoLocalitePreview.text = localite.ecoleYesNo

                    LogUtils.e(Commons.TAG, localite.ecoleYesNo)

                    labelEcoleYesNomsLocalitePreview.text = "Liste d'ecoles"

                    labelCentreSanteYesNoLocalitePreview.text = localite.centreYesNo

                    labelEcoleNoDistanceLocalitePreview.text = localite.ecoleDistance
                    labelEcoleNoNomLocalitePreview.text = localite.ecoleNom
                    labelSourceEauLocalitePreview.text = localite.source
                    labelSourceEauEtatLocalitePreview.text = localite.pompeYesNo

                    labelElectriciteYesNoLocalitePreview.text = localite.cieYesNo
                    labelMarketYesNoLocalitePreview.text = localite.marcheYesNo

                    labelMarketDayLocalitePreview.text = localite.dayMarche

                    labelDechetYesNoLocalitePreview.text = localite.dechetYesNo
                    labelNbreComiteLocalitePreview.text = localite.comite

                    labelNbreFemmeLocalitePreview.text = localite.femmeAsso
                    labelNbreJeuneLocalitePreview.text = localite.jeuneAsso

                    labelLatitudeLocalitePreview.text = localite.latitude
                    labelLongitudeLocalitePreview.text = localite.longitude
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localite_preview)
        retrieveDatasFromIntent(intent.extras)

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveLocalitePreview.setOnClickListener {
            try {
                Commons.showMessage(
                    "Etes-vous sur de vouloir faire ce enregistrement ?",
                    this,
                    showNo = true,
                    callback = {
                        CcbRoomDatabase.getDatabase(this)?.localiteDoa()?.insert(localiteDatas!!)
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
