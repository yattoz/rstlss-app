package com.rstlss.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.rstlss.app.BootBroadcastReceiver
import androidx.preference.PreferenceManager
import com.rstlss.app.*
import java.util.*

class RadioAlarm {

    companion object {
        val instance by lazy {
            RadioAlarm()
        }
    }

    private lateinit var alarmIntent: PendingIntent
    private lateinit var showIntent: PendingIntent

    private fun defineIntents(c: Context)
    {
        alarmIntent = Intent(c, com.rstlss.app.BootBroadcastReceiver::class.java).let { intent ->
            intent.putExtra("action", "$tag.${com.rstlss.app.Actions.PLAY_OR_FALLBACK.name}")
            PendingIntent.getBroadcast(c, 0, intent, 0)
        }
        showIntent = Intent(c, ParametersActivity::class.java).let { intent ->
            intent.putExtra("action", "alarm")
            PendingIntent.getActivity(c, 0, intent, 0)
        }
    }


    fun cancelAlarm(c: Context)
    {
        defineIntents(c)
        val alarmManager = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmIntent)
    }

    fun setNextAlarm(c: Context, isForce: Boolean = false, forceTime: Int? = null, forceDays: Set<String>? = null)
    {
        defineIntents(c)
        // don't do anything if the preference is set to FALSE, of course.
        if (!PreferenceManager.getDefaultSharedPreferences(c).getBoolean("isWakingUp", false) && !isForce)
            return

        val alarmManager = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val showIntent = Intent(c, ParametersActivity::class.java).let { intent ->
            intent.putExtra("action", com.rstlss.app.ActionOpenParam.ALARM.name)
            PendingIntent.getActivity(c, 0, intent, 0)
        }
        val time = findNextAlarmTime(c, forceTime, forceDays)
        if (time > 0)
            AlarmManagerCompat.setAlarmClock(alarmManager, time, showIntent, alarmIntent)
    }

    fun findNextAlarmTime(c: Context, forceTime: Int? = null, forceDays: Set<String>? = null) : Long
    {
        val calendar = Calendar.getInstance()

        val days = forceDays ?: PreferenceManager.getDefaultSharedPreferences(c).getStringSet("alarmDays", setOf())
        val time = forceTime ?: PreferenceManager.getDefaultSharedPreferences(c).getInt("alarmTimeFromMidnight", 7*60) // default value is set to 07:00 AM

        val hourOfDay = time / 60 //time is in minutes
        val minute = time % 60

        val fullWeekOrdered = weekdaysSundayFirst //Sunday --> Saturday
        val selectedDays = arrayListOf<Int>()
        for (item in fullWeekOrdered)
        {
            if (days!!.contains(item))
                selectedDays.add(fullWeekOrdered.indexOf(item))
        }

        if (selectedDays.isEmpty()) // in case the user uncheck all boxes... do nothing.
            return 0


        val currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 (Sunday) to 6 (Saturday)
        val datePassed = if (calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE) >= time ) 1 else 0
        var nextSelectedDay = (currentDay + datePassed)%7
        var i = 0 + datePassed
        while (!selectedDays.contains(nextSelectedDay))
        {
            nextSelectedDay = (nextSelectedDay + 1)%7
            i++
        }
        // We found out the next selected day in the list.
        // we must move 'i' days forward
        calendar.isLenient = true
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + i, hourOfDay, minute)

        //[REMOVE LOG CALLS]Log.d(tag, calendar.toString())


        return calendar.timeInMillis
    }

    fun snooze(c: Context)
    {
        defineIntents(c)
        val snoozeString = preferenceStore.getString("snoozeDuration", "10") ?: "10"
        val snoozeMinutes = if (snoozeString == c.getString(R.string.disable)) 0  else Integer.parseInt(snoozeString)
        if (snoozeMinutes > 0)
        {
            val alarmManager = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            AlarmManagerCompat.setAlarmClock(alarmManager, Calendar.getInstance().timeInMillis + (snoozeMinutes * 60 * 1000), showIntent, alarmIntent)

            // now that the next alarm has been scheduled, kill the app
            c.startService(Intent(c, RadioService::class.java).putExtra("action", com.rstlss.app.Actions.KILL.name))
        }
    }
}