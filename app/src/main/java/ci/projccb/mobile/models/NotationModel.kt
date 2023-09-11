package ci.projccb.mobile.models


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName = Constants.TABLE_NOTATION, indices = [Index(value = ["id"], unique = true)])
data class NotationModel(
    @Expose @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("id") @Expose var id: Int?,
    @SerializedName("nom") @Expose var nom: String? = "",
    @SerializedName("point") @Expose var point: Int? = 0,
) {
    override fun toString(): String {
        return nom!!
    }
}
