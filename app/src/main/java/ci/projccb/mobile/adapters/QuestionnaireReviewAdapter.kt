package ci.projccb.mobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ci.projccb.mobile.R
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.models.NotationModel
import ci.projccb.mobile.models.QuestionResponseModel
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.inspection_header_layout.view.*
import kotlinx.android.synthetic.main.questionnaire_items_list.view.*


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 10/04/2022.
 **/

class QuestionnaireReviewAdapter(
    var pContext: Context, var pQuestionnaireResponsesList: MutableList<QuestionResponseModel>,
    var pNotationsList: MutableList<NotationModel>,
): RecyclerView.Adapter<ViewHolder>() {


    private val LAYOUT_ONE = 0
    private val LAYOUT_TWO = 1
    private var selectionNotation: Int = 0
    lateinit var questionsListener: RecyclerItemListener<QuestionResponseModel>


    class QuesionnaireHolder(questionnaireView: View): ViewHolder(questionnaireView) {
        var labelQuestionInspection = questionnaireView.labelQuestionInspectionItem
        var selectionNotationInspection = questionnaireView.selectResponseInspectionItem
    }


    class QuesionnaireTitleHolder(questionnaireTitleView: View): ViewHolder(questionnaireTitleView) {
        var labelHeaderInspection = questionnaireTitleView.labelHeaderInspectionItem
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var mQuestionnaireView: View? = null
        var viewHolder: ViewHolder? = null

        if (viewType == LAYOUT_ONE) {
            mQuestionnaireView = LayoutInflater.from(parent.context).inflate(R.layout.inspection_header_layout, parent, false)
            viewHolder = QuesionnaireTitleHolder(mQuestionnaireView)
        } else {
            mQuestionnaireView = LayoutInflater.from(parent.context).inflate(R.layout.questionnaire_items_list, parent, false)
            viewHolder = QuesionnaireHolder(mQuestionnaireView)
        }

        return viewHolder
    }


    override fun getItemViewType(position: Int): Int {
        if (pQuestionnaireResponsesList[position].isTitle!!)
            return LAYOUT_ONE
        return LAYOUT_TWO
    }


    override fun getItemId(position: Int): Long {
        LogUtils.e(Commons.TAG, position)
        return super.getItemId(position)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionnaireResponseInfo = pQuestionnaireResponsesList[position]

        if (holder.itemViewType == LAYOUT_ONE) {
            val questionnaireTitleHolder: QuesionnaireTitleHolder = holder as QuesionnaireTitleHolder
            questionnaireTitleHolder.labelHeaderInspection.text = questionnaireResponseInfo.label
        } else {
            val questionnaireInfosHolder: QuesionnaireHolder = holder as QuesionnaireHolder
            questionnaireInfosHolder.selectionNotationInspection?.adapter = null

            val notationAdapter = ArrayAdapter(pContext, android.R.layout.simple_dropdown_item_1line, pNotationsList)
            questionnaireInfosHolder.selectionNotationInspection.selectResponseInspectionItem.adapter = notationAdapter

            questionnaireInfosHolder.selectionNotationInspection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, positionSelection: Int, l: Long) {
                    val selectedNote = pNotationsList[positionSelection]
                    questionnaireResponseInfo.note = selectedNote.point?.toString()
                    questionnaireResponseInfo.reponseId = positionSelection
                    questionnaireResponseInfo.noteLabel = selectedNote.nom
//                    LogUtils.d("${questionnaireResponseInfo.reponseId}")
                    questionsListener.itemSelected(holder.getAdapterPosition(), questionnaireResponseInfo)
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {
                }
            }

            questionnaireInfosHolder.labelQuestionInspection.text = questionnaireResponseInfo.label
            questionnaireInfosHolder.selectionNotationInspection.setSelection(questionnaireResponseInfo.reponseId ?: 0)
        }
    }

    fun setListQuestion(pQuestionnaireList: MutableList<QuestionResponseModel>){
        pQuestionnaireResponsesList.addAll(pQuestionnaireList)
        notifyDataSetChanged()
    }


    fun retrieveNotation() {

    }


    override fun getItemCount(): Int = pQuestionnaireResponsesList.size

}
