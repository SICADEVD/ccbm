/**
 * Created by didierboka.developer on 18/12/2021
 * mail for work:   (didierboka.developer@gmail.com)
 */

package ci.projccb.mobile.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ci.projccb.mobile.R
import ci.projccb.mobile.models.AgentModel
import ci.projccb.mobile.models.CoopDao
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.repositories.databases.daos.AgentDao
import ci.projccb.mobile.repositories.datas.AgentAuthResponse
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.showMessage
import ci.projccb.mobile.tools.Commons.Companion.showYearPickerDialog
import ci.projccb.mobile.tools.Constants
import ci.projccb.mobile.tools.Roles
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_authentification.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


@SuppressWarnings("ALL")
class AuthentificationActivity : AppCompatActivity() {


    companion object {
        const val TAG = "AuthentificationActivity.kt"
    }


    private var progressDialog: AlertDialog? = null
    var agentDoa: AgentDao? = null
    var coopDao: CoopDao? = null


    fun checkField(): Boolean {
        return inputLogin.text?.length == 0 || inputPassword.text?.length == 0
    }


    fun bindDatas() {
        inputLogin.text = Editable.Factory.getInstance().newEditable("abole".uppercase())
        //  inputLogin.text = Editable.Factory.getInstance().newEditable("cemoiuser@cemoi.com")
        //  inputLogin.text = Editable.Factory.getInstance().newEditable("cemoi.pauly@durabiliteci.com")
        inputPassword.text = Editable.Factory.getInstance().newEditable("1234567")
        //  inputPassword.text = Editable.Factory.getInstance().newEditable("fccdi@2022")

    }


    fun getLoggin(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }


//    private suspend fun authentificateAgentCoroutine() {
//        withContext(Dispatchers.IO) {
//            val postBody = GsonUtils.toJson(
//                AgentModel(codeApp = SPUtils.getInstance().getString(Constants.AGENT_CODE_APP), email = inputLogin.text?.trim().toString(), password = inputPassword.text?.trim().toString())
//            )
//
//            val clientAuth: OkHttpClient = OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.MINUTES)
//                .readTimeout(30, TimeUnit.MINUTES)
//                .writeTimeout(30, TimeUnit.MINUTES)
//                .addInterceptor(interceptor = getLoggin())
//                .build()
//
//            val request = Request.Builder()
//                //.url("${SPUtils.getInstance().getString(Constants.APP_BASE_URL)}connexion")
//                .url("https://fieldconnectv3.sicadevd.com/api/connexion")
//                .post(postBody.toRequestBody(ConfigurationBaseUrlActivity.MEDIA_TYPE_JSON))
//                .build()
//
//            clientAuth.newCall(request).execute().use { response ->
//                if (!response.isSuccessful) throw IOException("Unexpected code $response")
//                val authType = object : TypeToken<AgentAuthResponse>() {}.type
//
//                val authData = GsonUtils.fromJson<AgentAuthResponse>(response.body!!.string(), authType)
//                authData?.run {
//                    handleResponse(status_code, message, agentAuth = results)
//                }
//            }
//        }
//    }


    private suspend fun handleResponse(statusCode: Int?, message: String?, agentAuth: AgentModel?) {
        MainScope().launch(Dispatchers.Main) {
            progressDialog?.dismiss()

            if (statusCode == 500) {
                showMessage(message!!, this@AuthentificationActivity, callback = {})
            } else {
                agentAuth?.isLogged = true

                SPUtils.getInstance().put(Constants.HAS_USER_LOGGED, "yes")
                SPUtils.getInstance().put(Constants.AGENT_COOP_ID, agentAuth?.cooperativesId!!)
                SPUtils.getInstance().put(Constants.AGENT_ID, agentAuth.id!!)

                agentDoa?.insert(agentAuth)

                val intentConfiguration = Intent(this@AuthentificationActivity, ConfigurationActivity::class.java)
                intentConfiguration.putExtra(Constants.AGENT_ID, agentAuth.id)

                finish()
                startActivity(intentConfiguration)
            }
        }
    }


