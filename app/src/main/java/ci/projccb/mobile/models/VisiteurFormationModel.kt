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
import ci.projccb.mobile.activities.forms.VisiteurFormationActivity
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = Constants.TABLE_VISTEUR_FORMATION,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class VisiteurFormationModel(
    @Expose val id: Int? = 0,
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @SerializedName("producteur") @Expose var producteurId: String? = "",
    @SerializedName("formation") @Expose var formationId: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("userid") @Expose var userid: Int? = 0,
    @Expose @SerializedName("agentId") var agentId: String? = "",
): Parcelable {

}

@Dao
interface VisiteurFormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(visiteurFormationModel: VisiteurFormationModel)

    @Transaction
    @Query("SELECT * FROM visiteur_formation WHERE userid = :agentID")
    fun getAll(agentID: String?): MutableList<VisiteurFormationModel>

    @Transaction
    @Query("SELECT * FROM visiteur_formation WHERE isSynced = 0 AND userid = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<VisiteurFormationModel>

    @Transaction
    @Query("UPDATE visiteur_formation SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM visiteur_formation WHERE userid = :agentID")
    fun deleteAgentDatas(agentID: String?)

}
