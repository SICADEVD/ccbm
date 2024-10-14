package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.FormationActivity
import ci.progbandama.mobile.adapters.FormationAdapter
import ci.progbandama.mobile.databinding.ActivityFormationBinding
import ci.progbandama.mobile.databinding.ActivityFormationsListBinding
import ci.progbandama.mobile.models.FormationModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.FormationDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import org.joda.time.DateTime

class FormationsListActivity : AppCompatActivity() {


    var formationDao: FormationDao? = null
    var formationsList: MutableList<FormationModel>? = null
    var formationAdapter: FormationAdapter? = null

    lateinit var binding: ActivityFormationsListBinding

    fun retrieveDatas() {
        formationsList = mutableListOf()
        formationDao = ProgBandRoomDatabase.getDatabase(this)?.formationDao()

        formationsList = formationDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        formationAdapter = FormationAdapter(this, formationsList)

        binding.recyclerFormations .adapter = formationAdapter
        binding.recyclerFormations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.labelLastSynchronisationFormations.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        formationsList?.let {
            if (it.isEmpty()) {
                binding.recyclerFormations.visibility = View.GONE
                binding.linearEmptyContainerFormationsList.visibility = View.VISIBLE
            } else {
                binding.recyclerFormations.visibility = View.VISIBLE
                binding.linearEmptyContainerFormationsList.visibility = View.GONE
            }
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormationsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAddFormations.setOnClickListener {
            ActivityUtils.startActivity(FormationActivity::class.java)
        }

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
