package ci.projccb.mobile.activities.lists

import android.annotation.SuppressLint
import android.content.Intent
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
import ci.projccb.mobile.adapters.DataSyncedAdapter
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_datas_sync_list.*
import java.lang.Math.ceil


interface InstructionCallback {
    fun onInstructionReceived(search: String, callback: ((commonDataListCloned: MutableList<CommonData>)->Unit))
    fun onInstructionReceivedNoSearch()
}

@SuppressLint("All")
class DatasSyncListActivity : AppCompatActivity(R.layout.activity_datas_sync_list) {


    private var currIndex: Int = 0
    private var currSize: Int = 0
    private var paginSize: Double = 0.0
    val commonDataList = mutableListOf<CommonData>()
    val commonDataListCloned = mutableListOf<CommonData>()
    var fromGlobalMenu = ""
    var instructionCallback: InstructionCallback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageSearchUpdate.setOnClickListener {
            if(it.visibility == VISIBLE) {
                linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_button))
                linearSearchContainerUpdate.visibility = VISIBLE
                it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_button))
                it.visibility = INVISIBLE
            }
        }

        imageCloseSearchUpdate.setOnClickListener {
            if(it.visibility == VISIBLE) {
                imageSearchUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_button))
                imageSearchUpdate.visibility = VISIBLE
                linearSearchContainerUpdate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_button))
                linearSearchContainerUpdate.visibility = GONE
            }
        }

        intent?.let {
            fromGlobalMenu = it.getStringExtra("fromContent").toString()

           // LogUtils.d(fromGlobalMenu)
            val ccbBase = CcbRoomDatabase.getDatabase(this)!!;

            var intentGoToModif: Intent? = null
            when(fromGlobalMenu.toUpperCase()){
                "INSPECTION" -> {
                    labelTitleMenuAction.apply {
                        setText("${this.text} INSPECTION")
                    }
                    val dataList = ccbBase.inspectionDao().getAllNConformeOrNApplicableSync()
                    dataList.forEach {
//                        LogUtils.d(it.producteursId)
                        if(it.producteursId?.toIntOrNull() != null){

                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteursId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid, value = fromGlobalMenu.toUpperCase() ).apply {
                                    listOfValue = arrayListOf<String>("Inspection: ${it.uid}\nProducteur: ${prod.nom} ${prod.prenoms}\nCertificat: ${it.certificatStr}", it.dateEvaluation.toString())
                                })
                                commonDataListCloned.addAll(commonDataList)
                            }
                        }
                    }
                    intentGoToModif = Intent(this@DatasSyncListActivity, InspectionActivity::class.java)
                }
                "PARCELLE" -> {
                    labelTitleMenuAction.apply {
                        setText("${this.text} PARCELLE")
                    }
                    val dataListLimit = ccbBase.parcelleDao().getSyncedLimit(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), 100)
                    dataListLimit.forEach {
//                        LogUtils.d(it.producteursId)
                        if(it.producteurId?.toIntOrNull() != null){

                            val prod = ccbBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
                            if(prod!=null){
                                commonDataList.add(CommonData(id = it.uid.toInt(), value = fromGlobalMenu.toUpperCase() ).apply {
                                    var annee = it.anneeCreation
                                    if(annee.isNullOrEmpty()) annee = it.anneeRegenerer
                                    if(annee.isNullOrEmpty()) annee = "N/A"
                                    listOfValue = arrayListOf<String>("Code: ${it.codeParc?:"N/A"}\nProducteur: ${prod.nom} ${prod.prenoms}\nAnnée: ${annee}\nSuperficie: ${it.superficie?:"N/A"} ha")
                                })
                                commonDataListCloned.addAll(commonDataList)
                            }
                        }
                    }

                    instructionCallback = object : InstructionCallback{
                        override fun onInstructionReceived(search: String, callback: ((commonDataListCloned: MutableList<CommonData>)->Unit)) {
                            val dataListProd = ccbBase.producteurDoa().findByName(search)
//                            LogUtils.d("onInstructionReceived", dataListProd)
                            recyclerSyncedList.visibility = GONE
                            if(dataListProd.size > 0){
                                val listOfParcelUniq = dataListProd.toSet().toList()
                                commonDataList.clear()
                                commonDataListCloned.clear()
                                var processedItems = 0
//                                LogUtils.d(listOfParcelUniq.map { "${it.fullName}" })
                                listOfParcelUniq.forEach {prod->
                                    if(prod.id != null){

//                                        val prod = ccbBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
//                                        if(prod!=null){
                                            commonDataList.add(CommonData(id = prod.parceUid.toInt(), value = fromGlobalMenu.toUpperCase() ).apply {
                                                var annee = prod.anneeCreation
                                                if(annee.isNullOrEmpty()) annee = prod.anneeRegenerer
                                                if(annee.isNullOrEmpty()) annee = "N/A"
                                                listOfValue = arrayListOf<String>("Code: ${prod.codeParc?:"N/A"}\nProducteur: ${prod.nom} ${prod.prenoms}\nAnnée: ${annee}\nSuperficie: ${prod.superficie?:"N/A"} ha")
                                            })
//                                        }
                                    }
                                    commonDataListCloned.addAll(commonDataList)
                                    val listOfParcelUniq = commonDataListCloned.distinctBy { it.id }
//                                    LogUtils.d(commonDataList.map { "${it.listOfValue?.first()}" })
                                    processedItems++
                                    if (processedItems == dataListProd.size) {
//                                        LogUtils.d("onInstructionReceived2", listOfParcelUniq.map { it.id })
                                        if (commonDataList.size > 0) callback.invoke(listOfParcelUniq.toMutableList())
                                    }
                                }
                            }
                        }

                        override fun onInstructionReceivedNoSearch() {
                            val dataListProd = ccbBase.parcelleDao()?.getSyncedLimit(SPUtils.getInstance().getInt(Constants.AGENT_ID).toString(), 35)
                            commonDataList.clear()
                            commonDataListCloned.clear()
                            dataListProd.forEach {
                                if(it.producteurId?.toIntOrNull() != null){

                                    val prod = ccbBase.producteurDoa().getProducteurByID(it.producteurId?.toIntOrNull())
                                    if(prod!=null){
                                        commonDataList.add(CommonData(id = it.uid.toInt(), value = fromGlobalMenu.toUpperCase() ).apply {
                                            var annee = it.anneeCreation
                                            if(annee.isNullOrEmpty()) annee = it.anneeRegenerer
                                            if(annee.isNullOrEmpty()) annee = "N/A"
                                            listOfValue = arrayListOf<String>("Code: ${it.codeParc}\nProducteur: ${prod.nom} ${prod.prenoms}\nAnnée: ${annee}\nSuperficie: ${it.superficie} ha")
                                        })
                                        commonDataListCloned.addAll(commonDataList)
                                    }
                                }
                            }
                        }
                    }
                    intentGoToModif = Intent(this@DatasSyncListActivity, ParcelleActivity::class.java)
                }
            }

            imageSyncBtn.setOnClickListener {
                intentGoToModif?.let {
                    ActivityUtils.startActivity(it)
                    ActivityUtils.finishActivity(this@DatasSyncListActivity)
                }
            }

            currSize = commonDataList.size
            currIndex = 1
            paginSize = currSize.toDouble().div(35)
            
            setPageCurrTitle(currIndex)
