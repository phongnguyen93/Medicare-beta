package com.phongnguyen93.medicare.notification.push_notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.model.NotificationContent;
import com.phongnguyen93.medicare.notification.InAppNotification;

/**
 * Created by Phong Nguyen on 17-Mar-16.
 */
public class GcmMessageHandler extends GcmListenerService {


    @Override
    public void onMessageReceived(String from, Bundle data) {
        InAppNotification notification = new InAppNotification();
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
