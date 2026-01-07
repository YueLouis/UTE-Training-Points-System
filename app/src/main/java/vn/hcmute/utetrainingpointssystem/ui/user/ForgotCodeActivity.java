package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.VerifySecretRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;

public class ForgotCodeActivity extends AppCompatActivity {

    private String email;
    private EditText[] otpFields;
    private Button btnVerify;
    private TextView btnResend, tvTimer;
    private ProgressBar progressBar;
    private CountDownTimer resendTimer;
    private boolean canResend = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_code);

        // Get email from intent
        email = getIntent().getStringExtra("email");
        if (email == null)
            email = "";

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnVerify = findViewById(R.id.btnVerify);
        btnResend = findViewById(R.id.btnResend);
        tvTimer = findViewById(R.id.tvTimer);
        progressBar = findViewById(R.id.progressBar);

        // Initialize OTP fields
        otpFields = new EditText[] {
                findViewById(R.id.etOtp1),
                findViewById(R.id.etOtp2),
                findViewById(R.id.etOtp3),
                findViewById(R.id.etOtp4),
                findViewById(R.id.etOtp5),
                findViewById(R.id.etOtp6)
        };

        // Setup OTP input behavior
        setupOtpInputs();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Verify button
        btnVerify.setOnClickListener(v -> verifyOtp());

        // Resend button
        btnResend.setOnClickListener(v -> {
            if (canResend) {
                resendOtp();
            }
        });

        // Focus first OTP field
        otpFields[0].requestFocus();
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            EditText field = otpFields[i];

            // Handle text change
            field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        // Move to next field
                        otpFields[index + 1].requestFocus();
                    }

                    // Update background based on content
                    if (s.length() > 0) {
                        field.setBackgroundResource(R.drawable.bg_otp_box_focused);
                    } else {
                        field.setBackgroundResource(R.drawable.bg_otp_box);
                    }
                }
            });

            // Handle backspace for navigation
            field.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (field.getText().toString().isEmpty() && index > 0) {
                        // Move to previous field and clear it
                        otpFields[index - 1].requestFocus();
                        otpFields[index - 1].setText("");
                        return true;
                    }
                }
                return false;
            });

            // Handle focus change
            field.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && !field.getText().toString().isEmpty()) {
                    field.setBackgroundResource(R.drawable.bg_otp_box_focused);
                } else if (!hasFocus && field.getText().toString().isEmpty()) {
                    field.setBackgroundResource(R.drawable.bg_otp_box);
                }
            });
        }
    }

    private String getOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (EditText field : otpFields) {
            otp.append(field.getText().toString());
        }
        return otp.toString();
    }

    private void verifyOtp() {
        String code = getOtpCode();

        if (code.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        btnVerify.setEnabled(false);
        btnVerify.setText("Đang xác thực...");
        progressBar.setVisibility(View.VISIBLE);

        AuthApi api = RetrofitClient.getClient().create(AuthApi.class);
        api.verify(new VerifySecretRequest(email, code)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác nhận");
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    String errorMsg = "Mã OTP không đúng hoặc đã hết hạn";
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
                    Toast.makeText(ForgotCodeActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                // OTP verified successfully - navigate to reset password screen
                Toast.makeText(ForgotCodeActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();

                // Navigate to ResetPasswordActivity
                Intent intent = new Intent(ForgotCodeActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otpCode", code);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác nhận");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ForgotCodeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resendOtp() {
        if (!canResend)
            return;

        canResend = false;
        btnResend.setEnabled(false);
        btnResend.setTextColor(getResources().getColor(R.color.text_hint, null));
        tvTimer.setVisibility(View.VISIBLE);

        // Send OTP request
        AuthApi api = RetrofitClient.getClient().create(AuthApi.class);
        api.request(new ForgotPasswordRequest(email)).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotCodeActivity.this, "Mã OTP mới đã được gửi!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotCodeActivity.this, "Không thể gửi lại mã", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Toast.makeText(ForgotCodeActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });

        // Start 60 second countdown
        resendTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Gửi lại sau " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                canResend = true;
                btnResend.setEnabled(true);
                btnResend.setTextColor(getResources().getColor(R.color.text_link, null));
                tvTimer.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}
