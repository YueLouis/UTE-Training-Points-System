package vn.hcmute.utetrainingpointssystem.ui.event;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.viewmodel.registration.EventRegistrationViewModel;

public class MyEventsActivity extends AppCompatActivity {

    private EventRegistrationViewModel viewModel;
    private RecyclerView rv;
    private EventAdapter adapter;
    private TabLayout tabLayout;
    private List<EventRegistrationDTO> allRegistrations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tabLayout = findViewById(R.id.tabLayout);
        rv = findViewById(R.id.rvMyEvents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter();
        rv.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(EventRegistrationViewModel.class);
        
        setupObservers();
        loadData();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterEvents(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadData() {
        TokenManager tm = new TokenManager(this);
        Long studentId = tm.getStudentId();
        if (studentId != null) {
            viewModel.fetchStudentRegistrations(studentId);
        }
    }

    private void setupObservers() {
        viewModel.getRegistrations().observe(this, registrations -> {
            if (registrations != null) {
                this.allRegistrations = registrations;
                filterEvents(tabLayout.getSelectedTabPosition());
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterEvents(int tabIndex) {
        List<EventDTO> eventList;
        if (tabIndex == 0) {
            // Sắp diễn ra: REGISTERED
            eventList = allRegistrations.stream()
                    .filter(r -> "REGISTERED".equals(r.getStatus()))
                    .map(EventRegistrationDTO::getEvent)
                    .filter(e -> e != null)
                    .collect(Collectors.toList());
        } else {
            // Lịch sử: COMPLETED, ATTENDED, CANCELLED
            eventList = allRegistrations.stream()
                    .filter(r -> !"REGISTERED".equals(r.getStatus()))
                    .map(EventRegistrationDTO::getEvent)
                    .filter(e -> e != null)
                    .collect(Collectors.toList());
        }
        adapter.submitList(eventList);
    }
}
