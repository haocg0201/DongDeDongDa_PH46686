package com.haocg.myfootballapplication.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.Booking;
import com.haocg.myfootballapplication.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckInBookingAdapter extends RecyclerView.Adapter<CheckInBookingAdapter.ViewHolder> {
    private List<Booking> bookingList;
    private List<User> userList;
    private Context context;
    private static List<String> validTimes;

    public CheckInBookingAdapter(List<Booking> bookingList, List<User> userList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_booking, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        User user = userList.get(position);
        String soSan = booking.getStadiumId();
        char lastCharacter = soSan.charAt(soSan.length() - 1);
        holder.textSoSan.setTextColor(Color.BLUE);
        holder.textSoSan.setText("Sân số " + lastCharacter);
        holder.textViewDate.setText("Ngày đá: " + booking.getDate());
        holder.textViewTime.setText("Ca đá: " + booking.getTime());
        holder.textViewGuestNameChkBooking.setText("Tên khách: " + user.getName());
        holder.textViewGuestPNumberChkBooking.setText("SĐT khách: " + user.getPhone());

        if(isCurrentTimeBeforeBookingTime(booking.getTime())){
            holder.textViewStatus.setTextColor(Color.RED);
            holder.textViewStatus.setText("Trạng thái: Đã quá giờ, không thể xếp ca đá");

        }
        if(compareWithToday(booking.getDate()) == 1){
            holder.textViewStatus.setTextColor(Color.RED);
            holder.textViewStatus.setText("Trạng thái: Chưa tới ngày có thể xếp ca đá");
        } else if (booking.getStatus().equals("unpaid")) {
            holder.textViewStatus.setTextColor(Color.RED);
            holder.textViewStatus.setText("Cần thanh toán đặt cọc trước mới được check-in (khách chọn thanh toán cọc tại quầy)");
        } else{
            holder.textViewStatus.setTextColor(Color.BLUE);
            holder.textViewStatus.setText("Trạng thái: Có thể xếp ca đá");
        }

        getAvailableTimes(booking.getStadiumId(), booking.getDate());
        holder.tvDropdownTimeOptions.setOnClickListener(v -> {
            // Tạo PopupMenu
            PopupMenu popupMenu = new PopupMenu(context, holder.tvDropdownTimeOptions);

            for (String option : validTimes) {
                popupMenu.getMenu().add(option);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                Map<String, Object> times = new HashMap<>();
                times.put("time", item.getTitle());
                updateBooking(booking,times);
                Toast.makeText(context,"Cập nhật ca đá thành công", Toast.LENGTH_SHORT).show();
                reOpen();
                return true;
            });
            popupMenu.show();
        });



        holder.btnStaffChkBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(booking, user);
            }
        });

    }
    
    private void showDialog(Booking booking, User user){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Check in");
        builder.setMessage("Bạn có chắc check-in cho khách hàng " + user.getName() + " không ?");

        builder.setPositiveButton("Check-in", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("status", "checkedIn");
                updateBooking(booking, updateMap);
//                updatePaymentStatus(booking.getPaymentId());
                Toast.makeText(context, "Check-in thành công", Toast.LENGTH_SHORT).show();
                reOpen();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    private void reOpen(){
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);
        }
    }

    private void updateBooking(Booking booking, Map<String, Object> updates) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings").child(booking.getBookingId());

        bookingRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("OK");
                    }else {
                        String error = "Thất bại: " + task.getException();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void updatePaymentStatus(String paymentId) {
//        DatabaseReference paymentsRef = FirebaseDatabase.getInstance().getReference("payments");
//        paymentsRef.child(paymentId).child("status").setValue("paid")
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        System.out.println("OK");
//                    } else {
//                        String error = "Thất bại: " + task.getException();
//                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public boolean isCurrentTimeBeforeBookingTime(String bookingTime) {
        String startTime = bookingTime.split("-")[0]; // Lấy giờ đầu tiên ("08:00")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date bookingStartTime = timeFormat.parse(startTime);

            Calendar calendar = Calendar.getInstance();
            Date currentTime = timeFormat.parse(timeFormat.format(calendar.getTime()));

            return currentTime.before(bookingStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int compareWithToday(String bookingDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date bookingDateObj = dateFormat.parse(bookingDate);
            Date todayDate = new Date();

            todayDate = dateFormat.parse(dateFormat.format(todayDate));

        if (bookingDateObj.equals(todayDate)) {
                return 0; // Ngày trùng
            } else {
                return 1; // Ngày sau ngày hiện tại
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void  getAvailableTimes(String stadiumId,String inputDate) {
        DatabaseReference stadiumsRef = FirebaseDatabase.getInstance().getReference().child("stadiums").child(stadiumId);
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference().child("bookings");
        validTimes = new ArrayList<>();

        stadiumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot stadiumSnapshot) {
                if (stadiumSnapshot.exists()) {
                    Map<String, Boolean> availableTimes = (Map<String, Boolean>) stadiumSnapshot.child("available_times").getValue();
                    // Lọc các khung giờ có giá trị true nè
                    validTimes.clear();
                    for (Map.Entry<String, Boolean> timeEntry : availableTimes.entrySet()) {
                        if (timeEntry.getValue()) {
                            validTimes.add(timeEntry.getKey());
                        }
                    }
                    System.out.println("1. AvailableTime" + " - " + "Available times: " + validTimes);
                    // Kiểm tra tất cả các booking để loại bỏ các khung giờ bị trùng nhé
                    bookingsRef.orderByChild("stadiumId").equalTo(stadiumId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot bookingsSnapshot) {
                                    for (DataSnapshot bookingSnapshot : bookingsSnapshot.getChildren()) {
                                        String bookingDate = bookingSnapshot.child("date").getValue(String.class);
                                        String bookingTime = bookingSnapshot.child("time").getValue(String.class);
                                        String bookingStatus = bookingSnapshot.child("status").getValue(String.class);

                                        if ("confirmed".equalsIgnoreCase(bookingStatus) && bookingDate.equals(inputDate)) {
                                            validTimes.remove(bookingTime);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("FirebaseError", "Error retrieving bookings data: " + error.getMessage());
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving stadium data: " + error.getMessage());
            }
        });


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textSoSan,textViewDate, textViewTime,textViewGuestNameChkBooking, textViewGuestPNumberChkBooking, textViewStatus, tvDropdownTimeOptions;
        Button btnStaffChkBooking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSoSan = itemView.findViewById(R.id.textSoSanChkBooking);
            textViewDate = itemView.findViewById(R.id.textViewDateChkBooking);
            textViewTime = itemView.findViewById(R.id.textViewTimeChkBooking);
            textViewGuestNameChkBooking = itemView.findViewById(R.id.textViewGuestNameChkBooking);
            textViewGuestPNumberChkBooking = itemView.findViewById(R.id.textViewGuestPNumberChkBooking);
            textViewStatus = itemView.findViewById(R.id.textViewStatusChkBooking);
            btnStaffChkBooking = itemView.findViewById(R.id.btnStaffChkBooking);
            tvDropdownTimeOptions = itemView.findViewById(R.id.tvDropdownTimeOptions);
        }

//        @SuppressLint("SetTextI18n")
//        public void bind(Booking booking,User user) {}
    }
}

