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
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = Constants.TABLE_ARBRE_LIST,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        )
    ]
)
@Parcelize
data class ArbreModel(
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") @Expose var uid: Int,
    @Expose var id: Int? = null,
    @SerializedName("nom_scientifique") @Expose var nomScientifique: String? = "",
    @Expose var nom: String? = "",
    @Expose var strate: String? = "",
): Parcelable {

}

@Dao
interface ArbreDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(arbreModel: ArbreModel)

    @Transaction
    @Query("SELECT * FROM arbre_list")
    fun getAll(): MutableList<ArbreModel>

    @Transaction
    @Query("DELETE FROM arbre_list")
    fun deleteAll()

}
