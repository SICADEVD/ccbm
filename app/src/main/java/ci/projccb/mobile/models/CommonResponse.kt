package ci.projccb.mobile.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @Expose @SerializedName("message") var message: String? = "",
    @Expose @SerializedName("status_code") var statusCode: Int? = 0
)