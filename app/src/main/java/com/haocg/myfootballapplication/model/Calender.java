package com.haocg.myfootballapplication.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Calender {

    public List<String> getDaysInMonth(int month, int year) {
        List<String> daysList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= maxDay; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            daysList.add("Ngày " + dayOfMonth + "/" + (month + 1));
        }

        return daysList;
    }

}
