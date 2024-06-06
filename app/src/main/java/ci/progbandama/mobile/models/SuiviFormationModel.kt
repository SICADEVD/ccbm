package ci.progbandama.mobile.models


import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


//@Entity(tableName = Constants.TABLE_FORMATIONS)
data class SuiviFormationModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    @Expose @SerializedName("id") var id: Int? = 0,
    @Expose @SerializedName("dateFormation")
    var dateFormation: String? = "",
    @Expose @SerializedName("lieu_formations_id")
    var lieuFormationsId: String? = "",
    @Expose @SerializedName("section") var sectionId: String? = "",
    @Expose @SerializedName("localite") var localitesId: String? = "",
    @Expose @SerializedName("formation_type") var formationType: String? = "",
    @Expose @SerializedName("lieu_formation") var lieuFormation: String? = "",
    @Expose @SerializedName("staff") var staffId: String? = "",
    @Expose @SerializedName("duree_formation") var dureeFormation: String? = "",
    @Expose @SerializedName("observation_formation") var observationFormation: String? = "",
    @Expose @SerializedName("multiStartDate") var multiStartDate: String? = "",
    @Expose @SerializedName("multiEndDate") var multiEndDate: String? = "",
    @Expose @SerializedName("photo_formation") var photoFormation: String? = "",
    @Expose @SerializedName("rapport_formation") var rapportFormation: String? = "",
    @Expose var producteursIdStr: String = "",
    @Expose var typeFormationStr: String = "",
    @Expose var themeStr: String? = "",
    @Expose var sousThemeStr: String? = "",
    @Expose var photo_filename: String? = "",
    @Expose var rapport_filename: String? = "",
    @Expose @SerializedName("userid") var usersId: Int? = 0,
    @Expose(serialize = false, deserialize = false) var isSynced: Boolean = false,
) {
    @Ignore @SerializedName("producteur") @Expose(serialize = true, deserialize = false) var producteursIdList: MutableList<String>? = null
    @Ignore @SerializedName("type_formation") @Expose(serialize = true, deserialize = false) var typeFormationList: MutableList<String>? = null
    @Ignore @SerializedName("theme") @Expose(serialize = true, deserialize = false) var themeList: MutableList<String>? = null
    @Ignore @SerializedName("sous_theme") @Expose(serialize = true, deserialize = false) var sousThemeList: MutableList<String>? = null
}