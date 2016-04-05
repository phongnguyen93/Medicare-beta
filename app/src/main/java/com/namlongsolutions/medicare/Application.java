package com.namlongsolutions.medicare;


import android.content.Context;
import android.support.multidex.MultiDex;

import com.namlongsolutions.medicare.notification.schedule_notification.AlarmReceiver;

/**
 * Created by Phong Nguyen on 10/25/2015.
 */


public class Application extends android.app.Application {


    public Application(){

    }
    @Override
    public void onCreate() {
        super.onCreate();
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.enableAlarm(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
