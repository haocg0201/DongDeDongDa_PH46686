package com.haocg.myfootballapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.guest.stadiumutil.CalenderMainActivity;
import com.haocg.myfootballapplication.model.Stadium;
import com.haocg.myfootballapplication.staff.StaffCheckingMainActivity;

import java.util.List;

public class StadiumAdapter extends RecyclerView.Adapter<StadiumAdapter.StadiumViewHolder> {

    private List<Stadium> stadiumList;
    private final Context context;
    private boolean isHoldLonger = false;

    private final int[] imageResources = {
            R.mipmap.san1,
            R.mipmap.san2,
            R.mipmap.san3,
            R.mipmap.san4,
            R.mipmap.san5,
            R.mipmap.san6,
            R.mipmap.san7
    };
    // Constructor
    public StadiumAdapter(List<Stadium> stadiumList, Context context, boolean isHoldLonger) {
        this.stadiumList = stadiumList;
        this.context = context;
        this.isHoldLonger = isHoldLonger;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Stadium> newList) {
        stadiumList = newList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public StadiumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stadium_item, parent, false);
        return new StadiumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StadiumViewHolder holder, int position) {
        Stadium stadium = stadiumList.get(position);
        holder.stadiumName.setText(stadium.getName());
        holder.stadiumLocation.setText(stadium.getLocation());
        holder.stadiumPrice.setText(String.format("Giá: %d VND", stadium.getPrice()));

        if (position < imageResources.length) {
            holder.stadiumImg.setImageResource(imageResources[position]);
        } else {
            holder.stadiumImg.setImageResource(R.mipmap.stadium);
        }

        holder.itemView.setOnClickListener(v -> {
            if(!isHoldLonger){
                Intent intent = new Intent(context, CalenderMainActivity.class);
                intent.putExtra("stadiumId", stadium.getStadium_id());
                intent.putExtra("stadiumName", stadium.getName());
                context.startActivity(intent);
            }else{
                showDialog(stadium.getStadium_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return stadiumList.size();
    }

    private void showDialog(String stadiumId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tiêu đề Dialog")
                .setMessage("Xem lịch đặt sân và ca đá")
                .setPositiveButton("Hôm nay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, StaffCheckingMainActivity.class);
                        intent.putExtra("stadiumId", stadiumId);
                        intent.putExtra("dateStatus", "today");
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Ngày trong tháng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, StaffCheckingMainActivity.class);
                        intent.putExtra("stadiumId", stadiumId);
                        intent.putExtra("dateStatus", "InTheMonth");
                        context.startActivity(intent);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class StadiumViewHolder extends RecyclerView.ViewHolder {

        TextView stadiumName, stadiumLocation, stadiumPrice;
        ImageView stadiumImg;

        public StadiumViewHolder(@NonNull View itemView) {
            super(itemView);
            stadiumName = itemView.findViewById(R.id.stadium_name);
            stadiumLocation = itemView.findViewById(R.id.stadium_location);
            stadiumPrice = itemView.findViewById(R.id.stadium_price);
            stadiumImg = itemView.findViewById(R.id.stadium_img);
        }
    }
}
