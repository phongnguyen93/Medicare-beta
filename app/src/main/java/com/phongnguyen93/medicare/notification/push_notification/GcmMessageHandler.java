package com.phongnguyen93.medicare.notification.push_notification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GcmListenerService;
import com.phongnguyen93.medicare.MyPreferenceKeys;
import com.phongnguyen93.medicare.functions.BookingsFunctions;
import com.phongnguyen93.medicare.model.NotificationContent;
import com.phongnguyen93.medicare.notification.MyNotification;

/**
 * Created by Phong Nguyen on 17-Mar-16.
 */
public class GcmMessageHandler extends GcmListenerService {


    @Override
    public void onMessageReceived(String from, Bundle data) {
        MyNotification notification = new MyNotification();
        BookingsFunctions bookingsFunctions = new BookingsFunctions(getBaseContext());

        int id  = Integer.parseInt(data.getString(NotificationContent.NOTIFICATION_ID));
        String time = data.getString(NotificationContent.NOTIFICATION_TIME);
        bookingsFunctions.updateBookingStatus(id,true);
        bookingsFunctions.updateBookingDate(id,time);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNotify = sharedPref.getBoolean(MyPreferenceKeys.CONFIRM_BOOKING_NOTIFICATION,true);
        if(isNotify){
            notification.displayNotification(
                    new NotificationContent(data.getString(NotificationContent.NOTIFICATION_ID),
                            data.getString(NotificationContent.NOTIFICATION_TYPE),
                            data.getString(NotificationContent.NOTIFICATION_DOCTOR),
                            data.getString(NotificationContent.NOTIFICATION_DATE),
                            data.getString(NotificationContent.NOTIFICATION_TIME),
                            data.getString(NotificationContent.NOTIFICATION_ADDRESS)),
                    getBaseContext());
        }
    }


}
