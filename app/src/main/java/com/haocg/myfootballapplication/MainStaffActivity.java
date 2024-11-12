package com.haocg.myfootballapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.haocg.myfootballapplication.admin.frg.AsGainFragment;
import com.haocg.myfootballapplication.admin.frg.AsPeoFragment;
import com.haocg.myfootballapplication.admin.frg.MAccountFragment;
import com.haocg.myfootballapplication.admin.frg.MServiceFragment;
import com.haocg.myfootballapplication.admin.frg.MShiftFragment;
import com.haocg.myfootballapplication.admin.frg.MStadiumFragment;
import com.haocg.myfootballapplication.admin.frg.MStsFragment;
import com.haocg.myfootballapplication.guest.StadiumMainActivity;
import com.haocg.myfootballapplication.staff.stafffragments.AddServiceFragment;
import com.haocg.myfootballapplication.staff.stafffragments.HomeFragment;
import com.haocg.myfootballapplication.staff.stafffragments.InvoiceStaffFragment;
import com.haocg.myfootballapplication.staff.stafffragments.StaffShiftFragment;
import com.haocg.myfootballapplication.staff.stafffragments.ViewStadiumFragment;

public class MainStaffActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_staff);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainStaff), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, new HomeFragment())
                    .commit();
        }

        drawerLayout = findViewById(R.id.mainStaff);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View navHeaderView = navigationView.getHeaderView(0);
        TextView txtNavHeader = navHeaderView.findViewById(R.id.textNavigatorHeader);
        txtNavHeader.setText("Xin chào " + getUserInfFromPreferences());

        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        if(role != null && role.equals("staff")){
            Menu menu = navigationView.getMenu();
            menu.setGroupVisible(R.id.nav_main_admin, false);
        }else{
            Menu menu = navigationView.getMenu();
            menu.setGroupVisible(R.id.nav_main_staff, false);
        }



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_check_in) {
//                    Log.d("Navigation", "Item ID: " + id + "ck-in b");
                    replaceFragment(new ViewStadiumFragment());
                } else if (id == R.id.nav_add_service) {
                    AddServiceFragment addServiceFragment = new AddServiceFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("dKey", true);
                    addServiceFragment.setArguments(bundle);
                    replaceFragment(addServiceFragment);
                } else if (id == R.id.nav_old_check_in_booking) {
                    AddServiceFragment addServiceFragment = new AddServiceFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("dKey", false);
                    addServiceFragment.setArguments(bundle);
                    replaceFragment(addServiceFragment);
                } else if (id == R.id.nav_invoice) {
                    replaceFragment(new InvoiceStaffFragment());
                } else if (id == R.id.nav_booking) {
                    startActivity(new Intent(MainStaffActivity.this, StadiumMainActivity.class));
                } else if (id == R.id.nav_shift) {
                    replaceFragment(new StaffShiftFragment());
                } else if (id == R.id.nav_m_account) {
                    replaceFragment(new MAccountFragment());
                } else if (id == R.id.nav_m_service) {
                    replaceFragment(new MServiceFragment());
                } else if (id == R.id.nav_m_stadium) {
                    replaceFragment(new MStadiumFragment());
                } else if (id == R.id.nav_m_sts) {
                    replaceFragment(new MStsFragment());
                } else if (id == R.id.nav_as_total) {
                    replaceFragment(new AsGainFragment());
                } else if (id == R.id.nav_as_guest) {
                    replaceFragment(new AsPeoFragment());
                }else if (id == R.id.nav_as_shift) {
                    replaceFragment(new MShiftFragment());
                }else if (id == R.id.nav_invoice_admin) {
                    replaceFragment(new InvoiceStaffFragment());
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
                if (!(currentFragment instanceof HomeFragment)) {
                    getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    replaceFragment(new HomeFragment());
                } else {
                    getSupportFragmentManager().popBackStack();
                }

                if (currentFragment instanceof HomeFragment) {
                    new AlertDialog.Builder(MainStaffActivity.this)
                            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                            .setCancelable(false)
                            .setPositiveButton("Đăng xuất", (dialog, id) -> {
                                Toast.makeText(MainStaffActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainStaffActivity.this, LoginMainActivity.class));
                                finish();
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
            }
        });

    }

    private String getUserInfFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }


    public void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainStaffActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainStaffActivity.this, LoginMainActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nếu người dùng chọn "Không", chỉ đóng dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

//    public void onBackPressed() { // không dùng được nữa huhu
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//
//        if (currentFragment instanceof HomeFragment) {
//            new AlertDialog.Builder(this)
//                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
//                    .setCancelable(false)
//                    .setPositiveButton("Đăng xuất", (dialog, id) -> {
//                        Toast.makeText(MainStaffActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(MainStaffActivity.this, LoginMainActivity.class));
//                        finish();
//                    })
//                    .setNegativeButton("Hủy", null)
//                    .show();
//        } else {
//            super.getOnBackPressedDispatcher().onBackPressed();
//        }
//    }

}