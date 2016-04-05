package com.namlongsolutions.medicare.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Doctor implements ClusterItem , Parcelable{

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String license;
    private String spec;
    private String workdays;
    private String worktime;
    private String image;
    private double distance;
    private boolean active;
    private final LatLng mPosition;

    public Doctor(String id, String name, String email, String phone, String license, String spec, String address, boolean active, LatLng mPosition, double distance,String workdays,String worktime,String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.license = license;
        this.spec = spec;
        this.address = address;
        this.active = active;
        this.mPosition = mPosition;
        this.distance=distance;
        this.workdays=workdays;
        this.worktime=worktime;
        this.image =image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getWorkdays() {
        return workdays;
    }

    public void setWorkdays(String workdays) {
        this.workdays = workdays;
    }

    public String getWorktime() {
        return worktime;
    }

    public void setWorktime(String worktime) {
        this.worktime = worktime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(email);
        out.writeString(phone);
        out.writeString(address);
        out.writeString(license);
        out.writeString(spec);
        out.writeString(workdays);
        out.writeString(worktime);
        out.writeDouble(distance);
        out.writeString(image);
    }
    private Doctor(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        license = in.readString();
        spec = in.readString();
        workdays = in.readString();
        worktime = in.readString();
        distance = in.readDouble();
        image = in.readString();
        mPosition = null;
    }
    public static final Parcelable.Creator<Doctor> CREATOR
            = new Parcelable.Creator<Doctor>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}


