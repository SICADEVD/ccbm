package ci.progbandama.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.LivraisonActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.models.LivraisonModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_livraison_preview.*

class LivraisonPreviewActivity : AppCompatActivity() {


    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0
    val livraisonItemsListPrev = arrayListOf<Map<String,String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livraison_preview)

        intent?.let {
            try {
                val livraisonDatas: LivraisonModel = it.getParcelableExtra("preview")!!
                draftID = it.getIntExtra("draft_id", 0)

                val infoProdItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val infoProdItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                infoProdItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        val ignoredItemList = listOf<String>("section", "localité", "producteur", "parcelle")
                        val findItem = ignoredItemList.filter {  fit -> it.key.contains(fit, ignoreCase = true)  }.size
                        if(findItem == 0){
                            Commons.addItemsToList(
                                if(it.key=="null") "Autre" else it.key,
                                it.value.replace(", ", "\n"),
                                infoProdItemsListPrev
                            )
                        }
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
                                ProgBandRoomDatabase.getDatabase(this)?.livraisonDao()
                                    ?.insert(livraisonDatas)
                                draftDao?.completeDraft(draftID)
//                                Commons.synchronisation(type = "livraison", this)
                                Commons.showMessage(
                                    "Livraison enregistrée !",
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