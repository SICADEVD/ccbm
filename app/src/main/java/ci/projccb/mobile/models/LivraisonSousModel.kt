package ci.projccb.mobile.models

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
    @SerializedName("quantity") @Expose var quantityNb: Int? = 0,
    @SerializedName("amount") @Expose var amountNb: Int? = null,
    @SerializedName("scelleStringify") @Expose var numScelle: String? = null,
): Parcelable {
    @Ignore @SerializedName("scelle") @Expose(serialize = true, deserialize = false) var scelleList: MutableList<String>? = null
}
