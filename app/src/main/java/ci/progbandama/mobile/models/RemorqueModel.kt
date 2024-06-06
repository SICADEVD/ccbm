package ci.progbandama.mobile.models

import android.os.Parcelable
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
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = Constants.TABLE_REMORQUE, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class RemorqueModel (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose
    @SerializedName(value = "cooperative_id", alternate = ["cooperatives_id"])
    val cooperativesId: String? = null,
    @Expose val remorque_immat: String? = null,
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose @SerializedName("agentId") val agentId: String? = null,
):Parcelable{
    override fun toString(): String {
        return remorque_immat.toString()
    }
}

@Dao
interface RemorqueDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(remorqueModel: RemorqueModel)

    @Transaction
    @Query("SELECT * FROM remorques")
    fun getAll(): MutableList<RemorqueModel>

    @Transaction
    @Query("DELETE FROM remorques")
    fun deleteAll()

}

