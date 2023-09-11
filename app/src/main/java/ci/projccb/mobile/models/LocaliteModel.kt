package ci.projccb.mobile.models

import android.os.Parcelable
import androidx.room.*
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Entity(tableName = Constants.TABLE_LOCALITES, indices = [Index(value = ["uid"], unique = true)])
@Parcelize
data class LocaliteModel (
    @Expose @PrimaryKey(autoGenerate = true) val uid: Int,
    @Expose @SerializedName("id") val id: Int? = 0,
    @Expose @SerializedName("nom") val nom: String? = "",
    @Expose @SerializedName("cooperatives_id") val cooperativeId: String? = "",
    @Expose @SerializedName("sources_eaux_id") val source: String? = "",
    @Expose @SerializedName("type_localites_id") val type: String? = "",
    @Expose @SerializedName("typeproduit") val typeProduit: String? = "",
    @Expose @SerializedName("sousprefecture") val sousPref: String? = "",
    @Expose @SerializedName("population") val pop: String? = "",
    @Expose @SerializedName("centresante") val centreYesNo: String? = "",
    @Expose @SerializedName("kmCentresante") val centreDistance: String? = "",
    @Expose @SerializedName("nomCentresante") val centreNom: String? = "",
    @Expose @SerializedName("nomEcoleproche") val ecoleNom: String? = "",
    @Expose @SerializedName("nombrecole") val ecoleNbre: String? = "",
    @Expose @SerializedName("kmEcoleproche") val ecoleDistance: String? = "",
    @Expose @SerializedName("ecole") val ecoleYesNo: String? = "",
    @Expose @SerializedName("typecentre") val typeCentre: String? = "",
    @Expose @SerializedName("deversementDechets") val dechetYesNo: String? = "",
    @Expose @SerializedName("associationFemmes") val femmeAsso: String? = "",
    @Expose @SerializedName("associationJeunes") val jeuneAsso: String? = "",
    @Expose @SerializedName("comiteMainOeuvre") val comite: String? = "",
    @Expose @SerializedName("etatpompehydrau") val pompeYesNo: String? = "",
    @Expose @SerializedName("electricite") val cieYesNo: String? = "",
    @Expose @SerializedName("marche") val marcheYesNo: String? = "",
    @Expose @SerializedName("jourmarche") val dayMarche: String? = "",
    @Expose @SerializedName("localongitude") val longitude: String? = "",
    @Expose @SerializedName("nomsEcolesStringify") val nomsEcolesStringify: String? = "",
    @Expose @SerializedName("localatitude") val latitude: String? = "",
    @Expose @SerializedName("kmmarcheproche") val distanceMarche: String? = "",
    val isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = null,
    var origin: String? = "local"
): Parcelable {

    @Ignore @Expose(deserialize = false, serialize = true) @SerializedName("nomecolesprimaires") var ecolesNomsList = mutableListOf<String>()

    override fun toString(): String {
        return nom!!
    }
}
