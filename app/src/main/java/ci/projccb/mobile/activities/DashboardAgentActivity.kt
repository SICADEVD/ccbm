package ci.projccb.mobile.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.lists.DatasDraftedListActivity
import ci.projccb.mobile.adapters.ExpandableListAdapter

import ci.projccb.mobile.adapters.FeatureAdapter
import ci.projccb.mobile.broadcasts.LoopAlarmReceiver
import ci.projccb.mobile.models.AgentModel
import ci.projccb.mobile.models.FeatureModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.services.GpsService
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.*
import com.google.android.material.navigation.NavigationView
import com.google.gson.reflect.TypeToken
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview
import com.skydoves.expandablelayout.ExpandableLayout
import com.techatmosphere.expandablenavigation.model.ChildModel
import com.techatmosphere.expandablenavigation.model.HeaderModel
import kotlinx.android.synthetic.main.activity_dashboard_agent.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator2
import java.net.UnknownHostException


/**
 * Created by didierboka.developer on 18/12/2021
 * mail for work:   (didierboka.developer@gmail.com)
 */


class DashboardAgentActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {


    private var listOfFeatureCloned: MutableList<FeatureModel> = arrayListOf()
    private val listOfFeatures = mutableListOf<FeatureModel>()
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


    fun bindDatas(agentModel: AgentModel?) {
        labelUserDashboard.text = agentModel?.firstname.toString().plus(" ".plus(agentModel?.lastname.toString()))
        titleAccount.text = agentModel?.firstname.toString().plus(" ".plus(agentModel?.lastname.toString()))
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

        CoroutineScope(IO).launch {
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


    override fun onResume() {
        super.onResume()
        //refreshDatas()

        MainScope().launch {
            checkNetworkAvailablility()
        }
        askLocationPermission()
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
        producteurDao = ccbRoomDatabase?.producteurDoa();
        parcelleDao = ccbRoomDatabase?.parcelleDao();
        localiteDao = ccbRoomDatabase?.localiteDoa();
        producteurMenageDao = ccbRoomDatabase?.producteurMenageDoa()
        suiviParcelleDao = ccbRoomDatabase?.suiviParcelleDao()
        formationDao = ccbRoomDatabase?.formationDao()
        livraisonDao = ccbRoomDatabase?.livraisonDao()

        agentLogged = agentDao?.getAgent(SPUtils.getInstance().getInt(Constants.AGENT_ID, 3))

        setContentView(R.layout.activity_dashboard_agent)

        bindDatas(agentModel = agentLogged)

        // .setNavigationItemSelectedListener(this)
        Commons.modifyIcColor(this@DashboardAgentActivity, imgProfileDashboard, R.color.white)
        imgProfileDashboard.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Deconnexion ?")
            builder.setCancelable(false)

            builder.setPositiveButton("Oui") { dialog, _ ->
                dialog.dismiss()
                this.finish()

                CcbRoomDatabase.getDatabase(this)?.agentDoa()
                    ?.logoutAgent(false, SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                ActivityUtils.startActivity(SplashActivity::class.java)
            }

            builder.setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        imgProfileDashboardNDrawer.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Deconnexion ?")
            builder.setCancelable(false)

            builder.setPositiveButton("Oui") { dialog, _ ->
                dialog.dismiss()
                this.finish()

                CcbRoomDatabase.getDatabase(this)?.agentDoa()
                    ?.logoutAgent(false, SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                ActivityUtils.startActivity(SplashActivity::class.java)
            }

            builder.setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        Commons.modifyIcColor(this@DashboardAgentActivity, imgBackDashboard, R.color.white)
        imgBackDashboard.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Voulez-vous quitter ?")
            builder.setCancelable(false)

            builder.setPositiveButton("Oui") { dialog, _ ->
                dialog.dismiss()
                this.finish()
            }

            builder.setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        /*linearLocalite.setOnClickListener {
            Commons.showMessage("Cette fonctionnalité est désactivé", this, finished = true, callback = {}, positive = "Compris !", deconnec = false, showNo = false)
            return@setOnClickListener;
            //  val intentLocalite = Intent(this, MenusActionRedirectionActivity::class.java)
            //  intentLocalite.putExtra("from", "localite")
            //  ActivityUtils.startActivity(intentLocalite)
        }*/

        imgMenuDashboard.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START);
        }

        Commons.modifyIcColor(this@DashboardAgentActivity, linearSync, R.color.white)
        linearSync.setOnClickListener {
            var message = "Mettre à jour la base de données... ?"

            if (!networkFlag) {
                message = "Vous n'etes pas connecté à internet pour effectuer cette action !"
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage(message)
            builder.setCancelable(false)

            if (networkFlag) {
                builder.setPositiveButton("Oui") { dialog, _ ->
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

                builder.setNegativeButton("Non") { dialog, _ ->
                    dialog.dismiss()
                }
            } else {
                builder.setPositiveButton("OK") { dialog, _ ->
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
        val expandableList = arrayListOf<ExpandableLayout>(
            expandIdentif,
            expandIdentif2,
            expandIdentif3,
            expandIdentif4,
            expandIdentif5,
            expandIdentif6,
            //expandIdentif7,
        )

        expandableList.forEach {
            it.apply {
                setOnExpandListener {
                    if (it) {
                        containerFeatureDash.visibility = View.GONE
                        showAllExpandable(this, expandableList)
                    } else {
                        containerFeatureDash.visibility = View.VISIBLE
                        hideOtherExpandable(this, expandableList)
                    }
                    LogUtils.d("Expand : ${it}")
                }
                parentLayout.setOnClickListener { this.toggleLayout() }
            }
        }

        //updateListOfFeature()
        updateListOfFeature()
        setNavViewItems()
        setViewFeatureListing()

        if(carouselRecyclerview?.adapter?.itemCount!! > 0){
            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(carouselRecyclerview)

            (indicator as CircleIndicator2).attachToRecyclerView(carouselRecyclerview!!, pagerSnapHelper)
            carouselRecyclerview?.adapter?.registerAdapterDataObserver(indicator!!.getAdapterDataObserver());
        }

        /*if(
            listOrderItem.containsAll(listOf(1,2,3,4))
            || listOrderItem.containsAll(listOf(1,2,3,4,5,6))
            || listOrderItem.size > 6
        ) {
            gridLayoutOfDashboard.columnCount = 2
            gridLayoutOfDashboard.requestLayout()
        }*/
        //  WorkManager.getInstance(this).enqueue()
        // scheduleAlarm()


        /*for (i in 1..10) {
            FakeLocaliteDatas.saveLocalite(i, this)
        }*/
    }

    private fun hideOtherExpandable(expandableLayout: ExpandableLayout, expandableList: ArrayList<ExpandableLayout>) {
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
                if(isThereItem) containerFeatureDash.visibility = VISIBLE else containerFeatureDash.visibility = GONE
                (carouselRecyclerview.adapter as FeatureAdapter) ?.notifyDataSetChanged()

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

    private fun showAllExpandable(expandableLayout: ExpandableLayout, expandableList: ArrayList<ExpandableLayout>) {
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

        roles.map {
            LogUtils.d(it)
            when (it.uppercase()) {
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
                        canViewUpdate = true,
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
                        canViewSync = false //can be false
                    ).apply { this.image = image.plus("infosproducteur.png") })

                }
                "PARCELLE" -> {
                    //listOrderItem.add(4)
                    //setClickListenForFeature(4);
                    //linealParcel.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("PARCELLE",
                        countSync = parcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "parcelle")!!,
                        type = "PARCELLE",
                        categorie = 1,
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
                    listOfFeatures.add(FeatureModel("MENAGE",
                        countSync = producteurMenageDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "menage")!!,
                        type = "MENAGE",
                        categorie = 5,
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
                    listOfFeatures.add(FeatureModel("LES TRAITEMENTS",
                        countSync = CcbRoomDatabase.getDatabase(this)?.suiviApplicationDao()?.getUnSyncedAll()?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "suivi_application")!!,
                        type = "SUIVI_APPLICATION",
                        categorie = 1,
                        placeholder = R.drawable.suiviapplication,
                        icon = R.drawable.ic_applicateurs,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = false //can be false
                    ).apply { this.image = image.plus("suiviapplication.png")})
                }
                "EVALUATIONS","EVALUATION" -> {
                    //listOrderItem.add(6)
                    //setClickListenForFeature(6);
                    //linearEvaluation.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("EVALUATION",
                        countSync =  CcbRoomDatabase.getDatabase(this)?.inspectionDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "inspection")!!,
                        type = "INSPECTION",
                        //image = R.drawable.evaluation,
                        icon = R.drawable.baseline_elevator,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = false //can be false
                    ).apply { this.image = image.plus("evaluation.png")})
                }
                "ESTIMATIONS","ESTIMATION" -> {
                    //listOrderItem.add(9)
                    //setClickListenForFeature(9);
                    //linearCalculEstimation.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("ESTIMATION",
                        countSync = CcbRoomDatabase.getDatabase(this)?.estimationDao()?.getUnSyncedAll()?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "calcul_estimation")!!,
                        type = "CALCUL_ESTIMATION",
                        //image = R.drawable.estimations,
                        icon = R.drawable.ic_suivi_parcel,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = false //can be false
                    ).apply { this.image = image.plus("estimations.png")})
                }
                "SUIVIPARCELLES","PARCELLES" -> {
                    //listOrderItem.add(5)
                    //setClickListenForFeature(5);
                    //linealSuiviParcelle.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("TECHNIQUE AGRICOLE",
                        countSync = suiviParcelleDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "suivi_parcelle")!!,
                        type = "SUIVI_PARCELLE",
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
                    listOfFeatures.add(FeatureModel("FORMATION",
                        countSync = formationDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "formation")!!,
                        type = "FORMATION",
                        categorie = 2,
                        //image = R.drawable.formation,
                        icon = R.drawable.ic_formation,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = false //can be false
                    ).apply { this.image = image.plus("formation.png")})
                }
                "SSRTECLMRS","SSRTECLMR" -> {
                    //listOrderItem.add(7)
                    //setClickListenForFeature(7);
                    //linearSSRT.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("SSRTECLMR",
                        countSync = CcbRoomDatabase.getDatabase(this)?.enqueteSsrtDao()?.getUnSyncedAll()?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "ssrte")!!,
                        type = "SSRTE",
                        categorie = 5,
                        //image = R.drawable.ssrte,
                        icon = R.drawable.baseline_follow,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = false //can be false
                    ).apply { this.image = image.plus("ssrte.png")})
                }
                "LIVRAISONS","LIVRAISON" -> {
                    //listOrderItem.add(11)
                    //setClickListenForFeature(11);
                    //linearLivraison.visibility = View.VISIBLE
                    listOfFeatures.add(FeatureModel("LIVRAISON",
                        countSync = livraisonDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
                        countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "livraison")!!,
                        type = "LIVRAISON",
                        categorie = 3,
                        //image = R.drawable.livraison,
                        icon = R.drawable.ic_shipped,
                        canAdd = true,
                        canEdit = true,
                        canViewDraft = true,
                        canViewSync = true //can be false
                    ).apply { this.image = image.plus("livraison.png")})
                }

                else -> {}
            }
            //carouselRecyclerview?.adapter?.notifyDataSetChanged()
        }

        listOfFeatures.add(FeatureModel("VISITEUR FORMATION",
            countSync = formationDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "visiteur_formation")!!,
            type = "VISITEUR_FORMATION",
            categorie = 2,
            //image = R.drawable.formation,
            icon = R.drawable.ic_formation,
            canAdd = true,
            canEdit = false,
            canViewDraft = false,
            canViewSync = false //can be false
        ).apply { this.image = image.plus("formation.png")})

