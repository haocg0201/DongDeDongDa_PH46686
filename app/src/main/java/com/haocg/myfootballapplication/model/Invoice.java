package com.haocg.myfootballapplication.model;

import java.io.Serializable;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String invoiceId;
    private String userId;
    private String stadiumId;
    private String bookingId;
    private String name;
    private String phone;
    private String stadiumName;
    private String bookingTime;
    private String time;
    private int stadiumPrice;
    private int servicePrice;
    private int surcharge;
    private String note;
    private String status;
    private int mGuesst;
    private int sBack;
    private int total;

    public Invoice() {
    }
    public Invoice(String invoiceId, String userId, String stadiumId, String bookingId, String name, String phone, String time,
                   int stadiumPrice, int servicePrice, int surcharge, String note, String status, int mGuesst, int sBack, int total) {
        this.invoiceId = invoiceId;
        this.userId = userId;
        this.stadiumId = stadiumId;
        this.bookingId = bookingId;
        this.name = name;
        this.phone = phone;
        this.time = time;
        this.stadiumPrice = stadiumPrice;
        this.servicePrice = servicePrice;
        this.surcharge = surcharge;
        this.note = note;
        this.status = status;
        this.mGuesst = mGuesst;
        this.sBack = sBack;
        this.total = total;
    }

    public Invoice(String invoiceId, String userId, String stadiumId, String bookingId, String name, String phone, String stadiumName, String bookingTime, String time, int stadiumPrice, int servicePrice, int surcharge, String note, String status, int mGuesst, int sBack, int total) {
        this.invoiceId = invoiceId;
        this.userId = userId;
        this.stadiumId = stadiumId;
        this.bookingId = bookingId;
        this.name = name;
        this.phone = phone;
        this.stadiumName = stadiumName;
        this.bookingTime = bookingTime;
        this.time = time;
        this.stadiumPrice = stadiumPrice;
        this.servicePrice = servicePrice;
        this.surcharge = surcharge;
        this.note = note;
        this.status = status;
        this.mGuesst = mGuesst;
        this.sBack = sBack;
        this.total = total;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
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

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

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

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStadiumPrice() {
        return stadiumPrice;
    }

    public void setStadiumPrice(int stadiumPrice) {
        this.stadiumPrice = stadiumPrice;
    }

    public int getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(int servicePrice) {
        this.servicePrice = servicePrice;
    }

    public int getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(int surcharge) {
        this.surcharge = surcharge;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMGuesst() {
        return mGuesst;
    }

    public void setMGuesst(int mGuesst) {
        this.mGuesst = mGuesst;
    }

    public int getSBack() {
        return sBack;
    }

    public void setSBack(int sBack) {
        this.sBack = sBack;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
