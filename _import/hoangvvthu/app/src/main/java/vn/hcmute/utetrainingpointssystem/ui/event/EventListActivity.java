package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.UserApi;
import vn.hcmute.utetrainingpointssystem.ui.user.ProfileActivity;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventListViewModel;

public class EventListActivity extends AppCompatActivity {

    private EventListViewModel vm;
    private RecyclerView rv;
    private EventAdapter adapter;
    private TokenManager tokenManager;
    private TextView tvUserNameHeader, tvUserCodeHeader;
    private ImageView btnProfileHeader;
    private FloatingActionButton fabMyEvents;

    private TextView tvSelectedYear, tvSelectedSemester, tvSelectedPointType;
    private List<EventDTO> fullEventList = new ArrayList<>();

    // Mặc định để null để hiện TẤT CẢ khi vừa vào app, tránh bị trống danh sách
    private Long selectedSemesterId = null; 
    private Long selectedCategoryId = null; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        tokenManager = new TokenManager(this);
        initViews();
        setupRecyclerView();
        setupViewModel();
        refreshData();
    }

    private void refreshData() {
        loadUserInfo();
        vm.fetchEvents(tokenManager.getStudentId());
    }

    private void initViews() {
        tvUserNameHeader = findViewById(R.id.tvUserNameHeader);
        tvUserCodeHeader = findViewById(R.id.tvUserCodeHeader);
        btnProfileHeader = findViewById(R.id.btnProfileHeader);
        fabMyEvents = findViewById(R.id.fabMyEvents);

        tvSelectedYear = findViewById(R.id.tvSelectedYear);
        tvSelectedSemester = findViewById(R.id.tvSelectedSemester);
        tvSelectedPointType = findViewById(R.id.tvSelectedPointType);

        btnProfileHeader.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        fabMyEvents.setOnClickListener(v -> startActivity(new Intent(this, MyEventsActivity.class)));

        findViewById(R.id.btnYear).setOnClickListener(this::showYearMenu);
        findViewById(R.id.btnSemester).setOnClickListener(this::showSemesterMenu);
        findViewById(R.id.btnSelectPointType).setOnClickListener(this::showPointTypeMenu);
    }

    private void showYearMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("2025 - 26");
        popup.getMenu().add("Tất cả");
        popup.setOnMenuItemClickListener(item -> {
            tvSelectedYear.setText("› " + item.getTitle());
            applyFilters();
            return true;
        });
        popup.show();
    }

    private void showSemesterMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Học kỳ 1");
        popup.getMenu().add("Học kỳ 2");
        popup.getMenu().add("Tất cả");
        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            tvSelectedSemester.setText("› " + (title.equals("Tất cả") ? "Tất cả" : (title.equals("Học kỳ 1") ? "HK I" : "HK II")));
            
            if (title.equals("Tất cả")) selectedSemesterId = null;
            else selectedSemesterId = title.equals("Học kỳ 1") ? 1L : 2L;
            
            applyFilters();
            return true;
        });
        popup.show();
    }

    private void showPointTypeMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Tất cả");
        popup.getMenu().add("Điểm rèn luyện (DRL)");
        popup.getMenu().add("Điểm công tác xã hội (CTXH)");
        popup.getMenu().add("Điểm chuyên đề doanh nghiệp (CDDN)");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            tvSelectedPointType.setText("Loại điểm:\n" + (title.contains("(") ? title.substring(title.indexOf("(")+1, title.indexOf(")")) : title));
            
            if (title.contains("Tất cả")) selectedCategoryId = null;
            else if (title.contains("DRL")) selectedCategoryId = 1L;
            else if (title.contains("CTXH")) selectedCategoryId = 2L;
            else if (title.contains("CDDN")) selectedCategoryId = 3L;
            
            applyFilters();
            return true;
        });
        popup.show();
    }

    private void applyFilters() {
        List<EventDTO> filtered = fullEventList.stream()
                .filter(e -> selectedSemesterId == null || (e.getSemesterId() != null && e.getSemesterId().equals(selectedSemesterId)))
                .filter(e -> selectedCategoryId == null || (e.getCategoryId() != null && e.getCategoryId().equals(selectedCategoryId)))
                .collect(Collectors.toList());
        adapter.submitList(filtered);
        
        if (filtered.isEmpty() && !fullEventList.isEmpty()) {
            Toast.makeText(this, "Không có sự kiện nào khớp với bộ lọc", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInfo() {
        Long userId = tokenManager.getUserId();
        if (userId == null) return;
        UserApi api = RetrofitClient.getClient().create(UserApi.class);
        api.getUserById(userId).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO user = response.body();
                    tvUserNameHeader.setText(user.fullName != null ? user.fullName : "Chưa cập nhật");
                    tvUserCodeHeader.setText(user.studentCode != null ? user.studentCode : "---");
                }
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {}
        });
    }

    private void setupRecyclerView() {
        rv = findViewById(R.id.rvEvents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter();
        rv.setAdapter(adapter);
    }

    private void setupViewModel() {
        vm = new ViewModelProvider(this).get(EventListViewModel.class);
        vm.getEventsResult().observe(this, result -> {
            if (result instanceof ResultState.Success) {
                fullEventList = ((ResultState.Success<List<EventDTO>>) result).data;
                applyFilters();
            } else if (result instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) result).message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
