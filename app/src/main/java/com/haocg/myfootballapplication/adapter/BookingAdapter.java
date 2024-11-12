package com.haocg.myfootballapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private List<Booking> bookingList;
    private List<Booking> selectedItems = new ArrayList<>();
    private Context context;
    public BookingAdapter(List<Booking> bookingList,Context context) {
        this.bookingList = bookingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        String userId = booking.getUserId();
        holder.bind(booking);

        holder.itemView.setOnLongClickListener(v -> {
            if (selectedItems.contains(booking)) {
                selectedItems.remove(booking);
                holder.itemView.setBackgroundColor(Color.WHITE);
            } else {
                selectedItems.add(booking);
                holder.itemView.setBackgroundColor(Color.LTGRAY); // Đổi màu khi chọn
            }
//            System.out.println("Danh sách đặt sân đã chọn: " + selectedItems.size());
            return true;
        });


    }

    public List<Map<String, String>> getSelectedItemDetails() {
        List<Map<String, String>> selectedItemDetails = new ArrayList<>();
        for (Booking booking : selectedItems) {
            Map<String, String> itemDetails = new HashMap<>();
            itemDetails.put("bookingId", booking.getBookingId());
            itemDetails.put("stadiumId", booking.getStadiumId());
            selectedItemDetails.add(itemDetails);
        }
//        System.out.println("Selected items count: " + selectedItems.size());
//        for (Map<String, String> item : selectedItemDetails) {
//            System.out.println("Booking ID: " + item.get("bookingId") + ", Stadium ID: " + item.get("stadiumId"));
//        }
        return selectedItemDetails;
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textSoSan,textViewDate, textViewTime, textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSoSan = itemView.findViewById(R.id.textSoSan);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Booking booking) {
            String soSan = booking.getStadiumId();
            char lastCharacter = soSan.charAt(soSan.length() - 1);
            textSoSan.setTextColor(Color.BLUE);
            textSoSan.setText("Sân số " + lastCharacter);
            textViewDate.setText("Ngày đá: " + booking.getDate());
            textViewTime.setText("Ca đá: " + booking.getTime());
            if(booking.getStatus().equalsIgnoreCase("pending")){
                textViewStatus.setTextColor(Color.RED);
                textViewStatus.setText("Trạng thái đặt: chưa đặt cọc/thanh toán");
            } else if (booking.getStatus().equalsIgnoreCase("confirmed")){
                textViewStatus.setTextColor(Color.GREEN);
                textViewStatus.setText("Trạng thái đặt: Đã đặt cọc, sân đang chờ");
            } else if (booking.getStatus().equalsIgnoreCase("unpaid")) {
                textViewStatus.setTextColor(Color.BLUE);
                textViewStatus.setText("Trạng thái đặt: Chưa thanh toán, đang chờ thanh toán - Nếu quý khách không thanh toán nhanh sân sẽ bị đặt trước");
            } else {
                textViewStatus.setTextColor(Color.BLACK);
                textViewStatus.setText("Day-off");
            }
        }
    }
}