//        listOfFeatures.add(FeatureModel("FORMATION PRODUCTEUR",
//            countSync = CcbRoomDatabase.getDatabase(this)?.visiteurFormationDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
//            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "visiteur_formation")!!,
//            type = "FORMATION",
//            categorie = 2,
//            //image = R.drawable.livraison,
//            icon = R.drawable.ic_shipped,
//            canAdd = true,
//            canEdit = false,
//            canViewDraft = false,
//            canViewSync = false //can be false
//        ).apply { this.image = image.plus("formation.png")})

        listOfFeatures.add(FeatureModel("SUIVI LIVRAISON",
            countSync = livraisonDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "suivi_livraison")!!,
            type = "SUIVI_LIVRAISON",
            categorie = 3,
            //image = R.drawable.livraison,
            icon = R.drawable.ic_shipped,
            canAdd = false,
            canEdit = false,
            canViewDraft = false,
            canViewSync = false //can be false
        ).apply { this.image = image.plus("livraison.png")})

        listOfFeatures.add(FeatureModel("MAGASIN",
            countSync = livraisonDao?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "magasin")!!,
            type = "MAGASIN",
            categorie = 3,
            //image = R.drawable.livraison,
            icon = R.drawable.ic_shipped,
            canAdd = false,
            canEdit = false,
            canViewDraft = false,
            canViewSync = false //can be false
        ).apply { this.image = image.plus("livraison.png")})



        listOfFeatures.add(FeatureModel("EVALUATION DES BESOINS",
            countSync = CcbRoomDatabase.getDatabase(this)?.evaluationArbreDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "evaluation_arbre")!!,
            type = "EVALUATION_ARBRE",
            categorie = 4,
            //image = R.drawable.livraison,
            icon = R.drawable.ic_shipped,
            canAdd = true,
            canEdit = false,
            canViewDraft = false,
            canViewSync = false //can be false
        ).apply { this.image = image.plus("estimations.png")})

        listOfFeatures.add(FeatureModel("DISTRIBUTION D'ARBRE",
            countSync = CcbRoomDatabase.getDatabase(this)?.distributionArbreDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "distribution_arbre")!!,
            type = "DISTRIBUTION_ARBRE",
            categorie = 4,
            //image = R.drawable.livraison,
            icon = R.drawable.ic_shipped,
            canAdd = true,
            canEdit = false,
            canViewDraft = false,
            canViewSync = false //can be false
        ).apply { this.image = image.plus("estimations.png")})

        listOfFeatures.add(FeatureModel("APPROVISIONNEMENT",
            countSync = CcbRoomDatabase.getDatabase(this)?.estimationDao()?.getUnSyncedAll()?.size!!,
            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "calcul_estimation")!!,
            type = "CALCUL_ESTIMATION",
            categorie = 4,
            //image = R.drawable.estimations,
            icon = R.drawable.ic_suivi_parcel,
            canAdd = false,
            canEdit = false,
            canViewDraft = false,
            canViewSync = false //can be false
        ).apply { this.image = image.plus("estimations.png")})

        listOfFeatures.add(FeatureModel("MONITORING",
            countSync = CcbRoomDatabase.getDatabase(this)?.estimationDao()?.getUnSyncedAll()?.size!!,
            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "calcul_estimation")!!,
            type = "CALCUL_ESTIMATION",
            categorie = 4,
            //image = R.drawable.estimations,
            icon = R.drawable.ic_suivi_parcel,
            canAdd = false,
            canEdit = false,
            canViewDraft = false,
            canViewSync = false //can be false
        ).apply { this.image = image.plus("estimations.png")})

