package ci.projccb.mobile.broadcasts

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver
import ci.projccb.mobile.tools.Commons


class BootBroadcastReceiver : WakefulBroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        Commons.synchronisation("all", context)
    }
}
