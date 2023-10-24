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
    @SerializedName(value = "section") @Expose var section: String? = "0",
    @SerializedName(value = "localite_id", alternate = ["localites_id"]) @Expose var localitesId: String? = "0",
    @SerializedName(value = "programme_id") @Expose var programme_id: String? = "0",
    @Expose var nom: String? =  "",
    @Expose var prenoms: String? = "",
    @SerializedName("dateNaiss") @Expose var dateNaiss: String? = "",
    @SerializedName("certificats") @Expose var certificats: String? = "",
    @SerializedName("autreCertificats") @Expose var autreCertificats: String? = "",
    @SerializedName("habitationProducteur") @Expose var habitationProducteur: String? = "",
    @SerializedName("variete") @Expose var variete: String? = "",
    @SerializedName("autreVariete") @Expose var autreVariete: String? = "",
    @SerializedName("certificat") @Expose var anneeCertification: String? = "",
    @SerializedName(value = "statut", alternate = ["status"]) @Expose var statutCertification: String? = "",
    @SerializedName("consentement") @Expose var consentement: String? = "",
    @SerializedName("proprietaires") @Expose var proprietaires: String? = "",
    @SerializedName("plantePartage") @Expose var plantePartage: String? = "",
    @SerializedName(value="nationalite", alternate = ["nationalites_id"]) @Expose var nationalite: String? = "",
    @SerializedName("statutMatrimonial") @Expose var statutMatrimonial: String? = "",
    @SerializedName("sexe") @Expose var sexeProducteur: String? = "",
    @SerializedName("phone1") @Expose var phone1: String? = "",
    @SerializedName("phone2") @Expose var phone2: String? = "",
    @SerializedName("autreMembre") @Expose var autreMembre: String? = "",
    @SerializedName("autrePhone") @Expose var autrePhone: String? = "",
    @SerializedName(value="type_piece", alternate = ["type_pieces_id"]) @Expose var piece: String? = "",
    @SerializedName(value= "niveau_etude", alternate = ["niveaux_id"]) @Expose var etude: String? = "",
    @SerializedName("numPiece") @Expose var numPiece: String? = "",
    @SerializedName("num_ccc") @Expose var num_ccc: String? = "",
    @SerializedName("carteCMU") @Expose var carteCMU: String? = "",
    @SerializedName("numCMU") @Expose var numCMU: String? = "",
    @SerializedName("numSecuriteSociale") @Expose var numSecuriteSociale: String? = "",
    @SerializedName("typeCarteSecuriteSociale") @Expose var typeCarteSecuriteSociale: String? = "",
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
