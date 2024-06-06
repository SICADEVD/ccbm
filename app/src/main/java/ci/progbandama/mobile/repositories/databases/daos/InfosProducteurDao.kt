package ci.progbandama.mobile.repositories.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface InfosProducteurDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(infosProducteurDTO: InfosProducteurDTO)

    @Transaction
    @Query("SELECT * FROM infos_producteur WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<InfosProducteurDTO>

    @Transaction
    @Query("SELECT * FROM infos_producteur WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<InfosProducteurDTO>

    @Transaction
    @Query("UPDATE infos_producteur SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM infos_producteur WHERE uid = :uid")
    fun deleteProducteurInfo(uid: Int?)

    @Transaction
    @Query("DELETE FROM infos_producteur WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("SELECT * FROM infos_producteur WHERE (isSynced = 0 AND producteursId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<InfosProducteurDTO>
    @Transaction
    @Query("SELECT * FROM infos_producteur WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAllLive(agentID: String?): LiveData<MutableList<InfosProducteurDTO>>
}
