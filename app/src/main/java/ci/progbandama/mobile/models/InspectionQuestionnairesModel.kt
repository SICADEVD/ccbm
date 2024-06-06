package ci.progbandama.mobile.models


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose


@Entity(tableName = Constants.TABLE_QUESTIONAIRES, indices = [Index(value = ["uid"], unique = true)])
data class InspectionQuestionnairesModel(
    @Expose @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("questionnairesStringify") @Expose var questionnairesStringify: String? = "",
    @SerializedName("titre") @Expose var titre: String? = ""
) {
    @Ignore @SerializedName("questionnaires") @Expose var questionnaires: MutableList<QuestionModel>? = mutableListOf()
}
