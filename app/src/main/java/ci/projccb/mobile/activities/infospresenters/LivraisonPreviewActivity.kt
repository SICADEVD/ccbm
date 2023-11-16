package ci.projccb.mobile.activities.infospresenters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.LivraisonActivity
import ci.projccb.mobile.adapters.PreviewItemAdapter
import ci.projccb.mobile.models.LivraisonModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.addItemsToList
import ci.projccb.mobile.tools.ListConverters
import ci.projccb.mobile.tools.MapEntry
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_infos_producteur_preview.recyclerInfoPrev
import kotlinx.android.synthetic.main.activity_livraison_preview.*

class LivraisonPreviewActivity : AppCompatActivity() {


    val draftDao = CcbRoomDatabase.getDatabase(this)?.draftedDatasDao()
    var draftID = 0
    val livraisonItemsListPrev = arrayListOf<Map<String,String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livraison_preview)

        intent?.let {
            try {
                val livraisonDatas: LivraisonModel = it.getParcelableExtra("preview")!!
                draftID = it.getIntExtra("draft_id", 0)

                val infoProdItemsListPrev: MutableList<Map<String, String>> = arrayListOf()
                val infoProdItemListData = it.getParcelableArrayListExtra<MapEntry>("previewitem")

                infoProdItemListData?.forEach {
                    if(it.key.isNullOrEmpty()==false){
                        Commons.addItemsToList(
                            if(it.key=="null") "Autre" else it.key,
                            it.value,
                            infoProdItemsListPrev
                        )
                    }
                }
                //LogUtils.json(infosProducteur)
                //                LogUtils.d(producteurItemsListPrev)

                val rvPrevAdapter = PreviewItemAdapter(infoProdItemsListPrev)
                recyclerInfoPrev.adapter = rvPrevAdapter
                recyclerInfoPrev.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvPrevAdapter.notifyDataSetChanged()

                //livraisonDatas.let { livraison ->

//                    //addItemsToList("La coopérative", "${livraison.cooperativeId}")
//                    addItemsToList("Date estimée de livraison", "${livraison.estimatDate}", livraisonItemsListPrev)
//                    addItemsToList("Status de paiement", "${livraison.paymentStatus}", livraisonItemsListPrev)
//                    addItemsToList("Le staff", "${livraison.delegueNom}", livraisonItemsListPrev)
//                    addItemsToList("Nom expéditeur", "${livraison.senderName}", livraisonItemsListPrev)
//                    addItemsToList("Contact expéditeur", "${livraison.senderPhone}", livraisonItemsListPrev)
//                    addItemsToList("Email expéditeur", "${livraison.senderEmail}", livraisonItemsListPrev)
//                    addItemsToList("Adresse expéditeur", "${livraison.senderAddress}", livraisonItemsListPrev)
//
//                    addItemsToList("Le magasin de section", "${livraison.magasinSection}", livraisonItemsListPrev)
//                    addItemsToList("Nom destinataire", "${livraison.receiverName}", livraisonItemsListPrev)
//                    addItemsToList("Contact destinataire", "${livraison.receiverPhone}", livraisonItemsListPrev)
//                    addItemsToList("Email destinataire", "${livraison.receiverEmail}", livraisonItemsListPrev)
//                    addItemsToList("Adresse destinataire", "${livraison.receiverAddress}", livraisonItemsListPrev)

//                    var listValue = ""
//                    var counter = 0
//                    val parcelles = ListConverters.stringToMutableList(livraison.livraisonSousModelParcellesStringify)
//                    val quantites = ListConverters.stringToMutableList(livraison.livraisonSousModelQuantitysStringify)
//                    ListConverters.stringToMutableList(livraison.livraisonSousModelProdNamesStringify)?.forEach {
//                        listValue += "Producteur: ${it},Parcelle: ${parcelles!![counter]},Quantité: ${quantites!![counter]}\n"
//                        counter++
//                    }
//                    addItemsToList("Info livraison", "${listValue}", livraisonItemsListPrev)

//                    addItemsToList("Réduction", "${livraison.reduction}".plus("%"), livraisonItemsListPrev)
//                    addItemsToList("Sous total", "${livraison.sousTotalReduce}".plus(" ${Commons.CURRENCYLIB}"), livraisonItemsListPrev)
//                    addItemsToList("Total", "${livraison.totalReduce}".plus(" ${Commons.CURRENCYLIB}"), livraisonItemsListPrev)

                  //  rvPrevAdapter.notifyDataSetChanged()

                    clickSaveLivraisonPreview.setOnClickListener {
                        Commons.showMessage(
                            "Etes-vous sur de vouloir faire ce enregistrement ?",
                            this,
                            showNo = true,
                            callback = {
                                CcbRoomDatabase.getDatabase(this)?.livraisonDao()
                                    ?.insert(livraisonDatas)
                                draftDao?.completeDraft(draftID)
                                Commons.synchronisation(type = "livraison", this)
                                Commons.showMessage(
                                    "Livraison enregistrée !",
                                    this,
                                    finished = true,
                                    callback = {})
                            },
                            finished = false
                        )

                        ActivityUtils.finishActivity(LivraisonActivity::class.java)
                    }

                    clickCloseBtn.setOnClickListener {
                        finish()
                    }

                //}
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }
}
