package ci.projccb.mobile.activities

//import com.github.mikephil.charting.utils.Fill

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.lists.DatasDraftedListActivity
import ci.projccb.mobile.adapters.FeatureAdapter
import ci.projccb.mobile.broadcasts.LoopAlarmReceiver
import ci.projccb.mobile.models.AgentModel
import ci.projccb.mobile.models.CoopModel
import ci.projccb.mobile.models.FeatureModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.services.GpsService
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.Data
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.*
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.navigation.NavigationView
import com.google.gson.reflect.TypeToken
import com.skydoves.expandablelayout.ExpandableLayout
import com.techatmosphere.expandablenavigation.model.ChildModel
import com.techatmosphere.expandablenavigation.model.HeaderModel
import kotlinx.android.synthetic.main.activity_dashboard_agent.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.net.UnknownHostException


/**
 * Created by didierboka.developer on 18/12/2021
 * mail for work:   (didierboka.developer@gmail.com)
 */


class DashboardAgentActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {


    private var currentExpandLayout: ExpandableLayout? = null
    private var listOfFeatureCloned: MutableList<FeatureModel> = arrayListOf()
    private var listOfFeatureClonedNav: MutableList<FeatureModel> = arrayListOf()
    private val listOfFeatures = mutableListOf<FeatureModel>()

    var expandableList: MutableList<ExpandableLayout> = arrayListOf()

    var ccbRoomDatabase: CcbRoomDatabase? = null
    var agentDao: AgentDao? = null;
    var formationDao: FormationDao? = null;
    var localiteDao: LocaliteDao? = null;
    var livraisonDao: LivraisonDao? = null;
    var parcelleDao: ParcelleDao? = null;
    var suiviParcelleDao: SuiviParcelleDao? = null;
    var agentLogged: AgentModel? = null
    var producteurDao: ProducteurDao? = null
    var producteurMenageDao: ProducteurMenageDao? = null
    val TAG = "DashboardAgentActivity.kt"
    var networkFlag = true



    fun bindDatas(agentModel: AgentModel?, coopModel: CoopModel?) {
        labelUserDashboard.text = agentModel?.firstname.toString().plus(" ".plus(agentModel?.lastname.toString())).uppercase()
        labelCoopDashboard.text = coopModel?.name.toString().uppercase()
        titleAccount.text = agentModel?.firstname.toString().plus(" ".plus(agentModel?.lastname.toString())).uppercase()
    }


    fun refreshDatas() {
        /*labelProducteurCount.text = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )?.size.toString()
        labelLocaliteCount.text = localiteDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )?.size.toString()
        labelMenageCount.text = producteurMenageDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )?.size.toString()
        labelParcelleCount.text = parcelleDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )?.size.toString()
        labelSuiviParcelleCount.text = suiviParcelleDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )?.size.toString()
        labelFormationCount.text = formationDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )?.size.toString()
        labelLivraisonCount.text = livraisonDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )?.size.toString()
        labelEstimationCount.text = CcbRoomDatabase.getDatabase(this)?.estimationDao()
            ?.getUnSyncedAll()?.size.toString()
        labelApplicateurCount.text = CcbRoomDatabase.getDatabase(this)?.suiviApplicationDao()
            ?.getUnSyncedAll()?.size.toString()
        labelSSRTCount.text = CcbRoomDatabase.getDatabase(this)?.enqueteSsrtDao()
            ?.getUnSyncedAll()?.size.toString()
        labelEvaluation.text = CcbRoomDatabase.getDatabase(this)?.inspectionDao()
            ?.getUnSyncedAll(
                SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
            )?.size.toString()
        labelUniteAgricole.text = CcbRoomDatabase.getDatabase(this)?.infosProducteurDao()
            ?.getUnSyncedAll(
                SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
            )?.size.toString()

        labelProducteurDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "producteur"
            )?.toString()
        labelEstimationraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "calcul_estimation"
            )?.toString()
        labelLocaliteDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "localite"
            )?.toString()
        labelUniteAgricoleDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "infos_producteur"
            )?.toString()
        labelMenageDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "menage"
            )?.toString()
        labelParcelleDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "parcelle"
            )?.toString()
        labelSuiviParcelleDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "suivi_parcelle"
            )?.toString()
        labelFormationDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "formation"
            )?.toString()
        labelApplicateurDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "suivi_application"
            )?.toString()
        labelLivraisonDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "livraison"
            )?.toString()
        labelSSRTDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "ssrte"
            )?.toString()
        labelEvaluationDraftCount.text =
            CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                type = "inspection"
            )?.toString()*/
    }


