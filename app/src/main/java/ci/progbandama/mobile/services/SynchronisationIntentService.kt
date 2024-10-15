package ci.progbandama.mobile.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ci.progbandama.mobile.R
import ci.progbandama.mobile.models.*
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.*
import ci.progbandama.mobile.repositories.datas.ArbreData
import ci.progbandama.mobile.repositories.datas.InsectesParasitesData
import ci.progbandama.mobile.repositories.datas.PesticidesAnneDerniereModel
import ci.progbandama.mobile.repositories.datas.PesticidesApplicationModel
import ci.progbandama.mobile.repositories.datas.PresenceAutreInsecteData
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.returnStringList
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.ListConverters
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response
import java.net.UnknownHostException


@SuppressWarnings("All")
/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class SynchronisationIntentService : IntentService("SynchronisationIntentService") {


    var postplantingDao: PostplantingDao? = null
    var postPlantingArbrDistribDao: PostPlantingArbrDistribDao? = null
    private var contextAct: Context? = null
    var inspectionDao: InspectionDao? = null
    var infosProducteurDao: InfosProducteurDao? = null
    var localiteDao: LocaliteDao? = null
    var campagneDao: CampagneDao? = null
    var producteurDao: ProducteurDao? = null
    var parcelleDao: ParcelleDao? = null
    var livraisonDao: LivraisonDao? = null
    var livraisonCentralDao: LivraisonCentralDao? = null
    var menageDao: ProducteurMenageDao? = null
    var formationDao: FormationDao? = null
    var evaluationArbreDao: EvaluationArbreDao? = null
    var visiteurFormationDao: VisiteurFormationDao? = null
    var distributionArbreDao: DistributionArbreDao? = null
    var suiviParcelleDao: SuiviParcelleDao? = null
    var suiviApplicationDao: SuiviApplicationDao? = null
    var enqueteSsrtDao: EnqueteSsrteDao? = null
    var estimationDao: EstimationDao? = null
    var suncLocaliteFlag = true
    var suncProducteursFlag = true
    var suncParcellesFlag = true
    var suncSuiviParcellesFlag = true
    var suncSuiviMenagesFlag = true
    var suncFormationsFlag = true
    var suncLivraisonsFlag = true
    var notificationManager: NotificationManager? = null


    companion object {
        const val TAG = "SynchronisationIntentService::class"
    }


    fun syncLocalite(localiteDao: LocaliteDao) {
        val localiteDatas = localiteDao.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        for (localite in localiteDatas) {
            try {
                val ecolesToken = object : TypeToken<MutableList<String>>() {}.type

                localite.ecolesNomsList = ApiClient.gson.fromJson(localite.nomsEcolesStringify, ecolesToken)
                val clientLocalite: Call<LocaliteModel> = ApiClient.apiService.synchronisationLocalite(localiteModel = localite)

                val responseLocalite: Response<LocaliteModel> = clientLocalite.execute()
                val localiteSync: LocaliteModel = responseLocalite.body()!!

                localiteSync.ecolesNomsList = mutableListOf()

                localiteDao.syncData(id = localiteSync.id!!, synced = true, localID = localite.uid)

                val producteurLocalitesList = producteurDao?.getProducteursUnSynchronizedLocal(
                    localite.uid.toString(),
                    SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
                )!!

                for (producteur in producteurLocalitesList) {
                    producteur.localitesId = localiteSync.id.toString()
                    producteurDao?.insert(producteur)
                }

            } catch (uhex: UnknownHostException) {
                uhex.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                ex.printStackTrace()
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        // Update producteurs whith new localite ids
        syncProducteur(producteurDao!!)
    }


    fun syncProducteur(producteurDao: ProducteurDao) {
        val producteurDatas = producteurDao.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )
        for (producteur in producteurDatas) {
            try {
                producteur.dateNaiss = Commons.convertDate(producteur.dateNaiss, true)
                producteur.certificats = GsonUtils.fromJson(producteur.certificatsStr, object : TypeToken<MutableList<String>>(){}.type)

                if (!producteur.photo.isNullOrEmpty()) {
                    val photoPath = producteur.photo
                    producteur.picture = Commons.convertPathBase64(photoPath, 1)
                }


                producteur.apply {
                    banqueAccount = null
                    blessed = null
                    esignaturePath = null
                    farmersCount = null
                    forestSuperficy = null
                    hasFarmsPapers = null
                    hasForest = null
                    mobileMoney = null
                    hasMobileMoney = null
                    hasOtherFarms = null
                    mobileMoney = null
                    paperGuards = null
                    rectoPath = null
                    versoPath = null
                    recuAchat = null
                    superficieculture = null
                    typeculture = null
                    under18Count = null
                    under18SchooledCount = null
                    under18SchooledNoPaperCount = null
                }

                if(producteur.id != 0){
                    producteur.apply {
                        variete = null
//                        section = null
//                        photo = null
//                        localite = null
//                        localite = localitesId
                        certificatsStr = null
                        certification = null
                        autreVariete = null
//                        autreProgramme = null
//                        num_ccc = null
                    }
                }

//                LogUtils.file(GsonUtils.toJson(producteur))
//
//                LogUtils.d(LogUtils.getCurrentLogFilePath())

                val clientProducteur: Call<ProducteurModel> = ApiClient.apiService.synchronisationProducteur(producteurModel = producteur)
                val response = clientProducteur.execute()

                if(response.code().toString().contains("422") || response.code() == 422){
                    //val buffer = Buffer()
                    val respText = response.errorBody()?.string().toString()
//                            if(respText.contains("phone1", ignoreCase = true) && respText.contains("phone2", ignoreCase = true)){
////                                LogUtils.d(respText)
//                                producteurDao.syncDataOnExist(
//                                    synced = 1,
//                                    localID = producteur.uid
//                                )
//                            }
//                            if(respText.contains("num_ccc", ignoreCase = true) && respText.contains("phone2", ignoreCase = true)){
////                                LogUtils.d(respText)
//                            }
                    producteurDao.syncDataOnExist(
                        synced = 1,
                        localID = producteur.uid
                    )
                }

                val producteurSynced = response.body()
                if(response.isSuccessful){
                    producteurSynced?.let {
//                            LogUtils.d(producteurSynced?.id)
//                            LogUtils.d(response.code())

                        producteurDao.syncData(
                            id = producteurSynced?.id!!,
                            synced = true,
                            localID = producteur.uid
                        )

                        infosProducteurDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteursId = producteurSynced.id.toString()
                            infosProducteurDao?.insert(it)
                        }

                        inspectionDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteursId = producteurSynced.id.toString()
                            inspectionDao?.insert(it)
                        }

                        livraisonDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteursId = producteurSynced.id.toString()
                            livraisonDao?.insert(it)
                        }

                        livraisonCentralDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteursId = producteurSynced.id.toString()
                            livraisonCentralDao?.insert(it)
                        }

                        menageDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteurs_id = producteurSynced.id.toString()
                            menageDao?.insert(it)
                        }

                        enqueteSsrtDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteursId = producteurSynced.id.toString()
                            enqueteSsrtDao?.insert(it)
                        }

                        parcelleDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteurId = producteurSynced.id.toString()
                            parcelleDao?.insert(it)
                        }

                        distributionArbreDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteurId = producteurSynced.id.toString()
                            distributionArbreDao?.insert(it)
                        }

                        evaluationArbreDao?.getUnSyncedByProdUid(producteur.uid.toString())?.forEach {
                            it.producteurId = producteurSynced.id.toString()
                            evaluationArbreDao?.insert(it)
                        }


                        // THIS SYNC DEPEND TO PRODUCTEUR_ID AND PARCELLE_ID

                        estimationDao?.getUnSyncedByProdAndParcUid(producteur.uid.toString())?.forEach {
                            it.producteurId = producteurSynced.id.toString()
                            estimationDao?.insert(it)
                        }

                        suiviParcelleDao?.getUnSyncedByProdAndParcUid(producteur.uid.toString())?.forEach {
                            it.producteursId = producteurSynced.id.toString()
                            suiviParcelleDao?.insert(it)
                        }

                        suiviApplicationDao?.getUnSyncedByProdAndParcUid(producteur.uid.toString())?.forEach {
                            it.producteur = producteurSynced.id.toString()
                            suiviApplicationDao?.insert(it)
                        }

                        // THIS SYNC DEPEND TO PRODUCTEUR_ID AND FORMATION_ID

                        visiteurFormationDao?.getUnSyncedByProdAndFormUid(producteur.uid.toString())?.forEach {
                            it.producteurId = producteurSynced.id.toString()
                            visiteurFormationDao?.insert(it)
                        }

                        // THIS SYNC DEPEND TO PRODUCTEUR_ID
                        formationDao?.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())?.forEach { formMod ->
                            if(formMod.producteursIdList?.contains(producteur.uid.toString()) == true){
                                if( producteurDao.getProducteurByID(producteur.uid) == null ) {
                                    formMod.producteursIdStr = formMod.producteursIdStr.let {
                                        var curValue = it
                                        if (curValue.contains(producteur.uid.toString())) curValue = curValue.replace(producteur.uid.toString(), producteurSynced.id.toString())
                                        // LogUtils.d(curValue)
                                        curValue
                                    }
                                }
                            }

                            formationDao?.insert(formMod)

                        }

                    }
                }
