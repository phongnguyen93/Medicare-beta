package com.phongnguyen93.medicare.functions;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.thread.network_thread.JSONObjectRequest;
import com.phongnguyen93.medicare.model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Phong Nguyen on 11/14/2015.
 */
public class UserFunctions implements JSONObjectRequest.AsyncResponse {
    private static final String TAG = "medicare";
    DbOperations dp;
    Context context;
    String CURRENT_USER_ID;
    String CURRENT_USER_NAME;
    String CURRENT_USER_PHONE;
    String CURRENT_USER_EMAIL;

    public UserFunctions(Context context) {
        this.context =context;
    }


    public void setCurrentUser(String user_id, String user_pass) {
        connection(user_id, user_pass);

    }

    private void connection(String user_id, String user_pass) {
        JSONObjectRequest jsonObjectRequest =new JSONObjectRequest(this);
        String URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/service/getuserbyid?id=" + user_id + "&pass=" + user_pass;
        jsonObjectRequest.execute(URL);
    }

    @Override
    public void processFinish(JSONObject jsonObject) {
        if(jsonObject!=null)
        {
            try {
                setUser(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUser(JSONObject jsonObject) throws JSONException{
        CURRENT_USER_ID = jsonObject.getString("id");
        CURRENT_USER_NAME = jsonObject.getString("name");
        CURRENT_USER_PHONE = jsonObject.getString("phone");
        CURRENT_USER_EMAIL = jsonObject.getString("email");
        Log.d(TAG,CURRENT_USER_ID+"");
        dp = new DbOperations(context);
        dp.putUser(dp,CURRENT_USER_ID,CURRENT_USER_NAME,CURRENT_USER_PHONE,CURRENT_USER_EMAIL);
    }

    public User getCurrentUser(){
        dp = new DbOperations(context);
        Cursor CR =  dp.getUser(dp);
        User user =null;
        if(CR.getCount()== 0 )
        {
            return null;
        }
        CR.moveToFirst();
        do {
            String id = CR.getString(0);
            String name = CR.getString(1);
            String phone = CR.getString(2);
            String email = CR.getString(3);
            user = new User(id,name,email,phone);
        }while (CR.moveToNext());
        Log.d(TAG,user.getId());
        return user;
    }
    public void removeCurrentUser(String user_id){
        dp =  new DbOperations(context);
        dp.removeUser(dp,user_id);
    }

}
