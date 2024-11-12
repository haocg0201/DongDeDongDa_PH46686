package com.haocg.myfootballapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.model.User;

public class SignUpMainActivity extends AppCompatActivity {
    private EditText fullNameEdt, emailEdt, sdtEdt, passwordEdt, confirmPasswordEdt;
    private Button btnSignUp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullNameEdt = findViewById(R.id.edtFullNameSignUp);
        emailEdt = findViewById(R.id.edtEmailSignup);
        sdtEdt = findViewById(R.id.edtPhoneSignUp);
        passwordEdt = findViewById(R.id.edtPasswordSignup);
        confirmPasswordEdt = findViewById(R.id.edtConfirmPasswordSignUp);
        btnSignUp = findViewById(R.id.signupButton);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdt.getText().toString();
                checkAccountExistsAndAddUser(email);
            }
        });


        Toolbar toolbar = findViewById(R.id.signUpToolbar);
        // add toàn bộ chức năng của th actionBar lên cho th toolBar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
    }

    public void checkAccountExistsAndAddUser(String email) {
        boolean checkInput = validateInputs(fullNameEdt, emailEdt, sdtEdt, passwordEdt, confirmPasswordEdt);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            Toast.makeText(SignUpMainActivity.this, "Tài khoản email đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    if(checkInput){
                        addUser();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi: " + databaseError.getMessage());
            }
        });
    }

    public boolean validateInputs(EditText fullNameEdt,EditText emailEditText, EditText phoneEditText, EditText passwordEditText, EditText confirmPasswordEditText)  {
        String fullName = fullNameEdt.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if(fullName.isEmpty()){
            Toast.makeText(this, "Không bỏ trống họ tên!", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Kiểm tra định dạng email
        if (!isEmailValid(email)) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra định dạng số điện thoại
        if (!isPhoneValid(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra mật khẩu
        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Mật khẩu phải chứa chữ hoa, chữ thường, số, ký tự đặc biệt và không chứa khoảng trắng!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!confirmPassword.equalsIgnoreCase(password)){
            Toast.makeText(this, "Vui lòng xác nhận lại mật khẩu!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPhoneValid(String phone) {
        return phone.startsWith("0") && phone.length() <= 15 && phone.length() >= 8;
    }

    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false; // Mật khẩu tối thiểu 8 ký tự

        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        boolean hasWhitespace = password.contains(" ");

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar && !hasWhitespace;
    }

    private void clearForm() {
        fullNameEdt.setText("");
        emailEdt.setText("");
        sdtEdt.setText("");
        passwordEdt.setText("");
        confirmPasswordEdt.setText("");
    }

    private void addUser() {
        String fullName = fullNameEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();
        String phone = sdtEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        String userId = "userId_" + FirebaseDatabase.getInstance().getReference("users").push().getKey();
        User user = new User(fullName, email, phone, password);

        if (userId != null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpMainActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                clearForm(); // Xóa nội dung các trường sau khi thêm người dùng
                            } else {
                                Toast.makeText(SignUpMainActivity.this, "Đăng ký không thành công!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getOnBackPressedDispatcher().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}