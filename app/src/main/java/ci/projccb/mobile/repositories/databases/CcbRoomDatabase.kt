package ci.projccb.mobile.repositories.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ci.projccb.mobile.models.*
import ci.projccb.mobile.repositories.databases.daos.*
import androidx.sqlite.db.SupportSQLiteDatabase

import androidx.room.migration.Migration
import ci.projccb.mobile.tools.ListConverters


/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Database(
    version = 3, exportSchema = false,
    entities = [
        AgentModel::class,
        EauUseeModel::class,
        CourEauModel::class,
        GardeMachineModel::class,
        LieuFormationModel::class,
        NationaliteModel::class,
        NiveauModel::class,
        OrdureMenagereModel::class,
        PersonneBlesseeModel::class,
        SourceEauModel::class,
        SourceEnergieModel::class,
        TypeMachineModel::class,
        PaiementMobileModel::class,
        TypePieceModel::class,
        VarieteCacaoModel::class,
        LocaliteModel::class,
        TypeLocaliteModel::class,
        ProducteurModel::class,
        ProducteurMenageModel::class,
        ParcelleModel::class,
        RecuModel::class,
        LivraisonModel::class,
        FormationModel::class,
        TypeFormationModel::class,
        DelegueModel::class,
        SuiviParcelleModel::class,
        IntrantModel::class,
        ParcelleMappingModel::class,
        CampagneModel::class,
        SuiviApplicationModel::class,
        ApplicateurModel::class,
        NotationModel::class,
        MagasinModel::class,
        InspectionQuestionnairesModel::class,
        EstimationModel::class,
        EnqueteSsrtModel::class,
        InspectionDTO::class,
        LienParenteModel::class,
        MoyenTransportModel::class,
        TypeDocumentModel::class,
        InfosProducteurDTO::class,
        OperateurModel::class,
        ThemeFormationModel::class,
        TypeProduitModel::class,
        DataDraftedModel::class,
        ConcernesModel::class,
        SectionModel::class,
        ProgrammeModel::class,
        DistributionArbreModel::class,
        EvaluationArbreModel::class,
    ],
)
@TypeConverters(ListConverters::class)
abstract class CcbRoomDatabase : RoomDatabase() {

    abstract fun draftedDatasDao(): DraftedDatasDao
    abstract fun typeProduitDao(): TypeProduitDao
    abstract fun themeFormationDao(): ThemeFormationDao
    abstract fun moyenTransport(): MoyenTransportDao
    abstract fun lienParenteDao(): LienParenteDao
    abstract fun infosProducteurDao(): InfosProducteurDao
    abstract fun typeDocumentDao(): TypeDocumentDao
    abstract fun operateurDao(): OperateurDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun typeFormationDao(): TypeFormationDao
    abstract fun questionnaireDao(): QuestionnaireDao
    abstract fun enqueteSsrtDao(): EnqueteSsrteDao
    abstract fun estimationDao(): EstimationDao
    abstract fun magasinSectionDao(): MagasinDao
    abstract fun notationDao(): NotationDao
    abstract fun courEauDoa(): CourEauDao
    abstract fun applicateurDao(): ApplicateurDao
    abstract fun campagneDao(): CampagneDao
    abstract fun parcelleMappingDao(): ParcelleMappingDao
    abstract fun agentDoa(): AgentDao
    abstract fun eauUseeDoa(): EauUseeDao
    abstract fun gardeMachineDoa(): GardeMachineDao
    abstract fun lieuFormationDoa(): LieuFormationDao
    abstract fun nationaliteDoa(): NationaliteDao
    abstract fun niveauDoa(): NiveauDao
    abstract fun ordureMenagereDoa(): OrdureMenagereDao
    abstract fun persBlesseeDoa(): PersonneBlesseeDao
    abstract fun sourceEauDoa(): SourceEauDao
    abstract fun sourceEnergieDoa(): SourceEnergieDao
    abstract fun typeLocaliteDao(): TypeLocaliteDao
    abstract fun typeMachineDao(): TypeMachineDao
    abstract fun typePieceDao(): TypePieceDao
    abstract fun varieteCacaoDao(): VarieteCacaoDao
    abstract fun localiteDoa(): LocaliteDao
    abstract fun producteurDoa(): ProducteurDao
    abstract fun producteurMenageDoa(): ProducteurMenageDao
    abstract fun parcelleDao(): ParcelleDao
    abstract fun suiviParcelleDao(): SuiviParcelleDao
    abstract fun suiviApplicationDao(): SuiviApplicationDao
    abstract fun recuDao(): RecuDao
    abstract fun livraisonDao(): LivraisonDao
    abstract fun formationDao(): FormationDao
    abstract fun delegueDao(): DelegueDao
    abstract fun intrantDao(): IntrantDao
    abstract fun paiementMobileDao(): PaiementMobileDao
    abstract fun concernesDao(): ConcernesDao
    abstract fun sectionsDao(): SectionsDao
    abstract fun programmesDao(): ProgrammesDao
    abstract fun distributionArbreDao(): DistributionArbreDao
    abstract fun evaluationArbreDao(): EvaluationArbreDao


    companion object {
        private var INSTANCE: CcbRoomDatabase? = null


        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }


        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }


        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }


        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }


        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }


        fun getDatabase(context: Context): CcbRoomDatabase? {
            return INSTANCE ?: synchronized(CcbRoomDatabase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CcbRoomDatabase::class.java,
                    "projccb.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
