package ci.progbandama.mobile.repositories.datas

import ci.progbandama.mobile.models.AgentModelExt
import ci.progbandama.mobile.models.CoopModel
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
