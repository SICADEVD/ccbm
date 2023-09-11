package ci.projccb.mobile.activities.infospresenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.FormationActivity
import ci.projccb.mobile.models.FormationModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.ListConverters
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_formation_preview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class FormationPreviewActivity : AppCompatActivity() {


    var formationDatas: FormationModel? = null
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0


    suspend fun loadFileToBitmap(pPath: String?) {
        if (pPath?.isEmpty()!!) return

        val imgFile = File(pPath)

        if (imgFile.exists()) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 8
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath, options)


            MainScope().launch {
                imagePhotoFormationPreview.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 80, 80, false))
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formation_preview)

        intent?.let {
            try {
                formationDatas = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                LogUtils.e(Commons.TAG, GsonUtils.toJson(formationDatas))

                formationDatas?.let { formation ->
                    labelLocaliteNomFormationPreview.text = formation.localiteNom
                    labelCampagneNomFormationPreview.text = formation.campagneNom
                    labelDateFormationPreview.text = formation.dateFormation
                    labelLieuFormationPreview.text = formation.lieuFormationNom

                    formation.themesLabel =
                        ListConverters.stringToMutableList(formation.themesLabelStringify)
                    formation.themesLabel?.let { themesLabel ->
                        themesLabel.map { themeLabel ->
                            labelThemeFormationPreview.text =
                                labelThemeFormationPreview.text.toString().plus(themeLabel)
                                    .plus(System.getProperty("line.separator"))
                        }
                    }

                    val producteursNomType = object : TypeToken<MutableList<String>>() {}.type
                    formation.producteursNom =
                        GsonUtils.fromJson(formation.producteursNomStringify, producteursNomType)
                    formation.producteursNom?.let { presences ->
                        presences.map { presence ->
                            labelListePresenceFormationPreview.text =
                                labelListePresenceFormationPreview.text.toString().plus(presence)
                                    .plus(System.getProperty("line.separator"))
                        }
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        loadFileToBitmap(formation.photoPath)
                    }

                    labelVisiteursFormationPreview.text = formation.visiteurs

                }


                clickSaveFormationPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            CcbRoomDatabase.getDatabase(this)?.formationDao()
                                ?.insert(formationDatas!!)
                            draftDao?.completeDraft(draftID)
                            Commons.synchronisation(type = "formation", this)
                            Commons.showMessage(
                                "Formation enregistrée !",
                                this,
                                finished = true,
                                callback = {})
                        },
                        finished = false
                    )


                    ActivityUtils.finishActivity(FormationActivity::class.java)
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
