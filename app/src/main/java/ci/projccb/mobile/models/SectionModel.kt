package ci.projccb.mobile.models

import android.annotation.SuppressLint
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

@Entity(tableName = Constants.TABLE_SECTION, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class SectionModel(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose @SerializedName("id") val id: Int? = null,
    @Expose
    @SerializedName(value = "cooperative_id", alternate = ["cooperatives_id"])
    val cooperativesId: Int? = null,
    @Expose
    @SerializedName("libelle")
    val libelle: String? = null,
    @Expose @SerializedName("agentId") val agentId: String? = null,
):Parcelable {

}

@Dao
interface SectionsDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sectionModel: SectionModel)

    @Transaction
    @Query("SELECT * FROM sections WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<SectionModel>

    @Transaction
    @Query("DELETE FROM sections")
    fun deleteAll()

    @Transaction
    @Query("SELECT * FROM sections WHERE id = :sectionId")
    fun getById(sectionId: String?): SectionModel
}
