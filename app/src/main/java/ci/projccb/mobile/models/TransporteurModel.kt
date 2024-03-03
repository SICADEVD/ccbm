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

@Entity(tableName = Constants.TABLE_TRANNSPORTEUR, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class TransporteurModel (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose
    @SerializedName(value = "cooperative_id", alternate = ["cooperatives_id"])
    val cooperativesId: Int? = null,
    @Expose val entreprise_id: Int? = null,
    @Expose
    @SerializedName("nom")
    val nom: String? = null,
    @Expose
    @SerializedName("prenoms")
    val prenoms: String? = null,
    @Expose
    @SerializedName("date_naiss")
    val date_naiss: String? = null,
    @Expose
    @SerializedName("phone1")
    val phone1: String? = null,
    @Expose
    @SerializedName("num_piece")
    val num_piece: String? = null,
    @Expose
    @SerializedName("num_permis")
    val num_permis: String? = null,
    @Expose val entreprise: String? = null,
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose @SerializedName("agentId") val agentId: String? = null,
):Parcelable{
    override fun toString(): String {
        return nom.toString()
    }
}

@Dao
interface TransporteurDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transporteurModel: TransporteurModel)

    @Transaction
    @Query("SELECT * FROM transporteurs")
    fun getAll(): MutableList<TransporteurModel>

    @Transaction
    @Query("SELECT * FROM transporteurs Where entreprise_id = :entrepriseId")
    fun getListByEntrpriseId(entrepriseId: Int): MutableList<TransporteurModel>

    @Transaction
    @Query("DELETE FROM transporteurs")
    fun deleteAll()

}
