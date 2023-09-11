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


@Entity(
    tableName = Constants.TABLE_FORMATIONS,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class FormationModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @Expose var id: Int? = 0,
    @Expose @SerializedName(value = "date_formation", alternate = ["dateFormation"])
    var dateFormation: String? = "",
    @Expose @SerializedName(value = "lieu_formation", alternate = ["lieu_formations_id"]) var lieuFormationsId: String? = "",
    @Expose @SerializedName(value = "type_formation", alternate = ["type_formations_id"]) var typeFormationId: String? = "",
    @SerializedName(value = "localite", alternate = ["localites_id"]) @Expose var localitesId: String? = "",
    @Expose var producteursIdStringify: String? = "",
    @Expose var producteursNomStringify: String? = "",
    @Expose var producteursStringify: String? = "",
    @Expose var themesLabelStringify: String? = "",
    @Expose var themeStringify: String? = "",
    @Expose @SerializedName(value = "staff", alternate = ["userid"]) var usersId: Int? = 0,
    @Expose @SerializedName("campagnes_id") var campagneId: Int? = 0,
    @Expose @SerializedName("photo_formations") var photoFormation: String? = "",
    @Expose @SerializedName("visiteur")  var visiteurs: String? = null,
    var photoPath: String? = null,
    @Expose var themeNom: String? = null,
    @Expose var campagneNom: String? = null,
    @Expose var lieuFormationNom: String? = null,
    @Expose var localiteNom: String? = null,
    var isSynced: Boolean = false,
    var origin: String? = "local"
) : Parcelable {
    @Expose(serialize = true, deserialize = false) @SerializedName("producteur") @Ignore var producteursId: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @SerializedName("visiteurs") @Ignore var visiteursNom: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @SerializedName("producteurs") @Ignore var producteursNom: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @SerializedName("themesLabel") @Ignore var themesLabel: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @SerializedName("theme") @Ignore var themeIds: MutableList<String>? = null
}
