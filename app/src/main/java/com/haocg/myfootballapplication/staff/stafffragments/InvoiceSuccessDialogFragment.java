package com.haocg.myfootballapplication.staff.stafffragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.Invoice;

public class InvoiceSuccessDialogFragment extends DialogFragment {
    private Invoice invoice;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Lấy dữ liệu Invoice từ Bundle (sử dụng getParcelable thay vì getSerializable)
        if (getArguments() != null) {
            invoice = (Invoice) getArguments().getSerializable("invoice");
        }

        // Inflate layout cho Dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.invoice_inf, null);

        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        // Khai báo các TextView trong layout
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

        // Cập nhật dữ liệu vào các TextView
        String[] invTime = invoice.getTime().split(" ");
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

        // Tạo và hiển thị Dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }
}
