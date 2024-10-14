package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.SuiviParcelleActivity
import ci.progbandama.mobile.adapters.SuiviParcelleAdapter
import ci.progbandama.mobile.databinding.ActivitySuiviPacellesListBinding
import ci.progbandama.mobile.models.SuiviParcelleModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.SuiviParcelleDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import org.joda.time.DateTime

class SuiviPacellesListActivity : AppCompatActivity() {


    var suivisList: MutableList<SuiviParcelleModel>? = null
    var suiviParcelleDao: SuiviParcelleDao? = null
    var suiviAdapter: SuiviParcelleAdapter? = null

    lateinit var binding: ActivitySuiviPacellesListBinding

    fun retrieveDatas() {
        suivisList = mutableListOf()
        suiviParcelleDao = ProgBandRoomDatabase.getDatabase(this)?.suiviParcelleDao()

        suivisList = suiviParcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(
            Constants.AGENT_ID, 0).toString())

        suiviAdapter = SuiviParcelleAdapter(suivisList)

        binding.recyclerSuiviParcelles.adapter = suiviAdapter
        binding.recyclerSuiviParcelles.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.labelLastSynchronisationSuiviParcelles.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        suivisList?.let {
            if (it.isEmpty()) {
                binding.recyclerSuiviParcelles.visibility = View.GONE
                binding.linearEmptyContainerSuiviParcellesList.visibility = View.VISIBLE
            } else {
                binding.recyclerSuiviParcelles.visibility = View.VISIBLE
                binding.linearEmptyContainerSuiviParcellesList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuiviPacellesListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(SuiviParcelleActivity::class.java)
        }

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        retrieveDatas()
    }
}
