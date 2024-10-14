package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.ParcelleActivity
import ci.progbandama.mobile.adapters.ParcelleAdapter
import ci.progbandama.mobile.databinding.ActivityParcellesListBinding
import ci.progbandama.mobile.models.ParcelleModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.ParcelleDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import org.joda.time.DateTime

class ParcellesListActivity : AppCompatActivity() {


    var parcelleDao: ParcelleDao? = null
    var parcellesList: MutableList<ParcelleModel>? = null
    var parcelleAdapter: ParcelleAdapter? = null

    lateinit var binding: ActivityParcellesListBinding

    fun retrieveDatas() {
        parcellesList = mutableListOf()
        parcelleDao = ProgBandRoomDatabase.getDatabase(this)?.parcelleDao()

        parcellesList = parcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        parcelleAdapter = ParcelleAdapter(this, parcellesList)

        binding.recyclerParcelles.adapter = parcelleAdapter
        binding.recyclerParcelles.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.labelLastSynchronisationParcelles.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        parcellesList?.let {
            if (it.isEmpty()) {
                binding.recyclerParcelles.visibility = View.GONE
                binding.linearEmptyContainerParcellesList.visibility = View.VISIBLE
            } else {
                binding.recyclerParcelles.visibility = View.VISIBLE
                binding.linearEmptyContainerParcellesList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParcellesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        binding.imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(ParcelleActivity::class.java)
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }
}
