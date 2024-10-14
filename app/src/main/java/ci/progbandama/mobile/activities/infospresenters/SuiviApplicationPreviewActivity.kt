package ci.progbandama.mobile.activities.infospresenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.SuiviApplicationActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.databinding.ActivitySuiviApplicationPreviewBinding
import ci.progbandama.mobile.databinding.ActivitySuiviParcellePreviewBinding
import ci.progbandama.mobile.models.SuiviApplicationModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class SuiviApplicationPreviewActivity : AppCompatActivity() {


    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    private lateinit var binding: ActivitySuiviApplicationPreviewBinding

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
        binding = ActivitySuiviApplicationPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            try {
                val suiviApplication: SuiviApplicationModel? = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)


                val suiviParcelleItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val suiviParcelleItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                suiviParcelleItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value.replace(", ", "\n"),
                            suiviParcelleItemsListPrev
                        )
                    }
                }

                val rvPrevAdapter = PreviewItemAdapter(suiviParcelleItemsListPrev)
                binding.recyclerInfoPrev.adapter = rvPrevAdapter
                binding.recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


                binding.clickCloseBtn.setOnClickListener {
                    finish()
                }

                binding.clickSaveSApplicPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            ProgBandRoomDatabase.getDatabase(this)?.suiviApplicationDao()?.insert(suiviApplication!!)
                            draftDao?.completeDraft(draftID)
//                            Commons.synchronisation(type = "application", this)
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
