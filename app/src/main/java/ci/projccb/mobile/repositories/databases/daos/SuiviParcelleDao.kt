package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface SuiviParcelleDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(suiviParcelleModel: SuiviParcelleModel)

    @Transaction
    @Query("SELECT * FROM suivi_parcelle WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<SuiviParcelleModel>

    @Transaction
    @Query("SELECT * FROM suivi_parcelle WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<SuiviParcelleModel>

    @Transaction
    @Query("UPDATE suivi_parcelle SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("SELECT * FROM suivi_parcelle WHERE (isSynced = 0 AND parcellesId = :parcelleUid AND origin = 'local' AND agentId = :agentId)")
    fun getSuiviParcellesUnSynchronizedLocal(parcelleUid: String?, agentId: String?): MutableList<SuiviParcelleModel>

    @Transaction
    @Query("DELETE FROM suivi_parcelle WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)
    @Transaction
    @Query("DELETE FROM suivi_parcelle")
    fun deleteAll()

    @Transaction
    @Query("DELETE FROM suivi_parcelle WHERE uid = :uid")
    fun deleteByUid(uid: Int)

    @Transaction
    @Query("SELECT * FROM suivi_parcelle WHERE (isSynced = 0 AND producteursId = :producteurUid)")
    fun getUnSyncedByProdAndParcUid(producteurUid: String?): MutableList<SuiviParcelleModel>

}