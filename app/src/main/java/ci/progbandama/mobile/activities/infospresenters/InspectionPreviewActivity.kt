package ci.progbandama.mobile.activities.infospresenters


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.InspectionActivity
import ci.progbandama.mobile.adapters.QuestionnairePreviewAdapter
import ci.progbandama.mobile.databinding.ActivityInspectionPreviewBinding
import ci.progbandama.mobile.interfaces.SectionCallback
import ci.progbandama.mobile.itemviews.RecyclerItemDecoration
import ci.progbandama.mobile.models.InspectionDTO
import ci.progbandama.mobile.models.QuestionResponseModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception


class InspectionPreviewActivity : AppCompatActivity(), SectionCallback {


    private var cQuestionnairesMap: MutableList<HashMap<String, String>>? = mutableListOf()
    private var cQuestionnaires: MutableList<QuestionResponseModel>? = mutableListOf()
    lateinit var questionnaireAdapter: QuestionnairePreviewAdapter

    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    suspend fun fetchQuestionnaires(inspectionDTO: InspectionDTO?) {
        MainScope().launch {
            try {
                val mQuestionsPreviewToken =
                    object : TypeToken<MutableList<QuestionResponseModel>>() {}.type
                cQuestionnaires =
                    GsonUtils.fromJson(inspectionDTO?.reponseStringify, mQuestionsPreviewToken)

                questionnaireAdapter =
                    QuestionnairePreviewAdapter(this@InspectionPreviewActivity, cQuestionnaires!!)
                binding.recyclerQuestionnairesInspectionPreview.layoutManager = LinearLayoutManager(
                    this@InspectionPreviewActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding.recyclerQuestionnairesInspectionPreview.adapter = questionnaireAdapter

                val recyclerDecoration = RecyclerItemDecoration(
                    this@InspectionPreviewActivity,
                    40,
                    true,
                    this@InspectionPreviewActivity
                )
                binding.recyclerQuestionnairesInspectionPreview.addItemDecoration(recyclerDecoration)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }


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

    private lateinit var binding: ActivityInspectionPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspectionPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        clickCancelInspectionPreview.setOnClickListener { finish() }

        intent?.let { intent ->
            try {
                val inspectionDTO: InspectionDTO? = intent.getParcelableExtra("preview")
                draftID = intent.getIntExtra("draft_id", 0)

                inspectionDTO?.let { inspection ->
                    binding.labelCampagneInspectionPreview.text = ProgBandRoomDatabase.getDatabase(this)?.campagneDao()?.getAll()?.first().let { it?.campagnesNom?.split(" ")?.let {
                        var value = ""
                        if(it.size > 1) value = "${it.get(1)}"
                        else value = "N/A"
                        value
                    } }
                    binding.labelEncadrInspectionPreview.text = ProgBandRoomDatabase.getDatabase(this)?.staffFormation()?.getStaffFormationById(inspection.encadreur?.toInt())?.let { "${it.firstname} ${it.lastname}" }
                    binding.labelProducteurNomInspectionPreview.text = inspection.producteurNomPrenoms

                    binding.labelParcelleInspectionPreview.text = inspection.parcelleLib
                    val total_question = inspection.total_question?.toInt()
                    val total_question_non_applicable = inspection.total_question_non_applicable?.toInt()
                    val total_question_conforme = inspection.total_question_conforme?.toInt()
                    val total_question_non_conforme = inspection.total_question_non_conforme?.toInt()
                    val substrain = (total_question?:1).minus(total_question_non_applicable?:1)
                    //LogUtils.d(total_question, total_question_conforme, total_question_non_conforme, total_question_non_applicable, substrain)
                    binding.labelTauConformInspectionPreview.text = (total_question_conforme?.times(100))?.div(substrain).toString().plus("%")
                    inspection.noteInspection = (total_question_conforme?.times(100))?.div(substrain)?.toDouble().toString()
                    binding.labelNbConformInspectionPreview.text = inspection.total_question_conforme
                    binding.labelNbNonConformInspectionPreview.text = inspection.total_question_non_conforme
                    binding.labelNonApplicableInspectionPreview.text = inspection.total_question_non_applicable
                    binding.labelTotalInspectionPreview.text = inspection.total_question
                    binding.labelNrbProdInspectionPreview.text = inspection.production

                    CoroutineScope(Dispatchers.Main).launch {
                        fetchQuestionnaires(inspection)
                    }


                    binding.labelDateInspectionPreview.text = inspection.dateEvaluation

                    binding.clickSaveInspectionPreview.setOnClickListener {
                        try {
                            Commons.showMessage(
                                "Etes-vous sûr de vouloir enregistrer ce contenu ?",
                                this,
                                showNo = true,
                                callback = {
                                    ProgBandRoomDatabase.getDatabase(this)?.inspectionDao()
                                        ?.insert(inspection)
                                    draftDao?.completeDraft(draftID)
//                                    Commons.synchronisation(type = "inspection", this)
                                    Commons.showMessage(
                                        "Inpection enregistrée avec succes !",
                                        this,
                                        finished = true,
                                        callback = {})
                                },
                                finished = false
                            )

                            ActivityUtils.finishActivity(InspectionActivity::class.java)
                        } catch (ex: Exception) {
                            Commons.showMessage("Echec enregistreent !", this, callback = {})
                        }
                    }
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
