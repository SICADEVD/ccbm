package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface NotationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notationModel: NotationModel)

    @Transaction
    @Query("SELECT * FROM notation")
    fun getAll(): MutableList<NotationModel>

    @Transaction
    @Query("DELETE FROM notation")
    fun deleteAll()
}
