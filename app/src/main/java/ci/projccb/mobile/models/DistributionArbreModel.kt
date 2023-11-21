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
    @Expose var listNomArbreDistribueStr: String? = "",
    @Expose var listQuantiteArbreDistribueStr: String? = "",
    @Expose var listStrateArbreDistribueStr: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable{

    @SerializedName("list_nom_arbre_distribue") @Expose(serialize = true, deserialize = false) @Ignore
    var listNomArbreDistribue: MutableList<String> = mutableListOf()

    @SerializedName("list_quantite_arbre_distribue") @Expose(serialize = true, deserialize = false) @Ignore
    var listQuantiteArbreDistribue: MutableList<String> = mutableListOf()

    @SerializedName("list_strate_arbre_distribue") @Expose(serialize = true, deserialize = false) @Ignore
    var listStrateArbreDistribue: MutableList<String> = mutableListOf()

}

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
}