    // Setup a recurring alarm every half hour
    fun scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        val intent = Intent(applicationContext, LoopAlarmReceiver::class.java)
        // Create a PendingIntent to be triggered when the alarm goes off
        val pIntent = PendingIntent.getBroadcast(
            this,
            LoopAlarmReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Setup periodic alarm every every half hour from this point onwards
        val firstMillis = System.currentTimeMillis() // alarm is set right away
        val alarm = this.getSystemService(ALARM_SERVICE) as AlarmManager
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        // alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES / 3, pIntent);
        alarm.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            firstMillis,
            (TimeConstants.MIN).toLong(),
            pIntent
        )
    }


    fun askLocationPermission() {
        val permissionLocation = Manifest.permission.ACCESS_FINE_LOCATION
        val grant = ContextCompat.checkSelfPermission(this, permissionLocation)

        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissionList = arrayOfNulls<String>(1)
            permissionList[0] = permissionLocation
            ActivityCompat.requestPermissions(this, permissionList, 2021)
        } else {
            try {
                val intentGpsService = Intent(this, GpsService::class.java)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(intentGpsService)
                } else {
                    this.startService(intentGpsService)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            2021 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {
                        SPUtils.getInstance().put("permission_asked", true)
                        try {

                            val intentGpsService = Intent(this@DashboardAgentActivity, GpsService::class.java)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                this.startForegroundService(intentGpsService)
                            } else {
                                this.startService(intentGpsService)
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                } else {
                    SPUtils.getInstance().put("permission_asked", false)
                }
                return
            }
        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        MainScope().launch {
            // checkNetworkAvailablility()
        }
    }


    @SuppressLint("MissingPermission")
    suspend fun checkNetworkAvailablility() {


    }


    override fun onResume() {
        super.onResume()
        //refreshDatas()

        askLocationPermission()

        CoroutineScope(Dispatchers.IO).launch {
            var networkFlag = false
            try {
                networkFlag = NetworkUtils.isAvailable()
            } catch (ex: UnknownHostException) {
                networkFlag = false
                LogUtils.e("Internet error !")
            }

            if (networkFlag) {
                MainScope().launch {
                    Commons.synchronisation("all",  this@DashboardAgentActivity)
                }
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // 4 - Handle Navigation Item Click

        when (item.itemId) {
            R.id.addDashboardMenu -> {

            }
            R.id.updateDashboardMenu -> {

            }
            R.id.draftsDashboardMenu -> {
                val intentDraftsList = Intent(this, DatasDraftedListActivity::class.java)
                startActivity(intentDraftsList)
            }
            else -> {

            }
        }

        // drawerDashboard.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        ccbRoomDatabase = CcbRoomDatabase.getDatabase(this)
        agentDao = ccbRoomDatabase?.agentDoa();
        val coopDao = ccbRoomDatabase?.coopDao();
        producteurDao = ccbRoomDatabase?.producteurDoa();
        parcelleDao = ccbRoomDatabase?.parcelleDao();
        localiteDao = ccbRoomDatabase?.localiteDoa();
        producteurMenageDao = ccbRoomDatabase?.producteurMenageDoa()
        suiviParcelleDao = ccbRoomDatabase?.suiviParcelleDao()
        formationDao = ccbRoomDatabase?.formationDao()
        livraisonDao = ccbRoomDatabase?.livraisonDao()

        agentLogged = agentDao?.getAgent(SPUtils.getInstance().getInt(Constants.AGENT_ID, 3))
        val coopmodel = coopDao?.getAll()?.first()?: CoopModel()

        setContentView(R.layout.activity_dashboard_agent)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        bindDatas(agentModel = agentLogged, coopmodel)

        //setupBarChartView()

        // .setNavigationItemSelectedListener(this)
        Commons.modifyIcColor(this@DashboardAgentActivity, imgProfileDashboard, R.color.black)
        imgProfileDashboard.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DialogTheme)
            Commons.adjustTextViewSizesInDialog(this, builder, "Deconnexion ?",   this.resources.getDimension(R.dimen._6ssp)
                ,false)
            //builder.setMessage("Deconnexion ?")
            builder.setCancelable(false)

            builder.setPositiveButton(getString(R.string.oui)) { dialog, _ ->
                dialog.dismiss()
                this.finish()

                CcbRoomDatabase.getDatabase(this)?.agentDoa()
                    ?.logoutAgent(false, SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                ActivityUtils.startActivity(SplashActivity::class.java)
            }

            builder.setNegativeButton(getString(R.string.non)) { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()

            dialog.show()
        }

        imgProfileDashboardNDrawer.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DialogTheme)
            Commons.adjustTextViewSizesInDialog(this, builder, "Deconnexion ?",   this.resources.getDimension(R.dimen._6ssp)
                ,false)
            //builder.setMessage("Deconnexion ?")
            builder.setCancelable(false)

            builder.setPositiveButton(getString(R.string.oui)) { dialog, _ ->
                dialog.dismiss()
                this.finish()

                CcbRoomDatabase.getDatabase(this)?.agentDoa()
                    ?.logoutAgent(false, SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                ActivityUtils.startActivity(SplashActivity::class.java)
            }

            builder.setNegativeButton(getString(R.string.non)) { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()

            dialog.show()
        }

        Commons.modifyIcColor(this@DashboardAgentActivity, imgBackDashboard, R.color.black)
        imgBackDashboard.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DialogTheme)
            Commons.adjustTextViewSizesInDialog(this, builder, "Voulez-vous quitter ?",   this.resources.getDimension(R.dimen._6ssp)
                ,false)
            //builder.setMessage("Voulez-vous quitter ?")
            builder.setCancelable(false)

            builder.setPositiveButton(getString(R.string.oui)) { dialog, _ ->
                dialog.dismiss()
                this.finish()
            }

            builder.setNegativeButton(getString(R.string.non)) { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()

            dialog.show()
        }

        /*linearLocalite.setOnClickListener {
            Commons.showMessage("Cette fonctionnalité est désactivé", this, finished = true, callback = {}, positive = getString(R.string.compris), deconnec = false, showNo = false)
            return@setOnClickListener;
            //  val intentLocalite = Intent(this, MenusActionRedirectionActivity::class.java)
            //  intentLocalite.putExtra("from", "localite")
            //  ActivityUtils.startActivity(intentLocalite)
        }*/

        imgMenuDashboard.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START);
        }

        Commons.modifyIcColor(this@DashboardAgentActivity, linearSync, R.color.black)
        linearSync.setOnClickListener {
            var message = "Mettre à jour la base de données... ?"

            if (!networkFlag) {
                message = "Vous n'etes pas connecté à internet pour effectuer cette action !"
            }

            val builder = AlertDialog.Builder(this, R.style.DialogTheme)
            Commons.adjustTextViewSizesInDialog(this, builder, message,   this.resources.getDimension(R.dimen._6ssp)
                ,false)
            //builder.setMessage(message)
            builder.setCancelable(false)

            if (networkFlag) {
                builder.setPositiveButton(getString(R.string.oui)) { dialog, _ ->
                    dialog.dismiss()
                    this.finish()

                    val intentConfiguration =
                        Intent(this@DashboardAgentActivity, ConfigurationActivity::class.java)
                    intentConfiguration.putExtra(
                        Constants.AGENT_ID,
                        SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)
                    )
                    ActivityUtils.startActivity(intentConfiguration)
                }

                builder.setNegativeButton(getString(R.string.non)) { dialog, _ ->
                    dialog.dismiss()
                }
            } else {
                builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            }

            val dialog: AlertDialog = builder.create()

            dialog.show()
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1010
            )
        }

        //setDataClickListener()
        val roles: MutableList<String> = GsonUtils.fromJson(SPUtils.getInstance().getString("menu"), object : TypeToken<MutableList<String>>(){}.type)

        expandableList = mutableListOf<ExpandableLayout>(
            expandIdentif,
            expandIdentif2,
            expandIdentif3,
            expandIdentif4,
            expandIdentif5,
            //expandIdentif6,
            //expandIdentif7,
        )

        expandableList.forEach {
            it.apply {
                setOnExpandListener {
                    if (it) {
                        containerFeatureDash.visibility = View.GONE
                        currentExpandLayout = this
                        showAllExpandable(this, expandableList)
                    } else {
                        containerFeatureDash.visibility = View.VISIBLE
                        currentExpandLayout = this
                        hideOtherExpandable(this, expandableList)
                    }
                    //LogUtils.d("Expand : ${it}")
                    hideNotExistFeature(roles, this)

                }
                parentLayout.setOnClickListener {
                    this.toggleLayout()
                }
            }

            hideNotExistFeature(roles, it)
        }

        //updateListOfFeature()
        updateListOfFeature()
        setNavViewItems(roles)
        setViewFeatureListing()

        setupLiveData()
    }

    private fun setupLiveData() {

        producteurDao?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("PRODUCTEUR", length)
        })

        CcbRoomDatabase.getDatabase(this)?.infosProducteurDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("INFOS_PRODUCTEUR", length)
        })

        parcelleDao?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("PARCELLE", length)
        })

        producteurMenageDao?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("MENAGE", length)
        })

        CcbRoomDatabase.getDatabase(this)?.suiviApplicationDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("APPLICATION", length)
        })

        CcbRoomDatabase.getDatabase(this)?.inspectionDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("INSPECTION", length)
        })

        CcbRoomDatabase.getDatabase(this)?.estimationDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("ESTIMATION", length)
        })

        CcbRoomDatabase.getDatabase(this)?.suiviParcelleDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("PARCELLES", length)
        })

        CcbRoomDatabase.getDatabase(this)?.formationDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("FORMATION", length)
        })

        CcbRoomDatabase.getDatabase(this)?.enqueteSsrtDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("SSRTECLMRS", length)
        })

        CcbRoomDatabase.getDatabase(this)?.livraisonDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("LIVRAISON", length)
        })

        CcbRoomDatabase.getDatabase(this)?.evaluationArbreDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("AGRO_EVALUATION", length)
        })

        CcbRoomDatabase.getDatabase(this)?.distributionArbreDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("AGRO_DISTRIBUTION", length)
        })

        CcbRoomDatabase.getDatabase(this)?.postplantingDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("POSTPLANTING", length)
        })

        CcbRoomDatabase.getDatabase(this)?.livraisonCentralDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("LIVRAISON_MAGCENTRAL", length)
        })

        CcbRoomDatabase.getDatabase(this)?.visiteurFormationDao()?.getUnSyncedAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.observe(this, Observer { items ->
            val length = items.size
            updateRVFeatureCount("FORMATION_VISITEUR", length)
        })

        CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.getAllLive(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.observe(this, Observer { items ->
            items?.isNotEmpty()?.let {
                if (it) items.first()?.let {
                    updateRVFeatureCount(it.typeDraft.toString(), 1, false)
                }
            }
        })

//        draftCountLive("PRODUCTEUR")
//        draftCountLive("INFOS_PRODUCTEUR")
//        draftCountLive("PARCELLE")
//        draftCountLive("MENAGE")
//        draftCountLive("APPLICATION")
//        draftCountLive("INSPECTION")
//        draftCountLive("ESTIMATION")
//        draftCountLive("PARCELLES")
//        draftCountLive("FORMATION")
//        draftCountLive("SSRTECLMRS")
//        draftCountLive("LIVRAISON")
//        draftCountLive("AGRO_EVALUATION")
//        draftCountLive("AGRO_DISTRIBUTION")
//        draftCountLive("POSTPLANTING")
//        draftCountLive("LIVRAISON_MAGCENTRAL")
//        draftCountLive("FORMATION_VISITEUR")

    }

