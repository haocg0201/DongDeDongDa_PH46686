package com.haocg.myfootballapplication.guest.stadiumutil.paymentfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.BookingAdapter;
import com.haocg.myfootballapplication.model.Booking;
import com.haocg.myfootballapplication.model.Payment;
import com.haocg.myfootballapplication.model.Stadium;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class PendingBookingsFragment extends Fragment {
    private RecyclerView recyclerView;
    public BookingAdapter bookingAdapter;
    private List<Booking> pendingBookings = new ArrayList<>();
    private ExtendedFloatingActionButton eFltBtn;
    private int totalPrice = 0;
    List<Map<String,String>> bookingIdAndStadiumPriceMap;
    List<Map<String,String>> selectedItemDetails;

    @SuppressLint("MissingInflatedId")
    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_bookings, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingAdapter = new BookingAdapter(pendingBookings,requireActivity());
        recyclerView.setAdapter(bookingAdapter);
        loadPendingBookings();

        eFltBtn = view.findViewById(R.id.fabButtonPayment);
        eFltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pendingBookings.isEmpty()){
                    selectedItemDetails = bookingAdapter.getSelectedItemDetails();
                    handleSelectedItems(selectedItemDetails);
                    showPaymentDialog();
                }else Toast.makeText(requireActivity(), "Quý khách hàng vui lòng đặt lịch đá bóng trước", Toast.LENGTH_SHORT).show();

            }
        });



        return view;
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setTitle("Thanh toán");
        builder.setMessage("Bạn sẽ thanh toán một nửa sân tiền cọc " + (totalPrice/2) + " (50% tiền cọc sân)");

        builder.setPositiveButton("Thanh toán tại quầy", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {

                processPayments("cash");
                startActivity(requireActivity().getIntent());
                getActivity().finish();
            }
        });
        builder.setNegativeButton("Thanh toán trực tuyến", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processPayments("online");
                startActivity(requireActivity().getIntent());
                getActivity().finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setFabVisibility(boolean isVisible) {
        if (eFltBtn != null) {
            eFltBtn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void loadPendingBookings() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bookings");

        databaseReference.orderByChild("userId").equalTo(getUserIdFromPreferences()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingBookings.clear();
                if(snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Booking booking = data.getValue(Booking.class);
                        String bookingId = data.getKey();
                        if (booking != null && booking.getDate().compareTo(currentDate) >= 0 && booking.getStatus().equals("pending")) {
                            booking.setBookingId(bookingId);
                            pendingBookings.add(booking);
                        }
                    }
                    bookingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

//    private void handleSelectedItems(List<Map<String,String>> selectedItemDetails,String payStatus,DialogInterface dialog) {
//        if (selectedItemDetails == null || selectedItemDetails.isEmpty()) {
//            Toast.makeText(getContext(), "Không có dữ liệu đặt sân", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        DatabaseReference stadiumRef = FirebaseDatabase.getInstance().getReference("bookings");
//        bookingIdAndStadiumPriceMap = new ArrayList<>();
//        totalPrice = 0.0;
//        for (Map<String,String> item : selectedItemDetails) {
//            String bookingId = item.get("bookingId");
//            String stadiumId = item.get("stadiumId");
//            System.out.println("======== bookingId: " + bookingId + " stadiumId: " + stadiumId + " ===============");
//            if(stadiumId != null){
//                stadiumRef.child(stadiumId).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Stadium stadium = snapshot.getValue(Stadium.class);
//                        if (stadium != null) {
//                            double price = stadium.getPrice();
//                            totalPrice += (double) price;
//                            Map<String, String> itemDetails = new HashMap<>();
//                            itemDetails.put("bookingId", bookingId);
//                            itemDetails.put("stadiumPrice", String.valueOf(price));
//                            bookingIdAndStadiumPriceMap.add(itemDetails);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        System.out.println("Lỗi ++++++++++++++++: " + error.getMessage() + "======================== error ========================");
//                    }
//                });
//            }
//        }
//        processPayments(payStatus);
//    }

    private void handleSelectedItems(List<Map<String, String>> selectedItemDetails) {
        if (selectedItemDetails == null || selectedItemDetails.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu đặt sân", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums");
        bookingIdAndStadiumPriceMap = new ArrayList<>();
        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        for (Map<String, String> item : selectedItemDetails) {
            String bookingId = item.get("bookingId");
            String stadiumId = item.get("stadiumId");

            if (stadiumId != null) {
                System.out.println("Đang kiểm tra stadiumId: " + stadiumId);

                Task<DataSnapshot> task = stadiumRef.child(stadiumId).get();
                tasks.add(task);

                task.addOnSuccessListener(snapshot -> {
                    Stadium stadium = snapshot.getValue(Stadium.class);
                    if (stadium != null) {
                        int price = stadium.getPrice();
                        totalPrice += price;
                        Map<String, String> itemDetails = new HashMap<>();
                        itemDetails.put("bookingId", bookingId);
                        itemDetails.put("stadiumPrice", String.valueOf(price));
                        bookingIdAndStadiumPriceMap.add(itemDetails);
                    } else {
                        System.out.println("Stadium không tồn tại hoặc dữ liệu lỗi cho stadiumId: " + stadiumId);
                    }
                }).addOnFailureListener(error -> {
                    System.out.println("Lỗi khi tải dữ liệu sân: " + error.getMessage());
                });
            } else {
                System.out.println("stadiumId là null cho bookingId: " + bookingId);
            }
        }

        System.out.println("+++++++++++++++++++++== " + totalPrice + " ++++++++++++++++++++++++");

//        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
//            System.out.println("Danh sách bookingIdAndStadiumPriceMap: " + bookingIdAndStadiumPriceMap);
//            if (bookingIdAndStadiumPriceMap.isEmpty()) {
//                Toast.makeText(getContext(), "Không có dữ liệu thanh toán sau khi hoàn thành", Toast.LENGTH_SHORT).show();
//            } else {
//                processPayments(payStatus);
//            }
//        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void processPayments(String payStatus) {
        if (bookingIdAndStadiumPriceMap.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Map<String,String> item : bookingIdAndStadiumPriceMap) {
            String bookingId = item.get("bookingId");
            String stadiumPrice = item.get("stadiumPrice");
            String bookingStatus;
            double priceToSave = stadiumPrice != null ? Double.parseDouble(stadiumPrice) : 100000.0;
            String paymentId = "paymentId_" + FirebaseDatabase.getInstance().getReference("payments").push().getKey();
            Payment payment = new Payment();
            if(payStatus.equalsIgnoreCase("cash")){
                bookingStatus = "unpaid";
                payment = new Payment(bookingId, priceToSave, payStatus, "unpaid");
            }else {
                payment = new Payment(bookingId, priceToSave, payStatus, "paid");
                bookingStatus = "confirmed";
            }

            FirebaseDatabase.getInstance().getReference("payments").child(paymentId).setValue(payment)
                    .addOnSuccessListener(unused -> {
                        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings").child(bookingId);
                        bookingRef.child("status").setValue(bookingStatus);
                        bookingRef.child("paymentId").setValue(paymentId);
                        Toast.makeText(getContext(), "Đã đặt xong, sân đang chờ", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                    });
        }
//        if (getActivity() instanceof OnPaymentStatusChangeListener) {
//            ((OnPaymentStatusChangeListener) getActivity()).onPaymentStatusChanged();
//        }
    }

}