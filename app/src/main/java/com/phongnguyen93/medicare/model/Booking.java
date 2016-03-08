package com.phongnguyen93.medicare.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Phong Nguyen on 1/4/2016.
 */
public class Booking implements Parcelable {
    private int id;
    private String dr_name;
    private String date;
    private String time;
    private String phone;
    private String email;
    private boolean checked;
    private int rebook_days;

    public Booking(int id,String dr_name,String date,String time,String phone,String email,boolean checked, int rebook_days){
        this.id = id;
        this.dr_name= dr_name;
        this.setDate(date);
        this.setTime(time);
        this.phone = phone;
        this.email = email;
        this.checked = checked;
        this.rebook_days = rebook_days;
    }

    protected Booking(Parcel in) {
        id = in.readInt();
        dr_name = in.readString();
        phone = in.readString();
        email = in.readString();
        rebook_days = in.readInt();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(dr_name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeInt(rebook_days);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDr_name() {
        return dr_name;
    }

    public void setDr_name(String dr_name) {
        this.dr_name = dr_name;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getRebook_days() {
        return rebook_days;
    }

    public void setRebook_days(int rebook_days) {
        this.rebook_days = rebook_days;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
