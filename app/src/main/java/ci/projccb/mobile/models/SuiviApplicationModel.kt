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


@Entity(tableName = Constants.TABLE_SUIVI_APPLICATION,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class SuiviApplicationModel(
    @SerializedName(value = "applicateurs_id") @Expose var applicateursId: String? = "",
    @SerializedName("campagnes_id") @Expose var campagnesId: Int? = 0,
    @Expose var cultureNom: String? = "",
    @Expose var producteurNom: String? = "",
    @Expose var applicateurNom: String? = "",
    @Expose var localiteNom: String? = "",
    @Expose var campagneNom: String? = "",
    @SerializedName(value = "date_application", alternate = ["dateApplication"]) @Expose var dateApplication: String? = "",
    @SerializedName("degreDangerosite") @Expose var degreDangerosite: String? = "",
    @SerializedName("delaisReentree") @Expose var delaisReentree: String? = "",
    @SerializedName(value = "heure_application", alternate = ["heureApplication"]) @Expose var heureApplication: String? = "",
    @SerializedName(value = "heure_fin_application", alternate = ["heureFinApplication"]) @Expose var heureFinApplication: String? = "",
    @SerializedName("marqueProduitPulverise") @Expose var marqueProduitPulverise: String? = "",
    @SerializedName("matieresActivesStringify") @Expose var matieresActivesStringify: String? = "",
    @SerializedName("nomInsectesCiblesStringify") @Expose var nomInsectesCiblesStringify: String?  = "",
    @SerializedName(value = "parcelles_id") @Expose var parcellesId: String? = "",
    @SerializedName("photoDouche") @Expose var photoDouche: String? = "",
    @SerializedName("photoZoneTampons") @Expose var photoZoneTampons: String? = "",
    @SerializedName("presenceDouche") @Expose var presenceDouche: String? = "",
    @SerializedName("raisonApplication") @Expose var raisonApplication: String? = "",
    @SerializedName("superficiePulverisee") @Expose var superficiePulverisee: String? = "",
    @SerializedName("uid") @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @SerializedName("zoneTampons") @Expose var zoneTampons: String? = "",
    var photoTamponPath: String? = "",
    var photoDouchePath: String? = "",
    val isSynced: Boolean = false,
    @Expose var id: Int? = 0,
    var origin: String? = "local"
) : Parcelable {
    @Ignore @SerializedName("applicateur") @Expose var applicateursIds: Int? = null
    @Ignore @SerializedName("parcelle") @Expose var parcellesIds: Int? = null
    @Ignore @SerializedName("matieresActives") @Expose(serialize = true, deserialize = false) var matieresActives: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("nomInsectesCibles") @Expose(serialize = true, deserialize = false) var nomInsectesCibles: MutableList<String>? = mutableListOf()
}
