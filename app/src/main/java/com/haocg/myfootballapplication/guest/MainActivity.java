package com.haocg.myfootballapplication.guest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.haocg.myfootballapplication.LoginMainActivity;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.guest.stadiumutil.PaymentMainActivity;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge mode
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_guest), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.guestToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Thêm icon nút thoát ở góc trái
        toolbar.setNavigationIcon(R.drawable.ic_account_logout); // Đặt icon của bạn
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(MainActivity.this, LoginMainActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_guest_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.getOnBackPressedDispatcher().onBackPressed();
            return true;
        } else if (id == R.id.action_account) {
            Intent accountIntent = new Intent(this, GuestAccountMainActivity.class);
            startActivity(accountIntent);
            Toast.makeText(this, "Thông tin tài khoản", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_stadium) {
            Intent stadiumIntent = new Intent(this, StadiumMainActivity.class);
            startActivity(stadiumIntent);
            Toast.makeText(this, "Danh sách sân bóng", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_tab_payment) {
            Intent stadiumIntent = new Intent(this, PaymentMainActivity.class);
            startActivity(stadiumIntent);
            Toast.makeText(this, "Danh sách đơn đặt sân", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}