package ci.projccb.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.ParcelleActivity
import ci.projccb.mobile.adapters.ParcelleAdapter
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.ParcelleDao
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_parcelles_list.*
import org.joda.time.DateTime

class ParcellesListActivity : AppCompatActivity() {


    var parcelleDao: ParcelleDao? = null
    var parcellesList: MutableList<ParcelleModel>? = null
    var parcelleAdapter: ParcelleAdapter? = null


    fun retrieveDatas() {
        parcellesList = mutableListOf()
        parcelleDao = CcbRoomDatabase.getDatabase(this)?.parcelleDao()

        parcellesList = parcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        parcelleAdapter = ParcelleAdapter(this, parcellesList)

        recyclerParcelles.adapter = parcelleAdapter
        recyclerParcelles.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        labelLastSynchronisationParcelles.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        parcellesList?.let {
            if (it.isEmpty()) {
                recyclerParcelles.visibility = View.GONE
                linearEmptyContainerParcellesList.visibility = View.VISIBLE
            } else {
                recyclerParcelles.visibility = View.VISIBLE
                linearEmptyContainerParcellesList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parcelles_list)

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(ParcelleActivity::class.java)
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }
}
