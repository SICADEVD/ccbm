package ci.projccb.mobile.models

import android.os.Parcelable
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
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = Constants.TABLE_VEHICULE, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class VehiculeModel (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose
    @SerializedName(value = "cooperative_id", alternate = ["cooperatives_id"])
    val cooperativesId: Int? = null,
    @Expose val marque_id: Int? = null,
    @Expose val marque: String? = null,
    @Expose val vehicule_immat: Int? = null,
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose @SerializedName("agentId") val agentId: String? = null,
):Parcelable{
    override fun toString(): String {
        return vehicule_immat.toString()
    }
}

@Dao
interface VehiculeDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehiculeModel: VehiculeModel)

    @Transaction
    @Query("SELECT * FROM vehicules")
    fun getAll(): MutableList<VehiculeModel>

    @Transaction
    @Query("DELETE FROM vehicules")
    fun deleteAll()

}

