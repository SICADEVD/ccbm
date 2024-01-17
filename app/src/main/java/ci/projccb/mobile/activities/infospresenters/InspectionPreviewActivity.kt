package ci.projccb.mobile.activities.infospresenters


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.InspectionActivity
import ci.projccb.mobile.adapters.QuestionnairePreviewAdapter
import ci.projccb.mobile.interfaces.SectionCallback
import ci.projccb.mobile.itemviews.RecyclerItemDecoration
import ci.projccb.mobile.models.InspectionDTO
import ci.projccb.mobile.models.QuestionResponseModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_inspection_preview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception


class InspectionPreviewActivity : AppCompatActivity(), SectionCallback {


    private var cQuestionnairesMap: MutableList<HashMap<String, String>>? = mutableListOf()
    private var cQuestionnaires: MutableList<QuestionResponseModel>? = mutableListOf()
    lateinit var questionnaireAdapter: QuestionnairePreviewAdapter


    suspend fun fetchQuestionnaires(inspectionDTO: InspectionDTO?) {
        MainScope().launch {
            try {
                val mQuestionsPreviewToken =
                    object : TypeToken<MutableList<QuestionResponseModel>>() {}.type
                cQuestionnaires =
                    GsonUtils.fromJson(inspectionDTO?.reponseStringify, mQuestionsPreviewToken)

                questionnaireAdapter =
                    QuestionnairePreviewAdapter(this@InspectionPreviewActivity, cQuestionnaires!!)
                recyclerQuestionnairesInspectionPreview.layoutManager = LinearLayoutManager(
                    this@InspectionPreviewActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                recyclerQuestionnairesInspectionPreview.adapter = questionnaireAdapter

                val recyclerDecoration = RecyclerItemDecoration(
                    this@InspectionPreviewActivity,
                    40,
                    true,
                    this@InspectionPreviewActivity
                )
                recyclerQuestionnairesInspectionPreview.addItemDecoration(recyclerDecoration)
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspection_preview)

        intent?.let { intent ->
            try {
                val inspectionDTO: InspectionDTO? = intent.getParcelableExtra("preview")

                inspectionDTO?.let { inspection ->
                    labelProducteurNomInspectionPreview.text = inspection.producteurNomPrenoms
                    labelCampagneInspectionPreview.text = CcbRoomDatabase.getDatabase(this)?.staffFormation()?.getStaffFormationById(inspection.encadreur?.toInt())?.let { "${it.firstname} ${it.lastname}"  }
                    val total_question = inspection.total_question?.toInt()
                    val total_question_non_applicable = inspection.total_question_non_applicable?.toInt()
                    val total_question_conforme = inspection.total_question_conforme?.toInt()
                    labelTauConformInspectionPreview.text = ((total_question_conforme?.div(total_question?:1))?.toFloat()!! * 100).toString().plus("%")
                    labelNbConformInspectionPreview.text = inspection.total_question_conforme
                    labelNbNonConformInspectionPreview.text = inspection.total_question_non_conforme
                    labelNonApplicableInspectionPreview.text = inspection.total_question_non_applicable
                    labelTotalInspectionPreview.text = inspection.total_question

                    CoroutineScope(Dispatchers.Main).launch {
                        fetchQuestionnaires(inspection)
                    }

                    labelDateInspectionPreview.text = inspection.dateEvaluation

                    clickSaveInspectionPreview.setOnClickListener {
                        try {
                            Commons.showMessage(
                                "Etes-vous sûr de vouloir enregistrer ce contenu ?",
                                this,
                                showNo = true,
                                callback = {
                                    CcbRoomDatabase.getDatabase(this)?.inspectionDao()
                                        ?.insert(inspection)
                                    Commons.synchronisation(type = "inspection", this)
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

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
