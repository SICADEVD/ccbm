package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface InspectionDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inspectionDTO: InspectionDTO)

    @Transaction
    @Query("SELECT * FROM inspection WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<InspectionDTO>

    @Transaction
    @Query("SELECT * FROM inspection WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<InspectionDTO>

    @Transaction
    @Query("UPDATE inspection SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM inspection WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("SELECT * FROM inspection WHERE (isSynced = 0 AND producteursId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<InspectionDTO>
}
