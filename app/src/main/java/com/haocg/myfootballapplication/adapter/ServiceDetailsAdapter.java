package com.haocg.myfootballapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDetailsAdapter extends RecyclerView.Adapter<ServiceDetailsAdapter.ServiceViewHolder>{

    private List<Service> serviceList;
    Map<String, Boolean> selectedServices;

    public ServiceDetailsAdapter(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_details, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceDescription.setText(service.getDescription());
        holder.servicePrice.setText("Giá: " + service.getPrice() + " VNĐ");

        if (service.getName().startsWith("Nước")) {
            holder.serviceImage.setImageResource(R.mipmap.water);
        } else if (service.getName().startsWith("Khăn")) {
            holder.serviceImage.setImageResource(R.mipmap.towel);
        } else {
            holder.serviceImage.setImageResource(R.mipmap.service);
        }

        selectedServices = new HashMap<>();
        selectedServices.put(service.getsId(), false);

        holder.checkService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String serviceId = service.getsId();

                if (isChecked) {
                    selectedServices.put(serviceId, true);
                } else {
                    selectedServices.put(serviceId, false);
                }
            }
        });

    }

    public Map<String, Boolean> getServiceIds(){
        return selectedServices;
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, serviceDescription, servicePrice;
        ImageView serviceImage;
        CheckBox checkService;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            checkService = itemView.findViewById(R.id.checkService);
        }
    }
}
