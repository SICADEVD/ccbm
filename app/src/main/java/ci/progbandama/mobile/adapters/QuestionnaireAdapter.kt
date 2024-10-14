package ci.progbandama.mobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ci.progbandama.mobile.R
import ci.progbandama.mobile.databinding.InspectionHeaderLayoutBinding
import ci.progbandama.mobile.databinding.QuestionnaireItemsListBinding
import ci.progbandama.mobile.models.NotationModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 10/04/2022.
 **/

class QuestionnaireAdapter(var pContext: Context, var pQuestionnairesList: MutableList<HashMap<String, String>>, pQuestionResponse: MutableMap<String, String>): RecyclerView.Adapter<ViewHolder>() {


    private val LAYOUT_ONE = 0
    private val LAYOUT_TWO = 1
    private var notationsList = mutableListOf<NotationModel>()
    private var selectionNotation: Int = 0
    var cInspectionNotation = mutableMapOf<String, String>()


    init {
        cInspectionNotation.putAll(pQuestionResponse)
    }


    class QuesionnaireHolder(questionnaireView: QuestionnaireItemsListBinding): ViewHolder(questionnaireView.root) {
        var labelQuestionInspection = questionnaireView.labelQuestionInspectionItem
        var selectionNotationInspection = questionnaireView.selectResponseInspectionItem
    }


    class QuesionnaireTitleHolder(questionnaireTitleView: InspectionHeaderLayoutBinding): ViewHolder(questionnaireTitleView.root) {
        var labelHeaderInspection = questionnaireTitleView.labelHeaderInspectionItem
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var mQuestionnaireView: Any? = null
        var viewHolder: ViewHolder? = null

        if (viewType == LAYOUT_ONE) {
            mQuestionnaireView =
                InspectionHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                LayoutInflater.from(parent.context).inflate(R.layout.inspection_header_layout, parent, false)
            viewHolder = QuesionnaireTitleHolder(mQuestionnaireView)
        } else {
            mQuestionnaireView =
                QuestionnaireItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                LayoutInflater.from(parent.context).inflate(R.layout.questionnaire_items_list, parent, false)
            viewHolder = QuesionnaireHolder(mQuestionnaireView)
        }

        return viewHolder
    }


    override fun getItemViewType(position: Int): Int {
        if (pQuestionnairesList[position].containsKey("Title")) {
            return LAYOUT_ONE
        }
        return LAYOUT_TWO
    }


    override fun getItemId(position: Int): Long {
        LogUtils.e(Commons.TAG, position)
        return super.getItemId(position)
    }


    fun getNotations() = cInspectionNotation


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionnaireInfo = pQuestionnairesList[position]

        if (holder.itemViewType == LAYOUT_ONE) {
            val questionnaireHeader: QuesionnaireTitleHolder = holder as QuesionnaireTitleHolder
            questionnaireHeader.labelHeaderInspection.text = questionnaireInfo["Title"]
        } else {
            val questionnaireInfosHolder: QuesionnaireHolder = holder as QuesionnaireHolder
            notationsList.clear()
            questionnaireInfosHolder.selectionNotationInspection?.adapter = null

            notationsList.add(NotationModel(id = 0, uid = 0, nom = "Choisir la note", point = 0))

            notationsList.addAll(ProgBandRoomDatabase.getDatabase(pContext)?.notationDao()?.getAll()!!)

            val notationAdapter = ArrayAdapter(pContext, android.R.layout.simple_dropdown_item_1line, notationsList)
            questionnaireInfosHolder.selectionNotationInspection.adapter = notationAdapter

            questionnaireInfosHolder.selectionNotationInspection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, positionSelection: Int, l: Long) {
                    if (positionSelection > 0) {
                        MainScope().launch {
                            loopQ@for ((key, _) in questionnaireInfo.iterator()) {
                                loopR@for ((key1, _) in cInspectionNotation.iterator()) {
                                    if (key == key1) {
                                        val notation = notationsList[positionSelection]
                                        cInspectionNotation[key1] = notation.point.toString()
                                        break@loopQ
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {
                }
            }

            questionnaireInfosHolder.labelQuestionInspection.text = questionnaireInfo["${holder.layoutPosition + 1}"]
        }
    }


    fun retrieveNotation() {

    }

    override fun getItemCount(): Int = pQuestionnairesList.size

}
