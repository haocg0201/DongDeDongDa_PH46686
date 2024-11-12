package com.haocg.myfootballapplication.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Booking implements Parcelable {
    private String bookingId;
    private String userId;
    private String stadiumId;
    private String time; // Thời gian định dạng "08:00-10:00"
    private String date; // Ngày đặt sân định dạng "2024-09-25"
    private String status; // Trạng thái (pending, confirmed, etc.)
    private String paymentId;
    private Map<String, Boolean> services;

    public Booking() {
    }

    public Booking(String userId, String stadiumId, String time, String date, String status, String paymentId, Map<String, Boolean> services) {
        this.userId = userId;
        this.stadiumId = stadiumId;
        this.time = time;
        this.date = date;
        this.status = status;
        this.paymentId = paymentId;
        this.services = services;
    }

    public Booking(String bookingId, String userId, String stadiumId, String time, String date, String status, String paymentId, Map<String, Boolean> services) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.stadiumId = stadiumId;
        this.time = time;
        this.date = date;
        this.status = status;
        this.paymentId = paymentId;
        this.services = services;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Map<String, Boolean> getServices() {
        return services;
    }

    public void setServices(Map<String, Boolean> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // Implementing Parcelable
    protected Booking(Parcel in) {
        bookingId = in.readString();
        userId = in.readString();
        stadiumId = in.readString();
        time = in.readString();
        date = in.readString();
        status = in.readString();
        paymentId = in.readString();

        // Convert services from Bundle to Map<String, Boolean>
        services = new HashMap<>();
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                services.put(key, bundle.getBoolean(key));
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookingId);
        dest.writeString(userId);
        dest.writeString(stadiumId);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(status);
        dest.writeString(paymentId);

        // Convert services Map to Bundle
        Bundle bundle = new Bundle();
        if (services != null) {
            for (Map.Entry<String, Boolean> entry : services.entrySet()) {
                bundle.putBoolean(entry.getKey(), entry.getValue());
            }
        }
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
