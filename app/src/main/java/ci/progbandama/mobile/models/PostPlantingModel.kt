package ci.progbandama.mobile.models

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = Constants.TABLE_POSTPLANTING,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class PostPlantingModel(
    @Expose val id: Int? = 0,
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @Expose var section: String? = "",
    @Expose var localite: String? = "",
    @SerializedName("producteur") @Expose var producteurId: String? = "",
    @Expose var total: String? = "",
    @Expose var qteplante: String? = "",
    @Expose var qtesurvecue: String? = "",
    @Expose var date_planting: String? = "",
    @Expose var quantiterecueStr: String? = "",
    @Expose var quantiteStr: String? = "",
    @Expose var quantitesurvecueeStr: String? = "",
    @Expose var commentaireStr: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable{

    @Ignore @SerializedName("quantiterecue") @Expose(serialize = true, deserialize = false) var quantiterecueList: Map<String, Map<String, String>>? = null
    @Ignore @SerializedName("quantite") @Expose(serialize = true, deserialize = false) var quantiteList: Map<String, Map<String, String>>? = null
    @Ignore @SerializedName("quantitesurvecuee") @Expose(serialize = true, deserialize = false) var quantitesurvecueeList: Map<String, Map<String, String>>? = null
    @Ignore @SerializedName("commentaire") @Expose(serialize = true, deserialize = false) var commentaireList: Map<String, Map<String, String>>? = null

}

//@Parcelize
//data class QuantitePostPlanting(
//    val variableKey: Map<String, Map<String, String>>
//): Parcelable

@Dao
interface PostplantingDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postPlantingModel: PostPlantingModel)

    @Transaction
    @Query("SELECT * FROM post_planting WHERE userid = :agentID")
    fun getAll(agentID: String?): MutableList<PostPlantingModel>

    @Transaction
    @Query("SELECT * FROM post_planting WHERE isSynced = 0 AND userid = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<PostPlantingModel>

    @Transaction
    @Query("UPDATE post_planting SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM post_planting WHERE userid = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM post_planting WHERE uid = :uid")
    fun deleteByUid(uid: Int?)

    @Transaction
    @Query("SELECT * FROM post_planting WHERE isSynced = 1 AND producteurId = :producteurId")
    fun getDistributionByProducteur(producteurId: String): MutableList<PostPlantingModel>

    @Transaction
    @Query("SELECT * FROM post_planting WHERE (isSynced = 0 AND producteurId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<PostPlantingModel>
    @Transaction
    @Query("SELECT * FROM post_planting WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAllLive(agentID: String?): LiveData<MutableList<PostPlantingModel>>
}
