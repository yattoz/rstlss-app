package com.rstlss.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager
import com.rstlss.app.alarm.RadioAlarm
import com.rstlss.app.planning.Planning
import com.rstlss.app.playerstore.PlayerStore

class BootBroadcastReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context, arg1: Intent) {
        //[REMOVE LOG CALLS]Log.d(tag, "Broadcast Receiver received $arg1")
        // define preferenceStore for places of the program that needs to access Preferences without a context
        com.rstlss.app.preferenceStore = PreferenceManager.getDefaultSharedPreferences(context)

        if (arg1.action == Intent.ACTION_BOOT_COMPLETED) {
            RadioAlarm.instance.setNextAlarm(context) // schedule next alarm
        }

        if (arg1.getStringExtra("action") == "${com.rstlss.app.tag}.${com.rstlss.app.Actions.PLAY_OR_FALLBACK.name}" )
        {

            RadioAlarm.instance.setNextAlarm(context) // schedule next alarm
            Planning.instance.parseUrl(context = context)
            if (!PlayerStore.instance.isInitialized)
                PlayerStore.instance.initApi()
            if (PlayerStore.instance.streamerName.value.isNullOrBlank())
                PlayerStore.instance.initPicture(context)

            val i = Intent(context, com.rstlss.app.RadioService::class.java)
            i.putExtra("action", com.rstlss.app.Actions.PLAY_OR_FALLBACK.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(i)
            else
                context.startService(i)
        }
    }
}