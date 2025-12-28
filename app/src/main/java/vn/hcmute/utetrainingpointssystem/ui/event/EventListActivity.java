package vn.hcmute.utetrainingpointssystem.ui.event;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventListViewModel;

public class EventListActivity extends AppCompatActivity {

    private EventListViewModel vm;
    private RecyclerView rv;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        rv = findViewById(R.id.rvEvents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter();
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(EventListViewModel.class);

        vm.getEventsLive().observe(this, adapter::submitList);
        vm.getErrorLive().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        vm.fetchEvents();
    }
}
