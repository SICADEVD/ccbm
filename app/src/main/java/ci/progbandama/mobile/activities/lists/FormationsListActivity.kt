package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.FormationActivity
import ci.progbandama.mobile.adapters.FormationAdapter
import ci.progbandama.mobile.models.FormationModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.FormationDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_formations_list.*
import org.joda.time.DateTime

class FormationsListActivity : AppCompatActivity() {


    var formationDao: FormationDao? = null
    var formationsList: MutableList<FormationModel>? = null
    var formationAdapter: FormationAdapter? = null


    fun retrieveDatas() {
        formationsList = mutableListOf()
        formationDao = ProgBandRoomDatabase.getDatabase(this)?.formationDao()

        formationsList = formationDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        formationAdapter = FormationAdapter(this, formationsList)

        recyclerFormations .adapter = formationAdapter
        recyclerFormations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        labelLastSynchronisationFormations.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        formationsList?.let {
            if (it.isEmpty()) {
                recyclerFormations.visibility = View.GONE
                linearEmptyContainerFormationsList.visibility = View.VISIBLE
            } else {
                recyclerFormations.visibility = View.VISIBLE
                linearEmptyContainerFormationsList.visibility = View.GONE
            }
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formations_list)

        imgAddFormations.setOnClickListener {
            ActivityUtils.startActivity(FormationActivity::class.java)
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
