package ci.projccb.mobile.repositories.datas

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonData (
    @Expose val id: Int? = null,
    @Expose val nom: String? = null,
    @Expose val table: String? = null,
    @Expose val codeapp: String? = null,
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
