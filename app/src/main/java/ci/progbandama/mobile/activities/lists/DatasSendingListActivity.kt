package ci.progbandama.mobile.activities.lists

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.DataSendingAdapter
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_datas_sending_list.*
import kotlinx.android.synthetic.main.activity_menageres_list.labelLastSynchronisationMenage
import org.joda.time.DateTime


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
            val progbandBase = ProgBandRoomDatabase.getDatabase(this)!!

            labelLastSynchronisationMenage.text = resources.getString(
                R.string.last_synchronisation_date,
                DateTime.now().toString("HH:mm:ss")
            )

            when(fromGlobalMenu.toUpperCase()){
                "INFOS_PRODUCTEUR" -> {
                    labelTitleMenuAction.apply {
                        setText("INFOS PRODUCTEUR ${this.text}")
                    }
                    val dataList = progbandBase.infosProducteurDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
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
                    val dataList = progbandBase.suiviParcelleDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelle_id?.isNullOrEmpty() == false){
                                parc = progbandBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
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
                    val dataList = progbandBase.estimationDao().getUnSyncedAll()
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelleId?.isNullOrEmpty() == false){
                                parc = progbandBase.parcelleDao().getParcelle(it.parcelleId?.toInt()?:0)?.codeParc?:"N/A"
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
                    val dataList = progbandBase.inspectionDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelle?.isNullOrEmpty() == false){
                                parc = progbandBase.parcelleDao().getParcelle(it.parcelle?.toInt()?:0)?.codeParc?:"N/A"
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
                    val dataList = progbandBase.suiviApplicationDao().getUnSyncedAll()
                    dataList.forEach {
                        if(it.producteur?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteur?.toIntOrNull())
                            var parc = "N/A"
                            if(it.parcelle_id?.isNullOrEmpty() == false){
                                parc = progbandBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
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
                    val dataList = progbandBase.visiteurFormationDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                    dataList.forEach {
                        if(it.suivi_formation_id?.toIntOrNull() != null){
                            val prod = progbandBase.formationDao().getFormByID(it.suivi_formation_id?.toIntOrNull())
                            var parc = "N/A"
//                            if(it.parcelle_id?.isNullOrEmpty() == false){
//                                parc = progbandBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
//                            }
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Formation N°: ", "${prod.id} Date: ${Commons.convertDate(prod.multiStartDate, false)}", "${if(it.isSynced == true) "${it.isSynced}" else ""}")
                                })
                            }
                        }else{
                            commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                listOfValue = arrayListOf<String>("CODE: ", "${it.uid}", "Formation N°: ", "${it.suivi_formation_id}")
                            })
                        }
                    }
                    commonDataListCloned.addAll(commonDataList)
                }

                "LIVRAISON" -> {
                    labelTitleMenuAction.apply {
                        setText("STOCK DES SECTIONS ${this.text}")
                    }
                    val dataList = progbandBase.livraisonDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.magasinSection?.toIntOrNull() != null){
                            val prod = progbandBase.magasinSectionDao().getMagByID(it.magasinSection?.toIntOrNull()?:0)
                            var parc = "N/A"
//                            if(it.parcelle_id?.isNullOrEmpty() == false){
//                                parc = progbandBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
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
                    val dataList = progbandBase.livraisonCentralDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    LogUtils.d(dataList)
                    dataList.forEach {
                        if(it.magasinCentral?.toIntOrNull() != null){
                            val prod = progbandBase.magasinCentralDao().getMagByID(it.magasinCentral)
                            var parc = "N/A"
//                            if(it.parcelle_id?.isNullOrEmpty() == false){
//                                parc = progbandBase.parcelleDao().getParcelle(it.parcelle_id?.toInt()?:0)?.codeParc?:"N/A"
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

                "SSRTECLMRS" -> {
                    labelTitleMenuAction.apply {
                        setText("SSRTE-CLMRS ${this.text}")
                    }
                    val dataList = progbandBase.enqueteSsrtDao().getUnSyncedAll()
                    dataList.forEach {
                        if(it.producteursId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
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
                    val dataList = progbandBase.evaluationArbreDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0))
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
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
                    val dataList = progbandBase.distributionArbreDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
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
                    val dataList = progbandBase.postplantingDao().getUnSyncedAll(SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString())
                    dataList.forEach {
                        if(it.producteurId?.toIntOrNull() != null){
                            val prod = progbandBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
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