package ci.projccb.mobile.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelleUpdateModel(
    @SerializedName("anneeCreation") @Expose var anneeCreation: String? = "",
    @SerializedName("codeParc") @Expose var codeParc: String? = "",
    @SerializedName("created_at") @Expose var createdAt: String? = "",
    @SerializedName("culture") @Expose var culture: String? = "",
    @SerializedName("id") @Expose var id: Int? = 0,
    @SerializedName("latitude") @Expose var latitude: String? = "",
    @SerializedName("longitude") @Expose var longitude: String? = "",
    @SerializedName("producteurs_id") @Expose var producteursId: Int? = 0,
    @SerializedName("superficie") @Expose var superficie: String? = "",
    @SerializedName("typedeclaration") @Expose var typedeclaration: String? = "",
    @SerializedName("updated_at") @Expose var updatedAt: String? = "",
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose var nom: String? = "",
    @Expose var prenoms: String? = "",
    @SerializedName("waypoints") @Expose var waypoints: String? = "[]"
) : Parcelable {
    override fun toString(): String {
        return "$culture ($anneeCreation) : $superficie"
    }
}
