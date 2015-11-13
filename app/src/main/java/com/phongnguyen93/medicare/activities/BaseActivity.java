package com.phongnguyen93.medicare.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.Window;

/**
 * Created by Phong Nguyen on 10/23/2015.
 */
public  abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());
    }

}
