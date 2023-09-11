package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.TypeFormationModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface TypeFormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(typeFormationModel: TypeFormationModel)

    @Transaction
    @Query("SELECT * FROM type_formation WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<TypeFormationModel>

    @Transaction
    @Query("DELETE FROM type_formation")
    fun deleteAll()
}
