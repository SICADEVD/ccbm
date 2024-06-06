package ci.progbandama.mobile.models

import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Entity
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
    tableName = Constants.TABLE_POSTPLANTING_DISTRIB,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class PostPlantingArbrDistribModel(
    @Expose val id: Int? = 0,
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @Expose var nom: String? = "",
    @Expose var prenoms: String? = "",
    @Expose var arbresStr: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable{
}

@Parcelize
data class PostPlantingArbrDistribSecModel(
    @Expose val id: Int? = 0,
    @Expose var nom: String? = "",
    @Expose var prenoms: String? = "",
    @Expose var arbres: MutableList<PostPlantingItem>? = null,
): Parcelable{
}

@Parcelize
data class ListeEspeceArbrePostPlantModel(
    @Expose val arbre_id: Int? = 0,
    @Expose var nom_arbre: String? = "",
    @Expose var qte_recu: String? = "",
    @Expose var qte_plant: String? = "",
    @Expose var qte_survec: String? = "",
    @Expose var commentaire: String? = "",
): Parcelable{
}

@Parcelize
data class QuantitePostPlanting(
    val variableModel: MutableList<PostPlantingItem>
): Parcelable

@Parcelize
data class PostPlantingItem(
    @Expose var id_arbre: String? = null,
    @Expose var quantite: String? = null
): Parcelable

@Dao
interface PostPlantingArbrDistribDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postPlantingArbrDistribModel: PostPlantingArbrDistribModel)

    @Transaction
    @Query("SELECT * FROM post_planting_distrib WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<PostPlantingArbrDistribModel>

    @Transaction
    @Query("SELECT * FROM post_planting_distrib WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<PostPlantingArbrDistribModel>

    @Transaction
    @Query("UPDATE post_planting_distrib SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM post_planting_distrib WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)
    @Transaction
    @Query("DELETE FROM post_planting_distrib")
    fun deleteAll()

    @Transaction
    @Query("DELETE FROM post_planting_distrib WHERE uid = :uid")
    fun deleteByUid(uid: Int?)
    @Transaction
    @Query("DELETE FROM post_planting_distrib WHERE id = :pid")
    fun deleteById(pid: String?)

    @Transaction
    @Query("SELECT * FROM post_planting_distrib WHERE id = :producteurId")
    fun getPostPlantByPId(producteurId: String?): PostPlantingArbrDistribModel
}
