package com.haocg.myfootballapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class User implements Parcelable {
    private String uid;
    private String name;
    private String role = "user"; // Mặc định là user
    private String email;
    private String phone;
    private String password;
    private HashMap<String, Boolean> bookings; // Nếu có booking thì có thể thêm sau
    private String shiftId;

    public User() {
    }

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(String uid, String name, String email, String phone, String password, String role, HashMap<String, Boolean> bookings) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.bookings = bookings;
    }

    public User(String uid, String name, String role, String email, String phone, String password, String shiftId) {
        this.uid = uid;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.shiftId = shiftId;
    }

    public User(String uid, String name, String role, String email, String phone, String password) {
        this.uid = uid;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(String name, String role, String email, String phone, String password) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public HashMap<String, Boolean> getBookings() {
        return bookings;
    }

    public void setBookings(HashMap<String, Boolean> bookings) {
        this.bookings = bookings;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(role);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeSerializable(bookings); // Ghi HashMap vào Parcel
        dest.writeString(shiftId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Tạo đối tượng từ Parcel huhu
    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        role = in.readString();
        email = in.readString();
        phone = in.readString();
        password = in.readString();
        bookings = (HashMap<String, Boolean>) in.readSerializable(); // Đọc HashMap từ Parcel
        shiftId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
