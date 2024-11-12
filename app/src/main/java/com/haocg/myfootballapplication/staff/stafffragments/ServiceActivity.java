package com.haocg.myfootballapplication.staff.stafffragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.ServiceDetailsAdapter;
import com.haocg.myfootballapplication.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceActivity extends AppCompatActivity {

    private List<Service> serviceList;
    private ServiceDetailsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.service_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = findViewById(R.id.service_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Dịch vụ");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);

        String bookingId = getIntent().getStringExtra("bookingId");
        ExtendedFloatingActionButton fabButtonAddService = findViewById(R.id.fabButtonAddService);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewServices);

        serviceList = new ArrayList<>();
        adapter = new ServiceDetailsAdapter(serviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getAllServices();

        fabButtonAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Boolean> selectedServices = adapter.getServiceIds();
                updateBookingWithSelectedServices(bookingId, selectedServices);
            }
        });

    }

    private void updateBookingWithSelectedServices(String bookingId, Map<String, Boolean> selectedServices) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings").child(bookingId);

        bookingRef.child("services").setValue(selectedServices)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ServiceActivity.this, "Đã thao tác dịch vụ", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("BookingUpdate", "Failed to update booking services: " + e.getMessage());
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

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                //this.getOnBackPressedDispatcher().onBackPressed();
                setResult(Activity.RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}