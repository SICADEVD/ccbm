package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.ProducteurMenageActivity
import ci.progbandama.mobile.adapters.MenageAdapter
import ci.progbandama.mobile.databinding.ActivityMenageresListBinding
import ci.progbandama.mobile.models.ProducteurMenageModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.ProducteurMenageDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import org.joda.time.DateTime

class MenageresListActivity : AppCompatActivity() {


    var productMenagereDao: ProducteurMenageDao? = null
    var menagesList: MutableList<ProducteurMenageModel>? = null
    var menagesAdapter: MenageAdapter? = null

    lateinit var binding: ActivityMenageresListBinding

    fun retrieveDatas() {
        menagesList = mutableListOf()
        productMenagereDao = ProgBandRoomDatabase.getDatabase(this)?.producteurMenageDoa()

        menagesList = productMenagereDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(
            Constants.AGENT_ID, 0).toString())

        menagesAdapter = MenageAdapter(menagesList)

        binding.recyclerMenages.adapter = menagesAdapter
        binding.recyclerMenages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.labelLastSynchronisationMenage.text = resources.getString(
            R.string.last_synchronisation_date,
            DateTime.now().toString("HH:mm:ss")
        )

        menagesList?.let {
            if (it.isEmpty()) {
                binding.recyclerMenages.visibility = View.GONE
                binding.linearEmptyContainerMenagesList.visibility = View.VISIBLE
            } else {
                binding.recyclerMenages.visibility = View.VISIBLE
                binding.linearEmptyContainerMenagesList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenageresListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        binding.imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(ProducteurMenageActivity::class.java)
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }
}
