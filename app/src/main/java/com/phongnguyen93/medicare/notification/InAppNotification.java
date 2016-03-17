package com.phongnguyen93.medicare.notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.phongnguyen93.medicare.R;

/**
 * Created by Phong Nguyen on 17-Mar-16.
 */
public class InAppNotification {

    public static final int ALERT_SNACKBAR = 0;
    public static final int WARNING_SNACKBAR = 1;
    public static final int INFO_SNACKBAR = 2;
    public static final int NORMAL_SNACKBAR = 3;
    private static final int ACTION_SNACKBAR = 4;

    private Snackbar currentDisplaySnackbar;



    private SnackBarAction snackBarAction;

    // Interface for delegate action to snackbar
    public interface SnackBarAction{
        void setSnackBarAction(View v);
    }

    // Empty constructor for display snackbar
    public InAppNotification(){

    }
    // Constructor for action snackbar
    public InAppNotification(SnackBarAction snackBarAction){
        this.snackBarAction = snackBarAction;
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
                                snackBarAction.setSnackBarAction(v);

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
//            case INFO_SNACKBAR:
//                Snackbar snackbarInfo = Snackbar.make(layout,context.getResources().getString(R.string.success_connect_noti), Snackbar.LENGTH_SHORT);
//                sbView = snackbarInfo.getView();
//                textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                textView.setTextColor(Color.WHITE);
//                sbView.setBackgroundColor(context.getResources().getColor(R.color.accent));
//                // set current display snackbar
//                setCurrentDisplaySnackbar(snackbarInfo);
//                snackbarInfo.show();
//                break;
            case NORMAL_SNACKBAR:
                Snackbar snackbar = Snackbar.make(layout,displayText, Snackbar.LENGTH_SHORT);
                sbView = snackbar.getView();
                textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                sbView.setBackgroundColor(Color.BLACK);
                // set current display snackbar
                setCurrentDisplaySnackbar(snackbar);
                snackbar.show();
                break;
            case ACTION_SNACKBAR:
                Snackbar snackbarAction = Snackbar.make(layout,displayText, Snackbar.LENGTH_LONG)
                        .setAction(actionText, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackBarAction.setSnackBarAction(v);
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
}
