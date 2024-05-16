package ci.projccb.mobile.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.InspectionActivity
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.models.NotationModel
import ci.projccb.mobile.models.QuestionResponseModel
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.configDate
import ci.projccb.mobile.tools.Commons.Companion.getSpinnerContent
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

    var dateInspectionParmPass: String? = null,
): RecyclerView.Adapter<ViewHolder>() {


    private val LAYOUT_ONE = 0
    private val LAYOUT_TWO = 1
    private var selectionNotation: Int = 0
    lateinit var questionsListener: RecyclerItemListener<QuestionResponseModel>


    class QuesionnaireHolder(questionnaireView: View) : ViewHolder(questionnaireView) {
        var labelQuestionInspection = questionnaireView.labelQuestionInspectionItem
        var selectionNotationInspection = questionnaireView.selectResponseInspectionItem
        var commentContainer = questionnaireView.commentContainer
        var textInspecItemRecommandTitle = questionnaireView.textInspecItemRecommandTitle
        var editCommentItemQuestInspect = questionnaireView.editCommentItemQuestInspect
        var commentNonConforme = questionnaireView.commentNonConforme
        var tvLastCommentNonConform = questionnaireView.tvLastCommentNonConform
        var lastCommentContainer = questionnaireView.lastCommentContainer
        var editDateDelaiInspectItem = questionnaireView.editDateDelaiInspectItem
        var editDateVerifInspectItem = questionnaireView.editDateVerifInspectItem
        var selectStatutsInspectionItem = questionnaireView.selectStatutsInspectionItem
    }


    class QuesionnaireTitleHolder(questionnaireTitleView: View) :
        ViewHolder(questionnaireTitleView) {
        var labelHeaderInspection = questionnaireTitleView.labelHeaderInspectionItem
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var mQuestionnaireView: View? = null
        var viewHolder: ViewHolder? = null

        if (viewType == LAYOUT_ONE) {
            mQuestionnaireView = LayoutInflater.from(parent.context)
                .inflate(R.layout.inspection_header_layout, parent, false)
            viewHolder = QuesionnaireTitleHolder(mQuestionnaireView)
        } else {
            mQuestionnaireView = LayoutInflater.from(parent.context)
                .inflate(R.layout.questionnaire_items_list, parent, false)
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
            val questionnaireTitleHolder: QuesionnaireTitleHolder =
                holder as QuesionnaireTitleHolder
            questionnaireTitleHolder.labelHeaderInspection.text = questionnaireResponseInfo.label
        } else {
            val questionnaireInfosHolder: QuesionnaireHolder = holder as QuesionnaireHolder
            questionnaireInfosHolder.selectionNotationInspection?.adapter = null

            val notationAdapter =
                ArrayAdapter(pContext, android.R.layout.simple_dropdown_item_1line, pNotationsList)
            questionnaireInfosHolder.selectionNotationInspection.selectResponseInspectionItem.adapter =
                notationAdapter

            //var questionnaireResponseInfoExt: QuestionResponseModel? = null

            questionnaireInfosHolder.selectionNotationInspection.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        positionSelection: Int,
                        l: Long
                    ) {
                        val selectedNote = pNotationsList[positionSelection]
                        questionnaireResponseInfo.id = questionnaireResponseInfo.id.toString()
                        questionnaireResponseInfo.note = selectedNote.point?.toString()
                        questionnaireResponseInfo.reponseId = positionSelection
                        questionnaireResponseInfo.noteLabel = selectedNote.nom
                        //  LogUtils.d("${questionnaireResponseInfo.reponseId}")
                        //  questionnaireResponseInfoExt = questionnaireResponseInfo
                        if (selectedNote.nom?.equals(
                                "Conforme",
                                ignoreCase = true
                            ) == false && selectedNote.nom?.equals(
                                "Choisir la note",
                                ignoreCase = true
                            ) == false
                        ) {
                            holder.commentContainer.visibility = View.VISIBLE
                        } else holder.commentContainer.visibility = View.GONE


                        questionsListener.itemSelected(
                            holder.getAdapterPosition(),
                            questionnaireResponseInfo
                        )
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {
                    }
                }

            holder.editCommentItemQuestInspect.doOnTextChanged() { text, start, before, count ->
                holder.editCommentItemQuestInspect.clearFocus()
            }

            holder.editCommentItemQuestInspect.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    text: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (text.isNullOrEmpty()) return
//                    LogUtils.d(text)
                    questionnaireResponseInfo.commentaire = text.toString()
                    questionsListener.itemSelected(
                        holder.getAdapterPosition(),
                        questionnaireResponseInfo
                    )
                }

                override fun afterTextChanged(s: Editable) {
                    // Do something after text has changed
                    val enteredText = s.toString()
                    holder.editCommentItemQuestInspect.clearFocus()
                    // Process the entered text here
                }
            })

            if (questionnaireResponseInfo.id_en_base?.isNullOrEmpty() == false) {
                holder.commentNonConforme.visibility = View.VISIBLE
                holder.lastCommentContainer.visibility = View.VISIBLE
                holder.textInspecItemRecommandTitle.text = "Recommandations"

                holder.tvLastCommentNonConform.text = questionnaireResponseInfo.commentaireLast

                holder.editDateDelaiInspectItem.setOnClickListener{
                    pContext.configDate(holder.editDateDelaiInspectItem, true, false, minDate = dateInspectionParmPass)

                    (pContext as InspectionActivity).updatProgressBar()
                }

                holder.editDateVerifInspectItem.setOnClickListener {
                    pContext.configDate(holder.editDateVerifInspectItem)
                    (pContext as InspectionActivity).updatProgressBar()
                }

                holder.editDateDelaiInspectItem.doOnTextChanged { text, start, before, count ->
                    val valSelect = holder.selectStatutsInspectionItem.getSpinnerContent()
                    questionnaireResponseInfo.delai =
                        holder.editDateDelaiInspectItem.text.toString()
                    questionnaireResponseInfo.date_verification =
                        holder.editDateVerifInspectItem.text.toString()
                    questionnaireResponseInfo.statuts = valSelect

                    questionsListener.itemSelected(
                        holder.getAdapterPosition(),
                        questionnaireResponseInfo
                    )
                }
                holder.editDateVerifInspectItem.doOnTextChanged { text, start, before, count ->
                    val valSelect = holder.selectStatutsInspectionItem.getSpinnerContent()
                    questionnaireResponseInfo.delai =
                        holder.editDateDelaiInspectItem.text.toString()
                    questionnaireResponseInfo.date_verification =
                        holder.editDateVerifInspectItem.text.toString()
                    questionnaireResponseInfo.statuts = valSelect

                    questionsListener.itemSelected(
                        holder.getAdapterPosition(),
                        questionnaireResponseInfo
                    )
                }

                holder.selectStatutsInspectionItem.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            val valSelect = holder.selectStatutsInspectionItem.getSpinnerContent()
                            questionnaireResponseInfo.delai =
                                holder.editDateDelaiInspectItem.text.toString()
                            questionnaireResponseInfo.date_verification =
                                holder.editDateVerifInspectItem.text.toString()
                            questionnaireResponseInfo.statuts = valSelect

                            questionsListener.itemSelected(
                                holder.getAdapterPosition(),
                                questionnaireResponseInfo
                            )
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }
                questionnaireInfosHolder.selectionNotationInspection.isEnabled = false
            }

            questionnaireInfosHolder.labelQuestionInspection.text = questionnaireResponseInfo.label
            questionnaireInfosHolder.editCommentItemQuestInspect.setText(questionnaireResponseInfo.commentaire.toString())
            questionnaireInfosHolder.selectionNotationInspection.setSelection(
                questionnaireResponseInfo.reponseId ?: 0
            )
        }
    }

    fun setListQuestion(pQuestionnaireList: MutableList<QuestionResponseModel>) {
        pQuestionnaireResponsesList.addAll(pQuestionnaireList)
        notifyDataSetChanged()
    }


    fun retrieveNotation() {

    }


    override fun getItemCount(): Int = pQuestionnaireResponsesList.size

}
