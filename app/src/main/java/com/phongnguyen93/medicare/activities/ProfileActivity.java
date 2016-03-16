package com.phongnguyen93.medicare.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;


import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.functions.FunctionFavDoctor;

import com.phongnguyen93.medicare.functions.FunctionUser;
import com.phongnguyen93.medicare.model.Doctor;
import com.phongnguyen93.medicare.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ProfileActivity extends AppCompatActivity implements FloatingActionButton.OnClickListener {
    com.rengwuxian.materialedittext.MaterialEditText  address, phone, email, license, workdays, worktime;
    TextView name, spec, fav_text;
    ImageView imageView, fav_icon;
    private FloatingActionButton fab1;
    private Doctor doctor;
    private boolean IS_FAV =false;
    private LinearLayout fav_sector;
    private FunctionFavDoctor functionFavDoctor;
    private FunctionUser functionUser;
    private CoordinatorLayout coordinatorLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.profile_layout);
        functionUser = new FunctionUser(getApplicationContext());
        user = functionUser.getCurrentUser();
        functionFavDoctor = new FunctionFavDoctor(getApplicationContext());
        setupViewHolder();

    }

    private void setupViewHolder() {
        fav_sector = (LinearLayout)findViewById(R.id.fav_sector);
        name = (TextView) findViewById(R.id.txt_name);
        address = (MaterialEditText) findViewById(R.id.txt_address);
        email = (MaterialEditText) findViewById(R.id.txt_email);
        spec = (TextView) findViewById(R.id.txt_spec);
        license = (MaterialEditText) findViewById(R.id.txt_license);
        phone = (MaterialEditText) findViewById(R.id.txt_phone);
        workdays = (MaterialEditText) findViewById(R.id.txt_workdays);
        worktime = (MaterialEditText) findViewById(R.id.txt_worktime);
        imageView = (ImageView)findViewById(R.id.profile_image);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);

        fav_icon = (ImageView)findViewById(R.id.fav_icon);
        fav_text = (TextView) findViewById(R.id.fav_text);

        doctor =getIntent().getParcelableExtra("doctor");
        name.setText(doctor.getName());
        address.setText(doctor.getAddress());
        email.setText(doctor.getEmail());
        spec.setText(doctor.getSpec());
        license.setText(doctor.getLicense());
        phone.setText(doctor.getPhone());
        workdays.setText(doctor.getWorkdays());
        worktime.setText(doctor.getWorktime());
        imageView.setImageResource(R.drawable.img_5);
        checkIsFav(doctor.getId());

        fab1.setOnClickListener(this);
        fav_sector.setOnClickListener(this);
    }

    //check if this doctor is faved or not
    private void checkIsFav(String id) {
        if(functionFavDoctor.isFav(id)){
            IS_FAV = true;
            fav_icon.setImageResource(R.drawable.ic_favorite_primary_24dp);
            fav_text.setText(getResources().getString(R.string.unfav_text));
        }else
            IS_FAV = false;
    }

    //display fav or unfav notification
    private void displaySnackbar() {
        String displayText;
        if(IS_FAV){
            displayText = getResources().getString(R.string.fav_noti);
        }else
            displayText =getResources().getString(R.string.unfav_noti);
        Snackbar snackbar = Snackbar.make(coordinatorLayout,displayText, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        sbView.setBackgroundColor(Color.BLACK);
        snackbar.show();
    }

    //change fav or unfav state
    private void favSelector(){
        if(IS_FAV){
            if(functionFavDoctor.removeFavDoctor(doctor.getId(),user.getId())){
                Log.d("favour ",IS_FAV+"");
                IS_FAV = false;
                fav_icon.setImageResource(R.drawable.ic_favorite_border_primary_24dp);
                fav_text.setText(getResources().getString(R.string.fav_text));
                displaySnackbar();
            }
        }else
        if (!IS_FAV){
            if(functionFavDoctor.addFavDoctor(doctor,user.getId())){
                IS_FAV = true;
                Log.d("favour ",IS_FAV+"");
                fav_icon.setImageResource(R.drawable.ic_favorite_primary_24dp);
                fav_text.setText(getResources().getString(R.string.unfav_text));
                displaySnackbar();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab1:
                Intent t = new Intent(ProfileActivity.this, BookingActivity.class);
                t.putExtra("doctor", getIntent().getParcelableExtra("doctor"));
                startActivity(t);
                break;
            case R.id.fav_sector:
                Log.d("fav icon:","clicked");
                favSelector();
                break;

        }
    }
}
