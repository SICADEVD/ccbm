package ci.projccb.mobile.models

import ci.projccb.mobile.R

data class FeatureModel(
    val title: String? = null,
    val type: String? = null,
    var image: String = "https://fieldconnectw.sicadevd.com/mobile/",
    var placeholder: Int = R.drawable.producteurc,
    var icon: Int = R.drawable.ic_farmer_b,
    val countDraft: Int = 0,
    val countSync: Int = 0,
    val canAdd: Boolean = false,
    val canEdit: Boolean = false,
    val canViewDraft: Boolean = false,
    val canViewSync: Boolean = false,
    val canViewUpdate: Boolean = false,
    val categorie: Int = 0,
) {
}