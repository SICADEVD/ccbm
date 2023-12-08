package ci.projccb.mobile.models


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose


data class SousThemeFormationModel(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("id") @Expose var id: Int? = 0,
    @SerializedName("nom") @Expose var nom: String? = "",
    @SerializedName("agentId") @Expose var agentId: String? = "",
    @SerializedName("theme_formation_id") @Expose var themeFormationsId: Int? = 0
) {
    override fun toString(): String {
        return nom!!
    }
}
