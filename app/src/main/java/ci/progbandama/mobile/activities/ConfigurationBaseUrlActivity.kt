package ci.progbandama.mobile.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ci.progbandama.mobile.R
import ci.progbandama.mobile.models.CommonResponse
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.Commons
import ci.progbandama.mobile.tools.Constants
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_configuration_base_url.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class ConfigurationBaseUrlActivity : AppCompatActivity() {


    private var progressDialog: ProgressDialog? = null


    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
        const val TAG = "ConfigurationBaseUrlActivity.kt"
    }


    fun checkField(): Boolean {
        return inputCode.text?.length == 0
    }


    private suspend fun checkCode(code: String?) {
        withContext(Dispatchers.IO) {
            val postBody = GsonUtils.toJson(CommonData(codeapp = code))

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://sicadevd.com/api/getdomain")
                //.url("https://jularis.com/api/getdomain")
                .post(postBody.toRequestBody(MEDIA_TYPE_JSON))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val domainType = object : TypeToken<CommonResponse>() {}.type

                val domaineData = GsonUtils.fromJson<CommonResponse>(response.body!!.string(), domainType)
                domaineData?.run {
                    SPUtils.getInstance().put(Constants.AGENT_CODE_APP, code)
                    handleResponse(statusCode, message)
                }
            }
        }
    }


    private suspend fun handleResponse(statusCode: Int?, message: String?) {
        MainScope().launch(Dispatchers.Main) {
            progressDialog?.dismiss()

            if (statusCode == 500) {
                Commons.showMessage(message!!, this@ConfigurationBaseUrlActivity, callback = {})
            } else {
                SPUtils.getInstance().put(Constants.APP_BASE_URL, message!!)
                SPUtils.getInstance().put(Constants.APP_FIRST_LAUNCH, "no")
                SPUtils.getInstance().put(Constants.APP_BASE_URL_IS_CONFIGURED, "yes")

                finish()

                val intent = Intent(applicationContext, AuthentificationActivity::class.java)
                startActivity(intent)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration_base_url)

        progressDialog = ProgressDialog(this, R.style.DialogTheme)
        Commons.adjustTextViewSizesInDialog(this, progressDialog!!, "Connexion en cours...",   this.resources.getDimension(R.dimen._6ssp),
            false)
        //progressDialog?.setMessage("Connexion en cours...")

        btnCodeCheck.setOnClickListener {
            if (checkField()) {
                Commons.showMessage("Saisir votre code svp", this, callback = {})
            } else {
                MainScope().launch {
                    progressDialog?.show()
                    checkCode(inputCode.text?.toString())
                }
            }
        }
    }
}