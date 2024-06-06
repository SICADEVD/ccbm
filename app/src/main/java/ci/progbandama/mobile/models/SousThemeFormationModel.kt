package ci.progbandama.mobile.models


import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import ci.progbandama.mobile.tools.Constants
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

@Entity(tableName = Constants.TABLE_SOUS_THEMES_FORMATION, indices = [Index(value = ["id"], unique = true)])
data class SousThemeFormationModel(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @SerializedName("id") @Expose var id: Int? = 0,
    @SerializedName("nom") @Expose var nom: String? = "",
    @SerializedName("agentId") @Expose var agentId: String? = "",
    @SerializedName("theme_formation_id") @Expose var themeFormationsId: Int? = 0
) {
    override fun toString(): String {
        return nom!!
    }
}

@Dao
interface SousThemeFormationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sousThemeFormationModel: SousThemeFormationModel)

    @Transaction
    @Query("SELECT * FROM sous_theme_formation WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<SousThemeFormationModel>

    @Transaction
    @Query("DELETE FROM sous_theme_formation")
    fun deleteAll()
}