    fun authentificateAgent() {
        try {
            ApiClient.apiService.authAgent(user = AgentModel(codeApp = SPUtils.getInstance().getString(Constants.AGENT_CODE_APP), username = inputLogin.text?.trim().toString(), password = inputPassword.text?.trim().toString()))
                .enqueue(object: Callback<AgentAuthResponse> {
                    override fun onResponse(call: Call<AgentAuthResponse>, response: Response<AgentAuthResponse>) {
                        progressDialog?.dismiss()

                        if (response.isSuccessful) {
                            if (response.body()?.status_code == 500) {
                                val messageError = response.body()?.message
                                showMessage(messageError!! , this@AuthentificationActivity, callback = {})
                            } else {
                                coopDao?.deleteAll()

                                val agentResponseBody = response.body()
                                var roles = arrayListOf<String>() //agentResponseBody?.menu //
                                var role_name = agentResponseBody?.results?.roles?.let {
                                    if(it.size > 0) it.first().name?.uppercase()
                                    else ""
                                } //arrayListOf<String>()
                                LogUtils.d(role_name)
                                when (role_name.toString()){
                                    "Manager".uppercase() -> {
                                        roles.addAll(Roles.MANAGER)
                                    }
                                    "ADG".uppercase() -> {
                                        roles.addAll(Roles.MANAGER)
                                    }
                                    "Inspecteur".uppercase() -> {
                                        roles.addAll(Roles.INSPECTEUR)
                                    }
                                    "Coach".uppercase() -> {
                                        roles.addAll(Roles.COACH)
                                    }
                                    "Applicateur".uppercase() -> {
                                        roles.addAll(Roles.APPLICATEUR)
                                    }
                                    "Magasinier".uppercase() -> {
                                        roles.addAll(Roles.MAGASINIERSECTION)
                                    }
                                    "Magasinier Central".uppercase() -> {
                                        roles.addAll(Roles.MAGASINIERCENTRAL)
                                    }
                                    "Delegue".uppercase() -> {
                                        roles.addAll(Roles.DELEGUE)
                                    }
                                    else -> {

                                    }
                                }

                                val agentModel = agentResponseBody?.results
                                val coopModel = agentResponseBody?.cooperative

                                //remavoe some feature
                                //roles?.remove("ESTIMATION")
                                var rolesCopy = mutableListOf<String>()

                                roles?.forEachIndexed { index,item ->
                                    if(item.equals("EVALUATION", ignoreCase = true)) rolesCopy?.add(index, "AGRO_EVALUATION")
                                    else if(item.equals("DISTRIBUTION", ignoreCase = true)) rolesCopy?.add(index, "AGRO_DISTRIBUTION")
                                    else rolesCopy.add(item)
                                }

                                rolesCopy = Commons.invertValue("AGRO_EVALUATION", "AGRO_DISTRIBUTION", rolesCopy)

                                SPUtils.getInstance().put("menu", GsonUtils.toJson(rolesCopy))
                                agentModel?.isLogged = true

                                SPUtils.getInstance().put(Constants.HAS_USER_LOGGED, "yes")
                                SPUtils.getInstance().put(Constants.AGENT_COOP_ID, agentModel?.cooperativesId!!)
                                SPUtils.getInstance().put(Constants.AGENT_ID, agentModel.id!!)

                                val agentModelReal = AgentModel(
                                    id= agentModel.id,
                                    cooperativesId = agentModel.cooperativesId,
                                    createdAt= agentModel.createdAt,
                                    tp= agentModel.tp,
                                    dateNaissance = agentModel.dateNaissance,
                                    login= agentModel.login,
                                    firstname= agentModel.firstname,
                                    lastname= agentModel.lastname,
                                    username= agentModel.username,
                                    userType= agentModel.userType,
                                    typeCompte= agentModel.typeCompte,
                                    email= agentModel.email,
                                    emailVerifiedAt= agentModel.emailVerifiedAt,
                                    name = agentModel.name,
                                    nationalites= agentModel.nationalites,
                                    niveauxEtudes = agentModel.niveauxEtudes,
                                    numeroPiece= agentModel.numeroPiece,
                                    password= agentModel.password,
                                    phoneUn = agentModel.phoneUn,
                                    phoneDeux= agentModel.phoneDeux,
                                    photo= agentModel.photo,
                                    sexe= agentModel.sexe,
                                    typePieces= agentModel.typePieces,
                                    status= agentModel.status,
                                    adresse= agentModel.adresse,
                                    userId= agentModel.userId,
                                    codeApp= agentModel.codeApp,
                                    isLogged = agentModel.isLogged
                                )
                                agentDoa?.insert(agentModelReal)
                                coopDao?.insert(coopModel!!)

                                val intentConfiguration = Intent(this@AuthentificationActivity, ConfigurationActivity::class.java)
                                intentConfiguration.putExtra(Constants.AGENT_ID, agentModel.id)

                                finish()

                                ActivityUtils.startActivity(intentConfiguration)
                            }
                        } else {
                            showMessage("Utilisateur non autorisé", this@AuthentificationActivity, callback = {})
                        }
                    }

                    override fun onFailure(call: Call<AgentAuthResponse>, t: Throwable) {
                        progressDialog?.dismiss()
                        Toast.makeText(this@AuthentificationActivity, "Utilisateur non authorisé !", Toast.LENGTH_SHORT).show()
                    }
                })
        } catch (ex: Exception) {
            progressDialog?.dismiss()
            ex.printStackTrace()
            Log.e("TAG", "Exception")
            Toast.makeText(this@AuthentificationActivity, "Non authorisé !", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(R.dimen._6ssp),
            resources.getDimension(R.dimen._5ssp))

        //bindDatas()

        agentDoa = CcbRoomDatabase.getDatabase(this)?.agentDoa()
        coopDao = CcbRoomDatabase.getDatabase(this)?.coopDao()

        val progressDialogBuild = AlertDialog.Builder(this, R.style.DialogTheme)
        Commons.adjustTextViewSizesInDialog(this, progressDialogBuild!!, "Connexion en cours...",   this.resources.getDimension(R.dimen._6ssp)
            ,false, true)
        progressDialog = progressDialogBuild.create()
        //progressDialog!!.setMessage(Editable.Factory.getInstance().newEditable("Connexion en cours..."))

        imgBackAuth.setOnClickListener {
            finish()
            val intentConfiguration = Intent(this@AuthentificationActivity, ConfigurationBaseUrlActivity::class.java)
            startActivity(intentConfiguration)
        }

        actionAuthentification.setOnClickListener {
            if (checkField()) {
                Toast.makeText(this@AuthentificationActivity, "Renseignez les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog?.show()
            authentificateAgent()
            /*MainScope().launch {
                authentificateAgentCoroutine()
            }*/
        }
    }
}
