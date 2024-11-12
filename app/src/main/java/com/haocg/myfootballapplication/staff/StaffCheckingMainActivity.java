package com.haocg.myfootballapplication.staff;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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
import com.haocg.myfootballapplication.adapter.CheckInBookingAdapter;
import com.haocg.myfootballapplication.model.Booking;
import com.haocg.myfootballapplication.model.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaffCheckingMainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCheckBooking;
    private CheckInBookingAdapter CheckBookingAdapter;
    private List<Booking> bookingList;
    private List<User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_checking_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainStaffChecking), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.checking_toolbar_staff);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Các đơn và ca đá trong ngày");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);


        recyclerViewCheckBooking = findViewById(R.id.recyclerViewCheckBooking);
        recyclerViewCheckBooking.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        userList = new ArrayList<>();
        CheckBookingAdapter = new CheckInBookingAdapter(bookingList,userList, this);
        recyclerViewCheckBooking.setAdapter(CheckBookingAdapter);

        String stadiumId = getIntent().getStringExtra("stadiumId");
        String dateStatus = getIntent().getStringExtra("dateStatus");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        loadBookings(stadiumId, dateStatus, currentDate);
    }

    private void loadBookings(String stadiumId,String dateStatus, String date) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        bookingRef.orderByChild("stadiumId").equalTo(stadiumId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        if (booking != null && booking.getDate().equals(date) && !booking.getStatus().equalsIgnoreCase("checkedIn")) {
                            booking.setBookingId(bookingSnapshot.getKey());
                            String userId = booking.getUserId();
                            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        User user = userSnapshot.getValue(User.class);
                                        if (user != null) {
                                            bookingList.add(booking);
                                            userList.add(user);
                                            CheckBookingAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("BookingListActivity", "Error fetching user:(( : " + error.getMessage());
                                }
                            });
                        }else if(booking != null && dateStatus.equalsIgnoreCase("InTheMonth") && !booking.getStatus().equalsIgnoreCase("checkedIn")){
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate bookingDate = LocalDate.parse(booking.getDate(), formatter);
                            LocalDate currentDate = LocalDate.now();
                            if(bookingDate.isAfter(currentDate)){
                                booking.setBookingId(bookingSnapshot.getKey());
                                String userId = booking.getUserId();
                                userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        if (userSnapshot.exists()) {
                                            User user = userSnapshot.getValue(User.class);
                                            if (user != null) {
                                                bookingList.add(booking);
                                                userList.add(user);
                                                CheckBookingAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("BookingListActivity", "Error fetching user:(( : " + error.getMessage());
                                    }
                                });
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BookingListActivity", "Error: " + error.getMessage());
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}