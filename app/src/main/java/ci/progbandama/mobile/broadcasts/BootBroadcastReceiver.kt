package ci.progbandama.mobile.broadcasts

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver
import ci.progbandama.mobile.tools.Commons


class BootBroadcastReceiver : WakefulBroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        Commons.synchronisation("all", context)
    }
}
