package ci.progbandama.mobile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.*
import ci.progbandama.mobile.activities.lists.*
import ci.progbandama.mobile.databinding.ActivityMenusActionRedirectionBinding
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.*
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils

class MenusActionRedirectionActivity : AppCompatActivity() {


    var from: String? = ""
    var formationDao: FormationDao? = null
    var localiteDao: LocaliteDao? = null
    var livraisonDao: LivraisonDao? = null
    var parcelleDao: ParcelleDao? = null
    var suiviParcelleDao: SuiviParcelleDao? = null
    var producteurDao: ProducteurDao? = null
    var producteurMenageDao: ProducteurMenageDao? = null
    var progbandRoomDatabase: ProgBandRoomDatabase? = null


    fun refreshDatasDraft() {
        binding.labelSeeDraftsCountenuAction.text =
            ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()?.countByType(
                SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(),
                from ?: ""
            ).toString()
    }


    fun refreshDatas(fromData: String) {
        when (fromData.uppercase()) {
            "LOCALITE" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = localiteDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )?.size.toString()
            }
            "PRODUCTEUR" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = producteurDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )?.size.toString()
            }
            "INFOS_PRODUCTEUR" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = ProgBandRoomDatabase.getDatabase(this)?.infosProducteurDao()
                    ?.getUnSyncedAll(
                        SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )?.size.toString()
            }
            "MENAGE" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = producteurMenageDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )?.size.toString()
            }
            "PARCELLE" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = parcelleDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )?.size.toString()
            }
            "PARCELLES" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = suiviParcelleDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )?.size.toString()
            }
            "INSPECTION" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = ProgBandRoomDatabase.getDatabase(this)?.inspectionDao()
                    ?.getUnSyncedAll(
                        SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                    )?.size.toString()
            }
            "SSRTECLMRS" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = ProgBandRoomDatabase.getDatabase(this)?.enqueteSsrtDao()
                    ?.getUnSyncedAll()?.size.toString()
            }
            "FORMATION" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = formationDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )?.size.toString()
            }
            "ESTIMATION" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = ProgBandRoomDatabase.getDatabase(this)?.estimationDao()
                    ?.getUnSyncedAll()?.size.toString()
            }
            "APPLICATION" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = ProgBandRoomDatabase.getDatabase(this)?.suiviApplicationDao()
                    ?.getUnSyncedAll()?.size.toString()
            }
            "LIVRAISON" -> {
                binding.labelUnsyncDatasCountCountenuAction.text = livraisonDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                )?.size.toString()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        refreshDatasDraft()
        refreshDatas(from ?: "")
    }

    private lateinit var binding: ActivityMenusActionRedirectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenusActionRedirectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            progbandRoomDatabase = ProgBandRoomDatabase.getDatabase(this);
            producteurDao = progbandRoomDatabase?.producteurDoa()
            parcelleDao = progbandRoomDatabase?.parcelleDao();
            localiteDao = progbandRoomDatabase?.localiteDoa();
            producteurMenageDao = progbandRoomDatabase?.producteurMenageDoa()
            suiviParcelleDao = progbandRoomDatabase?.suiviParcelleDao()
            formationDao = progbandRoomDatabase?.formationDao()
            livraisonDao = progbandRoomDatabase?.livraisonDao()

            from = it.getStringExtra("from")
            binding.labelTitleMenuAction.text = binding.labelTitleMenuAction.text.toString().plus(from?.uppercase()?.replace("_", " "))
            buildInterfaceAccordingAction(from ?: "")
        }

        binding.clickCloseMenuAction.setOnClickListener {
            finish()
        }

