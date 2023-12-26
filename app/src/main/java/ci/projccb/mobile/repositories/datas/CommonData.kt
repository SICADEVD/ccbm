package ci.projccb.mobile.repositories.datas

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonData (
    @Expose var id: Int? = null,
    @Expose var nom: String? = null,
    @Expose val table: String? = null,
    @Expose val codeapp: String? = null,
    @Expose val is_different: Boolean? = false,
    @Expose val value: String? = "",
    @Expose @SerializedName("role_name") val role: String? = null,
    @Expose val userid: Int? = null,
    @Expose val typeFormationId: Int? = null,
    @Expose @SerializedName(value = "cooperative_id", alternate = ["cooperatives_id"]) val cooperativeId: Int? = null,
    @Expose @SerializedName(value = "cooperativesid") val cooperativeIdex: Int? = null
) {
    override fun toString(): String {
        return nom!!
    }
}

data class ArbreData (
    @Expose var id: Int? = null,
    @Expose var arbre: String? = null,
    @Expose val nombre: String? = null,
) {
    override fun toString(): String {
        return arbre!!
    }
}

data class InsectesParasitesData (
    @Expose var nom: String? = null,
    @Expose var nombreinsectesParasites: String? = null,
) {
    override fun toString(): String {
        return nom!!
    }
}

data class PresenceAutreInsecteData (
    @Expose var autreInsecteNom: String? = null,
    @Expose var nombreAutreInsectesParasites: String? = null,
) {
    override fun toString(): String {
        return autreInsecteNom!!
    }
}

data class PesticidesAnneDerniereModel (
    @Expose var nom: String? = null,
    @Expose val unite: String? = null,
    @Expose val quantite: String? = null,
    @Expose val contenant: String? = null,
    @Expose val frequence: String? = null,
) {
    override fun toString(): String {
        return nom!!
    }
}

data class PesticidesApplicationModel (
    @Expose var nom: String? = null,
    @Expose val nomCommercial: String? = null,
    @Expose val matiereActive: String? = null,
    @Expose val toxicicologie: String? = null,
    @Expose val dose: String? = null,
    @Expose val frequence: String? = null,
) {
    override fun toString(): String {
        return nom!!
    }
}
