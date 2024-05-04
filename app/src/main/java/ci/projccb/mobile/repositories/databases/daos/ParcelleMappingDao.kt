package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.ParcelleMappingModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ParcelleMappingDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(parcelleMappingModel: ParcelleMappingModel)

    @Transaction
    @Query("SELECT * FROM parcelle_mapping ORDER BY uid DESC LIMIT 8")
    fun getParcellesMappingList(): MutableList<ParcelleMappingModel>

    @Transaction
    @Query("SELECT * FROM parcelle_mapping WHERE producteurId = :producteurID ORDER BY uid DESC")
    fun getProducteurParcellesList(producteurID: String?): MutableList<ParcelleMappingModel>

    @Transaction
    @Query("DELETE FROM parcelle_mapping")
    fun deleteAll()
}
