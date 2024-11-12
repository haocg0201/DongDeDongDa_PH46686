package com.haocg.myfootballapplication.staff.stafffragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.InvoiceAdapter;
import com.haocg.myfootballapplication.model.Invoice;

import java.util.ArrayList;
import java.util.List;

public class InvoiceStaffFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private InvoiceAdapter adapter;
    private List<Invoice> invoiceList = new ArrayList<>();
    private List<Invoice> filteredInvoiceList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice_staff, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewInvoice);
        searchView = view.findViewById(R.id.searchField);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InvoiceAdapter(requireContext(), filteredInvoiceList);
        recyclerView.setAdapter(adapter);

        loadInvoiceData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterInvoices(newText);
                return true;
            }
        });

        return view;
    }

    private void loadInvoiceData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("invoices");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                invoiceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Invoice invoice = snapshot.getValue(Invoice.class);
                    if (invoice != null) {
                        invoiceList.add(invoice);
                    }
                }

                filteredInvoiceList.clear();
                filteredInvoiceList.addAll(invoiceList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterInvoices(String query) {
        filteredInvoiceList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredInvoiceList.addAll(invoiceList);
        } else {
            for (Invoice invoice : invoiceList) {
                if (invoice.getInvoiceId().toLowerCase().contains(query.toLowerCase()) ||
                        invoice.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredInvoiceList.add(invoice);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}