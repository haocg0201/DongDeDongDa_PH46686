package com.haocg.myfootballapplication.admin.frg;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;

import java.util.ArrayList;
import java.util.List;

public class AsPeoFragment extends Fragment {
    private BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_as_peo, container, false);
        barChart = view.findViewById(R.id.barChart);

        loadUserDataAndDisplayChart();

        return view;
    }

    private void loadUserDataAndDisplayChart() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int userCount = 0;
                int staffCount = 0;
                int adminCount = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String role = userSnapshot.child("role").getValue(String.class);
                    if (role != null) {
                        switch (role) {
                            case "user":
                                userCount++;
                                break;
                            case "staff":
                                staffCount++;
                                break;
                            case "admin":
                                adminCount++;
                                break;
                        }
                    }
                }

                updateBarChart(userCount, staffCount, adminCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu người dùng", error.toException());
            }
        });
    }

    private void updateBarChart(int userCount, int staffCount, int adminCount) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, userCount));
        entries.add(new BarEntry(1, staffCount));
        entries.add(new BarEntry(2, adminCount));

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng người dùng theo vai trò");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value; // Ép kiểu float thành int

                switch (index) {
                    case 0:
                        return "User";
                    case 1:
                        return "Staff";
                    case 2:
                        return "Admin";
                    default:
                        return "";
                }
            }
        });
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }
}