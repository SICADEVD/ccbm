package ci.progbandama.mobile.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ci.progbandama.mobile.interfaces.LocationListener
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics


@SuppressWarnings("All")
class GpsService : Service() {


    private var mLocationManager: LocationManager? = null
    var notificationManager: NotificationManager? = null


    var mLocationListeners = arrayOf(
        LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER)
    )


    companion object {
        private const val TAG = "GpsService.kt"
        private const val LOCATION_INTERVAL = 1000
        private const val LOCATION_DISTANCE = 10f
    }


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "field_connect"
            val channel = NotificationChannel(CHANNEL_ID, "ProgBand", NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            notificationManager?.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(1, notification)
        }

        initializeLocationManager()

        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[1]
            )
        } catch (ex: SecurityException) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        } catch (ex: IllegalArgumentException) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.e(TAG, "gps provider does not exist " + ex.message)
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "gps provider does not exist " + ex.message)
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()

        try {
            if (mLocationManager != null) {
                for (i in mLocationListeners.indices) {
                    try {
                        mLocationManager!!.removeUpdates(mLocationListeners[i])
                        if (Build.VERSION.SDK_INT >= 26) {
                            stopForeground(true)
                            notificationManager?.cancel(1)
                        } else {
                            stopSelf()
                        }
                    } catch (ex: Exception) {
                        Log.e(TAG, "fail to remove location listners, ignore", ex)
                    }
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager")
        try {
            if (mLocationManager == null) {
                mLocationManager =
                    applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
            }
        } catch (ex: Exception) {
            LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }
}
