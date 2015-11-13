package com.phongnguyen93.medicare.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.phongnguyen93.medicare.database.DbOperations;

/**
 * Created by Phong Nguyen on 11/4/2015.
 */
public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getCurrentSession()){
            Intent t = new Intent(this, MainActivity.class);
            t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(t);
            Log.d("medicare", "Already have token");
        }else if(!getCurrentSession()) {
            Intent t = new Intent(this,WelcomeActivity.class);
            t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(t);
            Log.d("medicare","No token available");
        }
    }

    private boolean getCurrentSession(){
        DbOperations dop = new DbOperations(getBaseContext());
        Cursor CR = dop.getToken(dop);
        boolean loginstatus = false;
        Log.d("medicare",CR.getCount()+"");
        if(CR.getCount() != 0){
            loginstatus = true;
        }
        return loginstatus;
    }
}
