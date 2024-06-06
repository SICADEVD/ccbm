package ci.progbandama.mobile.repositories.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface InspectionDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inspectionDTO: InspectionDTO)

    @Transaction
    @Query("SELECT * FROM inspection WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<InspectionDTO>

    @Transaction
    @Query("SELECT * FROM inspection WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<InspectionDTO>

    @Transaction
    @Query("UPDATE inspection SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM inspection WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("SELECT * FROM inspection WHERE (isSynced = 0 AND producteursId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<InspectionDTO>

    @Transaction
    @Query("SELECT * FROM inspection WHERE (isSynced = 1 AND ( (reponse_non_conformeStr IS NOT '' AND reponse_non_conformeStr IS NOT '[]') ) ) ORDER BY uid DESC")
    fun getAllNConformeOrNApplicableSync(): MutableList<InspectionDTO>

    @Transaction
    @Query("DELETE FROM inspection WHERE uid = :uid")
    fun deleteByUid(uid: Int)

    @Transaction
    @Query("UPDATE inspection SET reponse_non_conformeStr = :no_conforme, reponse_non_applicaleStr = :no_applicable WHERE uid = :uid")
    fun updateNConformNApplicable(uid: Int, no_conforme: String?, no_applicable:String?)

    @Transaction
    @Query("UPDATE inspection SET approbation = :approbation WHERE uid = :uid")
    fun updateApprobation(approbation: String?, uid:Int?)

    @Transaction
    @Query("UPDATE inspection SET update_content = :content WHERE uid = :uid")
    fun updateContent(content: String?, uid:Int?)

    @Transaction
    @Query("SELECT * FROM inspection WHERE uid = :inspectUid")
    fun getByUid(inspectUid: Int): MutableList<InspectionDTO>
    @Transaction
    @Query("SELECT * FROM inspection WHERE id = :inspectId")
    fun getById(inspectId: Int): InspectionDTO

    @Transaction
    @Query("SELECT * FROM inspection WHERE (update_content IS NOT NULL AND isSynced = 1)")
    fun getAllInspectionToUpdate(): MutableList<InspectionDTO>

    @Transaction
    @Query("DELETE FROM inspection")
    fun deleteAll()
    @Transaction
    @Query("SELECT * FROM inspection WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAllLive(agentID: String?): LiveData<MutableList<InspectionDTO>>
}
