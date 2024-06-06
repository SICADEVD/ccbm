package ci.progbandama.mobile.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.progbandama.mobile.repositories.datas.ArbreData
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = Constants.TABLE_PARCELLES,
    indices = [
        Index(
            value = ["uid"], unique = true
        )
    ]
)
@Parcelize
data class ParcelleModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    @Expose var id: Int? = 0,
    @Expose @SerializedName(value="producteur_id", alternate = ["producteur"]) var producteurId: String? = "",
    @Expose var producteurNom: String? = "",
    @Expose var anneeCreation: String? = "",
    @Expose var localiteNom: String? = "",
    @Expose var culture: String? = "",
    @Expose var codeParc: String? = "",
    @Expose var wayPointsString: String? = "",
    @Expose var perimeter: String? = "",
    @Expose var typedeclaration: String? = "",
    @Expose var yesnoautrearbreombrag: String? = "",
    @Expose var superficie: String? = "",
    @Expose var latitude: String? = "",
    @Expose var longitude: String? = "",
    @Expose var yesornoarbreombrage: String? = "",
    @Expose @SerializedName("nom") var nom: String? = "",
    @Expose @SerializedName("prenoms") var prenoms: String? = "",
    @Expose @SerializedName(value = "section", alternate = ["section_id"]) var section: String? = "",
    @Expose @SerializedName(value = "localite", alternate = ["localite_id"]) var localite: String? = "",
    @Expose var ageMoyenCacao: String? = "",
    @Expose var parcelleRegenerer: String? = "",
    @Expose var anneeRegenerer: String? = "",
    @Expose var typeDoc: String? = "",
    @Expose var superficieConcerne: String? = "",
    @Expose var presenceCourDeau: String? = "",
    @Expose var existeMesureProtection: String? = "",
    @Expose var arbreStr: String? = "",
    @Expose var protectionStr: String? = "",
    @Expose var arbreStrateStr: String? = "",
    @Expose var autreProtection: String? = "",
    @Expose var existePente: String? = "",
    @Expose var niveauPente: String? = "",
    @Expose var varieteStr: String? = "",
    @Expose var erosion: String? = "",
    @Expose var nbCacaoParHectare: String? = "",
    @Expose var courDeau: String? = "",
    @Expose var autreCourDeau: String? = "",
    var status: Boolean = false,
    var isSynced: Boolean = false,
    @Expose var sync_update: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    var origin: String? = "local"
) : Parcelable {

    @Ignore @Expose @SerializedName(value = "arbreStrate") var arbreStrate: MutableList<ParcAutreOmbrag> = mutableListOf()
    @Ignore @Expose(serialize = true, deserialize = false) @SerializedName("waypoints") var mappingPoints: MutableList<String> = mutableListOf()
    @Ignore @Expose @SerializedName(value = "variete") var varieteO: MutableList<String> = mutableListOf()
    @Ignore @Expose @SerializedName(value = "protection") var protectionO: MutableList<String> = mutableListOf()
    @Ignore @Expose @SerializedName(value = "items") var itemsO: MutableList<ArbreData> = mutableListOf()
//    @Ignore @Expose(serialize = true, deserialize = false) @SerializedName("items") var arbreList: MutableList<ArbreData> = mutableListOf()
//    @Ignore @Expose(serialize = true, deserialize = false) @SerializedName("protection") var protectionList: MutableList<String> = mutableListOf()


//    override fun toString(): String {
//        return "$culture $anneeCreation"
//    }

}

data class ParcelleExtModel(
    @Expose var uid: Long = 0,
    @Expose var id: Int? = 0,
    @Expose @SerializedName(value="producteur_id", alternate = ["producteur"]) var producteurId: String? = "",
    @Expose var producteurNom: String? = "",
    @Expose var anneeCreation: String? = "",
    @Expose var localiteNom: String? = "",
    @Expose var culture: String? = "",
    @Expose var codeParc: String? = "",
    @Expose var wayPointsString: String? = "",
    @Expose var perimeter: String? = "",
    @Expose var typedeclaration: String? = "",
    @Expose var superficie: String? = "",
    @Expose var latitude: String? = "",
    @Expose var longitude: String? = "",
    @Expose var yesornoarbreombrage: String? = "",
    @Expose @SerializedName("nom") var nom: String? = "",
    @Expose @SerializedName("prenoms") var prenoms: String? = "",
    @Expose @SerializedName(value = "section", alternate = ["section_id"]) var section: String? = "",
    @Expose @SerializedName(value = "localite", alternate = ["localite_id"]) var localite: String? = "",
    @Expose var ageMoyenCacao: String? = "",
    @Expose var parcelleRegenerer: String? = "",
    @Expose var anneeRegenerer: String? = "",
    @Expose var typeDoc: String? = "",
    @Expose var superficieConcerne: String? = "",
    @Expose var presenceCourDeau: String? = "",
    @Expose var existeMesureProtection: String? = "",
    @Expose var arbreStr: String? = "",
    @Expose var protectionStr: String? = "",
    @Expose var autreProtection: String? = "",
    @Expose var existePente: String? = "",
    @Expose var niveauPente: String? = "",
    @Expose var varieteStr: String? = "",
    @Expose var erosion: String? = "",
    @Expose var nbCacaoParHectare: String? = "",
    @Expose var courDeau: String? = "",
    @Expose var autreCourDeau: String? = "",
    var status: Boolean = false,
    var isSynced: Boolean = false,
    @Ignore @Expose var sync_update: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    var origin: String? = "local"
){
    @Ignore @Expose @SerializedName(value = "variete") var varieteO: MutableList<ParcVariete> = mutableListOf()
    @Ignore @Expose @SerializedName(value = "protection") var protectionO: MutableList<ParcProtect> = mutableListOf()
    @Ignore @Expose @SerializedName(value = "items") var itemsO: MutableList<ParcArbre> = mutableListOf()
    @Ignore @Expose @SerializedName(value = "arbreStrate") var arbreStrate: MutableList<ParcAutreOmbrag> = mutableListOf()
}

data class ParcProtect(
    @Expose var id: Int? = null,
    @Expose var parcelle_id: String? = null,
    @Expose var typeProtection: String? = null
)

data class ParcVariete(
    @Expose var id: Int? = null,
    @Expose var parcelle_id: String? = null,
    @Expose var variete: String? = null
)

data class ParcArbre(
    @Expose var id: Int? = null,
    @Expose var parcelle_id: String? = null,
    @Expose var agroespeceabre_id: String? = null,
    @Expose var nombre: String? = null
)

data class ParcAutreOmbrag(
    @Expose var id: Int? = null,
    @Expose var nom: String? = null,
    @Expose var strate: String? = null,
    @Expose var qte: String? = null
)
