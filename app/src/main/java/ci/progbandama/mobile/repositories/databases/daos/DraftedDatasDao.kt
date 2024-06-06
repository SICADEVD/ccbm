package ci.progbandama.mobile.repositories.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ci.progbandama.mobile.models.DataDraftedModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface DraftedDatasDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(draftedModel: DataDraftedModel)

    @Transaction
    @Query("SELECT * FROM drafted_datas WHERE (agentId = :agentID AND draftCompleted = 0)")
    fun getAll(agentID: String?): MutableList<DataDraftedModel>

    @Transaction
    @Query("SELECT * FROM drafted_datas WHERE (agentId = :agentID AND draftCompleted = 0 AND typeDraft like :typeDraft)")
    fun getAllByType(agentID: String?, typeDraft: String): MutableList<DataDraftedModel>


    @Transaction
    @Query("UPDATE drafted_datas SET draftCompleted = 1 WHERE uid = :draftUID")
    fun completeDraft(draftUID: Int)


    @Transaction
    @Query("SELECT * FROM drafted_datas WHERE (uid = :draftID AND draftCompleted = 0)")
    fun getDraftedDataByID(draftID: Int) : DataDraftedModel?


    @Transaction
    @Query("SELECT COUNT(*) FROM drafted_datas WHERE (agentId = :agentID AND typeDraft = :type AND draftCompleted = 0)")
    fun countByType(agentID: String, type:String) : Int?

    @Transaction
    @Query("SELECT COUNT(*) FROM drafted_datas WHERE (agentId = :agentID AND typeDraft = :type AND draftCompleted = 0)")
    fun countByTypeLive(agentID: String, type:String) : LiveData<Int?>

    @Transaction
    @Query("SELECT * FROM drafted_datas WHERE (agentId = :agentID AND draftCompleted = 0) ORDER BY uid DESC")
    fun getAllLive(agentID: String) : LiveData<MutableList<DataDraftedModel>>

    @Transaction
    @Query("DELETE FROM drafted_datas")
    fun deleteAll()
}
