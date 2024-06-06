package ci.progbandama.mobile.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.tools.Constants
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 18/04/2022.
 **/


@Entity(tableName = Constants.TABLE_DRAFTED_DATAS, indices = [Index(value = ["uid"], unique = true)])
data class DataDraftedModel(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val datas: String? = "",
    val typeDraft: String? = "",
    val ownerDraft : String = "",
    val dateDraft: String? = DateTime.now().toString(DateTimeFormat.forPattern("EEEE, 'le' dd MMMM 'à' HH:mm:ss")),
    val agentId: String? = "",
    val draftCompleted: Boolean? = false
) {
    override fun toString(): String {
        return ApiClient.gson.toJson(this)
    }
}
