package com.phongnguyen93.medicare.notification.push_notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.functions.FunctionUser;
import com.phongnguyen93.medicare.json.JSONObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Phong Nguyen on 17-Mar-16.
 */
// abbreviated tag name
public class RegistrationIntentService extends IntentService implements JSONObjectRequest.AsyncResponse {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String GCM_TOKEN = "gcmToken";
    public static final String BASE_URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/gcmtoken/put?";

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);

            // save token
            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();

            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();

        }
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        String request = BASE_URL+"user="+ new FunctionUser(getBaseContext()).getCurrentUser().getId()+"&token="+token;
         JSONObjectRequest sendToken = new JSONObjectRequest(this);
        sendToken.execute(request);
        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server

    }


    @Override
    public void processFinish(JSONObject jsonObject) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try{
            if(jsonObject!=null){
                Log.d(TAG, jsonObject.getString("token"));
                if(jsonObject.getBoolean("status"))
                    sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
                else
                    sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}