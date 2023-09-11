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
    tableName = Constants.TABLE_INFOS_PRODUCTEUR,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class InfosProducteurDTO(
    @SerializedName("age18") @Expose var age18: String? = "",
    @SerializedName("agentID") @Expose var agentID: Int? = 0,
    @SerializedName("autresCultures") @Expose var autresCultures: String? = "",
    @SerializedName("compteBanque") @Expose var compteBanque: String? = "",
    @SerializedName("foretsjachere") @Expose var foretsjachere: String? = "",
    @SerializedName("isSynced") @Expose var isSynced: Boolean? = false,
    @SerializedName("maladiesenfantsStringify") @Expose var maladiesenfantsStringify: String? = "",
    @SerializedName("mobileMoney") @Expose var mobileMoney: String? = "",
    @SerializedName("numeroCompteMM") @Expose var numeroCompteMM: String? = "",
    @SerializedName("operateurMM") @Expose var operateurMM: String? = "",
    @SerializedName("paiementMM") @Expose var paiementMM: String? = "",
    @SerializedName("localiteNom") @Expose var localiteNom: String? = "",
    @SerializedName("persEcole") @Expose var persEcole: String? = "",
    @SerializedName("personneBlessee") @Expose var personneBlessee: String? = "",
    @SerializedName(value="producteur_id", alternate = ["producteurs_id"]) @Expose var producteursId: String? = "",
    @SerializedName("producteurs_noms") @Expose var producteursNom: String? = "",
    @SerializedName("producteurs_code") @Expose var producteursCode: String? = "",
    @SerializedName("recuAchat") @Expose var recuAchat: String? = "",
    @SerializedName("scolarisesExtrait") @Expose var scolarisesExtrait: String? = "",
    @SerializedName("superficie") @Expose var superficie: String? = "",
    @SerializedName("superficiecultureStringify") @Expose var superficiecultureStringify: String? = "",
    @SerializedName("travailleurs") @Expose var travailleurs: String? = "",
    @SerializedName("travailleurspermanents") @Expose var travailleurspermanents: String? = "",
    @SerializedName("travailleurstemporaires") @Expose var travailleurstemporaires: String? = "",
    @SerializedName("typeDocuments") @Expose var typeDocuments: String? = "",
    @SerializedName("typecultureStringify") @Expose var typecultureStringify: String? = "",
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @SerializedName("id") @Expose var id: Int? = 0,
    var origin: String? = "local",
) : Parcelable {
    @SerializedName("typeculture") @Expose(serialize = true, deserialize = false) @Ignore var typeculture: MutableList<String>? = arrayListOf()
    @SerializedName("superficieculture") @Expose(serialize = true, deserialize = false) @Ignore var superficieculture: MutableList<String>? = arrayListOf()
    @SerializedName(value="maladieenfant", alternate = ["maladiesenfants"]) @Expose(serialize = true, deserialize = false) @Ignore var maladiesenfants: MutableList<String>? = arrayListOf()
}
