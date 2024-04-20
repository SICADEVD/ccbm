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
    @SerializedName("parcelle") @Expose var parcelle: String? = "",
    @SerializedName(value = "producteur", alternate = ["producteur_id"]) @Expose var producteursId: String? = "",
    @SerializedName("producteurs_nom") @Expose var producteurNomPrenoms: String? = "",
    @SerializedName("parcelle_nom") @Expose var parcelleLib: String? = "",
    @SerializedName("note") @Expose var noteInspection: String? = "",
    @SerializedName("encadreur") @Expose var encadreur: String? = "",
    @Expose var production: String? = "",
    @Expose var certificatStr: String? = "",
    @Expose var total_question: String? = "",
    @Expose var total_question_conforme: String? = "",
    @Expose var total_question_non_conforme: String? = "",
    @Expose var total_question_non_applicable: String? = "",
    @Expose var reponse_non_conformeStr: String? = "",
    @Expose var reponse_non_applicaleStr: String? = "",
    @Expose var status: String? = "1",
    @Expose var approbation: String? = null,
    var update_content: String? = null,
    @SerializedName("reponseStringify") @Expose var reponseStringify: String? = "",
    @SerializedName("commentaireStringify") @Expose var commentaireStringify: String? = "",
    @SerializedName("userid") @Expose var userid: Int? = 0,
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable {
    @SerializedName("reponse") @Expose(serialize = true, deserialize = false) @Ignore var reponse: MutableMap<String, String> = mutableMapOf()
    @SerializedName("commentaire") @Expose(serialize = true, deserialize = false) @Ignore var commentaire: MutableMap<String, String> = mutableMapOf()
    @SerializedName("certificat") @Expose(serialize = true, deserialize = false) @Ignore var certificatList: MutableList<String> = mutableListOf()
    @SerializedName("reponse_non_conforme") @Expose(serialize = true, deserialize = false) @Ignore var reponse_non_conformeList: MutableList<QuestionnaireNoteModel>? = null
    @SerializedName("reponse_non_applicale") @Expose(serialize = true, deserialize = false) @Ignore var reponse_non_applicaleList: MutableList<QuestionnaireNoteModel>? = null
}

@Parcelize
data class InspectionDTOExt(
    @Expose val id: Int? = 0,
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @SerializedName("campagne_id") @Expose var campagnesId: String? = "",
    @SerializedName("campagnes_label") @Expose var campagnesLabel: String? = "",
    @Expose @SerializedName("localite") var localiteId: String? = "",
    @Expose var section: String? = "",
    @Expose var localiteNom: String? = "",
    @SerializedName("date_evaluation") @Expose var dateEvaluation: String? = "",
    @SerializedName("formateur_id") @Expose var formateursId: String? = "",
    @SerializedName(value = "producteur", alternate = ["producteur_id"]) @Expose var producteursId: String? = "",
    @SerializedName("producteurs_nom") @Expose var producteurNomPrenoms: String? = "",
    @SerializedName("note") @Expose var noteInspection: String? = "",
    @SerializedName("encadreur") @Expose var encadreur: String? = "",
    @Expose var production: String? = "",
    @Expose @SerializedName(value = "certificat", alternate = ["certificatStr"]) var certificat: String? = "",
    @Expose var total_question: String? = "",
    @Expose var total_question_conforme: String? = "",
    @Expose var total_question_non_conforme: String? = "",
    @Expose var total_question_non_applicable: String? = "",
    @Expose var reponse_non_conformeStr: String? = "",
    @Expose var reponse_non_applicaleStr: String? = "",
    @Expose var status: String? = "1",
    @Expose var approbation: String? = null,
    @Expose @SerializedName("reponse_non_conforme") val reponseNonConforme: MutableList<QuestionnaireNoteModel>,
    @Expose @SerializedName("reponse_non_applicale") val reponseNonApplicale: MutableList<QuestionnaireNoteModel>,
    @SerializedName("reponseStringify") @Expose var reponseStringify: String? = "",
    @SerializedName("commentaireStringify") @Expose var commentaireStringify: String? = "",
    @SerializedName("userid") @Expose var userid: Int? = 0,
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable {}

@Parcelize
data class InspectionUpdateDTO(
    @Expose @SerializedName("id") val id: String? = null,
    @Expose @SerializedName("reponse_non_conforme") val nonConformingResponse: NonConformingResponse? = null,
    @Expose @SerializedName("approbation") val approbation: Int? = null
): Parcelable

@Parcelize
data class NonConformingResponse(
    @Expose @SerializedName("id") val id: String? = null,
    @Expose @SerializedName("recommandations") val recommandations: Map<String, String> = mutableMapOf(),
    @Expose @SerializedName("delai") val delai: Map<String, String> = mutableMapOf(),
    @Expose @SerializedName("date_verification") val dateVerification: Map<String, String> = mutableMapOf(),
    @Expose @SerializedName("statuts") val statuts: Map<String, String> = mutableMapOf()
): Parcelable

@Parcelize
data class QuestionnaireNoteModel(
    @Expose val id: String? = null,
    @Expose val questionnaire_id: String? = null,
): Parcelable {

}
