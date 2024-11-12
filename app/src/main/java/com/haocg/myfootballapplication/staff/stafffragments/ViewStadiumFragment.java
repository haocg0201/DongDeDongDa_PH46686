package com.haocg.myfootballapplication.staff.stafffragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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

public class ViewStadiumFragment extends Fragment {

    private RecyclerView stadiumRecyclerView;
    private StadiumAdapter stadiumAdapter;
    private List<Stadium> stadiumList;
    private SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_stadium, container, false);

        stadiumRecyclerView = view.findViewById(R.id.staffStadiumRecyclerView);
        stadiumRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        stadiumList = new ArrayList<>();
        stadiumAdapter = new StadiumAdapter(stadiumList, requireActivity(),true);
        loadStadiumData();
        stadiumRecyclerView.setAdapter(stadiumAdapter);



        searchView = view.findViewById(R.id.search_view_staff);
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


        return view;
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
                Toast.makeText(requireActivity(), "Không tải được dữ liệu sân bóng", Toast.LENGTH_SHORT).show();
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
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}