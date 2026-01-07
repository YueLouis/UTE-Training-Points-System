package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;

public class ForgotEmailActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnNext;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_email);

        // Initialize views
        edtEmail = findViewById(R.id.edtEmail);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Back button click - return to Login
        btnBack.setOnClickListener(v -> finish());

        // Next button click - send OTP request
        btnNext.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            sendOtpRequest(email);
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendOtpRequest(String email) {
        // Show loading state
        btnNext.setEnabled(false);
        btnNext.setText("Đang gửi...");
        progressBar.setVisibility(View.VISIBLE);

        AuthApi api = RetrofitClient.getClient().create(AuthApi.class);
        api.request(new ForgotPasswordRequest(email)).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                // Reset button state
                btnNext.setEnabled(true);
                btnNext.setText("Next");
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    String errorMsg = "Lỗi từ server: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            if (errorJson.contains("\"message\":\"")) {
                                int start = errorJson.indexOf("\"message\":\"") + 11;
                                int end = errorJson.indexOf("\"", start);
                                errorMsg = errorJson.substring(start, end);
                            }
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                    Toast.makeText(ForgotEmailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                ForgotPasswordResponse res = response.body();

                // Navigate to OTP verification screen
                Intent intent = new Intent(ForgotEmailActivity.this, ForgotCodeActivity.class);
                intent.putExtra("email", email);
                if (res != null && res.demoCode != null) {
                    intent.putExtra("demoCode", res.demoCode);
                }
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                // Reset button state
                btnNext.setEnabled(true);
                btnNext.setText("Next");
                progressBar.setVisibility(View.GONE);

                String errorMsg = t.getMessage();
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Thời gian chờ quá lâu. Vui lòng thử lại.";
                }
                Toast.makeText(ForgotEmailActivity.this, "Lỗi kết nối: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
