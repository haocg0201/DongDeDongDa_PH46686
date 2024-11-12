package com.haocg.myfootballapplication.guest.stadiumutil.paymentfragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BookingTabAdapter extends FragmentStateAdapter {
    public BookingTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingBookingsFragment();
            case 1:
                return new ConfirmedBookingsFragment();
            case 2:
                return new ExpiredBookingsFragment();
            default:
                return new PendingBookingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 3 Tabs
    }
}
