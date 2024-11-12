package com.haocg.myfootballapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable {
    private String sId;
    private String name;
    private int price;
    private String description;
    private boolean available;

    public Service() {
    }

    public Service(String name, int price, String description, boolean available) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    public Service(String sId, String name, int price, String description, boolean available) {
        this.sId = sId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", available=" + available +
                '}';
    }

    // Implementing Parcelable yaheee
    protected Service(Parcel in) {
        sId = in.readString();
        name = in.readString();
        price = in.readInt();
        description = in.readString();
        available = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sId);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(description);
        dest.writeByte((byte) (available ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };
}
