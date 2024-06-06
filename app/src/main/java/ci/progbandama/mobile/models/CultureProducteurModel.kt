package ci.progbandama.mobile.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Entity(tableName = "culture_producteur")
data class CultureProducteurModel (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @Expose val producteurId: Int?,
    @Expose val label: String?,
    @Expose val superficie: String?,
    @Expose @SerializedName("userid") val agentId: String?
)