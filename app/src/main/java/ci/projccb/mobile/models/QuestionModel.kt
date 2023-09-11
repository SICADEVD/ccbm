package ci.projccb.mobile.models


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class QuestionModel(
    @SerializedName("id")
    @Expose
    var id: Int? = 0,
    @SerializedName("libelle")
    @Expose
    var libelle: String? = ""
)
