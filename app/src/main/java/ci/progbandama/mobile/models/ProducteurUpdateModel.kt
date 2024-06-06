package ci.progbandama.mobile.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProducteurUpdateModel(
    @SerializedName("certificat") @Expose var certificat: String? = "",
    @SerializedName("codeProd") @Expose var codeProd: String? = "",
    @SerializedName("codeProdapp") @Expose var codeProdapp: String? = "",
    @SerializedName("consentement") @Expose var consentement: String? = "",
    @SerializedName("copiecarterecto") @Expose var copiecarterecto: String? = "",
    @SerializedName("copiecarteverso") @Expose var copiecarteverso: String? = "",
    @SerializedName("created_at") @Expose var createdAt: String? = "",
    @SerializedName("dateNaiss") @Expose var dateNaiss: String? = "",
    @SerializedName("esignature") @Expose var esignature: String? = "",
    @SerializedName("id") @Expose var id: Int? = 0,
    @SerializedName("localites_id") @Expose var localitesId: Int? = 0,
    @SerializedName("nationalites_id") @Expose var nationalitesId: String? = "",
    @SerializedName("niveaux_id") @Expose var niveauxId: String? = "",
    @SerializedName("nom") @Expose var nom: String? = "",
    @SerializedName("numPiece") @Expose var numPiece: String? = "",
    @SerializedName("phone1") @Expose var phone1: String? = "",
    @SerializedName("phone2") @Expose var phone2: String? = "",
    @SerializedName("picture") @Expose var picture: String? = "",
    @SerializedName("prenoms") @Expose var prenoms: String? = "",
    @SerializedName("sexe") @Expose var sexe: String? = "",
    @SerializedName("statut") @Expose var statut: String? = "",
    @SerializedName("type_pieces_id") @Expose var typePiecesId: String? = "",
    @SerializedName("updated_at") @Expose var updatedAt: String? = "",
    @SerializedName("userid") @Expose var userid: Int? = 0
) : Parcelable {
    override fun toString(): String {
        return "$nom $prenoms".trim()
    }
}
