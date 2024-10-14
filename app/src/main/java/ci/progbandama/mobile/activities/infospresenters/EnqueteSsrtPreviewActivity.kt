package ci.progbandama.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.SsrtClmsActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.databinding.ActivityEnqueteSsrtPreviewBinding
import ci.progbandama.mobile.models.EnqueteSsrtModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics

class EnqueteSsrtPreviewActivity : AppCompatActivity() {


    //var ssrteData: EnqueteSsrtModel
    var enqueteSsrtDatas: EnqueteSsrtModel? = null
    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    private lateinit var binding: ActivityEnqueteSsrtPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnqueteSsrtPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            try {
                enqueteSsrtDatas = it.getParcelableExtra("preview")

                enqueteSsrtDatas?.let { enquete ->
                    try {
//                        labelLocaliteNomEnquetePreview.text = enquete.localiteNom
//                        labelProducteurEnquetePreview.text = enquete.producteurNom
//                        labelNomMembreEnquetePreview.text = enquete.nomMembre
//                        labelPrenomMembreEnquetePreview.text = enquete.prenomMembre
//                        labelSexeMembreEnquetePreview.text = enquete.sexeMembre
//                        labelNaissanceMembreEnquetePreview.text = enquete.datenaissMembre
//                        labelLienParenteEnquetePreview.text = enquete.lienParente
//                        labelSchoolStatutEnquetePreview.text = enquete.frequente
//
//                        if (enquete.frequente == getString(R.string.oui)) {
//                            linearSchoolStatutYesContainerSsrtPreview.visibility = VISIBLE
//                            linearSchoolStatutNoContainerSsrtPreview.visibility = GONE
//                            labelSchoolLevelEnquetePreview.text = enquete.niveauEtude
//                            labelSchoolClasseEnquetePreview.text = enquete.classe
//                            labelSchoolVillageStatutEnquetePreview.text = enquete.ecoleVillage
//
//                            if (enquete.ecoleVillage == getString(R.string.oui)) {
//                                linearSchoolVillageDistanceContainerSsrtPreview.visibility = GONE
//                                enquete.distanceEcole = ""
//                            } else {
//                                linearSchoolVillageDistanceContainerSsrtPreview.visibility = VISIBLE
//                                labelSchoolVillageDistanceEnquetePreview.text = enquete.distanceEcole
//                            }
//                        } else {
//                            linearSchoolStatutYesContainerSsrtPreview.visibility = GONE
//                            linearSchoolStatutNoContainerSsrtPreview.visibility = VISIBLE
//                            labelSchoolStatutNoOlderEnquetePreview.text = enquete.avoirFrequente
//
//                            if (enquete.avoirFrequente == getString(R.string.oui)) {
//                                linearSchoolStatutNoOlderLevelContainerSsrtPreview.visibility = VISIBLE
//                            } else {
//                                linearSchoolStatutNoOlderLevelContainerSsrtPreview.visibility = GONE
//                            }
//
//                        }
//
//                        enquete.raisonArretEcole =
//                            ListConverters.stringToMutableList(enquete.raisonArretEcoleStringify)
//                        labelSchoolStatutNoOlderRaisonEnquetePreview.text = null
//                        labelTravauxLegerEnquetePreview.text = null
//                        labelTravauxDangereuxLegerLieuEnquetePreview.text = null
//                        labelTravauxDangereuxLieuEnquetePreview.text = null
//                        labelTravauxDangereuxEnquetePreview.text = null
//
//
//                        enquete.raisonArretEcole?.let { raisons ->
//                            raisons.map { raison ->
//                                labelSchoolStatutNoOlderRaisonEnquetePreview.text =
//                                    labelSchoolStatutNoOlderRaisonEnquetePreview.text.toString()
//                                        .plus(raison).plus(System.getProperty("line.separator"))
//                            }
//                        }
//
//                        enquete.travauxDangereux =
//                            ListConverters.stringToMutableList(enquete.travauxDangereuxStringify)
//                        enquete.travauxDangereux?.let { travauxDangereux ->
//                            travauxDangereux.map { travailDangereux ->
//                                labelTravauxDangereuxEnquetePreview.text =
//                                    labelTravauxDangereuxEnquetePreview.text.toString()
//                                        .plus(travailDangereux)
//                                        .plus(System.getProperty("line.separator"))
//                            }
//                        }
//
//                        enquete.travauxLegers =
//                            ListConverters.stringToMutableList(enquete.travauxLegersStringify)
//                        enquete.travauxLegers?.let { travauxLegers ->
//                            travauxLegers.map { travailLeger ->
//                                labelTravauxLegerEnquetePreview.text =
//                                    labelTravauxLegerEnquetePreview.text.toString().plus(travailLeger)
//                                        .plus(System.getProperty("line.separator"))
//                            }
//                        }
//
//                        enquete.lieuTravauxLegers =
//                            ListConverters.stringToMutableList(enquete.lieuTravauxLegersStringify)
//                        enquete.lieuTravauxLegers?.let { lieux ->
//                            lieux.map { lieu ->
//                                labelTravauxDangereuxLegerLieuEnquetePreview.text =
//                                    labelTravauxDangereuxLegerLieuEnquetePreview.text.toString()
//                                        .plus(lieu).plus(System.getProperty("line.separator"))
//                            }
//                        }
//
//                        enquete.lieuTravauxDangereux =
//                            ListConverters.stringToMutableList(enquete.lieuTravauxDangereuxStringify)
//                        enquete.lieuTravauxDangereux?.let { lieux ->
//                            lieux.map { lieu ->
//                                labelTravauxDangereuxLieuEnquetePreview.text =
//                                    labelTravauxDangereuxLieuEnquetePreview.text.toString().plus(lieu)
//                                        .plus(System.getProperty("line.separator"))
//                            }
//                        }
                        //ssrteData = it.getParcelableExtra("preview")!!
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
                        binding.recyclerInfoPrev.adapter = rvPrevAdapter
                        binding.recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


                    } catch (ex: Exception) {
                        LogUtils.e(ex.message)
                        FirebaseCrashlytics.getInstance().recordException(ex)
                    }
                }

                binding.clickSaveEnquetePreview.setOnClickListener {
                    Commons.showMessage(
                        "Etes-vous sur de vouloir faire ce enregistrement ?",
                        this,
                        showNo = true,
                        callback = {
                            ProgBandRoomDatabase.getDatabase(this)?.enqueteSsrtDao()
                                ?.insert(enqueteSsrtDatas!!)
//                            Commons.synchronisation(type = "ssrte", this)
                            Commons.showMessage(
                                "Enquete enregistrée !",
                                this,
                                finished = true,
                                callback = {
                                    ActivityUtils.finishActivity(SsrtClmsActivity::class.java)
                                }
                            )
                        },
                        finished = false
                    )
                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }

}
