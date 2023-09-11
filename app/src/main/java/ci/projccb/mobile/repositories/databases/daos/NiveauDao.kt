package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface NiveauDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(niveau: NiveauModel)

    @Transaction
    @Query("SELECT * FROM niveau WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<NiveauModel>

    @Transaction
    @Query("DELETE FROM niveau")
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