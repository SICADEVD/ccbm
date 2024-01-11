package ci.projccb.mobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ci.projccb.mobile.R
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.*
import ci.projccb.mobile.repositories.datas.ArbreData
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.activity_parcelle.*
import kotlinx.android.synthetic.main.activity_producteur.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception


/**
 * Created by didierboka.developer on 18/12/2021
 * mail for work:   (didierboka.developer@gmail.com)
 */


@SuppressWarnings("ALL")
class ConfigurationActivity : AppCompatActivity() {


    var database: CcbRoomDatabase? = null
    var coursEauxDao: CourEauDao? = null
    var paiementMobileDao: PaiementMobileDao? = null
    var lienParenteDao: LienParenteDao? = null
    var moyenTransportDao: MoyenTransportDao? = null
    var typeDocuDocumentDao: TypeDocumentDao? = null
    var typeFormationDao: TypeFormationDao? = null
    var themeFormationDao: ThemeFormationDao? = null
    var sousThemeFormationDao: SousThemeFormationDao? = null
    var parcelleDao: ParcelleDao? = null
    var intrantDao: IntrantDao? = null
    var campagneDao: CampagneDao? = null
    var applicateurDao: ApplicateurDao? = null
    var questionnaireDao: QuestionnaireDao? = null
    var notationDao: NotationDao? = null
    var magasinDao: MagasinCentralDao? = null
    var magasinSectionDao: MagasinDao? = null
    var menageDao: ProducteurMenageDao? = null
    var livraisonDao: LivraisonDao? = null
    var formationDao: FormationDao? = null
    var suiviParcelleDao: SuiviParcelleDao? = null
    var producteurDao: ProducteurDao? = null
    var agentDoa: AgentDao? = null
    var eauUseeDao: EauUseeDao? = null
    var typeProduitDao: TypeProduitDao? = null
    var gardeMachineDao: GardeMachineDao? = null
    var localiteDao: LocaliteDao? = null
    var lieuFormationDao: LieuFormationDao? = null
    var nationaliteDao: NationaliteDao? = null
    var niveauDao: NiveauDao? = null
    var ordureMenagereDao: OrdureMenagereDao? = null
    var sourceEauDao: SourceEauDao? = null
    var sourceEnergieDao: SourceEnergieDao? = null
    var typeLocaliteDao: TypeLocaliteDao? = null
    var personneBlesseeDao: PersonneBlesseeDao? = null
    var draftedDatasDao: DraftedDatasDao? = null
    var typeMachineDao: TypeMachineDao? = null
    var typePieceDao: TypePieceDao? = null
    var varieteCacaoDao: VarieteCacaoDao? = null
    var recuDao: RecuDao? = null
    var delegueDao: DelegueDao? = null
    var concernesDao: ConcernesDao? = null
    var transporteurDao: TransporteurDao? = null
    var entrepriseDao: EntrepriseDao? = null
    var vehiculeDao: VehiculeDao? = null
    var remorqueDao: RemorqueDao? = null
    var livraisonVerMagCentralDao: LivraisonVerMagCentralDao? = null
    var staffFormationDao: StaffFormationDao? = null
    var programmesDao: ProgrammesDao? = null
    var sectionsDao: SectionsDao? = null
    var agentModel: AgentModel? = null
    var arbreDao: ArbreDao? = null
    var approvisionnementDao: ApprovisionnementDao? = null
    var evaluationArbreDao: EvaluationArbreDao? = null
    var agentID: Int = 0
    var oneIssue = false


    companion object {
        const val TAG = "ConfigurationActivity.kt"
    }


    // region NEW CONFIGURATION LOGIC
    suspend fun configCompletedOrError(info: String? = "", hasOtherDatas: Boolean = true, hisSynchro: Boolean = false, hasError: Boolean = false) {
        MainScope().launch {
            if (hisSynchro) {
              if (hasError) {
                  labelIndicatorConfiguration.text = "$info !"
                  actionUpdate.visibility = View.VISIBLE
                  loaderConfiguration.visibility = View.GONE
              }
            } else {
                loaderConfiguration.visibility = View.VISIBLE
                actionUpdate.visibility = View.GONE

                if (hasOtherDatas) {
                    labelIndicatorConfiguration.text = "Mise à jour $info..."
                } else {
                    labelIndicatorConfiguration.text = "Mise à jour terminée !"
                    loaderConfiguration.visibility = View.INVISIBLE
                    ActivityUtils.startActivity(DashboardAgentActivity::class.java)

                    SPUtils.getInstance().put(Constants.AGENT_ID, agentModel?.id!!)
                    SPUtils.getInstance().put(Constants.HAS_USER_LOGGED, "yes")
                    SPUtils.getInstance().put(Constants.APP_FIRST_LAUNCH, "no")

                    finish()
                }
            }
        }
    }


    suspend fun configFlow() {
        // Check if device already used
        if (SPUtils.getInstance().getString(Constants.APP_FIRST_LAUNCH, "yes") == "yes") {
            getLocalites()
        } else {
            if (SPUtils.getInstance().getInt(Constants.AGENT_ID, 0) == agentID) {
                synchronizeLocalite()
            } else {
                getLocalites()
            }
        }
    }


