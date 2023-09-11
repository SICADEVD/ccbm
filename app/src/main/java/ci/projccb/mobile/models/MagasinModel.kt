package ci.projccb.mobile.models


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose


@Entity(tableName = Constants.TABLE_MAGASIN, indices = [Index(value = ["id"], unique = true)])
data class MagasinModel(
    @Expose @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("id") @Expose var id: Int? = 0,
    @SerializedName("status") @Expose var status: Int? = 0,
    @SerializedName("staff_id") @Expose var staffId: Int? = 0,
    @SerializedName("code") @Expose var codeMagasinsections: String? = null,
    @SerializedName("nom") @Expose var nomMagasinsections: String? = "",
    @SerializedName("phone") @Expose var phone: String? = "",
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("adresse") @Expose var adresse: String? = "",
) {
    override fun toString(): String {
        return nomMagasinsections.toString()
    }
}
