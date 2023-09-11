package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface RecuDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recuModel: RecuModel)

    @Transaction
    @Query("SELECT * FROM recu WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<RecuModel>

    @Transaction
    @Query("DELETE FROM recu")
    fun deleteAll()
}