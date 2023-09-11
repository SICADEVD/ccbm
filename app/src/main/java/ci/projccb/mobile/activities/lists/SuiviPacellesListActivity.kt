package ci.projccb.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.SuiviParcelleActivity
import ci.projccb.mobile.adapters.SuiviParcelleAdapter
import ci.projccb.mobile.models.SuiviParcelleModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.SuiviParcelleDao
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_suivi_pacelles_list.*
import org.joda.time.DateTime

class SuiviPacellesListActivity : AppCompatActivity() {


    var suivisList: MutableList<SuiviParcelleModel>? = null
    var suiviParcelleDao: SuiviParcelleDao? = null
    var suiviAdapter: SuiviParcelleAdapter? = null


    fun retrieveDatas() {
        suivisList = mutableListOf()
        suiviParcelleDao = CcbRoomDatabase.getDatabase(this)?.suiviParcelleDao()

        suivisList = suiviParcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(
            Constants.AGENT_ID, 0).toString())

        suiviAdapter = SuiviParcelleAdapter(suivisList)

        recyclerSuiviParcelles.adapter = suiviAdapter
        recyclerSuiviParcelles.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        labelLastSynchronisationSuiviParcelles.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        suivisList?.let {
            if (it.isEmpty()) {
                recyclerSuiviParcelles.visibility = View.GONE
                linearEmptyContainerSuiviParcellesList.visibility = View.VISIBLE
            } else {
                recyclerSuiviParcelles.visibility = View.VISIBLE
                linearEmptyContainerSuiviParcellesList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_pacelles_list)


        imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(SuiviParcelleActivity::class.java)
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

        retrieveDatas()
    }
}
