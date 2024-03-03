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

@Entity(tableName = Constants.TABLE_LIVRAISON_VER_MAG_CENTRAL, indices = [Index(value = ["uid"], unique = true)])
@Parcelize
data class LivraisonVerMagCentralModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @Expose val id: Int? = 0,
    @Expose val section: String? = null,
    @Expose val parcelle: String? = null,
    @Expose val producteur: String? = null,
    @Expose val magasinier: String? = null,
    @Expose val magasinSection: String? = null,
    @Expose val codeMagasinSection: String? = null,
    @Expose val Delegue: String? = null,
    @Expose val typeProduit: String? = null,
    @Expose val certificat: String? = null,
    @Expose val quantiteMagasinSection: String? = null,
    @Expose val quantiteLivreMagCentral: String? = null,
    @Expose val nom: String? = null,
    @Expose val prenoms: String? = null,
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    var origin: String? = "local"
) : Parcelable {
}

@Dao
interface LivraisonVerMagCentralDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(livraisonVerMagCentralModel: LivraisonVerMagCentralModel)

    @Transaction
    @Query("SELECT * FROM livraison_ver_mag_central WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<LivraisonVerMagCentralModel>

    @Transaction
    @Query("SELECT * FROM livraison_ver_mag_central WHERE typeProduit = :typeproduit AND magasinSection = :magsectnom")
    fun getLivraisonByTypeProdAndMagSection(typeproduit: String?, magsectnom: String?): MutableList<LivraisonVerMagCentralModel>

    @Transaction
    @Query("SELECT * FROM livraison_ver_mag_central WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<LivraisonVerMagCentralModel>

    @Transaction
    @Query("UPDATE livraison_ver_mag_central SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM livraison_ver_mag_central")
    fun deleteAll()
}
