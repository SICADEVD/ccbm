package ci.projccb.mobile.models

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

@Entity(tableName = Constants.TABLE_AGENT, indices = [Index(value = ["id"], unique = true)])
data class AgentModel (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose
    @SerializedName("cooperatives_id", alternate = ["cooperative_id"])
    val cooperativesId: Int? = null,
    @Expose
    @SerializedName("created_at")
    val createdAt: String? = null,
    val tp: String? = "",
    @Expose
    @SerializedName("date_naiss")
    val dateNaissance: String? = null,
    @Expose
    @SerializedName("login")
    val login: String? = null,
    @Expose
    @SerializedName("firstname")
    val firstname: String? = null,
    @Expose
    @SerializedName("lastname")
    val lastname: String? = null,
    @Expose
    @SerializedName("username")
    val username: String? = null,
    @Expose
    @SerializedName("user_type")
    val userType: String? = null,
    @Expose
    @SerializedName("type_compte")
    val typeCompte: String? = null,
    @Expose
    @SerializedName("email")
    val email: String? = null,
    @Expose
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String? = null,
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose
    @SerializedName("name")
    val name: String? = null,
    @Expose
    @SerializedName("nationalites")
    val nationalites: String? = null,
    @Expose
    @SerializedName("niveaux_etudes")
    val niveauxEtudes: String? = null,
    @Expose
    @SerializedName("numero_piece")
    val numeroPiece: String? = null,
    @Expose
    @SerializedName("password")
    val password: String? = null,
    @Expose
    @SerializedName("phone1", alternate = ["mobile"])
    val phoneUn: String? = null,
    @Expose
    @SerializedName("phone2")
    val phoneDeux: String? = null,
    @Expose
    @SerializedName("photo", alternate = ["image"])
    val photo: String? = null,
    @Expose
    @SerializedName("sexe")
    val sexe: String? = null,
    @Expose
    @SerializedName("type_pieces")
    val typePieces: String? = null,
    @Expose
    @SerializedName("status")
    val status: String? = null,
    @Expose
    @SerializedName("adresse")
    val adresse: String? = null,
    @Expose @SerializedName("userid") val userId: Int? = null,
    @SerializedName("codeapp") val codeApp: String? = null,
    var isLogged: Boolean = false
)

data class AgentModelExt (
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @Expose
    @SerializedName("cooperatives_id", alternate = ["cooperative_id"])
    val cooperativesId: Int? = null,
    @Expose
    @SerializedName("created_at")
    val createdAt: String? = null,
    val tp: String? = "",
    @Expose
    @SerializedName("date_naiss")
    val dateNaissance: String? = null,
    @Expose
    @SerializedName("login")
    val login: String? = null,
    @Expose
    @SerializedName("firstname")
    val firstname: String? = null,
    @Expose
    @SerializedName("lastname")
    val lastname: String? = null,
    @Expose
    @SerializedName("username")
    val username: String? = null,
    @Expose
    @SerializedName("user_type")
    val userType: String? = null,
    @Expose
    @SerializedName("type_compte")
    val typeCompte: String? = null,
    @Expose
    @SerializedName("email")
    val email: String? = null,
    @Expose
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String? = null,
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose
    @SerializedName("name")
    val name: String? = null,
    @Expose
    @SerializedName("nationalites")
    val nationalites: String? = null,
    @Expose
    @SerializedName("niveaux_etudes")
    val niveauxEtudes: String? = null,
    @Expose
    @SerializedName("numero_piece")
    val numeroPiece: String? = null,
    @Expose
    @SerializedName("password")
    val password: String? = null,
    @Expose
    @SerializedName("phone1", alternate = ["mobile"])
    val phoneUn: String? = null,
    @Expose
    @SerializedName("phone2")
    val phoneDeux: String? = null,
    @Expose
    @SerializedName("photo", alternate = ["image"])
    val photo: String? = null,
    @Expose
    @SerializedName("sexe")
    val sexe: String? = null,
    @Expose
    @SerializedName("type_pieces")
    val typePieces: String? = null,
    @Expose
    @SerializedName("status")
    val status: String? = null,
    @Expose
    @SerializedName("roles")
    val roles: MutableList<ItemForRole> = arrayListOf(),
    @Expose
    @SerializedName("adresse")
    val adresse: String? = null,
    @Expose @SerializedName("userid") val userId: Int? = null,
    @SerializedName("codeapp") val codeApp: String? = null,
    var isLogged: Boolean = false
)

data class ItemForRole(
    @Expose val id: Int = 0,
    @Expose val name: String? = null,
    @Expose val guard_name: String? = null,
)
