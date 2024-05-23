package ci.projccb.mobile.activities.lists

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.InspectionActivity
import ci.projccb.mobile.activities.forms.ParcelleActivity
import ci.projccb.mobile.activities.forms.ProducteurActivity
import ci.projccb.mobile.adapters.DataSendingAdapter
import ci.projccb.mobile.adapters.DataSyncedAdapter
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_datas_sending_list.*
import kotlinx.android.synthetic.main.activity_menageres_list.labelLastSynchronisationMenage
import org.joda.time.DateTime
import java.lang.Math.ceil


@SuppressLint("All")
class DatasSendingListActivity : AppCompatActivity(R.layout.activity_datas_sending_list) {


    val commonDataList = mutableListOf<CommonData>()
    val commonDataListCloned = mutableListOf<CommonData>()
    var fromGlobalMenu = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            fromGlobalMenu = it.getStringExtra("fromContent").toString()

            LogUtils.d(fromGlobalMenu)
            val ccbBase = CcbRoomDatabase.getDatabase(this)!!

            labelLastSynchronisationMenage.text = resources.getString(
                R.string.last_synchronisation_date,
                DateTime.now().toString("HH:mm:ss")
            )

            when(fromGlobalMenu.toUpperCase()){
                "INFOS_PRODUCTEUR" -> {
                    labelTitleMenuAction.apply {
                        setText("INFOS PRODUCTEUR ${this.text}")
                    }
                    val dataList = ccbBase.infosProducteurDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${prod.nom} ${prod.prenoms}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteursId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "PARCELLES" -> {
                    labelTitleMenuAction.apply {
                        setText("SUIVIS PARCELLES ${this.text}")
                    }
                    val dataList = ccbBase.suiviParcelleDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelle_id?.isNullOrEmpty() == false){
                                parc = ccbBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
                            }

                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: \nParcelle: ", "${prod.nom} ${prod.prenoms}\n${parc}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteursId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "ESTIMATION" -> {
                    labelTitleMenuAction.apply {
                        setText("ESTIMATIONS ${this.text}")
                    }
                    val dataList = ccbBase.estimationDao().getUnSyncedAll()
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelleId?.isNullOrEmpty() == false){
                                parc = ccbBase.parcelleDao().getParcelle(it.parcelleId?.toInt()?:0)?.codeParc?:"N/A"
                            }

                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: \nParcelle: ", "${prod.nom} ${prod.prenoms}\n${parc}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteurId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "INSPECTION" -> {
                    labelTitleMenuAction.apply {
                        setText("INSPECTIONS ${this.text}")
                    }
                    val dataList = ccbBase.inspectionDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelle?.isNullOrEmpty() == false){
                                parc = ccbBase.parcelleDao().getParcelle(it.parcelle?.toInt()?:0)?.codeParc?:"N/A"
                            }

                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: \nParcelle: ", "${prod.nom} ${prod.prenoms}\n${parc}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteursId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "APPLICATION" -> {
                    labelTitleMenuAction.apply {
                        setText("APPLICATIONS PHYTOS ${this.text}")
                    }
                    val dataList = ccbBase.suiviApplicationDao().getUnSyncedAll()
                    dataList.forEach {
                        if(it.producteur?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteur?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelle_id?.isNullOrEmpty() == false){
                                parc = ccbBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
                            }

                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: \nParcelle: ", "${prod.nom} ${prod.prenoms}\n${parc}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteur}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "FORMATION_VISITEUR" -> {
                    labelTitleMenuAction.apply {
                        setText("VISITEURS FORMATION ${this.text}")
                    }
                    val dataList = ccbBase.visiteurFormationDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                    dataList.forEach {
                        if(it.formationId?.toIntOrNull() != null){
                            val prod = ccbBase.formationDao().getFormByID(it.formationId?.toIntOrNull())
                            var parc = "N/A"
//                            if(it.parcelle_id?.isNullOrEmpty() == false){
//                                parc = ccbBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
//                            }
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Formation N°: ", "${prod.id} Date: ${Commons.convertDate(prod.dateFormation, false)}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Formation N°: ", "${it.id}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "LIVRAISON" -> {
                    labelTitleMenuAction.apply {
                        setText("STOCK DES SECTIONS ${this.text}")
                    }
                    val dataList = ccbBase.livraisonDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.magasinSection?.toIntOrNull() != null){
                            val prod = ccbBase.magasinSectionDao().getMagByID(it.magasinSection?.toIntOrNull()?:0)
                            var parc = "N/A"
//                            if(it.parcelle_id?.isNullOrEmpty() == false){
//                                parc = ccbBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
//                            }
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Magasin: ", "${prod.nomMagasinsections}\nDate: ${Commons.convertDate(it.estimatDate, false)}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Magasin: ", "${it.magasinSection}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "LIVRAISON_MAGCENTRAL" -> {
                    labelTitleMenuAction.apply {
                        setText("STOCK DES MAG CENTRAUX ${this.text}")
                    }
                    val dataList = ccbBase.livraisonCentralDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    LogUtils.d(dataList)
                    dataList.forEach {
                        if(it.magasinCentral?.toIntOrNull() != null){
                            val prod = ccbBase.magasinCentralDao().getMagByID(it.magasinCentral)
                            var parc = "N/A"
//                            if(it.parcelle_id?.isNullOrEmpty() == false){
//                                parc = ccbBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
//                            }
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Magasin: ", "${prod.nomMagasinsections}\nDate: ${Commons.convertDate(it.estimatDate, false)}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Magasin: ", "${it.magasinCentral}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "SSRTE" -> {
                    labelTitleMenuAction.apply {
                        setText("SSRTE-CLMRS ${this.text}")
                    }
                    val dataList = ccbBase.enqueteSsrtDao().getUnSyncedAll()
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${prod.nom} ${prod.prenoms}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteursId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "AGRO_EVALUATION" -> {
                    labelTitleMenuAction.apply {
                        setText("EVALUATIONS DES ARBRES ${this.text}")
                    }
                    val dataList = ccbBase.evaluationArbreDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${prod.nom} ${prod.prenoms}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteurId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "AGRO_DISTRIBUTION" -> {
                    labelTitleMenuAction.apply {
                        setText("DISTRIBUTIONS DES ARBRES ${this.text}")
                    }
                    val dataList = ccbBase.distributionArbreDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${prod.nom} ${prod.prenoms}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteurId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "POSTPLANTING" -> {
                    labelTitleMenuAction.apply {
                        setText("EVALUATIONS POSTPLANTING ${this.text}")
                    }
                    val dataList = ccbBase.distributionArbreDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${prod.nom} ${prod.prenoms}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Producteur: ", "${it.producteurId}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

            }
            val syncedDatasAdapter = DataSendingAdapter(
                this,
                commonDataList
            )
            recyclerSyncedList.adapter = syncedDatasAdapter
            recyclerSyncedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            syncedDatasAdapter.notifyDataSetChanged()
            //refreshAdapter(commonDataList.subList(0, subSizeEnd))

            commonDataList?.let { SyncsList ->
                if (SyncsList.isEmpty()) {
                    recyclerSyncedList.visibility = View.GONE
                    linearEmptyContainerSyncsList.visibility = View.VISIBLE
                } else {
                    recyclerSyncedList.visibility = View.VISIBLE
                    linearEmptyContainerSyncsList.visibility = View.GONE
                }
            }
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }
    }


}
