package ci.projccb.mobile.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = Constants.TABLE_PARCELLES,
    indices = [
        Index(
            value = ["uid"], unique = true
        )
    ]
)
@Parcelize
data class ParcelleModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    @Expose var id: Int? = 0,
    @Expose @SerializedName(value="producteur", alternate = ["producteur_id"]) var producteurId: String? = "",
    @Expose var producteurNom: String? = "",
    @Expose var anneeCreation: String? = "",
    @Expose var localiteNom: String? = "",
    @Expose var culture: String? = "",
    @Expose var codeParc: String? = "",
    @Expose var wayPointsString: String? = "",
    @Expose var perimeter: String? = "",
    @Expose var typedeclaration: String? = "",
    @Expose var superficie: String? = "",
    @Expose var latitude: String? = "",
    @Expose var longitude: String? = "",
    @Expose var nom: String? = "",
    @Expose var prenoms: String? = "",
    var status: Boolean = false,
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") val agentId: String? = "",
    var origin: String? = "local"
) : Parcelable {

    @Ignore @Expose(serialize = true, deserialize = false) @SerializedName("waypoints") var mappingPoints: MutableList<String> = mutableListOf()


    override fun toString(): String {
        return "$culture ($anneeCreation)"
    }

}
