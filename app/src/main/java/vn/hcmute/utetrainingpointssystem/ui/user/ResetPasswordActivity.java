package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.ResetPasswordRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;

public class ResetPasswordActivity extends AppCompatActivity {

    private String email;
    private String otpCode;
    private EditText etNewPassword, etConfirmPassword;
    private CheckBox cbShowPassword;
    private Button btnConfirm;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Get email and OTP code from intent
        email = getIntent().getStringExtra("email");
        otpCode = getIntent().getStringExtra("otpCode");

        if (email == null)
            email = "";
        if (otpCode == null)
            otpCode = "";

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbShowPassword = findViewById(R.id.cbShowPassword);
        btnConfirm = findViewById(R.id.btnConfirm);
        progressBar = findViewById(R.id.progressBar);

        // Back button - allow user to go back to OTP screen
        btnBack.setOnClickListener(v -> finish());

        // Note: No need to intercept back press since default behavior is to finish()

        // Show/Hide password toggle
        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int inputType = isChecked
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

            etNewPassword.setInputType(inputType);
            etConfirmPassword.setInputType(inputType);

            // Move cursor to end
            etNewPassword.setSelection(etNewPassword.getText().length());
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        // Confirm button
        btnConfirm.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return;
        }

        // Show loading
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang xử lý...");
        progressBar.setVisibility(View.VISIBLE);

        // Call API
        AuthApi api = RetrofitClient.getClient().create(AuthApi.class);
        api.resetPassword(new ResetPasswordRequest(email, otpCode, newPassword))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Xác nhận");
                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful()) {
                            String errorMsg = "Đổi mật khẩu thất bại";
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
                            Toast.makeText(ResetPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Success - navigate to success screen
                        Intent intent = new Intent(ResetPasswordActivity.this, PasswordSuccessActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Xác nhận");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
}
