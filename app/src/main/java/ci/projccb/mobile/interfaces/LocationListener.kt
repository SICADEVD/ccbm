package ci.projccb.mobile.interfaces

import android.location.Location
import android.location.LocationListener
import com.blankj.utilcode.util.SPUtils
import android.os.Bundle
import android.util.Log
import ci.projccb.mobile.tools.Constants

class LocationListener(provider: String?) : LocationListener {


    var mLastLocation: Location


    init {
        mLastLocation = Location(provider)
        Log.e(TAG, "onLocationChanged: CALLED 8")
    }


    companion object {
        const val TAG = "LocationListener.kt"
    }


    override fun onLocationChanged(location: Location) {
        mLastLocation.set(location)
        SPUtils.getInstance().put(Constants.PREFS_COMMON_LNG, mLastLocation.longitude.toString())
        SPUtils.getInstance().put(Constants.PREFS_COMMON_LAT, mLastLocation.latitude.toString())
    }


    override fun onProviderDisabled(provider: String) {}


    override fun onProviderEnabled(provider: String) {}


    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}