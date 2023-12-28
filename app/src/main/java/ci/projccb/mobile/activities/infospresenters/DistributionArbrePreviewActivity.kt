package ci.projccb.mobile.activities.infospresenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.DistributionArbreActivity
import ci.projccb.mobile.activities.forms.FormationActivity
import ci.projccb.mobile.adapters.PreviewItemAdapter
import ci.projccb.mobile.models.DistributionArbreModel
import ci.projccb.mobile.models.VisiteurFormationModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.ListConverters
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_distribution_arbre_preview.*

import kotlinx.android.synthetic.main.activity_producteur_preview.imageProfileProdPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class DistributionArbrePreviewActivity : AppCompatActivity() {


    var distributionArbreDatas: DistributionArbreModel? = null
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution_arbre_preview)

        intent?.let {
            try {
                val infoItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val infoItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                infoItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value,
                            infoItemsListPrev
                        )
                    }
                }
                //LogUtils.json(infosProducteur)
                //                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(infoItemsListPrev)
                recyclerInfoPrev.adapter = rvPrevAdapter
                recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                distributionArbreDatas = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                LogUtils.d(Commons.TAG, GsonUtils.toJson(distributionArbreDatas))

                clickSaveDistributionArbrePreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            CcbRoomDatabase.getDatabase(this)?.distributionArbreDao()
                                ?.insert(distributionArbreDatas!!)
                            draftDao?.completeDraft(draftID)
                            Commons.synchronisation(type = "distribution_arbre", this)
                            Commons.showMessage(
                                "Distribution d'arbre enregistrée !",
                                this,
                                finished = true,
                                callback = {})
                        },
                        finished = false
                    )


                    ActivityUtils.finishActivity(DistributionArbreActivity::class.java)
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
