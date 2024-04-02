package ci.projccb.mobile.models

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
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = Constants.TABLE_DISTRIBUTION_ARBRE,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class DistributionArbreModel(
    @Expose val id: Int? = 0,
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @SerializedName("producteur") @Expose var producteurId: String? = "",
    @SerializedName("parcelle") @Expose var parcelleId: String? = "",
    @SerializedName("nombre_arbre_denombre") @Expose var nombreDarbreDenombre: String? = "",
    @Expose var section: String? = "",
    @Expose var localite: String? = "",
    @Expose var quantiteStr: String? = "",
    @Expose var qtelivre: String? = "",
    @Expose var total: String? = "",
    @Expose var agroapprovisionnementsection: String? = "0",
    @Expose var listNomArbreDistribueStr: String? = "",
    @Expose var listQuantiteArbreDistribueStr: String? = "",
    @Expose var listStrateArbreDistribueStr: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable{

    @Ignore @SerializedName("quantite") @Expose(serialize = true, deserialize = false) var quantiteList: Map<String, Map<String, String>>? = null

}

@Parcelize
data class QuantiteArbrDistribuer(
    @Expose val evaluations: MutableList<ItemDistribuer>
): Parcelable

@Parcelize
data class ItemDistribuer(
    @Expose val agroespecesarbre_id: String? = null,
    @Expose val producteur_id: String? = null,
    @Expose val total: String? = null,
): Parcelable

@Parcelize
data class QuantiteDistribuer(
    val variableKey: Map<String, Map<String, String>>
): Parcelable

@Dao
interface DistributionArbreDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(distributionArbreModel: DistributionArbreModel)

    @Transaction
    @Query("SELECT * FROM distribution_arbre WHERE userid = :agentID")
    fun getAll(agentID: String?): MutableList<DistributionArbreModel>

    @Transaction
    @Query("SELECT * FROM distribution_arbre WHERE isSynced = 0 AND userid = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<DistributionArbreModel>

    @Transaction
    @Query("UPDATE distribution_arbre SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM distribution_arbre WHERE userid = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM distribution_arbre WHERE uid = :uid")
    fun deleteByUid(uid: Int?)

    @Transaction
    @Query("SELECT * FROM distribution_arbre WHERE isSynced = 1 AND producteurId = :producteurId")
    fun getDistributionByProducteur(producteurId: String): MutableList<DistributionArbreModel>

    @Transaction
    @Query("SELECT * FROM distribution_arbre WHERE (isSynced = 0 AND producteurId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<DistributionArbreModel>
    @Transaction
    @Query("SELECT * FROM distribution_arbre WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAllLive(agentID: String?): LiveData<MutableList<DistributionArbreModel>>
}
