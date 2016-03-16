package com.phongnguyen93.medicare.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.functions.FunctionUser;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.json.JSONObjectRequest;
import com.phongnguyen93.medicare.model.Doctor;
import com.phongnguyen93.medicare.model.User;
import com.phongnguyen93.medicare.ui_view.calendarpicker.CalendarPickerView;
import com.phongnguyen93.medicare.ui_view.dateslider.DateSlider;
import com.phongnguyen93.medicare.ui_view.dateslider.TimeSlider;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends BaseActivity implements View.OnTouchListener,JSONObjectRequest.AsyncResponse, View.OnClickListener {
    private static final int DATE_SLIDER_DIALOG_ID = 1;
    private static final int TIME_SLIDER_DIALOG_ID = 2;
    final Calendar myCalendar = Calendar.getInstance();
    MaterialEditText edit_date, edit_time, edit_drName, edit_phone, edit_email;
    private Doctor doctor;
    private User user;
    private FunctionUser functionUser;
    private String inserDate;
    private String inserTime;
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;
    private DateSlider.OnDateSetListener mTimeSetListener =
            new DateSlider.OnDateSetListener() {
                public void onDateSet(DateSlider view, Calendar selectedDate) {
                    // update the dateText view with the corresponding date
                    updateLabel(selectedDate.getTime(), "time");
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        functionUser = new FunctionUser(getApplicationContext());
        doctor = getIntent().getParcelableExtra("doctor");
        user = functionUser.getCurrentUser();
        viewHolder();
    }

    private void viewHolder() {
        edit_drName = (MaterialEditText)findViewById(R.id.edit_dr_name);
        edit_phone = (MaterialEditText)findViewById(R.id.edit_phone);
        edit_email = (MaterialEditText)findViewById(R.id.edit_email);
        edit_date = (MaterialEditText)findViewById(R.id.edit_date);
        edit_time = (MaterialEditText)findViewById(R.id.edit_time);
        doctor = getIntent().getParcelableExtra("doctor");
        edit_drName.setText(doctor.getName());
        edit_drName.setClickable(false);
        edit_time.setOnTouchListener(this);
        edit_date.setOnTouchListener(this);

    }

    private void updateLabel(Date selected,String type) {
    switch (type){
        case "date":
            String myDateFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat dateFormat = new SimpleDateFormat(myDateFormat, Locale.US);
            edit_date.setText(dateFormat.format(selected));
            String mdateFormat = "yyyy-MM-dd";
            SimpleDateFormat insertDateFormat= new SimpleDateFormat(mdateFormat,Locale.US);
            inserDate = insertDateFormat.format(selected);
            break;
        case "time":
            String myTimeFormat = "HH:mm"; //In which you need put here
            SimpleDateFormat timeFormat = new SimpleDateFormat(myTimeFormat, Locale.US);
            edit_time.setText(timeFormat.format(selected));
            String mtimeFormat = "HH:mm:ss";
            SimpleDateFormat insertTimeFormat= new SimpleDateFormat(mtimeFormat,Locale.US);
            inserTime = insertTimeFormat.format(selected);
    }

    }

    public void setupActionBar(){
        assert getSupportActionBar() != null;
        final View customActionBarView;
        customActionBarView = View.inflate(getApplicationContext(),R.layout.actionbar_custom_view_done_cancel,null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String dr_id = doctor.getId();
                        String user_id = user.getId();
                        String time = inserTime;
                        String date = inserDate;
                        String phone = edit_phone.getText().toString();
                        String email = edit_email.getText().toString();
                        addBooking(user_id,dr_id,date,time,email,phone);
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        // END_INCLUDE (inflate_set_custom_view)

        setContentView(R.layout.activity_booking);
    }

    private void addBooking(String user_id, String dr_id, String date, String time, String email,String phone) {
        JSONObjectRequest jsonObjectRequest = new JSONObjectRequest(this);
        String URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/booking/add?" +
                "user="+user_id+
                "&doctor="+dr_id+
                "&date="+date+
                "&time="+time+
                "&phone="+phone+
                "&email="+email+"";
        jsonObjectRequest.execute(URL);
    }

    @Override
    public void processFinish(JSONObject jsonObject) {
        if(jsonObject!= null)
        {
            try {
                validateBooking(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void validateBooking(JSONObject jsonObject) throws JSONException{
        boolean status = jsonObject.getBoolean("status");
        if(status){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.booking_success),Toast.LENGTH_LONG).show();
            finish();
        }else{
            String error_msg = jsonObject.getString("error_msg");
            Toast.makeText(getApplicationContext(),error_msg+getResources().getString(R.string.booking_fail),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            switch(v.getId()){
                case R.id.edit_date:
                    final Calendar nextYear = Calendar.getInstance();
                    nextYear.add(Calendar.YEAR,+1);

                    final Calendar lastYear = Calendar.getInstance();
                    lastYear.add(Calendar.MONTH, 0);
                    showCalendarInDialog(doctor.getWorkdays(),R.layout.dialog_calendarpicker);
                    dialogView.init(lastYear.getTime(), nextYear.getTime())
                            .withSelectedDate(new Date());
                    break;
                case R.id.edit_time:
                    showDialog(TIME_SLIDER_DIALOG_ID);
                    break;
            }
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // this method is called after invoking 'showDialog' for the first time
        // here we initiate the corresponding DateSlideSelector and return the dialog to its caller
        final Calendar c = Calendar.getInstance();
        switch (id) {
            case DATE_SLIDER_DIALOG_ID:
              //  final Calendar maxTime = Calendar.getInstance();
             //   maxTime.add(Calendar.DAY_OF_MONTH, 14);
              //  return new DefaultDateSlider(this,mDateSetListener,c,c,maxTime);
            case TIME_SLIDER_DIALOG_ID:
                ArrayList<String> mWorktime = getWorktime(doctor.getWorktime());
                int min =  Integer.parseInt(mWorktime.get(0));
                int max =  Integer.parseInt(mWorktime.get(1));
                final Calendar minTime = Calendar.getInstance();
                final Calendar maxTime = Calendar.getInstance();
                int currentTime = minTime.get(Calendar.HOUR_OF_DAY);
                if(currentTime>min && currentTime< max) min=currentTime;
                minTime.set(Calendar.HOUR_OF_DAY,min);
                minTime.set(Calendar.MINUTE,0);
                maxTime.set(Calendar.HOUR_OF_DAY, max);
                maxTime.set(Calendar.MINUTE,-10);
                Log.d("min-max time",min+","+max);
                String Worktime = getResources().getString(R.string.edittext_dr_worktime)+": "+doctor.getWorktime();
                return new  TimeSlider(this,
                        mTimeSetListener,c,
                        minTime,maxTime,10,
                        Utils.convertWorktime(Worktime,getApplicationContext()));

            default:
                throw new UnsupportedOperationException("Unknown id"+id);
        }
    }

    private ArrayList<String> getWorktime(String worktime){
        ArrayList<String> mWorktime = new ArrayList<>();
        String[] split = worktime.split(",");
        for( String strs : split){
            mWorktime.add(strs);
        }
        return mWorktime;
    }

    private void showCalendarInDialog(String title, int layoutResId) {
        final View dialogLayout;
        dialogLayout = View.inflate(this,layoutResId,null);
        TextView okButton = (TextView) dialogLayout.findViewById(R.id.dateSliderOkButton);
        TextView workdaysText = (TextView) dialogLayout.findViewById(R.id.mWorkdaysText);
        workdaysText.setText(getResources().getString(R.string.edittext_dr_workdays)+": "+doctor.getWorkdays());
        okButton.setOnClickListener(this);
        dialogView = (CalendarPickerView) dialogLayout.findViewById(R.id.calendar_view);
        theDialog = new AlertDialog.Builder(this) //
                .setView(dialogLayout)
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                Log.d("calendar_picker", "onShow: fix the dimens!");
                dialogView.fixDialogDimens();
            }
        });
        theDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dateSliderCancelButton:
                theDialog.dismiss();
            case R.id.dateSliderOkButton:
                updateLabel(dialogView.getSelectedDate(),"date");
                theDialog.dismiss();
        }
    }
}