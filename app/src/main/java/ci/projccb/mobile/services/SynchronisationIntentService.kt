package ci.projccb.mobile.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.returnStringList
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.ListConverters
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.LogUtils
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


    var inspectionDao: InspectionDao? = null
    var infosProducteurDao: InfosProducteurDao? = null
    var localiteDao: LocaliteDao? = null
    var producteurDao: ProducteurDao? = null
    var parcelleDao: ParcelleDao? = null
    var livraisonDao: LivraisonDao? = null
    var menageDao: ProducteurMenageDao? = null
    var formationDao: FormationDao? = null
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

                if (!producteur.photo.isNullOrEmpty()) producteur.photo = Commons.convertPathBase64(producteur.photo, 1)
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
                    localite = null
                    mobileMoney = null
                    paperGuards = null
                    photo = null
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
                val responseProducteur: Response<ProducteurModel> = clientProducteur.execute()
                val producteurSynced: ProducteurModel? = responseProducteur.body()

                if(responseProducteur.isSuccessful){

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

                    val livraisonsList = livraisonDao?.getUnSyncedAll(
                        agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                    )!!

                    livraisonsList.map { livraisonModel ->
                        livraisonModel.producteursId = producteurSynced.id.toString()
                        livraisonDao?.insert(livraisonModel)
                    }

                    val formationsList = formationDao?.getUnSyncedAll(
                        agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
                    )!!

                    for (formation in formationsList) {
                        try {
                            // deserialize datas producteurs
                            val producteursType = object : TypeToken<MutableList<String>>() {}.type
                            formation.producteursId = GsonUtils.fromJson<MutableList<String>>(formation.producteursIdStringify, producteursType)
                            val cleanList = formation.producteursId?.toMutableList()

                            var positionLoop = 0
                            var positionFound: Int

                            formation.producteursId?.map {
                                val producteurId = it.split("-")[0]
                                val typeId = it.split("-")[1]

                                if (typeId == "uid") {
                                    if (producteurId.toInt() == producteurSynced.uid) {
                                        positionFound = positionLoop
                                        cleanList?.removeAt(positionFound)
                                        cleanList?.add("${producteurSynced.id}-id")
                                    }
                                }

                                positionLoop += 1
                            }

                            formation.producteursId = mutableListOf()
                            formation.producteursId = cleanList
                            formation.producteursIdStringify = GsonUtils.toJson(cleanList)

                            formationDao?.insert(formation)
                        } catch (uhex: UnknownHostException) {
                            FirebaseCrashlytics.getInstance().recordException(uhex)
                        } catch (ex: Exception) {
                            LogUtils.e(ex.message)
                            FirebaseCrashlytics.getInstance().recordException(ex)
                        }
                    }

                }

            } catch (uhex: UnknownHostException) {
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
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
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
                if (!parcelle.wayPointsString.isNullOrEmpty()) parcelle.mappingPoints = ApiClient.gson.fromJson(parcelle.wayPointsString, parcelleWayPointsMappedToken)

                //LogUtils.e(TAG, "syncParcelle ID before -> ${parcelle.id}")
                val clientParcelle: Call<ParcelleModel> = ApiClient.apiService.synchronisationParcelle(parcelle)

                val responseParcelle: Response<ParcelleModel> = clientParcelle.execute()
                val parcelleSync: ParcelleModel = responseParcelle.body()!!

                parcelleDao.syncData(
                    id = parcelleSync.id!!,
                    synced = true,
                    localID = parcelle.uid.toInt()
                )

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
                    val ombragesType = object : TypeToken<MutableList<OmbrageVarieteModel>>() {}.type
                    suivi.ombrages = GsonUtils.fromJson<MutableList<OmbrageVarieteModel>>(suivi.varieteOmbragesTemp, ombragesType)

                    val arbreType = object : TypeToken<MutableList<OmbrageVarieteModel>>() {}.type
                    val arbres = GsonUtils.fromJson<MutableList<OmbrageVarieteModel>>(suivi.arbreAgroForestierStringify, arbreType)


                    val animauxType = object : TypeToken<MutableList<String>>() {}.type
                    suivi.animauxRencontres = GsonUtils.fromJson<MutableList<String>>(suivi.animauxRencontresStringify, animauxType)

                    suivi.varietesOmbrage = mutableListOf()
                    suivi.nombreOmbrage = mutableListOf()

                    suivi.nombreArbresAgro = mutableListOf()
                    suivi.agroForestiers = mutableListOf()

                    suivi.ombrages?.map { ombrage ->
                        suivi.varietesOmbrage?.add(ombrage.variete!!)
                        suivi.nombreOmbrage?.add(ombrage.nombre.toString())
                    }

                    arbres?.map { arbre ->
                        suivi.agroForestiers?.add(arbre.variete!!)
                        suivi.nombreArbresAgro?.add(arbre.nombre!!)
                    }

                    suivi.dateVisite = Commons.convertDate(suivi.dateVisite, true)

                    val clientSuivi: Call<SuiviParcelleModel> = ApiClient.apiService.synchronisationSuivi(suivi)

                    val responseSuivi: Response<SuiviParcelleModel> = clientSuivi.execute()
                    val suiviSynced: SuiviParcelleModel? = responseSuivi.body()



                    suiviParcelleDao.syncData(
                        id = suiviSynced?.id!!,
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
                val producteursType = object : TypeToken<MutableList<String>>() {}.type
                formation.producteursId = GsonUtils.fromJson<MutableList<String>>(formation.producteursIdStringify, producteursType)
                val cleanList = formation.producteursId?.toMutableList()

                formation.dateFormation = Commons.convertDate(formation.dateFormation, true)
                formation.themeIds = ListConverters.stringToMutableList(formation.themeStringify)

                var positionLoop = 0
                var positionFound = 0

                formation.producteursId?.map {
                    val producteurId = it.split("-")[0]
                    val typeId = it.split("-")[1]

                    if (typeId == "uid") {
                        val producteurCheck = producteurDao?.getProducteurByUID(producteurUID = producteurId.toInt())

                        if (producteurCheck?.isSynced!!) {
                            positionFound = positionLoop
                            cleanList?.removeAt(positionFound)
                            cleanList?.add("${producteurCheck.id}-id")
                        } else {
                            suncFormationsFlag = false
                        }
                    }

                    positionLoop += 1
                }

                formation.producteursId = mutableListOf()
                formation.producteursId = cleanList
            } catch (uhex: UnknownHostException) {
                FirebaseCrashlytics.getInstance().recordException(uhex)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        if (suncFormationsFlag) { // all is done bring synchronization
            formationDatas.map { formationModel ->
                // deserialize datas producteurs
                val producteursType = object : TypeToken<MutableList<String>>() {}.type
                formationModel.producteursId = GsonUtils.fromJson<MutableList<String>>(formationModel.producteursIdStringify, producteursType)
                formationModel.photoFormation = Commons.convertPathBase64(formationModel.photoPath, 0)
                //LogUtils.e(TAG, GsonUtils.toJson(formationModel.producteurs))

                formationModel.visiteursNom = mutableListOf<String>()
                formationModel.visiteurs?.split(",")?.map {
                    formationModel.visiteursNom?.add(it.toString())
                }

                val listM = formationModel.producteursId?.map {
                    it.replace("-id", "")
                }

                formationModel.producteursId = listM?.toMutableList()

                val clientFormation: Call<FormationModel> = ApiClient.apiService.synchronisationFormation(formationModel = formationModel)

                val responseFormation: Response<FormationModel> = clientFormation.execute()
                val formationSynced: FormationModel? = responseFormation.body()

                formationDao.syncData(
                    formationSynced?.id!!,
                    true,
                    formationModel.uid
                )
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

            val livraisonSModList:ArrayList<LivraisonSousModel> = arrayListOf()
            val producteursId = ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelProdNamesStringify)
            val parcelles = ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelParcellesStringify)
            val parcellesId = ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelParcelleIdsStringify)
            val quantites = ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelQuantitysStringify)
            val types = ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelTypesStringify)
            val amounts = ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelAmountsStringify)
            val scelles = ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelScellesStringify)
            var counter = 0
            ListConverters.stringToMutableList(livraisonPojo.livraisonSousModelProdNamesStringify)?.forEach {
                livraisonSModList.add(
                    LivraisonSousModel(
                        producteurId = producteursId!![counter],
                        producteurIdName = it,
                        parcelleIdName = parcelles!![counter],
                        parcelleId = parcellesId!![counter],
                        quantityNb = quantites!![counter].toInt(),
                        amountNb = amounts!![counter].toInt(),
                        typeName = types!![counter]
                        //nsumScelle = scelles!![counter],
                    ).apply {
                        scelleList = mutableListOf<String>()
                        scelleList!!.add(numScelle.toString())
                    }
                )
                counter++
            }

            val listLivrSModJson = ApiClient.gson.toJson(livraisonSModList)
            livraisonPojo.apply {
                itemList = livraisonSModList
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

            val clientLivraison: Call<LivraisonModel> = ApiClient.apiService.synchronisationLivraison(livraisonModel = livraisonPojo)

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

                if (!suiviApplication.matieresActivesStringify.isNullOrEmpty()) suiviApplication.matieresActives = ListConverters.stringToMutableList(suiviApplication.matieresActivesStringify)

                if (!suiviApplication.nomInsectesCiblesStringify.isNullOrEmpty()) {
                    val insecteType = object : TypeToken<MutableList<InsecteRavageurModel>>() {}.type
                    val insectesRavs: MutableList<InsecteRavageurModel> = ApiClient.gson.fromJson(suiviApplication.nomInsectesCiblesStringify, insecteType)

                    suiviApplication.nomInsectesCibles = mutableListOf()

                    insectesRavs.map { insecte ->
                        suiviApplication.nomInsectesCibles?.add(insecte.nom!!)
                    }
                }

                if (!suiviApplication.photoDouchePath.isNullOrEmpty()) suiviApplication.photoDouche = Commons.convertPathBase64(suiviApplication.photoDouchePath, 0)
                if (!suiviApplication.photoTamponPath.isNullOrEmpty()) suiviApplication.photoZoneTampons = Commons.convertPathBase64(suiviApplication.photoTamponPath, 0)

                suiviApplication.apply {
                    parcellesIds = parcellesId?.toInt()
                    applicateursIds = applicateursId?.toInt()
                    parcellesId = null
                    applicateursId = null
                }

                val clientSuiviApplication: Call<SuiviApplicationModel> = ApiClient.apiService.synchronisationSuiviApplication(suiviApplication)
                val responseSuiviApplication: Response<SuiviApplicationModel> = clientSuiviApplication.execute()

                val suiviApplicationSynced: SuiviApplicationModel? = responseSuiviApplication.body()
                suiviApplicationDao.syncData(
                    suiviApplicationSynced?.id!!,
                    true,
                    suiviApplication.uid
                )
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

                enquete.raisonArretEcole = ListConverters.stringToMutableList(enquete.raisonArretEcoleStringify)

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
                inspection.reponse = mutableMapOf()

                var counter = 1;
                var note = 0;
                ApiClient.gson.fromJson<MutableList<QuestionResponseModel>>(inspection.reponseStringify, inspectionsToken).map {
                    if(it.isTitle == false) {
                        inspection.reponse[counter.toString()] = it.note!!
                        counter++
                        note += it.note!!.toInt()
                    }
                }

                inspection.noteInspection = note.toString()

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
                val responseInfos: Response<InfosProducteurDTO> = clientInfos.execute()

                val infoSynced: InfosProducteurDTO? = responseInfos.body()
                infosProducteurDao.syncData(
                    infoSynced?.id!!,
                    true,
                    info.uid
                )
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

            localiteDao = CcbRoomDatabase.getDatabase(this)?.localiteDoa()
            producteurDao = CcbRoomDatabase.getDatabase(this)?.producteurDoa()
            parcelleDao = CcbRoomDatabase.getDatabase(this)?.parcelleDao()
            menageDao = CcbRoomDatabase.getDatabase(this)?.producteurMenageDoa()
            formationDao = CcbRoomDatabase.getDatabase(this)?.formationDao()
            suiviParcelleDao = CcbRoomDatabase.getDatabase(this)?.suiviParcelleDao()
            livraisonDao = CcbRoomDatabase.getDatabase(this)?.livraisonDao()
            estimationDao = CcbRoomDatabase.getDatabase(this)?.estimationDao()
            suiviApplicationDao = CcbRoomDatabase.getDatabase(this)?.suiviApplicationDao()
            enqueteSsrtDao = CcbRoomDatabase.getDatabase(this)?.enqueteSsrtDao()
            inspectionDao = CcbRoomDatabase.getDatabase(this)?.inspectionDao()
            infosProducteurDao = CcbRoomDatabase.getDatabase(this)?.infosProducteurDao()

            if (intent != null) {
                syncLocalite(localiteDao!!)
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }
}
