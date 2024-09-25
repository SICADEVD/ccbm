package ci.progbandama.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ci.progbandama.mobile.R
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.AgentDao
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.SPUtils

/**
 * Created by didierboka.developer on 18/12/2021
 * mail for work:   (didierboka.developer@gmail.com)
 */

class SplashActivity : AppCompatActivity() {


    companion object {
        const val TAG = "SplashActivity.kt"
    }


    lateinit var agentDao: AgentDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(com.intuit.ssp.R.dimen._6ssp),
            resources.getDimension(com.intuit.ssp.R.dimen._5ssp))

        agentDao = ProgBandRoomDatabase.getDatabase(this)?.agentDoa()!!
        val agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)

        Handler().postDelayed({ // Check if app has already openned

            if (SPUtils.getInstance().getString(Constants.APP_FIRST_LAUNCH, "yes") == "no") {
                if (agentID == 0) {
                    val intent = Intent(this, AuthentificationActivity::class.java)
                    startActivity(intent)
                } else {
                    val agentCheck = agentDao.getAgent(agentID)
                    if (agentCheck?.isLogged == true) {
                        val intent = Intent(this, DashboardAgentActivity::class.java)
                        startActivity(intent)

                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            this.startForegroundService(Intent(this, SynchronisationIntentService::class.java))
                        } else {
                            this.startService(Intent(this, SynchronisationIntentService::class.java))
                        }*/
                    } else {
                        val intent = Intent(this, AuthentificationActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else { // App is first launch
                val intent = Intent(this, AuthentificationActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 2000)
        //}, TimeConstants.DAY.toLong())
    }


}
