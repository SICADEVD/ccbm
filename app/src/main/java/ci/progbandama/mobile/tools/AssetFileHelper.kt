package ci.progbandama.mobile.tools

import android.app.Activity
import ci.progbandama.mobile.models.CourEauModel
import ci.progbandama.mobile.models.EauUseeModel
import ci.progbandama.mobile.models.GardeMachineModel
import ci.progbandama.mobile.models.IntrantModel
import ci.progbandama.mobile.models.LieuFormationModel
import ci.progbandama.mobile.models.NationaliteModel
import ci.progbandama.mobile.models.NiveauModel
import ci.progbandama.mobile.models.NotationModel
import ci.progbandama.mobile.models.OrdureMenagereModel
import ci.progbandama.mobile.models.PaiementMobileModel
import ci.progbandama.mobile.models.PersonneBlesseeModel
import ci.progbandama.mobile.models.RecuModel
import ci.progbandama.mobile.models.SourceEauModel
import ci.progbandama.mobile.models.SourceEnergieModel
import ci.progbandama.mobile.models.SousThemeFormationModel
import ci.progbandama.mobile.models.ThemeFormationModel
import ci.progbandama.mobile.models.TypeDocumentModel
import ci.progbandama.mobile.models.TypeLocaliteModel
import ci.progbandama.mobile.models.TypeMachineModel
import ci.progbandama.mobile.models.TypePieceModel
import ci.progbandama.mobile.models.TypeProduitModel
import ci.progbandama.mobile.repositories.datas.CommonData
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class AssetFileHelper {



    companion object{


        val listAssetFile = arrayListOf<String>(
            "cours_eaux",
            "eaux_usees",
            "garde_machines",
            "geres_recus",
            "lieu_formations",
            "nationalites",
            "niveaux",
            "ordures_menageres",
            "paiement_mobile",
            "themes_formations",
            "type_documents",
            "type_intrants",
            "type_produits",
            "type_pieces",
            "personne_blessee",
            "type_localites",
            "sources_eaux",
            "sources_energies",
            "type_machines",
            "notations",
            "liste_certificat",
            "liste_variete",
            "lieu_habite",
            "statut_matrimonial",
            "type_membre",
            "arbre_ombrage",
            "titre_producteur",
            "type_css",
            "sous_themes_formations",
            "approbat_inpect",
        );


        /**
           *     @return:
         *           0 = "cours_eaux",
           *         1 = "eaux_usees",
           *         2 = "garde_machines",
           *         3 = "geres_recus",
           *         4 = "lieu_formations",
           *         5 = "nationalites",
           *         6 = "niveaux",
           *         7 = "ordures_menageres",
           *         8 = "paiement_mobile",
           *         9 = "themes_formations",
           *         10 = "type_documents",
           *         11 = "type_intrants",
           *         12 = "type_produits"
           *         13 = "type_pieces",
           *         14 = "personne_blessee",
           *         15 = "type_localites",
           *         16 = "sources_eaux",
           *         17 = "sources_energies",
           *         18 = "type_machines",
           *         19 = "notations",
           *         20 = "liste_certificat",
           *         21 = "liste_variete",
           *         22 = "lieu_habite",
           *         23 = "statut_matrimonial",
           *         24 = "type_membre",
           *         25 = "arbre_ombrage",
           *         26 = "titre_producteur",
           *         27 = "type_css",
           *         28 = "sous_themes_formations",
           *         29 = "approbat_inpect",
        */
        fun getListDataFromAsset(position: Int = 0, context: Activity): MutableList<*>? {
            val typer : Type? = when(position) {
                0 -> object : TypeToken<MutableList<CourEauModel>>() {}.type
                1 -> object : TypeToken<MutableList<EauUseeModel>>() {}.type
                2 -> object : TypeToken<MutableList<GardeMachineModel>>() {}.type
                3 -> object : TypeToken<MutableList<RecuModel>>() {}.type
                4 -> object : TypeToken<MutableList<LieuFormationModel>>() {}.type
                5 -> object : TypeToken<MutableList<NationaliteModel>>() {}.type
                6 -> object : TypeToken<MutableList<NiveauModel>>() {}.type
                7 -> object : TypeToken<MutableList<OrdureMenagereModel>>() {}.type
                8 -> object : TypeToken<MutableList<PaiementMobileModel>>() {}.type
                9 -> object : TypeToken<MutableList<ThemeFormationModel>>() {}.type
                10 -> object : TypeToken<MutableList<TypeDocumentModel>>() {}.type
                11 -> object : TypeToken<MutableList<IntrantModel>>() {}.type
                12 -> object : TypeToken<MutableList<TypeProduitModel>>() {}.type
                13 -> object : TypeToken<MutableList<TypePieceModel>>() {}.type
                14 -> object : TypeToken<MutableList<PersonneBlesseeModel>>() {}.type
                15 -> object : TypeToken<MutableList<TypeLocaliteModel>>() {}.type
                16 -> object : TypeToken<MutableList<SourceEauModel>>() {}.type
                17 -> object : TypeToken<MutableList<SourceEnergieModel>>() {}.type
                18 -> object : TypeToken<MutableList<TypeMachineModel>>() {}.type
                19 -> object : TypeToken<MutableList<NotationModel>>() {}.type
                20 -> object : TypeToken<MutableList<CommonData>>() {}.type
                21 -> object : TypeToken<MutableList<CommonData>>() {}.type
                22 -> object : TypeToken<MutableList<CommonData>>() {}.type
                23 -> object : TypeToken<MutableList<CommonData>>() {}.type
                24 -> object : TypeToken<MutableList<CommonData>>() {}.type
                25 -> object : TypeToken<MutableList<CommonData>>() {}.type
                26 -> object : TypeToken<MutableList<CommonData>>() {}.type
                27 -> object : TypeToken<MutableList<CommonData>>() {}.type
                28 -> object : TypeToken<MutableList<SousThemeFormationModel>>() {}.type
                29 -> object : TypeToken<MutableList<CommonData>>() {}.type
                else -> {
                    null
                }
            }
            return GsonUtils.fromJson(
                Commons.loadJSONFromAsset(
                    context,
                    listAssetFile[position].toString().plus(".json")
                ), typer!!
            )
        }
    }


}