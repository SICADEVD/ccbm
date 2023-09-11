package ci.projccb.mobile.activities.lists

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.DataDraftedAdapter
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_update_contents_list.*


@SuppressLint("All")
class UpdateContentsListActivity : AppCompatActivity(R.layout.activity_update_contents_list) {


    var contentsList = mutableListOf<DataDraftedModel>()
    var contentsCloneList = mutableListOf<DataDraftedModel>()
    var fromContent: String? = ""


    fun refreshAdapter(list: MutableList<DataDraftedModel>) {
        val draftedDatasAdapter = DataDraftedAdapter(
            this,
            list
        )

        recyclerListUpdateContent.adapter = draftedDatasAdapter
        recyclerListUpdateContent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        draftedDatasAdapter.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            fromContent =  it.getStringExtra("fromContent")

            labelTitleUpdateContent.text = labelTitleUpdateContent.text.toString().plus(" ").plus(fromContent?.uppercase()).plus("S")

            contentsList = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getAllByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                typeDraft = if (fromContent?.uppercase() == "PRODUCTEUR") "content_producteur" else "content_parcelle"
            ) ?: mutableListOf()

            contentsCloneList.addAll(contentsList)

            refreshAdapter(contentsList)

            contentsList.let { draftsList ->
                if (draftsList.isEmpty()) {
                    recyclerListUpdateContent.visibility = View.GONE
                    linearEmptyContainerUpdate.visibility = View.VISIBLE
                } else {
                    recyclerListUpdateContent.visibility = View.VISIBLE
                    linearEmptyContainerUpdate.visibility = View.GONE

                    /*draftsList.map { draftModel ->
                        if (fromContent?.uppercase() == "PRODUCTEUR") {
                            val producteurModelDraft = ApiClient.gson.fromJson(draftModel.datas, ProducteurModel::class.java)
                            contentProducteurs.add(producteurModelDraft)
                        } else {
                            val parcelleModelDraft = ApiClient.gson.fromJson(draftModel.datas, ParcelleModel::class.java)
                            contentParcelles.add(parcelleModelDraft)
                        }
                    }*/
                }
            }
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imageSearchUpdate.setOnClickListener {
            if (linearSearchContainerUpdate.visibility == View.VISIBLE) {
                linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_button))
                linearSearchContainerUpdate.visibility = View.GONE
            } else {
                linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_button))
                linearSearchContainerUpdate.visibility = View.VISIBLE
            }
        }

        imageCloseSearchUpdate.setOnClickListener {
            if (editSearchUpdate.text.toString().isNotEmpty()) {
                editSearchUpdate.text = null
            } else {
                linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_button))
                linearSearchContainerUpdate.visibility = View.GONE
            }
        }

        editSearchUpdate.doAfterTextChanged {
            val searchWord = it?.toString()?.trim()

            if (searchWord.toString().length < 3) {
                refreshAdapter(contentsList)
            } else {
                contentsCloneList.clear()

                contentsList.map { draftSearch ->
                    if (draftSearch.ownerDraft.lowercase().trim().contains(searchWord.toString().lowercase())) contentsCloneList.add( draftSearch)
                }

                refreshAdapter(contentsCloneList)
            }
        }
    }
}
