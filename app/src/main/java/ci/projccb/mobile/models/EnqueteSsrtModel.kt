package ci.projccb.mobile.models


import android.os.Parcelable
import androidx.room.*
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(
    tableName = Constants.TABLE_SSRTE,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
data class EnqueteSsrtModel(
    @SerializedName("autreLienParente") @Expose var autreLienParente: String? = "",
    @SerializedName("avoirFrequente") @Expose var avoirFrequente: String? = "",
    @SerializedName("classe") @Expose var classe: String? = "",
    @SerializedName("codeMembre") @Expose var codeMembre: String? = "",
    @SerializedName(value = "date_enquete", alternate = ["dateEnquete"]) @Expose var dateEnquete: String? = "",
    @SerializedName("datenaissMembre") @Expose var datenaissMembre: String? = "",
    @SerializedName("distanceEcole") @Expose var distanceEcole: String? = "",
    @SerializedName("ecoleVillage") @Expose var ecoleVillage: String? = "",
    @SerializedName("endpoint") @Expose var endpoint: String? = "",
    @SerializedName("frequente") @Expose var frequente: String? = "",
    @SerializedName("lienParente") @Expose var lienParente: String? = "",
    @SerializedName("lieuTravauxDangereuxStringify") @Expose var lieuTravauxDangereuxStringify: String? = "",
    @SerializedName("lieuTravauxLegersStringify") @Expose var lieuTravauxLegersStringify: String? = "",
    @SerializedName("moyenTransport") @Expose var moyenTransport: String? = "",
    @SerializedName("niveauEtude") @Expose var niveauEtude: String? = "",
    @SerializedName("niveauEtudeAtteint") @Expose var niveauEtudeAtteint: String? = "",
    @SerializedName("nomEcole") @Expose var nomEcole: String? = "",
    @SerializedName("nomMembre") @Expose var nomMembre: String? = "",
    @SerializedName("prenomMembre") @Expose var prenomMembre: String? = "",
    @SerializedName(value = "producteur", alternate = ["producteurs_id"]) @Expose var producteursId: String? = "",
    @SerializedName("raisonArretEcoleStringify") @Expose var raisonArretEcoleStringify: String? = "",
    @SerializedName("sexeMembre") @Expose var sexeMembre: String? = "Masculin",
    @SerializedName("title") @Expose var title: String? = "",
    @SerializedName("travauxDangereuxStringify") @Expose var travauxDangereuxStringify: String? = "",
    @SerializedName("travauxLegersStringify") @Expose var travauxLegersStringify: String? = "",
    @SerializedName("uid") @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @SerializedName("id") @Expose var id: Int = 0,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    var isSynced: Boolean = false,
    var origin: String? = "local",
    @Expose var localiteNom: String? = "",
    @Expose var localiteId: String? = "",
    @Expose var producteurNom: String? = "",

) : Parcelable {
    @Ignore @SerializedName("lieuTravauxDangereux") @Expose(serialize = true, deserialize = false) var lieuTravauxDangereux: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("lieuTravauxLegers") @Expose(serialize = true, deserialize = false) var lieuTravauxLegers: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("travauxLegers") @Expose(serialize = true, deserialize = false) var travauxLegers: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("travauxDangereux") @Expose(serialize = true, deserialize = false) var travauxDangereux: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("raisonArretEcole") @Expose(serialize = true, deserialize = false) var raisonArretEcole: MutableList<String>? = mutableListOf()
}
