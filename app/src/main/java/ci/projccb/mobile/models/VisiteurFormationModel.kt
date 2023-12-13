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
    @Expose var section: String? = "",
    @Expose var localite: String? = "",
    @Expose var nom: String? = "",
    @Expose var prenom: String? = "",
    @Expose var sexe: String? = "",
    @Expose var telephone: String? = "",
    @Expose var representer: String? = "",
    @Expose var lien: String? = "",
    @Expose var autre_lien: String? = "",
    @Expose var suivi_formation_id: String? = "",
    var origin: String? = "local",
    var isSynced: Boolean = false,
    @SerializedName("agentId") @Expose var agentId: Int? = 0,
): Parcelable {

}

@Dao
interface VisiteurFormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(visiteurFormationModel: VisiteurFormationModel)

    @Transaction
    @Query("SELECT * FROM visiteur_formation WHERE agentId = :agentID")
    fun getAll(agentID: Int?): MutableList<VisiteurFormationModel>

    @Transaction
    @Query("SELECT * FROM visiteur_formation WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: Int?): MutableList<VisiteurFormationModel>

    @Transaction
    @Query("UPDATE visiteur_formation SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM visiteur_formation WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: Int?)

}
