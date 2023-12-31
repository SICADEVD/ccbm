package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.AgentModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface AgentDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(agent: AgentModel)

    @Transaction
    @Query("SELECT * FROM agent WHERE id = :agentID")
    fun getAgent(agentID: Int?) : AgentModel?

    @Transaction
    @Query("SELECT * FROM agent")
    fun getAll(): MutableList<AgentModel>?

    @Transaction
    @Query("UPDATE agent SET isLogged = :status WHERE id = :agentId")
    fun logoutAgent(status: Boolean, agentId: Int?)
}