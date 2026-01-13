package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.ui.user.ProfileActivity;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventListViewModel;

public class EventListActivity extends AppCompatActivity {

    private EventListViewModel vm;
    private RecyclerView rv;
    private AdminEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        TokenManager tokenManager = new TokenManager(this);
        String userRole = tokenManager.getRole();
        boolean isAdmin = "ADMIN".equals(userRole);

        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnCreateEvent = findViewById(R.id.btnCreateEvent);

        // Show Create Event button only for admin
        if (isAdmin) {
            btnCreateEvent.setVisibility(View.VISIBLE);
            btnCreateEvent.setOnClickListener(v ->
                    startActivity(new Intent(EventListActivity.this, EventCreateActivity.class))
            );
        }

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(EventListActivity.this, ProfileActivity.class))
        );

        rv = findViewById(R.id.rvEvents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        vm = new ViewModelProvider(this).get(EventListViewModel.class);
        adapter = new AdminEventAdapter(vm, this);
        rv.setAdapter(adapter);

        // Set delete listener to refresh list
        adapter.setOnEventDeletedListener(() -> vm.fetchAllEvents());

        // Observe events
        vm.getEventsResult().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                ResultState.Success<List<EventDTO>> successState = (ResultState.Success<List<EventDTO>>) state;
                List<EventDTO> events = successState.data;
                adapter.submitList(events != null ? events : new ArrayList<>());
            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        // Load events
        vm.fetchAllEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vm.fetchAllEvents();
    }
}
