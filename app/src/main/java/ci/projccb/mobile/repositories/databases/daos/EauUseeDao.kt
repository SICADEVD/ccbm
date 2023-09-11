package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.EauUseeModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface EauUseeDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eauUsee: EauUseeModel)

    @Transaction
    @Query("SELECT * FROM eau_usee WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<EauUseeModel>

    @Transaction
    @Query("DELETE FROM eau_usee")
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