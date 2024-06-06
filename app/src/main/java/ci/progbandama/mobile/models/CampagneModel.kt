package ci.progbandama.mobile.models


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

@Entity(tableName = Constants.TABLE_CAMPAGNE, indices = [Index(value = ["id"], unique = true)])
data class CampagneModel(
    @Expose @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("campagnes_nom", alternate = ["nom"]) @Expose var campagnesNom: String? = "",
    @SerializedName("id")
    @Expose
    var id: Int? = 0
) {
    override fun toString(): String {
        return campagnesNom!!
    }
}
