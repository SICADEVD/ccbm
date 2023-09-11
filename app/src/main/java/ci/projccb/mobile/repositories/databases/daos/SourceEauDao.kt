package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface SourceEauDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sourceEau: SourceEauModel)

    @Transaction
    @Query("SELECT * FROM source_eau WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<SourceEauModel>

    @Transaction
    @Query("DELETE FROM source_eau")
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