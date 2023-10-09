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

@Entity(tableName = Constants.TABLE_PROGRAMME, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class ProgrammeModel(
     @PrimaryKey(autoGenerate = true) val uid: Int? = null,
     @Expose @SerializedName("id") val id: Int? = null,
     @Expose
     @SerializedName("libelle")
     val libelle: String? = null,
     @Expose
     @SerializedName("agentId") val agentId: String? = null,
    ):Parcelable {

}

@Dao
interface ProgrammesDao {

     @Transaction
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(programmeModel: ProgrammeModel)

     @Transaction
     @Query("SELECT * FROM programmes WHERE agentId = :agentID")
     fun getAll(agentID: String?): MutableList<ProgrammeModel>

     @Transaction
     @Query("DELETE FROM programmes")
     fun deleteAll()
}
