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

@Entity(tableName = Constants.TABLE_LIVRAISONS, indices = [Index(value = ["uid"], unique = true)])
@Parcelize
data class LivraisonModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @Expose val id: Int? = 0,
    @Expose val programme_id: String? = null,
    @SerializedName("dateLivre")
    @Expose var dateLivre: String? = "",
    @SerializedName("nomDelegue") @Expose var delegueId: String? = "",
    @Expose var delegueNom: String? = "",
    @Expose var typeProduit: String? = "",
    @SerializedName("nombreSacs")
    @Expose var nombreSacs: String? = "",
    @SerializedName("producteurs_id") @Expose var producteursId: String? = "",
    @Expose var producteurNom: String? = "",
    @SerializedName("campagnes_id") @Expose var campagneId: String? = "",
    @Expose var campagneNom: String? = "",
    @SerializedName("parcelles_id") @Expose var parcelleId: String? = "",
    @Expose var parcelleNom: String? = "",
    @SerializedName("quantiteLivre")
    @Expose var quantiteLivre: String? = "",
    @Expose var localiteNom: String? = "",
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    @Expose @SerializedName("magasinsections_id") var magasinSectionId: String? = "",
    @Expose var magasinSectionLabel: String? = "",
    @SerializedName("cooperative") @Expose var cooperativeId: String? = "",
    @SerializedName("estimate_date") @Expose var estimatDate: String? = "",
    @SerializedName("payment_status") @Expose var paymentStatus: String? = "",
    @SerializedName("sender_staff") @Expose var senderStaff: String? = "",
    @SerializedName("sender_name") @Expose var senderName: String? = "",
    @SerializedName("sender_phone") @Expose var senderPhone: String? = "",
    @SerializedName("sender_email") @Expose var senderEmail: String? = "",
    @SerializedName("sender_address") @Expose var senderAddress: String? = null,
    @SerializedName("receiver_name") @Expose var receiverName: String? = "",
    @SerializedName("receiver_phone") @Expose var receiverPhone: String? = "",
    @SerializedName("receiver_email") @Expose var receiverEmail: String? = "",
    @SerializedName("receiver_address") @Expose var receiverAddress: String? = null,
    @SerializedName("reduction") @Expose var reduction: String? = null,
    @SerializedName("total_reduce") @Expose var totalReduce: String? = null,
    @SerializedName("sous_total_reduce") @Expose var sousTotalReduce: String? = null,
    @SerializedName("magasin_section") @Expose var magasinSection: String? = null,
    @SerializedName("items_stringify") @Expose var itemsStringify: String? = "",
     @Expose var livraisonSousModelProdNamesStringify: String? = null,
     @Expose var livraisonSousModelProdIdsStringify: String? = null,
     @Expose var livraisonSousModelParcellesStringify: String? = null,
     @Expose var livraisonSousModelParcelleIdsStringify: String? = null,
     @Expose var livraisonSousModelTypesStringify: String? = null,
     @Expose var livraisonSousModelQuantitysStringify: String? = null,
     @Expose var livraisonSousModelAmountsStringify: String? = null,
     @Expose var livraisonSousModelScellesStringify: String? = null,
     @Expose var staffLab: String? = null,
     @Expose var magasinLab: String? = null,
    var origin: String? = "local"
) : Parcelable {
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("items") var itemList: MutableList<LivraisonSousModel>? = null
}
