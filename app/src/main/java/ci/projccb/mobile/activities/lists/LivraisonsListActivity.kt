package ci.projccb.mobile.activities.lists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.LivraisonActivity
import ci.projccb.mobile.adapters.LivraisonAdapter
import ci.projccb.mobile.models.LivraisonModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.LivraisonDao
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_livraisons_list.*
import org.joda.time.DateTime

class LivraisonsListActivity : AppCompatActivity() {


    var livraisonDao: LivraisonDao? = null
    var livraisonsList: MutableList<LivraisonModel>? = null
    var livraisonAdapter: LivraisonAdapter? = null


    fun retrieveDatas() {
        livraisonsList = mutableListOf()
        livraisonDao = CcbRoomDatabase.getDatabase(this)?.livraisonDao()

        livraisonsList = livraisonDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
        livraisonAdapter = LivraisonAdapter(livraisonsList)

        recyclerLivraisons .adapter = livraisonAdapter
        recyclerLivraisons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        labelLastSynchronisationLivraisons.text = resources.getString(R.string.last_synchronisation_date, DateTime.now().toString("HH:mm:ss"))

        livraisonsList?.let {
            if (it.isEmpty()) {
                recyclerLivraisons.visibility = View.GONE
                linearEmptyContainerLivraisonsList.visibility = View.VISIBLE
            } else {
                recyclerLivraisons.visibility = View.VISIBLE
                linearEmptyContainerLivraisonsList.visibility = View.GONE
            }
        }
    }


    override fun onResume() {
        super.onResume()
        retrieveDatas()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livraisons_list)

        imgAddBtn.setOnClickListener {
            ActivityUtils.startActivity(LivraisonActivity::class.java)
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
