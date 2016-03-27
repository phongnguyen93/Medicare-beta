package com.phongnguyen93.medicare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;


import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.functions.FavDoctorFunctions;

import com.phongnguyen93.medicare.functions.UserFunctions;
import com.phongnguyen93.medicare.model.Doctor;
import com.phongnguyen93.medicare.model.User;
import com.phongnguyen93.medicare.notification.MyNotification;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements FloatingActionButton.OnClickListener {
    private static final int NORMAL_SNACKBAR = 3 ;
    com.rengwuxian.materialedittext.MaterialEditText  address, phone, email, license, workdays, worktime;
    TextView name, spec, fav_text;
    ImageView imageView, fav_icon;
    private Doctor doctor;
    private boolean IS_FAV =false;
    private FavDoctorFunctions favDoctorFunctions;
    private MyNotification notification;
    private CoordinatorLayout coordinatorLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.profile_layout);
        // setup needed service
        UserFunctions userFunctions = new UserFunctions(getApplicationContext());
        user = userFunctions.getCurrentUser();
        favDoctorFunctions = new FavDoctorFunctions(getApplicationContext());
        notification = new MyNotification();
        // setup views for this activity
        setupViews();
    }

    private void setupViews() {
        LinearLayout fav_sector = (LinearLayout) findViewById(R.id.fav_sector);
        name = (TextView) findViewById(R.id.txt_name);
        address = (MaterialEditText) findViewById(R.id.txt_address);
        email = (MaterialEditText) findViewById(R.id.txt_email);
        spec = (TextView) findViewById(R.id.txt_spec);
        license = (MaterialEditText) findViewById(R.id.txt_license);
        phone = (MaterialEditText) findViewById(R.id.txt_phone);
        workdays = (MaterialEditText) findViewById(R.id.txt_workdays);
        worktime = (MaterialEditText) findViewById(R.id.txt_worktime);
        imageView = (ImageView)findViewById(R.id.profile_image);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);

        fav_icon = (ImageView)findViewById(R.id.fav_icon);
        fav_text = (TextView) findViewById(R.id.fav_text);

        doctor =getIntent().getParcelableExtra("doctor");

        Picasso.with(getBaseContext())
                .load(doctor.getImage())
                .placeholder(R.drawable.applogo)
                .error(R.drawable.applogo)
                .into(imageView);

        name.setText(doctor.getName());
        address.setText(doctor.getAddress());
        email.setText(doctor.getEmail());
        spec.setText(doctor.getSpec());
        license.setText(doctor.getLicense());
        phone.setText(doctor.getPhone());
        workdays.setText(doctor.getWorkdays());
        worktime.setText(doctor.getWorktime());
        checkIsFav(doctor.getId());

        fab1.setOnClickListener(this);
        fav_sector.setOnClickListener(this);
    }

    //check if this doctor is faved or not
    private void checkIsFav(String id) {
        if(favDoctorFunctions.isFav(id)){
            IS_FAV = true;
            fav_icon.setImageResource(R.drawable.ic_favorite_primary_24dp);
            fav_text.setText(getResources().getString(R.string.unfav_text));
        }else
            IS_FAV = false;
    }

    //change fav or unfav state
    private void favStateSelector(){
        String displayText;
        if(IS_FAV){
            if(favDoctorFunctions.removeFavDoctor(doctor.getId(),user.getId())){
                IS_FAV = false;
                displayText = getResources().getString(R.string.unfav_noti);
                fav_icon.setImageResource(R.drawable.ic_favorite_border_primary_24dp);
                fav_text.setText(getResources().getString(R.string.fav_text));
                notification.displaySnackbar(NORMAL_SNACKBAR, coordinatorLayout, displayText,null);
            }
        }else {
            if(favDoctorFunctions.addFavDoctor(doctor,user.getId())){
                IS_FAV = true;
                displayText =getResources().getString(R.string.fav_noti);
                fav_icon.setImageResource(R.drawable.ic_favorite_primary_24dp);
                fav_text.setText(getResources().getString(R.string.unfav_text));
                notification.displaySnackbar(NORMAL_SNACKBAR,coordinatorLayout, displayText,null);
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
                favStateSelector();
                break;

        }
    }
}
