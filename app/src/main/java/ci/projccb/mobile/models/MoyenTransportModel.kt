package ci.projccb.mobile.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.gson.annotations.Expose

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Entity(tableName = Constants.TABLE_MOYENS_TRANSPORT, indices = [Index(value = ["id"], unique = true)])
data class MoyenTransportModel (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @Expose val id: Int?,
    @Expose val nom: String?,
    val agentId: String?
) {
    override fun toString(): String {
        return nom!!
    }
}
