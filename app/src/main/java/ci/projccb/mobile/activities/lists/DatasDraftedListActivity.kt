package ci.projccb.mobile.activities.lists

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.DataDraftedAdapter
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_datas_drafted_list.*


@SuppressLint("All")
class DatasDraftedListActivity : AppCompatActivity(R.layout.activity_datas_drafted_list) {


    var draftedDatasList: MutableList<DataDraftedModel>? = mutableListOf()
    var fromGlobalMenu = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            fromGlobalMenu = it.getStringExtra("fromMenu").toString()

            val replaceID = fromGlobalMenu
                .replace("parcelles", "suivi_parcelle")
                .replace("formation_visiteur", "visiteur_formation")
                .replace("livraison_magcentral", "suivi_livraison_central")
                .replace("ssrteclmrs", "ssrte")
                .lowercase()
            LogUtils.d(fromGlobalMenu, replaceID)
            val draftedDataList = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getAllByType(
                    SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                    replaceID
                )

            LogUtils.d(draftedDataList)

            val draftedDatasAdapter = DataDraftedAdapter(
                this,
                draftedDataList
            )

            recyclerDraftedList.adapter = draftedDatasAdapter
            recyclerDraftedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            draftedDatasAdapter.notifyDataSetChanged()

            draftedDataList?.let { draftsList ->
                if (draftsList.isEmpty()) {
                    recyclerDraftedList.visibility = View.GONE
                    linearEmptyContainerDraftsList.visibility = View.VISIBLE
                } else {
                    recyclerDraftedList.visibility = View.VISIBLE
                    linearEmptyContainerDraftsList.visibility = View.GONE
                }
            }
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
