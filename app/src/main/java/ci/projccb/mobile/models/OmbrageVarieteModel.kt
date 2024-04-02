package ci.projccb.mobile.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Entity(tableName = "variete_ombrage")
data class OmbrageVarieteModel (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @Expose var variete: String?,
    @Expose val nombre: String?,
) {
    override fun toString(): String {
        return "$variete : $nombre"
    }
}

data class AdapterItemModel (
    val id: Int,
    val value: String? = "",
    val value1: String? = "",
    val value2: String? = "",
    val value3: String? = "",
    val value4: String? = "",
    val value5: String? = "",
    val value6: String? = "",
    val value7: String? = "",
    val value8: String? = "",
) {
    override fun toString(): String {
        return "$value"
    }
}
