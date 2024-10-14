package ci.progbandama.mobile.activities.infospresenters


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.InspectionActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.adapters.QuestionnairePreviewAdapter
import ci.progbandama.mobile.databinding.ActivityInspectionUpdatePreviewBinding
import ci.progbandama.mobile.interfaces.SectionCallback
import ci.progbandama.mobile.models.InspectionDTO
import ci.progbandama.mobile.models.QuestionResponseModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.Exception


class InspectionPreviewUpdateActivity : AppCompatActivity(), SectionCallback {


    private var cQuestionnairesMap: MutableList<HashMap<String, String>>? = mutableListOf()
    private var cQuestionnaires: MutableList<QuestionResponseModel>? = mutableListOf()
    lateinit var questionnaireAdapter: QuestionnairePreviewAdapter

    private lateinit var binding: ActivityInspectionUpdatePreviewBinding

    override fun isSectionHeader(position: Int): Boolean {
        return position == 0 || cQuestionnaires!![position].isTitle!!
    }


    override fun getSectionHeaderName(postion: Int): String {
        try {
            val mQuestionMapped = hashMapOf<String, String>()
            return mQuestionMapped["Title"] ?: "Size"
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }

        return ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspectionUpdatePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickCloseBtn.setOnClickListener { finish() }

        intent?.let { it ->
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


                val itemsDatas: InspectionDTO? = it.getParcelableExtra("preview")

                LogUtils.d(itemsDatas)

                binding.clickSaveFormationPreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire cette mise à jour ?",
                        this,
                        showNo = true,
                        callback = {
                            ProgBandRoomDatabase.getDatabase(this)?.inspectionDao()
                                ?.insert(itemsDatas!!)
//                            Commons.synchronisation(type = "inspection", this)
                            Commons.showMessage(
                                "Inspection mise à jour !",
                                this,
                                finished = true,
                                callback = {})
                        },
                        finished = false
                    )

                    ActivityUtils.finishActivity(InspectionActivity::class.java)
                }
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