//            var subSizeEnd = currIndex*35
//            if(subSizeEnd > currSize) subSizeEnd = currSize - 1
            val syncedDatasAdapter = DataSyncedAdapter(
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

        paginationContainerBack.setOnClickListener{
            val currentVal = currIndex - 1
            if( ceil(paginSize) > currentVal && currentVal > 0){
                val beCount = (currentVal - 1)*35
                val endCount = currentVal*35

                LogUtils.d(beCount, endCount, commonDataList.size)

                if(currSize >= endCount && beCount >= 0){
                    recyclerSyncedList.visibility = GONE
//                    val subListForPage = commonDataList.subList(beCount, endCount)
                    //commonDataListCloned.addAll(commonDataList)
                    currIndex = currentVal
                    setPageCurrTitle(currIndex)
                    val list = mutableListOf<CommonData>()
                    for(i in beCount .. endCount){
                        list.add(commonDataListCloned.get(i))
                    }
                    updateListRv(list)
                }
            }
            recyclerSyncedList.visibility = VISIBLE
        }

        paginationContainerNext.setOnClickListener{
            val currentVal = currIndex + 1
            if( ceil(paginSize) >= currentVal && currentVal > 0){
                val beCount = currIndex*35
                var endCount = currentVal*35


                LogUtils.d(beCount, endCount, commonDataList.size)

                if(beCount > 0){
                    if(endCount > currSize) endCount = currSize - 1
                    recyclerSyncedList.visibility = GONE
//                    commonDataListCloned.clear()
                    //commonDataListCloned.addAll(commonDataList)
                    val list = mutableListOf<CommonData>()
                    for(i in beCount .. endCount){
                        list.add(commonDataListCloned.get(i))
                    }
                    updateListRv(list)
                    currIndex = currentVal
                    setPageCurrTitle(currIndex)
                }
            }
            recyclerSyncedList.visibility = VISIBLE
        }

        clickCloseBtn.setOnClickListener {
            finish()
        }

//        editSearchUpdate.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                p0?.let {
//                    if (it.toString().length < 3) {
//
////                        instructionCallback?.let {
////                            it.onInstructionReceivedNoSearch()
////                        }
//                        if(commonDataList.size > 0) updateListRv(commonDataList)
//
//                    }
//                }
//            }

//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun afterTextChanged(p0: Editable?) {

//            }

//        })

        imageRunSearchUpdate.setOnClickListener {
            val searchWord = editSearchUpdate.text?.toString()?.trim()

            //LogUtils.d(searchWord.toString().length, searchWord.toString())
            if (searchWord.toString().length < 3) {

            } else {
                instructionCallback?.let {
                    it.onInstructionReceived(searchWord.toString(), {
                        LogUtils.d("IN VIEW", it.map { "${it.listOfValue?.first()}" })
                        recyclerSyncedList.visibility = VISIBLE
                        val commonDataListClonedF = it
//                        LogUtils.d(it.size, it)
                        if(it.size > 0){
                            currSize = commonDataListClonedF.size
                            currIndex = 1
                            paginSize = currSize.toDouble().div(35)
                            setPageCurrTitle(currIndex)

                            var endCount = currIndex*35
                            if(endCount > currSize) endCount = currSize - 1

                            updateListRv(commonDataListClonedF.subList(0, endCount))
                        }else{
                            updateListRv(commonDataListClonedF)
                        }

                    })
                }

            }
        }

    }

    fun updateListRv(list: MutableList<CommonData>){
//        LogUtils.d(list)
        if(list.isEmpty()) return
        try{
            (recyclerSyncedList.adapter as DataSyncedAdapter).updateItems(list)
            list?.let { SyncsList ->
                if (SyncsList.isEmpty()) {
                    recyclerSyncedList.visibility = View.GONE
                    linearEmptyContainerSyncsList.visibility = View.VISIBLE
                } else {
                    recyclerSyncedList.visibility = View.VISIBLE
                    linearEmptyContainerSyncsList.visibility = View.GONE
                }
            }
        }catch (ex: ConcurrentModificationException){
            LogUtils.e(ex)
        }
    }

    private fun setPageCurrTitle(currIndex: Int) {
        labelTitlePaginate.text = currIndex.toString()
    }

    fun refreshAdapter(list: MutableList<CommonData>) {
        

    }


}
