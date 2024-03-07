package ci.projccb.mobile.models

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose

@Entity(tableName = Constants.TABLE_COOP, indices = [Index(value = ["id"], unique = true)])
data class CoopModel (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose val id: Int? = null,
    @Expose val codeCoop: String? = null,
    @Expose val name: String? = null,
    @Expose val email: String? = null,
    @Expose val latitude: String? = null,
    @Expose val longitude: String? = null,
)

@Dao
interface CoopDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(coopModel: CoopModel)

    @Transaction
    @Query("SELECT * FROM cooperative")
    fun getAll(): MutableList<CoopModel>?

    @Transaction
    @Query("DELETE FROM cooperative")
    fun deleteAll()
}