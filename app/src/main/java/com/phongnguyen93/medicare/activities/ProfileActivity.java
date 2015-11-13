package com.phongnguyen93.medicare.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.extras.FloatingActionButton;
import com.phongnguyen93.medicare.pojo.Doctor;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ProfileActivity extends AppCompatActivity implements FloatingActionButton.OnClickListener {
    com.rengwuxian.materialedittext.MaterialEditText  address, phone, email, license, workdays, worktime;
    TextView name, spec;
    ImageView imageView;
    private FloatingActionButton fab1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        viewHolder();
        Doctor doctor =getIntent().getParcelableExtra("doctor");
        name.setText(doctor.getName());
        address.setText(doctor.getAddress());
        email.setText(doctor.getEmail());
        spec.setText(doctor.getSpec());
        license.setText(doctor.getLicense());
        phone.setText(doctor.getPhone());
        workdays.setText(doctor.getWorkdays());
        worktime.setText(doctor.getWorktime());
        imageView.setImageResource(R.drawable.img_5);
        fab1.setOnClickListener(this);
    }

    private void viewHolder() {
        name = (TextView) findViewById(R.id.txt_name);
        address = (MaterialEditText) findViewById(R.id.txt_address);
        email = (MaterialEditText) findViewById(R.id.txt_email);
        spec = (TextView) findViewById(R.id.txt_spec);
        license = (MaterialEditText) findViewById(R.id.txt_license);
        phone = (MaterialEditText) findViewById(R.id.txt_phone);
        workdays = (MaterialEditText) findViewById(R.id.txt_workdays);
        worktime = (MaterialEditText) findViewById(R.id.txt_worktime);
        imageView = (ImageView)findViewById(R.id.profile_image);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
    }


    @Override
    public void onClick(View v) {
        
    }
}
