package ci.projccb.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.SuiviParcelleActivity
import ci.projccb.mobile.adapters.PreviewItemAdapter
import ci.projccb.mobile.models.SuiviParcelleModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_producteur_preview.recyclerInfoPrev
import kotlinx.android.synthetic.main.activity_suivi_parcelle_preview.*


class SuiviParcellePreviewActivity : AppCompatActivity() {


    var suiviParcelleDatas = SuiviParcelleModel()
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0


    fun saveParcelle() {
        Commons.showMessage(
            "Etes-vous sur de vouloir faire ce enregistrement ?",
            this,
            showNo = true,
            callback = {
                CcbRoomDatabase.getDatabase(this)?.suiviParcelleDao()?.insert(suiviParcelleDatas)
                draftDao?.completeDraft(draftID)
                Commons.synchronisation(type = "suivi", this)
                Commons.showMessage(
                    "Suivi de parcelle effectué avec succes !",
                    this,
                    finished = true,
                    callback = {})
            },
            finished = false
        )

        ActivityUtils.finishActivity(SuiviParcelleActivity::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_parcelle_preview)

        intent?.let {
            try {
                suiviParcelleDatas = it.getParcelableExtra("preview")!!
                draftID = it.getIntExtra("draft_id", 0)
//
//                LogUtils.e(Commons.TAG, "Datas -> ${suiviParcelleDatas.parcelleNom}")
//
//                labelProducteurNomSuiviPreview.text = suiviParcelleDatas.parcelleProducteur
//                labelParcelleNomSuiviPreview.text = suiviParcelleDatas.parcelleNom
//                labelCampagneSuiviPreview.text = suiviParcelleDatas.campagneNom
//                labelPresenceCoursEauSuiviPreview.text = suiviParcelleDatas.existeCoursEaux
//                labelPresencePenteSuiviPreview.text = suiviParcelleDatas.pente
//                labelNbreArbreOmbrageSuiviPreview.text = suiviParcelleDatas.nombreOmbrage?.size.toString()
//
//                suiviParcelleDatas.ombrages?.map { ombrageArbre ->
//                    labelArbreOmbrageListSuiviPreview.text = labelArbreOmbrageListSuiviPreview.text.toString().plus(ombrageArbre.variete)
//                }
//
//                //labelOmbrageSouhaiteeSuiviPreview.text = suiviParcelleDatas.varietesOmbrage.
//                labelPresenceBioAgresseursSuiviPreview.text = suiviParcelleDatas.presenceBioAgresseur
//                labelActiviteTailleuiviPreview.text = suiviParcelleDatas.activiteTaille
//                labelActiviteEngourmandSuiviPreview.text = suiviParcelleDatas.activiteEgourmandage
//                labelActiviteDesherbageManuelSuiviPreview.text = suiviParcelleDatas.activiteDesherbageManuel
//                labelActiviteRecolteSanitaireSuiviPreview.text = suiviParcelleDatas.activiteRecolteSanitaire
//                labelBenefAgroSuiviPreview.text = suiviParcelleDatas.arbresAgroForestiersYesNo
//                labelPresencePourritureSuiviPreview.text = suiviParcelleDatas.presencePourritureBrune
//
//                labelPresenceInsecteRavageurSuiviPreview.text = suiviParcelleDatas.presenceInsectesRavageurs
//                labelPresenceFormisRougeSuiviPreview.text = suiviParcelleDatas.presenceFourmisRouge
//                labelPresenceAraigneeSuiviPreview.text = suiviParcelleDatas.presenceAraignee
//                labelPresenceVerDeTerreuiviPreview.text = suiviParcelleDatas.presenceVerTerre
//                labelPresenceManteReligieuseSuiviPreview.text = suiviParcelleDatas.presenceMenteReligieuse
//
//                labelInsecticideUtiliseSuiviPreview.text = suiviParcelleDatas.nomInsecticide?.plus(" : ")?.plus(suiviParcelleDatas.nombreInsecticide)
//                labelFongicideUtiliseSuiviPreview.text = suiviParcelleDatas.nomFongicide?.plus(" : ")?.plus(suiviParcelleDatas.nombreFongicide)
//                labelHernicalUtiliseSuiviPreview.text = suiviParcelleDatas.nomHerbicide?.plus(" : ")?.plus(suiviParcelleDatas.nombreHerbicide)
//
//                labelPresenceSholenSuiviPreview.text = suiviParcelleDatas.presenceShooter
//                labeNombreDesherbageSuiviPreview.text = suiviParcelleDatas.nombreDesherbage
//                labelIntrantUtiliseSuiviPreview.text = suiviParcelleDatas.intrant
//
//                labelPresencePenteSuiviPreview.text = suiviParcelleDatas.pente
//
//                labelNombreSauvageonsSuiviPreview.text = suiviParcelleDatas.nombreSauvageons

                val suiviParcelleItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val suiviParcelleItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                suiviParcelleItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value,
                            suiviParcelleItemsListPrev
                        )
                    }
                }
//                LogUtils.d(producteurItemListData)
//                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(suiviParcelleItemsListPrev)
                recyclerInfoPrev.adapter = rvPrevAdapter
                recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


                clickSaveSuiviPreview.setOnClickListener {
                    saveParcelle()
                }

                clickCloseBtn.setOnClickListener {
                    finish()
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
