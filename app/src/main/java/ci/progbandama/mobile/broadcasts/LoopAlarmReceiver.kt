package ci.progbandama.mobile.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ci.progbandama.mobile.tools.Commons


class LoopAlarmReceiver : BroadcastReceiver() {


    companion object {
        const val REQUEST_CODE = 4120
    }


    override fun onReceive(context: Context, intent: Intent) {
        Commons.synchronisation("all", context)
    }
}
