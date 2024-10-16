package ci.progbandama.mobile.repositories.databases

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ci.progbandama.mobile.models.*
import ci.progbandama.mobile.repositories.databases.daos.*
import androidx.sqlite.db.SupportSQLiteDatabase

import androidx.room.migration.Migration
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.ListConverters


/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Database(
    version = Constants.BUILD_VERSION, exportSchema = false,
    entities = [
        CoopModel::class,
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
        SousThemeFormationModel::class,
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
        VisiteurFormationModel::class,
        ArbreModel::class,
        StaffFormationModel::class,
        MagasinCentralModel::class,
        EntrepriseModel::class,
        TransporteurModel::class,
        VehiculeModel::class,
        RemorqueModel::class,
        LivraisonCentralModel::class,
        LivraisonVerMagCentralModel::class,
        ApprovisionnementModel::class,
        PostPlantingModel::class,
        PostPlantingArbrDistribModel::class,
    ],
//    autoMigrations = [
//        AutoMigration(from = Constants.LAST_BUILD_VERSION, to = Constants.BUILD_VERSION)
//    ]
)
@TypeConverters(ListConverters::class)
abstract class ProgBandRoomDatabase : RoomDatabase() {

    abstract fun coopDao(): CoopDao
    abstract fun draftedDatasDao(): DraftedDatasDao
    abstract fun typeProduitDao(): TypeProduitDao
    abstract fun themeFormationDao(): ThemeFormationDao
    abstract fun sousThemeFormationDao(): SousThemeFormationDao
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
    abstract fun magasinCentralDao(): MagasinCentralDao
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
    abstract fun visiteurFormationDao(): VisiteurFormationDao
    abstract fun arbreDao(): ArbreDao
    abstract fun staffFormation(): StaffFormationDao
    abstract fun entrepriseDao(): EntrepriseDao
    abstract fun transporteurDao(): TransporteurDao
    abstract fun vehiculeDao(): VehiculeDao
    abstract fun remorqueDao(): RemorqueDao
    abstract fun livraisonCentralDao(): LivraisonCentralDao
    abstract fun livraisonVerMagCentralDao(): LivraisonVerMagCentralDao
    abstract fun approvisionnementDao(): ApprovisionnementDao
    abstract fun postplantingDao(): PostplantingDao
    abstract fun postPlantingArbrDistribDao(): PostPlantingArbrDistribDao


    companion object {
        var INSTANCE: ProgBandRoomDatabase? = null


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

        val MIGRATION_71_72 = object : Migration(71, 72) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajout des nouvelles colonnes à la table EstimationModel
                database.execSQL("ALTER TABLE estimation ADD COLUMN ajustement TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN typeEstimation TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN recolteEstime TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN rendFinal TEXT")
            }
        }

        val MIGRATION_72_73 = object : Migration(72, 73) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajout des nouvelles colonnes à la table EstimationModel
                database.execSQL("ALTER TABLE estimation ADD COLUMN ajustement TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN typeEstimation TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN recolteEstime TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN rendFinal TEXT")
            }
        }

        val MIGRATION_73_74 = object : Migration(73, 74) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajout des nouvelles colonnes à la table EstimationModel
                database.execSQL("ALTER TABLE estimation ADD COLUMN ajustement TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN typeEstimation TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN recolteEstime TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN rendFinal TEXT")
            }
        }

        val MIGRATION_74_75 = object : Migration(74, 75) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajout des nouvelles colonnes à la table EstimationModel
                database.execSQL("ALTER TABLE estimation ADD COLUMN ajustement TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN typeEstimation TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN recolteEstime TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN rendFinal TEXT")
            }
        }

        val MIGRATION_75_76 = object : Migration(75, 76) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajout des nouvelles colonnes à la table EstimationModel
                database.execSQL("ALTER TABLE estimation ADD COLUMN ajustement TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN typeEstimation TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN recolteEstime TEXT")
                database.execSQL("ALTER TABLE estimation ADD COLUMN rendFinal TEXT")
            }
        }

        val MIGRATION_76_77 = object : Migration(75, 76) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajout des nouvelles colonnes à la table EstimationModel
                database.execSQL("ALTER TABLE parcelle ADD COLUMN otherTestVar TEXT")
            }
        }

        fun escapeSql(value: String): String {
            val builder = StringBuilder()
            for (char in value) {
                when (char) {
                    '\'', '%' -> builder.append(char) // Escape single quote and percent sign builder.append('')
                    else -> builder.append(char)
                }
            }
//            LogUtils.d(builder.toString())
            return "%"+builder.toString()+"%"
        }

        fun getDatabase(context: Context): ProgBandRoomDatabase? {
            return INSTANCE ?: synchronized(ProgBandRoomDatabase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProgBandRoomDatabase::class.java,
                    "progbandama.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
//                    .addMigrations(MIGRATION_71_76)
                    .addMigrations(
//                        MIGRATION_71_72,
//                        MIGRATION_72_73,
//                        MIGRATION_73_74,
//                        MIGRATION_74_75,
                        MIGRATION_75_76,
//                        MIGRATION_76_77,
                        )
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
