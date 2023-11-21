package ci.projccb.mobile.models

import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = Constants.TABLE_EVALUATION_ARBRE,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class EvaluationArbreModel(
    @Expose val id: Int? = 0,
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @SerializedName("producteur") @Expose var producteurId: String? = "",
    @SerializedName("parcelle") @Expose var parcelleId: String? = "",
    @SerializedName("nombre_arbre_denombre") @Expose var nombreDarbreDenombre: String? = "",
    @SerializedName("superficie") @Expose var superficie: String? = "",
    @SerializedName("nombre_arbre") @Expose var nombreArbre: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable {

}

@Dao
interface EvaluationArbreDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(evaluationArbreModel: EvaluationArbreModel)

    @Transaction
    @Query("SELECT * FROM evaluation_arbre WHERE userid = :agentID")
    fun getAll(agentID: String?): MutableList<EvaluationArbreModel>

    @Transaction
    @Query("SELECT * FROM evaluation_arbre WHERE isSynced = 0 AND userid = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<EvaluationArbreModel>

    @Transaction
    @Query("UPDATE evaluation_arbre SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM evaluation_arbre WHERE userid = :agentID")
    fun deleteAgentDatas(agentID: String?)

}
