package ci.projccb.mobile.activities.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.DistribArbreAdapter
import ci.projccb.mobile.models.ArbreModel
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.DistributionArbreDao
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.calculateTotalHeight
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_distribution_arbre.clickSaveDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.*
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectLocaliteDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectParcelleDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectProducteurDistributionArbre
import kotlinx.android.synthetic.main.activity_distribution_arbre.selectSectionDistributionArbre
import kotlinx.android.synthetic.main.activity_suivi_parcelle.clickCloseBtn
import kotlinx.android.synthetic.main.activity_suivi_parcelle.imageDraftBtn

class DistributionArbreActivity : AppCompatActivity() {
    val sectionCommon = CommonData();
    val localiteCommon = CommonData();
    val producteurCommon = CommonData();
    val parcelleCommon = CommonData();
    private var distributionArbreDao: DistributionArbreDao? = null
    var draftedDataDistribution: DataDraftedModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution_arbre)

        distributionArbreDao = CcbRoomDatabase.getDatabase(this)?.distributionArbreDao()

        clickCloseBtn.setOnClickListener {
            finish()
        }

        clickSaveDistributionArbre.setOnClickListener {
            collectDatas()
        }

        clickCancelDistributionArbre.setOnClickListener {
            ActivityUtils.startActivity(Intent(this, this::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActivityUtils.getActivityByContext(this)?.finish()
        }

        imageDraftBtn.setOnClickListener {
            draftData(draftedDataDistribution ?: DataDraftedModel(uid = 0))
        }

        setOtherListenner()

        try {
            if (intent.getStringExtra("from") != null) {
                draftedDataDistribution = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
                    ?.getDraftedDataByID(intent.getIntExtra("drafted_uid", 0)) ?: DataDraftedModel(
                    uid = 0
                )
                undraftedDatas(draftedDataDistribution!!)
            }else{
                setAllSelection()
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }

    private fun setOtherListenner() {

//        Commons.setFiveItremRV(this, recyclerArbreListDistributionArbre, clickAddArbreDistributionArbre,
//            selectChoixDeLArbreDistributionArbre,
//            selectChoixStateArbrDistributionArbre,
//            selectChoixDeLArbreDistributionArbre,
//            editQuantitArbreDistribut,
//            editQuantitArbreDistribut,
//            defaultItemSize = 3,
//            libeleList = arrayListOf(
//                "Arbre concerné",
//                "Strate",
//                "Quantité",
//                "",
//                "",
//            )
//        )

    }

    private fun setAllSelection() {

        setupSectionSelection()

        val listArbreAndState: MutableList<ArbreModel>? = CcbRoomDatabase.getDatabase(this)?.arbreDao()?.getAll()

        recyclerArbreListDistrArbre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerArbreListDistrArbre.adapter = DistribArbreAdapter(listArbreAndState)
        recyclerArbreListDistrArbre.adapter?.notifyDataSetChanged()

        val totalHeight = calculateTotalHeight(this, recyclerArbreListDistrArbre, 80)

        val params = recyclerArbreListDistrArbre.layoutParams
        params.height = totalHeight
        recyclerArbreListDistrArbre.layoutParams = params

        //listArbreAndState.put(resources.getStringArray(R.array.nomScienArbreConseille).toList(),resources.getStringArray(R.array.stratArbrConseille).toList())

//        Commons.setListenerForSpinner(this,
//            "De quel strate s'agit-il ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectChoixStateArbrDistributionArbre,
//            listIem = resources.getStringArray(R.array.listStrat)
//                ?.toList() ?: listOf(),
//            onChanged = {
//                val listArbreADistri = mutableListOf<String>()
//                LogUtils.d(it)
//                if(it == 0){
//                    var counter = 0
//                    listArbreAndState.values.first().forEach{
//                        if(it.equals("strate 3", ignoreCase = true)) listArbreADistri.add(listArbreAndState.keys.first()[counter])
//                        counter++
//                    }
//                }
//
//                if(it == 1){
//                    var counter = 0
//                    listArbreAndState.values.first().forEach{
//                        if(it.equals("strate 2", ignoreCase = true)) listArbreADistri.add(listArbreAndState.keys.first()[counter])
//                        counter++
//                    }
//                }
//
//                if(it == 2){
//                    var counter = 0
//                    listArbreAndState.values.first().forEach{
//                        if(it.equals("strate 1", ignoreCase = true)) listArbreADistri.add(listArbreAndState.keys.first()[counter])
//                        counter++
//                    }
//                }
//
//                LogUtils.d(listArbreADistri)
//                setupSelectionArbreList(listArbreADistri)
//            },
//            onSelected = { itemId, visibility ->
//            })

    }

    private fun setupSelectionArbreList(listArbreADistri: MutableList<String>, currentVal: String? = null) {
//        Commons.setListenerForSpinner(this,
//            "De quel arbre s'agit-il ?","La liste des options semble vide, veuillez procéder à la synchronisation des données svp.",
//            spinner = selectChoixDeLArbreDistributionArbre,
//            listIem = listArbreADistri
//                ?.toList() ?: listOf(),
//            onChanged = {
//
//            },
//            onSelected = { itemId, visibility ->
//            })
    }

    fun getAllRVItemInList(
        viewGroup: ViewGroup,
        mutableListOfId: MutableList<String>,
        mutableListOfNom: MutableList<String>,
        mutableListOfLimit: MutableList<String>,
        mutableListOfQte: MutableList<String>,
    ) {
        val childCount = viewGroup.childCount

        for (i in 0 until childCount) {
            val childView = viewGroup.getChildAt(i)

            if ( childView is AppCompatTextView && childView.tag != null ) {
                val value = childView.text.toString()
                //LogUtils.d("Spinner ${value} "+ childView::class.java.simpleName)
                when(childView.tag){
                    "arbreId" -> mutableListOfId.add(value)
                    "arbreNom" -> mutableListOfNom.add(value)
                    "arbreLimit" -> mutableListOfLimit.add(value)
                }
            } else if ( childView is AppCompatEditText && childView.tag != null ) {
                // You've found an EditText with the specified tag, get its value
                val editText = childView as AppCompatEditText
                val value = editText.text.toString()
                when(childView.tag){
                    "arbreQte" -> mutableListOfQte.add(value)
                }
                //countField++
            } else if (childView is ViewGroup) {
                // If it's a ViewGroup, recursively call this method
//                if(childView.visibility == View.VISIBLE)
//                {
                    getAllRVItemInList(
                        viewGroup = childView,
                        mutableListOfId,
                        mutableListOfNom,
                        mutableListOfLimit,
                        mutableListOfQte,
                    )
//                }
            }
        }
    }

    private fun undraftedDatas(draftedDataDistribution: DataDraftedModel) {

    }

    private fun draftData(dataDraftedModel: DataDraftedModel) {

    }

    private fun collectDatas() {

        val idList = mutableListOf<String>()
        val nomList = mutableListOf<String>()
        val limitList = mutableListOf<String>()
        val qteList = mutableListOf<String>()
        getAllRVItemInList(recyclerArbreListDistrArbre, idList, nomList, limitList, qteList )

        LogUtils.d( recyclerArbreListDistrArbre.childCount )
        LogUtils.d(idList, nomList, limitList, qteList)

    }

    fun setupSectionSelection(currVal:String? = null, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {
        var sectionDao = CcbRoomDatabase.getDatabase(applicationContext)?.sectionsDao();
        var sectionList = sectionDao?.getAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        var libItem: String? = null
        currVal?.let { idc ->
            sectionList?.forEach {
                if(it.id == idc.toInt()) libItem = it.libelle
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la section !",
            "La liste des sections semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (sectionList?.size!! > 0) false else true,
            currentVal = libItem ,
            spinner = selectSectionDistributionArbre,
            listIem = sectionList?.map { it.libelle }
                ?.toList() ?: listOf(),
            onChanged = {

                val section = sectionList!![it]
                //ogUtils.d(section)
                sectionCommon.nom = section.libelle!!
                sectionCommon.id = section.id!!

                setLocaliteSpinner(sectionCommon.id!!, currVal1, currVal2, currVal3)

            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setLocaliteSpinner(id: Int, currVal1:String? = null, currVal2: String? = null, currVal3: String? = null) {

        var localiteDao = CcbRoomDatabase.getDatabase(applicationContext)?.localiteDoa();
        var localitesListi = localiteDao?.getLocaliteBySection(id)
        //LogUtils.d(localitesListi)
        var libItem: String? = null
        currVal1?.let { idc ->
            localitesListi?.forEach {
                if(it.id == idc.toInt()) libItem = it.nom
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la localité !",
            "La liste des localités semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (localitesListi?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectLocaliteDistributionArbre,
            listIem = localitesListi?.map { it.nom }
                ?.toList() ?: listOf(),
            onChanged = {

                localitesListi?.let { list ->
                    var localite = list.get(it)
                    localiteCommon.nom = localite.nom!!
                    localiteCommon.id = localite.id!!

                    setupProducteurSelection(localiteCommon.id!!, currVal2, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupProducteurSelection(id: Int, currVal2: String? = null, currVal3: String? = null) {
        val producteursList = CcbRoomDatabase.getDatabase(applicationContext)?.producteurDoa()
            ?.getProducteursByLocalite(localite = id.toString())

        var libItem: String? = null
        currVal2?.let { idc ->
            producteursList?.forEach {
                if (it.id == idc.toInt()) libItem = "${it.nom} ${it.prenoms}"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix du producteur !",
            "La liste des producteurs semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (producteursList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectProducteurDistributionArbre,
            listIem = producteursList?.map { "${it.nom!!} ${it.prenoms!!}" }
                ?.toList() ?: listOf(),
            onChanged = {

                producteursList?.let { list ->
                    var producteur = list.get(it)
                    producteurCommon.nom = "${producteur.nom!!} ${producteur.prenoms!!}"
                    producteurCommon.id = producteur.id!!

                    setupParcelleSelection(producteurCommon.id.toString(), currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })

    }

    fun setupParcelleSelection(producteurId: String?, currVal3: String? = null) {
        var parcellesList = CcbRoomDatabase.getDatabase(applicationContext)?.parcelleDao()
            ?.getParcellesProducteur(producteurId = producteurId.toString(), agentID = SPUtils.getInstance().getInt(
                Constants.AGENT_ID, 0).toString())

//        LogUtils.json(parcellesList)
        var libItem: String? = null
        currVal3?.let { idc ->
            parcellesList?.forEach {
                if (it.id == idc.toInt()) libItem = "(${it.anneeCreation}) ${it.superficieConcerne} ha"
            }
        }

        Commons.setListenerForSpinner(this,
            "Choix de la parcelle !",
            "La liste des parcelles semble vide, veuillez procéder à la synchronisation des données svp.",
            isEmpty = if (parcellesList?.size!! > 0) false else true,
            currentVal = libItem,
            spinner = selectParcelleDistributionArbre,
            listIem = parcellesList?.map { "(${it.anneeCreation}) ${it.superficieConcerne} ha" }
                ?.toList() ?: listOf(),
            onChanged = {

                parcellesList?.let { list ->
                    var parcelle = list.get(it)
                    parcelleCommon.nom = "(${parcelle.anneeCreation}) ${parcelle.superficieConcerne} ha"
                    parcelleCommon.id = parcelle.id!!

                    //setupParcelleSelection(parcelleCommon.id, currVal3)
                }


            },
            onSelected = { itemId, visibility ->

            })
    }
}