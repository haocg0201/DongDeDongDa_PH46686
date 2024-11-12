package com.haocg.myfootballapplication.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseUtil {
    private DatabaseReference databaseReference;

    public FirebaseDatabaseUtil(String node) {
        databaseReference = FirebaseDatabase.getInstance().getReference(node);
    }

    // Thêm dữ liệu mới
    public void addData(String key, Object data, OnDataAddedListener listener) {
        databaseReference.child(key).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    listener.onDataAdded(true);
                })
                .addOnFailureListener(e -> {
                    listener.onDataAdded(false);
                });
    }

    // Đọc dữ liệu
    public void getData(String key, OnDataRetrievedListener listener) {
        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onDataRetrieved(dataSnapshot.getValue());
                } else {
                    listener.onDataRetrieved(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.toException());
            }
        });
    }

    // Cập nhật dữ liệu
    public void updateData(String key, Map<String, Object> updates, OnDataUpdatedListener listener) {
        databaseReference.child(key).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    listener.onDataUpdated(true);
                })
                .addOnFailureListener(e -> {
                    listener.onDataUpdated(false);
                });
    }

    // Xóa dữ liệu
    public void deleteData(String key, OnDataDeletedListener listener) {
        databaseReference.child(key).removeValue()
                .addOnSuccessListener(aVoid -> {
                    listener.onDataDeleted(true);
                })
                .addOnFailureListener(e -> {
                    listener.onDataDeleted(false);
                });
    }

    // Interface cho các listener
    public interface OnDataAddedListener {
        void onDataAdded(boolean success);
    }

    public interface OnDataRetrievedListener {
        void onDataRetrieved(Object data);
        void onError(Exception e);
    }

    public interface OnDataUpdatedListener {
        void onDataUpdated(boolean success);
    }

    public interface OnDataDeletedListener {
        void onDataDeleted(boolean success);
    }
}

