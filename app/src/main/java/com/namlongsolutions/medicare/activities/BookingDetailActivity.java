package com.namlongsolutions.medicare.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.namlongsolutions.medicare.R;
import com.namlongsolutions.medicare.functions.BookingsFunctions;
import com.namlongsolutions.medicare.model.Booking;
import com.rengwuxian.materialedittext.MaterialEditText;

public class BookingDetailActivity extends BaseActivity {

    private static final String EXTRA_DATA = "booking_id";
    MaterialEditText edit_date, edit_time, edit_drName, edit_phone, edit_email,edit_address;
    ImageView status_icon;
    TextView status_text;
    private Booking booking;
    private BookingsFunctions bookingsFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        bookingsFunctions = new BookingsFunctions(getBaseContext());
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViews();

    }
    private void setupViews() {
        edit_drName = (MaterialEditText)this.findViewById(R.id.edit_dr_name);
        edit_phone = (MaterialEditText)this.findViewById(R.id.edit_phone);
        edit_email = (MaterialEditText)this.findViewById(R.id.edit_email);
        edit_date = (MaterialEditText)this.findViewById(R.id.edit_date);
        edit_time = (MaterialEditText)this.findViewById(R.id.edit_time);
        edit_address = (MaterialEditText)this.findViewById(R.id.edit_address);
        status_icon = (ImageView) findViewById(R.id.status_icon);
        status_text = (TextView) findViewById(R.id.status_text);

        edit_drName.setFocusable(false);
        edit_phone.setFocusable(false);
        edit_email.setFocusable(false);
        edit_date.setFocusable(false);
        edit_time.setFocusable(false);
        edit_address.setFocusable(false);

        String booking_id = getIntent().getStringExtra(EXTRA_DATA);
        if(booking_id != null){
            booking = bookingsFunctions.getBooking(Integer.parseInt(booking_id));
            edit_drName.setText(booking.getDr_name());
            edit_phone.setText(booking.getPhone());
            edit_email.setText(booking.getEmail());
            edit_date.setText(booking.getDate());
            edit_time.setText(booking.getTime());
            edit_address.setText(booking.getAddress());

            if(booking.isChecked()){
                status_text.setTextColor(getResources().getColor(R.color.green));
                status_icon.setImageResource(R.drawable.ic_event_available_green_500_18dp);
                status_text.setText(R.string.status_checked_text);
            }else{
                status_icon.setImageResource(R.drawable.ic_event_busy_red_500_18dp);
                status_text.setText(R.string.status_unchecked_text);
            }

        }



    }

}
