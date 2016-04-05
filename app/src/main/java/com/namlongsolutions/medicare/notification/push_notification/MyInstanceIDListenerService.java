package com.namlongsolutions.medicare.notification.push_notification;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Phong Nguyen on 17-Mar-16.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
