package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface PersonneBlesseeDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(personneBlessee: PersonneBlesseeModel)

    @Transaction
    @Query("SELECT * FROM personne_blessee WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<PersonneBlesseeModel>

    @Transaction
    @Query("DELETE FROM personne_blessee")
    fun deleteAll()
}

