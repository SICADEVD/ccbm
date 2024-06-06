package ci.progbandama.mobile.tools

object Roles{

    val MANAGER = listOf<String>(
        "PRODUCTEUR",
        "PARCELLE",
        "ESTIMATION",
        "MENAGE",
        "PARCELLES",
        "FORMATION",
        "INSPECTION",
        "APPLICATION",
        "SSRTECLMRS",
        "AGRO_DISTRIBUTION",
        "AGRO_EVALUATION",
        "LIVRAISON",
        "LIVRAISON_MAGCENTRAL",
        "FORMATION_VISITEUR",
        "POSTPLANTING"
    )

    val COACH = listOf<String>(
        "PRODUCTEUR",
        "PARCELLE",
        "ESTIMATION",
        "MENAGE",
        "PARCELLES",
        "FORMATION",
        "INSPECTION",
        "APPLICATION",
//        "LIVRAISON",
        "SSRTECLMRS",
        "AGRO_DISTRIBUTION",
        "FORMATION_VISITEUR",
        "AGRO_EVALUATION",
        "POSTPLANTING"
    )

    val INSPECTEUR = listOf<String>(
        "PRODUCTEUR",
        "PARCELLE",
        "ESTIMATION",
        "PARCELLES",
        "FORMATION",
        "INSPECTION",
        "APPLICATION",
        "FORMATION_VISITEUR",
    )

    val APPLICATEUR = listOf<String>(
        "APPLICATION",
    )

    val MAGASINIERSECTION = listOf<String>(
        "LIVRAISON",
    )

    val MAGASINIERCENTRAL = listOf<String>(
        "LIVRAISON",
        "LIVRAISON_MAGCENTRAL",
    )

    val DELEGUE = listOf<String>(
        "LIVRAISON",
    )
}
