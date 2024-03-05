package ci.projccb.mobile.activities.lists

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.InspectionActivity
import ci.projccb.mobile.adapters.DataDraftedAdapter
import ci.projccb.mobile.adapters.DataSyncedAdapter
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_datas_sync_list.*


@SuppressLint("All")
class DatasSyncListActivity : AppCompatActivity(R.layout.activity_datas_sync_list) {


    var fromGlobalMenu = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            fromGlobalMenu = it.getStringExtra("fromContent").toString()

           // LogUtils.d(fromGlobalMenu)
            val ccbBase = CcbRoomDatabase.getDatabase(this)!!;
//            val SyncedDataList = CcbRoomDatabase.getDatabase(this)?.SyncedDatasDao()?.getAllByType(
//                    SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
//                    fromGlobalMenu.toLowerCase()
//                )
            val commonDataList = mutableListOf<CommonData>()
            when(fromGlobalMenu.toUpperCase()){
                "INSPECTION" -> {
                    labelTitleMenuAction.apply {
                        setText("${this.text} INSPECTION")
                    }
                    val dataList = ccbBase.inspectionDao().getAllNConformeOrNApplicableSync()
                    dataList.forEach {
//                        LogUtils.d(it.producteursId)
                        if(it.producteursId?.toIntOrNull() != null){

                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("Inspection: ${it.uid}\nProducteur: ${prod.nom} ${prod.prenoms}\nCertificat: ${it.certificatStr}", it.dateEvaluation.toString())
                                })
                            }
                        }
                    }
                    imageSyncBtn.setOnClickListener {
                        ActivityUtils.startActivity(InspectionActivity::class.java)
                        ActivityUtils.finishActivity(this@DatasSyncListActivity)
                    }
                }
            }

            val syncedDatasAdapter = DataSyncedAdapter(
                this,
                commonDataList
            )

            recyclerSyncedList.adapter = syncedDatasAdapter
            recyclerSyncedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            syncedDatasAdapter.notifyDataSetChanged()

            commonDataList?.let { SyncsList ->
                if (SyncsList.isEmpty()) {
                    recyclerSyncedList.visibility = View.GONE
                    linearEmptyContainerSyncsList.visibility = View.VISIBLE
                } else {
                    recyclerSyncedList.visibility = View.VISIBLE
                    linearEmptyContainerSyncsList.visibility = View.GONE
                }
            }
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }


    }
}
