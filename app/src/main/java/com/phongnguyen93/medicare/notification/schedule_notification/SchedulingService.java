package com.phongnguyen93.medicare.notification.schedule_notification;

import android.app.IntentService;
import android.content.Intent;

import com.phongnguyen93.medicare.functions.BookingsFunctions;
import com.phongnguyen93.medicare.model.Booking;
import com.phongnguyen93.medicare.model.NotificationContent;
import com.phongnguyen93.medicare.notification.MyNotification;

import java.util.ArrayList;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code AlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService {

    private MyNotification notification;

    public SchedulingService() {
        super("SchedulingService");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        notification = new MyNotification();
        BookingsFunctions bookingsFunctions = new BookingsFunctions(getBaseContext());
        int alarmType = intent.getIntExtra(AlarmReceiver.ALARM_TYPE, 3);
        if(alarmType == AlarmReceiver.DAILY_ALARM_TYPE){
            String todayDate= intent.getStringExtra("today_date");
            ArrayList<Booking> bookings =  bookingsFunctions.getBookingsByDate(todayDate);
            setupDailyNotification(bookings);
        }else
        if(alarmType == AlarmReceiver.UPCOMING_ALARM_TYPE){
            int bookingId = intent.getIntExtra("booking_id",0);
            Booking booking = bookingsFunctions.getBooking(bookingId);
            NotificationContent content = new NotificationContent(booking.getId()+"",
                    MyNotification.ALARM_NOTI,
                    booking.getDr_name(),
                    booking.getDate(),
                    booking.getTime(),
                    booking.getAddress());
            notification.displayNotification(content,getBaseContext());
        }

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }

    private void setupDailyNotification(ArrayList<Booking> bookings) {
        ArrayList<NotificationContent> contents = new ArrayList<>();
        for(int i = 0;i<bookings.size();i++){
            Booking booking = bookings.get(i);
            contents.add(new NotificationContent(booking.getId()+"",
                    "booking_alarm",
                    booking.getDr_name(),
                    booking.getDate(),
                    booking.getTime(),
                    booking.getAddress()));
        }
        notification.displayGroupNotification(contents,getBaseContext());
    }

}
