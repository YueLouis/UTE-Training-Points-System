package vn.hcmute.utetrainingpointssystem.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.ui.category.CategoryListActivity;
import vn.hcmute.utetrainingpointssystem.ui.event.EventListActivity;
import vn.hcmute.utetrainingpointssystem.ui.user.LoginActivity;
import vn.hcmute.utetrainingpointssystem.ui.user.ProfileActivity;
import vn.hcmute.utetrainingpointssystem.ui.user.UserListActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        TokenManager tokenManager = new TokenManager(this);

        // Get buttons and cards
        Button btnManageUsers = findViewById(R.id.btnManageUsers);
        Button btnManageEvents = findViewById(R.id.btnManageEvents);
        Button btnManageCategories = findViewById(R.id.btnManageCategories);
        Button btnLogout = findViewById(R.id.btnLogout);

        CardView cardManageUsers = findViewById(R.id.cardManageUsers);
        CardView cardManageEvents = findViewById(R.id.cardManageEvents);
        CardView cardManageCategories = findViewById(R.id.cardManageCategories);

        // Profile button (if exists in header)
        View headerLayout = findViewById(R.id.layoutAdminInfo);
        if (headerLayout != null) {
            headerLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
            );
        }

        // User management click listeners
        View.OnClickListener manageUsersClick = v -> {
            startActivity(new Intent(this, UserListActivity.class));
        };
        btnManageUsers.setOnClickListener(manageUsersClick);
        cardManageUsers.setOnClickListener(manageUsersClick);

        // Event management click listeners
        View.OnClickListener manageEventsClick = v -> {
            startActivity(new Intent(this, EventListActivity.class));
        };
        btnManageEvents.setOnClickListener(manageEventsClick);
        cardManageEvents.setOnClickListener(manageEventsClick);

        // Category management click listeners
        View.OnClickListener manageCategoriesClick = v -> {
            startActivity(new Intent(this, CategoryListActivity.class));
        };
        btnManageCategories.setOnClickListener(manageCategoriesClick);
        cardManageCategories.setOnClickListener(manageCategoriesClick);

        // Logout click listener
        btnLogout.setOnClickListener(v -> {
            tokenManager.clear();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}



