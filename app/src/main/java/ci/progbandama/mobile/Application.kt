package ci.progbandama.mobile

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

        if (isFirebaseInitialized()) {
            // Firebase is initialized, proceed with Firebase-dependent code
            FirebaseApp.initializeApp(applicationContext)

        } else {
            // Firebase is not initialized, handle accordingly
        }

        CaocConfig.Builder.create().apply()
    }

    fun isFirebaseInitialized(): Boolean {
        return try {
            FirebaseApp.getInstance()
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
            true // Firebase is initialized
        } catch (e: IllegalStateException) {
            Log.e("FirebaseCheck", "Firebase is not initialized", e)
            false // Firebase is not initialized
        }
    }

}