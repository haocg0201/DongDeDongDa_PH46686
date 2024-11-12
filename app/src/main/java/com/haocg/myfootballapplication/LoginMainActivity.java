package com.haocg.myfootballapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.guest.MainActivity;

public class LoginMainActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText emailEdt, passwordEdt;
    private CheckBox rememberMeCheckBox;
    private boolean checkLogin;
    private TextView forgotPasswordTxt, signUpTxt;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    String role = ""; // admin - staff - user
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        loginBtn = findViewById(R.id.loginButton);
        emailEdt = findViewById(R.id.EdtEmailLogin);
        passwordEdt = findViewById(R.id.EdtPasswordLogin);
        rememberMeCheckBox = findViewById(R.id.rememberMe);
        forgotPasswordTxt = findViewById(R.id.forgotPassword);
        signUpTxt = findViewById(R.id.register);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadLoginInfo();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdt.getText().toString();
                String pw = passwordEdt.getText().toString();
                findUserByEmail(email,pw);
            }
        });
        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginMainActivity.this,SignUpMainActivity.class));
            }
        });
    }

    public void findUserByEmail(String email, String password) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = "";
                    String pw = "";
                    String name = "";
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        userId = userSnapshot.getKey();
                        pw = userSnapshot.child("password").getValue(String.class);
                        role = userSnapshot.child("role").getValue(String.class);
                        name = userSnapshot.child("name").getValue(String.class);
                    }
                    System.out.println("Login +++++++++++++++++++++++++++++= userId: " + userId);
                    saveUserIdToPreferences(userId,name);
                    if (pw.equalsIgnoreCase(password)) {
                        checkLogin = true;
                    } else {
                        checkLogin = false;
                    }

                    //  sau khi có kết quả đăng nhập mới thực hiện hành động tiếp theo
                    processLoginResult(checkLogin, email, password);
                } else {
                    // Không tìm thấy email
                    checkLogin = false;
                    processLoginResult(checkLogin, email, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi: " + databaseError.getMessage());
            }
        });
    }

    // Hàm xử lý kết quả đăng nhập sau khi kiểm tra Firebase
    private void processLoginResult(boolean checkLogin, String email, String password) {
        if (checkLogin) {
            if (rememberMeCheckBox.isChecked()) {
                saveLoginInfo(email, password);
            } else {
                clearLoginInfo();
            }

            // Điều hướng dựa trên vai trò người dùng
            switch (role) {
                case "user": {
                    Intent intent = new Intent(LoginMainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case "admin": {
                    Intent intent = new Intent(LoginMainActivity.this, MainStaffActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);
                    finish();
                    break;
                }
                case "staff": {
                    Intent intent = new Intent(LoginMainActivity.this, MainStaffActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);
                    finish();
                    break;
                }
                default: {
                    startActivity(new Intent(LoginMainActivity.this, MainActivity.class));
                    finish();
                    break;
                }
            }
            Toast.makeText(LoginMainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginMainActivity.this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveLoginInfo(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    private void clearLoginInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa thông tin đã lưu
        editor.apply();
    }

    private void loadLoginInfo() {
        // Nếu đã lưu thông tin, tự động điền vào
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");
            emailEdt.setText(savedEmail);
            passwordEdt.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void saveUserIdToPreferences(String userId,String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("name", name);
        editor.apply();
    }

}