package com.haocg.myfootballapplication.admin.frg;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.MAccountAdapter;
import com.haocg.myfootballapplication.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MAccountFragment extends Fragment {
    private RecyclerView recyclerView;
    private MAccountAdapter adapter;
    private SearchView searchView;
    private List<User> userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_m_account, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewMA);
        searchView = view.findViewById(R.id.searchViewMA);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ExtendedFloatingActionButton fabButtonMaCreateAcc = view.findViewById(R.id.fabButtonMaCreateAcc);
        fabButtonMaCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserFormDialog(new User(),1);
            }
        });

        userList = new ArrayList<>();
        adapter = new MAccountAdapter(requireContext(),userList, new MAccountAdapter.AccountActionListener() {
            @Override
            public void onUpdateUser(User user) {
                showUserFormDialog(user,0);
            }

            @Override
            public void onDeleteUser(String userId) {
                showDeleteDialog(userId);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        loadUserDataFromFirebase();

        return view;
    }

    private void loadUserDataFromFirebase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setUid(userSnapshot.getKey());
                        userList.add(user);
                    }
                }
                adapter.filter("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi tải dữ liệu người dùng", error.toException());
                Toast.makeText(requireContext(), "Mạng bận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUser(User user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String userId = user.getUid();
        User upUser = new User(user.getName(),user.getRole(), user.getEmail(), user.getPhone(), user.getPassword());
        usersRef.child(userId).setValue(upUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    adapter.filter("");
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createUser(User user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "Email đã tồn tại, không thể tạo", Toast.LENGTH_SHORT).show();
                } else {
                    String userId = "userId_" + UUID.randomUUID().toString();
                    User upUser = new User(user.getName(), user.getRole(), user.getEmail(), user.getPhone(), user.getPassword());

                    usersRef.child(userId).setValue(upUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                adapter.filter("");
                                Toast.makeText(getContext(), "Tạo người dùng thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("Firebase", "Lỗi khi tạo người dùng.", task.getException());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý nếu có lỗi trong quá trình truy vấn dữ liệu Firebase
                Log.e("Firebase", "Lỗi khi kiểm tra email: " + databaseError.getMessage());
            }
        });
    }


    public void deleteUser(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    System.out.println(userId);
                    adapter.filter("");
                    Toast.makeText(requireContext(),"Xóa người dùng thành công",Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Firebase", "Lỗi khi xóa người dùng.", task.getException());
                    Toast.makeText(requireContext(),"Lỗi mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showUserFormDialog(User user,int key) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.dialog_form_ma, null);

        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etPhone = view.findViewById(R.id.etPhone);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        CheckBox cbRoleUser = view.findViewById(R.id.cbRoleUser);
        CheckBox cbRoleEmployee = view.findViewById(R.id.cbRoleEmployee);
        CheckBox cbRoleAdmin = view.findViewById(R.id.cbRoleAdmin);
        if(user != null){
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etPassword.setText(user.getPassword());

            if (user.getRole().equals("user")) {
                cbRoleUser.setChecked(true);
                cbRoleEmployee.setChecked(false);
                cbRoleAdmin.setChecked(false);
            } else if (user.getRole().equals("staff")) {
                cbRoleUser.setChecked(false);
                cbRoleEmployee.setChecked(true);
                cbRoleAdmin.setChecked(false);
            } else if (user.getRole().equals("admin")) {
                cbRoleUser.setChecked(false);
                cbRoleEmployee.setChecked(false);
                cbRoleAdmin.setChecked(true);
            }
        }

        cbRoleUser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbRoleEmployee.setChecked(false);
                cbRoleAdmin.setChecked(false);
            }
        });

        cbRoleEmployee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbRoleUser.setChecked(false);
                cbRoleAdmin.setChecked(false);
            }
        });

        cbRoleAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbRoleUser.setChecked(false);
                cbRoleEmployee.setChecked(false);
            }
        });

        // Set up AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("Thực hiện",null)
                .setNegativeButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                // Get input values
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String role = "";
                boolean isUser = cbRoleUser.isChecked();
                boolean isEmployee = cbRoleEmployee.isChecked();
                boolean isAdmin = cbRoleAdmin.isChecked();

                if (name.isEmpty()) {
                    etName.setError("Tên không được để trống");
                } else if (!isValidEmail(email)) {
                    etEmail.setError("Email không hợp lệ");
                } else if (phone.isEmpty() || phone.length() > 13) {
                    etPhone.setError("Số điện thoại không hợp lệ");
                } else if (!isValidPassword(password)) {
                    etPassword.setError("Mật khẩu cần có ít nhất 1 ký tự đặc biệt và không có khoảng trắng");
                } else if (!isUser && !isEmployee && !isAdmin) {
                    Toast.makeText(requireContext(), "Chọn ít nhất một vai trò", Toast.LENGTH_SHORT).show();
                } else {
                    role = (isAdmin) ? "admin" : (isEmployee) ? "staff" : "user";
                    User u = new User(user.getUid(),name,role,email,phone,password);
                    if(key == 1){
                        createUser(u);
                        dialog.dismiss();
                    }else {
                        updateUser(u);
                        dialog.dismiss();
                    }
                }
            });
        });

        dialog.show();
    }

    private void showDeleteDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản này?")
                .setCancelable(false) // Prevent dismissing dialog by clicking outside
                .setPositiveButton("Thực hiện", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteUser(userId);
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // Create and show the dialog
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[!@#$%^&*])\\S+$");
    }


}