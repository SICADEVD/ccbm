package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface TypeLocaliteDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(typeLocalite: TypeLocaliteModel)

    @Transaction
    @Query("SELECT * FROM type_localite WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<TypeLocaliteModel>

    @Transaction
    @Query("DELETE FROM type_localite")
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