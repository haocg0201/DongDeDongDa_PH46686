package com.haocg.myfootballapplication.guest.stadiumutil;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.AvailableTimeAdapter;
import com.haocg.myfootballapplication.model.Booking;
import com.haocg.myfootballapplication.model.Stadium;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingMainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AvailableTimeAdapter adapter;
    List<String> availableTimes ;
    ExtendedFloatingActionButton extendedFabDatSan;

    String stadiumId;
    String stadiumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainDatCa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        stadiumId = getIntent().getStringExtra("stadiumId");
        stadiumName = getIntent().getStringExtra("stadiumName");

        extendedFabDatSan = findViewById(R.id.extendedFabDatSan);
        if (stadiumId != null && stadiumName != null) {
            System.out.println("---------------- " + stadiumId + " ------------------------");
        }

        getAvailableTimes(stadiumId);

        extendedFabDatSan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Boolean> mapUserBook = adapter.getMapTimeUserBook();
                StringBuilder selectedTimes = new StringBuilder();
                boolean hasSelected = false;

                for (Map.Entry<String, Boolean> entry : mapUserBook.entrySet()) {
                    if (entry.getValue()) {
                        selectedTimes.append(entry.getKey()).append("\n");
                        hasSelected = true;
                    }
                }

                if (!hasSelected) {
                    Toast.makeText(BookingMainActivity.this, "Không có ca nào được chọn", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingMainActivity.this);
                    builder.setTitle("ĐẶT CA");

                    String message = "Bạn có muốn đặt khung giờ đá của sân không?\n" + selectedTimes.toString();
                    System.out.println(message);
                    builder.setMessage(message);

                    builder.setPositiveButton("Đặt", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bookingRN(selectedTimes);
                        }
                    });

                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = findViewById(R.id.bookingStadium_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Đặt ca");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);

    }

    private void getAvailableTimes(String stadiumId) {
        DatabaseReference stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums").child(stadiumId);

        stadiumRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                availableTimes = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    Stadium stadium = dataSnapshot.getValue(Stadium.class);
                    if (stadium != null) {
                        Map<String, Boolean> availableTimesMap = stadium.getAvailable_times();
                        if (availableTimesMap != null) {
                            for (Map.Entry<String, Boolean> entry : availableTimesMap.entrySet()) {
                                if (entry.getValue() != null && entry.getValue()) {
                                    availableTimes.add(entry.getKey());
                                }
                            }
                        }
                    }
//                    availableTimes = new ArrayList<>(availableTimesMap.keySet());
                    Collections.sort(availableTimes, new Comparator<String>() {
                        @Override
                        public int compare(String time1, String time2) {
                            // Tách thời gian từ chuỗi
                            String[] parts1 = time1.split("-");
                            String[] parts2 = time2.split("-");

                            // Chuyển đổi thời gian sang kiểu LocalTime để so sánh
                            LocalTime startTime1 = LocalTime.parse(parts1[0]);
                            LocalTime endTime1 = LocalTime.parse(parts1[1]);
                            LocalTime startTime2 = LocalTime.parse(parts2[0]);
                            LocalTime endTime2 = LocalTime.parse(parts2[1]);
                            return startTime1.compareTo(startTime2);
                        }
                    });
                    int price = stadium.getPrice();
                    if (stadium != null) {
                        //List<String> unavailableTimesList = new ArrayList<>();
                        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
                        bookingsRef.orderByChild("stadiumId").equalTo(stadiumId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot bookingSnapshot) {
                                String date = getDateFromPreferences();
                                List<String> lstUnAvailableTime = new ArrayList<>();
                                for (DataSnapshot bookingData : bookingSnapshot.getChildren()) {
                                    Booking booking = bookingData.getValue(Booking.class);
                                    String [] arr = date.split("-");
                                    System.out.println("arr: " + Arrays.toString(arr));
                                    String [] bkArr = booking.getDate().split(("-"));
                                    System.out.println("brArr: " + Arrays.toString(bkArr));
                                    if (Integer.parseInt(arr[0]) == Integer.parseInt(bkArr[0]) && Integer.parseInt(arr[1]) == Integer.parseInt(bkArr[1]) && Integer.parseInt(arr[2]) == Integer.parseInt(bkArr[2])) {
                                        if(booking.getStatus().equalsIgnoreCase("confirmed")) {
                                            lstUnAvailableTime.add(booking.getTime());
                                        }
                                    }
                                }
                                updateAdapter(availableTimes,price,lstUnAvailableTime);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Xử lý lỗi Firebase
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi Firebase
            }
        });
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("userId", null); // trả về null nếu không tìm thấy nhé
    }

    private String getDateFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyDatePrefs", MODE_PRIVATE);
        return sharedPreferences.getString("date", null); // trả về null nếu không tìm thấy nhé
    }

    private void bookingRN(StringBuilder selectedTimes){
        String[] timesArray = selectedTimes.toString().split("\\n");

        String userId = getUserIdFromPreferences();
        String date = getDateFromPreferences();
        String paymentId = "";
        Map<String, Boolean> services = new HashMap<>();
        services.put("serviceId_1", false);
        services.put("serviceId_2", false);

        // Lặp qua các khung giờ đã chọn
        for (String selectedTime : timesArray) {
            String bookingId = "bookingId_" + System.currentTimeMillis();

            // Tạo đối tượng booking
            Map<String, Object> booking = new HashMap<>();
            booking.put("userId", userId);
            booking.put("stadiumId", stadiumId);
            booking.put("time", selectedTime.trim());
            booking.put("date", date);
            booking.put("status", "pending");
            booking.put("paymentId", paymentId);
            booking.put("services", services);

            // Thêm vào Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference bookingsRef = database.getReference("bookings");

            bookingsRef.child(bookingId).setValue(booking).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(BookingMainActivity.this, "Đặt sân thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BookingMainActivity.this, "Lỗi khi đặt sân: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        startActivity(new Intent(BookingMainActivity.this, PaymentMainActivity.class));
        finish();
    }

    private void updateAdapter(List<String> availableTimesList,int price, List<String> unavailableTimesList) {
        recyclerView = findViewById(R.id.recyclerViewDatCa);
        recyclerView.setLayoutManager(new LinearLayoutManager(BookingMainActivity.this));
        adapter = new AvailableTimeAdapter(BookingMainActivity.this, availableTimesList,price,unavailableTimesList,stadiumId);
        recyclerView.setAdapter(adapter);
    }

    private String dateNow(String pattern) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return today.format(formatter);
    }

    private String[] toTimeArray(StringBuilder selectedTimes) {
        String selectedTimesString = selectedTimes.toString();
        return selectedTimesString.split(","); // ** //
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getOnBackPressedDispatcher().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}