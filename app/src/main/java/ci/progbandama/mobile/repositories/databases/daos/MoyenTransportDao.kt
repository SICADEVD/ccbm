package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface MoyenTransportDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(moyenTransportModel: MoyenTransportModel)

    @Transaction
    @Query("SELECT * FROM moyens_transport WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<MoyenTransportModel>

    @Transaction
    @Query("DELETE FROM moyens_transport")
    fun deleteAll()
}
