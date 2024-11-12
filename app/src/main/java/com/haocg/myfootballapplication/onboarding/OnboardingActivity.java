package com.haocg.myfootballapplication.onboarding;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haocg.myfootballapplication.R;

import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.CircleIndicator3;

public class OnboardingActivity extends AppCompatActivity {
    private TextView txtSkip;
    private ViewPager2 viewPager2;
    private RelativeLayout layoutBottom;
    private CircleIndicator3 circleIndicator;
//    private LinearLayout layoutNext;

    private ViewPagerAdapter viewPagerAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_onboarding), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        circleIndicator.setViewPager(viewPager2);

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager2.setCurrentItem(2);
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Xử lý sự kiện khi trang được chọn
                if(position == 2){
                    txtSkip.setVisibility(View.GONE);
                }else {
                    txtSkip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                // Xử lý sự kiện khi trang đang cuộn
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // Xử lý sự kiện khi trạng thái cuộn thay đổi
            }
        });

    }

    private void initUI(){
        txtSkip = findViewById(R.id.txtSkipOnboarding);
        viewPager2 = findViewById(R.id.viewPagerOnboarding);
        layoutBottom = findViewById(R.id.layoutBottom);
        circleIndicator = findViewById(R.id.circleIndicator);
//        layoutNext = findViewById(R.id.layoutNext);
    }
}