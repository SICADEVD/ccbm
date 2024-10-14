package ci.progbandama.mobile.activities.infospresenters

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.ParcelleActivity
import ci.progbandama.mobile.adapters.PreviewItemAdapter
import ci.progbandama.mobile.databinding.ActivityParcellePreviewBinding
import ci.progbandama.mobile.models.ParcelleModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.showMessage
import ci.progbandama.mobile.tools.ExportUtils
import ci.progbandama.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ParcellePreviewActivity : AppCompatActivity() {


    var parcellePoo = ParcelleModel()
    val draftDao = ProgBandRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0
    var labelSaving: AppCompatTextView? = null
    var labelSharing: AppCompatTextView? = null
    var whichButton = 0

    private lateinit var binding: ActivityParcellePreviewBinding

//    fun collecteDatas() {
//        labelProducteurParcellePreview.text = parcellePoo.producteurNom
//        labelCultueParcellePreview.text = parcellePoo.culture
//        labelDeclarationParcellePreview.text = parcellePoo.typedeclaration
//        labelSupeficieParcellePreview.text = parcellePoo.superficie
//        labelLatLngParcellePreview.text = parcellePoo.latitude?.plus("/").plus(parcellePoo.longitude)
//        labelAnneeParcellePreview.text = parcellePoo.anneeCreation
//        labelWayPointsParcellePreview.text = parcellePoo.wayPointsString
//    }


    fun buildExport(
        activity: Activity,
        parcellePo: ParcelleModel,
        type: Int,
        action: Int
    ) {

        MainScope().launch {
            withContext(IO) {
                try {
                    val product = ProgBandRoomDatabase.getDatabase(activity)?.producteurDoa()?.getProducteurByID(parcellePo.producteurId?.toInt())
                    val localite = ProgBandRoomDatabase.getDatabase(activity)?.localiteDoa()?.getLocalite(product?.localitesId?.toInt()?:0)
//                    LogUtils.d(product)
//                    LogUtils.d(localite)
                    val nomPrenoms = "${localite?.nom}_"+"${product?.nom} ${product?.prenoms}".replace(" ", "_")+"_"
                    if (type == 1) ExportUtils.expotToKml(nomPrenoms?.lowercase(), parcellePo, action,this@ParcellePreviewActivity)
                    else ExportUtils.exportToGpx(nomPrenoms?.lowercase(), parcellePo, action, this@ParcellePreviewActivity)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParcellePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.dialog_action, null)

        labelSaving = view.findViewById(R.id.labelSavingDialog)
        labelSharing = view.findViewById(R.id.labelSharingDialog)

        // below line is use to set cancelable to avoid
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        dialog.setContentView(view)


        intent?.let {
            try {
                parcellePoo = it.getParcelableExtra("preview")!!
//                LogUtils.json(parcellePoo)
                draftID = it.getIntExtra("draft_id", 0)
                //collecteDatas()

                val parcelleItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val parcelleItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                parcelleItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value.replace(", ", "\n"),
                            parcelleItemsListPrev
                        )
                    }
                }
//                LogUtils.d(producteurItemListData)
//                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(parcelleItemsListPrev)
                binding.recyclerInfoPrev.adapter = rvPrevAdapter
                binding.recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        labelSaving?.setOnClickListener {
            try {
                dialog.dismiss()
                buildExport(this, parcellePoo, whichButton, 2)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        labelSharing?.setOnClickListener {
            try {
                dialog.dismiss()
                buildExport(this, parcellePoo, whichButton, 1)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        binding.clickSaveParcellePreview.setOnClickListener {
            try {
                showMessage(
                    "Etes-vous sûr de vouloir faire cet enregistrement ?",
                    this,
                    showNo = true,
                    callback = {
                        ProgBandRoomDatabase.getDatabase(this)?.parcelleDao()?.insert(parcellePoo)
                        draftDao?.completeDraft(draftID)
//                        Commons.synchronisation(type = "parcelle", this)
                        showMessage(
                            "Parcelle enregistrée avec succes !",
                            this,
                            finished = true,
                            callback = {})
                    },
                    finished = false
                )
                ActivityUtils.finishActivity(ParcelleActivity::class.java)
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        binding.clickCloseBtn.setOnClickListener {
            finish()
        }

        binding.clickKmlParcellePreview.setOnClickListener {
            whichButton = 1
            dialog.show()
        }

        binding.clickGpxParcellePreview.setOnClickListener {
            whichButton = 2
            dialog.show()
        }

        if (parcellePoo.wayPointsString.toString().isEmpty()) {
            binding.clickGpxParcellePreview.visibility = View.GONE
            binding.clickKmlParcellePreview.visibility = View.GONE
        }
        
        
        if (parcellePoo.typedeclaration.toString().lowercase() == "verbale") {
            try {
                binding.linearActionExportContainerParcellePreview.visibility = View.GONE
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }
}
