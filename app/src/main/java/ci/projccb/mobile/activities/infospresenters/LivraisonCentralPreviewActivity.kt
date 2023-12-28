package ci.projccb.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.LivraisonActivity
import ci.projccb.mobile.activities.forms.LivraisonCentralActivity
import ci.projccb.mobile.adapters.PreviewItemAdapter
import ci.projccb.mobile.models.LivraisonCentralModel
import ci.projccb.mobile.models.LivraisonModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.addItemsToList
import ci.projccb.mobile.tools.ListConverters
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_livraison_preview.*

class LivraisonCentralPreviewActivity : AppCompatActivity() {


    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0
    val livraisonItemsListPrev = arrayListOf<Map<String,String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livraison_central_preview)

        intent?.let {
            try {
                val livraisonCentralDatas: LivraisonCentralModel = it.getParcelableExtra("preview")!!
                draftID = it.getIntExtra("draft_id", 0)

                val infoProdItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val infoProdItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                infoProdItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value,
                            infoProdItemsListPrev
                        )
                    }
                }
                //LogUtils.json(infosProducteur)
                //                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(infoProdItemsListPrev)
                recyclerInfoLivraison.adapter = rvPrevAdapter
                recyclerInfoLivraison.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvPrevAdapter.notifyDataSetChanged()

                    clickSaveLivraisonPreview.setOnClickListener {
                        Commons.showMessage(
                            "Etes-vous sur de vouloir faire ce enregistrement ?",
                            this,
                            showNo = true,
                            callback = {
                                CcbRoomDatabase.getDatabase(this)?.livraisonCentralDao()
                                    ?.insert(livraisonCentralDatas)
                                draftDao?.completeDraft(draftID)
                                Commons.synchronisation(type = "livraison_central", this)
                                Commons.showMessage(
                                    "Livraison central enregistrée !",
                                    this,
                                    finished = true,
                                    callback = {})
                            },
                            finished = false
                        )

                        ActivityUtils.finishActivity(LivraisonActivity::class.java)
                    }

                    clickCloseBtn.setOnClickListener {
                        finish()
                    }

                //}
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
