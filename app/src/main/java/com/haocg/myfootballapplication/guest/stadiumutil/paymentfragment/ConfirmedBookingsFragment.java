package com.haocg.myfootballapplication.guest.stadiumutil.paymentfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.BookingAdapter;
import com.haocg.myfootballapplication.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedBookingsFragment extends Fragment {
    private RecyclerView recyclerView;
    public BookingAdapter confirmedAdapter;
    private List<Booking> bookingList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_bookings, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải dữ liệu booking có `status` là "confirmed"
        loadBookings();

        confirmedAdapter = new BookingAdapter(bookingList,requireContext());
        recyclerView.setAdapter(confirmedAdapter);
        return view;
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    private void loadBookings() {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        bookingsRef.orderByChild("userId").equalTo(getUserIdFromPreferences()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                        String status = bookingSnapshot.child("status").getValue(String.class);
                        if ("confirmed".equals(status) || "unpaid".equals(status)) {
                            bookingList.add(bookingSnapshot.getValue(Booking.class));
                        }
                    }
                }
                confirmedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }
}
