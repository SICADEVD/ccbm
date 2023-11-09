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
    @Expose var section: String? = "",
    @Expose var localite: String? = "",
    @SerializedName("age18") @Expose var age18: String? = "",
    @SerializedName("agentID") @Expose var agentID: Int? = 0,
    @SerializedName("autresCultures") @Expose var autresCultures: String? = "",
    @SerializedName("compteBanque") @Expose var compteBanque: String? = "",
    @SerializedName("nomBanque") @Expose var nomBanque: String? = "",
    @SerializedName("autreBanque") @Expose var autreBanque: String? = "",
    @SerializedName("foretsjachere") @Expose var foretsjachere: String? = "",
    @SerializedName("isSynced") @Expose var isSynced: Boolean? = false,
    @SerializedName("maladiesenfantsStringify") @Expose var maladiesenfantsStringify: String? = "",
    @SerializedName("mobileMoney") @Expose var mobileMoney: String? = "",
    @SerializedName("numeroCompteMM") @Expose var numeroCompteMM: String? = "",
    @Expose var typeactiviteStr: String? = "",
    @Expose var operateurMMStr: String? = "",
    @Expose var numerosMMStr: String? = "",
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
    @SerializedName("travailleurs") @Expose var travailleurs: String? = "",
    @SerializedName("travailleurspermanents") @Expose var travailleurspermanents: String? = "",
    @SerializedName("travailleurstemporaires") @Expose var travailleurstemporaires: String? = "",
    @SerializedName("typeDocuments") @Expose var typeDocuments: String? = "",
    @SerializedName("autreActivite") @Expose var autreActivite: String? = "",
    @SerializedName("typecultureStringify") @Expose var typecultureStringify: String? = "",
    @SerializedName("superficiecultureStringify") @Expose var superficiecultureStringify: String? = "",
    @SerializedName("mainOeuvreFamilial") @Expose var mainOeuvreFamilial: String? = "",
    @SerializedName("societeTravail") @Expose var membreSocieteTravail: String? = "",
    @SerializedName("nombrePersonne") @Expose var nombrePersonne: String? = "",
    @SerializedName("travailleurFamilial") @Expose var travailleurFamilial: String? = "",
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @SerializedName("id") @Expose var id: Int? = 0,
    var origin: String? = "local",
) : Parcelable {
    @SerializedName("typeactivite") @Expose(serialize = true, deserialize = false) @Ignore var typeactiviteList: MutableList<String>? = arrayListOf()
    @SerializedName("operateurMM") @Expose(serialize = true, deserialize = false) @Ignore var operateurMM: MutableList<String>? = arrayListOf()
    @SerializedName("numeros") @Expose(serialize = true, deserialize = false) @Ignore var numerosMM: MutableList<String>? = arrayListOf()

    @SerializedName("typeculture") @Expose(serialize = true, deserialize = false) @Ignore var typeculture: MutableList<String>? = arrayListOf()
    @SerializedName("superficieculture") @Expose(serialize = true, deserialize = false) @Ignore var superficieculture: MutableList<String>? = arrayListOf()

    @SerializedName(value="maladieenfant", alternate = ["maladiesenfants"]) @Expose(serialize = true, deserialize = false) @Ignore var maladiesenfants: MutableList<String>? = arrayListOf()
}
