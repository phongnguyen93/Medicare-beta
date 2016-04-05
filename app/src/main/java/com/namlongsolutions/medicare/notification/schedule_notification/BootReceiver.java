package com.namlongsolutions.medicare.notification.schedule_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.namlongsolutions.medicare.MyPreferenceKeys;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean setAlarm = sharedPref.getBoolean(MyPreferenceKeys.UPCOMING_BOOKING_NOTIFICATION, true);
        AlarmReceiver alarm = new AlarmReceiver();
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            if(setAlarm)
                alarm.enableAlarm(context);
        }
    }
}
//END_INCLUDE(autostart)