//                else{
//                    producteurDao.syncDataOnExist(
//                        synced = 1,
//                        localID = producteur.uid
//                    )
//                }

//                clientProducteur.enqueue(object: Callback<ProducteurModel>{
//
//                    override fun onResponse(
//                        call: Call<ProducteurModel>,
//                        response: Response<ProducteurModel>
//                    ) {
//                        var producteurSynced = response.body()
//
//
//
//                    }
//
//                    override fun onFailure(call: Call<ProducteurModel>, t: Throwable) {
//                        LogUtils.e(t.message)
//                    }
//
//                })

            } catch (uhex: UnknownHostException) {
                LogUtils.e(uhex.message)
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }


        syncParcelle(parcelleDao!!)
    }


    fun syncMenage(menageDao: ProducteurMenageDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            val menageDatas = menageDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            )

            for (menage in menageDatas) {
                menage.apply {
                    localiteNom = null
                    machine = null
                    gardeEmpruntMachine = null
                    id = null
                    empruntMachine = null
                    if(activiteFemme.isNullOrBlank()) {
                        activiteFemme = getString(R.string.non)
                    }
                }

                menage.apply {

                    sourcesEnergieList = returnStringList(sources_energies_id)
                    ordureMenageresList = returnStringList(ordures_menageres_id)

                }

                //LogUtils.d("VAR MENAGE : "+menage.equipements)

                try {
                    //LogUtils.e(TAG, "menage ID before -> ${menage.id}")
                    val clientMenage: Call<ProducteurMenageModel> =
                        ApiClient.apiService.synchronisationMenage(menage)

                    val responseMenage: Response<ProducteurMenageModel> = clientMenage.execute()
                    val menageSync: ProducteurMenageModel = responseMenage.body()!!

                    if(responseMenage.isSuccessful){
                        menageDao.syncData(
                            id = menageSync.id!!.toInt(),
                            synced = true,
                            localID = menage.uid.toInt()
                        )
                    }else if(responseMenage.code() == 501){
                        menageDao.syncData(
                            id = menageSync.id!!.toInt(),
                            synced = true,
                            localID = menage.uid.toInt()
                        )
                    }
                    //LogUtils.e(TAG, "menage ID after -> ${menageSync.id}")
                } catch (uhex: UnknownHostException) {
                    menageDao.deleteUid(
                        uId = menage.uid.toString()
                    )
                    FirebaseCrashlytics.getInstance().recordException(uhex)
                } catch (ex: Exception) {
                    menageDao.deleteUid(
                        uId = menage.uid.toString()
                    )
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }else recallServiceIntent()


        syncEnqueteSsrt(enqueteSsrtDao!!)

    }


    fun syncParcelle(parcelleDao: ParcelleDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0){
            val parcelleDatas = parcelleDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            )

            val parcelleWayPointsMappedToken = object : TypeToken<MutableList<String>>() {}.type

            for (parcelle in parcelleDatas) {
                try {
                    parcelle?.apply {
                        codeParc = null
//                        id = 0
//                        localiteNom = null
//                        nom = null
//                        perimeter = null
//                        prenoms = null
//                        producteurNom = null
                    }

                    if (!parcelle.wayPointsString.isNullOrEmpty()) parcelle.mappingPoints = ApiClient.gson.fromJson(parcelle.wayPointsString, parcelleWayPointsMappedToken)

                    parcelle.apply {
                        varieteO = returnStringList(varieteStr)?: arrayListOf()
                        protectionO = returnStringList(protectionStr)?: arrayListOf()
                        itemsO = GsonUtils.fromJson<MutableList<ArbreData>>(arbreStr, object : TypeToken<List<ArbreData>>(){}.type)
                        arbreStrate = GsonUtils.fromJson<MutableList<ParcAutreOmbrag>>(arbreStrateStr, object : TypeToken<List<ParcAutreOmbrag>>(){}.type)
                    }

//                    Commons.logErrorToFile(parcelle)

                    //LogUtils.e(TAG, "syncParcelle ID before -> ${parcelle.id}")
                    val clientParcelle: Call<ParcelleModel> = ApiClient.apiService.synchronisationParcelle(parcelle)

                    val response = clientParcelle.execute()

                    if(response.isSuccessful){

                        val parcelleSync: ParcelleModel = response.body()!!

                        parcelleDao.syncData(
                            id = parcelleSync.id!!,
                            synced = true,
                            codeparc = parcelleSync.codeParc.toString(),
                            localID = parcelle.uid.toInt()
                        )

                        estimationDao?.getUnSyncedByParcUid(parcelle.uid.toString())?.forEach {
                            it.parcelleId = parcelleSync.id.toString()
                            estimationDao?.insert(it)
                        }

                        suiviParcelleDao?.getSuiviParcellesUnSynchronizedLocal(
                            parcelleUid = parcelle.uid.toString(),
                            SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
                        )?.forEach {
                            it.parcellesId = parcelleSync.id.toString()
                            suiviParcelleDao?.insert(it)
                        }

                        suiviApplicationDao?.getUnSyncedByParcUid(parcelle.uid.toString())?.forEach {
                            it.parcelle_id = parcelleSync.id.toString()
                            suiviApplicationDao?.insert(it)
                        }

                    }else{

                        parcelleDao.deleteUid(parcelle.uid)

                    }

//                    clientParcelle.enqueue(object : Callback<ParcelleModel>{
//
//                        override fun onResponse(
//                            call: Call<ParcelleModel>,
//                            response: Response<ParcelleModel>
//                        ) {
//
//                        }
//
//                        override fun onFailure(call: Call<ParcelleModel>, t: Throwable) {
//                            LogUtils.e(t.message)
//                        }
//
//                    })

                } catch (uhex: UnknownHostException) {
                    FirebaseCrashlytics.getInstance().recordException(uhex)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
        }else recallServiceIntent()

        syncEstimation(estimationDao!!)

    }


    fun syncSuivi(suiviParcelleDao: SuiviParcelleDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        val parcelleDatas = parcelleDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0 && parcelleDatas?.size == 0) {

            val suiviDatas = suiviParcelleDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            )

            if (suiviDatas.size > 0) {
                for (suivi in suiviDatas) {
                    try {
                        // deserialize datas producteurs

                        suivi.apply {
                            campagneId = campagneDao!!.getAll()[0].id.toString()
                            pesticidesAnneDerniereList = GsonUtils.fromJson(pesticidesAnneDerniereStr, object : TypeToken<MutableList<PesticidesAnneDerniereModel>>() {}.type)
                            intrantsAnneDerniereList = GsonUtils.fromJson(intrantsAnneDerniereStr, object : TypeToken<MutableList<PesticidesAnneDerniereModel>>() {}.type)

                            insectesParasitesList = GsonUtils.fromJson(insectesParasitesStr, object : TypeToken<MutableList<InsectesParasitesData>>() {}.type)

                            presenceAutreInsecteList = GsonUtils.fromJson(presenceAutreInsecteStr, object : TypeToken<MutableList<PresenceAutreInsecteData>>() {}.type)

                            traitementList = GsonUtils.fromJson(traitementStr, object : TypeToken<MutableList<PesticidesAnneDerniereModel>>() {}.type)
                            insectesAmisList = GsonUtils.fromJson(insectesAmisStr, object : TypeToken<MutableList<String>>() {}.type)
                            nombreinsectesAmisList = GsonUtils.fromJson(nombreinsectesAmisStr, object : TypeToken<MutableList<String>>() {}.type)

                            animauxRencontres = GsonUtils.fromJson(animauxRencontresStringify, object : TypeToken<MutableList<String>>() {}.type)

                            arbreList = GsonUtils.fromJson(arbreStr, object : TypeToken<MutableList<String>>() {}.type)
                            arbreItemsList = GsonUtils.fromJson(arbreItemStr, object : TypeToken<MutableList<ArbreData>>() {}.type)
                        }


                        suivi.dateVisite = Commons.convertDate(suivi.dateVisite, true)

                        val clientSuivi: Call<SuiviParcelleModel> = ApiClient.apiService.synchronisationSuivi(suivi)

                        val response = clientSuivi.execute()

                        if(response.isSuccessful){

                            val suiviSynced: SuiviParcelleModel? = response.body()

                            suiviParcelleDao.syncData(
                                id = suiviSynced?.id!!,
                                synced = true,
                                localID = suivi.uid
                            )
                        }else{
                            suiviParcelleDao.deleteByUid(
                                suivi.uid
                            )
                        }
//                        clientSuivi.enqueue(object : Callback<SuiviParcelleModel>{
//                            override fun onResponse(
//                                call: Call<SuiviParcelleModel>,
//                                response: Response<SuiviParcelleModel>
//                            ) {
//
//                            }
//
//                            override fun onFailure(call: Call<SuiviParcelleModel>, t: Throwable) {
//                                LogUtils.e(t.message)
//                            }
//
//                        })

                    } catch (uhex: UnknownHostException) {
                        FirebaseCrashlytics.getInstance().recordException(uhex)
                    } catch (ex: Exception) {
                        LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                    }
                }
                //syncVisiteurFormation(visiteurFormationDao!!)
            } else {
            }
        }else recallServiceIntent()


        syncSuiviApplication(suiviApplicationDao!!)

    }

    fun syncVisiteurFormation(visiteurFormationDao: VisiteurFormationDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        val formationDatas = formationDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        );

        if(producteurDatas?.size == 0 && formationDatas?.size == 0 ) {

            val suiviDatas = visiteurFormationDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)
            )
    //        LogUtils.json(suiviDatas)
            if (suiviDatas.size > 0) {
                for (suivi in suiviDatas) {
                    try {
                        // deserialize datas producteurs

                        val clientSuivi: Call<VisiteurFormationModel> = ApiClient.apiService.synchronisationVisiteurFormation(suivi)

                        val response = clientSuivi.execute()

                        if(response.isSuccessful){
                            val visiteurFormationModel: VisiteurFormationModel? = response.body()

                            visiteurFormationDao.syncData(
                                id = visiteurFormationModel?.id!!,
                                synced = true,
                                localID = suivi.uid
                            )
                        }else{
                            visiteurFormationDao.deleteByUid(suivi.uid)
                        }
//                        clientSuivi.enqueue(object : Callback<VisiteurFormationModel>{
//                            override fun onResponse(
//                                call: Call<VisiteurFormationModel>,
//                                response: Response<VisiteurFormationModel>
//                            ) {
//
//
//                            }
//
//                            override fun onFailure(call: Call<VisiteurFormationModel>, t: Throwable) {
//    //                            try{
//    //                                visiteurFormationDao.deleteByUid(suivi.uid)
//    //                            }catch (ex: Exception){
//    //
//    //                            }
//                            }
//
//                        })


                    } catch (uhex: UnknownHostException) {
                        FirebaseCrashlytics.getInstance().recordException(uhex)
                    } catch (ex: Exception) {
                        LogUtils.e(ex.message)
                        FirebaseCrashlytics.getInstance().recordException(ex)
                    }
                }
                //syncEvaluationBesoin(evaluationArbreDao!!)
            } else {
            }

        }else recallServiceIntent()

        if (Build.VERSION.SDK_INT >= 26) {
            stopForeground(true)
            notificationManager?.cancel(1)
        }


    }

    fun syncEvaluationBesoin(evaluationArbreDao: EvaluationArbreDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            val suiviDatas = evaluationArbreDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)
            )

            if (suiviDatas.size > 0) {
                for (suivi in suiviDatas) {
                    try {
                        // deserialize datas producteurs
                        suivi.apply {
                            especesarbreList = returnStringList(especesarbreStr)
                            quantiteList = returnStringList(quantiteStr)
                        }

                        val clientSuivi: Call<EvaluationArbreModel> = ApiClient.apiService.synchronisationEvaluationBesoin(suivi)
                        val response = clientSuivi.execute()

                        if(response.isSuccessful){
                            val evaluationArbreModel: EvaluationArbreModel? = response.body()

                            evaluationArbreDao.syncData(
                                id = evaluationArbreModel?.id!!,
                                synced = true,
                                localID = suivi.uid
                            )
                        }else{
                            evaluationArbreDao.deleteByUid(
                                suivi.uid
                            )
                        }
//                        clientSuivi.enqueue(object : Callback<EvaluationArbreModel>{
//                            override fun onResponse(
//                                call: Call<EvaluationArbreModel>,
//                                response: Response<EvaluationArbreModel>
//                            ) {
//
//                            }
//
//                            override fun onFailure(call: Call<EvaluationArbreModel>, t: Throwable) {
//                                LogUtils.e(t.message)
//                            }
//
//                        })
                    } catch (uhex: UnknownHostException) {
                        FirebaseCrashlytics.getInstance().recordException(uhex)
                    } catch (ex: Exception) {
                        LogUtils.e(ex.message)
                        FirebaseCrashlytics.getInstance().recordException(ex)
                    }
                }
                //syncFormations(formationDao!!)
            } else {
            }

        }else recallServiceIntent()

        syncPostPlanting(postplantingDao!!)
    }

    fun syncPostPlanting(postplantingDao: PostplantingDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            val suiviDatas = postplantingDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            )

            if (suiviDatas.size > 0) {
                for (suivi in suiviDatas) {
                    try {
                        // deserialize datas producteurs
                        suivi.apply {
                            quantiterecueList = GsonUtils.fromJson(suivi.quantiterecueStr, object : TypeToken<Map<String, Map<String, String>>>(){}.type) //returnStringList(especesarbreStr)
                            quantiteList = GsonUtils.fromJson(suivi.quantiteStr, object : TypeToken<Map<String, Map<String, String>>>(){}.type)
                            quantitesurvecueeList = GsonUtils.fromJson(suivi.quantitesurvecueeStr, object : TypeToken<Map<String, Map<String, String>>>(){}.type)
                            commentaireList = GsonUtils.fromJson(suivi.commentaireStr, object : TypeToken<Map<String, Map<String, String>>>(){}.type)
                        }

                        val postplantDistr = postPlantingArbrDistribDao?.getPostPlantByPId(suivi.producteurId)
                        var listArbrItem = GsonUtils.fromJson<MutableList<PostPlantingItem>>(postplantDistr?.arbresStr, object : TypeToken<MutableList<PostPlantingItem>>(){}.type)

                        listArbrItem.map { postit ->
                            val liliModif = suivi.quantitesurvecueeList?.get(suivi.producteurId.toString())
                            var itemo = liliModif?.filter { it.key.equals(postit.id_arbre) }
                            itemo?.let {
                                it.values.first()?.let {
                                    postit.quantite = it
                                }
                            }
                        }

                        //LogUtils.d(listArbrItem)
                        postplantDistr?.arbresStr = GsonUtils.toJson(listArbrItem)
                        val clientSuivi: Call<PostPlantingModel> = ApiClient.apiService.synchronisationPostPlanting(suivi)

                        val response = clientSuivi.execute()

                        if(response.isSuccessful){
                            val postPlantingModel: PostPlantingModel? = response.body()

                            postplantingDao.syncData(
                                id = postPlantingModel?.id!!,
                                synced = true,
                                localID = suivi.uid
                            )

                            postplantDistr?.let {
                                postPlantingArbrDistribDao?.insert(it)
                            }
                            //postPlantingArbrDistribDao?.deleteById(suivi.producteurId)
                        }else{
                            postplantingDao.deleteByUid(
                                suivi.uid
                            )
                        }
//                        clientSuivi.enqueue(object : Callback<PostPlantingModel>{
//                            override fun onResponse(
//                                call: Call<PostPlantingModel>,
//                                response: Response<PostPlantingModel>
//                            ) {
//
//                            }
//
//                            override fun onFailure(call: Call<PostPlantingModel>, t: Throwable) {
//                                LogUtils.e(t.message)
//                            }
//
//                        })
                    } catch (uhex: UnknownHostException) {
                        FirebaseCrashlytics.getInstance().recordException(uhex)
                    } catch (ex: Exception) {
                        LogUtils.e(ex.message)
                        FirebaseCrashlytics.getInstance().recordException(ex)
                    }
                }
                //syncFormations(formationDao!!)
            } else {
            }

        }else recallServiceIntent()

        syncFormations(formationDao!!)
    }


    fun syncFormations(formationDao: FormationDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            val formationDatas = formationDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            );

            for (formation in formationDatas) {
                try {
                    // deserialize datas producteurs

                    formation.apply {
                        producteursIdList = GsonUtils.fromJson(formation.producteursIdStr, object : TypeToken<MutableList<String>>() {}.type)
                        typeFormationList = GsonUtils.fromJson(formation.typeFormationStr, object : TypeToken<MutableList<String>>() {}.type)
                        themeList = GsonUtils.fromJson(formation.themeStr, object : TypeToken<MutableList<String>>() {}.type)
                        sousThemeList = GsonUtils.fromJson(formation.sousThemeStr, object : TypeToken<MutableList<String>>() {}.type)

                        dureeFormation?.split(":").let {
                            hour = it?.get(0)?.toString()
                            minute = it?.get(1)?.toString()
                        }

                        dureeFormation = (if(hour?.length == 1) "0$hour:" else "$hour:")+(if(minute?.length == 1) "0$minute" else "$minute")

                        multiStartDate = Commons.convertDate(multiStartDate, true)
                        multiEndDate = Commons.convertDate(multiEndDate, true)


                        photo_filename = photoFormation
                        rapport_filename = rapportFormation

                        photoFormation = Commons.convertPathBase64(photoFormation, 1)
                        rapportFormation = Commons.fileToBase64(rapportFormation)

                        photoListePresence = Commons.convertPathBase64(photoListePresence, 1)
                        docListePres = Commons.fileToBase64(docListePres)
                    }

                    Commons.logErrorToFile(formation)

                    val clientFormation: Call<FormationModel> = ApiClient.apiService.synchronisationFormation(formationModel = formation)

                    val response = clientFormation.execute()

                    if(response.isSuccessful){
                        val formationSynced: FormationModel? = response.body()

                        formationDao.syncData(
                            formationSynced?.id!!,
                            true,
                            formation.uid
                        )

                        visiteurFormationDao?.getUnSyncedByFormUid(formation.uid.toString())?.forEach {
                            it.producteurId = formationSynced.id.toString()
                            visiteurFormationDao?.insert(it)
                        }

                    }else{
                        formationDao.deleteByUid(
                            formation.uid
                        )
                    }
//                    clientFormation.enqueue(object : Callback<FormationModel>{
//                        override fun onResponse(
//                            call: Call<FormationModel>,
//                            response: Response<FormationModel>
//                        ) {
//
//                        }
//
//                        override fun onFailure(call: Call<FormationModel>, t: Throwable) {
//                            LogUtils.e(t.message)
//                        }
//
//                    })


                } catch (uhex: UnknownHostException) {
                    FirebaseCrashlytics.getInstance().recordException(uhex)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }else recallServiceIntent()

        syncVisiteurFormation(visiteurFormationDao!!)

    }


    fun syncLivraison(livraisonDao: LivraisonDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            val livraisonDatas = livraisonDao.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            )

            livraisonDatas.map { livraisonPojo ->
                livraisonPojo.estimatDate = Commons.convertDate(livraisonPojo.estimatDate, true)

                livraisonPojo.itemList = GsonUtils.fromJson<MutableList<LivraisonSousModel>>(livraisonPojo.itemsStringify, object : TypeToken<MutableList<LivraisonSousModel>>() {}.type)

                //val listLivrSModJson = ApiClient.gson.toJson(livraisonSModList)
                livraisonPojo.apply {
                     itemsStringify = null
                     livraisonSousModelProdNamesStringify = null
                     livraisonSousModelProdIdsStringify = null
                     livraisonSousModelParcellesStringify = null
                     livraisonSousModelParcelleIdsStringify = null
                     livraisonSousModelTypesStringify = null
                     livraisonSousModelQuantitysStringify = null
                     livraisonSousModelAmountsStringify = null
                     livraisonSousModelScellesStringify = null
                }

                val clientLivraison: Call<LivraisonModel> = ApiClient.apiService.synchronisationLivraisonSection(livraisonModel = livraisonPojo)

                val responseLivraison: Response<LivraisonModel> = clientLivraison.execute()
                val livraisonSynced: LivraisonModel? = responseLivraison.body()

                try {
                    if (livraisonSynced != null) {
                        livraisonDao.syncData(
                            livraisonSynced.id!!,
                            true,
                            livraisonPojo.uid
                        )
                    }

                } catch (uhex: UnknownHostException) {
                    LogUtils.e(uhex.message)
                    FirebaseCrashlytics.getInstance().recordException(uhex)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
        }else recallServiceIntent()

        syncLivraisonMagCentral(livraisonCentralDao!!)

    }


    fun syncEstimation(estimationDao: EstimationDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        val parcelleDatas = parcelleDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0 && parcelleDatas?.size == 0) {
            try {
                val estimationDatas = estimationDao.getUnSyncedAll()

                estimationDatas.map { estimationPojo ->
                    estimationPojo.dateEstimation =
                        Commons.convertDate(estimationPojo.dateEstimation, toEng = true)

                    val clientEstimation: Call<EstimationModel> =
                        ApiClient.apiService.synchronisationEstimation(estimationPojo)

//                    Commons.debugModelToJson(estimationPojo)

                    val response = clientEstimation.execute()

                    if (response.isSuccessful) {
                        val responseEstimation: EstimationModel? = response.body()
                        estimationDao.syncData(
                            responseEstimation?.id!!,
                            true,
                            estimationPojo.uid!!
                        )
                    } else {
                        estimationDao?.deleteByUid(
                            estimationPojo.uid
                        )
                    }
//                    clientEstimation.enqueue(object : Callback<EstimationModel> {
//                        override fun onResponse(
//                            call: Call<EstimationModel>,
//                            response: Response<EstimationModel>
//                        ) {
//
//                        }
//
//                        override fun onFailure(call: Call<EstimationModel>, t: Throwable) {
//                            LogUtils.e(t.message)
//                        }
//
//                    })
                }


            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }else recallServiceIntent()

        syncSuivi(suiviParcelleDao!!)


    }


    fun syncSuiviApplication(suiviApplicationDao: SuiviApplicationDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        val parcelleDatas = parcelleDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0 && parcelleDatas?.size == 0) {
            try {
                val suiviApplicationDatas = suiviApplicationDao.getUnSyncedAll()

                suiviApplicationDatas.map { suiviApplication ->
                    suiviApplication.dateApplication = Commons.convertDate(suiviApplication.dateApplication, toEng = true)


                    suiviApplication.apply {
                        campagnesId = campagneDao?.getAll()?.get(0)?.id
                        hour = heureApplication?.split(":")?.get(0).toString()
                        minute = heureApplication?.split(":")?.get(1).toString()

                        pesticidesList = GsonUtils.fromJson(pesticidesStr, object : TypeToken<MutableList<PesticidesApplicationModel>>() {}.type)
                        maladiesList = GsonUtils.fromJson(maladiesStr, object : TypeToken<MutableList<String>>() {}.type)
                        autreMaladieList = GsonUtils.fromJson(autreMaladieStr, object : TypeToken<MutableList<String>>() {}.type)
                    }

                    val clientSuiviApplication: Call<SuiviApplicationModel> = ApiClient.apiService.synchronisationSuiviApplication(suiviApplication)
                    val response = clientSuiviApplication.execute()
                    if(response.isSuccessful){
                        val suiviApplicationSynced: SuiviApplicationModel? = response.body()
                        suiviApplicationDao.syncData(
                            suiviApplicationSynced?.id!!,
                            true,
                            suiviApplication.uid
                        )
                    }else{
                        suiviApplicationDao.deleteByUid(
                            suiviApplication.uid
                        )
                    }
//                    clientSuiviApplication.enqueue(object : Callback<SuiviApplicationModel>{
//                        override fun onResponse(
//                            call: Call<SuiviApplicationModel>,
//                            response: Response<SuiviApplicationModel>
//                        ) {
//
//                        }
//
//                        override fun onFailure(call: Call<SuiviApplicationModel>, t: Throwable) {
//                            LogUtils.e(t.message)
//                        }
//
//                    })


                }

            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }else recallServiceIntent()

        syncInfosProducteur(infosProducteurDao!!)


    }


    fun syncDistributionDarbre(distributionArbreDao: DistributionArbreDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {
            try {
                val distribArbrDatas = distributionArbreDao.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
    //            LogUtils.d("DISTRIBUTION ARBRE : ", distribArbrDatas)

                distribArbrDatas.map {distrib ->

                    GsonUtils.fromJson<Map<String, Map<String, String>>>(distrib.quantiteStr, object : TypeToken<Map<String, Map<String, String>>>(){}.type)?.let {
                        distrib.quantiteList = it
                    }

                    val clientRequ: Call<DistributionArbreModel> = ApiClient.apiService.synchronisationDistributionArbre(distrib)
                    val response = clientRequ.execute()

                    if(response.isSuccessful){

                        val responseItem = response.body()

                        distributionArbreDao.syncData(
                            responseItem?.id!!,
                            true,
                            distrib.uid
                        )

                        val prod = producteurDao?.getProducteurByID(distrib.producteurId?.toInt())

                        prod?.let {
                            var arbres: MutableList<PostPlantingItem>? = distrib.quantiteList?.get(distrib.producteurId.toString())?.map { PostPlantingItem(it.key.toString(), it.value.toString()) }?.toMutableList()
//                                    LogUtils.d(arbres)
                            postPlantingArbrDistribDao?.insert(PostPlantingArbrDistribModel(
                                uid = 0,
                                nom = it.nom,
                                prenoms = it.prenoms,
                                arbresStr = GsonUtils.toJson(arbres),
                                id = it.id,
                                isSynced = true,
                                origin = "remote",
                                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
                            ))
                        }

                        evaluationArbreDao?.deleteByProducteurId(distrib.producteurId)

                    }else{

                        distributionArbreDao.deleteByUid(
                            distrib.uid
                        )

                    }
//                    clientRequ.enqueue(object : Callback<DistributionArbreModel>{
//                        override fun onResponse(
//                            call: Call<DistributionArbreModel>,
//                            response: Response<DistributionArbreModel>
//                        ) {
//
//                        }
//
//                        override fun onFailure(call: Call<DistributionArbreModel>, t: Throwable) {
//    //                        distributionArbreDao.deleteByUid(
//    //                            distrib.uid
//    //                        )
//                            LogUtils.e(t.message)
//                        }
//
//                    })


                }

            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

        }else recallServiceIntent()

        syncEvaluationBesoin(evaluationArbreDao!!)

    }


    fun syncLivraisonMagCentral(livraisonCentralDao: LivraisonCentralDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            try {
                val livraisonCentralDatas = livraisonCentralDao.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
    //            LogUtils.d("livraisonVerMagCentralDao : ", livraisonCentralDatas)

                livraisonCentralDatas.map {

                    val livraisonList = GsonUtils.fromJson<MutableList<LivraisonCentralSousModel>>(it.itemsStringify, object : TypeToken<MutableList<LivraisonCentralSousModel>>(){}.type)

                    it.apply {
                        producteur_idList = livraisonList.map { "${it.producteur_id}" }.toMutableList()
                        producteursList = livraisonList.map { "${it.producteurs}" }.toMutableList()
                        parcelleList = livraisonList.map { "${it.parcelle}" }.toMutableList()
                        quantiteList = livraisonList.map { "${it.quantite}" }.toMutableList()
                        certificatList  = livraisonList.map { "${it.certificat}" }.toMutableList()
                        typeproduitList = livraisonList.map { "${it.typeproduit}" }.toMutableList()
                        typeList = arrayListOf()
                        GsonUtils.fromJson<MutableList<String>>(typeStr, object : TypeToken<MutableList<String>>(){}.type).map {
                            it.split(",").forEach {
                                if(it != null && it != "null"){
                                    (typeList as ArrayList<String>).add(it.trim())
                                }
                            }
                        }

                        poidsnet = livraisonList.sumBy { it?.quantite?.toInt()?:0 }.toString()

                        estimatDate = Commons.convertDate(estimatDate, toEng = true)
                    }

                    val clientRequ: Call<LivraisonCentralModel> = ApiClient.apiService.synchronisationLivraisonCentral(it)
                    val response = clientRequ.execute()

                    if(response.isSuccessful){

                        val responseItem = response.body()

                        livraisonCentralDao.syncData(
                            responseItem?.id!!,
                            true,
                            it.uid
                        )

                    }else{

                        livraisonCentralDao.deleteByUid(
                            it.uid.toString()
                        )

                    }
//                    clientRequ.enqueue(object : Callback<LivraisonCentralModel>{
//                        override fun onResponse(
//                            call: Call<LivraisonCentralModel>,
//                            response: Response<LivraisonCentralModel>
//                        ) {
//
//                        }
//
//                        override fun onFailure(call: Call<LivraisonCentralModel>, t: Throwable) {
//                            LogUtils.e(t.message)
//                        }
//
//                    })


                }


            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }else recallServiceIntent()

        syncMenage(menageDao!!)


    }


    fun syncEnqueteSsrt(enqueteSsrteDao: EnqueteSsrteDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            try {
                val enquetesDatas = enqueteSsrteDao.getUnSyncedAll()

                enquetesDatas.map { enquete ->
                    enquete.dateEnquete = Commons.convertDate(enquete.dateEnquete, toEng = true)
                    enquete.datenaissMembre = Commons.convertDate(enquete.datenaissMembre, toEng = true)

                    enquete.travauxLegers = ListConverters.stringToMutableList(enquete.travauxLegersStringify)
                    enquete.travauxDangereux = ListConverters.stringToMutableList(enquete.travauxDangereuxStringify)

                    enquete.lieuTravauxDangereux = ListConverters.stringToMutableList(enquete.lieuTravauxDangereuxStringify)
                    enquete.lieuTravauxLegers = ListConverters.stringToMutableList(enquete.lieuTravauxLegersStringify)

                    //enquete.raisonArretEcole = ListConverters.stringToMutableList(enquete.raisonArretEcoleStringify)

    //                travauxDangereuxStringify = GsonUtils.toJson(selectLequelTravEffectSSrte.selectedStrings)
    //                travauxLegersStringify = GsonUtils.toJson(selectLequelTrav2EffectSSrte.selectedStrings)
    //
    //                lieuTravauxDangereuxStringify = GsonUtils.toJson(selectEndroitTravEffectSSrte.selectedStrings)
    //                lieuTravauxLegersStringify = GsonUtils.toJson(selectEndroitTrav2EffectSSrte.selectedStrings)
    //                LogUtils.json(enquete)

                    val clientEnqueteSsrt: Call<EnqueteSsrtModel> = ApiClient.apiService.synchronisationEnqueteSsrt(enquete)
                    val responseEnqueteSsrt: Response<EnqueteSsrtModel> = clientEnqueteSsrt.execute()

                    val enqueteSsrtSynced: EnqueteSsrtModel? = responseEnqueteSsrt.body()
                    enqueteSsrteDao.syncData(
                        enqueteSsrtSynced?.id!!,
                        true,
                        enquete.uid
                    )
                }




            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
            }

        }else recallServiceIntent()

        syncDistributionDarbre(distributionArbreDao!!)

    }


    fun syncInspection(inspectionDao: InspectionDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            try {
                val inspectionsDatas = inspectionDao.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                val inspectionsToken = object : TypeToken<MutableList<QuestionResponseModel>>(){}.type

                inspectionsDatas.map { inspection ->
                    inspection.dateEvaluation = Commons.convertDate(inspection.dateEvaluation, toEng = true)
                    inspection.certificatList = mutableListOf<String>()
                    inspection.certificatList?.add(inspection.certificatStr!!)
    //                inspection.reponse = mutableMapOf()

                    var counter = 1;
    //                var note = 0;
                    ApiClient.gson.fromJson<MutableList<QuestionResponseModel>>(inspection.reponseStringify, inspectionsToken).map {
                        if(it.isTitle == false) {
                            inspection.reponse[counter.toString()] = it.noteLabel!!
                            inspection.commentaire[counter.toString()] = it.commentaire!!
                            counter++
                            //note += it.note!!.toInt()
                        }
                    }
    //
    //                inspection.noteInspection = note.toString()

                    inspection.reponseStringify = null

                    val clientInspection: Call<InspectionDTOExt> = ApiClient.apiService.synchronisationInspection(inspection)
                    val response = clientInspection.execute()

                    if(response.isSuccessful){
                        val inspectionSynced = response.body()

                        inspectionDao.syncData(
                            inspectionSynced?.id!!,
                            true,
                            inspection.uid
                        )

                        inspectionSynced.reponse_non_conformeStr = GsonUtils.toJson(inspectionSynced.reponseNonConforme)
                        inspectionSynced.reponse_non_applicaleStr = GsonUtils.toJson(inspectionSynced.reponseNonApplicale)

                        inspectionDao.updateNConformNApplicable(
                            inspection.uid,
                            inspectionSynced.reponse_non_conformeStr,
                            inspectionSynced.reponse_non_applicaleStr
                        )

                    }else{

                        inspectionDao.deleteByUid(inspection.uid)

                    }
//                    clientInspection.enqueue(object: Callback<InspectionDTOExt>{
//                            override fun onResponse(
//                                call: Call<InspectionDTOExt>,
//                                response: Response<InspectionDTOExt>
//                            ) {
//
//                            }
//
//                            override fun onFailure(call: Call<InspectionDTOExt>, t: Throwable) {
//
//                            }
//
//                        });
                }



            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }else recallServiceIntent()

        syncInspectionUpdate(inspectionDao!!)


    }

    fun syncInspectionUpdate(inspectionDao: InspectionDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            try {
                val inspectionsDatas = inspectionDao.getAllInspectionToUpdate()

                inspectionsDatas.map { inspection ->
                    val contentUpdate = GsonUtils.fromJson<InspectionUpdateDTO>(inspection.update_content, InspectionUpdateDTO::class.java)

                    val clientInspection: Call<InspectionDTOExt> = ApiClient.apiService.synchronisationInspectionUpdate(contentUpdate)

                    val response = clientInspection.execute()

                    if(response.isSuccessful){
                        val inspectionSynced = response.body()

                        inspectionSynced?.reponse_non_conformeStr = GsonUtils.toJson(inspectionSynced?.reponseNonConforme)
                        inspectionSynced?.reponse_non_applicaleStr = GsonUtils.toJson(inspectionSynced?.reponseNonApplicale)

                        inspectionDao.updateNConformNApplicable(
                            inspection.uid,
                            inspectionSynced?.reponse_non_conformeStr,
                            inspectionSynced?.reponse_non_applicaleStr
                        )

                        inspectionDao.updateApprobation(inspectionSynced?.approbation, inspection.uid)
                        inspectionDao.updateContent(null, inspection.uid)

                    }else{

                    }
//                    clientInspection.enqueue(object: Callback<InspectionDTOExt>{
//                        override fun onResponse(
//                            call: Call<InspectionDTOExt>,
//                            response: Response<InspectionDTOExt>
//                        ) {
//
//                        }
//
//                        override fun onFailure(call: Call<InspectionDTOExt>, t: Throwable) {
//
//                        }
//
//                    });
                }



            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }else recallServiceIntent()

        syncLivraison(livraisonDao!!)


    }


    fun syncInfosProducteur(infosProducteurDao: InfosProducteurDao) {

        val producteurDatas = producteurDao?.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        if(producteurDatas?.size == 0) {

            try {
                val infosDatas = infosProducteurDao.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

                infosDatas.map { info ->
                    try {
                        info.typeculture = ListConverters.stringToMutableList(info.typecultureStringify)
                        info.superficieculture = ListConverters.stringToMutableList(info.superficiecultureStringify)
                        info.numerosMM = ListConverters.stringToMutableList(info.numerosMMStr)
                        info.operateurMM = ListConverters.stringToMutableList(info.operateurMMStr)
                        info.typeactiviteList = ListConverters.stringToMutableList(info.typeactiviteStr)
                    }catch (e:Exception){
                        println(e.message)
                    }

                    // This field will be ignored
                    info.apply {
                        agentID = null
                        isSynced = null
                        maladiesenfantsStringify = null
                        typecultureStringify = null
                        superficiecultureStringify = null
                        id = null
                        localiteNom = null
                        producteursCode = null
                        producteursNom = null
                        typeactiviteStr = null
                        typeDocuments = null
                        scolarisesExtrait = null
                        recuAchat = null
                        personneBlessee = null
                        persEcole = null
                        paiementMM = null
                        operateurMMStr = null
                        numerosMMStr = null
                        numeroCompteMM = null
                        age18 = null
                    }

                    Commons.logErrorToFile(info)

                    val clientInfos: Call<InfosProducteurDTO> = ApiClient.apiService.synchronisationInfosProducteur(info)
                    val response = clientInfos.execute()

                    if(response.isSuccessful){

                        if(response.raw().message.contains("info existe", ignoreCase = true)){
                            infosProducteurDao.deleteProducteurInfo(
                                info.uid
                            )
                        }else{
                            infosProducteurDao.syncData(
                                response.body()?.id!!,
                                true,
                                info.uid
                            )
                        }

                    }else{

                        infosProducteurDao.deleteProducteurInfo(
                            info.uid
                        )

                    }
//                    clientInfos.enqueue(object: Callback<InfosProducteurDTO>{
//                        override fun onResponse(
//                            call: Call<InfosProducteurDTO>,
//                            response: Response<InfosProducteurDTO>
//                        ) {
//    //                        val responseInfos: Response<InfosProducteurDTO> = clientInfos.execute()
//    //                        LogUtils.d("RESPONSE CODE "+responseInfos.code())
//
//                        }
//
//                        override fun onFailure(call: Call<InfosProducteurDTO>, t: Throwable) {
//
//                        }
//
//                    });
                }


            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }else recallServiceIntent()

        syncInspection(inspectionDao!!)


    }


    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                val CHANNEL_ID = "field_connect"
                val channel = NotificationChannel(CHANNEL_ID, "ProgBand", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                notificationManager?.createNotificationChannel(channel)

                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("")
                    .build()
                startForeground(1, notification)
            }

            contextAct = this.applicationContext

            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }

            campagneDao = ProgBandRoomDatabase.getDatabase(this)?.campagneDao()
            localiteDao = ProgBandRoomDatabase.getDatabase(this)?.localiteDoa()
            producteurDao = ProgBandRoomDatabase.getDatabase(this)?.producteurDoa()
            parcelleDao = ProgBandRoomDatabase.getDatabase(this)?.parcelleDao()
            menageDao = ProgBandRoomDatabase.getDatabase(this)?.producteurMenageDoa()
            formationDao = ProgBandRoomDatabase.getDatabase(this)?.formationDao()
            evaluationArbreDao = ProgBandRoomDatabase.getDatabase(this)?.evaluationArbreDao()
            visiteurFormationDao = ProgBandRoomDatabase.getDatabase(this)?.visiteurFormationDao()
            distributionArbreDao = ProgBandRoomDatabase.getDatabase(this)?.distributionArbreDao()
            suiviParcelleDao = ProgBandRoomDatabase.getDatabase(this)?.suiviParcelleDao()
            livraisonDao = ProgBandRoomDatabase.getDatabase(this)?.livraisonDao()
            estimationDao = ProgBandRoomDatabase.getDatabase(this)?.estimationDao()
            suiviApplicationDao = ProgBandRoomDatabase.getDatabase(this)?.suiviApplicationDao()
            enqueteSsrtDao = ProgBandRoomDatabase.getDatabase(this)?.enqueteSsrtDao()
            inspectionDao = ProgBandRoomDatabase.getDatabase(this)?.inspectionDao()
            infosProducteurDao = ProgBandRoomDatabase.getDatabase(this)?.infosProducteurDao()
            livraisonCentralDao = ProgBandRoomDatabase.getDatabase(this)?.livraisonCentralDao()

            postplantingDao = ProgBandRoomDatabase.getDatabase(this)?.postplantingDao()
            postPlantingArbrDistribDao = ProgBandRoomDatabase.getDatabase(this)?.postPlantingArbrDistribDao()

            if (intent != null) {
                syncLocalite(localiteDao!!)
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun recallServiceIntent() {
        //if(NetworkUtils.isAvailable() && NetworkUtils.isConnected()) ;
    }

}
