package ci.projccb.mobile.repositories.apis

import ci.projccb.mobile.repositories.apis.services.ApiService
import ci.projccb.mobile.tools.SendErrorOnline
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Response
import java.lang.Exception
import java.lang.RuntimeException
import okio.Buffer
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


object ApiClient {


    val gson : Gson by lazy {
        GsonBuilder().excludeFieldsWithoutExposeAnnotation().setLenient().create()
    }

    fun getLoggin(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory,
                trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                //.addInterceptor(interceptor = CustomInterceptor())
                .addInterceptor(interceptor = getLoggin())
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }


    private val httpClient : OkHttpClient by lazy {

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.MINUTES)
            //.addInterceptor(interceptor = CustomInterceptor())
            .addInterceptor(interceptor = getLoggin())
            .build()
    }


    internal val retrofit : Retrofit by lazy {
        //  val baseUrl = if (SPUtils.getInstance().getString(Constants.APP_BASE_URL).isBlank()) "https://jularis.com/api/" else SPUtils.getInstance().getString(Constants.APP_BASE_URL)
        val baseUrl = "https://ccbw.sicadevd.com/api/"
        //val baseUrl = "http://192.168.1.5:5000/api/"
        //val baseUrl = "http://192.168.43.102:5000/api/"
        //val  baseUrl = "https://fieldconnectv3.sicadevd.com/api/"
        //val  baseUrl = "https://demo.sicadevd.com/api/"
        //  val  baseUrl = "https://anouanze.sicadevd.com/api/"
        //  val  baseUrl = "https://cemoi.sicadevd.com/api/"
        //  val  baseUrl = "https://ccb.sicadevd.com/api/"
        //  val  baseUrl = "https://sivaco.sicadevd.com/api/"

        Retrofit.Builder()
            .baseUrl(baseUrl)
            //.client(httpClient)
            .client(getUnsafeOkHttpClient().build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    val apiService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val buffer = Buffer()
        request.body?.writeTo(buffer)
        val requestBodyJson = buffer.readUtf8()

        // Log response using HttpLoggingInterceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val loggingResponse = loggingInterceptor.intercept(chain)

        // Pass response body to SendErrorOnline
        SendErrorOnline( requestBodyJson+" -||- "+response.body?.string().toString()).execute()

        return loggingResponse
    }
}