//    fun draftCountLive(featName: String){
//
//
//
//    }

    fun updateRVFeatureCount(featName:String = "", length:Int = 0, isUnSync:Boolean = true){
        val lisFeat = (recyclerViewFeature.adapter as FeatureAdapter).getListFeatures()
        lisFeat.forEach {
            if(it.type?.lowercase() == featName.lowercase()){
                if(isUnSync) it.countSync = length
                else it.countDraft = (it.countDraft + length)
            }
        }
        (recyclerViewFeature.adapter as FeatureAdapter).updateFeatures(lisFeat)
    }

    fun hideExpandFromAdapter(){
        val roles: MutableList<String> = GsonUtils.fromJson(SPUtils.getInstance().getString("menu"), object : TypeToken<MutableList<String>>(){}.type)
        currentExpandLayout?.let {
            containerFeatureDash.visibility = View.GONE
            showAllExpandable(it, expandableList)

            hideNotExistFeature(roles, it)
        }
        expandableList.forEach {
            if(it.isExpanded) it.toggleLayout()
        }
    }

//    private fun setupBarChartView() {
//
//
//        val chart = findViewById<BarChart>(R.id.chart_enreg)
//
//
//        val data: MutableList<Data> = ArrayList<Data>()
//        data.add(Data(0f, producteurDao?.getSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "PRODUCTEUR", Color.BLACK))
//        data.add(Data(1f, parcelleDao?.getSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "PARCELLE", Color.BLUE))
//        data.add(Data(2f, formationDao?.getSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "FORMATION", Color.MAGENTA))
//        data.add(Data(3f, CcbRoomDatabase.getDatabase(this)?.producteurMenageDoa()?.getSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "MENAGE", Color.GRAY))
//        data.add(Data(4f, CcbRoomDatabase.getDatabase(this)?.evaluationArbreDao()?.getSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "EVALUATION", Color.GREEN))
//
//        Commons.applyChartSetting(this, chart, data)
//
//        setData(chart, data)
//
//        val chartUnSync = findViewById<BarChart>(R.id.chart_unsync)
//
//
//        val dataUnSync: MutableList<Data> = ArrayList<Data>()
//        dataUnSync.add(Data(0f, producteurDao?.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "PRODUCTEUR", Color.BLACK))
//        dataUnSync.add(Data(1f, parcelleDao?.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "PARCELLE", Color.BLUE))
//        dataUnSync.add(Data(2f, formationDao?.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "FORMATION", Color.MAGENTA))
//        dataUnSync.add(Data(3f, CcbRoomDatabase.getDatabase(this)?.producteurMenageDoa()?.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.count()?.toFloat()?:0.0f, "MENAGE", Color.GRAY))
//        dataUnSync.add(Data(4f, CcbRoomDatabase.getDatabase(this)?.evaluationArbreDao()?.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID))?.count()?.toFloat()?:0.0f, "EVALUATION", Color.GREEN))
//
//        Commons.applyChartSetting(this, chartUnSync, dataUnSync)
//
//        setData(chartUnSync, dataUnSync)
//
//        val chartDraft = findViewById<BarChart>(R.id.chart_draft)
//
//
//        val dataDraft: MutableList<Data> = ArrayList<Data>()
//        dataDraft.add(Data(0f, CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "producteur")?.toFloat()?:0.0f, "PRODUCTEUR", Color.BLACK))
//        dataDraft.add(Data(1f, CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "parcelle")?.toFloat()?:0.0f, "PARCELLE", Color.BLUE))
//        dataDraft.add(Data(2f, CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "formation")?.toFloat()?:0.0f, "FORMATION", Color.MAGENTA))
//        dataDraft.add(Data(3f, CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "menage")?.toFloat()?:0.0f, "MENAGE", Color.GRAY))
//        dataDraft.add(Data(4f, CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "evaluation_arbre")?.toFloat()?:0.0f, "EVALUATION", Color.GREEN))
//
//        Commons.applyChartSetting(this, chartDraft, dataDraft)
//
//        setData(chartDraft, dataDraft)
//    }

    private fun generateBarData(dataList:MutableList<Data>): BarData? {
        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()
        val listBar = ArrayList<String>()
        val listBar2 = ArrayList<String>()
        val listBarColor = ArrayList<Int>()
        val listBarColor2 = ArrayList<Int>()

        for (index in 0 until dataList.size-1 ) {
            if(dataList.get(index) != null){
                entries1.add(BarEntry(dataList.get(index).xValue, dataList.get(index).yValue))
                listBar.add(dataList.get(index).xAxisValue)
                listBarColor.add(dataList.get(index).xAxisColor)
            }
            if(dataList.get(index+1) != null){
                entries2.add(BarEntry(dataList.get(index+1).xValue, dataList.get(index+1).yValue))
                listBar2.add(dataList.get(index+1).xAxisValue)
                listBarColor2.add(dataList.get(index+1).xAxisColor)
            }
        }

        val set1 = BarDataSet(entries1, "${listBar.toModifString(true, " - ")}")
        set1.setStackLabels(listBar.toTypedArray())
        set1.color = Color.rgb(60, 220, 78)
        set1.valueTextColor = Color.rgb(60, 220, 78)
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT

//        val set2 = BarDataSet(entries2, "")
//        set1.stackLabels = listBar2.toTypedArray()
//        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255))
//        set2.valueTextColor = Color.rgb(61, 165, 255)
//        set2.valueTextSize = 10f
//        set2.axisDependency = YAxis.AxisDependency.LEFT

        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        val d = BarData(set1)
        d.barWidth = barWidth

        // make this BarData object grouped
//        d.groupBars(dataList.first().xValue, groupSpace, barSpace) // start at x = 0
        return d
    }

    private fun hideNotExistFeature(
        roles: MutableList<String>,
        expandableLayout: ExpandableLayout
    ) {
        if( (roles.containsAll(listOf("PRODUCTEUR")) == false && roles.containsAll(listOf("PARCELLE")) == false && roles.containsAll(listOf("ESTIMATION")) == false) && expandableLayout.tag.toString().equals("expand0") ) lexpand0.visibility = View.GONE
        if( (roles.containsAll(listOf("PARCELLES")) == false && roles.containsAll(listOf("PARCELLES")) == false && roles.containsAll(listOf("FORMATION")) == false && roles.containsAll(listOf("FORMATION_VISITEUR")) == false && roles.containsAll(listOf("APPLICATION")) == false && roles.containsAll(listOf("INSPECTION")) == false) && expandableLayout.tag.toString().equals("expand1") ) lexpand1.visibility = View.GONE
        if( (roles.containsAll(listOf("LIVRAISON")) == false && roles.containsAll(listOf("LIVRAISON_MAGCENTRAL")) == false) && expandableLayout.tag.toString().equals("expand2") ) lexpand2.visibility = View.GONE
        if( (roles.containsAll(listOf("MENAGE")) == false && roles.containsAll(listOf("SSRTECLMRS")) == false) && expandableLayout.tag.toString().equals("expand3") ) lexpand3.visibility = View.GONE
        if( (roles.containsAll(listOf("AGRO_EVALUATION")) == false && roles.containsAll(listOf("AGRO_DISTRIBUTION")) == false && roles.containsAll(listOf("POSTPLANTING")) == false) && expandableLayout.tag.toString().equals("expand4") ) lexpand4.visibility = View.GONE
        //if( () && expandableLayout.tag.toString().equals("expand5") ) lexpand5.visibility = View.GONE
        //if(roles.containsAll(listOf("APPLICATION", "INSPECTION")) == false && expandableLayout.tag.toString().equals("expand6") ) lexpand6.visibility = View.GONE
    }

    private fun hideOtherExpandable(expandableLayout: ExpandableLayout, expandableList: MutableList<ExpandableLayout>) {
        expandableList.forEach {
            if(it.id != expandableLayout.id){
                it.visibility = View.GONE
            }else{
                val position = (it.tag as String).replace("expand", "").toInt()

                //carouselRecyclerview?.adapter?.unregisterAdapterDataObserver(indicator!!.getAdapterDataObserver())

                listOfFeatures.clear()
//                LogUtils.d(listOfFeatureCloned.size)
//                LogUtils.d("Pos "+position)
                var isThereItem = false

                listOfFeatureCloned.forEach {
                    if(it.categorie == position) {
                        listOfFeatures.add(it)
                        //LogUtils.d(it.title)
                        isThereItem = true
                    }
                }
                if(isThereItem) {
                    containerFeatureDash.visibility = VISIBLE
                    //carouselRecyclerview.scrollToPosition(0)
                }else containerFeatureDash.visibility = GONE
                (recyclerViewFeature.adapter as FeatureAdapter) ?.notifyDataSetChanged()

//                if(carouselRecyclerview?.adapter?.itemCount!! > 0){
//                    val pagerSnapHelper = PagerSnapHelper()
//                    pagerSnapHelper.attachToRecyclerView(carouselRecyclerview)
//
//                    (indicator as CircleIndicator2).attachToRecyclerView(carouselRecyclerview!!, pagerSnapHelper)
//                    carouselRecyclerview?.adapter?.registerAdapterDataObserver(indicator!!.getAdapterDataObserver());
//                }
            }
        }
    }

    private fun showAllExpandable(expandableLayout: ExpandableLayout, expandableList: MutableList<ExpandableLayout>) {
        expandableList.forEach {
            it.visibility = View.VISIBLE
        }
//        listOfFeatures.clear()
//        (carouselRecyclerview.adapter as FeatureAdapter) ?.notifyDataSetChanged()
    }

    private fun updateListOfFeature() {

        listOfFeatures.clear()
        listOfFeatureCloned.clear()
        val menuToken = object : TypeToken<MutableList<String>>() {}.type
        val roles: MutableList<String> = GsonUtils.fromJson(SPUtils.getInstance().getString("menu"), menuToken)

//        LogUtils.d(roles)
        roles.map {
            //LogUtils.d(it)
            when(it.uppercase()) {
                "LOCALITES","LOCALITE" -> {
                    // linearLocalite.visibility = View.VISIBLE
                }
                "PRODUCTEURS","PRODUCTEUR" -> {
//                    listOrderItem.add(1)
//                    listOrderItem.add(2)
                    //setClickListenForFeature(1);
                    //setClickListenForFeature(2);
//                    linealProducteur.visibility = View.VISIBLE
//                    linearUniteAgricole.visibility = View.VISIBLE

                    listOfFeatures.add(FeatureModel("PRODUCTEUR",
                        countSync = producteurDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "producteur")!!,
                        type = "PRODUCTEUR",
                        //image = R.drawable.producteurc,
                        icon = R.drawable.ic_farmer,
                        canAdd = true,
                        canEdit = true,
                        canViewUpdate = false,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("producteurc.png")})

                    listOfFeatures.add(FeatureModel("INFOS PRODUCTEUR",
                        countSync = CcbRoomDatabase.getDatabase(this)?.infosProducteurDao()
                            ?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "infos_producteur")!!,
                        type = "INFOS_PRODUCTEUR",
                        icon = R.drawable.ic_profile_producteur,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("infosproducteur.png") })

                }
                "PARCELLE" -> {
                    //listOrderItem.add(4)
                    //setClickListenForFeature(4);
                    //linealParcel.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("IDENTIFICATIONS PARCELLES",
                        countSync = parcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "parcelle")!!,
                        type = "PARCELLE",
                        categorie = 0,
                        //image = R.drawable.parcelles,
                        icon = R.drawable.ic_parcel,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("parcelles.png")})
                }
                "MENAGES","MENAGE" -> {
                    //listOrderItem.add(3)
                    //setClickListenForFeature(3);
                    //linealMenage.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("ENQUÊTE MÉNAGE",
                        countSync = producteurMenageDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "menage")!!,
                        type = "MENAGE",
                        categorie = 3,
                        //image = R.drawable.menage,
                        icon = R.drawable.ic_menage,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("menage.png")})
                }
                "SUIVIAPPLICATIONS","APPLICATION" -> {
                    //listOrderItem.add(10)
                    //setClickListenForFeature(10);
                    //linealSuiviApplictions.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("APPLICATIONS PHYTOS",
                        countSync = CcbRoomDatabase.getDatabase(this)?.suiviApplicationDao()?.getUnSyncedAll()?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "application")!!,
                        type = "APPLICATION",
                        categorie = 1,
                        //placeholder = R.drawable.suiviapplication,
                        icon = R.drawable.application_phyto,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("application_phyto.png")})
                }
                "EVALUATIONS","INSPECTION" -> {
                    //listOrderItem.add(6)
                    //setClickListenForFeature(6);
                    //linearEvaluation.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("INSPECTIONS",
                        countSync =  CcbRoomDatabase.getDatabase(this)?.inspectionDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "inspection")!!,
                        type = "INSPECTION",
                        //image = R.drawable.evaluation,
                        categorie = 1,
                        icon = R.drawable.baseline_elevator,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("evaluation.png")})
                }
                "ESTIMATIONS","ESTIMATION" -> {
                    //listOrderItem.add(9)
                    //setClickListenForFeature(9);
                    //linearCalculEstimation.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("ESTIMATIONS",
                        countSync = CcbRoomDatabase.getDatabase(this)?.estimationDao()?.getUnSyncedAll()?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "estimation")!!,
                        type = "ESTIMATION",
                        categorie = 0,
                        //image = R.drawable.estimations,
                        icon = R.drawable.ic_suivi_parcel,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("estimations.png")})

                }
                "SUIVIPARCELLES","PARCELLES" -> {
                    //listOrderItem.add(5)
                    //setClickListenForFeature(5);
                    //linealSuiviParcelle.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("SUIVIS PARCELLES",
                        countSync = suiviParcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "suivi_parcelle")!!,
                        type = "PARCELLES",
                        categorie = 1,
                        //image = R.drawable.suiviparcelle,
                        icon = R.drawable.ic_suivi_parcel,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("suiviparcelle.png")})
                }
                "SUIVIFORMATION","FORMATION" -> {
                    //listOrderItem.add(8)
                    //setClickListenForFeature(8);
                    //linearFormation.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("FORMATIONS",
                        countSync = formationDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "formation")!!,
                        type = "FORMATION",
                        categorie = 1,
                        //image = R.drawable.formation,
                        icon = R.drawable.ic_formation,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("formation.png")})
                }
                "SSRTECLMRS","SSRTECLMR" -> {
                    //listOrderItem.add(7)
                    //setClickListenForFeature(7);
                    //linearSSRT.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("SSRTE-CLMRS",
                        countSync = CcbRoomDatabase.getDatabase(this)?.enqueteSsrtDao()?.getUnSyncedAll()?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "ssrte")!!,
                        type = "SSRTECLMRS",
                        categorie = 3,
                        //image = R.drawable.ssrte,
                        icon = R.drawable.ssrte_ic,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("ssrte.png")})
                }
                "LIVRAISONS","LIVRAISON" -> {
                    //listOrderItem.add(11)
                    //setClickListenForFeature(11);
                    //linearLivraison.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("STOCK MAGASINS_SECTION",
                        countSync = livraisonDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "livraison")!!,
                        type = "LIVRAISON",
                        categorie = 2,
                        //image = R.drawable.livraison,
                        icon = R.drawable.livrais_mag_sect,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("livrais_mag_sect.png")})
                }
                "AGRO_EVALUATION" -> {
                    listOfFeatures.add(FeatureModel("EVALUATION BESOINS",
                        countSync = CcbRoomDatabase.getDatabase(this)?.evaluationArbreDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "AGRO_EVALUATION".lowercase())!!,
                        type = "AGRO_EVALUATION",
                        categorie = 4,
                        //image = R.drawable.livraison,
                        icon = R.drawable.arbre_black,
                        canAdd = true,
                        canEdit = false,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("arbre_black.png")})
                }
                "AGRO_DISTRIBUTION" -> {
                    listOfFeatures.add(FeatureModel("DISTRIBUTION D'ARBRE",
                        countSync = CcbRoomDatabase.getDatabase(this)?.distributionArbreDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "AGRO_DISTRIBUTION".lowercase())!!,
                        type = "AGRO_DISTRIBUTION",
                        categorie = 4,
                        //image = R.drawable.livraison,
                        icon = R.drawable.distrib_arbre,
                        canAdd = true,
                        canEdit = false,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("distrib_arbre.png")})
                }
                "POSTPLANTING" -> {
                    listOfFeatures.add(FeatureModel("EVALUATION POST-PLANTING",
                        countSync = CcbRoomDatabase.getDatabase(this)?.postplantingDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "postplanting")!!,
                        type = "POSTPLANTING",
                        categorie = 4,
                        //image = R.drawable.livraison,
                        icon = R.drawable.distrib_arbre,
                        canAdd = true,
                        canEdit = false,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("distrib_arbre.png")})
                }
                "LIVRAISON_MAGCENTRAL" -> {
                    listOfFeatures.add(FeatureModel("STOCK MAGASINS_CENTRAUX",
                        countSync = CcbRoomDatabase.getDatabase(this)?.livraisonCentralDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "suivi_livraison_central")!!,
                        type = "LIVRAISON_MAGCENTRAL",
                        categorie = 2,
                        //image = R.drawable.livraison,
                        icon = R.drawable.livrais_mag_central,
                        canAdd = true,
                        canEdit = false,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("livrais_mag_central.png")})
                }
                "FORMATION_VISITEUR" -> {
                    listOfFeatures.add(FeatureModel("VISITEUR FORMATION",
                        countSync = CcbRoomDatabase.getDatabase(this)?.visiteurFormationDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toInt())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "visiteur_formation")!!,
                        type = "FORMATION_VISITEUR",
                        categorie = 1,
                        //image = R.drawable.formation,
                        icon = R.drawable.visitor_form,
                        canAdd = true,
                        canEdit = false,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("visitor_form.png")})
                }
                else -> {}
            }
            //carouselRecyclerview?.adapter?.notifyDataSetChanged()
        }

        listOfFeatures.map {
            listOfFeatureCloned.add(it)
        }

        //LogUtils.d(listOfFeatures.map { it.type })

        recyclerViewFeature?.adapter?.let {
            it.notifyDataSetChanged()
        }

    }

    private fun setNavViewItems(roles: MutableList<String>) {

        var expandableLV = expandable_navigation1.init(this@DashboardAgentActivity)
        val listHeaders = mutableListOf<HeaderModel>()
        //LogUtils.d(roles)
        listOfFeatureCloned.forEach {


            if(roles.contains(it.type)){
                listOfFeatureClonedNav.add(it)
                var titlo = Commons.formatTitleOfNavView(it.title)

                val featured = HeaderModel(titlo  , it.icon, true)
                    .addChildModel(ChildModel("NOUVEAU"))

                if(it.canViewDraft) featured.addChildModel(ChildModel("BROUILLON"))
                if(it.canViewUpdate) featured.addChildModel(ChildModel("A MODIFIER"))
                if(it.canViewSync) featured.addChildModel(ChildModel("A ENVOYER"))
                listHeaders.add(featured)
                expandableLV.addHeaderModel(featured)
            }

            if(roles.contains("PRODUCTEUR") && it.type == "INFOS_PRODUCTEUR"){
                listOfFeatureClonedNav.add(it)
                var titlo = Commons.formatTitleOfNavView(it.title)
                val featured = HeaderModel(titlo  , it.icon, true)
                    .addChildModel(ChildModel("NOUVEAU"))

                if(it.canViewDraft) featured.addChildModel(ChildModel("BROUILLON"))
                if(it.canViewUpdate) featured.addChildModel(ChildModel("A MODIFIER"))
                if(it.canViewSync) featured.addChildModel(ChildModel("A ENVOYER"))
                listHeaders.add(featured)
                expandableLV.addHeaderModel(featured)
            }

        }

        expandableLV.build()
        //expandableLV.setAdapter(ExpandableListAdapter(this, listHeaders))
        expandableLV.addOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
                expandable_navigation1.setSelected(groupPosition)
                //drawer_layout.closeDrawer(GravityCompat.START)
                //LogUtils.d(listOfFeatureCloned.get(groupPosition).title)

                false
            })
            .addOnChildClickListener(OnChildClickListener { parent, v, groupPosition, childPosition, id ->
                expandable_navigation1.setSelected(groupPosition, childPosition)
                drawer_layout.closeDrawer(GravityCompat.START)
                val currentGroup = listOfFeatureClonedNav.get(groupPosition)
                val btnList = mutableListOf<String>("ADD")

                if(currentGroup.canViewDraft) btnList.add("DRAFTS")
                if(currentGroup.canViewUpdate) btnList.add("UPDATE")
                if(currentGroup.canViewSync) btnList.add("DATAS")
                val currName = btnList.get(childPosition)
                LogUtils.d(currName)
                LogUtils.d(currentGroup.type)
                Commons.redirectMenu(currentGroup.type.toString().lowercase(), "${currName}", this@DashboardAgentActivity)

                false
            })

        if(listOfFeatureClonedNav.size > 0 && expandable_navigation1.size > 0) expandable_navigation1.setSelected(0)

    }

    private fun setViewFeatureListing() {

        val manager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerViewFeature!!.setLayoutManager(manager)
        recyclerViewFeature?.adapter = FeatureAdapter(this@DashboardAgentActivity, listOfFeatures)

    }

}
