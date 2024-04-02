package ci.projccb.mobile.repositories.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface FormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(formationModel: FormationModel)

    @Transaction
    @Query("SELECT * FROM formation WHERE agentId = :agentID ORDER BY dateFormation DESC")
    fun getAll(agentID: String?): MutableList<FormationModel>

    @Transaction
    @Query("SELECT * FROM formation WHERE isSynced = 0 AND id = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<FormationModel>

    @Transaction
    @Query("UPDATE formation SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM formation WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM formation")
    fun deleteAll()

    @Transaction
    @Query("DELETE FROM formation WHERE uid = :uId")
    fun deleteByUid(uId: Int)

    @Transaction
    @Query("SELECT * FROM formation WHERE isSynced = 1 AND agentId = :agentID")
    fun getSyncedAll(agentID: String): MutableList<FormationModel>
    @Transaction
    @Query("SELECT * FROM formation WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAllLive(agentID: String?): LiveData<MutableList<FormationModel>>
}