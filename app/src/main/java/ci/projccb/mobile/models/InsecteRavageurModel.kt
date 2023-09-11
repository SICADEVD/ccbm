package ci.projccb.mobile.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Entity(tableName = "insecte_ravageur")
data class InsecteRavageurModel (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @Expose val nom: String?,
    @Expose val quantite: String?,
)
