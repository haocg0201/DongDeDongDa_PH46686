package com.haocg.myfootballapplication.guest.stadiumutil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.guest.stadiumutil.paymentfragment.BookingTabAdapter;
import com.haocg.myfootballapplication.guest.stadiumutil.paymentfragment.ConfirmedBookingsFragment;
import com.haocg.myfootballapplication.guest.stadiumutil.paymentfragment.ExpiredBookingsFragment;
import com.haocg.myfootballapplication.guest.stadiumutil.paymentfragment.PendingBookingsFragment;
import com.haocg.myfootballapplication.model.serviceinterf.OnPaymentStatusChangeListener;

public class PaymentMainActivity extends AppCompatActivity implements OnPaymentStatusChangeListener{

    private PendingBookingsFragment pendingFragment;
    private ConfirmedBookingsFragment confirmedFragment;
    private ExpiredBookingsFragment expiredFragment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainPayment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pendingFragment = new PendingBookingsFragment();
        confirmedFragment = new ConfirmedBookingsFragment();
        expiredFragment = new ExpiredBookingsFragment();

        Toolbar toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Đặt ca");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);

        // ##
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        BookingTabAdapter adapter = new BookingTabAdapter(this);

        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Đang chờ xử lý"); break;
                        case 1: tab.setText("Đã xác nhận"); break;
                        case 2: tab.setText("Đã quá hạn"); break;
                    }
                }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                PendingBookingsFragment f0 = new PendingBookingsFragment();
                if (position == 0) {
                    f0.setFabVisibility(true);
                } else {
                    f0.setFabVisibility(false);
                }
            }
        });
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getOnBackPressedDispatcher().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onPaymentStatusChanged() {
        if (pendingFragment != null && pendingFragment.bookingAdapter != null) {
            pendingFragment.bookingAdapter.notifyDataSetChanged();
        }
        if (confirmedFragment != null && confirmedFragment.confirmedAdapter != null) {
            confirmedFragment.confirmedAdapter.notifyDataSetChanged();
        }
        if (expiredFragment != null && expiredFragment.expiredAdapter != null) {
            expiredFragment.expiredAdapter.notifyDataSetChanged();
        }
    }
}