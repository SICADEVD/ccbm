package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.CourEauModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface CourEauDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(courEau: CourEauModel)

    @Transaction
    @Query("SELECT * FROM cours_eau WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<CourEauModel>

    @Transaction
    @Query("DELETE FROM cours_eau")
    fun deleteAll()
}