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
import ci.projccb.mobile.tools.ListConverters
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

                val rvPrevAdapter = PreviewItemAdapter(livraisonItemsListPrev)
                recyclerInfoLivraison.adapter = rvPrevAdapter
                recyclerInfoLivraison.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                livraisonDatas.let { livraison ->

                    //addItemsToList("La coopérative", "${livraison.cooperativeId}")
                    addItemsToList("Date estimée de livraison", "${livraison.estimatDate}")
                    addItemsToList("Status de paiement", "${livraison.paymentStatus}")
                    addItemsToList("Le staff", "${livraison.delegueNom}")
                    addItemsToList("Nom expéditeur", "${livraison.senderName}")
                    addItemsToList("Contact expéditeur", "${livraison.senderPhone}")
                    addItemsToList("Email expéditeur", "${livraison.senderEmail}")
                    addItemsToList("Adresse expéditeur", "${livraison.senderAddress}")

                    addItemsToList("Le magasin de section", "${livraison.magasinSection}")
                    addItemsToList("Nom destinataire", "${livraison.receiverName}")
                    addItemsToList("Contact destinataire", "${livraison.receiverPhone}")
                    addItemsToList("Email destinataire", "${livraison.receiverEmail}")
                    addItemsToList("Adresse destinataire", "${livraison.receiverAddress}")

                    var listValue = ""
                    var counter = 0
                    val parcelles = ListConverters.stringToMutableList(livraison.livraisonSousModelParcellesStringify)
                    val quantites = ListConverters.stringToMutableList(livraison.livraisonSousModelQuantitysStringify)
                    ListConverters.stringToMutableList(livraison.livraisonSousModelProdNamesStringify)?.forEach {
                        listValue += "Producteur: ${it},Parcelle: ${parcelles!![counter]},Quantité: ${quantites!![counter]}\n"
                        counter++
                    }
                    addItemsToList("Info livraison", "${listValue}")

                    addItemsToList("Réduction", "${livraison.reduction}".plus("%"))
                    addItemsToList("Sous total", "${livraison.sousTotalReduce}".plus(" ${Commons.CURRENCYLIB}"))
                    addItemsToList("Total", "${livraison.totalReduce}".plus(" ${Commons.CURRENCYLIB}"))

                    rvPrevAdapter.notifyDataSetChanged()

                    clickSaveLivraisonPreview.setOnClickListener {
                        Commons.showMessage(
                            "Etes-vous sur de vouloir faire ce enregistrement ?",
                            this,
                            showNo = true,
                            callback = {
                                CcbRoomDatabase.getDatabase(this)?.livraisonDao()
                                    ?.insert(livraison)
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

                }
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }

    private fun addItemsToList(keyI: String, valueI: String) {
        val item = mutableMapOf<String,String>()
        item.put(keyI, valueI)
        livraisonItemsListPrev.add(item)
    }
}
