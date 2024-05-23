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


@Entity(tableName = Constants.TABLE_MENAGES, indices = [Index(value = ["id"], unique = true)])
@Parcelize
data class ProducteurMenageModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    @Expose var id: String? = "",
    @Expose var activiteFemme: String? = "",
    @Expose var boisChauffe: String? = "",
    @Expose var champFemme: String? = "",
    @Expose var eauxToillette: String? = "",
    @Expose var eauxVaisselle: String? = "",
    @Expose var empruntMachine: String? = "",
    @Expose @SerializedName("equipements") var equipements: String? = "",
    @Expose var gardeEmpruntMachine: String? = "",
    @Expose @SerializedName(value="garde_machines") var garde_machines_id: String? = "",
    @Expose var machine: String? = "",
    @Expose var nomActiviteFemme: String? = "",
    @Expose var nomPersonneTraitant: String? = "",
    @Expose var nombreHectareFemme: String? = "",
    @Expose @SerializedName(value="ordures_menageres") var ordures_menageres_id: String? = "",
    @Expose @SerializedName(value="producteur_id") var producteurs_id: String? = "",
    @Expose var producteurNomPrenoms: String? = "",
    @Expose @SerializedName(value="quartier") var quartier: String? = "",
    @Expose var separationMenage: String? = "",
    @Expose @SerializedName(value="sources_eaux") var sources_eaux_id: String? = "",
    @Expose @SerializedName(value="sources_energies") var sources_energies_id: String? = "",
    @Expose var superficieCacaoFemme: String? = "",
    @Expose var traitementChamps: String? = "",
    @Expose @SerializedName(value="type_machines") var type_machines_id: String? = "",
    @Expose var numeroPersonneTraitant: String? = "",
    var codeProducteur: String? = "",
    @Expose var wc: String? = "",
    @Expose var localiteNom: String? = "",
    @Expose var section: String? = "",
    @Expose var localite: String? = "",
    @Expose var ageEnfant0A5: String? = "",
    @Expose var ageEnfant6A17: String? = "",
    @Expose var enfantscolarises: String? = "",
    @Expose var enfantsPasExtrait: String? = "",
    @Expose var enfantsPasExtrait6A17: String? = "",
    @Expose var etatatomiseur: String? = "",
    @Expose var nomApplicateur: String? = "",
    @Expose var numeroApplicateur: String? = "",
    @Expose var autreMachine: String? = "",
    @Expose var autreSourceEau: String? = "",
    @Expose var sourcesEnergieStr: String? = "",
    @Expose var ordureMenageresStr: String? = "",
    @Expose var autreEndroit: String? = "",
    @Expose var etatEpi: String? = "",
    @Expose var etatAutreMachine: String? = "",
    @Expose var typeActivite: String? = "",
    @Expose var nomActiviteAgricole: String? = "",
    @Expose var autreActiviteAgricole: String? = "",
    @Expose var nomActiviteNonAgricole: String? = "",
    @Expose var autreActiviteNonAgricole: String? = "",
    @Expose var nombreHectareConjoint: String? = "",
    @Expose var capitalDemarrage: String? = "",
    @Expose var autreCapital: String? = "",
    @Expose var dureeActivite: String? = "",
    @Expose var formation: String? = "",
    @Expose var entite: String? = "",
    @Expose var anneeFormationStruct: String? = "",
    @Expose var activiteRevenueSupplement: String? = "",
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") val agentId: String? = "",
    var origin: String? = "local"
) : Parcelable{

    @SerializedName("sourcesEnergie") @Expose(serialize = true, deserialize = false) @Ignore
    var sourcesEnergieList: MutableList<String>? = arrayListOf()

    @SerializedName("ordureMenageres") @Expose(serialize = true, deserialize = false) @Ignore
    var ordureMenageresList: MutableList<String>? = arrayListOf()

}
