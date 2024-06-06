package ci.progbandama.mobile.models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(
    tableName = Constants.TABLE_FORMATIONS,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class FormationModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @Expose var id: Int? = 0,
    @Expose @SerializedName(value = "date_formation", alternate = ["dateFormation"])
    var dateFormation: String? = "",
    @SerializedName(value = "localite", alternate = ["localites_id"]) @Expose var localitesId: String? = "",
    @Expose var producteursIdStringify: String? = "",
    @Expose var producteursNomStringify: String? = "",
    @Expose var producteursStringify: String? = "",
    @Expose var themesLabelStringify: String? = "",
    @Expose var themeStringify: String? = "",
    @Expose var module: String? = "",
    @Expose @SerializedName("campagnes_id") var campagneId: Int? = 0,
    @Expose @SerializedName("visiteur")  var visiteurs: String? = null,
    @Expose @SerializedName("formation_type") var formationType: String? = "",
    @Expose @SerializedName("lieu_formation") var lieuFormation: String? = "",
    @Expose @SerializedName("staff") var staffId: String? = "",
    @Expose @SerializedName("duree_formation") var dureeFormation: String? = "",
    @Expose @SerializedName("observation_formation") var observationFormation: String? = "",
    @Expose @SerializedName("multiStartDate") var multiStartDate: String? = "",
    @Expose @SerializedName("multiEndDate") var multiEndDate: String? = "",
    @Expose @SerializedName("photo_formation") var photoFormation: String? = "",
    @Expose @SerializedName("rapport_formation") var rapportFormation: String? = "",

    @Expose @SerializedName("docListePresence") var docListePres: String? = "",
    @Expose @SerializedName("photo_docListePresence") var photoListePresence: String? = "",

    @Expose var section: String = "",
    @Expose var entreprise_id: String = "",
    @Expose var producteursIdStr: String = "",
    @Expose var typeFormationStr: String = "",
    @Expose var themeStr: String? = "",
    @Expose var sousThemeStr: String? = "",
    @Expose var photo_filename: String? = "",
    @Expose var rapport_filename: String? = "",
    @Expose var latitude: String? = "",
    @Expose var longitude: String? = "",
    var photoPath: String? = null,
    @Expose var themeNom: String? = null,
    @Expose var campagneNom: String? = null,
    @Expose var lieuFormationNom: String? = null,
    @Expose var localiteNom: String? = null,
    @Expose var hour: String? = null,
    @Expose var minute: String? = null,
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    var origin: String? = "local"
) : Parcelable {
    @Ignore @SerializedName("producteur") @Expose(serialize = true, deserialize = false) var producteursIdList: MutableList<String>? = null
    @Ignore @SerializedName("type_formation") @Expose(serialize = true, deserialize = false) var typeFormationList: MutableList<String>? = null
    @Ignore @SerializedName("theme") @Expose(serialize = true, deserialize = false) var themeList: MutableList<String>? = null
    @Ignore @SerializedName("sous_theme") @Expose(serialize = true, deserialize = false) var sousThemeList: MutableList<String>? = null

    @Expose(serialize = true, deserialize = false) @SerializedName("visiteurs") @Ignore var visiteursNom: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @SerializedName("producteurs") @Ignore var producteursNom: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @SerializedName("themesLabel") @Ignore var themesLabel: MutableList<String>? = null
    //@Expose(serialize = true, deserialize = false) @SerializedName("module") @Ignore var moduleList: MutableList<String>? = null
}


@Parcelize
data class FormationModelExt(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @Expose var id: Int? = 0,
    @Expose @SerializedName(value = "date_formation", alternate = ["dateFormation"])
    var dateFormation: String? = "",
    @SerializedName(value = "localite", alternate = ["localite_id"]) @Expose var localitesId: String? = "",
    @Expose @SerializedName("campagne_id") var campagneId: Int? = 0,
    @Expose @SerializedName("visiteur")  var visiteurs: String? = null,
    @Expose @SerializedName("formation_type") var formationType: String? = "",
    @Expose @SerializedName("lieu_formation") var lieuFormation: String? = "",
    @Expose @SerializedName("staff") var staffId: String? = "",
    @Expose @SerializedName("duree_formation") var dureeFormation: String? = "",
    @Expose @SerializedName("observation_formation") var observationFormation: String? = "",
    @Expose var date_debut_formation: String? = "",
    @Expose var date_fin_formation: String? = "",
    @Expose @SerializedName("photo_formation") var photoFormation: String? = "",
    @Expose @SerializedName("rapport_formation") var rapportFormation: String? = "",
    @Expose var producteurs_ids: MutableList<String>? = null,
    @Expose var type_formation_ids: MutableList<String>? = null,
    @Expose var theme_ids: MutableList<String>? = null,
    @Expose var sous_themes_ids: MutableList<String>? = null,
    var isSynced: Boolean = false,
    @Expose @SerializedName("user_id", alternate = ["userid"]) var agentId: String? = "",
    var origin: String? = "local"
) : Parcelable {
}