//        listOfFeatures.add(FeatureModel("EVALUATION DES BESOINS",
//            countSync =  CcbRoomDatabase.getDatabase(this)?.inspectionDao()?.getUnSyncedAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())?.size!!,
//            countDraft = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), type = "inspection")!!,
//            type = "INSPECTION",
//            categorie = 4,
//            //image = R.drawable.evaluation,
//            icon = R.drawable.baseline_elevator,
//            canAdd = false,
//            canEdit = false,
//            canViewDraft = false,
//            canViewSync = false //can be false
//        ).apply { this.image = image.plus("evaluation.png")})

        listOfFeatures.map {
            listOfFeatureCloned.add(it)
        }
        LogUtils.d(listOfFeatures.size)

        carouselRecyclerview?.adapter?.let {
            it.notifyDataSetChanged()
        }

    }

    private fun setNavViewItems() {

        var expandableLV = expandable_navigation1.init(this@DashboardAgentActivity)
        val listHeaders = mutableListOf<HeaderModel>()
        listOfFeatureCloned.forEach {
            val featured = HeaderModel(it.title, it.icon, true)
                .addChildModel(ChildModel("NOUVEAU"))

            if(it.canViewDraft) featured.addChildModel(ChildModel("BROUILLON"))
            if(it.canViewUpdate) featured.addChildModel(ChildModel("A MODIFIER"))
            if(it.canViewSync) featured.addChildModel(ChildModel("A ENVOYER"))
            listHeaders.add(featured)
            expandableLV.addHeaderModel(featured)
        }

//        val tvmain = this.findViewById<ImageView>(com.techatmosphere.R.id.icon_menu)
//        Commons.modifyIcColor(this@DashboardAgentActivity, tvmain, R.color.text_color_white)

        expandableLV.build()
        //expandableLV.setAdapter(ExpandableListAdapter(this, listHeaders))
        expandableLV.addOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
                expandable_navigation1.setSelected(groupPosition)
                //drawer_layout.closeDrawer(GravityCompat.START)
                false
            })
            .addOnChildClickListener(OnChildClickListener { parent, v, groupPosition, childPosition, id ->
                expandable_navigation1.setSelected(groupPosition, childPosition)
                drawer_layout.closeDrawer(GravityCompat.START)
                val currentGroup = listOfFeatureCloned.get(groupPosition)
                val btnList = mutableListOf<String>("ADD")

                if(currentGroup.canViewDraft) btnList.add("DRAFTS")
                if(currentGroup.canViewUpdate) btnList.add("UPDATE")
                if(currentGroup.canViewSync) btnList.add("DATAS")
                val currName = btnList.get(childPosition)
                Commons.redirectMenu(currentGroup.type.toString(), "${currName}", this@DashboardAgentActivity)

//                when(childPosition){
//                    0 -> {
//                    }
//
//                    1 -> {
//                        if(){
//                            Commons.redirectMenu(currentGroup.type.toString(), "UPDATE", this@DashboardAgentActivity)
//                        }
//                    }
//                }
//                if(currentGroup.canViewDraft == false && currentGroup.canViewSync == false && currentGroup.canViewUpdate == false){
//
//                }else if(currentGroup.canViewDraft == false){
//                    when(childPosition){
//                        0 -> {
//                            Commons.redirectMenu(currentGroup.type.toString(), "ADD", this@DashboardAgentActivity)
//                        }
//
//                        1 -> {
//                            Commons.redirectMenu(currentGroup.type.toString(), "UPDATE", this@DashboardAgentActivity)
//                        }
//
//                        2 -> {
//                            Commons.redirectMenu(currentGroup.type.toString(), "DATAS", this@DashboardAgentActivity)
//                        }
//                    }
//                }else if(currentGroup.canViewSync == false){
//                    when(childPosition){
//                        0 -> {
//                            Commons.redirectMenu(currentGroup.type.toString(), "ADD", this@DashboardAgentActivity)
//                        }
//
//                        1 -> {
//                            Commons.redirectMenu(currentGroup.type.toString(), "UPDATE", this@DashboardAgentActivity)
//                        }
//
//                        2 -> {
//                            Commons.redirectMenu(currentGroup.type.toString(), "DRAFTS", this@DashboardAgentActivity)
//                        }
//                    }
//                }

                false
            })

        if(listOfFeatures.size > 0) expandable_navigation1.setSelected(0)

    }

    private fun setViewFeatureListing() {

        carouselRecyclerview?.adapter = FeatureAdapter(this@DashboardAgentActivity, listOfFeatures)
        //carouselRecyclerview.set3DItem(true)
        carouselRecyclerview?.setInfinite(false)
        //carouselRecyclerview?.setAlpha(true)
        carouselRecyclerview?.setFlat(true)
        carouselRecyclerview?.setIsScrollingEnabled(true)

        carouselRecyclerview?.setItemSelectListener(object : CarouselLayoutManager.OnSelected {
            override fun onItemSelected(position: Int) {
                //LogUtils.d(position)
                (carouselRecyclerview?.adapter as FeatureAdapter).setPositionSelected(position)
            }
        })

        carouselRecyclerview?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }
        })

    }

    private fun setDataClickListener() {
        /*linealProducteur.let {
            it.setOnClickListener {
                val intentLocalite = Intent(this, MenusActionRedirectionActivity::class.java)
                intentLocalite.putExtra("from", "producteur")
                ActivityUtils.startActivity(intentLocalite)
            }

        }
        linealParcel.let {
            it.setOnClickListener {
                val intentParcelle = Intent(this, MenusActionRedirectionActivity::class.java)
                intentParcelle.putExtra("from", "parcelle")
                ActivityUtils.startActivity(intentParcelle)
            }

        }

        linealSuiviParcelle.let{
            it.setOnClickListener {
                val intentSuiviParcelle = Intent(this, MenusActionRedirectionActivity::class.java)
                intentSuiviParcelle.putExtra("from", "suivi_parcelle")
                ActivityUtils.startActivity(intentSuiviParcelle)
            }

        }

        linealMenage.let {
            it.setOnClickListener {
                val intentMenage = Intent(this, MenusActionRedirectionActivity::class.java)
                intentMenage.putExtra("from", "menage")
                ActivityUtils.startActivity(intentMenage)
            }

        }

        linearEvaluation.let{
            it.setOnClickListener {
                val intentInspection = Intent(this, MenusActionRedirectionActivity::class.java)
                intentInspection.putExtra("from", "inspection")
                ActivityUtils.startActivity(intentInspection)
            }

        }

        // Merci de faire les test de la base de données
        linearUniteAgricole.let{
            it.setOnClickListener {
                val intentInfosProducteur =
                    Intent(this, MenusActionRedirectionActivity::class.java)
                intentInfosProducteur.putExtra("from", "infos_producteur")
                ActivityUtils.startActivity(intentInfosProducteur)
            }

        }
        linearFormation.let{
            it.setOnClickListener {
                val intentFormation = Intent(this, MenusActionRedirectionActivity::class.java)
                intentFormation.putExtra("from", "formation")
                ActivityUtils.startActivity(intentFormation)
            }

        }

        linearLivraison.let{
            it.setOnClickListener {
                val intentLivraison = Intent(this, MenusActionRedirectionActivity::class.java)
                intentLivraison.putExtra("from", "livraison")
                ActivityUtils.startActivity(intentLivraison)
            }

        }
        linearCalculEstimation.let{
            it.setOnClickListener {
                val intentCalculEstimation =
                    Intent(this, MenusActionRedirectionActivity::class.java)
                intentCalculEstimation.putExtra("from", "calcul_estimation")
                ActivityUtils.startActivity(intentCalculEstimation)
            }

        }

        linealSuiviApplictions.let{
            it.setOnClickListener {
                val intentSuiviApplicatio =
                    Intent(this, MenusActionRedirectionActivity::class.java)
                intentSuiviApplicatio.putExtra("from", "suivi_application")
                ActivityUtils.startActivity(intentSuiviApplicatio)
            }

        }
        linearSSRT.let{
            it.setOnClickListener {
                val intentSsrt = Intent(this, MenusActionRedirectionActivity::class.java)
                intentSsrt.putExtra("from", "ssrte")
                ActivityUtils.startActivity(intentSsrt)
            }

        }*/
    }

    private fun setClickListenForFeature(position: Int) {

        when(position){

        }
    }

    private fun getPlaceToViewInGrid(it: LinearLayout?) {
//        (it?.layoutParams as GridLayout.LayoutParams).columnSpec = GridLayout.spec(
//            GridLayout.UNDEFINED,GridLayout.FILL, 1f
//        )
    }
}
