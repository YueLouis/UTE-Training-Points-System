package vn.hcmute.utetrainingpointssystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.semester.SemesterDTO;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventCategoryApi;
import vn.hcmute.utetrainingpointssystem.network.api.SemesterApi;
import vn.hcmute.utetrainingpointssystem.network.api.UserApi;
import vn.hcmute.utetrainingpointssystem.ui.event.EventAdapter;
import vn.hcmute.utetrainingpointssystem.ui.event.EventDetailActivity;
import vn.hcmute.utetrainingpointssystem.ui.user.ProfileActivity;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventListViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EventListViewModel eventViewModel;
    private EventAdapter adapter;
    private TokenManager tokenManager;

    private TextView tvUserNameHeader, tvUserCodeHeader;
    private TextView tvYearSelection, tvSemesterSelection, tvWeekFilter, tvTypeFilter, tvSelectedRangeLabel;
    private View btnYearSelect, btnSemesterSelect, btnWeekFilter, btnTypeFilter;

    private LinearLayout[] dayColumnLayouts = new LinearLayout[7];
    private LinearLayout[] dayEventContainers = new LinearLayout[7];
    private TextView[] tvDayDates = new TextView[7];
    private TextView[] tvDayWeeks = new TextView[7];

    private String[] years = {"2023 - 24", "2024 - 25", "2025 - 26"};
    private String[] weeks = {"29/12/2025 - 04/01/2026", "05/01/2026 - 11/01/2026", "12/01/2026 - 18/01/2026"};

    private List<SemesterDTO> semesters = new ArrayList<>();
    private List<String> semesterNames = new ArrayList<>();
    private Long selectedSemesterId = null;

    private List<EventCategoryDTO> categories = new ArrayList<>();
    private Long selectedCategoryId = null;
    private String selectedWeekRange = "05/01/2026 - 11/01/2026";
    private List<EventDTO> allFetchedEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = new TokenManager(this);

        // Bind UI
        tvUserNameHeader = findViewById(R.id.tvUserNameHeader);
        tvUserCodeHeader = findViewById(R.id.tvUserCodeHeader);
        findViewById(R.id.layoutUserInfo).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        tvYearSelection = findViewById(R.id.tvYearSelection);
        tvSemesterSelection = findViewById(R.id.tvSemesterSelection);
        btnYearSelect = findViewById(R.id.btnYearSelect);
        btnSemesterSelect = findViewById(R.id.btnSemesterSelect);

        tvWeekFilter = findViewById(R.id.tvWeekFilter);
        tvTypeFilter = findViewById(R.id.tvTypeFilter);
        btnWeekFilter = findViewById(R.id.btnWeekFilter);
        btnTypeFilter = findViewById(R.id.btnTypeFilter);
        tvSelectedRangeLabel = findViewById(R.id.tvSelectedRangeLabel);

        for (int i = 0; i < 7; i++) {
            dayColumnLayouts[i] = findViewById(getResources().getIdentifier("dayColumn" + i, "id", getPackageName()));
            dayEventContainers[i] = findViewById(getResources().getIdentifier("dayEventsContainer" + i, "id", getPackageName()));
            tvDayDates[i] = findViewById(getResources().getIdentifier("tvDayDate" + i, "id", getPackageName()));
            tvDayWeeks[i] = findViewById(getResources().getIdentifier("tvDayWeek" + i, "id", getPackageName()));
        }

        // Set Defaults
        tvYearSelection.setText("› 2025 - 26");
        tvSemesterSelection.setText("› HK I");
        tvWeekFilter.setText(selectedWeekRange);
        tvTypeFilter.setText("Tất cả");
        tvSelectedRangeLabel.setText("Toàn bộ sự kiện");

        // Listeners
        btnYearSelect.setOnClickListener(v -> showPicker("Chọn năm học", years, tvYearSelection, true));
        btnSemesterSelect.setOnClickListener(v -> showSemesterPicker());
        btnWeekFilter.setOnClickListener(v -> showWeekPicker());
        btnTypeFilter.setOnClickListener(v -> showCategoryPicker());

        // Recycler
        RecyclerView rv = findViewById(R.id.rvEventsMain);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter();
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(event -> {
            Intent intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("EVENT_ID", event.id);
            startActivity(intent);
        });

        // ViewModel
        eventViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        eventViewModel.getEventsResult().observe(this, result -> {
            if (result instanceof ResultState.Success) {
                allFetchedEvents = ((ResultState.Success<List<EventDTO>>) result).data;
                adapter.submitList(new ArrayList<>(allFetchedEvents));
                updateCalendarUI();
            }
        });

        loadUserInfo();
        loadCategories();
        loadSemesters();
        initializeToCurrentWeek();
        updateCalendarUI();
        eventViewModel.fetchEvents(null);
    }

    private void loadCategories() {
        EventCategoryApi api = RetrofitClient.getClient().create(EventCategoryApi.class);
        api.getAll().enqueue(new Callback<List<EventCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<EventCategoryDTO>> call, Response<List<EventCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                }
            }
            @Override public void onFailure(Call<List<EventCategoryDTO>> call, Throwable t) {}
        });
    }

    private void loadSemesters() {
        SemesterApi api = RetrofitClient.getClient().create(SemesterApi.class);
        api.getAll().enqueue(new Callback<List<SemesterDTO>>() {
            @Override
            public void onResponse(Call<List<SemesterDTO>> call, Response<List<SemesterDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    semesters = response.body();
                    semesterNames.clear();
                    for (SemesterDTO s : semesters) {
                        semesterNames.add(s.name);
                    }
                    // Set default to first active semester
                    if (!semesters.isEmpty()) {
                        selectedSemesterId = semesters.get(0).id;
                        tvSemesterSelection.setText("› " + semesters.get(0).name);
                        eventViewModel.fetchEvents(selectedCategoryId);
                    }
                }
            }
            @Override public void onFailure(Call<List<SemesterDTO>> call, Throwable t) {}
        });
    }

    private void initializeToCurrentWeek() {
        // Set to current week (index 1 = week containing today)
        if (weeks.length > 1) {
            selectedWeekRange = weeks[1];
            tvWeekFilter.setText(selectedWeekRange);
            tvSelectedRangeLabel.setText(selectedWeekRange);
        }
    }

    private void showSemesterPicker() {
        if (semesters.isEmpty()) {
            Toast.makeText(this, "Đang tải học kỳ...", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] items = new String[semesters.size()];
        for (int i = 0; i < semesters.size(); i++) {
            items[i] = semesters.get(i).name;
        }
        new AlertDialog.Builder(this)
            .setTitle("Chọn học kỳ")
            .setItems(items, (d, i) -> {
                selectedSemesterId = semesters.get(i).id;
                tvSemesterSelection.setText("› " + items[i]);
                eventViewModel.fetchEvents(selectedCategoryId);
                updateCalendarUI();
            })
            .show();
    }

    private void showCategoryPicker() {
        if (categories.isEmpty()) {
            Toast.makeText(this, "Đang tải danh mục...", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] catNames = new String[categories.size() + 1];
        catNames[0] = "Tất cả";
        for (int i = 0; i < categories.size(); i++) {
            catNames[i+1] = categories.get(i).name;
        }

        new AlertDialog.Builder(this).setTitle("Loại điểm").setItems(catNames, (d, i) -> {
            tvTypeFilter.setText(catNames[i]);
            selectedCategoryId = (i == 0) ? null : categories.get(i-1).id;
            eventViewModel.fetchEvents(selectedCategoryId);
        }).show();
    }

    private void showPicker(String title, String[] items, TextView target, boolean isPrefix) {
        new AlertDialog.Builder(this).setTitle(title).setItems(items, (d, i) -> {
            target.setText(isPrefix ? "› " + items[i] : items[i]);
            eventViewModel.fetchEvents(selectedCategoryId);
        }).show();
    }

    private void showWeekPicker() {
        new AlertDialog.Builder(this)
            .setTitle("Chọn tuần")
            .setItems(weeks, (d, i) -> {
                selectedWeekRange = weeks[i];
                tvWeekFilter.setText(selectedWeekRange);
                if (tvSelectedRangeLabel != null) tvSelectedRangeLabel.setText(selectedWeekRange);
                updateCalendarUI();
                filterEventsByWeek();
            })
            .show();
    }

    private void updateCalendarUI() {
        SimpleDateFormat sdfWeek = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date start;
        try { start = sdfWeek.parse(selectedWeekRange.split(" - ")[0]); } 
        catch (Exception e) { start = new Date(); }

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        Date today = new Date();

        for (int i = 0; i < 7; i++) {
            Date d = cal.getTime();
            tvDayDates[i].setText(new SimpleDateFormat("dd", Locale.getDefault()).format(d));
            tvDayWeeks[i].setText(new SimpleDateFormat("EE", Locale.getDefault()).format(d));
            dayEventContainers[i].removeAllViews();

            boolean isToday = isSameDay(d, today);
            dayColumnLayouts[i].setBackgroundResource(isToday ? R.drawable.bg_today_pink : android.R.color.transparent);
            
            String key = sdfKey.format(d);
            for (EventDTO ev : allFetchedEvents) {
                try {
                    if (ev.startTime != null) {
                        Date eventDate;
                        try {
                            eventDate = sdfApi.parse(ev.startTime);
                        } catch (Exception e) {
                            eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(ev.startTime);
                        }
                        if (eventDate != null && sdfKey.format(eventDate).equals(key)) {
                            addChip(dayEventContainers[i], ev);
                        }
                    }
                } catch (Exception ex) {}
            }
            int finalI = i;
            dayColumnLayouts[i].setOnClickListener(v -> filterByDay(finalI));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void filterEventsByWeek() {
        SimpleDateFormat sdfWeek = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date start = sdfWeek.parse(selectedWeekRange.split(" - ")[0]);
            Date end = sdfWeek.parse(selectedWeekRange.split(" - ")[1]);
            List<EventDTO> filtered = new ArrayList<>();
            for (EventDTO e : allFetchedEvents) {
                if (e.startTime == null) continue;
                Date d;
                try { d = sdfApi.parse(e.startTime); } 
                catch (Exception ex) { d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(e.startTime); }
                if (d != null && !d.before(start) && !d.after(end)) filtered.add(e);
            }
            adapter.submitList(filtered);
        } catch (Exception e) {}
    }

    private void addChip(LinearLayout container, EventDTO ev) {
        if (container.getChildCount() >= 2) return;
        View v = LayoutInflater.from(this).inflate(R.layout.item_event_chip, container, false);
        TextView tv = v.findViewById(R.id.tvChipCategory);
        if (ev.categoryId == 1) { tv.setText("DRL"); tv.setBackgroundResource(R.drawable.bg_event_card_drl); }
        else if (ev.categoryId == 2) { tv.setText("CTXH"); tv.setBackgroundResource(R.drawable.bg_event_card_ctxh); }
        else { tv.setText("CDDN"); tv.setBackgroundResource(R.drawable.bg_event_card_cddn); }
        container.addView(v);
    }

    private void filterByDay(int index) {
        SimpleDateFormat sdfWeek = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date start = sdfWeek.parse(selectedWeekRange.split(" - ")[0]);
            Calendar c = Calendar.getInstance(); c.setTime(start); c.add(Calendar.DAY_OF_MONTH, index);
            String key = sdfKey.format(c.getTime());
            tvSelectedRangeLabel.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.getTime()));

            List<EventDTO> filtered = new ArrayList<>();
            for (EventDTO e : allFetchedEvents) {
                if (e.startTime == null) continue;
                Date ed;
                try { ed = sdfApi.parse(e.startTime); } 
                catch (Exception ex) { ed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(e.startTime); }
                if (ed != null && sdfKey.format(ed).equals(key)) filtered.add(e);
            }
            adapter.submitList(filtered);
        } catch (Exception e) {}
    }

    private boolean isSameDay(Date a, Date b) {
        Calendar ca = Calendar.getInstance(); ca.setTime(a);
        Calendar cb = Calendar.getInstance(); cb.setTime(b);
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) && ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR);
    }

    private void loadUserInfo() {
        Long userId = tokenManager.getUserId();
        if (userId == null) return;
        RetrofitClient.getClient().create(UserApi.class).getUserById(userId).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO u = response.body();
                    tvUserNameHeader.setText(u.fullName);
                    tvUserCodeHeader.setText(u.studentCode);
                }
            }
            @Override public void onFailure(Call<UserDTO> call, Throwable t) {}
        });
    }
}
