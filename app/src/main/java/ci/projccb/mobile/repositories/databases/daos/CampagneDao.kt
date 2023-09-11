package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.CampagneModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface CampagneDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(campagneModel: CampagneModel)

    @Transaction
    @Query("SELECT * FROM campagne")
    fun getAll(): MutableList<CampagneModel>

    @Transaction
    @Query("DELETE FROM campagne")
    fun deleteAll()
}
