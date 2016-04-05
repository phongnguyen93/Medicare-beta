package com.namlongsolutions.medicare.model;

/**
 * Created by Phong Nguyen on 19-Mar-16.
 * Object to store notification data
 */
public class NotificationContent {
    // Constant text keys to get values
    public static final String NOTIFICATION_DOCTOR = "doctor";
    public static final String NOTIFICATION_DATE = "date";
    public static final String NOTIFICATION_TIME = "time";
    public static final String NOTIFICATION_ADDRESS = "address";
    public static final String NOTIFICATION_TYPE = "type";
    public static final String NOTIFICATION_ID = "booking_id";

    private String doctor;
    private String date;
    private String time;
    private String address;
    private String type;
    private String id;

    public NotificationContent(String id, String type, String doctor, String date, String time, String address){
        this.setId(id);
        this.type = type;
        this.doctor = doctor;
        this.address = address;
        this.time = time;
        this.date = date;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
