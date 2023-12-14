package ci.projccb.mobile.repositories.apis.services

import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.datas.AgentAuthResponse
import ci.projccb.mobile.repositories.datas.CommonData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {


    @POST("connexion")
    fun authAgent(@Body user: AgentModel): Call<AgentAuthResponse>

    // region SYNCHRONIZATION
    @POST("apiproducteur")
    fun synchronisationProducteur(@Body producteurModel: ProducteurModel): Call<ProducteurModel>

    @POST("apilocalite")
    fun synchronisationLocalite(@Body localiteModel: LocaliteModel): Call<LocaliteModel>

    @POST("apisuiviparcelle")
    fun synchronisationSuivi(@Body suiviParcelleModel: SuiviParcelleModel): Call<SuiviParcelleModel>

    @POST("apiparcelle")
    fun synchronisationParcelle(@Body parcelleModel: ParcelleModel): Call<ParcelleModel>

    @POST("apimenage")
    fun synchronisationMenage(@Body menageModel: ProducteurMenageModel): Call<ProducteurMenageModel>

    @POST("apisuiviformation")
    fun synchronisationFormation(@Body formationModel: FormationModel): Call<FormationModel>

    @POST("apivisiteur")
    fun synchronisationVisiteurFormation(@Body visiteurFormationModel: VisiteurFormationModel): Call<VisiteurFormationModel>

    @POST("apiagroevaluation")
    fun synchronisationEvaluationBesoin(@Body evaluationArbreModel: EvaluationArbreModel): Call<EvaluationArbreModel>

    @POST("apilivraison")
    fun synchronisationLivraison(@Body livraisonModel: LivraisonModel): Call<LivraisonModel>

    @POST("apievaluation")
    fun synchronisationInspection(@Body questionDTO: InspectionDTO): Call<InspectionDTO>

    @POST("apiinfosproducteur")
    fun synchronisationInfosProducteur(@Body infosProducteurDTO: InfosProducteurDTO): Call<InfosProducteurDTO>
    // endregion



    @POST("getmagasinsection")
    fun getMagasins(@Body commonData: CommonData): Call<MutableList<MagasinModel>>



    // region SERVICE WITH COROUTINES

    @POST("apiproducteur")
    fun synchronisationProducteurC(@Body producteurModel: ProducteurModel): Call<ProducteurModel>

    @POST("getproducteurs")
    fun getProducteurs(@Body agent: AgentModel): Call<MutableList<ProducteurModel>>

    @POST("getlistedatas")
    fun getDatasList(@Body table: CommonData): Call<MutableList<CommonData>>

    @POST("getcampagne")
    fun getCampagnes(): Call<MutableList<CampagneModel>>

    @POST("getapplicateurs")
    fun getApplicateurs(@Body table: CommonData): Call<MutableList<ApplicateurModel>>

    @POST("getlocalite")
    fun getLocalites(@Body table: CommonData): Call<MutableList<LocaliteModel>>

    @POST("getdomain")
    fun getDomaine(@Body table: CommonData): Call<CommonResponse>

    @POST("getproducteurupdate")
    fun getProducteurInfosUncomplete(@Body table: CommonData): Call<MutableList<ProducteurUpdateModel>>

    @POST("getparcelleupdate")
    fun getParcelleInfosUncomplete(@Body table: CommonData): Call<MutableList<ParcelleUpdateModel>>

    @POST("getdelegues")
    fun getDelegues(@Body table: CommonData): Call<MutableList<CommonData>>

    @POST("getparcelles")
    fun getParcelles(): Call<MutableList<ParcelleModel>>

    @POST("getquestionnaire")
    fun getQuestionnaires(): Call<MutableList<InspectionQuestionnairesModel>>

    @POST("getnotation")
    fun getNotations(): Call<MutableList<NotationModel>>

    @POST("getthemes")
    fun getThemes(): Call<MutableList<ThemeFormationModel>>

    @POST("getsousthemes")
    fun getSousThemes(): Call<MutableList<SousThemeFormationModel>>

    @POST("apiestimation")
    fun synchronisationEstimation(@Body estimationModel: EstimationModel): Call<EstimationModel>

    @POST("apiapplication")
    fun synchronisationSuiviApplication(@Body suiviApplicationModel: SuiviApplicationModel): Call<SuiviApplicationModel>

    @POST("apissrteclrms")
    fun synchronisationEnqueteSsrt(@Body enqueteSsrtModel: EnqueteSsrtModel): Call<EnqueteSsrtModel>

    @POST("apilocalite")
    fun synchronisationLocaliteC(@Body localiteModel: LocaliteModel): Call<LocaliteModel>

    @POST("gettypeformation")
    fun getTypeThemesFormations(): Call<MutableList<TypeFormationModel>>

    @POST("getstaff")
    fun getStaff(@Body table: CommonData): Call<MutableList<ConcernesModel>>

    @POST("getsections")
    fun getSections(@Body table: CommonData): Call<MutableList<SectionModel>>

    @POST("getprogrammes")
    fun getProgrammes(): Call<MutableList<ProgrammeModel>>

    @POST("getarbre")
    fun getArbreList(): Call<MutableList<ArbreModel>>

}
