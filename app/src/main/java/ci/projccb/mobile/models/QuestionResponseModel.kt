package ci.projccb.mobile.models

import com.google.gson.annotations.Expose

/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 19/05/2022.
 **/
data class QuestionResponseModel(
    @Expose var id: String? = "",
    @Expose var id_en_base: String? = "",
    @Expose var label: String? = "",
    @Expose var note: String? = "",
    @Expose var noteLabel: String? = "",
    @Expose var commentaire: String? = "",
    @Expose var commentaireLast: String? = "",
    @Expose var delai: String? = "",
    @Expose var date_verification: String? = "",
    @Expose var statuts: String? = "",
    @Expose var reponseId: Int? = 0,
    @Expose var isTitle: Boolean? = false
)
