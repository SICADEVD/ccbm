package ci.projccb.mobile.models


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName = Constants.TABLE_FORMATIONS)
data class SuiviFormationModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    @Expose @SerializedName("id") var id: Int? = 0,
    @Expose @SerializedName("dateFormation")
    var dateFormation: String? = "",
    @Expose @SerializedName("lieu_formations_id")
    var lieuFormationsId: String? = "",
    @Expose @SerializedName("localites_id")
    var localitesId: String? = "",
    @Ignore @SerializedName("producteurs_id")
    var producteursId: List<String>? = listOf(),
    @SerializedName("theme")
    var theme: String? = "",
    @Expose @SerializedName("userid") var usersId: Int? = 0,
    @Expose(serialize = false, deserialize = false) var isSynced: Boolean = false,
)