package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.ProducteurModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ProducteurDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(producteurModel: ProducteurModel)

    @Transaction
    @Query("SELECT * FROM producteur WHERE id = :producteurID")
    fun getProducteur(producteurID: Int?) : ProducteurModel

    @Transaction
    @Query("SELECT * FROM producteur WHERE uid = :producteurUID")
    fun getProducteurByUID(producteurUID: Int?) : ProducteurModel

    @Transaction
    @Query("SELECT * FROM producteur WHERE id = :producteurID")
    fun getProducteurByID(producteurID: Int?) : ProducteurModel

    @Transaction
    @Query("SELECT * FROM producteur WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("SELECT * FROM producteur WHERE localitesId = :localite")
    fun getProducteursByLocalite(localite: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("UPDATE producteur SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("UPDATE producteur SET isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncDataOnExist(synced: Int, localID: Int)

    @Transaction
    @Query("SELECT * FROM producteur WHERE (isSynced = 0 AND localitesId = :localiteUid AND origin = 'local' AND agentId = :agentId)")
    fun getProducteursUnSynchronizedLocal(localiteUid: String?, agentId: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("SELECT * FROM producteur WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("DELETE FROM producteur WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM producteur")
    fun deleteAll()
}