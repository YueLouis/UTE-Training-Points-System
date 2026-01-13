package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.VerifySecretRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;

public class ForgotCodeActivity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_code);


        email = getIntent().getStringExtra("email");
        if (email == null) email = "";

        // Get OTP input fields
        EditText etOtp1 = findViewById(R.id.etOtp1);
        EditText etOtp2 = findViewById(R.id.etOtp2);
        EditText etOtp3 = findViewById(R.id.etOtp3);
        EditText etOtp4 = findViewById(R.id.etOtp4);
        EditText etOtp5 = findViewById(R.id.etOtp5);
        EditText etOtp6 = findViewById(R.id.etOtp6);

        Button btnVerify = findViewById(R.id.btnVerify);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnVerify.setOnClickListener(v -> {
            String code = etOtp1.getText().toString() +
                         etOtp2.getText().toString() +
                         etOtp3.getText().toString() +
                         etOtp4.getText().toString() +
                         etOtp5.getText().toString() +
                         etOtp6.getText().toString();

            if (code.length() < 6) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            btnVerify.setEnabled(false);
            btnVerify.setText("Đang xác thực...");
            progressBar.setVisibility(View.VISIBLE);

            AuthApi api = RetrofitClient.getClient().create(AuthApi.class);
            api.verify(new VerifySecretRequest(email, code)).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    Log.d("ForgotCodeActivity", "Response code: " + response.code());
                    Log.d("ForgotCodeActivity", "Response successful: " + response.isSuccessful());
                    Log.d("ForgotCodeActivity", "Response body: " + response.body());

                    btnVerify.setEnabled(true);
                    btnVerify.setText("Xác nhận");
                    progressBar.setVisibility(View.GONE);

                    if (!response.isSuccessful() || response.body() == null) {
                        String errorMsg = "Xác thực thất bại: " + response.code();
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

                    Log.d("ForgotCodeActivity", "Verification successful! Navigating to ResetPasswordActivity");
                    // Sau khi verify thành công, chuyển sang trang đặt lại mật khẩu
                    Toast.makeText(ForgotCodeActivity.this, "Xác thực thành công! Vui lòng đặt lại mật khẩu.", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ForgotCodeActivity.this, ResetPasswordActivity.class);
                    i.putExtra("email", email);
                    i.putExtra("otpCode", code);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    btnVerify.setEnabled(true);
                    btnVerify.setText("Xác nhận");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotCodeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
