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
    @SerializedName("codeProd") @Expose var codeProd: String? = null,
    @SerializedName("codeProdapp") @Expose var codeProdApp: String? = null,
    @Expose var id: Int? = 0,
    @SerializedName(value = "section", alternate = ["section_id"]) @Expose var section: String? = "0",
    @SerializedName(value = "localite_id", alternate = ["localites_id"]) @Expose var localitesId: String? = "0",
    @SerializedName(value = "programme_id") @Expose var programme_id: String? = "0",
    @SerializedName(value = "autreProgramme") @Expose var autreProgramme: String? = null,
    @Expose var nom: String? =  null,
    @Expose var prenoms: String? = null,
    @SerializedName("dateNaiss") @Expose var dateNaiss: String? = null,
    @Expose var certificatsStr: String? = null,
    @SerializedName("autreCertificats") @Expose var autreCertificats: String? = null,
    @SerializedName("habitationProducteur") @Expose var habitationProducteur: String? = null,
    @SerializedName("variete") @Expose var variete: String? = null,
    @SerializedName("autreVariete") @Expose var autreVariete: String? = null,
    @SerializedName("certification") @Expose var certification: String? = null,
    @SerializedName("certificat") @Expose var anneeCertification: String? = null,
    @SerializedName(value = "statut", alternate = ["status"]) @Expose var statutCertification: String? = null,
    @SerializedName("consentement") @Expose var consentement: String? = null,
    @SerializedName("proprietaires") @Expose var proprietaires: String? = null,
    @SerializedName("plantePartage") @Expose var plantePartage: String? = null,
    @SerializedName(value="nationalite", alternate = ["nationalites_id"]) @Expose var nationalite: String? = null,
    @SerializedName("statutMatrimonial") @Expose var statutMatrimonial: String? = null,
    @SerializedName("sexe") @Expose var sexeProducteur: String? = null,
    @SerializedName("phone1") @Expose var phone1: String? = null,
    @SerializedName("phone2") @Expose var phoneMembre: String? = null,
    @SerializedName("autreMembre") @Expose var autreMembre: String? = null,
    @SerializedName("autrePhone") @Expose var autrePhone: String? = null,
    @SerializedName(value="type_piece", alternate = ["type_pieces_id"]) @Expose var piece: String? = null,
    @SerializedName(value= "niveau_etude", alternate = ["niveaux_id"]) @Expose var etude: String? = null,
    @SerializedName("numPiece") @Expose var numPiece: String? = null,
    @SerializedName("num_ccc") @Expose var num_ccc: String? = null,
    @SerializedName("carteCMU") @Expose var carteCMU: String? = null,
    @SerializedName("numCMU") @Expose var numCMU: String? = null,
    @SerializedName("numSecuriteSociale") @Expose var numSecuriteSociale: String? = null,
    @SerializedName("typeCarteSecuriteSociale") @Expose var typeCarteSecuriteSociale: String? = null,
    @SerializedName("foretsjachere") @Expose var hasForest: String? = null,
    @SerializedName("superficie") @Expose var forestSuperficy: String? = null,
    @SerializedName("autresCultures") @Expose var hasOtherFarms: String? = null,
    @SerializedName("age18") @Expose var under18Count: String? = null,
    @SerializedName("persEcole") @Expose var under18SchooledCount: String? = null,
    var cultures: String? = null,
    @SerializedName("scolarisesExtrait") @Expose var under18SchooledNoPaperCount: String? = null,
    @SerializedName("travailleurs") @Expose var farmersCount: String? = null,
    @SerializedName("personneBlessee") @Expose var blessed: String? = null,
    @SerializedName("papiersChamps") @Expose var hasFarmsPapers: String? = null,
    @SerializedName("gardePapiersChamps") @Expose var paperGuards: String? = null,
    @SerializedName("recuAchat") @Expose var recuAchat: String? = null,
    @SerializedName("mobileMoney") @Expose var hasMobileMoney: String? = null,
    @SerializedName("numeroCompteMM") @Expose var mobileMoney: String? = null,
    @SerializedName("compteBanque") @Expose var banqueAccount: String? = null,
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = null,
    @Expose @SerializedName("copiecarterectoPath") var rectoPath: String? = null,
    @Expose @SerializedName("copiecarterecto") var recto: String? = null,
    @Expose @SerializedName("copiecarteversoPath") var versoPath: String? = null,
    @Expose @SerializedName("copiecarteverso") var verso: String? = null,
    @Expose @SerializedName("picture") var picture: String? = null,
    @Expose @SerializedName("photo") var photo: String? = null,
    @Expose @SerializedName("esignature") var esignature: String? = null,
    @Expose @SerializedName("esignaturePath") var esignaturePath: String? = null,
    var origin: String? = "local",

) : Parcelable {

    @Ignore @Expose(serialize = true, deserialize = false) @SerializedName("certificats") var certificats: MutableList<String> = mutableListOf()
    @Ignore @Expose(serialize = true, deserialize = false) var typeculture: MutableList<String>? = null
    @Ignore @Expose(serialize = true, deserialize = false) var superficieculture: MutableList<String>? = null
    @Ignore var producteursCultures: MutableList<CultureProducteurModel>? = null
    @Expose @Ignore var localite: String? = null

//    override fun toString(): String {
//        return "$nom $prenoms"
//    }
}

data class ProdExt(
    val uid: Int,
    val id: Int,
    val parceId: String? = null,
    val parceUid: String? = null,
    val fullName: String = "",
    val isSynced: Boolean,
    val nom: String = "",
    val prenoms: String = "",
    val codeProd: String? = null,
    val localite: String? = null,
    val codeParc: String? = null, // Make nullable as it might not be available in all cases
    val superficie: String? = null, // Make nullable as it might not be available in all cases
    val anneeCreation: String? = null, // Make nullable as it might not be available in all cases
    val anneeRegenerer: String? = null // Make nullable as it might not be available in all cases
)
