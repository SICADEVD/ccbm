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

@Entity(tableName = Constants.TABLE_FORM_STAFF, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class StaffFormationModel (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose
    @SerializedName(value = "cooperative_id", alternate = ["cooperatives_id"])
    val cooperativesId: Int? = null,
    @Expose
    @SerializedName("firstname")
    val firstname: String? = null,
    @Expose
    @SerializedName("lastname")
    val lastname: String? = null,
    @Expose
    @SerializedName("username")
    val username: String? = null,
    @Expose
    @SerializedName("email")
    val email: String? = null,
    @Expose
    @SerializedName("adresse")
    val adresse: String? = null,
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose
    @SerializedName(value = "mobile", alternate = ["phone1"])
    val mobile: String? = null,
    @Expose
    @SerializedName("role")
    val role: String? = null,
    @Expose @SerializedName("agentId") val agentId: String? = null,
):Parcelable

@Dao
interface StaffFormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(staffFormationModel: StaffFormationModel)

    @Transaction
    @Query("SELECT * FROM staff_formation WHERE agentId = :agentID AND role = :roleName")
    fun getAll(agentID: String?, roleName: String?): MutableList<StaffFormationModel>

    @Transaction
    @Query("SELECT * FROM staff_formation WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<StaffFormationModel>

    @Transaction
    @Query("SELECT * FROM staff_formation WHERE id = :id")
    fun getStaffFormationById(id: Int?): StaffFormationModel

    @Transaction
    @Query("DELETE FROM staff_formation")
    fun deleteAll()
}
