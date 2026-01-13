package vn.hcmute.utetrainingpointssystem.ui.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;
import vn.hcmute.utetrainingpointssystem.viewmodel.user.UserListViewModel;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private UserListViewModel viewModel;
    private RecyclerView rvUsers;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup RecyclerView
        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter();
        rvUsers.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(UserListViewModel.class);

        // Observe users
        viewModel.getUsersResult().observe(this, result -> {
            if (result instanceof ResultState.Success) {
                ResultState.Success<List<UserDTO>> successState = (ResultState.Success<List<UserDTO>>) result;
                List<UserDTO> users = successState.data;
                adapter.submitList(users != null ? users : new ArrayList<>());
            } else if (result instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) result).message, Toast.LENGTH_SHORT).show();
            }
        });

        // Load users
        viewModel.fetchAllUsers();
    }
}

