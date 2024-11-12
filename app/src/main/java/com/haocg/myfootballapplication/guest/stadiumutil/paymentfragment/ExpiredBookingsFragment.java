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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpiredBookingsFragment extends Fragment {
    private RecyclerView recyclerView;
    public BookingAdapter expiredAdapter;
    private List<Booking> bookingList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_bookings, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải dữ liệu booking quá hạn
        loadBookings();

        expiredAdapter = new BookingAdapter(bookingList,requireContext());
        recyclerView.setAdapter(expiredAdapter);
        return view;
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    private void loadBookings() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bookings");

        databaseReference.orderByChild("userId").equalTo(getUserIdFromPreferences()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        if (booking.getStatus().equals("pending") && isPastDate(booking.getDate())) {
                            bookingList.add(booking);
                        }
                    }
                }

                expiredAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

    private boolean isPastDate(String bookingDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date bookingDateObj = dateFormat.parse(bookingDate);

            // Lấy ngày hiện tại và đặt thời gian là 00:00:00
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
            currentCalendar.set(Calendar.MINUTE, 0);
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);
            Date currentDate = currentCalendar.getTime();

            // So sánh ngày bookingDate với currentDate (đã bỏ thời gian trong ngày)
            return bookingDateObj != null && bookingDateObj.before(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


//    private boolean isPastDate(String bookingDate) {
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            Date bookingDateObj = dateFormat.parse(bookingDate);
//            Date currentDate = new Date();
//            return bookingDateObj != null && bookingDateObj.before(currentDate);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
