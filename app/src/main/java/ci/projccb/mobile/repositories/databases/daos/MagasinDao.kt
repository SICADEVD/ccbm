package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface MagasinDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(magasinModel: MagasinModel)

    @Transaction
    @Query("SELECT * FROM magasin")
    fun getAll(): MutableList<MagasinModel>

    @Transaction
    @Query("SELECT * FROM magasin where staffId = :concernes")
    fun getConcerneeMagasins(concernes: Int): MutableList<MagasinModel>

    @Transaction
    @Query("SELECT * FROM magasin where section_id != 0")
    fun getMagasinsSections(): MutableList<MagasinModel>

    @Transaction
    @Query("SELECT * FROM magasin where cooperative_id != 0")
    fun getMagasinsCentraux(): MutableList<MagasinModel>

    @Transaction
    @Query("DELETE FROM magasin")
    fun deleteAll()
}
