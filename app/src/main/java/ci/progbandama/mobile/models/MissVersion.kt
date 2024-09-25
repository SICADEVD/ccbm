package ci.progbandama.mobile.models

data class MissVersion(
    val latestVersion: String? = null,
    val latestVersionCode: String? = null,
    val url: String? = null,
    val releaseNotes: MutableList<String>? = arrayListOf(),
)
