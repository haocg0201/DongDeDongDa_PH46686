package com.haocg.myfootballapplication.guest.stadiumutil;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.CalenderAdapter;
import com.haocg.myfootballapplication.model.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalenderMainActivity extends AppCompatActivity {
    TextView txtStadiumName, txtMonthInfo;
    List<Booking> bookingList = new ArrayList<>(); // Danh sách booking by userId
    RecyclerView recyclerView;
    List<Booking> bookings; // Danh sách booking
    private String stadiumId;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_boooking_stadium_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainBSM), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtStadiumName = findViewById(R.id.txtStadiumName);
        txtMonthInfo = findViewById(R.id.txtMonthInfo);
        stadiumId = getIntent().getStringExtra("stadiumId");
        String stadiumName = getIntent().getStringExtra("stadiumName");
        if (stadiumId != null && stadiumName != null) {
            txtStadiumName.setText(stadiumName);
        }

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        txtMonthInfo.setText("Tháng " + month);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = findViewById(R.id.booking_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Đặt lịch");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        recyclerView = findViewById(R.id.rcvMonthBookingStadium);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDataToView();

    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

//    private void findAllBookings(){
//        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
//
//        bookingsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
//                    Booking booking = bookingSnapshot.getValue(Booking.class);
//                    String bookingId = bookingSnapshot.getKey();
//
//                    if (booking != null) {
//                        booking.setBookingId(bookingId);
//                        bookings.add(booking);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý lỗi nếu có
//                Log.e("Firebase", "Error: " + databaseError.getMessage());
//            }
//        });
//
//    }

    public void loadDataToView(){
        List<String> daysInMonth = getDaysInMonth();
        CalenderAdapter adapter = new CalenderAdapter(daysInMonth, CalenderMainActivity.this,stadiumId);
        recyclerView.setAdapter(adapter);

        // Cuộn đến ngày hiện tại
        int currentDayPosition = daysInMonth.indexOf(getCurrentDateString());
        if (currentDayPosition != -1) {
            recyclerView.scrollToPosition(currentDayPosition);
        }
    }

    private void findBookingByUserId(String userId) {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        bookingsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        if (booking != null && booking.getUserId().equals(userId)) {
                            String bookingId = bookingSnapshot.getKey();
                            booking.setBookingId(bookingId);
                            bookingList.add(booking);
                        }
                    }
                } else {
                    Log.d("Booking", "Không tìm thấy booking cho userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Booking", "Lỗi khi tìm booking: " + databaseError.getMessage());
            }
        });
    }


    public List<String> getDaysInMonth() {
        List<String> daysList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= maxDay; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            daysList.add(String.valueOf(dayOfMonth));
        }
        return daysList;
    }

    public String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getOnBackPressedDispatcher().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
