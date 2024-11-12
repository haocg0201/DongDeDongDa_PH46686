package com.haocg.myfootballapplication.admin.frg;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsGainFragment extends Fragment {

    private BarChart barChart;
    private Map<String, Float> dailyRevenue = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_as_gain, container, false);

        Button btnDay = view.findViewById(R.id.btnDay);
        Button btnWeek = view.findViewById(R.id.btnWeek);
        Button btnMonth = view.findViewById(R.id.btnMonth);

        barChart = view.findViewById(R.id.barChart);

        getDataTotalInInvoice();

        btnDay.setOnClickListener(v -> updateBarChart(filterRevenue(dailyRevenue, "day")));
        btnWeek.setOnClickListener(v -> updateBarChart(filterRevenue(dailyRevenue, "week")));
        btnMonth.setOnClickListener(v -> updateBarChart(filterRevenue(dailyRevenue, "month")));

        return view;
    }

//    private void setupPieChart() {
//        pieChart.setDrawHoleEnabled(true);
//        pieChart.setHoleRadius(40f);
//        pieChart.setTransparentCircleRadius(45f);
//        pieChart.setCenterText("Invoice Totals");
//        pieChart.setCenterTextSize(18);
//        pieChart.getDescription().setEnabled(false);
//
//        Legend legend = pieChart.getLegend();
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//        legend.setDrawInside(false);
//    }

    private void getDataTotalInInvoice() {
        DatabaseReference invoicesRef = FirebaseDatabase.getInstance().getReference("invoices");
        invoicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dailyRevenue.clear();

                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    String time = invoiceSnapshot.child("time").getValue(String.class);
                    Float total = invoiceSnapshot.child("total").getValue(Float.class);

                    if (time != null && total != null) {
                        String date = time.split(" ")[0]; // Lấy phần "yyyy-MM-dd" của time
                        if (dailyRevenue.containsKey(date)) {
                            dailyRevenue.put(date, dailyRevenue.get(date) + total);
                        } else {
                            dailyRevenue.put(date, total);
                        }
                    }
                }

                updateBarChart(dailyRevenue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu hóa đơn", error.toException());
            }
        });
    }

    public Map<String, Float> filterRevenue(Map<String, Float> dailyRevenue, String filterType) {
        Map<String, Float> filteredRevenue = new HashMap<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map.Entry<String, Float> entry : dailyRevenue.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey(), formatter);
            boolean addEntry = false;

            switch (filterType) {
                case "day":
                    if (date.equals(currentDate)) addEntry = true;
                    break;
                case "week":
                    if (ChronoUnit.WEEKS.between(date, currentDate) == 0) addEntry = true;
                    break;
                case "month":
                    if (ChronoUnit.MONTHS.between(date, currentDate) == 0) addEntry = true;
                    break;
            }

            if (addEntry) {
                filteredRevenue.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredRevenue;
    }

    private void updateBarChart(Map<String, Float> revenueData) {
        List<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Float> entry : revenueData.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh Thu");
        dataSet.setColor(Color.BLUE);
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.invalidate();
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new ArrayList<>(revenueData.keySet())));
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}
