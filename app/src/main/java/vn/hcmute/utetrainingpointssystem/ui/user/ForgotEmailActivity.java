package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
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
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;

public class ForgotEmailActivity extends AppCompatActivity {

    private EditText edtEmail;
    private TextView tvDemoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_email);

        edtEmail = findViewById(R.id.edtEmail);
        tvDemoCode = findViewById(R.id.tvDemoCode);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnSkip = findViewById(R.id.btnSkip);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnSkip.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            Intent i = new Intent(ForgotEmailActivity.this, ForgotCodeActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        btnSend.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSend.setEnabled(false);
            btnSend.setText("Đang gửi...");
            progressBar.setVisibility(View.VISIBLE);

            AuthApi api = RetrofitClient.getClient().create(AuthApi.class);
            api.request(new ForgotPasswordRequest(email)).enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                    btnSend.setEnabled(true);
                    btnSend.setText("Gửi mã");
                    progressBar.setVisibility(View.GONE);

                    if (!response.isSuccessful() || response.body() == null) {
                        String errorMsg = "Lỗi từ server: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorJson = response.errorBody().string();
                                // Giả sử backend trả JSON có field "message"
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
                    
                    // qua màn nhập code, mang theo demoCode để test cho nhanh nếu mail chưa tới
                    Intent i = new Intent(ForgotEmailActivity.this, ForgotCodeActivity.class);
                    i.putExtra("email", email);
                    if (res != null && res.demoCode != null) {
                        i.putExtra("demoCode", res.demoCode);
                    }
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    btnSend.setEnabled(true);
                    btnSend.setText("Gửi mã");
                    progressBar.setVisibility(View.GONE);
                    
                    String errorMsg = t.getMessage();
                    if (t instanceof java.net.SocketTimeoutException) {
                        errorMsg = "Thời gian chờ quá lâu (Timeout). Có thể server đang gặp lỗi gửi mail.";
                    }
                    Toast.makeText(ForgotEmailActivity.this, "Lỗi kết nối: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
