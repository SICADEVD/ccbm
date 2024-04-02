package ci.projccb.mobile.repositories.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface EnqueteSsrteDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(enqueteSsrtModel: EnqueteSsrtModel)

    @Transaction
    @Query("SELECT * FROM ssrte_clms")
    fun getAll(): MutableList<EnqueteSsrtModel>

    @Transaction
    @Query("SELECT * FROM ssrte_clms WHERE isSynced = 0")
    fun getUnSyncedAll(): MutableList<EnqueteSsrtModel>

    @Transaction
    @Query("UPDATE ssrte_clms SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM ssrte_clms")
    fun deleteAgentDatas()

    @Transaction
    @Query("SELECT * FROM ssrte_clms WHERE (isSynced = 0 AND producteursId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<EnqueteSsrtModel>
    @Transaction
    @Query("SELECT * FROM ssrte_clms WHERE isSynced = 0 AND userid = :agentID")
    fun getUnSyncedAllLive(agentID: String?): LiveData<MutableList<EnqueteSsrtModel>>
}
