package ci.progbandama.mobile.models

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LivraisonSousModel(
    @SerializedName("producteur") @Expose var producteurId: String? = "",
    @SerializedName("producteur_name") @Expose var producteurIdName: String? = "",
    @SerializedName("parcelle") @Expose var parcelleId: String? = "",
    @SerializedName("parcelle_name") @Expose var parcelleIdName: String? = "",
    @SerializedName("type") @Expose var typeName: String? = "",
    @SerializedName("certificat") @Expose var certificat: String? = "",
    @SerializedName("quantity") @Expose var quantityNb: Int? = 0,
    @SerializedName("amount") @Expose var amountNb: Int? = null,
    @SerializedName("scelleStringify") @Expose var numScelle: String? = null,
): Parcelable {
    @Ignore @SerializedName("scelle") @Expose(serialize = true, deserialize = false) var scelleList: MutableList<String>? = null
}
@Parcelize
data class LivraisonCentralSousModel(
    @Expose var type: String? = "",
    @Expose var producteur_id: String? = "",
    @Expose var producteurs: String? = "",
    @Expose var producteurIdName: String? = "",
    @Expose var parcelle: String? = "",
    @Expose var certificat: String? = "",
    @Expose var typeproduit: String? = "",
    @Expose var quantite: String? = "",
): Parcelable {

}
