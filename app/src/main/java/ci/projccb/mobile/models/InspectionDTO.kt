package ci.projccb.mobile.models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = Constants.TABLE_INSPECTIONS,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class InspectionDTO(
    @Expose val id: Int? = 0,
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @SerializedName("campagne_id") @Expose var campagnesId: String? = "",
    @SerializedName("campagnes_label") @Expose var campagnesLabel: String? = "",
    @Expose @SerializedName("localite") var localiteId: String? = "",
    @Expose var section: String? = "",
    @Expose var localiteNom: String? = "",
    @SerializedName("date_evaluation") @Expose var dateEvaluation: String? = "",
    @SerializedName("formateur_id") @Expose var formateursId: String? = "",
    @SerializedName("producteur") @Expose var producteursId: String? = "",
    @SerializedName("producteurs_nom") @Expose var producteurNomPrenoms: String? = "",
    @SerializedName("note") @Expose var noteInspection: String? = "",
    @SerializedName("encadreur") @Expose var encadreur: String? = "",
    @Expose var production: String? = "",
    @Expose var certificatStr: String? = "",
    @Expose var total_question: String? = "",
    @Expose var total_question_conforme: String? = "",
    @Expose var total_question_non_conforme: String? = "",
    @Expose var total_question_non_applicable: String? = "",
    @SerializedName("reponseStringify") @Expose var reponseStringify: String? = "",
    @SerializedName("userid") @Expose var userid: Int? = 0,
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable {
    @SerializedName("reponse") @Expose(serialize = true, deserialize = false) @Ignore var reponse: MutableMap<String, String> = mutableMapOf()
    @SerializedName("certificat") @Expose(serialize = true, deserialize = false) @Ignore var certificatList: MutableList<String> = mutableListOf()
}
