package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.ui.user.ProfileActivity;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventListViewModel;

public class EventListActivity extends AppCompatActivity {

    private EventListViewModel vm;
    private RecyclerView rv;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Button btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(EventListActivity.this, ProfileActivity.class))
        );

        rv = findViewById(R.id.rvEvents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter();
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(EventListViewModel.class);

        vm.getEventsResult().observe(this, result -> {
            if (result instanceof ResultState.Success) {
                adapter.submitList(((ResultState.Success<java.util.List<vn.hcmute.utetrainingpointssystem.model.event.EventDTO>>) result).data);
            } else if (result instanceof ResultState.Error) {
                String msg = ((ResultState.Error<?>) result).message;
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });

        vm.fetchEvents();
    }
}
