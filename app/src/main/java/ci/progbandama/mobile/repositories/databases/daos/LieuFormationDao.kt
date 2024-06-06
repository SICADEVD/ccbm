package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface LieuFormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lieuFormation: LieuFormationModel)

    @Transaction
    @Query("SELECT * FROM lieu_formation WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<LieuFormationModel>


    @Transaction
    @Query("DELETE FROM lieu_formation")
    fun deleteAll()
}

/*
cours_eaux
eaux_usees
garde_machines
lieu_formations
nationalites
niveaux
ordures_menageres
sources_eaux
sources_energies
type_localites
type_machines
type_pieces
varietes_cacao
 */