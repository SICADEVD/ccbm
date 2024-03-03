package ci.projccb.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.ProducteurMenageActivity
import ci.projccb.mobile.adapters.PreviewItemAdapter
import ci.projccb.mobile.models.ProducteurMenageModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_menage_preview.*
import kotlinx.android.synthetic.main.activity_producteur_preview.recyclerInfoPrev

class MenagePreviewActivity : AppCompatActivity() {


    var menageData = ProducteurMenageModel()
    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menage_preview)

        intent?.let {
            try {
                menageData = it.getParcelableExtra("preview")!!
                draftID = it.getIntExtra("draft_id", 0)

                val menageItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")
                val producteurItemsListPrev: MutableList<Map<String, String>> = arrayListOf()

                menageItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value.replace(", ", "\n"),
                            producteurItemsListPrev
                        )
                    }
                }

                val rvPrevAdapter = PreviewItemAdapter(producteurItemsListPrev)
                recyclerInfoPrev.adapter = rvPrevAdapter
                recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


                menageData.let { menage ->

                    clickSavePreview.setOnClickListener {
                        Commons.showMessage(
                            "Etes-vous sur de vouloir faire ce enregistrement ?",
                            this,
                            showNo = true,
                            callback = {
                                try {
                                    CcbRoomDatabase.getDatabase(this)?.producteurMenageDoa()
                                        ?.insert(menage)
                                    draftDao?.completeDraft(draftID)
                                    Commons.synchronisation(type = "menage", this)
                                    Commons.showMessage(
                                        "Menage enregistrée avec succes !",
                                        this,
                                        finished = true,
                                        callback = {})
                                } catch (ex: Exception) {
                                    LogUtils.e(ex.message)
                                    FirebaseCrashlytics.getInstance().recordException(ex)
                                }
                            },
                            finished = false
                        )

                        ActivityUtils.finishActivity(ProducteurMenageActivity::class.java)
                    }

                }
//                    labelCodeProducteurMenagePreview.text = menage.codeProducteur
//                    labelLocaliteMenagePreview.text = menage.localiteNom
//                    labelProducteurMenagePreview.text = menage.producteurNomPrenoms
//                    labelQuartierMenagePreview.text = menage.quartier
//                    labelEnergieMenagePreview.text = menage.sources_energies_id
//
//                    try {
//                        if (menage.sources_energies_id.toString().uppercase().contains("BOIS")) {
//                            linearEnergieBoisWeekCountContainerMenagePreview.visibility = VISIBLE
//                            labelEnergieBoisWeekCountMenagePreview.text = menage.boisChauffe
//                        } else {
//                            linearEnergieBoisWeekCountContainerMenagePreview.visibility = GONE
//                        }
//                    } catch (ex: Exception) {
//                        LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                    }
//
//                    labelOrdureLieuMenagePreview.text = menage.ordures_menageres_id
//                    labelSeparationDechetYesNoMenagePreview.text = menage.separationMenage
//                    labelGestionEauMenagePreview.text = menage.eauxToillette
//                    labelGestionVaisselleMenagePreview.text = menage.eauxVaisselle
//                    labelWCYesNoMenagePreview.text = menage.wc
//                    labellieuEauPortableMenagePreview.text = menage.sources_eaux_id
//                    labelTraiterChampsMenagePreview.text = menage.traitementChamps
//
//                    try {
//                        if (menage.traitementChamps == getString(R.string.oui)) {
//                            labelTraiteYourselfMachineMenagePreview.text = menage.type_machines_id
//                            labelEquipementProtectionMenagePreview.text = menage.equipements?:getString(R.string.non)
//
//                            if (menage.machine.toString().uppercase().contains("ATOMISATEUR")) {
//                                labelTraiteChampsMachineAtomisateurStatusMenagePreview.text = getString(R.string.oui)
//                                linearTraiteChampsMachineAtomisateurStatusContainerMenagePreview.visibility =
//                                    VISIBLE
//                            } else {
//                                linearTraiteChampsMachineAtomisateurStatusContainerMenagePreview.visibility =
//                                    GONE
//                            }
//
//                            linearTraiteYourselfFarmYesContainerMenagePreview.visibility = VISIBLE
//                            linearTraitYourselfFarmNoContainer.visibility = GONE
//                        } else {
//                            labelTraitYourselfFarmNoOtherNameMenagePreview.text =
//                                menage.nomPersonneTraitant
//                            labelTraitYourselfFarmNoOtherNumberMenagePreview.text =
//                                menage.numeroPersonneTraitant
//
//                            linearTraiteYourselfFarmEquipmentYesNoContainerMenagePreview.visibility =
//                                GONE
//                            linearTraiteYourselfFarmYesContainerMenagePreview.visibility = GONE
//                            linearTraiteChampsMachineAtomisateurStatusContainerMenagePreview.visibility =
//                                GONE
//                            linearTraitYourselfFarmNoContainer.visibility = VISIBLE
//                        }
//                    } catch (ex: Exception) {
//                        LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                    }
//
//                    labelFemmeActiviteYesNoMenagePreview.text = menage.champFemme
//
//                    try {
//                        if (menage.activiteFemme.toString().contains(getString(R.string.oui))) {
//                            labelFemmeActiviteYesNomMenagePreview.text = menage.nomActiviteFemme
//                            linearFemmeActiviteYesContainerMenagePreview.visibility = VISIBLE
//                        } else {
//                            linearFemmeActiviteYesContainerMenagePreview.visibility = GONE
//                        }
//                    } catch (ex: Exception) {
//                        LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                    }
//
//                    labelFemmeCacaoSuperficieMenagePreview.text = menage.superficieCacaoFemme
//
//                    try {
//                        menage.superficieCacaoFemme = if (menage.superficieCacaoFemme.toString()
//                                .isEmpty()
//                        ) "0.0" else menage.superficieCacaoFemme.toString().toDouble().toString()
//                        menage.nombreHectareFemme = if (menage.nombreHectareFemme.toString()
//                                .isEmpty()
//                        ) "0.0" else menage.nombreHectareFemme.toString().toDouble().toString()
//
//                        if ((menage.superficieCacaoFemme.toString().toDouble()) == 0.0) {
//                            labelProducteurFemmeDonChampsYesNoMenagePreview.text =
//                                if (menage.nombreHectareFemme.toString()
//                                        .toDouble() > 0.0
//                                ) getString(R.string.oui) else getString(R.string.non)
//                            labelProducteurFemmeDonChampsYesHectarMenagePreview.text =
//                                menage.nombreHectareFemme
//                            linearFemmeCacaoHectarUnderZeroContainerMenagePreview.visibility =
//                                VISIBLE
//                            linearProducteurFemmeDonChampsYesHectarContainerMenagePreview.visibility =
//                                VISIBLE
//                        } else {
//                            linearFemmeCacaoHectarUnderZeroContainerMenagePreview.visibility = GONE
//                            linearProducteurFemmeDonChampsYesHectarContainerMenagePreview.visibility =
//                                GONE
//                        }
//                    } catch (ex: Exception) {
//                        LogUtils.e(ex.message)
//                FirebaseCrashlytics.getInstance().recordException(ex)
//                    }
//

//
//
                    clickCloseBtn.setOnClickListener {
                        finish()
                    }
//                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
