package com.example.admin.myapplication.model;

import java.io.Serializable;

public class ListOffice implements Serializable {
    public String longtitude;
    public String latitude;
    public String address;
    public String name;
    public String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ListOffice(String longtitude, String latitude, String address, String name, String phone) {
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.address = address;
        this.name = name;
        this.phone = phone;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
