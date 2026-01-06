package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.ui.admin.AdminDashboardActivity;
import vn.hcmute.utetrainingpointssystem.ui.event.EventListActivity;
import vn.hcmute.utetrainingpointssystem.viewmodel.auth.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private AuthViewModel viewModel;

    private EditText etUsername, etPassword;
    private Button btnLogin, btnForgot;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(this);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Nếu đã có token -> vào dashboard luôn
        if (tokenManager.isLoggedIn()) {
            navigateToDashboard(tokenManager.getRole());
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgot = findViewById(R.id.btnForgot);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();
            if (username.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.login(username, p);
        });

        btnForgot.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotEmailActivity.class));
        });

        observeViewModel();
    }

    private void navigateToDashboard(String role) {
        if ("ADMIN".equals(role)) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(this, EventListActivity.class));
        }
        finish();
    }

    private void observeViewModel() {
        viewModel.getLoginResult().observe(this, result -> {
            if (result instanceof ResultState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
            } else if (result instanceof ResultState.Success) {
                progressBar.setVisibility(View.GONE);
                var auth = ((ResultState.Success<vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse>) result).data;
                // Lưu token
                tokenManager.saveAuth(auth.token, auth.user.id, auth.user.role);
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                navigateToDashboard(auth.user.role);
            } else if (result instanceof ResultState.Error) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                String msg = ((ResultState.Error<?>) result).message;
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
