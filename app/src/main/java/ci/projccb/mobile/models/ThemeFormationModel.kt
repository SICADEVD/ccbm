package ci.projccb.mobile.models


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose


@Entity(tableName = Constants.TABLE_THEMES_FORMATION, indices = [Index(value = ["id"], unique = true)])
data class ThemeFormationModel(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("chapitre") @Expose var chapitre: String? = "",
    @SerializedName("id") @Expose var id: Int? = 0,
    @SerializedName("nom") @Expose var nom: String? = "",
    @SerializedName("agentId") @Expose var agentId: String? = "",
    @SerializedName("type_formation_id") @Expose var typeFormationsId: Int? = 0
) {
    override fun toString(): String {
        return nom!!
    }
}
