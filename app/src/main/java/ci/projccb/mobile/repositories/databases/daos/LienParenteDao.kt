package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.LienParenteModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface LienParenteDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lienParenteModel: LienParenteModel)

    @Transaction
    @Query("SELECT * FROM lien_parente WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<LienParenteModel>

    @Transaction
    @Query("DELETE FROM lien_parente")
    fun deleteAll()
}
