package ci.progbandama.mobile

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient


class Application: Application() {

    @SuppressLint("RestrictedApi")
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        CustomActivityOnCrash.install(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .cache(Cache(this.getCacheDir(), 10024)) // Define the cache size
            .build()

        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(okHttpClient))
        val picasso = builder.build()
        Picasso.setSingletonInstance(picasso)

        CaocConfig.Builder.create().apply()
    }


}