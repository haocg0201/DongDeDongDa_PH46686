package com.haocg.myfootballapplication.staff.stafffragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.AddServiceAdapter;
import com.haocg.myfootballapplication.model.Booking;
import com.haocg.myfootballapplication.model.Service;
import com.haocg.myfootballapplication.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class AddServiceFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Booking> bookingList;
    private List<User> userList;
    private List<Service> serviceList;
    private AddServiceAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean dKey = false;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_service, container, false);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        getAllCheckedInBookings();
                        adapter.notifyDataSetChanged();
                    }
                });

        Bundle bundle = getArguments();
        if (bundle != null) {
            dKey = bundle.getBoolean("dKey", false);
        }

        ExtendedFloatingActionButton fabButtonInvoice = view.findViewById(R.id.fabButtonInvoice);
        fabButtonInvoice.setOnClickListener(v -> {
            int code = adapter.checkSelectedItems();
            if(code == 1){
                adapter.openTT(code);
            }else {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất 1 mục", Toast.LENGTH_SHORT).show();
            }
        });

        if(!dKey){
            fabButtonInvoice.setVisibility(View.GONE);
        }

        TextView txtTitleAddService = view.findViewById(R.id.txtTitleAddService);
        if(!dKey) txtTitleAddService.setText("Check-in hết hạn");

        bookingList = new ArrayList<>();
        userList = new ArrayList<>();
        serviceList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerViewAddService);
        adapter = new AddServiceAdapter(bookingList, userList, serviceList, requireActivity(), activityResultLauncher,dKey);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getAllCheckedInBookings();
        return view;
    }

    private void getAllCheckedInBookings() {
        bookingList.clear();
        userList.clear();

        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int[] userCount = {0};

                    for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        if (booking != null && booking.getStatus().equalsIgnoreCase("checkedIn")) {
                            boolean isTodayBooking = booking.getDate().equals(currentDate);
                            boolean isPastBooking = booking.getDate().compareTo(currentDate) < 0;

                            if ((dKey && isTodayBooking) || (!dKey && isPastBooking)) {
                                String userId = booking.getUserId();
                                booking.setBookingId(bookingSnapshot.getKey());
                                bookingList.add(booking);

                                userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            User user = snapshot.getValue(User.class);
                                            if (user != null) {
                                                user.setUid(snapshot.getKey());
                                                userList.add(user);
                                            }
                                        }
                                        userCount[0]++;
                                        if (userCount[0] == bookingList.size()) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("UserById @@", "Error loading user: " + error.getMessage());
                                    }
                                });
                            }
                        }
                    }
                    getAllServices();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AllBookings", "Error loading bookings: " + error.getMessage());
            }
        });
    }



    private void getAllServices() {
        serviceList.clear();
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");
        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                        Service service = serviceSnapshot.getValue(Service.class);
                        if (service != null) {
                            service.setsId(serviceSnapshot.getKey());
                            serviceList.add(service);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AllServices", "Error loading services: " + error.getMessage());
            }
        });
    }
}