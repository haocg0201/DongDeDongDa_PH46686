package com.haocg.myfootballapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class MAccountAdapter extends RecyclerView.Adapter<MAccountAdapter.AccountViewHolder> {

    public interface AccountActionListener {
        void onUpdateUser(User user);
        void onDeleteUser(String userId);
    }

    private Context context;
    private List<User> userList;
    private List<User> filteredList;
    private AccountActionListener listener;

    public MAccountAdapter(Context context, List<User> userList, AccountActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.filteredList = new ArrayList<>(userList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ma, parent, false);
        return new AccountViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        User user = filteredList.get(position);
        holder.tvName.setText("Họ và tên: " + user.getName());
        holder.tvEmail.setText("Email: " + user.getEmail());
        holder.tvPhone.setText("Số điện thoại: " + user.getPhone());
        holder.tvRole.setText("Vai trò: " + (user.getRole().equals("admin") ? "Quản trị viên(admin)" : (user.getRole().equals("staff")) ? "Nhân viên(staff)" : "Người dùng(user)"));

        holder.btnInfo.setOnClickListener(v -> {
            showUserDetailsDialog(context,user);
        });

        holder.btnEdit.setOnClickListener(v -> listener.onUpdateUser(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteUser(user.getUid()));
        holder.switchActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context, "Đã thay đổi trạng thái", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void showUserDetailsDialog(Context context, User user) {
        // Tạo layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_inf_acc_ma, null);

        // Khởi tạo các TextView
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        TextView tvRole = view.findViewById(R.id.tvRole);

        // Set giá trị user vào các TextView
        if (user != null) {
            tvName.setText("Name: " + user.getName());
            tvEmail.setText("Email: " + user.getEmail());
            tvPhone.setText("Phone: " + user.getPhone());
            tvRole.setText("Role: " + user.getRole());
        }

        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("User Information")
                .setView(view)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (User user : userList) {
                if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                        user.getPhone().contains(query) ||
                        user.getRole().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvRole;
        ImageButton btnInfo, btnEdit, btnDelete;
        Switch switchActive;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            switchActive = itemView.findViewById(R.id.switchActive);
        }
    }
}


