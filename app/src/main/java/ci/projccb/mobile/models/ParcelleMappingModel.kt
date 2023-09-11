package ci.projccb.mobile.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import ci.projccb.mobile.tools.Constants
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize


@Entity(tableName = Constants.TABLE_PARCELLES_MAPPING,
    indices = [
        Index(
            value = ["uid"], unique = true
        )
    ]
)
@Parcelize
data class ParcelleMappingModel(
    @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    var id: Int? = 0,
    var parcelleName: String? = "",
    var parcelleNameTag: String? = "",
    var parcelleSuperficie: String? = "",
    var parcellePerimeter: String? = "",
    var typeDeclaration: String? = "",
    var parcelleWayPoints: String? = "",
    var parcelleLat: String? = "",
    var parcelleLng: String? = "",
    var parcelleID: String? = "",
    var producteurId: String? = ""
) : Parcelable {

    @Ignore var mutableWayPoints: MutableList<LatLng>? = mutableListOf()

}
