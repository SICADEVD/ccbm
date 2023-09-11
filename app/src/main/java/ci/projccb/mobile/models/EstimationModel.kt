package ci.projccb.mobile.models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = Constants.TABLE_ESTIMATIONS,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class EstimationModel(
    @SerializedName(value = "campagne", alternate = ["campagnes_id"]) @Expose var campagnesId: String? = "",
    @Expose var campagnesNom: String? = "",
    @SerializedName("date_estimation") @Expose var dateEstimation: String? = "",
    @Expose var id: Int? = 0,
    @Expose var parcelleNom: String? = "",
    @Expose var producteurNom: String? = "",
    @Expose @SerializedName("producteur") var producteurId: String? = "",
    @Expose @SerializedName("localite") var localiteId: String? = "",
    @Expose var localiteNom: String? = "",
    @SerializedName("EA1") @Expose var ea1: String? = "",
    @SerializedName("EA2") @Expose var ea2: String? = "",
    @SerializedName("EA3") @Expose var ea3: String? = "",
    @SerializedName("EB1") @Expose var eb1: String? = "",
    @SerializedName("EB2") @Expose var eb2: String? = "",
    @SerializedName("EB3") @Expose var eb3: String? = "",
    @SerializedName("EC1") @Expose var ec1: String? = "",
    @SerializedName("EC2") @Expose var ec2: String? = "",
    @SerializedName("EC3") @Expose var ec3: String? = "",
    @SerializedName(value = "parcelle", alternate = ["parcelles_id"]) @Expose var parcelleId: String? = "",
    @SerializedName("superficie") @Expose var superficie: String? = "",
    @SerializedName("uid") @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    var isSynced: Boolean = false,
    var origin: String? = "local"
) : Parcelable
