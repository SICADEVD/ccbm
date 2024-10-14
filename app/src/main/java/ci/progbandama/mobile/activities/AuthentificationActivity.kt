/**
 * Created by didierboka.developer on 18/12/2021
 * mail for work:   (didierboka.developer@gmail.com)
 */

package ci.progbandama.mobile.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ci.progbandama.mobile.R
import ci.progbandama.mobile.databinding.ActivityAuthentificationBinding
import ci.progbandama.mobile.models.AgentModel
import ci.progbandama.mobile.models.CoopDao
import ci.progbandama.mobile.repositories.apis.ApiClient
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import ci.progbandama.mobile.repositories.databases.daos.AgentDao
import ci.progbandama.mobile.repositories.datas.AgentAuthResponse
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Commons.Companion.showMessage
import ci.progbandama.mobile.tools.Constants
import ci.progbandama.mobile.tools.Roles
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.coroutines.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@SuppressWarnings("ALL")
class AuthentificationActivity : AppCompatActivity() {


    companion object {
        const val TAG = "AuthentificationActivity.kt"
    }

    private var progressDialog: AlertDialog? = null
    var agentDoa: AgentDao? = null
    var coopDao: CoopDao? = null


    fun checkField(): Boolean {
        return binding.inputLogin.text?.length == 0 || binding.inputPassword.text?.length == 0
    }


    fun bindDatas() {
        binding.inputLogin.text = Editable.Factory.getInstance().newEditable("abole".uppercase())
        //  binding.inputLogin.text = Editable.Factory.getInstance().newEditable("cemoiuser@cemoi.com")
        //  binding.inputLogin.text = Editable.Factory.getInstance().newEditable("cemoi.pauly@durabiliteci.com")
        binding.inputPassword.text = Editable.Factory.getInstance().newEditable("1234567")
        //  binding.inputPassword.text = Editable.Factory.getInstance().newEditable("fccdi@2022")

    }


    fun getLoggin(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }


//    private suspend fun authentificateAgentCoroutine() {
//        withContext(Dispatchers.IO) {
//            val postBody = GsonUtils.toJson(
//                AgentModel(codeApp = SPUtils.getInstance().getString(Constants.AGENT_CODE_APP), email = binding.inputLogin.text?.trim().toString(), password = binding.inputPassword.text?.trim().toString())
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


    private fun handleResponse(statusCode: Int?, message: String?, agentAuth: AgentModel?) {
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
            ApiClient.apiService.authAgent(user = AgentModel(codeApp = SPUtils.getInstance().getString(Constants.AGENT_CODE_APP), username = binding.inputLogin.text?.trim().toString(), password = binding.inputPassword.text?.trim().toString()))
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
                                var roles = mutableListOf<String>() //agentResponseBody?.menu //
                                agentResponseBody?.results?.roles?.let {
                                    it.forEach { role ->

                                        var role_name = role.name?.uppercase()

                                        LogUtils.d(role_name)
                                        when (role_name.toString()){
                                            "Manager".uppercase() -> {
                                                roles.addAll(Roles.MANAGER)
                                            }
                                            "Directeur".uppercase() -> {
                                                roles.addAll(Roles.MANAGER)
                                            }
                                            else -> {

                                            }
                                        }
                                        when (role_name.toString()){
                                            "ADG".uppercase() -> {
                                                roles.addAll(Roles.MANAGER)
                                            }
                                            else -> {

                                            }
                                        }
                                        when (role_name.toString()){
                                            "Inspecteur".uppercase() -> {
                                                roles.addAll(Roles.INSPECTEUR)
                                            }
                                            else -> {

                                            }
                                        }
                                        when (role_name.toString()){
                                            "Coach".uppercase() -> {
                                                roles.addAll(Roles.COACH)
                                            }
                                            else -> {

                                            }
                                        }
                                        when (role_name.toString()){
                                            "Applicateur".uppercase() -> {
                                                roles.addAll(Roles.APPLICATEUR)
                                            }
                                            else -> {

                                            }
                                        }
                                        when (role_name.toString()){
                                            "Magasinier".uppercase() -> {
                                                roles.addAll(Roles.MAGASINIERSECTION)
                                            }
                                            else -> {

                                            }
                                        }
                                        when (role_name.toString()){
                                            "Magasinier Central".uppercase() -> {
                                                roles.addAll(Roles.MAGASINIERCENTRAL)
                                            }
                                            else -> {

                                            }
                                        }
                                        when (role_name.toString()){
                                            "Delegue".uppercase() -> {
                                                roles.addAll(Roles.DELEGUE)
                                            }
                                            else -> {

                                            }
                                        }
                                    }


                                } //arrayListOf<String>()

                                roles = roles.toSet().toMutableList()

//                                LogUtils.d(roles, roles.toSet().toMutableList())

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

    private lateinit var binding: ActivityAuthentificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthentificationBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Commons.setSizeOfAllTextViews(this, findViewById<ViewGroup>(android.R.id.content),
            resources.getDimension(com.intuit.ssp.R.dimen._6ssp),
            resources.getDimension(com.intuit.ssp.R.dimen._5ssp))

        //bindDatas()

        agentDoa = ProgBandRoomDatabase.getDatabase(this)?.agentDoa()
        coopDao = ProgBandRoomDatabase.getDatabase(this)?.coopDao()

        val progressDialogBuild = AlertDialog.Builder(this, R.style.DialogTheme)
        Commons.adjustTextViewSizesInDialog(this, progressDialogBuild!!, "Connexion en cours...",   this.resources.getDimension(com.intuit.ssp.R.dimen._6ssp)
            ,false, true)
        progressDialog = progressDialogBuild.create()
        //progressDialog!!.setMessage(Editable.Factory.getInstance().newEditable("Connexion en cours..."))

        binding.imgBackAuth.setOnClickListener {
            finish()
            val intentConfiguration = Intent(this@AuthentificationActivity, ConfigurationBaseUrlActivity::class.java)
            startActivity(intentConfiguration)
        }

        binding.actionAuthentification.setOnClickListener {
            /*showMessage(
                message = "Enregistrer le tracé ?",
                context = this,
                finished = true,
                deconnec = false,
                showNo = true,
                callback = ::bindDatas
            )*/

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
