package ci.projccb.mobile.models

import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Ignore
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
    @Expose var section: String? = "",
    @Expose var localite: String? = "",
    @Expose var especesarbreStr: String? = "",
    @Expose var quantiteStr: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose @SerializedName("agentId") var agentId: Int? = 0,
): Parcelable {

    @Ignore @SerializedName("especesarbre") @Expose(serialize = true, deserialize = false) var especesarbreList: MutableList<String>? = null
    @Ignore @SerializedName("quantite") @Expose(serialize = true, deserialize = false) var quantiteList: MutableList<String>? = null

}

@Dao
interface EvaluationArbreDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(evaluationArbreModel: EvaluationArbreModel)

    @Transaction
    @Query("SELECT * FROM evaluation_arbre WHERE agentId = :agentID")
    fun getAll(agentID: Int?): MutableList<EvaluationArbreModel>

    @Transaction
    @Query("SELECT * FROM evaluation_arbre WHERE producteurId = :producteurid AND isSynced = 1")
    fun getEvaluationByProducteur(producteurid: String?): MutableList<EvaluationArbreModel>

    @Transaction
    @Query("SELECT * FROM evaluation_arbre WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: Int?): MutableList<EvaluationArbreModel>

    @Transaction
    @Query("UPDATE evaluation_arbre SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM evaluation_arbre WHERE agentId = :agentID")
    fun delete(agentID: Int?)

    @Transaction
    @Query("DELETE FROM evaluation_arbre WHERE uid = :uid")
    fun deleteByUid(uid: Int?)

    @Transaction
    @Query("DELETE FROM evaluation_arbre WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: Int)

    @Transaction
    @Query("DELETE FROM evaluation_arbre WHERE producteurId = :producteurId")
    fun deleteByProducteurId(producteurId: String?)

    @Transaction
    @Query("SELECT * FROM evaluation_arbre WHERE (isSynced = 0 AND producteurId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<EvaluationArbreModel>

}
