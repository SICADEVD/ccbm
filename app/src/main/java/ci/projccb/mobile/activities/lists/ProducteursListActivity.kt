package ci.projccb.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.ProducteurActivity
import ci.projccb.mobile.adapters.ProducteurAdapter
import ci.projccb.mobile.models.ProducteurModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.ProducteurDao
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_producteurs_list.*
import org.joda.time.DateTime

class ProducteursListActivity : AppCompatActivity() {


    var producteurDao: ProducteurDao? = null
    var producteursList: MutableList<ProducteurModel>? = null
    var producteurAdapter: ProducteurAdapter? = null


    fun retrieveDatas() {
        producteursList = mutableListOf()
        producteurDao = CcbRoomDatabase.getDatabase(this)?.producteurDoa()

        producteursList = producteurDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(
            Constants.AGENT_ID, 0).toString())

        producteurAdapter = ProducteurAdapter(producteursList)

        recyclerProducteurs.adapter = producteurAdapter
        recyclerProducteurs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        labelLastSynchronisationProducteur.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        producteursList?.let {
            if (it.isEmpty()) {
                recyclerProducteurs.visibility = View.GONE
                linearEmptyContainerProducteursList.visibility = View.VISIBLE
            } else {
                recyclerProducteurs.visibility = View.VISIBLE
                linearEmptyContainerProducteursList.visibility = View.GONE
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producteurs_list)

        imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(ProducteurActivity::class.java)
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
