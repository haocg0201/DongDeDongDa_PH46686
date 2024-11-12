package com.haocg.myfootballapplication.model;

import java.util.Map;

public class Stadium {
    private String stadium_id;
    private String name;
    private String location;
    private int price;
    private Map<String, Boolean> available_times;
    private Map<String, Boolean> bookings;

    public Stadium() {}

    public Stadium(String name, String location, int price) {
        this.name = name;
        this.location = location;
        this.price = price;
    }

    public Stadium(String stadium_id, String name, String location, int price, Map<String, Boolean> available_times, Map<String, Boolean> bookings) {
        this.stadium_id = stadium_id;
        this.name = name;
        this.location = location;
        this.price = price;
        this.available_times = available_times;
        this.bookings = bookings;
    }

    public Stadium(String name, String location, int price, Map<String, Boolean> available_times, Map<String, Boolean> bookings) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.available_times = available_times;
        this.bookings = bookings;
    }

    public String getStadium_id() {
        return stadium_id;
    }

    public void setStadium_id(String stadium_id) {
        this.stadium_id = stadium_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Map<String, Boolean> getAvailable_times() {
        return available_times;
    }

    public void setAvailable_times(Map<String, Boolean> available_times) {
        this.available_times = available_times;
    }

    public Map<String, Boolean> getBookings() {
        return bookings;
    }

    public void setBookings(Map<String, Boolean> bookings) {
        this.bookings = bookings;
    }
}

