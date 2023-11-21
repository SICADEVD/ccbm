package ci.projccb.mobile.activities.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ci.projccb.mobile.R
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.TypeMachineModel
import ci.projccb.mobile.models.VisiteurFormationDao
import ci.projccb.mobile.models.VisiteurFormationModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.FormationDao
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.AssetFileHelper
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_evaluation_arbre.clickCancelEvaluationArbre
import kotlinx.android.synthetic.main.activity_evaluation_arbre.clickSaveEvaluationArbre
import kotlinx.android.synthetic.main.activity_evaluation_arbre.selectSectionEvaluationArbre
import kotlinx.android.synthetic.main.activity_producteur_menage.containerAutreMachineMenage
import kotlinx.android.synthetic.main.activity_producteur_menage.selectMachinePulMenage
import kotlinx.android.synthetic.main.activity_suivi_parcelle.clickCloseBtn
import kotlinx.android.synthetic.main.activity_suivi_parcelle.imageDraftBtn
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.containerNbrTravSocieteInfosProducteur
import kotlinx.android.synthetic.main.activity_unite_agricole_producteur.selectTravaiSocietInfosProducteur
import kotlinx.android.synthetic.main.activity_visiteur_formation.containerAutreLienParentVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.containerVisiteurIsProducteur
import kotlinx.android.synthetic.main.activity_visiteur_formation.selectFormationVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.selectLienParentVisitForm
import kotlinx.android.synthetic.main.activity_visiteur_formation.selectReprProducteurVisitForm

class VisiteurFormationActivity : AppCompatActivity() {
    private val formationCommon: CommonData = CommonData()
    private var formationDao: FormationDao? = null
    private var draftedDataVisit: DataDraftedModel? = null
    private var visiteurFormationDao: VisiteurFormationDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visiteur_formation)

        visiteurFormationDao = CcbRoomDatabase.getDatabase(this)?.visiteurFormationDao()
        formationDao = CcbRoomDatabase.getDatabase(this)?.formationDao()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveEvaluationArbre.setOnClickListener {
            collectDatas()
        }

        clickCancelEvaluationArbre.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftData(draftedDataVisit ?: DataDraftedModel(uid = 0))
        }

        setOtherListenner()

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataVisit = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(uid = 0)
                undraftedDatas(draftedDataVisit!!)
            }else{
                setAllSelection()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }

    private fun setOtherListenner() {

    }

    private fun undraftedDatas(draftedDataVisit: DataDraftedModel) {

    }

    private fun draftData(dataDraftedModel: DataDraftedModel) {

    }

    private fun setAllSelection() {

        val formationList = formationDao?.getAll(agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

        Commons.setListenerForSpinner(this,
            "Selectionner la formation !",
            "La liste des formations semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (formationList?.size!! > 0) false else true,
            spinner = selectFormationVisitForm,
            listIem = formationList?.map { "Formation ${it.id}" }
                ?.toList() ?: listOf(),
            onChanged = {

                val formation = formationList!![it]
                //ogUtils.d(section)
                formationCommon.nom = "Formation ${formation.id}"
                formationCommon.id = formation.id!!

            },
            onSelected = { itemId, visibility ->

            })

        Commons.setListenerForSpinner(this,
            "Representez vous un producteur ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectReprProducteurVisitForm,
            itemChanged = arrayListOf(Pair(1, "Oui")),
            listIem = resources.getStringArray(R.array.YesOrNo)
                ?.toList() ?: listOf(),
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if (itemId == 1) {
                    containerVisiteurIsProducteur.visibility = visibility
                }
            })

        Commons.setListenerForSpinner(this,
            "Quel lien avez vous ?",
            "La liste des éléments semble vide, veuillez procéder à la synchronisation des données svp.",
            spinner = selectLienParentVisitForm,
            itemChanged = arrayListOf(Pair(1, "Autre")),
            listIem = (resources.getStringArray(R.array.parentAffiliation)?.toList() ?: listOf()) ,
            onChanged = {

            },
            onSelected = { itemId, visibility ->
                if(itemId == 1) containerAutreLienParentVisitForm.visibility = visibility
            })
    }

    private fun collectDatas() {

    }
}