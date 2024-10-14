package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.ProducteurActivity
import ci.progbandama.mobile.adapters.ProducteurAdapter
import ci.progbandama.mobile.databinding.ActivityProducteursListBinding
import ci.progbandama.mobile.models.ProducteurModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.ProducteurDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import org.joda.time.DateTime

class ProducteursListActivity : AppCompatActivity() {


    var producteurDao: ProducteurDao? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var producteurAdapter: ProducteurAdapter? = null

    lateinit var binding: ActivityProducteursListBinding

    fun retrieveDatas() {
        producteursList = mutableListOf()
        producteurDao = ProgBandRoomDatabase.getDatabase(this)?.producteurDoa()

        producteursList = producteurDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(
            Constants.AGENT_ID, 0).toString())

        producteurAdapter = ProducteurAdapter(producteursList)

        binding.recyclerProducteurs.adapter = producteurAdapter
        binding.recyclerProducteurs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.labelLastSynchronisationProducteur.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        producteursList?.let {
            if (it.isEmpty()) {
                binding.recyclerProducteurs.visibility = View.GONE
                binding.linearEmptyContainerProducteursList.visibility = View.VISIBLE
            } else {
                binding.recyclerProducteurs.visibility = View.VISIBLE
                binding.linearEmptyContainerProducteursList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProducteursListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(ProducteurActivity::class.java)
        }

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }


}
