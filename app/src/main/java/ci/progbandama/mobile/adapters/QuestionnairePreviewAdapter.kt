package ci.progbandama.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ci.progbandama.mobile.R
import ci.progbandama.mobile.models.QuestionResponseModel
import kotlinx.android.synthetic.main.inspection_header_layout.view.*
import kotlinx.android.synthetic.main.questionnaire_items_list.view.*
import kotlinx.android.synthetic.main.questionnaire_preview_items_list.view.*


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 10/04/2022.
 **/

@SuppressLint("All")
class QuestionnairePreviewAdapter(var pContext: Context, var pQuestionnaires: MutableList<QuestionResponseModel>): RecyclerView.Adapter<ViewHolder>() {


    private val LAYOUT_ONE = 0
    private val LAYOUT_TWO = 1
    private var selectionNotation: Int = 0


    class QuesionnaireHolder(questionnaireView: View): ViewHolder(questionnaireView) {
        var labelQuestion = questionnaireView.labelQuestionInspectionPreview
        var labelResponse = questionnaireView.labelResponseInspectionPreview
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
            mQuestionnaireView = LayoutInflater.from(parent.context).inflate(R.layout.questionnaire_preview_items_list, parent, false)
            viewHolder = QuesionnaireHolder(mQuestionnaireView)
        }

        return viewHolder
    }


    override fun getItemViewType(position: Int): Int {
        if (pQuestionnaires[position].isTitle!!)
            return LAYOUT_ONE
        return LAYOUT_TWO
    }


    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionnaireInfo = pQuestionnaires[position]

        if (holder.itemViewType == LAYOUT_ONE) {
            val questionnaireHeader: QuesionnaireTitleHolder = holder as QuesionnaireTitleHolder
            questionnaireHeader.labelHeaderInspection.text = questionnaireInfo.label
        } else {
            val questionnaireInfosHolder: QuesionnaireHolder = holder as QuesionnaireHolder
            questionnaireInfosHolder.labelQuestion.text = questionnaireInfo.label
            questionnaireInfosHolder.labelResponse.text = null
            questionnaireInfosHolder.labelResponse.text = "Note: ${questionnaireInfo.noteLabel}\nCommentaire: \n${questionnaireInfo.commentaire}"

        }
    }


    override fun getItemCount(): Int = pQuestionnaires.size

}
