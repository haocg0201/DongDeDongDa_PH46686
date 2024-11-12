package com.haocg.myfootballapplication.staff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.model.Booking;
import com.haocg.myfootballapplication.model.Invoice;
import com.haocg.myfootballapplication.model.Payment;
import com.haocg.myfootballapplication.model.Service;
import com.haocg.myfootballapplication.model.User;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InvoiceActivity extends AppCompatActivity {

    private TextView textTotal;
    private TextInputEditText editTextName, editTextPhone, editTextStadiumName, editTextStadiumPrice, editTextServicePrice, editTextSurcharge, editTextNote, editTextMGuesst, editTextSBack;
    private TextView textBookingTime;
    private CheckBox checkBoxOptionCash, checkBoxOptionOnline;
    Button buttonSaveInvoice;
    private List<Integer> prices;
    Invoice invoiceIO;
    ArrayList<Booking> bookingList;
    private int total;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.invoice_staff_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadForm();

        buttonSaveInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    Invoice invoice = getForm(invoiceIO);
                    createInvoice(invoice);
                    updateBookingStatus(bookingList,"OK");
                } else {
                    Toast.makeText(InvoiceActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void loadForm(){
        textTotal = findViewById(R.id.textTotal);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextStadiumName = findViewById(R.id.editTextStadiumName);
        textBookingTime = findViewById(R.id.textBookingTime);
        editTextStadiumPrice = findViewById(R.id.editTextStadiumPrice);
        editTextServicePrice = findViewById(R.id.editTextServicePrice);
        editTextSurcharge = findViewById(R.id.editTextSurcharge);
        editTextNote = findViewById(R.id.editTextNote);
        editTextMGuesst = findViewById(R.id.editTextMGuesst);
        editTextSBack = findViewById(R.id.editTextSBack);
        checkBoxOptionCash = findViewById(R.id.checkBoxOptionCash);
        checkBoxOptionOnline = findViewById(R.id.checkBoxOptionOnline);
        buttonSaveInvoice= findViewById(R.id.buttonSaveInvoice);

        Toolbar toolbar = findViewById(R.id.invoice_toolbar_staff);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Thanh toán và tạo hóa đơn");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }


        Intent intent = getIntent();
        bookingList= intent.getParcelableArrayListExtra("booking_list");
        ArrayList<Service> serviceList = intent.getParcelableArrayListExtra("service_list");
        User userIO = getIntent().getParcelableExtra("userIO");
        if(bookingList != null){
            getAllPaymentsPrice(bookingList);
            invoiceIO = createInvoice(bookingList, serviceList, userIO);
            setForm(invoiceIO);
        } else{
            Toast.makeText(this, "Lỗi mạng khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
        }

    }

    private void setForm(Invoice invoice){
        editTextName.setText(invoice.getName());
        editTextPhone.setText(invoice.getPhone());
        editTextStadiumName.setText(invoice.getStadiumName());
        textBookingTime.setText("Khung giờ đá: " + invoice.getBookingTime());
//        editTextStadiumPrice.setText(String.valueOf(invoice.getStadiumPrice()));
        editTextServicePrice.setText(String.valueOf(invoice.getServicePrice()));
        editTextSurcharge.setText(String.valueOf(invoice.getSurcharge()));
        editTextMGuesst.setText(String.valueOf(invoice.getMGuesst()));
        editTextSBack.setText(String.valueOf(invoice.getSBack()));
        textTotal.setText(String.valueOf(invoice.getTotal()));
        editTextMGuesst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String mGuestText = editTextMGuesst.getText().toString();
                String totolServicePrice = editTextServicePrice.getText().toString();
                String stadiumPrice = editTextStadiumPrice.getText().toString();
                String surcharge = editTextSurcharge.getText().toString();
                if (mGuestText.isEmpty() ) {
                    mGuestText = "0";
                }

                if (surcharge.isEmpty()){
                    surcharge = "0";
                }

                DecimalFormat formatter = new DecimalFormat("#,###.##");


                int mGuest = Integer.parseInt(mGuestText);
                int tSP = Integer.parseInt(totolServicePrice);
                int sp = Integer.parseInt(stadiumPrice);
                int sur = Integer.parseInt(surcharge);
                int total1 = sp + tSP + sur;
                String formattedTotal = formatter.format(total1);
                int sBack = mGuest - total1;
                editTextSBack.setText(String.valueOf(sBack));
                textTotal.setText("Tổng thanh toán: " + formattedTotal + " VND");
                total = total1;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (editTextName.getText().toString().trim().isEmpty()) {
            editTextName.setError("Vui lòng nhập tên");
            isValid = false;
        } else {
            editTextName.setError(null);
        }


//        String phonePattern = "^\\d{10}$"; // định dạng số điện thoại 10 chữ số
        String phoneText = editTextPhone.getText().toString().trim();

        if (phoneText.isEmpty()) {
            editTextPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        }
//        else if (!phoneText.matches(phonePattern)) {
//            editTextPhone.setError("Số điện thoại không hợp lệ");
//            isValid = false;
//        }
        else {
            editTextPhone.setError(null);
        }

        String abc = editTextMGuesst.getText().toString().trim();
        if (abc.isEmpty() || abc.equals("0")) {
            editTextMGuesst.setError("Vui lòng nhập số tiền khách đưa");
            isValid = false;
        } else {
            editTextMGuesst.setError(null);
        }

        return isValid;
    }

    private Invoice getForm(Invoice invoice){
        invoice.setName(editTextName.getText().toString());
        invoice.setPhone(editTextPhone.getText().toString());
        invoice.setStadiumName(editTextStadiumName.getText().toString());
        invoice.setBookingTime(textBookingTime.getText().toString());
        invoice.setStadiumPrice(Integer.parseInt(editTextStadiumPrice.getText().toString()));
        invoice.setServicePrice(Integer.parseInt(editTextServicePrice.getText().toString()));
        invoice.setSurcharge(Integer.parseInt(editTextSurcharge.getText().toString()));
        invoice.setMGuesst(Integer.parseInt(editTextMGuesst.getText().toString()));
        invoice.setSBack(Integer.parseInt(editTextSBack.getText().toString()));
        invoice.setTotal(total);
        invoice.setNote(editTextNote.getText().toString());
        invoice.setStatus(checkBoxOptionCash.isChecked() ? "cash" : checkBoxOptionOnline.isChecked() ? "transfer" : "cash transfer");
        return invoice;
    }

    private Invoice createInvoice(ArrayList<Booking> bookings, ArrayList<Service> serviceList, User user){
        String invoiceId = "invoiceId_" + System.currentTimeMillis()  + new Random().nextInt(9999) + "inv";
        String userId = user.getUid();
        String stadiumId = "";
        String bookingId = "";
        String name = "";
        String phone = "";
        StringBuilder stadiumName = new StringBuilder();
        StringBuilder bookingTime = new StringBuilder();
        String time = getCurrentTimeFormatted();
        int stadiumPrice = 0;
        int servicePrice = 0;
        int surcharge = 0;
        String note = "abc";
        String status = "cash transfer";
        int mGuesst = 0;

        List<String> serviceId = new ArrayList<>();

        if (bookings != null) {
            for (Booking booking : bookings) {
                stadiumId = booking.getStadiumId();
                bookingId = booking.getBookingId();

                String StadiumId = booking.getStadiumId();
                String[] parts = StadiumId.split("_");
                String number = parts[1];
                stadiumName.append(" ").append(number);
                bookingTime.append(" ").append(booking.getTime());
                for(Map.Entry<String, Boolean> entry : booking.getServices().entrySet()){
                    String service = entry.getKey();
                    Boolean value = entry.getValue();
                    if (value != null && value) {
                        serviceId.add(service);
                    }
                }
            }
        }

        for(String id : serviceId){
            if (serviceList != null) {
                for(Service s : serviceList){
                    if(id.equals(s.getsId())){
                        servicePrice += s.getPrice();
                    }
                }
            }
        }

        if(user != null){
            name = user.getName();
            phone = user.getPhone();
        }

        int total = 0;
        int sBack = 0;

        return  new Invoice(
                invoiceId,
                userId,
                stadiumId,
                bookingId,
                name,
                phone,
                stadiumName.toString(),
                bookingTime.toString(),
                time,
                stadiumPrice,
                servicePrice,
                surcharge,
                note,
                status,
                mGuesst,
                sBack,
                total
        );
    }

    private void createInvoice(Invoice invoice){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("invoices");
        String invoiceId = invoice.getInvoiceId();
        databaseReference.child(invoiceId).setValue(invoice)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showInfo(invoice);
                        buttonSaveInvoice.setVisibility(View.GONE);
                        Toast.makeText(this, "Đã tạo hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi khi tạo hóa đơn: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String getCurrentTimeFormatted() {
        // "yyyy-MM-dd HH:mm"
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }

    private void getAllPaymentsPrice(ArrayList<Booking> bookingList) {
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments");
        prices = new ArrayList<>();

        for (Booking booking : bookingList) {
            String paymentId = booking.getPaymentId();
            if (paymentId != null && !paymentId.isEmpty()) {
                paymentRef.child(paymentId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Payment payment = snapshot.getValue(Payment.class);
                            if(payment.getStatus().equals("unpaid")){
                                int price = (int) payment.getAmount();
                                prices.add(price);
                            }else {
                                int price = (int) payment.getAmount() / 2;
                                prices.add(price);
                            }



                            if (prices.size() == bookingList.size()) {
                                // Thực hiện các thao tác tiếp theo, ví dụ:
                                int totalStadiumPrice = 0;
                                for (int stadiumPrice : prices) {
                                    totalStadiumPrice += stadiumPrice;
                                }
                                editTextStadiumPrice.setText(String.valueOf(totalStadiumPrice));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PaymentError", "Lỗi khi lấy payment: " + error.getMessage());
                    }
                });
            }
        }
    }

    private void updateBookingStatus(ArrayList<Booking> bookingList, String newStatus) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        for (Booking booking : bookingList) {
            String bookingId = booking.getBookingId();

            if (bookingId != null && !bookingId.isEmpty()) {
                DatabaseReference bookingStatusRef = bookingRef.child(bookingId).child("status");
                bookingStatusRef.setValue(newStatus)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Log.d("BookingStatus", "Trạng thái của booking " + bookingId + " đã được cập nhật thành công rồi nè.");
                                } else {
                                    //Log.e("BookingStatus", "Cập nhật trạng thái booking thất bại ú huhu: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        }
    }

//    public static InvoiceSuccessDialogFragment newInstance(Invoice invoice) {
//        InvoiceSuccessDialogFragment fragment = new InvoiceSuccessDialogFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("invoice", invoice);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress", "SetTextI18n"})
    private void showInfo(Invoice invoice){
        View dialogView = LayoutInflater.from(this).inflate(R.layout.invoice_inf, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}