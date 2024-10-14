package ci.progbandama.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.UniteAgricoleProducteurActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.databinding.ActivityInfosProducteurPreviewBinding
import ci.progbandama.mobile.models.InfosProducteurDTO
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.Exception

class InfosProducteurPreviewActivity : AppCompatActivity() {


    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0

    private lateinit var binding: ActivityInfosProducteurPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfosProducteurPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            try {
                val infosProducteur: InfosProducteurDTO? = it.getParcelableExtra("preview")
                draftID = it.getIntExtra("draft_id", 0)

//                infosProducteur?.run {
//                    labelProducteurInfosProducteurPreview.text = this.producteursNom
//                    labelCodeInfosProducteurPreview.text = this.producteursCode
//
//                    labelJachereYesNoInfosProducteurPreview.text = this.foretsjachere
//
//                    if (foretsjachere == getString(R.string.oui)) {
//                        linearJachereYesSuperficieContainerInfosProducteurPreview.visibility =
//                            VISIBLE
//                        labelJachereYesSuperficieInfosProducteurPreview.text = this.superficie
//                    } else {
//                        linearJachereYesSuperficieContainerInfosProducteurPreview.visibility = GONE
//                    }
//
//                    labelOthersFarmsYesNoInfosProducteurPreview.text = this.autresCultures
//
//                    if (autresCultures == getString(R.string.oui)) {
//                        this.typeculture =
//                            ListConverters.stringToMutableList(this.typecultureStringify)
//                        typeculture?.let { cultures ->
//                            labelOthersFarmsInfosProducteurPreview.text = null
//                            cultures.map { culture ->
//                                labelOthersFarmsInfosProducteurPreview.text =
//                                    labelOthersFarmsInfosProducteurPreview.text.toString()
//                                        .plus(culture).plus(System.getProperty("line.separator"))
//                            }
//                        }
//
//                        linearOthersCultureYesContainerInfosProducteursPreview.visibility = VISIBLE
//                    } else {
//                        linearOthersCultureYesContainerInfosProducteursPreview.visibility = GONE
//                    }
//
//                    this.maladiesenfants =
//                        ListConverters.stringToMutableList(this.maladiesenfantsStringify)
//                    maladiesenfants?.let { maladies ->
//                        labelDeseasesInfosProducteurPreview.text = null
//                        maladies.map { maladie ->
//                            labelDeseasesInfosProducteurPreview.text =
//                                labelDeseasesInfosProducteurPreview.text.toString().plus(maladie)
//                                    .plus(System.getProperty("line.separator"))
//                        }
//                    }
//
//                    labelNbreWorkersInfosProducteurPreview.text = this.travailleurs
//                    labelNbreWorkersUndefinedInfosProducteurPreview.text =
//                        this.travailleurstemporaires
//                    labelNbreWorkersDefinedInfosProducteurPreview.text = this.travailleurspermanents
//
//                    labelChildSchoolInfosProducteurPreview.text = this.persEcole
//                    labelChildUnder18InfosProducteurPreview.text = this.age18
//                    labelChildScoolExtraitInfosProducteurPreview.text = this.scolarisesExtrait
//
//                    labelActionPeopleInjuryInfosProducteurPreview.text = this.personneBlessee
//                    labelPaperFarmsInfosProducteurPreview.text = this.typeDocuments
//                    labelRecuHolderInfosProducteurPreview.text = this.recuAchat
//
//                    labelMobileMoneyYesNoInfosProducteurPreview.text = this.mobileMoney
//
//                    if (this.mobileMoney == getString(R.string.oui)) {
//                        labelMobileMoneyOperateurInfosProducteurPreview.text = this.operateurMM
//                        labelMobieMoneyNumberInfosProducteurPreview.text = this.numeroCompteMM
//
//                        linearMobileMoneyYesOperateurContainerInfosProducteurPreview.visibility =
//                            VISIBLE
//                        linearMobileMoneyYesNumberContainerInfosProducteurPreview.visibility =
//                            VISIBLE
//                    }
//
//                    labelBuyMethodInfoProducteurPreview.text = this.paiementMM
//                    labelBanqueYesNoInfosProducteurPreview.text = this.compteBanque

                    val infoProdItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                    val infoProdItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                    infoProdItemListData?.forEach {
                        if(it.key.isNullOrEmpty()==false){
                            Commons.addItemsToList(
                                if(it.key=="null") "Autre" else it.key,
                                it.value.replace(", ", "\n"),
                                infoProdItemsListPrev
                            )
                        }
                    }
                    //LogUtils.json(infosProducteur)
    //                LogUtils.d(producteurItemsListPrev)

                    val rvPrevAdapter = PreviewItemAdapter(infoProdItemsListPrev)
                binding.recyclerInfoPrev.adapter = rvPrevAdapter
                binding.recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)



                binding.clickCloseBtn.setOnClickListener {
                        finish()
                    }

                binding.clickSaveInfosProducteurPreview.setOnClickListener {
                        try {
                            Commons.showMessage(
                                "Etes-vous sûr de vouloir enregistrer ce contenu ?",
                                this@InfosProducteurPreviewActivity,
                                showNo = true,
                                callback = {
                                    ProgBandRoomDatabase.getDatabase(this@InfosProducteurPreviewActivity)
                                        ?.infosProducteurDao()?.insert(infosProducteur!!)
                                    draftDao?.completeDraft(draftID)
//                                    Commons.synchronisation(
//                                        type = "infos_producteur",
//                                        this@InfosProducteurPreviewActivity
//                                    )
                                    Commons.showMessage(
                                        "Information du producteur enregistrée avec succes !",
                                        this@InfosProducteurPreviewActivity,
                                        finished = true,
                                        callback = {})
                                },
                                finished = false
                            )

                            ActivityUtils.finishActivity(UniteAgricoleProducteurActivity::class.java)
                        } catch (ex: Exception) {
                            Commons.showMessage(
                                "Echec enregistreent !",
                                this@InfosProducteurPreviewActivity,
                                callback = {})
                        }
                    }
//                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
