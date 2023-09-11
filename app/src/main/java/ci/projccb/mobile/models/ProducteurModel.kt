package ci.projccb.mobile.models

import android.os.Parcelable
import androidx.room.*
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = Constants.TABLE_PRODUCTEURS, indices = [Index(value = ["uid"], unique = true)])
@Parcelize
data class ProducteurModel(
    @PrimaryKey(autoGenerate = true) @Expose var uid: Int,
    @SerializedName("codeProd") @Expose var codeProd: String? = "",
    @SerializedName("codeProdapp") @Expose var codeProdApp: String? = "",
    @Expose var id: Int? = 0,
    @SerializedName(value = "localite_id", alternate = ["localites_id"]) @Expose var localitesId: String? = "0",
    @Expose var nom: String? =  "",
    @Expose var prenoms: String? = "",
    @SerializedName("dateNaiss") @Expose var naissance: String? = "",
    @SerializedName("certificat") @Expose var anneeCertification: String? = "",
    @SerializedName(value = "statut", alternate = ["status"]) @Expose var statutCertification: String? = "",
    @SerializedName("consentement") @Expose var consentement: String? = "",
    @SerializedName(value="nationalite", alternate = ["nationalites_id"]) @Expose var nationalite: String? = "",
    @SerializedName("sexe") @Expose var sexeProducteur: String? = "",
    @SerializedName("phone1") @Expose var phoneOne: String? = "",
    @SerializedName("phone2") @Expose var phoneTwo: String? = "",
    @SerializedName(value="type_piece", alternate = ["type_pieces_id"]) @Expose var piece: String? = "",
    @SerializedName(value= "niveau_etude", alternate = ["niveaux_id"]) @Expose var etude: String? = "",
    @SerializedName("numPiece") @Expose var pieceNumber: String? = "",
    @SerializedName("foretsjachere") @Expose var hasForest: String? = "",
    @SerializedName("superficie") @Expose var forestSuperficy: String? = "",
    @SerializedName("autresCultures") @Expose var hasOtherFarms: String? = "",
    @SerializedName("age18") @Expose var under18Count: String? = "",
    @SerializedName("persEcole") @Expose var under18SchooledCount: String? = "",
    var cultures: String? = null,
    @SerializedName("scolarisesExtrait") @Expose var under18SchooledNoPaperCount: String? = "",
    @SerializedName("travailleurs") @Expose var farmersCount: String? = "",
    @SerializedName("personneBlessee") @Expose var blessed: String? = "",
    @SerializedName("papiersChamps") @Expose var hasFarmsPapers: String? = "",
    @SerializedName("gardePapiersChamps") @Expose var paperGuards: String? = "",
    @SerializedName("recuAchat") @Expose var recuAchat: String? = "",
    @SerializedName("mobileMoney") @Expose var hasMobileMoney: String? = "",
    @SerializedName("numeroCompteMM") @Expose var mobileMoney: String? = "",
    @SerializedName("compteBanque") @Expose var banqueAccount: String? = "",
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    @Expose @SerializedName("copiecarterectoPath") var rectoPath: String? = "",
    @Expose @SerializedName("copiecarterecto") var recto: String? = "",
    @Expose @SerializedName("copiecarteversoPath") var versoPath: String? = "",
    @Expose @SerializedName("copiecarteverso") var verso: String? = "",
    @Expose @SerializedName("picture") var picture: String? = "",
    @Expose @SerializedName("picturePath") var picturePath: String? = "",
    @Expose @SerializedName("esignature") var esignature: String? = "",
    @Expose @SerializedName("esignaturePath") var esignaturePath: String? = "",
    var origin: String? = "local",

) : Parcelable {

    @Ignore @Expose(serialize = true, deserialize = false) var typeculture: MutableList<String>? = null
    @Ignore @Expose(serialize = true, deserialize = false) var superficieculture: MutableList<String>? = null
    @Ignore var producteursCultures: MutableList<CultureProducteurModel>? = null
    @Expose @Ignore var localite: String? = ""

    override fun toString(): String {
        return "$nom $prenoms"
    }
}
