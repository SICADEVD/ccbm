package ci.progbandama.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.ProducteurMenageActivity
import ci.progbandama.mobile.adapters.MenageAdapter
import ci.progbandama.mobile.models.ProducteurMenageModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.ProducteurMenageDao
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_menageres_list.*
import org.joda.time.DateTime

class MenageresListActivity : AppCompatActivity() {


    var productMenagereDao: ProducteurMenageDao? = null
    var menagesList: MutableList<ProducteurMenageModel>? = null
    var menagesAdapter: MenageAdapter? = null


    fun retrieveDatas() {
        menagesList = mutableListOf()
        productMenagereDao = ProgBandRoomDatabase.getDatabase(this)?.producteurMenageDoa()

        menagesList = productMenagereDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(
            Constants.AGENT_ID, 0).toString())

        menagesAdapter = MenageAdapter(menagesList)

        recyclerMenages.adapter = menagesAdapter
        recyclerMenages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        labelLastSynchronisationMenage.text = resources.getString(
            R.string.last_synchronisation_date,
            DateTime.now().toString("HH:mm:ss")
        )

        menagesList?.let {
            if (it.isEmpty()) {
                recyclerMenages.visibility = View.GONE
                linearEmptyContainerMenagesList.visibility = View.VISIBLE
            } else {
                recyclerMenages.visibility = View.VISIBLE
                linearEmptyContainerMenagesList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menageres_list)

        clickCloseBtn.setOnClickListener {
            finish()
        }

        imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(ProducteurMenageActivity::class.java)
        }
    }


    override fun onResume() {
        super.onResume()

        retrieveDatas()
    }
}
