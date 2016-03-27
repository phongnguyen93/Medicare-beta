package com.phongnguyen93.medicare;


import com.phongnguyen93.medicare.notification.schedule_notification.AlarmReceiver;

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


}
