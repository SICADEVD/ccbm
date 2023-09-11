package ci.projccb.mobile.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ci.projccb.mobile.models.LocaliteModel
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.LocaliteDao
import ci.projccb.mobile.repositories.databases.daos.ProducteurDao
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.SPUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response
import java.net.UnknownHostException


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 11/06/2022.
 **/

class SynchroniseLocaliteWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    var contextApp: Context? = null
    var localiteDao: LocaliteDao? = null
    var producteurDao: ProducteurDao? = null


    init {
        contextApp = context
        localiteDao = CcbRoomDatabase.getDatabase(contextApp!!)?.localiteDoa()
        producteurDao = CcbRoomDatabase.getDatabase(contextApp!!)?.producteurDoa()
    }


    override fun doWork(): Result {
        val localiteDatas = localiteDao?.getUnSyncedAll(
                agentID = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
            ) ?: mutableListOf()

        try {
            for (localite in localiteDatas) {
                val ecolesToken = object : TypeToken<MutableList<String>>() {}.type

                localite.ecolesNomsList = ApiClient.gson.fromJson(localite.nomsEcolesStringify, ecolesToken)
                val clientLocalite: Call<LocaliteModel> = ApiClient.apiService.synchronisationLocalite(localiteModel = localite)

                val responseLocalite: Response<LocaliteModel> = clientLocalite.execute()
                val localiteSync: LocaliteModel = responseLocalite.body()!!

                localiteSync.ecolesNomsList = mutableListOf()

                localiteDao?.syncData(id = localiteSync.id!!, synced = true, localID = localite.uid)

                val producteurLocalitesList = producteurDao?.getProducteursUnSynchronizedLocal(
                    localite.uid.toString(),
                    SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString(),
                 )!!

                for (producteur in producteurLocalitesList) {
                    producteur.localitesId = localiteSync.id?.toString()
                    producteurDao?.insert(producteur)
                }

            }
        } catch (uhex: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(uhex)
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }

        return Result.success()
    }




}
