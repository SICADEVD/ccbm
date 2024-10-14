package ci.progbandama.mobile.activities.lists

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.DataDraftedAdapter
import ci.progbandama.mobile.databinding.ActivityUpdateContentsListBinding
import ci.progbandama.mobile.models.DataDraftedModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.SPUtils


@SuppressLint("All")
class UpdateContentsListActivity : AppCompatActivity() {


    var contentsList = mutableListOf<DataDraftedModel>()
    var contentsCloneList = mutableListOf<DataDraftedModel>()
    var fromContent: String? = ""

    lateinit var binding: ActivityUpdateContentsListBinding

    fun refreshAdapter(list: MutableList<DataDraftedModel>) {
        val draftedDatasAdapter = DataDraftedAdapter(
            this,
            list
        )

        binding.recyclerListUpdateContent.adapter = draftedDatasAdapter
        binding.recyclerListUpdateContent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        draftedDatasAdapter.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateContentsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            fromContent =  it.getStringExtra("fromContent")

            binding.labelTitleUpdateContent.text = binding.labelTitleUpdateContent.text.toString().plus(" ").plus(fromContent?.uppercase()).plus("S")

            contentsList = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getAllByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                typeDraft = if (fromContent?.uppercase() == "PRODUCTEUR") "content_producteur" else "content_parcelle"
            ) ?: mutableListOf()

            contentsCloneList.addAll(contentsList)

            refreshAdapter(contentsList)

            contentsList.let { draftsList ->
                if (draftsList.isEmpty()) {
                    binding.recyclerListUpdateContent.visibility = View.GONE
                    binding.linearEmptyContainerUpdate.visibility = View.VISIBLE
                } else {
                    binding.recyclerListUpdateContent.visibility = View.VISIBLE
                    binding.linearEmptyContainerUpdate.visibility = View.GONE

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

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        binding.imageSearchUpdate.setOnClickListener {
            if (binding.linearSearchContainerUpdate.visibility == View.VISIBLE) {
                binding.linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_button))
                binding.linearSearchContainerUpdate.visibility = View.GONE
            } else {
                binding.linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_button))
                binding.linearSearchContainerUpdate.visibility = View.VISIBLE
            }
        }

        binding.imageCloseSearchUpdate.setOnClickListener {
            if (binding.editSearchUpdate.text.toString().isNotEmpty()) {
                binding.editSearchUpdate.text = null
            } else {
                binding.linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_button))
                binding.linearSearchContainerUpdate.visibility = View.GONE
            }
        }

        binding.editSearchUpdate.doAfterTextChanged {
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
