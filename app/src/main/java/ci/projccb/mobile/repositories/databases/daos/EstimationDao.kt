package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface EstimationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(estimationModel: EstimationModel)

    @Transaction
    @Query("SELECT * FROM estimation")
    fun getAll(): MutableList<EstimationModel>

    @Transaction
    @Query("SELECT * FROM estimation WHERE isSynced = 0")
    fun getUnSyncedAll(): MutableList<EstimationModel>

    @Transaction
    @Query("UPDATE estimation SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM estimation")
    fun deleteAgentDatas()
}
