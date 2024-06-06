package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ThemeFormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(theme: ThemeFormationModel)

    @Transaction
    @Query("SELECT * FROM theme_formation WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<ThemeFormationModel>

    @Transaction
    @Query("SELECT * FROM theme_formation WHERE typeFormationsId = :type")
    fun getAllByType(type: String): MutableList<ThemeFormationModel>

    @Transaction
    @Query("DELETE FROM theme_formation")
    fun deleteAll()
}
