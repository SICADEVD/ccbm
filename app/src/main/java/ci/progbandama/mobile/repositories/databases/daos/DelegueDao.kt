package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface DelegueDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(delegueModel: DelegueModel)

    @Transaction
    @Query("SELECT * FROM delegue WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<DelegueModel>

    @Transaction
    @Query("SELECT * FROM delegue WHERE id = :id")
    fun getDelegueById(id: Int?): DelegueModel

    @Transaction
    @Query("DELETE FROM delegue")
    fun deleteAll()
}

