package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface OperateurDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(operateurModel: OperateurModel)

    @Transaction
    @Query("SELECT * FROM operateur WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<OperateurModel>

    @Transaction
    @Query("DELETE FROM operateur")
    fun deleteAll()
}
