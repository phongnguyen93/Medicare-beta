package com.phongnguyen93.medicare.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.activities.BookingDetailActivity;
import com.phongnguyen93.medicare.activities.MainActivity;
import com.phongnguyen93.medicare.model.NotificationContent;

import java.util.ArrayList;


/**
 * Created by Phong Nguyen on 17-Mar-16.
 *
 */
public class MyNotification {

    public static final String ALARM_NOTI = "booking_alarm";
    public static final String CONFIRM_NOTI = "booking_confirm";
    public final static String GROUP_NOTI = "group_key_noti_by_date";

    public static final int ALERT_SNACKBAR = 0;
    public static final int WARNING_SNACKBAR = 1;
    public static final int INFO_SNACKBAR = 2;
    public static final int NORMAL_SNACKBAR = 3;
    private static final int ACTION_SNACKBAR = 4;

    private Snackbar currentDisplaySnackbar;



    private SnackBarAction snackBarAction;

    // Interface for delegate action to snackbar
    public interface SnackBarAction{
        void setSnackBarAction();
    }

    // Empty constructor for display snackbar
    public MyNotification(){

    }
    // Constructor for action snackbar
    public MyNotification(SnackBarAction snackBarAction){
        this.snackBarAction = snackBarAction;
    }


    public void displayToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    // Show snack bar to display informations or make actions
    public void displaySnackbar(int type, View layout, String displayText , String actionText){
        View sbView;
        TextView textView;
        switch (type) {
            //display disconnection alert snack bar
            case ALERT_SNACKBAR:
                Snackbar snackbarAlert = Snackbar.make(layout,displayText, Snackbar.LENGTH_INDEFINITE)
                        .setAction(actionText, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackBarAction.setSnackBarAction();

                            }
                        })
                        .setActionTextColor(Color.WHITE);
                sbView = snackbarAlert.getView();
                textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                sbView.setBackgroundColor(Color.RED);
                // set current display snackbar
                setCurrentDisplaySnackbar(snackbarAlert);
                snackbarAlert.show();
                break;
            //display slow connection warning snack bar
            case WARNING_SNACKBAR:
                Snackbar snackbarWarning = Snackbar.make(layout, displayText, Snackbar.LENGTH_LONG);
                sbView = snackbarWarning.getView();
                textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                sbView.setBackgroundColor(Color.YELLOW);
                // set current display snackbar
                setCurrentDisplaySnackbar(snackbarWarning);
                snackbarWarning.show();
                break;
            //display confirm connected success snackbar
            case INFO_SNACKBAR:
                Snackbar snackbarInfo = Snackbar.make(layout,displayText, Snackbar.LENGTH_SHORT);
                sbView = snackbarInfo.getView();
                textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                sbView.setBackgroundColor(Color.parseColor("#45AD45"));
                // set current display snackbar
                setCurrentDisplaySnackbar(snackbarInfo);
                snackbarInfo.show();
                break;
            case NORMAL_SNACKBAR:
                Snackbar snackbar = Snackbar.make(layout,displayText, Snackbar.LENGTH_SHORT);
                sbView = snackbar.getView();
                textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                sbView.setBackgroundColor(Color.parseColor("#f77c46"));
                // set current display snackbar
                setCurrentDisplaySnackbar(snackbar);
                snackbar.show();
                break;
            case ACTION_SNACKBAR:
                Snackbar snackbarAction = Snackbar.make(layout,displayText, Snackbar.LENGTH_LONG)
                        .setAction(actionText, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackBarAction.setSnackBarAction();
                            }
                        })
                        .setActionTextColor(Color.RED);
                sbView = snackbarAction.getView();
                textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                sbView.setBackgroundColor(Color.BLACK);
                snackbarAction.show();
                break;
            default:
                throw new UnsupportedOperationException("Unknown snackbar type :"+ type);
        }
    }

    //hide current displayed snack bar
    public void hideSnackbar(){
        if(getCurrentDisplaySnackbar() != null)
            getCurrentDisplaySnackbar().dismiss();
    }

    public Snackbar getCurrentDisplaySnackbar() {
        return currentDisplaySnackbar;
    }

    public void setCurrentDisplaySnackbar(Snackbar currentDisplaySnackbar) {
        this.currentDisplaySnackbar = currentDisplaySnackbar;
    }

    // Display a notification
    public void displayNotification(NotificationContent content,Context context){
        String title = "";
        String msg ="";
        if(content.getType().equals(ALARM_NOTI)) {
            title = context.getResources().getString(R.string.alarm_noti_title)+" "+content.getTime();
            msg = context.getResources().getString(R.string.alarm_noti_message) + "\n" +
                    context.getResources().getString(R.string.confirm_noti_doctor) + content.getDoctor() + "\n" +
                    context.getResources().getString(R.string.confirm_noti_address) + content.getAddress();
        }
        if (content.getType().equals(CONFIRM_NOTI)){
            title = context.getResources().getString(R.string.confirm_noti_title);
            msg = context.getResources().getString(R.string.confirm_noti_message) + "\n" +
                    context.getResources().getString(R.string.confirm_noti_doctor) + content.getDoctor() + "\n" +
                    context.getResources().getString(R.string.confirm_noti_date) + content.getDate() + "\n" +
                    context.getResources().getString(R.string.confirm_noti_time) + content.getTime() + "\n" +
                    context.getResources().getString(R.string.confirm_noti_address) + content.getAddress();
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, BookingDetailActivity.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        resultIntent.putExtra("booking_id", content.getId());
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // setup Notification Builder
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.applogo);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .addAction(R.drawable.ic_info_outline_black_18dp, "Xem chi tiết", resultPendingIntent)
                .setLargeIcon(icon)
                .setContentText(context.getResources().getString(R.string.confirm_noti_doctor) + content.getDoctor())
                .setSmallIcon(R.drawable.ic_event_available_white_18dp)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg));


        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notiBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notiBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(CONFIRM_NOTI,Integer.parseInt(content.getId()), notiBuilder.build());
    }

    // Display group notification for summary
    public void displayGroupNotification(ArrayList<NotificationContent> contents, Context context){
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.applogo);

        String title =context.getResources().getString(R.string.group_noti_title)+" "+ contents.size()+" lịch khám ";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(icon)
                .setGroup(GROUP_NOTI)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setGroupSummary(true)
                .setContentTitle(title);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setSummaryText(contents.get(0).getDate()).setBigContentTitle(title);
        for(int i = 0; i < contents.size(); i++) {
            inboxStyle.addLine(contents.get(i).getTime() + "  " +
                    context.getResources().getString(R.string.confirm_noti_doctor)
                    + contents.get(i).getDoctor());
        }
        mBuilder.setStyle(inboxStyle).build();
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mNotificationManager.notify(contents.size(),mBuilder.build());
    }
}
