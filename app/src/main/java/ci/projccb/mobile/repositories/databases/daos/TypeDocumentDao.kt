package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface TypeDocumentDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(typeDocumentModel: TypeDocumentModel)

    @Transaction
    @Query("SELECT * FROM type_document WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<TypeDocumentModel>

    @Transaction
    @Query("DELETE FROM type_document")
    fun deleteAll()
}
