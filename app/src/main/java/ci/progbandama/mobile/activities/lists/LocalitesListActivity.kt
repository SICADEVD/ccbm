package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.LocaliteActivity
import ci.progbandama.mobile.adapters.LocaliteAdapter
import ci.progbandama.mobile.models.LocaliteModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.LocaliteDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_localites_list.*
import org.joda.time.DateTime

class LocalitesListActivity : AppCompatActivity() {


    var localiteDao: LocaliteDao? = null
    var localitesList: MutableList<LocaliteModel>? = null
    var localiteAdapter: LocaliteAdapter? = null


    fun retrieveDatas() {
        localitesList = mutableListOf()
        localiteDao = ProgBandRoomDatabase.getDatabase(this)?.localiteDoa()

        localitesList = localiteDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        localiteAdapter = LocaliteAdapter(localitesList)

        recyclerLocalites.adapter = localiteAdapter
        recyclerLocalites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        labelLastSynchronisationLocalite.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        localitesList?.let {
            if (it.isEmpty()) {
                recyclerLocalites.visibility = View.GONE
                linearEmptyContainerLocalitesList.visibility = View.VISIBLE
            } else {
                recyclerLocalites.visibility = View.VISIBLE
                linearEmptyContainerLocalitesList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localites_list)

        imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(LocaliteActivity::class.java)
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }


}
