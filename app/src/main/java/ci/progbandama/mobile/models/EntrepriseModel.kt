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

@Entity(tableName = Constants.TABLE_ENTREPRISE, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class EntrepriseModel (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose
    @SerializedName(value = "cooperative_id", alternate = ["cooperatives_id"])
    val cooperativesId: Int? = null,
    @Expose val nom: String? = null,
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose @SerializedName("agentId") val agentId: String? = null,
):Parcelable{

}

@Dao
interface EntrepriseDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entrepriseModel: EntrepriseModel)

    @Transaction
    @Query("SELECT * FROM entreprises")
    fun getAll(): MutableList<EntrepriseModel>

    @Transaction
    @Query("DELETE FROM entreprises")
    fun deleteAll()

}

