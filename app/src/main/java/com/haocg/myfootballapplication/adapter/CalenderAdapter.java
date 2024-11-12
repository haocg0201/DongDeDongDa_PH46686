package com.haocg.myfootballapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.guest.stadiumutil.BookingMainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.ViewHolder> {
    private List<String> daysList;
    //private List<Booking> bookingList;
    private Context context;
    private String stadiumId;

//    public CalenderAdapter(List<String> daysList, List<Booking> bookingList, Context context, String stadiumId) {
//        this.daysList = daysList;
//        this.bookingList = bookingList;
//        this.context = context;
//        this.stadiumId = stadiumId;
//    }


    public CalenderAdapter(List<String> daysList, Context context, String stadiumId) {
        this.daysList = daysList;
        this.context = context;
        this.stadiumId = stadiumId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = daysList.get(position);
        holder.dayTextView.setText(day);
        if(isDateInThePast(day)){
            holder.txtStatusBooking.setText("Không thể đặt");
            ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
            holder.txtStatusBooking.setBackgroundTintList(colorStateList);

        }else {
            holder.txtStatusBooking.setText("Ngày có thể đặt");
            ColorStateList colorStateList = ColorStateList.valueOf(Color.GREEN);
            holder.txtStatusBooking.setBackgroundTintList(colorStateList);

        }


//        String status = getBookingStatusForDay(day);
//        holder.txtStatusBooking.setText(status);

        holder.itemView.setOnClickListener(v -> {
            if (isDateInThePast(day)) {
                Toast.makeText(context, "Ngày đã qua", Toast.LENGTH_SHORT).show();
            } else {
                openBookingForm(day);
            }
        });
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

//    private String getBookingStatusForDay(String day) {
//        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        SimpleDateFormat sdfDay = new SimpleDateFormat("dd", Locale.getDefault());
//
//        for (Booking booking : bookingList) {
//            try {
//                Date bookingDate = sdfInput.parse(booking.getDate());
//                System.out.println("booking date: " + bookingDate + " - " + booking.getDate());
//                System.out.println("sdfDay.format(bookingDate).toString(): " + sdfDay.format(bookingDate));
//                if (sdfDay.format(bookingDate).equals(day)) {
//                    return booking.getStatus();
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return "";
//    }


    private boolean isDateInThePast(String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        try {
            int dayNumber = Integer.parseInt(day);
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentYear = calendar.get(Calendar.YEAR);

            if (currentMonth == getMonth() && dayNumber < calendar.get(Calendar.DAY_OF_MONTH)) {
                return true; // Ngày đã qua
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false; // Ngày chưa qua
    }

    private String getFormattedDate(String day) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng tính từ 1
        int year = calendar.get(Calendar.YEAR);
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, Integer.parseInt(day));
    }

    private void openBookingForm(String day) {
        String formattedDate = getFormattedDate(day);
        saveDateToPreferences(formattedDate);
        Intent intent = new Intent(context, BookingMainActivity.class);
        intent.putExtra("stadiumId", stadiumId);
        context.startActivity(intent);
    }

    private int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1; // Tháng tính từ 1
    }

    private void saveDateToPreferences(String date) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyDatePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("date", date);
        editor.apply();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView, txtStatusBooking;

        public ViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            txtStatusBooking = itemView.findViewById(R.id.statusBooking);
        }
    }
}
