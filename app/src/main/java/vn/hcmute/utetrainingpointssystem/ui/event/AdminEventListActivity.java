package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventManageViewModel;

public class AdminEventListActivity extends AppCompatActivity {

    private EventManageViewModel vm;
    private AdminEventAdapter adapter;

    private RecyclerView rv;
    private ImageButton btnAdd;
    private Spinner spFilterCategory;

    private final List<EventCategoryDTO> categoryList = new ArrayList<>();
    private final List<String> filterNames = new ArrayList<>();
    private ArrayAdapter<String> filterAdapter;

    private int lastFilterPos = 0;
    private boolean spinnerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_list);

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        rv = findViewById(R.id.rvEvents);
        btnAdd = findViewById(R.id.btnAdd);
        spFilterCategory = findViewById(R.id.spFilterCategory);

        adapter = new AdminEventAdapter(
                this::openDetail,
                this::openEdit,
                this::confirmDelete
        );

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterNames);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterCategory.setAdapter(filterAdapter);

        spFilterCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                // tránh trường hợp onCreate setSelection gây gọi load 2 lần
                if (!spinnerInitialized) {
                    spinnerInitialized = true;
                }
                lastFilterPos = position;
                applyFilter(position);
            }

            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        observeVM();

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, EventCreateActivity.class)));

        vm.loadCategories();
        vm.loadAllEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilter(lastFilterPos);
    }

    private void applyFilter(int position) {
        if (position == 0) {
            vm.loadAllEvents();
            return;
        }
        int idx = position - 1;
        if (idx >= 0 && idx < categoryList.size()) {
            Long categoryId = categoryList.get(idx).id;
            vm.loadEventsByCategory(categoryId);
        } else {
            vm.loadAllEvents();
        }
    }

    private void observeVM() {
        vm.getCategoriesState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                List<EventCategoryDTO> data = ((ResultState.Success<List<EventCategoryDTO>>) state).data;

                categoryList.clear();
                filterNames.clear();
                filterNames.add("Tất cả");

                if (data != null) {
                    categoryList.addAll(data);
                    for (EventCategoryDTO c : data) filterNames.add(c.name);
                }

                filterAdapter.notifyDataSetChanged();

                if (lastFilterPos < filterNames.size()) spFilterCategory.setSelection(lastFilterPos);
                else { lastFilterPos = 0; spFilterCategory.setSelection(0); }

            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getEventsState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                List<EventDTO> data = ((ResultState.Success<List<EventDTO>>) state).data;
                adapter.submitList(data);
            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getActionState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                applyFilter(lastFilterPos);
            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDetail(EventDTO e) {
        if (e == null || e.id == null) {
            Toast.makeText(this, "Event thiếu id", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, EventDetailActivity.class);
        i.putExtra("eventId", e.id);
        startActivity(i);
    }

    private void openEdit(EventDTO e) {
        if (e == null || e.id == null) {
            Toast.makeText(this, "Event thiếu id", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, EventEditActivity.class);
        i.putExtra("eventId", e.id);
        startActivity(i);
    }

    private void confirmDelete(EventDTO e) {
        if (e == null || e.id == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Xoá event?")
                .setMessage("Bạn chắc muốn xoá: " + (e.title == null ? "" : e.title))
                .setPositiveButton("Xoá", (d, which) -> vm.deleteEvent(e.id))
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