    suspend fun getLocalites() {
        withContext(IO) {
            val localitesUpdate = async {
                localiteDao?.deleteAgentDatas(SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString())

                try {
                    val clientLocalite = ApiClient.apiService.getLocalites(CommonData(userid = agentID, table = "localites"))
                    val responseLocalites: Response<MutableList<LocaliteModel>> = clientLocalite.execute()
                    val localitesList: MutableList<LocaliteModel>? = responseLocalites.body()

                    localitesList?.map {
                        val localite = LocaliteModel(
                            id = it.id,
                            nom = it.nom,
                            sectionId = it.sectionId,
                            uid = 0,
                            isSynced = true,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            origin = "remote"
                        )

                        localiteDao?.insert(localite)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            localitesUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("localités")
                getProducteurs()
            }
        }
    }


    suspend fun getProducteurs() {
        withContext(IO) {
            val producteursUpdate = async {
                producteurDao?.deleteAgentDatas(SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString())

                try {
                    val clientProducteurs = ApiClient.apiService.getProducteurs(agent = AgentModel(userId = agentModel?.id))
                    val responseProducteurs: Response<MutableList<ProducteurModel>> = clientProducteurs.execute()
                    val produteursList: MutableList<ProducteurModel>? = responseProducteurs.body()

                    produteursList?.map {

                        val producteur = ProducteurModel(
                            id = it.id,
                            nom = it.nom,
                            prenoms = it.prenoms,
                            localitesId = it.localitesId,
                            codeProd = it.codeProd,
                            certification = it.certification,
                            statutCertification = it.statutCertification,
                            uid = 0,
                            isSynced = true,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            origin = "remote"
                        )

                        producteurDao?.insert(producteur)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            producteursUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("producteurs")
                getParcelles()
                //getStaffApplicateurs()
            }
        }
    }


    suspend fun getParcelles() {
        withContext(IO) {
            val parcellesUpdate = async {
                //parcelleDao?.deleteAgentDatas(SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString())

                try {
                    val clientParcelles = ApiClient.apiService.getParcelles()
                    val responseParcelles: Response<MutableList<ParcelleModel>> = clientParcelles.execute()
                    val parcellesList: MutableList<ParcelleModel>? = responseParcelles.body()

                    //LogUtils.json(parcellesList)
                    parcellesList?.map {
                        val parcelle = ParcelleModel(
                            id = it.id,
                            culture = it.culture,
                            producteurId = it.producteurId,
                            anneeCreation = it.anneeCreation,
                            superficie = it.superficie,
                            codeParc = it.codeParc,
                            nbCacaoParHectare = it.nbCacaoParHectare,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            isSynced = true,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            origin = "remote"
                        )

                        parcelleDao?.insert(parcelle)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            parcellesUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - parcelle, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("parcelle")
                //getCoursEaux()
                //getDelegues()
                getStaffApplicateurs()
            }
        }

    }


//    suspend fun getCoursEaux() {
//        withContext(IO) {
//            val courEauUpdate = async {
//                coursEauxDao?.deleteAll()
//
//                try {
//                    val clientCourEaux = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "cours_eaux")
//                    )
//                    val responseCourEau: Response<MutableList<CommonData>> = clientCourEaux.execute()
//                    val courEauList: MutableList<CommonData>? = responseCourEau.body()
//
//                    courEauList?.map {
//                        val courEau = CourEauModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        coursEauxDao?.insert(courEau)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            courEauUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Cours eaux")
//                getEauxUsees()
//            }
//        }
//    }


//    suspend fun getEauxUsees() {
//        withContext(IO) {
//            val eauUseeUpdate = async {
//                eauUseeDao?.deleteAll()
//
//                try {
//                    val clientEauUsee = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "eaux_usees"
//                        )
//                    )
//                    val responseEauUsee: Response<MutableList<CommonData>> = clientEauUsee.execute()
//                    val eauUseeList: MutableList<CommonData>? = responseEauUsee.body()
//
//                    eauUseeList?.map {
//                        val eauUsee = EauUseeModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID)
//                                .toString()
//                        )
//
//                        eauUseeDao?.insert(eauUsee)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            eauUseeUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Eaux usées")
//                getTypeProduits()
//            }
//
//        }
//    }


//    suspend fun getTypeProduits() {
//        withContext(IO) {
//            val typeProduitUpdate = async {
//                typeProduitDao?.deleteAll()
//
//                try {
//                    val clientTypeProduit = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "type_produits")
//                    )
//                    val responseTypeProduit: Response<MutableList<CommonData>> = clientTypeProduit.execute()
//                    val typeProduitList: MutableList<CommonData>? = responseTypeProduit.body()
//
//                    typeProduitList?.map {
//                        val typeProduit = TypeProduitModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        typeProduitDao?.insert(typeProduit)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            typeProduitUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Type de produits")
//                getGardesMachines()
//            }
//
//
//        }
//    }


//    suspend fun getGardesMachines() {
//        withContext(IO) {
//            val gardeMachineUpdate = async {
//                gardeMachineDao?.deleteAll()
//
//                try {
//                    val clientGardeMachine = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "garde_machines")
//                    )
//                    val responseGardeMachine: Response<MutableList<CommonData>> = clientGardeMachine.execute()
//                    val eauUseeList: MutableList<CommonData>? = responseGardeMachine.body()
//
//                    eauUseeList?.map {
//                        val gardeMachine = GardeMachineModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        gardeMachineDao?.insert(gardeMachine)
//                    }
//
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            gardeMachineUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Gardes machine")
//                getLieuxFormations()
//            }
//
//
//        }
//    }


//    suspend fun getLieuxFormations() {
//        withContext(IO) {
//            val dataUpdate = async {
//                lieuFormationDao?.deleteAll()
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "lieu_formations")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = LieuFormationModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        lieuFormationDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Lieux formations")
//                getNationalites()
//            }
//
//        }
//    }


//    suspend fun getNationalites() {
//        withContext(IO) {
//            val dataUpdate = async {
//                nationaliteDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "nationalites")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = NationaliteModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        nationaliteDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Nationalités")
//                getNiveaux()
//            }
//
//        }
//    }


    suspend fun getStaffApplicateurs() {
        withContext(IO) {
            val dataUpdate = async {
                concernesDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getStaff(table = CommonData(cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID),
                        role = "applicateur", is_different = false))
                    var responseData: Response<MutableList<ConcernesModel>> = clientData.execute()
                    val dataList: MutableList<ConcernesModel>? = responseData.body()

//                    clientData = ApiClient.apiService.getStaff(table = CommonData(cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID), role = "delegue"))
//                    responseData = clientData.execute()
//                    dataList?.addAll(responseData.body()?: arrayListOf())

                    dataList?.map {
                        val data = ConcernesModel(
                            id = it.id,
                            firstname = it.firstname,
                            lastname = it.lastname,
                            username = it.username,
                            email = it.email,
                            mobile = it.mobile,
                            adresse = it.adresse,
                            role = it.role,
                            cooperativesId = it.cooperativesId,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        concernesDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Applicateurs, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Applicateurs")
                //getStaffDelegue()
                getListeTransporteurs()
            }

        }
    }

    suspend fun getListeTransporteurs() {

        val listOfEntreprise = mutableListOf<EntrepriseModel>()

        withContext(IO) {
            val dataUpdate = async {
                transporteurDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getTransporteurList()
                    var responseData: Response<MutableList<TransporteurModel>> = clientData.execute()
                    val dataList: MutableList<TransporteurModel>? = responseData.body()

                    val entrepriseNameList = mutableListOf<String>()

                    dataList?.map {

                        if (!entrepriseNameList.contains(it.entreprise.toString())) {
                            entrepriseNameList.add(it.entreprise.toString())
                            val entreprise = EntrepriseModel(
                                id = it.entreprise_id,
                                nom = it.entreprise.toString(),
                                uid = 0,
                                agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                            )
                            listOfEntreprise.add(entreprise)
                        }

                        val data = TransporteurModel(
                            id = it.id,
                            entreprise_id = it.entreprise_id,
                            nom = it.nom,
                            prenoms = it.prenoms,
                            date_naiss = it.date_naiss,
                            phone1 = it.phone1,
                            num_piece = it.num_piece,
                            num_permis = it.num_permis,
                            cooperativesId = it.cooperativesId,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        transporteurDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Transporteurs, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Transporteurs")
                //getStaffDelegue()
                getEntrepriseFormation(listOfEntreprise)
                //getStaffFormation()
            }

        }
    }

    suspend fun getEntrepriseFormation(listOfEntreprise: MutableList<EntrepriseModel>) {

        withContext(IO) {
            val dataUpdate = async {
                entrepriseDao?.deleteAll()

                try {

                    listOfEntreprise?.map {
                        entrepriseDao?.insert(it)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Entreprises, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Entreprises")
                //getStaffDelegue()
                getStaffFormation()
            }

        }
    }

    suspend fun getStaffFormation() {
        withContext(IO) {
            val dataUpdate = async {
                staffFormationDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getStaff(table = CommonData(cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID),
                        role = "manager", is_different = true))
                    var responseData: Response<MutableList<ConcernesModel>> = clientData.execute()
                    val dataList: MutableList<ConcernesModel>? = responseData.body()

//                    clientData = ApiClient.apiService.getStaff(table = CommonData(cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID), role = "delegue"))
//                    responseData = clientData.execute()
//                    dataList?.addAll(responseData.body()?: arrayListOf())

                    dataList?.map {
                        val data = StaffFormationModel(
                            id = it.id,
                            firstname = it.firstname,
                            lastname = it.lastname,
                            username = it.username,
                            email = it.email,
                            mobile = it.mobile,
                            adresse = it.adresse,
                            role = it.role,
                            cooperativesId = it.cooperativesId,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        staffFormationDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Staff de la formation, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Staff de la formation")
                getVehicule()
            }

        }
    }

    suspend fun getVehicule() {
        withContext(IO) {
            val dataUpdate = async {
                vehiculeDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getVehiculeList()
                    var responseData: Response<MutableList<VehiculeModel>> = clientData.execute()
                    val dataList: MutableList<VehiculeModel>? = responseData.body()

                    dataList?.map {
                        val data = VehiculeModel(
                            id = it.id,
                            marque_id = it.marque_id,
                            marque = it.marque,
                            vehicule_immat = it.vehicule_immat,
                            cooperativesId = it.cooperativesId,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        vehiculeDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Vehicules, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Vehicules")
                getRemorque()
            }

        }
    }

    suspend fun getRemorque() {
        withContext(IO) {
            val dataUpdate = async {
                remorqueDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getRemorqueList()
                    var responseData: Response<MutableList<RemorqueModel>> = clientData.execute()
                    val dataList: MutableList<RemorqueModel>? = responseData.body()

                    dataList?.map {
                        val data = RemorqueModel(
                            id = it.id,
                            remorque_immat = it.remorque_immat,
                            cooperativesId = it.cooperativesId,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        remorqueDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Remorques, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Remorques")
                getLivraisonVerMagCentral()
            }

        }
    }

    suspend fun getLivraisonVerMagCentral() {
        withContext(IO) {
            val dataUpdate = async {
                livraisonVerMagCentralDao?.deleteAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())

                try {
                    var clientData = ApiClient.apiService.getLivraisonVerMagCentralList()
                    var responseData: Response<MutableList<LivraisonVerMagCentralModel>> = clientData.execute()
                    val dataList: MutableList<LivraisonVerMagCentralModel>? = responseData.body()

                    dataList?.map { data ->
                        val data = LivraisonVerMagCentralModel(
                            id = data.id,
                            section = data.section,
                            magasinier = data.magasinier,
                            magasinSection = data.magasinSection,
                            codeMagasinSection = data.codeMagasinSection,
                            Delegue = data.Delegue,
                            typeProduit = data.typeProduit,
                            certificat = data.certificat,
                            quantiteMagasinSection = data.quantiteMagasinSection,
                            quantiteLivreMagCentral = data.quantiteLivreMagCentral,
                            nom = data.nom,
                            prenoms = data.prenoms,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            uid = 0
                        )

                        livraisonVerMagCentralDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Livraison vers magasin central, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Livraison vers magasin central")
                getStaffDelegue()
            }

        }
    }

    suspend fun getStaffDelegue() {
        withContext(IO) {
            val dataUpdate = async {
                delegueDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getStaff(table = CommonData(cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID), role = "delegue"))
                    var responseData: Response<MutableList<ConcernesModel>> = clientData.execute()
                    val dataList: MutableList<ConcernesModel>? = responseData.body()

//                    clientData = ApiClient.apiService.getStaff(table = CommonData(cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID), role = "delegue"))
//                    responseData = clientData.execute()
//                    dataList?.addAll(responseData.body()?: arrayListOf())

                    dataList?.map {
                        val data = DelegueModel(
                            id = it.id,
                            uid = 0,
                            nom = "${it.firstname} ${it.lastname}",
                            mobile = it.mobile,
                            adresse = it.adresse,
                            email = it.email,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        delegueDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Délégué, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Délégué")
                //getTypeThemeFormations()
                getListFormation()
            }

        }
    }

    suspend fun getListFormation() {
        withContext(IO) {
            val dataUpdate = async {
                formationDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getFormationByUser(table = CommonData(userid = SPUtils.getInstance().getInt(Constants.AGENT_ID)))
                    var responseData: Response<MutableList<FormationModel>> = clientData.execute()
                    val dataList: MutableList<FormationModel>? = responseData.body()

//                    clientData = ApiClient.apiService.getStaff(table = CommonData(cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID), role = "delegue"))
//                    responseData = clientData.execute()
//                    dataList?.addAll(responseData.body()?: arrayListOf())

                    dataList?.map {
                        val formModel = FormationModel(
                            id = it.id,
                            uid = 0,
                            localitesId = it.localitesId,
                            lieuFormation = it.lieuFormation,
                            formationType = it.formationType,
                            observationFormation = it.observationFormation,
                            agentId = it.agentId
                        )
                        formationDao?.insert(formModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Délégué, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Liste des formations")
                //getTypeThemeFormations()
                getSections()
            }

        }
    }

    suspend fun getSections() {
        withContext(IO) {
            val dataUpdate = async {
                sectionsDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getSections(CommonData(userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID) ))
                    var responseData: Response<MutableList<SectionModel>> = clientData.execute()
                    val dataList: MutableList<SectionModel>? = responseData.body()

                    dataList?.map {
                        val data = SectionModel(
                            id = it.id,
                            cooperativesId = it.cooperativesId,
                            libelle = it.libelle,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        sectionsDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Section, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Sections")
                getProgrammes()
            }

        }
    }

    suspend fun getProgrammes() {
        withContext(IO) {
            val dataUpdate = async {
                programmesDao?.deleteAll()

                try {
                    var clientData = ApiClient.apiService.getProgrammes()
                    var responseData: Response<MutableList<ProgrammeModel>> = clientData.execute()
                    val dataList: MutableList<ProgrammeModel>? = responseData.body()

                    dataList?.map {
                        val data = ProgrammeModel(
                            id = it.id,
                            libelle = "${it.libelle}",
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        programmesDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Programme, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Programmes")
                getTypeThemeFormations()
            }

        }
    }


//    suspend fun getNiveaux() {
//        withContext(IO) {
//            val dataUpdate = async {
//                niveauDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "niveaux")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = NiveauModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        niveauDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Niveaux")
//                getRecus()
//            }
//
//        }
//    }


//    suspend fun getRecus() {
//        withContext(IO) {
//            val dataUpdate = async {
//                recuDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "geres_recus")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = RecuModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        recuDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Recus")
//                getOrduresMenageres()
//            }
//        }
//    }


//    suspend fun getOrduresMenageres() {
//        withContext(IO) {
//            val dataUpdate = async {
//                ordureMenagereDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "ordures_menageres")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = OrdureMenagereModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        ordureMenagereDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Ordures menageres")
//                getSourcesEaux()
//            }
//
//
//        }
//    }


//    suspend fun getSourcesEaux() {
//        withContext(IO) {
//            val dataUpdate = async {
//                sourceEauDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "sources_eaux")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = SourceEauModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        sourceEauDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Sources eaux")
//                getSourcesEnergies()
//            }
//        }
//    }


//    suspend fun getSourcesEnergies() {
//        withContext(IO) {
//            val dataUpdate = async {
//                sourceEnergieDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "sources_energies")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = SourceEnergieModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        sourceEnergieDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//
//
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Sources energies")
//                getTypesLocalites()
//            }
//        }
//    }


//    suspend fun getTypesLocalites() {
//        withContext(IO) {
//            val dataUpdate = async {
//                typeLocaliteDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "type_localites")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = TypeLocaliteModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        typeLocaliteDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Types localité")
//                getTypesPieces()
//            }
//        }
//    }


//    suspend fun getTypesPieces() {
//        withContext(IO) {
//            val dataUpdate = async {
//                typePieceDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "type_pieces")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = TypePieceModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        typePieceDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Types pièce")
//                getTypesMachines()
//            }
//        }
//    }


//    suspend fun getTypesMachines() {
//        withContext(IO) {
//            val dataUpdate = async {
//                typeMachineDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "type_machines")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = TypeMachineModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        typeMachineDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Types machine")
//                getVarietesCacaos()
//            }
//        }
//    }


//    suspend fun getVarietesCacaos() {
//        withContext(IO) {
//            val dataUpdate = async {
//                varieteCacaoDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "varietes_cacao")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = VarieteCacaoModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        varieteCacaoDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Varietés cacao")
//                getPersonnesBlesses()
//            }
//
//
//        }
//    }


//    suspend fun getPersonnesBlesses() {
//        withContext(IO) {
//            val dataUpdate = async {
//                personneBlesseeDao?.deleteAll()
//
//                try {
//                    val clientData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "personne_blessee")
//                    )
//                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
//                    val dataList: MutableList<CommonData>? = responseData.body()
//
//                    dataList?.map {
//                        val data = PersonneBlesseeModel(
//                            id = it.id,
//                            nom = it.nom,
//                            uid = 0,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
//                        )
//
//                        personneBlesseeDao?.insert(data)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//
//            }
//
//            dataUpdate.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError(info = "Personnes blessées")
//                getDelegues()
//            }
//        }
//    }


    suspend fun getTypeThemeFormations() {
        withContext(IO) {
            val dataUpdate = async {
                //themeFormationDao?.deleteAll()
                typeFormationDao?.deleteAll()


                try {
                    val clientData = ApiClient.apiService.getTypeThemesFormations()
                    val responseData: Response<MutableList<TypeFormationModel>> = clientData.execute()

                    val datasList: MutableList<TypeFormationModel>? = responseData.body()

                    datasList?.map { typeFormation ->
                        val dataTypeFormationModel = TypeFormationModel(
                            uid = 0,
                            id = typeFormation.id,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            nom = typeFormation.nom
                        )

                        typeFormationDao?.insert(dataTypeFormationModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Type de formation")
                //getTypeIntrans()
                getCampagnes()
            }
        }
    }


   /*suspend fun getThemesFormation() {
        withContext(IO) {
            val dataUpdate = async {
                themeFormationDao?.deleteAll()

                val clientData = ApiClient.apiService.getDatasList(
                    table = CommonData(
                        userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
                        table = "themes_formations"
                    )
                )
                val responseData: Response<MutableList<CommonData>> = clientData.execute()
                val datasList: MutableList<CommonData>? = responseData.body()

                datasList?.map {
                    val data = FormationThemeModel(
                        id = it.id,
                        nom = it.nom,
                        uid = 0,
                        agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                        typeFormationId = it.typeFormationId.toString()
                    )

                    themeFormationDao?.insert(data)
                }

            }

            dataUpdate.join()
            configCompletedOrError("Themes")

            getTypeIntrans()
        }
    }*/


/*    suspend fun getTypeIntrans() {
        withContext(IO) {
            val dataUpdate = async {
                intrantDao?.deleteAll()

                try {
                    val clientData = ApiClient.apiService.getDatasList(
                        table = CommonData(
                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
                            table = "type_intrants"
                        )
                    )
                    val responseData: Response<MutableList<CommonData>> = clientData.execute()
                    val datasList: MutableList<CommonData>? = responseData.body()

                    datasList?.map {
                        val data = IntrantModel(
                            id = it.id,
                            nom = it.nom,
                            uid = 0,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        intrantDao?.insert(data)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            dataUpdate.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Intrants")
                getCampagnes()
            }
        }
    }*/


    suspend fun getCampagnes() {
        withContext(IO) {
            val campagneFetchDatas = async {
                campagneDao?.deleteAll()

                try {
                    val clientCampagneData = ApiClient.apiService.getCampagnes()
                    val responseCampagneData: Response<MutableList<CampagneModel>> = clientCampagneData.execute()
                    val datasCampagneList: MutableList<CampagneModel>? = responseCampagneData.body()

                    datasCampagneList?.map { model ->
                        model?.let {
                            if(it.campagnesNom!=null)
                            {
                                val dataCampagneModel = CampagneModel(
                                    campagnesNom = it.campagnesNom,
                                    id = it.id,
                                    uid = 0
                                )

                                campagneDao?.insert(dataCampagneModel)
                            }
                        }
                    }

                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            campagneFetchDatas.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Campagnes")
                getApplicateurs()
            }
        }
    }


    suspend fun getApplicateurs() {
        withContext(IO) {
            val applicateursFetchDatas = async {
                applicateurDao?.deleteAll()

                try {
                    val clientApplicateurData = ApiClient.apiService.getApplicateurs(CommonData(cooperativeIdex = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID)))
                    val responseApplicateurData: Response<MutableList<ApplicateurModel>> = clientApplicateurData.execute()
                    val datasCampagneList: MutableList<ApplicateurModel>? = responseApplicateurData.body()

                    datasCampagneList?.map {
                        val dataApplicateurModel = ApplicateurModel(
                            id = it.id,
                            uid = 0,
                            nom = it.nom,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                        )

                        applicateurDao?.insert(dataApplicateurModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            applicateursFetchDatas.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Applicateurs, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Applicateurs")
                getQuestionnaires()
                //getNotations()
            }
        }
    }


    suspend fun getQuestionnaires() {
        withContext(IO) {
            val questionnairesFetch = async {
                questionnaireDao?.deleteAll()


                try {
                    val clientQuestionnaireData = ApiClient.apiService.getQuestionnaires()
                    val responseQuestionnaireData: Response<MutableList<InspectionQuestionnairesModel>> = clientQuestionnaireData.execute()
                    val datasQuestionnaireList: MutableList<InspectionQuestionnairesModel>? = responseQuestionnaireData.body()

                    datasQuestionnaireList?.map {
                        val dataQuestionnaireModel = InspectionQuestionnairesModel(
                            questionnairesStringify = GsonUtils.toJson(it.questionnaires),
                            titre = it.titre,
                            uid = 0
                        )

                        questionnaireDao?.insert(dataQuestionnaireModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            questionnairesFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Questionnaires, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Questionnaires")
                getNotations()
            }
        }
    }


    suspend fun getNotations() {
        withContext(IO) {
            val notationsFetch = async {
                notationDao?.deleteAll()

                try {
                    val clientNotationData = ApiClient.apiService.getNotations()
                    val responseNotationData: Response<MutableList<NotationModel>> = clientNotationData.execute()
                    val datasNotationList: MutableList<NotationModel>? = responseNotationData.body()

                    datasNotationList?.map {
                        val dataNotationModel = NotationModel(
                            nom = it.nom,
                            point = it.point,
                            id = it.id,
                            uid = it.id!!
                        )

                        notationDao?.insert(dataNotationModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            notationsFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue - Notations, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Notations")
                getMagasinsCentral()
            }


        }
    }


    suspend fun getMagasinsCentral() {
        withContext(IO) {
            val magasinsFetch = async {
                magasinDao?.deleteAll()
                var magasinList = mutableListOf<MagasinModel>()

                try {
                    val clientMagasinData = ApiClient.apiService.getMagasinsCentraux(commonData = CommonData(userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)))
                    val responseMagasinData: Response<MutableList<MagasinModel>> = clientMagasinData.execute()
                    val datasMagasinList: MutableList<MagasinModel>? = responseMagasinData.body()

                    datasMagasinList?.map {
                        val dataMagasinModel = MagasinCentralModel(
                            codeMagasinsections = it.codeMagasinsections,
                            nomMagasinsections = it.nomMagasinsections,
                            staffId = it.staffId,
                            phone = it.phone,
                            status = it.status,
                            id = it.id,
                            uid = 0
                        )
                        magasinDao?.insert(dataMagasinModel)
                        //magasinDao?.insert(it)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }

            }

            magasinsFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Magasins centraux")
                getMagasinsSection()
            }
        }
    }

    suspend fun getMagasinsSection() {
        withContext(IO) {
            val magasinsFetch = async {
                magasinSectionDao?.deleteAll()
                var magasinList = mutableListOf<MagasinModel>()

                try {
                    val clientMagasinData = ApiClient.apiService.getMagasins(commonData = CommonData(userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0)))
                    val responseMagasinData: Response<MutableList<MagasinModel>> = clientMagasinData.execute()
                    val datasMagasinList: MutableList<MagasinModel>? = responseMagasinData.body()

                    datasMagasinList?.map {
                        magasinSectionDao?.insert(it)
                        //magasinDao?.insert(it)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            magasinsFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Magasins sections")
                getThemesFormationSelection()
            }
        }
    }


    suspend fun getThemesFormationSelection() {
        withContext(IO) {
            val themeFormationFetch = async {
                themeFormationDao?.deleteAll()


                try {
                    val clieThemeFormationData = ApiClient.apiService.getThemes()
                    val responseThemeFormationData: Response<MutableList<ThemeFormationModel>> = clieThemeFormationData.execute()
                    val datasThemeFormationList: MutableList<ThemeFormationModel>? = responseThemeFormationData.body()

                    datasThemeFormationList?.map {
                        val dataThemeFormationModel = ThemeFormationModel(
                            chapitre = it.chapitre,
                            id = it.id,
                            nom = it.nom,
                            uid = 0,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            typeFormationsId = it.typeFormationsId
                        )

                        themeFormationDao?.insert(dataThemeFormationModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            themeFormationFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("themes")
                getSousThemesFormationSelection()
            }


        }
    }

    suspend fun getSousThemesFormationSelection() {
        withContext(IO) {
            val themeFormationFetch = async {
                sousThemeFormationDao?.deleteAll()


                try {
                    val clientSousThemeFormationData = ApiClient.apiService.getSousThemes()
                    val responseThemeFormationData: Response<MutableList<SousThemeFormationModel>> = clientSousThemeFormationData.execute()
                    val datasSousThemeFormationList: MutableList<SousThemeFormationModel>? = responseThemeFormationData.body()

                    datasSousThemeFormationList?.map {
                        val dataSousThemeFormationModel = SousThemeFormationModel(
                            id = it.id,
                            nom = it.nom,
                            uid = 0,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            themeFormationsId = it.themeFormationsId
                        )

                        sousThemeFormationDao?.insert(dataSousThemeFormationModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            themeFormationFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("sous themes")
                getTypeFormationsSection()
            }


        }
    }


    suspend fun getTypeFormationsSection() {
        withContext(IO) {
            val magasinsFetch = async {
                typeFormationDao?.deleteAll()

                try {
                    val clientTypeFormationData = ApiClient.apiService.getDatasList(
                        table = CommonData(
                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
                            table = "type_formations"
                        )
                    )

                    val responseTypeFormationData: Response<MutableList<CommonData>> = clientTypeFormationData.execute()
                    val datasTypeFormationList: MutableList<CommonData>? = responseTypeFormationData.body()

                    datasTypeFormationList?.map {
                        val dataTypeFormationModel = TypeFormationModel(
                            uid = 0,
                            nom = it.nom,
                            id = it.id,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )

                        typeFormationDao?.insert(dataTypeFormationModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            magasinsFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Type de formations")
                //configFlow()
                //getLiensParenteSection()
                getArbreList()
            }
        }
    }

    suspend fun getArbreList() {
        withContext(IO) {
            val arbreListFetch = async {
                arbreDao?.deleteAll()

                try {
                    val clientArbreListData = ApiClient.apiService.getArbreList()

                    val responseArbreListData: Response<MutableList<ArbreModel>> = clientArbreListData.execute()
                    val datasArbreListList: MutableList<ArbreModel>? = responseArbreListData.body()

                    datasArbreListList?.map {
                        val dataArbreListModel = ArbreModel(
                            uid = 0,
                            nom = it.nom,
                            nomScientifique = it.nomScientifique,
                            strate = it.strate,
                            id = it.id,
                        )

                        arbreDao?.insert(dataArbreListModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            arbreListFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Liste des arbres")
                //configFlow()
                getApprovisionnementList()
            }
        }
    }


    suspend fun getApprovisionnementList() {
        withContext(IO) {
            val approvisionnementListFetch = async {
                approvisionnementDao?.deleteAll(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())

                try {
                    val clientArbreListData = ApiClient.apiService.getApprovisionnement()

                    val responseArbreListData: Response<MutableList<ApprovisionnementModel>> = clientArbreListData.execute()
                    val datasArbreListList: MutableList<ApprovisionnementModel>? = responseArbreListData.body()

                    datasArbreListList?.map {
                        val dataArbreListModel = ApprovisionnementModel(
                            uid = 0,
                            id = it.id,
                            section_id = it.section_id,
                            agroapprovisionnement_id = it.agroapprovisionnement_id,
                            bon_livraison = it.bon_livraison,
                            total = it.total,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID).toString()
                        )

                        approvisionnementDao?.insert(dataArbreListModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            approvisionnementListFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Liste des approvisionnements")
                //configFlow()
                getProductEvalArbrList()
            }
        }
    }

    suspend fun getProductEvalArbrList() {
        withContext(IO) {
            val distArbreListFetch = async {
                evaluationArbreDao?.deleteAgentDatas(SPUtils.getInstance().getInt(Constants.AGENT_ID))

                try {
                    val clientProductEvalListData = ApiClient.apiService.getProductEvalList()

                    val responseProductEvalListData: Response<QuantiteArbrDistribuer> = clientProductEvalListData.execute()
                    val datasProductEval: QuantiteArbrDistribuer? = responseProductEvalListData.body()

                    var variableKey: MutableMap<String, MutableMap<String, String>> = mutableMapOf()
                    datasProductEval?.evaluations?.map {
                        val item = variableKey.get(it.producteur_id)
                        if(item != null)
                            variableKey.get(it.producteur_id)?.put(it.agroespecesarbre_id.toString(), it.total.toString())
                        else {
                            val value = mutableMapOf<String, String>()
                            value.put(it.agroespecesarbre_id.toString(), it.total.toString())
                            variableKey.put(it.producteur_id.toString(), value)
                        }
                    }

                    variableKey.map {

                        val curreProd = it.key
                        val currentInfoArbre = it.value.map { it.key }.toMutableList()
                        val currentInfoArbreEval = it.value.map { it.value }.toMutableList()
                        val dataEvalProdListModel = EvaluationArbreModel(
                            uid = 0,
                            producteurId = curreProd,
                            especesarbreStr = GsonUtils.toJson(currentInfoArbre),
                            quantiteStr = GsonUtils.toJson(currentInfoArbreEval),
                            id = curreProd.toInt(),
                            isSynced = true,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID),
                            origin = "remote"
                        )

                        evaluationArbreDao?.insert(dataEvalProdListModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            distArbreListFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Liste des arbres distribués")
                //configFlow()
                getLiensParenteSection()
            }
        }
    }


    suspend fun getLiensParenteSection() {
        withContext(IO) {
            val liensParenteFetch = async {
                lienParenteDao?.deleteAll()

                try {
                    val clientLienParenteData = ApiClient.apiService.getDatasList(
                        table = CommonData(
                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
                            table = "lien_parente"
                        )
                    )

                    val responseLienParenteData: Response<MutableList<CommonData>> = clientLienParenteData.execute()
                    val datasLienParenteList: MutableList<CommonData>? = responseLienParenteData.body()

                    datasLienParenteList?.map {
                        val dataLienParenteModel = LienParenteModel(
                            uid = 0,
                            nom = it.nom,
                            id = it.id,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            libelle = "",
                            typeFormationId = ""
                        )

                        lienParenteDao?.insert(dataLienParenteModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            liensParenteFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Liens de parenté")
                getMoyenTransportSection()
            }
        }
    }


    suspend fun getMoyenTransportSection() {
        withContext(IO) {
            val moyenTransportFetch = async {
                moyenTransportDao?.deleteAll()

                try {
                    val clientMoyenTransportData = ApiClient.apiService.getDatasList(
                        table = CommonData(
                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
                            table = "moyens_transport"
                        )
                    )

                    val responseMoyenTransportData: Response<MutableList<CommonData>> = clientMoyenTransportData.execute()
                    val datasMoyenTransportList: MutableList<CommonData>? = responseMoyenTransportData.body()

                    datasMoyenTransportList?.map {
                        val dataMoyenTransportModel = MoyenTransportModel(
                            uid = 0,
                            nom = it.nom,
                            id = it.id,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                        )

                        moyenTransportDao?.insert(dataMoyenTransportModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            moyenTransportFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Moyens de transport")
                //getTypeDocumentsSection()
                getUnCompleteParcelleDatas()
            }
        }
    }

//not exist
/*    suspend fun getTypeDocumentsSection() {
        withContext(IO) {
            val typeDocumentFetch = async {
                typeDocuDocumentDao?.deleteAll()


                try {
                    val clientTypeDocumentnData = ApiClient.apiService.getDatasList(
                        table = CommonData(
                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
                            table = "type_documents"
                        )
                    )

                    val responseTypeDocumentData: Response<MutableList<CommonData>> = clientTypeDocumentnData.execute()
                    val datasTypeDocumentList: MutableList<CommonData>? = responseTypeDocumentData.body()

                    datasTypeDocumentList?.map {
                        val dataTypeDocumentModel = TypeDocumentModel(
                            uid = 0,
                            nom = it.nom,
                            id = it.id,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                        )

                        typeDocuDocumentDao?.insert(dataTypeDocumentModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            typeDocumentFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Type de documets")
                getOperateursSelection()
            }
        }
    }*/

//not exist
/*    suspend fun getOperateursSelection() {
        withContext(IO) {
            val operateurFetch = async {
                paiementMobileDao?.deleteAll()

                try {
                    val clientOperateurData = ApiClient.apiService.getDatasList(
                        table = CommonData(
                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
                            table = "paiement_mobile"
                        )
                    )

                    val responsePaiementMobileData: Response<MutableList<CommonData>> = clientOperateurData.execute()
                    val datasPaiementMobileList: MutableList<CommonData>? = responsePaiementMobileData.body()

                    datasPaiementMobileList?.map {
                        val dataPaiementMobileModel = PaiementMobileModel(
                            uid = 0,
                            nom = it.nom,
                            id = it.id,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                            libelle = "",
                            typeFormationId = ""
                        )

                        paiementMobileDao?.insert(dataPaiementMobileModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = true
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            operateurFetch.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Paiemets mobile")
                // getPaiementBanksSelection
                getUnCompleteProducteurDatas()
            }


        }
    }*/

//not exist
//    suspend fun getPaiementBanksSelection() { // Todo fetch data operateur
//        withContext(IO) {
//            val operateurFetch = async {
//                typeDocuDocumentDao?.deleteAll()
//
//
//                try {
//                    val clientOperateurData = ApiClient.apiService.getDatasList(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID),
//                            table = "type_documents"
//                        )
//                    )
//
//                    val responseTypeDocumentData: Response<MutableList<CommonData>> = clientOperateurData.execute()
//                    val datasTypeDocumentList: MutableList<CommonData>? = responseTypeDocumentData.body()
//
//                    datasTypeDocumentList?.map {
//                        val dataTypeDocumentModel = TypeDocumentModel(
//                            uid = 0,
//                            nom = it.nom,
//                            id = it.id,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
//                        )
//
//                        typeDocuDocumentDao?.insert(dataTypeDocumentModel)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            operateurFetch.join()
//            configCompletedOrError("Type de documents")
//        }
//    }

//not exist
//    suspend fun getUnCompleteProducteurDatas() { // TODO : Review duplicate data when synchronize is triggered
//        withContext(IO) {
//            val producteurInfosUncomplete = async {
//                try {
//                    val clientProducteurInfosUncompleteData = ApiClient.apiService.getProducteurInfosUncomplete(
//                        table = CommonData(
//                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID)
//                        )
//                    )
//
//                    val responseProducteurInfosUncompleteData: Response<MutableList<ProducteurUpdateModel>> = clientProducteurInfosUncompleteData.execute()
//                    val datasProducteurInfosUncompleteList: MutableList<ProducteurUpdateModel>? = responseProducteurInfosUncompleteData.body()
//
//                    datasProducteurInfosUncompleteList?.map { producteurUpdate ->
//                        // Check if producteur existe
//                        val producteurExist = producteurDao?.getProducteur(producteurUpdate.id)
//
//                        val producteurDraft = ProducteurModel(
//                            uid = 0,
//                            id = producteurUpdate.id,
//                            codeProd = producteurUpdate.codeProd.toString(),
//                            codeProdApp = producteurUpdate.codeProdapp,
//                            localitesId = producteurUpdate.localitesId,
//                            nom = producteurUpdate.nom.toString(),
//                            prenoms = producteurUpdate.prenoms.toString(),
//                            dateNaiss = producteurUpdate.dateNaiss.toString(),
//                            nationalite = producteurUpdate.nationalitesId.toString(),
//                            phoneOne = producteurUpdate.phone1.toString(),
//                            phoneTwo = producteurUpdate.phone2.toString(),
//                            piece = producteurUpdate.typePiecesId,
//                            pieceNumber = producteurUpdate.numPiece,
//                            statutCertification = producteurUpdate.statut,
//                            anneeCertification = producteurUpdate.certificat,
//                            sexeProducteur = producteurUpdate.sexe,
//                            isSynced = false,
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
//                            origin = "local",
//                            consentement = producteurUpdate.consentement,
//                            rectoPath = producteurUpdate.copiecarterecto ?: "",
//                            versoPath = producteurUpdate.copiecarteverso ?: "",
//                            picturePath = producteurUpdate.picture ?: "",
//                            esignaturePath = producteurUpdate.esignature ?: "",
//                        )
//
//                        producteurDraft.localite = localiteDao?.getLocalite(producteurUpdate.localitesId ?: 0)?.nom
//
//                        val draftedProducteurInfosUncompleteModel = DataDraftedModel(
//                            uid = 0,
//                            datas = ApiClient.gson.toJson(producteurDraft),
//                            ownerDraft = "${producteurDraft.nom} ${producteurDraft.prenoms}",
//                            typeDraft = "content_producteur",
//                            dateDraft = DateTime.now().toString(DateTimeFormat.forPattern("EEEE, 'le' dd MMMM 'à' HH:mm:ss")),
//                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
//                            draftCompleted = false
//                        )
//
//                        draftedDatasDao?.insert(draftedProducteurInfosUncompleteModel)
//                    }
//                } catch (ex: Exception) {
//                    oneIssue = true
//                    LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                }
//            }
//
//            producteurInfosUncomplete.join()
//
//            if (oneIssue) {
//                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
//            } else {
//                configCompletedOrError("Fiche incomplete (producteurs)")
//                getUnCompleteParcelleDatas()
//            }
//
//
//        }
//    }


    suspend fun getUnCompleteParcelleDatas() {
        withContext(IO) {
            val parcelleInfosUncomplete = async {

                try {
                    val clientParcelleInfosUncompleteData = ApiClient.apiService.getParcelleInfosUncomplete(
                        table = CommonData(
                            userid = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID)
                        )
                    )

                    val responseParcelleInfosUncompleteData: Response<MutableList<ParcelleUpdateModel>> = clientParcelleInfosUncompleteData.execute()
                    val datasParcelleInfosUncompleteList: MutableList<ParcelleUpdateModel>? = responseParcelleInfosUncompleteData.body()

                    datasParcelleInfosUncompleteList?.map { parcelleUpdate ->
                        val parcelleDraft = ParcelleModel(
                            uid = 0,
                            id = parcelleUpdate.id,
                            producteurId = parcelleUpdate.producteursId.toString(),
                            producteurNom = "${parcelleUpdate.nom} ${parcelleUpdate.prenoms}",
                            anneeCreation = parcelleUpdate.anneeCreation.toString(),
                            codeParc = parcelleUpdate.codeParc,
                            culture = parcelleUpdate.culture.toString(),
                            wayPointsString = ApiClient.gson.toJson(mutableListOf<String>()),
                            perimeter = "",
                            typedeclaration = parcelleUpdate.typedeclaration.toString(),
                            superficie = parcelleUpdate.superficie.toString(),
                            latitude = parcelleUpdate.latitude.toString(),
                            longitude = parcelleUpdate.longitude,
                            isSynced = false,
                            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
                            origin = "local"
                        )

                        val draftedParcelleInfosUncompleteModel = DataDraftedModel(
                            uid = 0,
                            datas = ApiClient.gson.toJson(parcelleDraft),
                            ownerDraft = "${parcelleDraft.producteurNom}",
                            typeDraft = "content_parcelle",
                            dateDraft = DateTime.now().toString(DateTimeFormat.forPattern("EEEE, 'le' dd MMMM 'à' HH:mm:ss")),
                            agentId = parcelleDraft.agentId,
                            draftCompleted = false
                        )

                        draftedDatasDao?.insert(draftedParcelleInfosUncompleteModel)
                    }
                } catch (ex: Exception) {
                    oneIssue = false
                    LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

            parcelleInfosUncomplete.join()

            if (oneIssue) {
                configCompletedOrError("Une erreur est survenue, veuillez recommencer la mise à jour svp.", hasError = true, hisSynchro = true)
            } else {
                configCompletedOrError("Fiche incomplete (parcelles)", hasOtherDatas = false)
            }
        }
    }
    // endregion


    // region SYNCHRONIZE USER DATAS
    suspend fun synchronizeLocalite() {
        var flagLocaliteSynchroErreur = false

        withContext(IO) {
            val dataSynchronisation = async {
                val dataUnSynchronized = localiteDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                )

                dataUnSynchronized?.map { data ->
                    try {
                        val clientDatas: Call<LocaliteModel> = ApiClient.apiService.synchronisationLocalite(localiteModel = data)

                        val responseData: Response<LocaliteModel> = clientDatas.execute()
                        val dataSync: LocaliteModel = responseData.body()!!

                        val producteurLocalitesList = producteurDao?.getProducteursUnSynchronizedLocal(
                            dataSync.uid.toString(),
                            SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                        )!!

                        for (producteur in producteurLocalitesList) {
                            producteur.localitesId = dataSync.id.toString()
                            producteurDao?.insert(producteur)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        flagLocaliteSynchroErreur = true
                    }
                }
            }

            dataSynchronisation.invokeOnCompletion {
                if (it == null) {
                    flagLocaliteSynchroErreur = false
                } else {
                    flagLocaliteSynchroErreur = true
                    it.printStackTrace()
                }
            }

            dataSynchronisation.join()

            if (flagLocaliteSynchroErreur) {
                configCompletedOrError(hasError = true, info = "impossible de synchroniser les localités")
            } else {
                localiteDao?.deleteAgentDatas(agentID = agentID.toString())
                synchronizeProducteurs()
            }
        }
    }


    suspend fun synchronizeProducteurs() {
        var flagProducteurSynchroErreur = false
        withContext(IO) {
            val dataSynchronisation = async {
                val dataSynchronized = producteurDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                )

                dataSynchronized?.map { producteur ->
                    // deserialize datas producteurs
                    val culturesType = object : TypeToken<MutableList<CultureProducteurModel>>() {}.type
                    producteur.producteursCultures = GsonUtils.fromJson<MutableList<CultureProducteurModel>>(producteur.cultures, culturesType)
                    producteur.typeculture = mutableListOf()
                    producteur.superficieculture = mutableListOf()

                    producteur.dateNaiss = Commons.convertDate(producteur.dateNaiss, true)

                    producteur.producteursCultures?.map { culture ->
                        producteur.typeculture?.add(culture.label!!)
                        producteur.superficieculture?.add(culture.superficie.toString())
                    }

                    val clientProducteur: Call<ProducteurModel> = ApiClient.apiService.synchronisationProducteur(producteurModel = producteur)

                    val responseProducteur: Response<ProducteurModel> = clientProducteur.execute()
                    val producteurSynced: ProducteurModel? = responseProducteur.body()

                    producteurSynced?.let {
                        producteurDao?.syncData(
                            id = producteurSynced?.id!!,
                            synced = true,
                            localID = producteur.uid
                        )

                        val producteurMenagesList = menageDao?.getMenagesUnSynchronizedLocal(
                            producteur.uid.toString(),
                            SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )!!

                        for (prodMenage in producteurMenagesList) {
                            prodMenage.producteurs_id = producteurSynced?.id.toString()
                            menageDao?.insert(prodMenage)
                        }

                        val producteurParcellesList = parcelleDao?.getParcellesUnSynchronizedLocal(
                            producteur.uid.toString(),
                            SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )!!

                        for (parcelle in producteurParcellesList) {
                            parcelle.producteurId = producteurSynced?.id.toString()
                            parcelleDao?.insert(parcelle)
                        }

                        val livraisonsList = livraisonDao?.getUnSyncedAll(
                            agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                        )!!

                        livraisonsList.map { livraisonModel ->
                            livraisonModel.producteursId = producteurSynced?.id.toString()
                            livraisonDao?.insert(livraisonModel)
                        }

                    }
                }
            }

            dataSynchronisation.invokeOnCompletion {
                if (it == null) {
                    flagProducteurSynchroErreur = false
                } else {
                    flagProducteurSynchroErreur = true
                    //LogUtils.e(TAG, "Erreur")
                    it.printStackTrace()
                }
            }

            dataSynchronisation.join()

            if (flagProducteurSynchroErreur) {
                configCompletedOrError(hasError = true, info = "Impossible de synchroniser les producteurs")
            } else {
                producteurDao?.deleteAgentDatas(agentID = agentID.toString())
                sychronisationParcelles()
            }
        }
    }


    suspend fun sychronisationParcelles() {
        var flagParcelleSynchroErreur = false

        withContext(IO) {
            var parcelleSynchronized = async {
                val parcelleDatas = parcelleDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                )

                parcelleDatas?.map { parcelle ->
                    try {
                        //LogUtils.e(TAG, "syncParcelle ID before -> ${parcelle.id}")
                        val clientParcelle: Call<ParcelleModel> = ApiClient.apiService.synchronisationParcelle(parcelle)

                        val responseParcelle: Response<ParcelleModel> = clientParcelle.execute()
                        val parcelleSync: ParcelleModel = responseParcelle.body()!!

                        parcelleDao?.syncData(
                            id = parcelleSync.id!!,
                            synced = true,
                            localID = parcelle.uid.toInt()
                        )

                        val suiviParcellesList = suiviParcelleDao?.getSuiviParcellesUnSynchronizedLocal(
                            parcelleUid = parcelle.uid.toString(),
                            SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString(),
                        )!!

                        for (suivi in suiviParcellesList) {
                            suivi.parcellesId = parcelleSync.id.toString()
                            suivi.producteursId = parcelleSync.producteurId
                            suiviParcelleDao?.insert(suivi)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        flagParcelleSynchroErreur = true
                    }
                }
            }

            parcelleSynchronized.invokeOnCompletion {
                if (it == null) {
                    flagParcelleSynchroErreur = false
                } else {
                    flagParcelleSynchroErreur = true
                    //LogUtils.e(TAG, "Erreur")
                    it.printStackTrace()
                }
            }

            parcelleSynchronized.join()

            if (flagParcelleSynchroErreur) {
                configCompletedOrError(hasError = true, info = "Impossible de synchroniser les parcelles des producteurs")
            } else {
                parcelleDao?.deleteAgentDatas(agentID = agentID.toString())
                synchronisationSuiviParcelles()
            }
        }
    }


    suspend fun synchronisationSuiviParcelles() {
        var flagSuiviParcelleSynchroError = false

        withContext(IO) {
            var suiviSynchronisation = async {
                val suiviDatas = suiviParcelleDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                )

                suiviDatas?.map { suivi ->
                    try {
                        // deserialize datas producteurs
                        val ombragesType = object : TypeToken<MutableList<OmbrageVarieteModel>>() {}.type
                        suivi.ombrages = GsonUtils.fromJson<MutableList<OmbrageVarieteModel>>(suivi.varieteOmbragesTemp, ombragesType)

                        //LogUtils.e(TAG, suivi.ombrages?.size)

                        suivi.varietesOmbrage = mutableListOf()
                        suivi.nombreOmbrage = mutableListOf()

                        suivi.ombrages?.map { ombrage ->
                            suivi.varietesOmbrage?.add(ombrage.variete!!)
                            suivi.nombreOmbrage?.add(ombrage.nombre.toString())
                        }

                        suivi.dateVisite = Commons.convertDate(suivi.dateVisite, true)

                        //LogUtils.e(TAG, "suiviDatas ID before -> ${suivi.id}")
                        val clientSuivi: Call<SuiviParcelleModel> = ApiClient.apiService.synchronisationSuivi(suivi)

                        val responseSuivi: Response<SuiviParcelleModel> = clientSuivi.execute()
                        val suiviSynced: SuiviParcelleModel? = responseSuivi.body()

                        suiviParcelleDao?.syncData(
                            id = suiviSynced?.id!!,
                            synced = true,
                            localID = suivi.uid
                        )
                        //LogUtils.e(TAG, "Producteur ID after -> ${suiviSynced?.id}")
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        flagSuiviParcelleSynchroError = true
                    }
                }
            }

            suiviSynchronisation.invokeOnCompletion {
                if (it == null) {
                    flagSuiviParcelleSynchroError = false
                } else {
                    flagSuiviParcelleSynchroError = true
                    //LogUtils.e(TAG, "Erreur")
                    it.printStackTrace()
                }
            }

            suiviSynchronisation.join()

            if (flagSuiviParcelleSynchroError) {
                configCompletedOrError(hasError = true, info = "Impossible de synchroniser les suivis des parcelles")
            } else {
                suiviParcelleDao?.deleteAgentDatas(agentID = agentID.toString())
                synchronisationMenages()
            }
        }
    }


    suspend fun synchronisationMenages() {
        var flagMenageSynchroError = false

        withContext(IO) {
            var menageSynchronized = async {
                val menageDatas = menageDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                )

                menageDatas?.map { menage ->
                    try {
                        //LogUtils.e(TAG, "menage ID before -> ${menage.id}")
                        val clientMenage: Call<ProducteurMenageModel> =
                            ApiClient.apiService.synchronisationMenage(menage)

                        val responseMenage: Response<ProducteurMenageModel> = clientMenage.execute()
                        val menageSync: ProducteurMenageModel = responseMenage.body()!!

                        menageDao?.syncData(
                            id = menageSync.id!!.toInt(),
                            synced = true,
                            localID = menage.uid.toInt()
                        )
                        //LogUtils.e(TAG, "menage ID after -> ${menageSync.id}")
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        //LogUtils.e(TAG, ex.message)
                        flagMenageSynchroError = true
                    }
                }
            }

            menageSynchronized.invokeOnCompletion {
                if (it == null) {
                    flagMenageSynchroError = false
                } else {
                    flagMenageSynchroError = true
                    //LogUtils.e(TAG, "Erreur")
                    it.printStackTrace()
                }
            }

            menageSynchronized.join()

            if (flagMenageSynchroError) {
                configCompletedOrError(hasError = true, info = "Impossible de synchroniser les menages des producteurs")
            } else {
                menageDao?.deleteAgentDatas(agentID = agentID.toString())
                //synchronisationMenages()
                synchtonisationFormations()
            }
        }
    }


    suspend fun synchtonisationFormations() {
        var flagFormationSynchroError = false
        var suncFormationsFlag = false

        withContext(IO) {
            var formationSynchronisation = async {
                val formationDatas = formationDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                )

                formationDatas?.map { formation ->
//                    try {
//                        // deserialize datas producteurs
//                        val producteursType = object : TypeToken<MutableList<String>>() {}.type
//                        formation.producteursId = GsonUtils.fromJson<MutableList<String>>(formation.producteursIdStringify, producteursType)
//                        val cleanList = formation.producteursId?.toMutableList()
//
//                        formation.dateFormation = Commons.convertDate(formation.dateFormation, true)
//
//                        var positionLoop = 0
//                        var positionFound = 0
//
//                        formation.producteursId?.map {
//                            val producteurId = it.split("-")[0]
//                            val typeId = it.split("-")[1]
//
//                            if (typeId == "uid") {
//                                val producteurCheck = producteurDao?.getProducteurByUID(producteurUID = producteurId.toInt())
//
//                                if (producteurCheck?.isSynced!!) {
//                                    positionFound = positionLoop
//                                    cleanList?.removeAt(positionFound)
//                                    cleanList?.add("${producteurCheck.id}-id")
//                                } else {
//                                    suncFormationsFlag = false
//                                }
//                            }
//
//                            positionLoop += 1
//                        }
//
//                        formation.producteursId = mutableListOf()
//                        formation.producteursId = cleanList
//                    } catch (ex: Exception) {
//                        ex.printStackTrace()
//                    }

                }

//                if (suncFormationsFlag) { // all is done bring synchronization
//                    formationDatas?.map { formationModel ->
//                        // deserialize datas producteurs
//                        val producteursType = object : TypeToken<MutableList<String>>() {}.type
//                        formationModel.producteursId = GsonUtils.fromJson<MutableList<String>>(formationModel.producteursIdStringify, producteursType)
//
//                        val listM = formationModel.producteursId?.map {
//                            it.replace("-id", "")
//                        }
//
//                        formationModel.producteursId = listM?.toMutableList()
//
//                        val clientFormation: Call<FormationModel> = ApiClient.apiService.synchronisationFormation(formationModel = formationModel)
//
//                        val responseFormation: Response<FormationModel> = clientFormation.execute()
//                        val formationSynced: FormationModel? = responseFormation.body()
//
//                        formationDao?.syncData(
//                            formationSynced?.id!!,
//                            true,
//                            formationModel.uid
//                        )
//                    }
//                }

            }

//            formationSynchronisation.invokeOnCompletion {
//                if (it == null) {
//                    flagFormationSynchroError = false
//                } else {
//                    flagFormationSynchroError = true
//                    //LogUtils.e(TAG, "Erreur")
//                    it.printStackTrace()
//                }
//            }
//
//            formationSynchronisation.join()
//
//            if (flagFormationSynchroError) {
//                configCompletedOrError(hasError = true, info = "Impossible de synchroniser les formations")
//            } else {
//            }
            //formationDao?.deleteAgentDatas(agentID = agentID.toString())
            synchronisationLivraisons()
        }
    }


    suspend fun synchronisationLivraisons() {
        var flagLivraisonSynchroError = false

        withContext(IO) {
            var livraisonSynchronized = async {
                val livraisonDatas = livraisonDao?.getUnSyncedAll(
                    agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, agentID).toString()
                )

                livraisonDatas?.map { livraisonPojo ->
                    livraisonPojo.dateLivre = Commons.convertDate(livraisonPojo.dateLivre, true)

                    val clientLivraison: Call<LivraisonModel> = ApiClient.apiService.synchronisationLivraisonSection(livraisonModel = livraisonPojo)

                    val responseLivraison: Response<LivraisonModel> = clientLivraison.execute()
                    val livraisonSynced: LivraisonModel? = responseLivraison.body()

                    try {
                        livraisonDao?.syncData(
                            livraisonSynced?.id!!,
                            true,
                            livraisonPojo.uid
                        )

                        //LogUtils.e(SynchronisationIntentService.TAG, "Livraison -> ${livraisonSynced?.id}")
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        flagLivraisonSynchroError = true
                    }
                }
            }

            livraisonSynchronized.invokeOnCompletion {
                if (it == null) {
                    flagLivraisonSynchroError = false
                } else {
                    flagLivraisonSynchroError = true
                    //LogUtils.e(TAG, "Erreur")
                    it.printStackTrace()
                }
            }

            livraisonSynchronized.join()

            if (flagLivraisonSynchroError) {
                configCompletedOrError(hasError = true, info = "Impossible de synchroniser les livraisons")
            } else {
                livraisonDao?.deleteAgentDatas(agentID = agentID.toString())
                getLocalites()
            }

        }
    }

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        database = CcbRoomDatabase.getDatabase(this)

        agentDoa = database?.agentDoa()
        coursEauxDao = database?.courEauDoa()
        eauUseeDao = database?.eauUseeDoa()
        gardeMachineDao = database?.gardeMachineDoa()
        localiteDao = database?.localiteDoa()
        lieuFormationDao = database?.lieuFormationDoa()
        nationaliteDao = database?.nationaliteDoa()
        niveauDao = database?.niveauDoa()
        ordureMenagereDao = database?.ordureMenagereDoa()
        sourceEauDao = database?.sourceEauDoa()
        sourceEnergieDao = database?.sourceEnergieDoa()
        typeLocaliteDao = database?.typeLocaliteDao()
        typeMachineDao = database?.typeMachineDao()
        varieteCacaoDao = database?.varieteCacaoDao()
        personneBlesseeDao = database?.persBlesseeDoa()
        draftedDatasDao = database?.draftedDatasDao()
        typePieceDao = database?.typePieceDao()
        recuDao = database?.recuDao()
        parcelleDao = database?.parcelleDao()
        producteurDao = database?.producteurDoa()
        delegueDao = database?.delegueDao()
        formationDao = database?.formationDao()
        themeFormationDao = database?.themeFormationDao()
        sousThemeFormationDao = database?.sousThemeFormationDao()
        livraisonDao = database?.livraisonDao()
        suiviParcelleDao = database?.suiviParcelleDao()
        intrantDao = database?.intrantDao()
        campagneDao = database?.campagneDao()
        applicateurDao = database?.applicateurDao()
        notationDao = database?.notationDao()
        questionnaireDao = database?.questionnaireDao()
        magasinDao = database?.magasinCentralDao()
        magasinSectionDao = database?.magasinSectionDao()
        typeFormationDao = database?.typeFormationDao()
        lienParenteDao = database?.lienParenteDao()
        paiementMobileDao = database?.paiementMobileDao()
        moyenTransportDao = database?.moyenTransport()
        typeDocuDocumentDao = database?.typeDocumentDao()
        typeProduitDao = database?.typeProduitDao()
        concernesDao = database?.concernesDao()
        transporteurDao = database?.transporteurDao()
        entrepriseDao = database?.entrepriseDao()
        vehiculeDao = database?.vehiculeDao()
        remorqueDao = database?.remorqueDao()
        livraisonVerMagCentralDao = database?.livraisonVerMagCentralDao()
        staffFormationDao = database?.staffFormation()
        programmesDao = database?.programmesDao()
        sectionsDao = database?.sectionsDao()
        arbreDao = database?.arbreDao()
        evaluationArbreDao = database?.evaluationArbreDao()
        approvisionnementDao = database?.approvisionnementDao()

        if (intent != null) {
            agentID = intent.getIntExtra(Constants.AGENT_ID, 0)
            agentModel = agentDoa?.getAgent(agentID)
        }

        actionUpdate.setOnClickListener {
            ActivityUtils.finishAllActivities(true)
            ActivityUtils.startActivity(SplashActivity::class.java)
        }

        MainScope().launch {
            configFlow()
        }
    }
}
