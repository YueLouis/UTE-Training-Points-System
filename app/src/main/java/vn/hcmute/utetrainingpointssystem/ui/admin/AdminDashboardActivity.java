package vn.hcmute.utetrainingpointssystem.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.ui.category.CategoryListActivity;
import vn.hcmute.utetrainingpointssystem.ui.event.EventListActivity;
import vn.hcmute.utetrainingpointssystem.ui.user.LoginActivity;
import vn.hcmute.utetrainingpointssystem.ui.user.UserListActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        TokenManager tokenManager = new TokenManager(this);

        Button btnManageUsers = findViewById(R.id.btnManageUsers);
        Button btnManageEvents = findViewById(R.id.btnManageEvents);
        Button btnManageCategories = findViewById(R.id.btnManageCategories);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(this, UserListActivity.class));
        });

        btnManageEvents.setOnClickListener(v -> {
            startActivity(new Intent(this, EventListActivity.class));
        });

        btnManageCategories.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryListActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            tokenManager.clear();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
