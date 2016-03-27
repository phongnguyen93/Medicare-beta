package com.phongnguyen93.medicare.functions;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.thread.network_thread.JSONArrayRequest;
import com.phongnguyen93.medicare.extras.JSONParse;
import com.phongnguyen93.medicare.model.Booking;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 21-Mar-16.
 * Implement Bookings interface for handle bookings functions
 */
public class BookingsFunctions implements Bookings, JSONArrayRequest.AsyncResponse {
    private DbOperations dop;

    private Context context;

    private JSONArrayRequest jsonArrayRequest;
    public static final String BASE_URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/service/getallbookingbyuser?user=";

    public BookingsFunctions(Context context) {
        this.context = context;
        dop = new DbOperations(context);
        jsonArrayRequest = new JSONArrayRequest(this);
    }

    private boolean checkLocalDb() {
        Cursor cursor = dop.getAllBookings(dop);
        return cursor.getCount() != 0;
    }

    @Override
    public void setupBookings(String userId) {
        if (!checkLocalDb())
            requestBookings(userId);
    }

    private void requestBookings(String userId) {
        String query = BASE_URL + userId;
        jsonArrayRequest.execute(query);
    }

    @Override
    public ArrayList<Booking> getAllBookings() {
        Cursor cursor = dop.getAllBookings(dop);
        if (checkLocalDb())
            return getBookingFromLocal(cursor);
        else
            return null;
    }

    @Override
    public ArrayList<Booking> getBookingsByDate(String lookUpDate) {
        Cursor cursor = dop.getBookingByDate(dop, lookUpDate);
        ArrayList<Booking> bookings = new ArrayList<>();
        if(cursor != null){
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(0);
                String doctor = cursor.getString(1);
                String email = cursor.getString(2);
                String phone = cursor.getString(3);
                String address = cursor.getString(6);
                String date = cursor.getString(4);
                String time = cursor.getString(5);
                boolean is_checked;
                if (cursor.getInt(7) == 0) {
                    is_checked = false;
                } else
                    is_checked = true;
                int rebook_days = cursor.getInt(8);
                bookings.add(new Booking(id, doctor, date, time, phone, email, is_checked, rebook_days, address));
            } while (cursor.moveToNext());
            return bookings;
        }
       return null;
    }

    private ArrayList<Booking> getBookingFromLocal(Cursor cursor) {
        ArrayList<Booking> bookings = new ArrayList<>();
        cursor.moveToFirst();
        do {
            int id = cursor.getInt(0);
            String doctor = cursor.getString(1);
            String email = cursor.getString(2);
            String phone = cursor.getString(3);
            String address = cursor.getString(6);
            String date = cursor.getString(4);
            String time = cursor.getString(5);
            boolean is_checked;
            if (cursor.getInt(7) == 0) {
                is_checked = false;
            } else
                is_checked = true;
            int rebook_days = cursor.getInt(8);
            bookings.add(new Booking(id, doctor, date, time, phone, email, is_checked, rebook_days, address));
        } while (cursor.moveToNext());

        return bookings;
    }

    // Not use this method now
    @Override
    public boolean removeBooking(int bookingId) {
        return false;
    }

    @Override
    public Booking getBooking(int bookingId) {
        Cursor cursor = dop.getBooking(dop,bookingId);
        cursor.moveToFirst();
        Booking booking;
        do{
            int id = cursor.getInt(0);
            String doctor = cursor.getString(1);
            String email = cursor.getString(2);
            String phone = cursor.getString(3);
            String address = cursor.getString(6);
            String date = cursor.getString(4);
            String time = cursor.getString(5);
            boolean is_checked = false;
            if (cursor.getInt(7) == 0) {
                is_checked = false;
            } else
                is_checked = true;
            int rebook_days = cursor.getInt(8);
            booking = new Booking(id,doctor,date,time,phone,email,is_checked,rebook_days,address);
        }while (cursor.moveToNext());
        return booking;
    }

    @Override
    public boolean removeAllBookings() {
        return dop.removeAllBooking(dop);
    }

    @Override
    public boolean insertBookingToLocal(Booking booking) {
        int is_checked=0;
        int id = booking.getId();
        String doctor = booking.getDr_name();
        String date = booking.getDate();
        String time = booking.getTime();
        String phone = booking.getPhone();
        String email = booking.getEmail();
        if(booking.isChecked())
            is_checked = 1;
        int rebook_days = booking.getRebook_days();
        String address = booking.getAddress();
        if(dop.insertBooking(dop,id,doctor,email,phone,date,time,address,is_checked,rebook_days))
            return true;
        else
            return false;
    }

    @Override
    public boolean updateBookingStatus(int bookingId, boolean status) {
        return dop.updateBookingStatus(dop,bookingId,status);
    }

    @Override
    public void processFinish(JSONArray jsonArray) {
        int result = 0;
        int is_checked = 0;
        ArrayList<Booking> bookings = JSONParse.bookingList(jsonArray,context);
        if(bookings!=null){
            for( int i = 0; i<bookings.size();i++){
                Booking booking = bookings.get(i);
                int id = booking.getId();
                String doctor = booking.getDr_name();
                String date = booking.getDate();
                String time = booking.getTime();
                String phone = booking.getPhone();
                String email = booking.getEmail();
                if(booking.isChecked())
                    is_checked = 1;
                int rebook_days = booking.getRebook_days();
                String address = booking.getAddress();
                if(dop.insertBooking(dop,id,doctor,email,phone,date,time,address,is_checked,rebook_days))
                    result++;
            }
            if(result> 0 && result==bookings.size())
                Log.d("setup booking data","success");
        }
    }

    @Override
    public boolean updateBookingDate(int id, String time) {
        return dop.updateBookingDate(dop, id, time);
    }
}
