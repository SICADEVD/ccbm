package ci.progbandama.mobile.models

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.Expose

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Entity(tableName = Constants.TABLE_APPROVISION, indices = [Index(value = ["uid"], unique = true)])
data class ApprovisionnementModel (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @Expose val id: Int?,
    @Expose val agroapprovisionnement_section_id: String? = null,
    @Expose val agroespecesarbre_id: String? = null,
    @Expose val total: String? = null,
    @Expose val total_restant: String? = null,
    @Expose val section_id: String? = null,
    @Expose val campagne_id: String? = null,
    @Expose val bon_livraison: String? = null,
    @Expose val agentId: String? = "0"
) {
    override fun toString(): String {
        return total!!
    }
}

@Dao
interface ApprovisionnementDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ApprovisionnementModel: ApprovisionnementModel)

    @Transaction
    @Query("SELECT * FROM approvisionnement WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<ApprovisionnementModel>

    @Transaction
    @Query("DELETE FROM approvisionnement")
    fun deleteAll()


    @Transaction
    @Query("SELECT * FROM approvisionnement WHERE section_id = :section_id")
    fun getApproBySect(section_id: Int?): MutableList<ApprovisionnementModel>
}
