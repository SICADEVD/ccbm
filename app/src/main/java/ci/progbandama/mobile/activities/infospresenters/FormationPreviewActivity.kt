package ci.progbandama.mobile.activities.infospresenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.FormationActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.models.FormationModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_formation_preview.*
import kotlinx.android.synthetic.main.activity_formation_preview.imagePhotoFormationPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class FormationPreviewActivity : AppCompatActivity() {


    var formationDatas: FormationModel? = null
    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
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

        clickCloseBtn.setOnClickListener {
            finish()
        }

        intent?.let {
            try {
                val infoItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val infoItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                infoItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value.replace(", ", "\n"),
                            infoItemsListPrev
                        )
                    }
                }
                //LogUtils.json(infosProducteur)
                //                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(infoItemsListPrev)
                recyclerInfoPrev.adapter = rvPrevAdapter
                recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


                formationDatas = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                //LogUtils.e(Commons.TAG, GsonUtils.toJson(formationDatas))

                formationDatas?.let { formation ->
                    formation.photoFormation.isNullOrEmpty()?.let {
                        if(!it){
                            val tobi =  BitmapFactory.decodeFile(File(formation.photoFormation).absolutePath)
                            val tobi2 =  Bitmap.createScaledBitmap(tobi, 80, 80, false)
                            imagePhotoFormationPreview.setImageBitmap(tobi2)
                        }
                    }


                }


                clickSaveFormationPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            ProgBandRoomDatabase.getDatabase(this)?.formationDao()
                                ?.insert(formationDatas!!)
                            draftDao?.completeDraft(draftID)
//                            Commons.synchronisation(type = "formation", this)
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