//        goToHomeAction.setOnClickListener {
//            ActivityUtils.startActivity(DashboardAgentActivity::class.java)
//        }

        binding.linearUpdateContentMenuAction.setOnClickListener {
            redirectMenu(from ?: "", "UPDATE")
        }

        binding.linearAddContentMenuAction.setOnClickListener {
            redirectMenu(from ?: "", "ADD")
        }

        binding.linearSeeDraftsMenuAction.setOnClickListener {
            redirectMenu(fromMenu = from ?: "", "DRAFTS")
        }

        binding.linearUnsyncDatasMenuAction.setOnClickListener {
            redirectMenu(from ?: "", "DATAS")
        }



    }


    fun buildInterfaceAccordingAction(fromMenu: String) {
        when (fromMenu.uppercase()) {
            "INSPECTION",
            "SSRTECLMRS",
            "LOCALITE",
            "INFOS_PRODUCTEUR",
            "MENAGE",
            "PARCELLES",
            "FORMATION",
            "ESTIMATION",
            "APPLICATION",
            "LIVRAISON" -> {
                binding.linearUpdateContentMenuAction.visibility = View.GONE
            }

        }
    }


    fun redirectMenu(fromMenu: String, actionMenu: String) {
        when (actionMenu.uppercase()) {
            "ADD" -> {
                when (fromMenu.uppercase()) {
                    "LOCALITE" -> Commons.showMessage("Cette fonctionnalité est désactivé", this, finished = true, callback = {}, positive = getString(R.string.compris), deconnec = false, showNo = false) // ActivityUtils.startActivity(LocaliteActivity::class.java)
                    "PRODUCTEUR" -> ActivityUtils.startActivity(ProducteurActivity::class.java)
                    "INFOS_PRODUCTEUR" -> ActivityUtils.startActivity(UniteAgricoleProducteurActivity::class.java)
                    "MENAGE" -> ActivityUtils.startActivity(ProducteurMenageActivity::class.java)
                    "PARCELLE" -> ActivityUtils.startActivity(ParcelleActivity::class.java)
                    "PARCELLES" -> ActivityUtils.startActivity(SuiviParcelleActivity::class.java)
                    "INSPECTION" -> ActivityUtils.startActivity(InspectionActivity::class.java)
                    "SSRTECLMRS" -> ActivityUtils.startActivity(SsrtClmsActivity::class.java)
                    "FORMATION" -> ActivityUtils.startActivity(FormationActivity::class.java)
                    "ESTIMATION" -> ActivityUtils.startActivity(CalculEstimationActivity::class.java)
                    "APPLICATION" -> ActivityUtils.startActivity(SuiviApplicationActivity::class.java)
                    "LIVRAISON" -> ActivityUtils.startActivity(LivraisonActivity::class.java)
                    "LIVRAISON" -> ActivityUtils.startActivity(EvaluationArbreActivity::class.java)
                    "LIVRAISON" -> ActivityUtils.startActivity(EvaluationArbreActivity::class.java)
                }
            }

            "UPDATE" -> {
                val intentUpdateContent = Intent(this, UpdateContentsListActivity::class.java)
                intentUpdateContent.putExtra("fromContent", fromMenu)
                ActivityUtils.startActivity(intentUpdateContent)
            }

            "DRAFTS" -> {
                val intentDraft = Intent(this, DatasDraftedListActivity::class.java)
                intentDraft.putExtra("fromMenu", fromMenu)
                ActivityUtils.startActivity(intentDraft)
            }

            "DATAS" -> {
                when (fromMenu.uppercase()) {
                    "LOCALITE" -> Commons.showMessage("Cette fonctionnalité est désactivé", this, finished = true, callback = {}, positive = getString(R.string.compris), deconnec = false, showNo = false) //ActivityUtils.startActivity(LocalitesListActivity::class.java)
                    "PRODUCTEUR" -> ActivityUtils.startActivity(ProducteursListActivity::class.java)
                    //"INFOS_PRODUCTEUR" -> ActivityUtils.startActivity(UniteAgricoleProducteurActivity::class.java)
                    "MENAGE" -> ActivityUtils.startActivity(MenageresListActivity::class.java)
                    "PARCELLE" -> ActivityUtils.startActivity(ParcellesListActivity::class.java)
                    "PARCELLES" -> ActivityUtils.startActivity(SuiviPacellesListActivity::class.java)
                    //"INSPECTION" -> ActivityUtils.startActivity(EvaluationActivity::class.java)
                    //"SSRTE" -> ActivityUtils.startActivity(SsrtClmsActivity::class.java)
                    "FORMATION" -> ActivityUtils.startActivity(FormationsListActivity::class.java)
                    //"ESTIMATION" -> ActivityUtils.startActivity(CalculEstimationActivity::class.java)
                    //"APPLICATION" -> ActivityUtils.startActivity(SuiviApplicationActivity::class.java)
                    "LIVRAISON" -> ActivityUtils.startActivity(LivraisonsListActivity::class.java)
                }
            }
        }
    }

}
