package com.haocg.myfootballapplication.guest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haocg.myfootballapplication.R;
import com.haocg.myfootballapplication.adapter.StadiumAdapter;
import com.haocg.myfootballapplication.model.Stadium;

import java.util.ArrayList;
import java.util.List;

public class StadiumMainActivity extends AppCompatActivity {

    private RecyclerView stadiumRecyclerView;
    private StadiumAdapter stadiumAdapter;
    private List<Stadium> stadiumList;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stadium_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_stadium), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.stadium_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Các sân bóng đang hoạt động");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);

        stadiumRecyclerView = findViewById(R.id.stadiumRecyclerView);
        stadiumRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        stadiumList = new ArrayList<>();
        stadiumAdapter = new StadiumAdapter(stadiumList,this,false);
        loadStadiumData();
        stadiumRecyclerView.setAdapter(stadiumAdapter);



        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterStadiums(s);
                return true;
            }
        });

    }

    private void loadStadiumData() {
        DatabaseReference stadiumsRef = FirebaseDatabase.getInstance().getReference("stadiums");

        stadiumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stadiumList.clear();
                for (DataSnapshot stadiumSnapshot : dataSnapshot.getChildren()) {
                    Stadium stadium = stadiumSnapshot.getValue(Stadium.class);
                    String stadiumId = stadiumSnapshot.getKey();
                    if (stadium != null) {
                        stadium.setStadium_id(stadiumId);
                        stadiumList.add(stadium);
                    }
                }
                stadiumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi: " + databaseError.getMessage());
                Toast.makeText(StadiumMainActivity.this, "Không tải được dữ liệu sân bóng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterStadiums(String query) {
        List<Stadium> filteredList = new ArrayList<>();

        for (Stadium stadium : stadiumList) {
            // Kiểm tra xem tên sân có chứa chuỗi tìm kiếm không (bỏ qua trường hợp chữ hoa chữ thường)
            if (stadium.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(stadium);
            }
        }

        // Cập nhật adapter với danh sách đã lọc
        stadiumAdapter.updateList(filteredList);
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