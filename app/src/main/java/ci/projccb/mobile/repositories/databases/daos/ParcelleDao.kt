package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ParcelleDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(parcelleModel: ParcelleModel)

    @Transaction
    @Query("SELECT * FROM parcelle WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<ParcelleModel>

    @Transaction
    @Query("SELECT * FROM parcelle WHERE (producteurId = :producteurId AND agentId = :agentID)")
    fun getParcellesProducteur(producteurId: String?, agentID: String?): MutableList<ParcelleModel>

    @Transaction
    @Query("SELECT * FROM parcelle WHERE id = :id")
    fun getParcelle(id: Int): LocaliteModel

    @Transaction
    @Query("SELECT * FROM parcelle WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<ParcelleModel>

    @Transaction
    @Query("UPDATE parcelle SET id = :id, isSynced = :synced, origin = 'remote', codeParc = :codeparc WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, codeparc: String, localID: Int)

    @Transaction
    @Query("SELECT * FROM parcelle WHERE (isSynced = 0 AND producteurId = :producteurUid AND origin = 'local' AND agentId = :agentId)")
    fun getParcellesUnSynchronizedLocal(producteurUid: String?, agentId: String?): MutableList<ParcelleModel>

    @Transaction
    @Query("DELETE FROM parcelle WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)
    @Transaction
    @Query("DELETE FROM parcelle")
    fun deleteAll()

    @Transaction
    @Query("DELETE FROM parcelle WHERE uid = :uid")
    fun deleteUid(uid: Long)

    @Transaction
    @Query("SELECT * FROM parcelle WHERE (isSynced = 0 AND producteurId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<ParcelleModel>

    @Transaction
    @Query("SELECT * FROM parcelle WHERE isSynced = 1 AND agentId = :agentID")
    fun getSyncedAll(agentID: String): MutableList<ParcelleModel>

}