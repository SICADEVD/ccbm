package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface NationaliteDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nationalite: NationaliteModel)

    @Transaction
    @Query("SELECT * FROM nationalite WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<NationaliteModel>

    @Transaction
    @Query("DELETE FROM nationalite")
    fun deleteAll()
}