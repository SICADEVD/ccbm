package ci.projccb.mobile.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ci.projccb.mobile.adapters.MultipleItemAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.adapters.OnlyFieldAdapter
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.ArbreData
import ci.projccb.mobile.repositories.datas.InsectesParasitesData
import ci.projccb.mobile.repositories.datas.PesticidesAnneDerniereModel
import ci.projccb.mobile.repositories.datas.PesticidesApplicationModel
import ci.projccb.mobile.repositories.datas.PresenceAutreInsecteData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.returnStringList
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.ListConverters
import ci.projccb.mobile.tools.SendErrorOnline
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_ssrt_clms.selectEndroitTrav2EffectSSrte
import kotlinx.android.synthetic.main.activity_ssrt_clms.selectEndroitTravEffectSSrte
import kotlinx.android.synthetic.main.activity_ssrt_clms.selectLequelTrav2EffectSSrte
import kotlinx.android.synthetic.main.activity_ssrt_clms.selectLequelTravEffectSSrte
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerAnimauxSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerAutreInsecteParOuRavSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerInsecteAmisSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerInsecteParOuRavSuiviParcelle
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerIntantAnDerListSuiviParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerPestListSuiviParcel
import kotlinx.android.synthetic.main.activity_suivi_parcelle.recyclerTraitInsecteParOuRavListSuiviParcel
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
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
        LogUtils.json(producteurDatas)
        for (producteur in producteurDatas) {
            try {
                // deserialize datas producteurs
//                val culturesType = object : TypeToken<MutableList<CultureProducteurModel>>() {}.type
//                producteur.producteursCultures = GsonUtils.fromJson<MutableList<CultureProducteurModel>>(producteur.cultures, culturesType)
//                producteur.typeculture = mutableListOf()
//                producteur.superficieculture = mutableListOf()

                //reform producteur
                //producteur.localitesId

                producteur.dateNaiss = Commons.convertDate(producteur.dateNaiss, true)
                producteur.certificats = GsonUtils.fromJson(producteur.certificatsStr, object : TypeToken<MutableList<String>>(){}.type)

                if (!producteur.photo.isNullOrEmpty()) {
                    val photoPath = producteur.photo
                    producteur.photo = Commons.convertPathBase64(photoPath, 1)
                }
//                if (!producteur.rectoPath.isNullOrEmpty()) producteur.recto = Commons.convertPathBase64(producteur.rectoPath, 1)
//                if (!producteur.versoPath.isNullOrEmpty()) producteur.verso = Commons.convertPathBase64(producteur.versoPath, 1)
//                if (!producteur.esignaturePath.isNullOrEmpty()) producteur.esignature = Commons.convertPathBase64(producteur.esignaturePath, 3)

//                producteur.producteursCultures?.map { culture ->
//                    producteur.typeculture?.add(culture.label!!)
//                    producteur.superficieculture?.add(culture.superficie.toString())
//                }


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

                val clientProducteur: Call<ProducteurModel> = ApiClient.apiService.synchronisationProducteur(producteurModel = producteur)
                clientProducteur.enqueue(object: Callback<ProducteurModel>{
                    override fun onResponse(
                        call: Call<ProducteurModel>,
                        response: Response<ProducteurModel>
                    ) {
                        var producteurSynced = response.body()

                        if(response.code().toString().contains("422")){
                            //val buffer = Buffer()
                            val respText = response.errorBody()?.string().toString()
                            if(respText.contains("phone1", ignoreCase = true) && respText.contains("phone2", ignoreCase = true)){
//                                LogUtils.d(respText)
                                producteurDao.syncDataOnExist(
                                    synced = 1,
                                    localID = producteur.uid
                                )
                            }
                        }

                        producteurSynced?.let {
//                            LogUtils.d(producteurSynced?.id)
//                            LogUtils.d(response.code())

                            producteurDao.syncData(
                                id = producteurSynced?.id!!,
                                synced = true,
                                localID = producteur.uid
                            )

                            val producteurMenagesList = menageDao?.getMenagesUnSynchronizedLocal(
                                producteur.uid.toString(),
                                SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                            )!!

                            for (prodMenage in producteurMenagesList) {
                                prodMenage.producteurs_id = producteurSynced.id.toString()
                                menageDao?.insert(prodMenage)
                            }

                            val producteurParcellesList = parcelleDao?.getParcellesUnSynchronizedLocal(
                                producteur.uid.toString(),
                                SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                            )!!

                            for (parcelle in producteurParcellesList) {
                                parcelle.producteurId = producteurSynced.id.toString()
                                parcelleDao?.insert(parcelle)
                            }

//                            val livraisonsList = livraisonDao?.getUnSyncedAll(
//                                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
//                            )!!
//
//                            livraisonsList.map { livraisonModel ->
//                                livraisonModel.producteursId = producteurSynced.id.toString()
//                                livraisonDao?.insert(livraisonModel)
//                            }

                        }

                    }

                    override fun onFailure(call: Call<ProducteurModel>, t: Throwable) {
                        LogUtils.e(t.message)
                    }

                })
//                val responseProducteur: Response<ProducteurModel> = clientProducteur.execute()
//                val producteurSynced: ProducteurModel? = responseProducteur.body()
//
//                LogUtils.d("RESPONSE CODE "+responseProducteur.code())
//

//
//                producteurSynced?.let {
//
//                }

            } catch (uhex: UnknownHostException) {
                LogUtils.e(uhex.message)
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        syncMenage(menageDao!!)
    }


    fun syncMenage(menageDao: ProducteurMenageDao) {
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
                    activiteFemme = "non"
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

        syncParcelle(parcelleDao!!)
    }


    fun syncParcelle(parcelleDao: ParcelleDao) {
        val parcelleDatas = parcelleDao.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        val parcelleWayPointsMappedToken = object : TypeToken<MutableList<String>>() {}.type

        for (parcelle in parcelleDatas) {
            try {
                parcelle?.apply {
                    codeParc = null
                    id = 0
                    localiteNom = null
                    nom = null
                    perimeter = null
                    prenoms = null
                    producteurNom = null
                }

                if (!parcelle.wayPointsString.isNullOrEmpty()) parcelle.mappingPoints = ApiClient.gson.fromJson(parcelle.wayPointsString, parcelleWayPointsMappedToken)

                parcelle.apply {
                    protectionList = returnStringList(protectionStr)?: arrayListOf()
                    arbreList = GsonUtils.fromJson<MutableList<ArbreData>>(arbreStr, object : TypeToken<List<ArbreData>>(){}.type)
                }

                //LogUtils.e(TAG, "syncParcelle ID before -> ${parcelle.id}")
                val clientParcelle: Call<ParcelleModel> = ApiClient.apiService.synchronisationParcelle(parcelle)

                val responseParcelle: Response<ParcelleModel> = clientParcelle.execute()
                val parcelleSync: ParcelleModel = responseParcelle.body()!!

                if(responseParcelle.code() == 200 || responseParcelle.code() == 201){
                    parcelleDao.syncData(
                        id = parcelleSync.id!!,
                        synced = true,
                        localID = parcelle.uid.toInt()
                    )
                }else if(responseParcelle.code() == 501){
                    parcelleDao.syncData(
                        id = parcelleSync.id!!,
                        synced = true,
                        localID = parcelle.uid.toInt()
                    )
                }

                val suiviParcellesList = suiviParcelleDao?.getSuiviParcellesUnSynchronizedLocal(
                    parcelleUid = parcelle.uid.toString(),
                    SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
                )!!

                for (suivi in suiviParcellesList) {
                    suivi.parcellesId = parcelleSync.id.toString()
                    suivi.producteursId = parcelleSync.producteurId
                    suiviParcelleDao?.insert(suivi)
                }

            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        syncSuivi(suiviParcelleDao!!)
    }


    fun syncSuivi(suiviParcelleDao: SuiviParcelleDao) {
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

//                    suivi.ombrages?.map { ombrage ->
//                        suivi.varietesOmbrage?.add(ombrage.variete!!)
//                        suivi.nombreOmbrage?.add(ombrage.nombre.toString())
//                    }
//
//                    arbres?.map { arbre ->
//                        suivi.agroForestiers?.add(arbre.variete!!)
//                        suivi.nombreArbresAgro?.add(arbre.nombre!!)
//                    }

                    suivi.dateVisite = Commons.convertDate(suivi.dateVisite, true)

                    val clientSuivi: Call<SuiviParcelleModel> = ApiClient.apiService.synchronisationSuivi(suivi)

                    clientSuivi.enqueue(object : Callback<SuiviParcelleModel>{
                        override fun onResponse(
                            call: Call<SuiviParcelleModel>,
                            response: Response<SuiviParcelleModel>
                        ) {
                            if(response.isSuccessful){

                                val suiviSynced: SuiviParcelleModel? = response.body()

                                suiviParcelleDao.syncData(
                                    id = suiviSynced?.id!!,
                                    synced = true,
                                    localID = suivi.uid
                                )
                            }else{
//                                suiviParcelleDao.deleteByUid(
//                                    suivi.uid
//                                )
                            }
                        }

                        override fun onFailure(call: Call<SuiviParcelleModel>, t: Throwable) {
                            LogUtils.e(t.message)
                        }

                    })

                } catch (uhex: UnknownHostException) {
                    FirebaseCrashlytics.getInstance().recordException(uhex)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
            syncVisiteurFormation(visiteurFormationDao!!)
        } else {
            syncVisiteurFormation(visiteurFormationDao!!)
        }
    }

    fun syncVisiteurFormation(visiteurFormationDao: VisiteurFormationDao) {
        val suiviDatas = visiteurFormationDao.getUnSyncedAll(
            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)
        )
        LogUtils.json(suiviDatas)
        if (suiviDatas.size > 0) {
            for (suivi in suiviDatas) {
                try {
                    // deserialize datas producteurs

                    val clientSuivi: Call<VisiteurFormationModel> = ApiClient.apiService.synchronisationVisiteurFormation(suivi)

                    val responseSuivi: Response<VisiteurFormationModel> = clientSuivi.execute()
                    val visiteurFormationModel: VisiteurFormationModel? = responseSuivi.body()



                    visiteurFormationDao.syncData(
                        id = visiteurFormationModel?.id!!,
                        synced = true,
                        localID = suivi.uid
                    )
                } catch (uhex: UnknownHostException) {
                    FirebaseCrashlytics.getInstance().recordException(uhex)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
            syncEvaluationBesoin(evaluationArbreDao!!)
        } else {
            syncEvaluationBesoin(evaluationArbreDao!!)
        }
    }

    fun syncEvaluationBesoin(evaluationArbreDao: EvaluationArbreDao) {
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

                    clientSuivi.enqueue(object : Callback<EvaluationArbreModel>{
                        override fun onResponse(
                            call: Call<EvaluationArbreModel>,
                            response: Response<EvaluationArbreModel>
                        ) {
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
                        }

                        override fun onFailure(call: Call<EvaluationArbreModel>, t: Throwable) {
                            LogUtils.e(t.message)
                        }

                    })
                } catch (uhex: UnknownHostException) {
                    FirebaseCrashlytics.getInstance().recordException(uhex)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
            syncFormations(formationDao!!)
        } else {
            syncFormations(formationDao!!)
        }
    }


    fun syncFormations(formationDao: FormationDao) {
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
                }

                val clientFormation: Call<FormationModel> = ApiClient.apiService.synchronisationFormation(formationModel = formation)

                clientFormation.enqueue(object : Callback<FormationModel>{
                    override fun onResponse(
                        call: Call<FormationModel>,
                        response: Response<FormationModel>
                    ) {
                        if(response.isSuccessful){
                            val formationSynced: FormationModel? = response.body()

                            formationDao.syncData(
                                formationSynced?.id!!,
                                true,
                                formation.uid
                            )
                        }else{
                            formationDao.deleteByUid(
                                formation.uid
                            )
                        }
                    }

                    override fun onFailure(call: Call<FormationModel>, t: Throwable) {
                        LogUtils.e(t.message)
                    }

                })


            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        syncLivraison(livraisonDao!!)
    }


    fun syncLivraison(livraisonDao: LivraisonDao) {
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

        syncEstimation(estimationDao!!)
    }


    fun syncEstimation(estimationDao: EstimationDao) {
        try {
            val estimationDatas = estimationDao.getUnSyncedAll()

            estimationDatas.map { estimationPojo ->
                estimationPojo.dateEstimation = Commons.convertDate(estimationPojo.dateEstimation, toEng = true)

                val clientEstimation: Call<EstimationModel> = ApiClient.apiService.synchronisationEstimation(estimationPojo)
                val responseEstimation: Response<EstimationModel> = clientEstimation.execute()

                val estimationSynced: EstimationModel? = responseEstimation.body()
                estimationDao.syncData(
                    estimationSynced?.id!!,
                    true,
                    estimationPojo.uid!!
                )
            }

            syncSuiviApplication(suiviApplicationDao!!)
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }


    fun syncSuiviApplication(suiviApplicationDao: SuiviApplicationDao) {
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
                }

                val clientSuiviApplication: Call<SuiviApplicationModel> = ApiClient.apiService.synchronisationSuiviApplication(suiviApplication)
                clientSuiviApplication.enqueue(object : Callback<SuiviApplicationModel>{
                    override fun onResponse(
                        call: Call<SuiviApplicationModel>,
                        response: Response<SuiviApplicationModel>
                    ) {
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
                    }

                    override fun onFailure(call: Call<SuiviApplicationModel>, t: Throwable) {
                        LogUtils.e(t.message)
                    }

                })


            }

            syncDistributionDarbre(distributionArbreDao!!)
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }


    fun syncDistributionDarbre(distributionArbreDao: DistributionArbreDao) {
        try {
            val distribArbrDatas = distributionArbreDao.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            LogUtils.d("DISTRIBUTION ARBRE : ", distribArbrDatas)

            distribArbrDatas.map {distrib ->

                GsonUtils.fromJson<Map<String, Map<String, String>>>(distrib.quantiteStr, object : TypeToken<Map<String, Map<String, String>>>(){}.type)?.let {
                    distrib.quantiteList = it
                }

                val clientRequ: Call<DistributionArbreModel> = ApiClient.apiService.synchronisationDistributionArbre(distrib)
                clientRequ.enqueue(object : Callback<DistributionArbreModel>{
                    override fun onResponse(
                        call: Call<DistributionArbreModel>,
                        response: Response<DistributionArbreModel>
                    ) {
                        if(response.isSuccessful){

                            val responseItem = response.body()

                            distributionArbreDao.syncData(
                                responseItem?.id!!,
                                true,
                                distrib.uid
                            )

                            evaluationArbreDao?.deleteByProducteurId(distrib.producteurId)

                        }else{

                            distributionArbreDao.deleteByUid(
                                distrib.uid
                            )

                        }
                    }

                    override fun onFailure(call: Call<DistributionArbreModel>, t: Throwable) {
                        distributionArbreDao.deleteByUid(
                            distrib.uid
                        )
                        LogUtils.e(t.message)
                    }

                })


            }

            syncLivraisonMagCentral(livraisonCentralDao!!)
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }


    fun syncLivraisonMagCentral(livraisonCentralDao: LivraisonCentralDao) {
        try {
            val livraisonCentralDatas = livraisonCentralDao.getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
            LogUtils.d("livraisonVerMagCentralDao : ", livraisonCentralDatas)

            livraisonCentralDatas.map {

                val livraisonList = GsonUtils.fromJson<MutableList<LivraisonCentralSousModel>>(it.itemsStringify, object : TypeToken<MutableList<LivraisonCentralSousModel>>(){}.type)

                it.apply {
                    producteur_idList = livraisonList.map { "${it.producteur_id}" }.toMutableList()
                    producteursList = livraisonList.map { "${it.producteurs}" }.toMutableList()
                    parcelleList = livraisonList.map { "${it.parcelle}" }.toMutableList()
                    quantiteList = livraisonList.map { "${it.quantite}" }.toMutableList()
                    certificatList  = livraisonList.map { "${it.certificat}" }.toMutableList()
                    typeproduitList = livraisonList.map { "${it.typeproduit}" }.toMutableList()
                    typeList = GsonUtils.fromJson<MutableList<String>>(typeStr, object : TypeToken<MutableList<String>>(){}.type)

                    poidsnet = livraisonList.sumBy { it?.quantite?.toInt()?:0 }.toString()

                    estimatDate = Commons.convertDate(estimatDate, toEng = true)
                }

                val clientRequ: Call<LivraisonCentralModel> = ApiClient.apiService.synchronisationLivraisonCentral(it)
                clientRequ.enqueue(object : Callback<LivraisonCentralModel>{
                    override fun onResponse(
                        call: Call<LivraisonCentralModel>,
                        response: Response<LivraisonCentralModel>
                    ) {
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
                    }

                    override fun onFailure(call: Call<LivraisonCentralModel>, t: Throwable) {
                        LogUtils.e(t.message)
                    }

                })


            }

            syncEnqueteSsrt(enqueteSsrtDao!!)
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }


    fun syncEnqueteSsrt(enqueteSsrteDao: EnqueteSsrteDao) {
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
                LogUtils.json(enquete)

                val clientEnqueteSsrt: Call<EnqueteSsrtModel> = ApiClient.apiService.synchronisationEnqueteSsrt(enquete)
                val responseEnqueteSsrt: Response<EnqueteSsrtModel> = clientEnqueteSsrt.execute()

                val enqueteSsrtSynced: EnqueteSsrtModel? = responseEnqueteSsrt.body()
                enqueteSsrteDao.syncData(
                    enqueteSsrtSynced?.id!!,
                    true,
                    enquete.uid
                )
            }

            syncInspection(inspectionDao!!)
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    fun syncInspection(inspectionDao: InspectionDao) {
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
                        inspection.reponse[counter.toString()] = it.note!!
                        counter++
                        //note += it.note!!.toInt()
                    }
                }
//
//                inspection.noteInspection = note.toString()

                inspection.reponseStringify = null

                val clientInspection: Call<InspectionDTO> = ApiClient.apiService.synchronisationInspection(inspection)
                val responseInspection: Response<InspectionDTO> = clientInspection.execute()

                val inspectionSynced: InspectionDTO? = responseInspection.body()
                inspectionDao.syncData(
                    inspectionSynced?.id!!,
                    true,
                    inspection.uid
                )
            }

            syncInfosProducteur(infosProducteurDao!!)
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }


    }


    fun syncInfosProducteur(infosProducteurDao: InfosProducteurDao) {
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

                val clientInfos: Call<InfosProducteurDTO> = ApiClient.apiService.synchronisationInfosProducteur(info)
                try {
                    val responseInfos: Response<InfosProducteurDTO> = clientInfos.execute()
                    LogUtils.d("RESPONSE CODE "+responseInfos.code())
                    if(responseInfos.raw().message.contains("info existe", ignoreCase = true)){
                        infosProducteurDao.deleteProducteurInfo(
                            info.uid
                        )
                    }
                }catch (ex: Exception) {
                    infosProducteurDao.deleteProducteurInfo(
                        info.uid
                    )
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
//
//
//                if(responseInfos.code() == 200){
//                    val infoSynced: InfosProducteurDTO? = responseInfos.body()
//                    infosProducteurDao.syncData(
//                        infoSynced?.id!!,
//                        true,
//                        info.uid
//                    )
//                }
            }

            if (Build.VERSION.SDK_INT >= 26) {
                stopForeground(true)
                notificationManager?.cancel(1)
            }
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }


    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                val CHANNEL_ID = "field_connect"
                val channel = NotificationChannel(CHANNEL_ID, "CCB", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                notificationManager?.createNotificationChannel(channel)

                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("")
                    .build()
                startForeground(1, notification)
            }

            campagneDao = CcbRoomDatabase.getDatabase(this)?.campagneDao()
            localiteDao = CcbRoomDatabase.getDatabase(this)?.localiteDoa()
            producteurDao = CcbRoomDatabase.getDatabase(this)?.producteurDoa()
            parcelleDao = CcbRoomDatabase.getDatabase(this)?.parcelleDao()
            menageDao = CcbRoomDatabase.getDatabase(this)?.producteurMenageDoa()
            formationDao = CcbRoomDatabase.getDatabase(this)?.formationDao()
            evaluationArbreDao = CcbRoomDatabase.getDatabase(this)?.evaluationArbreDao()
            visiteurFormationDao = CcbRoomDatabase.getDatabase(this)?.visiteurFormationDao()
            distributionArbreDao = CcbRoomDatabase.getDatabase(this)?.distributionArbreDao()
            suiviParcelleDao = CcbRoomDatabase.getDatabase(this)?.suiviParcelleDao()
            livraisonDao = CcbRoomDatabase.getDatabase(this)?.livraisonDao()
            estimationDao = CcbRoomDatabase.getDatabase(this)?.estimationDao()
            suiviApplicationDao = CcbRoomDatabase.getDatabase(this)?.suiviApplicationDao()
            enqueteSsrtDao = CcbRoomDatabase.getDatabase(this)?.enqueteSsrtDao()
            inspectionDao = CcbRoomDatabase.getDatabase(this)?.inspectionDao()
            infosProducteurDao = CcbRoomDatabase.getDatabase(this)?.infosProducteurDao()
            livraisonCentralDao = CcbRoomDatabase.getDatabase(this)?.livraisonCentralDao()

            if (intent != null) {
                syncLocalite(localiteDao!!)
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }
}
