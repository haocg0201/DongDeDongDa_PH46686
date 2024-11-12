package com.haocg.myfootballapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.Booking;
import com.haocg.myfootballapplication.model.Service;
import com.haocg.myfootballapplication.model.User;
import com.haocg.myfootballapplication.staff.InvoiceActivity;
import com.haocg.myfootballapplication.staff.stafffragments.ServiceActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddServiceAdapter extends RecyclerView.Adapter<AddServiceAdapter.ViewHolder> {
    private List<Booking> bookingList;
    private List<User> userList;
    private List<Service> serviceList;
    private Context context;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private List<Booking> selectedItems = new ArrayList<>();
    private User userIO = new User();
    private boolean dKey = false;

    public AddServiceAdapter(List<Booking> bookingList, List<User> userList, List<Service> serviceList, Context context, ActivityResultLauncher<Intent> launcher, boolean dKey) {
        this.bookingList = bookingList;
        this.userList = userList;
        this.serviceList = serviceList;
        this.context = context;
        this.activityResultLauncher = launcher;
        this.dKey = dKey;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checked_in, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        if(!userList.isEmpty()){
            User user = userList.get(position);
            holder.textViewGuestNameAddService.setText("Tên khách: " + user.getName());
            holder.textViewGuestPNumberAddService.setText("SĐT khách: " + user.getPhone());
            userIO = user;
        }

        String soSan = booking.getStadiumId();
        char lastCharacter = soSan.charAt(soSan.length() - 1);
        holder.textSoSanAddService.setTextColor(Color.BLUE);
        holder.textSoSanAddService.setText("Sân số " + lastCharacter);
        holder.textViewDateAddService.setText("Ngày đá: " + booking.getDate());
        holder.textViewTimeAddService.setText("Ca đá: " + booking.getTime());
        holder.textStatusAddService.setText("ĐÃ CHUẨN BỊ SÂN");
        Map<String, Boolean> servicesMap = booking.getServices();
        Log.d("BookingServices", "Services: " + servicesMap);
        List<String> lstServ = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : servicesMap.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            if(value){
                lstServ.add(key);
            }
        }
        Log.d("SelectedServices", "Services: " + lstServ);
        StringBuilder servicesText = new StringBuilder();
        serviceList.forEach(service -> {
            lstServ.forEach(key -> {
                if (service.getsId().equals(key)) {
                    servicesText.append(service.getDescription())
                            .append(" - Giá: ")
                            .append(service.getPrice())
                            .append(" VNĐ\n");
                }
            });
        });

        if (servicesText.length() > 0) {
            holder.textAllServiceAddService.setText(servicesText.toString());
        } else {
            holder.textAllServiceAddService.setText("Không có dịch vụ nào.");
        }

        holder.textAddServiceForm.setVisibility(dKey? View.VISIBLE : View.GONE);

        holder.textAddServiceForm.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceActivity.class);
            intent.putExtra("bookingId", booking.getBookingId());
            activityResultLauncher.launch(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            if(dKey){
                boolean foundDuplicate = false;

                for (Booking selectedBooking : selectedItems) {
                    if (!selectedBooking.getUserId().equals(booking.getUserId())) {
                        foundDuplicate = true;
                        break;
                    }
                }
                if (!foundDuplicate) {
                    if (!selectedItems.contains(booking)) {
                        selectedItems.add(booking);
                    }else {
                        selectedItems.remove(booking);
                    }
                    notifyItemChanged(position);
                }else {
                    Toast.makeText(context, "Không thể thanh toán cho 2 đối khác hàng khác nhau cùng lúc", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                }
            }
        });

        holder.itemView.setBackgroundColor(selectedItems.contains(booking) ? Color.LTGRAY : Color.WHITE);

        holder.btnTT.setVisibility(dKey? View.VISIBLE : View.GONE);
        holder.btnTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItems.add(booking);
                openTT(1);
            }
        });
    }

    public int checkSelectedItems(){
        if(selectedItems.isEmpty()){
            return 0;
        }else{
            return 1;
        }
    }

    public void openTT(int code) {
        if (code == 1){
            Intent intent = new Intent(context, InvoiceActivity.class);
            intent.putParcelableArrayListExtra("booking_list", new ArrayList<Parcelable>(selectedItems));
            intent.putParcelableArrayListExtra("service_list", new ArrayList<>(serviceList));
            intent.putExtra("userIO",userIO);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0; // mệt luôn load aaau vãi chưởng, chắc do mạng
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textSoSanAddService,
                textViewDateAddService,
                textViewTimeAddService,
                textViewGuestNameAddService,
                textViewGuestPNumberAddService,
                textAllServiceAddService,
                textStatusAddService,
                textAddServiceForm,
                btnTT;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSoSanAddService = itemView.findViewById(R.id.textSoSanAddService);
            textViewDateAddService = itemView.findViewById(R.id.textViewDateAddService);
            textViewTimeAddService = itemView.findViewById(R.id.textViewTimeAddService);
            textViewGuestNameAddService = itemView.findViewById(R.id.textViewGuestNameAddService);
            textViewGuestPNumberAddService = itemView.findViewById(R.id.textViewGuestPNumberAddService);
            textAllServiceAddService = itemView.findViewById(R.id.textAllServiceAddService);
            textStatusAddService = itemView.findViewById(R.id.textStatusAddService);
            textAddServiceForm = itemView.findViewById(R.id.textAddServiceForm);
            btnTT = itemView.findViewById(R.id.btnTT);
        }
    }

}

