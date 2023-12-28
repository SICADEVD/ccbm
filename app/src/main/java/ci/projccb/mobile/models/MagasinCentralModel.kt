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
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose


@Entity(tableName = Constants.TABLE_MAGASIN_CENTRAL, indices = [Index(value = ["id"], unique = true)])
data class MagasinCentralModel(
    @Expose @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("id") @Expose var id: Int? = 0,
    @SerializedName("status") @Expose var status: Int? = 0,
    @SerializedName("cooperative_id") @Expose var cooperative_id: Int? = 0,
    @SerializedName("section_id") @Expose var section_id: Int? = 0,
    @SerializedName("staff_id") @Expose var staffId: Int? = 0,
    @SerializedName("code") @Expose var codeMagasinsections: String? = null,
    @SerializedName("nom") @Expose var nomMagasinsections: String? = "",
    @SerializedName("phone") @Expose var phone: String? = "",
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("adresse") @Expose var adresse: String? = "",
    @SerializedName("longitude") @Expose var longitude: String? = "",
    @SerializedName("latitude") @Expose var latitude: String? = "",
) {
    override fun toString(): String {
        return nomMagasinsections.toString()
    }
}

@Dao
interface MagasinCentralDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(magasinCentralModel: MagasinCentralModel)

    @Transaction
    @Query("SELECT * FROM magasin_central")
    fun getAll(): MutableList<MagasinCentralModel>

    @Transaction
    @Query("SELECT * FROM magasin_central where staffId = :concernes")
    fun getConcerneeMagasins(concernes: Int): MutableList<MagasinCentralModel>

    @Transaction
    @Query("SELECT * FROM magasin_central where section_id != 0")
    fun getMagasinsSections(): MutableList<MagasinCentralModel>

    @Transaction
    @Query("SELECT * FROM magasin_central where cooperative_id != 0")
    fun getMagasinsCentraux(): MutableList<MagasinCentralModel>

    @Transaction
    @Query("DELETE FROM magasin_central")
    fun deleteAll()
}
