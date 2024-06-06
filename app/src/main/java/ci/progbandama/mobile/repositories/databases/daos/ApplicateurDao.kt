package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.ApplicateurModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ApplicateurDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(applicateurModel: ApplicateurModel)

    @Transaction
    @Query("SELECT * FROM applicateur")
    fun getAll(): MutableList<ApplicateurModel>

    @Transaction
    @Query("DELETE FROM applicateur")
    fun deleteAll()
}
