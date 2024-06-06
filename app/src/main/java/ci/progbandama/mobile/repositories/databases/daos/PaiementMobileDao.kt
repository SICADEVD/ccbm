package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface PaiementMobileDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(paiementMobileModel: PaiementMobileModel)

    @Transaction
    @Query("SELECT * FROM paiement_mobile")
    fun getAll(): MutableList<PaiementMobileModel>

    @Transaction
    @Query("DELETE FROM paiement_mobile")
    fun deleteAll()
}

