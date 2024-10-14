package ci.progbandama.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.VisiteurFormationActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.databinding.ActivityVisiteurFormationPreviewBinding
import ci.progbandama.mobile.models.VisiteurFormationModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics

class VisiteurFormationPreviewActivity : AppCompatActivity() {


    var visiteurFormationDatas: VisiteurFormationModel? = null
    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    private lateinit var binding: ActivityVisiteurFormationPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisiteurFormationPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickCloseBtn.setOnClickListener {
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
                binding.recyclerInfoPrev.adapter = rvPrevAdapter
                binding.recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                visiteurFormationDatas = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

                //LogUtils.e(Commons.TAG, GsonUtils.toJson(visiteurFormationDatas))



                binding.clickSaveVisitFormationPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            ProgBandRoomDatabase.getDatabase(this)?.visiteurFormationDao()
                                ?.insert(visiteurFormationDatas!!)
                            draftDao?.completeDraft(draftID)
//                            Commons.synchronisation(type = "visiteur_formation", this)
                            Commons.showMessage(
                                "Formation enregistrée !",
                                this,
                                finished = true,
                                callback = {})
                        },
                        finished = false
                    )


                    ActivityUtils.finishActivity(VisiteurFormationActivity::class.java)
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
