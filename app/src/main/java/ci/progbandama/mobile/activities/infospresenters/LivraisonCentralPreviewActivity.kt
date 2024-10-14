package ci.progbandama.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.LivraisonActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.databinding.ActivityLivraisonCentralBinding
import ci.progbandama.mobile.databinding.ActivityLivraisonCentralPreviewBinding
import ci.progbandama.mobile.models.LivraisonCentralModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics

class LivraisonCentralPreviewActivity : AppCompatActivity() {


    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0
    val livraisonItemsListPrev = arrayListOf<Map<String,String>>()

    private lateinit var binding: ActivityLivraisonCentralPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLivraisonCentralPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                            it.value.replace(", ", "\n"),
                            infoProdItemsListPrev
                        )
                    }
                }
                //LogUtils.json(infosProducteur)
                //                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(infoProdItemsListPrev)
                binding.recyclerInfoLivraison.adapter = rvPrevAdapter
                binding.recyclerInfoLivraison.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvPrevAdapter.notifyDataSetChanged()

                binding.clickSaveLivraisonPreview.setOnClickListener {
                        Commons.showMessage(
                            "Etes-vous sur de vouloir faire ce enregistrement ?",
                            this,
                            showNo = true,
                            callback = {
                                ProgBandRoomDatabase.getDatabase(this)?.livraisonCentralDao()
                                    ?.insert(livraisonCentralDatas)
                                draftDao?.completeDraft(draftID)
//                                Commons.synchronisation(type = "livraison_central", this)
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

                binding.clickCloseBtn.setOnClickListener {
                        finish()
                    }

                //}
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
