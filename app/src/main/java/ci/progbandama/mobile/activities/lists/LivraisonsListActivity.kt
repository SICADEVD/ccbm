package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.LivraisonActivity
import ci.progbandama.mobile.adapters.LivraisonAdapter
import ci.progbandama.mobile.databinding.ActivityLivraisonsListBinding
import ci.progbandama.mobile.models.LivraisonModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.LivraisonDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import org.joda.time.DateTime

class LivraisonsListActivity : AppCompatActivity() {


    var livraisonDao: LivraisonDao? = null
    var livraisonsList: MutableList<LivraisonModel>? = null
    var livraisonAdapter: LivraisonAdapter? = null

    lateinit var binding: ActivityLivraisonsListBinding

    fun retrieveDatas() {
        livraisonsList = mutableListOf()
        livraisonDao = ProgBandRoomDatabase.getDatabase(this)?.livraisonDao()

        livraisonsList = livraisonDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        livraisonAdapter = LivraisonAdapter(livraisonsList)

        binding.recyclerLivraisons .adapter = livraisonAdapter
        binding.recyclerLivraisons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.labelLastSynchronisationLivraisons.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        livraisonsList?.let {
            if (it.isEmpty()) {
                binding.recyclerLivraisons.visibility = View.GONE
                binding.linearEmptyContainerLivraisonsList.visibility = View.VISIBLE
            } else {
                binding.recyclerLivraisons.visibility = View.VISIBLE
                binding.linearEmptyContainerLivraisonsList.visibility = View.GONE
            }
        }
    }


    override fun onResume() {
        super.onResume()
        retrieveDatas()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLivraisonsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(LivraisonActivity::class.java)
        }

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
