package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface LivraisonDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(livraisonModel: LivraisonModel)

    @Transaction
    @Query("SELECT * FROM livraison WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<LivraisonModel>

    @Transaction
    @Query("SELECT * FROM Livraison WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<LivraisonModel>

    @Transaction
    @Query("UPDATE livraison SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM livraison WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM livraison")
    fun deleteAll()

    @Transaction
    @Query("SELECT * FROM livraison WHERE (isSynced = 0 AND producteursId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<LivraisonModel>

}