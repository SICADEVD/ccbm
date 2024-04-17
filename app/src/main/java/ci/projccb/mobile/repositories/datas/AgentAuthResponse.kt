package ci.projccb.mobile.repositories.datas

import ci.projccb.mobile.models.AgentModel
import ci.projccb.mobile.models.AgentModelExt
import ci.projccb.mobile.models.CoopModel
import com.google.gson.annotations.Expose

data class AgentAuthResponse(
    @Expose val access_token: String,
    @Expose val message: String,
    @Expose val results: AgentModelExt,
    @Expose val cooperative: CoopModel,
    @Expose val status_code: Int,
    @Expose val menu: MutableList<String>,
    @Expose val token_type: String
)
