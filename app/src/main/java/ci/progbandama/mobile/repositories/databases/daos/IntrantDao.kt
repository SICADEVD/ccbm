package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.IntrantModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface IntrantDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(intrant: IntrantModel)

    @Transaction
    @Query("SELECT * FROM intrant WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<IntrantModel>

    @Transaction
    @Query("DELETE FROM intrant")
    fun deleteAll()
}