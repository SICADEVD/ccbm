package ci.progbandama.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.DistributionArbreActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.models.DistributionArbreModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_distribution_arbre_preview.*

class DistributionArbrePreviewActivity : AppCompatActivity() {


    var distributionArbreDatas: DistributionArbreModel? = null
    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution_arbre_preview)

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

                distributionArbreDatas = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                LogUtils.d(Commons.TAG, GsonUtils.toJson(distributionArbreDatas))

                clickSaveDistributionArbrePreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            ProgBandRoomDatabase.getDatabase(this)?.distributionArbreDao()
                                ?.insert(distributionArbreDatas!!)
                            draftDao?.completeDraft(draftID)
//                            Commons.synchronisation(type = "AGRO_DISTRIBUTION".lowercase(), this)
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
