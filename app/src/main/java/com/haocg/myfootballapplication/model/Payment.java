package com.haocg.myfootballapplication.model;

public class Payment {
    private String paymentId;
    private String bookingId;
    private double amount;
    private String method;
    private String status;

    public Payment() {}

    public Payment(String bookingId, double amount, String method, String status) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public Payment(String paymentId, String bookingId, double amount, String method, String status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

