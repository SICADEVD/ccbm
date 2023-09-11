package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ConcernesDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ConcernesModel: ConcernesModel)

    @Transaction
    @Query("SELECT * FROM concernes WHERE agentId = :agentID AND role = :roleName")
    fun getAll(agentID: String?, roleName: String?): MutableList<ConcernesModel>

    @Transaction
    @Query("SELECT * FROM concernes WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<ConcernesModel>

    @Transaction
    @Query("DELETE FROM concernes")
    fun deleteAll()
}

