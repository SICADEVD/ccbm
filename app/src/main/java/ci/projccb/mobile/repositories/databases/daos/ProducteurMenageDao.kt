package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ProducteurMenageDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(producteurMenageModel: ProducteurMenageModel)

    @Transaction
    @Query("SELECT * FROM menage WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<ProducteurMenageModel>

    @Transaction
    @Query("SELECT * FROM menage WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<ProducteurMenageModel>

    @Transaction
    @Query("UPDATE menage SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)



    @Transaction
    @Query("SELECT * FROM menage WHERE (isSynced = 0 AND producteurs_id = :producteurUid AND origin = 'local' AND agentId = :agentId)")
    fun getMenagesUnSynchronizedLocal(producteurUid: String?, agentId: String?): MutableList<ProducteurMenageModel>

    @Transaction
    @Query("DELETE FROM menage WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM menage WHERE uid = :uId")
    fun deleteUid(uId: String?)
}