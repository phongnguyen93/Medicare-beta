package com.namlongsolutions.medicare.notification.schedule_notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.namlongsolutions.medicare.MyPreferenceKeys;
import com.namlongsolutions.medicare.functions.BookingsFunctions;
import com.namlongsolutions.medicare.model.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SchedulingService} to do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    // The pending intent that is triggered when the alarm fires.
    private BookingsFunctions bookingsFunctions;
    private Context context;
    public static final int DAILY_ALARM_TYPE = 0;
    public static final int UPCOMING_ALARM_TYPE = 1;
    public static final String ALARM_TYPE = "alarm type";

    public AlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SchedulingService.class);
        service.putExtra(ALARM_TYPE, intent.getIntExtra(ALARM_TYPE, 0));

        if (intent.getIntExtra(ALARM_TYPE, 0) == UPCOMING_ALARM_TYPE) {
            service.putExtra("booking_id", intent.getIntExtra("booking_id", 0));
        } else if (intent.getIntExtra(ALARM_TYPE, 0) == DAILY_ALARM_TYPE) {
            service.putExtra("today_date", intent.getStringExtra("today_date"));
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isAlarm = sharedPref.getBoolean(MyPreferenceKeys.UPCOMING_BOOKING_NOTIFICATION, true);
        // Start the service, keeping the device awake while it is launching.
        if (isAlarm)
            startWakefulService(context, service);
        // END_INCLUDE(alarm_onreceive)
    }

    // Enable alarm: daily alarm, upcoming alarm
    public void enableAlarm(Context context) {
        this.context = context;

        bookingsFunctions = new BookingsFunctions(context);

        Calendar calendar = Calendar.getInstance();
        if (calendar.getTime().getHours() == 6 && calendar.getTime().getMinutes() == 0)
            setDailySummaryAlarm();
        if (getTodayBookings() != null) {
            setUpcomingAlarm();
        }
        // Enable {@code BootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    // Set up daily alarm to display summary notification at 6:00 a.m
    private void setDailySummaryAlarm() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALARM_TYPE, DAILY_ALARM_TYPE);
        intent.putExtra("today_date", getTodayDate());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//            // Set the alarm's trigger time to 6:00 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        // Set the alarm to fire at approximately 6:00 a.m., according to the device's
        // clock, and to repeat once a day.
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    // Set up alarm for upcoming booking on day
    private void setUpcomingAlarm() {
        ArrayList<String> alarmTime = new ArrayList<>();
        ArrayList<Integer> bookingId = new ArrayList<>();
        for (int i = 0; i < getTodayBookings().size(); i++) {
            alarmTime.add(getTodayBookings().get(i).getTime());
            bookingId.add(getTodayBookings().get(i).getId());
        }
        for (int i = 0; i < alarmTime.size(); i++) {
            setupAlarmIntent(bookingId.get(i), alarmTime.get(i));
        }
    }

    // Set up intent with booking id
    private void setupAlarmIntent(int bookingId, String time) {
        String[] splitTime = time.split(":");
        int hour = Integer.parseInt(splitTime[0]);
        int minute = Integer.parseInt(splitTime[1]);
        Calendar calendar = Calendar.getInstance();
        if(calendar.getTime().getHours()< hour-1){
            calendar.set(Calendar.HOUR_OF_DAY, hour-1);
            calendar.set(Calendar.MINUTE, minute);

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(ALARM_TYPE, UPCOMING_ALARM_TYPE);
            intent.putExtra("booking_id", bookingId);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }

    // get all bookings today
    private ArrayList<Booking> getTodayBookings() {
        return bookingsFunctions.getBookingsByDate(getTodayDate());
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

}
