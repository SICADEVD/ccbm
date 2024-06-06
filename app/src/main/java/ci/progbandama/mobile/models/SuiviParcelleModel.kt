package ci.progbandama.mobile.models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.progbandama.mobile.repositories.datas.ArbreData
import ci.progbandama.mobile.repositories.datas.InsectesParasitesData
import ci.progbandama.mobile.repositories.datas.PesticidesAnneDerniereModel
import ci.progbandama.mobile.repositories.datas.PresenceAutreInsecteData
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = Constants.TABLE_SUIVI_PARCELLES,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class SuiviParcelleModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    @Expose var id: Int? = 0,
    @SerializedName("activiteDesherbageManuel")
    @Expose var activiteDesherbageManuel: String? = "",
    @Expose var parcelleNom: String? = "",
    @Expose var localiteNom: String? = "",
    @Expose @SerializedName("localite")  var localiteId: String? = "",
    @Expose var parcelleSuperficie: String? = "",
    @Expose var parcelleProducteur: String? = "",
    @SerializedName("activiteEgourmandage") @Expose var activiteEgourmandage: String? = "",
    @SerializedName("activiteRecolteSanitaire")
    @Expose var activiteRecolteSanitaire: String? = "",
    @SerializedName("activiteTaille") @Expose var activiteTaille: String? = "",
    @SerializedName(value = "cours_eaux", alternate = ["cours_eaux_id"])
    @Expose var coursEauxId: String? = "",
    @SerializedName("dateVisite") @Expose var dateVisite: String? = "",
    @SerializedName(value = "campagne_id", alternate = ["campagnes"]) @Expose var campagneId: String? = "",
    var campagneNom: String? = "",
    @SerializedName("existeCoursEaux") @Expose var existeCoursEaux: String? = "",
    @SerializedName("intrant")
    @Expose var intrant: String? = "",
    @SerializedName("nomFongicide")
    @Expose var nomFongicide: String? = "",
    @SerializedName("nomHerbicide")
    @Expose var nomHerbicide: String? = "",
    @SerializedName("nomInsecticide")
    @Expose var nomInsecticide: String? = "",
    @SerializedName("nombreDesherbage")
    @Expose var nombreDesherbage: String? = "",
    @SerializedName("nombreFongicide")
    @Expose var nombreFongicide: String? = "",
    @SerializedName("nombreHerbicide")
    @Expose var nombreHerbicide: String? = "",
    @SerializedName("nombreInsecticide")
    @Expose var nombreInsecticide: String? = "",
    @SerializedName("nombreOmbrage") @Ignore @Expose(serialize = true, deserialize = false) var nombreOmbrage: MutableList<String>? = mutableListOf(),
    @Expose(serialize = true, deserialize = false) @SerializedName("varietesOmbrage") @Ignore var varietesOmbrage: MutableList<String>? = mutableListOf(),
    @SerializedName("nombreSauvageons") @Expose var nombreSauvageons: String? = "",
    @SerializedName("arbresagroforestiers") @Expose var arbresAgroForestiersYesNo: String? = "",
    @SerializedName("arbresagroforestiersStringify") @Expose var arbreAgroForestierStringify: String? = "",
    @SerializedName("nombreagroforestiersStringify") @Expose var nombreArbreAgroStringify: String? = "",
    @SerializedName("presencePourritureBrune") @Expose var presencePourritureBrune: String? = "",
    @SerializedName("presenceSwollenShoot") @Expose var presenceShooter: String? = "",
    @Expose var insectesAmisStr: String? = "",
    @Expose var nombreinsectesAmisStr: String? = "",
    @Expose var itemsStr: String? = "",
    @SerializedName("nombresacs")
    @Expose var nombresacs: String? = "",
    @SerializedName(value = "parcelle", alternate = ["parcelles_id"]) @Expose var parcellesId: String? = "",
    @SerializedName("pente")
    @Expose var pente: String? = "",
    @SerializedName("presenceAraignee")
    @Expose var presenceAraignee: String? = "",
    @SerializedName("presenceBioAgresseur")
    @Expose var presenceBioAgresseur: String? = "",
    @SerializedName("presenceFourmisRouge")
    @Expose var presenceFourmisRouge: String? = "",
    @SerializedName("presenceInsectesRavageurs")
    @Expose var presenceInsectesRavageurs: String? = "",
    @SerializedName("presenceMenteReligieuse")
    @Expose var presenceMenteReligieuse: String? = "",
    @Expose var nombresacsNPK: String? = "",
    @Expose var intrantNPK: String? = "NPK",
    @Expose var intrantFiente: String? = "",
    @Expose var nombresacsFiente: String? = "",
    @Expose var intrantDechetAnimal: String? = "Déchet Animal",
    @Expose var nombresacsDechetAnimal: String? = "",
    @Expose var intrantComposte: String? = "Composte",
    @Expose var nombresacsComposte: String? = "",
    @Expose var qteBiofertilisant: String? = "",
    @Expose var uniteBioFertilisant: String? = "L",
    @Expose var qteEngraisOrganique: String? = "",
    @Expose var uniteEngraisOrganique: String? = "L",
    @Expose var animauxRencontresStringify: String? = "",
    @Expose var intrantsAnneDerniereStr: String? = "",
    @Expose var pesticidesAnneDerniereStr: String? = "",
    @Expose var insectesParasitesStr: String? = "",
    @SerializedName("presenceVerTerre")
    @Expose var presenceVerTerre: String? = "",
    @SerializedName(value = "producteur", alternate = ["producteurs_id"]) @Expose var producteursId: String? = "",
    @Expose @SerializedName("varieteAbres") var varieteAbres: String? = "",
    @Expose @SerializedName(value = "varietes_cacao", alternate = ["varietes_cacao_id"]) var varietesCacaoId: String? = "",
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    var origin: String? = "local",
    @Expose var varieteOmbragesTemp: String? = "",
    @Expose var insectesParasitesTemp: String? = "",
    @Expose var nombreInsectesParasitesTemp: String? = "",
    @Expose var animauxTemp: String? = "",
    @Expose var section: String? = "",
    @Expose var parcelle_id: String? = "",
    @Expose var autreInsecte: String? = "",
    @Expose var traiterParcelle: String? = "",
    @Expose var traitementStr: String? = "",
    @Expose var arbreStr: String? = "",
    @Expose var arbreItemStr: String? = "",
    @Expose var recuArbreAgroForestier: String? = "",
    @Expose var frequencePesticide: String? = "",
    @Expose var pesticideUtiliseAnneeDerStr: String? = "",
    @Expose var intrantUtiliseAnneeDerStr: String? = "",
    @Expose var presenceAutreInsecteStr: String? = "",
    @Expose var autrePesticide: String? = "",
    @Expose var presenceInsectesParasites: String? = "",
    @Expose var presenceInsectesParasitesRavageur: String? = "",
    @Expose var presenceInsectesParasitesRavageurStr: String? = "",
    @Expose var autreInsectesParasitesRavageurStr: String? = "",
    @Expose var presenceAutreTypeInsecteAmi: String? = "",
    @Expose var intrantNomListStr: String? = "",
    @Expose var bioferNomListStr: String? = "",
    @Expose var bioferNbrListStr: String? = "",
    @Expose var animauxRencontrer: String? = "",
    @Expose var qteFongicide: String? = "",
    @Expose var uniteFongicide: String? = "",
    @Expose var qteHerbicide: String? = "",
    @Expose var uniteHerbicide: String? = "",
) : Parcelable {
    @Ignore var ombrages: MutableList<OmbrageVarieteModel>? = null
    @Ignore var insecteAmis: MutableList<InsecteAmisModel>? = null
    @Ignore @SerializedName("presenceAutreInsecte") @Expose(serialize = true, deserialize = false) var presenceAutreInsecteList: MutableList<PresenceAutreInsecteData>? = null
    @Ignore @SerializedName("insectesParasites") @Expose(serialize = true, deserialize = false) var insectesParasitesList: MutableList<InsectesParasitesData>? = null
    @Ignore @SerializedName("nombreinsectesParasites") @Expose(serialize = true, deserialize = false) var nombreInsectesParasitesList: MutableList<String>? = null

    @Ignore @SerializedName("insectesParasitesOuRavageurs") @Expose(serialize = true, deserialize = false) var insectesParasitesOuRavageursList: MutableList<String>? = null
    @Ignore @SerializedName("nombreinsectesParasitesOuRavageurs") @Expose(serialize = true, deserialize = false) var nombreInsectesParasitesOuRavageursList: MutableList<String>? = null

    @Ignore @SerializedName("autreInsectesParasitesOuRavageurs") @Expose(serialize = true, deserialize = false) var autreParasitesOuRavageursList: MutableList<String>? = null
    @Ignore @SerializedName("autreNombreinsectesParasitesOuRavageurs") @Expose(serialize = true, deserialize = false) var autreInsectesParasitesOuRavageursList: MutableList<String>? = null

    @Ignore @SerializedName("insectesAmis") @Expose(serialize = true, deserialize = false) var insectesAmisList: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("nombreinsectesAmis") @Expose(serialize = true, deserialize = false) var nombreinsectesAmisList: MutableList<String>? = mutableListOf()

    @Ignore @Expose(serialize = true, deserialize = false) var animauxRencontres: MutableList<String>? = mutableListOf()
    @Ignore @Expose(serialize = true, deserialize = false) var intrantNomList: MutableList<String>? = mutableListOf()

    @Ignore @Expose(serialize = true, deserialize = false) var bioferNomList: MutableList<String>? = mutableListOf()
    @Ignore @Expose(serialize = true, deserialize = false) var bioferNbrList: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("items") @Expose(serialize = true, deserialize = false) var arbreItemsList: MutableList<ArbreData>? = mutableListOf()
    @Ignore @SerializedName("traitement") @Expose(serialize = true, deserialize = false) var traitementList: MutableList<PesticidesAnneDerniereModel>? = mutableListOf()
    @Ignore @SerializedName("pesticidesAnneDerniere") @Expose(serialize = true, deserialize = false) var pesticidesAnneDerniereList: MutableList<PesticidesAnneDerniereModel>? = mutableListOf()
    @Ignore @SerializedName("intrantsAnneDerniere") @Expose(serialize = true, deserialize = false) var intrantsAnneDerniereList: MutableList<PesticidesAnneDerniereModel>? = mutableListOf()
    @Ignore @SerializedName("arbre") @Expose(serialize = true, deserialize = false) var arbreList: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("agroforestiers") @Expose(serialize = true, deserialize = false) var agroForestiers: MutableList<String>? = mutableListOf()
    @Ignore @SerializedName("nombreagroforestiers") @Expose(serialize = true, deserialize = false) var nombreArbresAgro: MutableList<String>? = mutableListOf()

}
