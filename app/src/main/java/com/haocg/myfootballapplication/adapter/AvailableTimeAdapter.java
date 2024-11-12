package com.haocg.myfootballapplication.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.guest.stadiumutil.PaymentMainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailableTimeAdapter extends RecyclerView.Adapter<AvailableTimeAdapter.ViewHolder> {

    private Context context;
    private List<String> availableTimes;
    private int price;
    private List<String> unAvailableTimes;
    private Map<String, Boolean> mapTimeUserBook = new HashMap<>();
    private String stadiumId;

    public AvailableTimeAdapter(Context context, List<String> availableTimes, int price, List<String> UnAvailableTimes,String stadiumId) {
        this.context = context;
        this.availableTimes = availableTimes;
        this.price = price;
        this.unAvailableTimes = UnAvailableTimes;
        this.stadiumId = stadiumId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String availableTime = availableTimes.get(position);
//       System.out.println(availableTime);

        mapTimeUserBook.put(availableTime,false);

        holder.txtCaDat.setText(availableTime);
        holder.txtGiaCa.setText(price + " VND");
        holder.txtTrangThaiSan.setText("");
        if (unAvailableTimes.contains(availableTime)) {
            holder.txtTrangThaiSan.setText("Đã kín lịch");
            holder.btnDatCa.setVisibility(View.GONE);
            holder.checkBoxDatNhieuCa.setVisibility(View.GONE);
        }
        holder.btnDatCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Đặt sân");
                builder.setMessage("Bạn có muốn đặt sân lúc " + availableTime + " không?");
                builder.setPositiveButton("Đặt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookingRN(availableTime);
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
        });

        holder.checkBoxDatNhieuCa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            holder.btnDatCa.setEnabled(!isChecked);
            mapTimeUserBook.put(availableTime,isChecked);

            System.out.println("Danh sách sân và trạng thái đặt sân:");
            for (Map.Entry<String, Boolean> entry : mapTimeUserBook.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }

        });

    }

    private void bookingRN(String selectedTime){
        String userId = getUserIdFromPreferences();
        String date = getDateFromPreferences();
        String paymentId = "";
        Map<String, Boolean> services = new HashMap<>();
        services.put("serviceId_1", false);
        services.put("serviceId_2", false);

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
                handleBookingResult(true);
            } else {
                handleBookingResult(false);
                Toast.makeText(context, "Lỗi khi đặt sân: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBookingResult(boolean isSuccessful) {
        if (isSuccessful) {
            Toast.makeText(context, "Đặt sân thành công!", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, PaymentMainActivity.class));
            ((Activity) context).finish();
        } else {
            Toast.makeText(context, "Lỗi khi đặt sân: Không biết nữa @@, chắc do mạng" , Toast.LENGTH_SHORT).show();
        }
    }

    public Map<String, Boolean> getMapTimeUserBook() {
        return mapTimeUserBook;
    }


    @Override
    public int getItemCount() {
        return availableTimes.size();
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null); // trả về null nếu không tìm thấy nhé
    }

    private String getDateFromPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyDatePrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("date", null); // trả về null nếu không tìm thấy nhé
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCaDat, txtGiaCa, txtTrangThaiSan;
        CheckBox checkBoxDatNhieuCa;
        Button btnDatCa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCaDat = itemView.findViewById(R.id.txtGioCa);
            txtGiaCa = itemView.findViewById(R.id.txtGiaCa);
            txtTrangThaiSan = itemView.findViewById(R.id.txtTrangThaiSan);
            checkBoxDatNhieuCa = itemView.findViewById(R.id.datNhieuSanChk);
            btnDatCa = itemView.findViewById(R.id.dat1SanBtn);
        }
    }
}
