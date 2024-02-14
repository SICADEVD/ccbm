package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface SuiviApplicationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(suiviApplicationModel: SuiviApplicationModel)

    @Transaction
    @Query("SELECT * FROM suivi_application")
    fun getAll(): MutableList<SuiviApplicationModel>

    @Transaction
    @Query("SELECT * FROM suivi_application WHERE isSynced = 0")
    fun getUnSyncedAll(): MutableList<SuiviApplicationModel>

    @Transaction
    @Query("UPDATE suivi_application SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM suivi_application")
    fun deleteAgentDatas()

    @Transaction
    @Query("DELETE FROM suivi_application WHERE uid = :uid")
    fun deleteByUid(uid: Int)

    @Transaction
    @Query("SELECT * FROM suivi_application WHERE (isSynced = 0 AND producteur = :producteurUid)")
    fun getUnSyncedByProdAndParcUid(producteurUid: String?): MutableList<SuiviApplicationModel>

    @Transaction
    @Query("SELECT * FROM suivi_application WHERE (isSynced = 0 AND parcelle_id = :parcUid)")
    fun getUnSyncedByParcUid(parcUid: String?): MutableList<SuiviApplicationModel>
}
