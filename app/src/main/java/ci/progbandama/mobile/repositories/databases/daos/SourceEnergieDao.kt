package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface SourceEnergieDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sourceEnergie: SourceEnergieModel)

    @Transaction
    @Query("SELECT * FROM source_energie WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<SourceEnergieModel>

    @Transaction
    @Query("DELETE FROM source_energie")
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