package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface TypeMachineDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(typeMachine: TypeMachineModel)

    @Transaction
    @Query("SELECT * FROM type_machine WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<TypeMachineModel>

    @Transaction
    @Query("DELETE FROM type_machine")
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