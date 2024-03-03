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

@Entity(tableName = Constants.TABLE_LIVRAISON_CENTRAL, indices = [Index(value = ["uid"], unique = true)])
@Parcelize
data class LivraisonCentralModel(
    @Expose @PrimaryKey(autoGenerate = true) var uid: Int,
    @Expose val id: Int? = 0,
    @Expose val programme_id: String? = null,
    @SerializedName("dateLivre")
    @Expose var dateLivre: String? = "",
    @SerializedName("nomDelegue") @Expose var delegueId: String? = "",
    @Expose var delegueNom: String? = "",
    @Expose var typeProduit: String? = "",
    @SerializedName("producteurs_id") @Expose var producteursId: String? = "",
    @Expose var producteurNom: String? = "",
    @SerializedName("campagnes_id") @Expose var campagneId: String? = "",
    @Expose var campagneNom: String? = "",
    @SerializedName("parcelles_id") @Expose var parcelleId: String? = "",
    @Expose var parcelleNom: String? = "",
    @SerializedName("quantiteLivre")
    @Expose var quantiteLivre: String? = "",
    @Expose var localiteNom: String? = "",
    var isSynced: Boolean = false,
    @Expose @SerializedName("userid") var agentId: String? = "",
    @Expose @SerializedName("magasinsections_id") var magasinSectionId: String? = "",
    @Expose var magasinSectionLabel: String? = "",
    @SerializedName("cooperative_id") @Expose var cooperativeId: String? = "",
    @SerializedName("estimate_date") @Expose var estimatDate: String? = "",
    @SerializedName("payment_status") @Expose var paymentStatus: String? = "",
    @SerializedName("sender_magasin") @Expose var senderMagasin: String? = "",
    @SerializedName("sender_name") @Expose var senderName: String? = "",
    @SerializedName("sender_phone") @Expose var senderPhone: String? = "",
    @SerializedName("sender_email") @Expose var senderEmail: String? = "",
    @SerializedName("sender_address") @Expose var senderAddress: String? = null,
    @SerializedName("receiver_name") @Expose var receiverName: String? = "",
    @SerializedName("receiver_phone") @Expose var receiverPhone: String? = "",
    @SerializedName("receiver_email") @Expose var receiverEmail: String? = "",
    @SerializedName("receiver_address") @Expose var receiverAddress: String? = null,
    @SerializedName("reduction") @Expose var reduction: String? = null,
    @SerializedName("total_reduce") @Expose var totalReduce: String? = null,
    @SerializedName("sous_total_reduce") @Expose var sousTotalReduce: String? = null,
    @SerializedName("magasin_central") @Expose var magasinCentral: String? = null,
     @Expose var typeStr: String? = null,
     @Expose var producteur_idStr: String? = null,
     @Expose var producteursStr: String? = null,
     @Expose var parcelleStr: String? = null,
     @Expose var certificatStr: String? = null,
     @Expose var typeproduitStr: String? = null,
     @Expose var quantiteStr: String? = null,
     @Expose var poidsnet: String? = null,
     @Expose var nombresacs: String? = null,
     @Expose var entreprise_id: String? = null,
     @Expose var sender_vehicule: String? = null,
     @Expose var sender_transporteur: String? = null,
     @Expose var sender_remorque: String? = null,
     @Expose var itemsStringify: String? = null,
     @Expose var code: String? = null,
    var origin: String? = "local"
) : Parcelable {
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("type") var typeList: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("producteur_id") var producteur_idList: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("producteurs") var producteursList: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("parcelle") var parcelleList: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("certificat") var certificatList: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("typeproduit") var typeproduitList: MutableList<String>? = null
    @Expose(serialize = true, deserialize = false) @Ignore @SerializedName("quantite") var quantiteList: MutableList<String>? = null
}

@Dao
interface LivraisonCentralDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(livraisonCentralModel: LivraisonCentralModel)

    @Transaction
    @Query("SELECT * FROM livraison_central WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<LivraisonCentralModel>

    @Transaction
    @Query("SELECT * FROM livraison_central WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<LivraisonCentralModel>

    @Transaction
    @Query("UPDATE livraison_central SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("DELETE FROM livraison_central WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM livraison_central WHERE uid = :uId")
    fun deleteByUid(uId: String?)

    @Transaction
    @Query("SELECT * FROM livraison_central WHERE (isSynced = 0 AND producteursId = :producteurUid)")
    fun getUnSyncedByProdUid(producteurUid: String?): MutableList<LivraisonCentralModel>
}
