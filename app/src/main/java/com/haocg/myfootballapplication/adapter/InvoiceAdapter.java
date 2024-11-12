package com.haocg.myfootballapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.Invoice;

import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {
    private Context context;
    private List<Invoice> invoiceList;

    public InvoiceAdapter(Context context, List<Invoice> invoiceList) {
        this.context = context;
        this.invoiceList = invoiceList;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);

        String invoiceId = invoice.getInvoiceId().split("_")[1];
        String [] createdAt = invoice.getTime().split(" ");
        holder.invoiceIdTextView.setText("Định danh hóa đơn: " + invoiceId);
        holder.nameTextView.setText("Tên khách hàng: " + invoice.getName());
        holder.phoneTextView.setText("Số điện thoại: " + invoice.getPhone());
        holder.createdAtTextView.setText("Thời gian tạo: " + createdAt[0] + " - " + createdAt[1]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfo(invoice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress", "SetTextI18n"})
    private void showInfo(Invoice invoice){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.invoice_inf, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView txtInvoiceId = dialogView.findViewById(R.id.txtInvoiceId);
        TextView tvCustomerName = dialogView.findViewById(R.id.tvCustomerName);
        TextView tvPhoneNumber = dialogView.findViewById(R.id.tvPhoneNumber);
        TextView tvStadiumName = dialogView.findViewById(R.id.tvStadiumName);
        TextView tvBookingTime = dialogView.findViewById(R.id.tvBookingTime);
        TextView tvInvoiceTime = dialogView.findViewById(R.id.tvInvoiceTime);
        TextView tvStadiumPrice = dialogView.findViewById(R.id.tvStadiumPrice);
        TextView tvServicePrice = dialogView.findViewById(R.id.tvServicePrice);
        TextView tvSurcharge = dialogView.findViewById(R.id.tvSurcharge);
        TextView tvNote = dialogView.findViewById(R.id.tvNote);
        TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
        TextView tvMoneyGuest = dialogView.findViewById(R.id.tvMGuesst);
        TextView tvMoneyBack = dialogView.findViewById(R.id.tvSBack);
        TextView tvTotal = dialogView.findViewById(R.id.tvTotal);

        String [] invTime = invoice.getTime().split(" ");
        txtInvoiceId.setText("Mã hóa đơn: " + invoice.getInvoiceId().split("_")[1] + "\n");
        tvCustomerName.setText(invoice.getName());
        tvPhoneNumber.setText(invoice.getPhone());
        tvStadiumName.setText(invoice.getStadiumName());
        tvBookingTime.setText(invoice.getBookingTime());
        tvInvoiceTime.setText(invTime[0] + " - " + invTime[1]);
        tvStadiumPrice.setText(String.valueOf(invoice.getStadiumPrice()));
        tvServicePrice.setText(String.valueOf(invoice.getServicePrice()));
        tvSurcharge.setText(String.valueOf(invoice.getSurcharge()));
        tvNote.setText(invoice.getNote());
        tvStatus.setText(invoice.getStatus());
        tvMoneyGuest.setText(invoice.getMGuesst() + " VND");
        tvMoneyBack.setText(invoice.getSBack() + " VND");
        tvTotal.setText(invoice.getTotal() + " VND");

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView invoiceIdTextView;
        TextView nameTextView;
        TextView phoneTextView;
        TextView createdAtTextView;

        public InvoiceViewHolder(View itemView) {
            super(itemView);
            invoiceIdTextView = itemView.findViewById(R.id.invoiceIdTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
        }
    }
}

